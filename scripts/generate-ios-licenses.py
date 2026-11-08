#!/usr/bin/env python3
"""Builds iosApp/iosApp/Resources/licenses.json, the list behind Settings > Open source licenses on iOS.

Three sources are merged (docs/LICENSES.md):
  * the Kotlin libraries in the shared framework, exported by the AboutLibraries Gradle plugin from :shared-umbrella,
    which also carries the hand-written fonts and assets from licenses/;
  * the Swift packages in Package.resolved, read by LicensePlist from their local checkouts.

Needs `license-plist` (brew install licenseplist) and the Swift package checkouts that an iOS build leaves in
build/xcode/SourcePackages (run ./scripts/ios-build.sh first). Run it after any dependency change and commit the result.
"""
import json
import plistlib
import re
import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
PACKAGE_RESOLVED = ROOT / "iosApp/iosApp.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/Package.resolved"
SOURCE_PACKAGES = ROOT / "build/xcode/SourcePackages"
KOTLIN_EXPORT = ROOT / "shared-umbrella/build/generated/aboutLibraries/aboutlibraries.json"
OUTPUT = ROOT / "iosApp/iosApp/Resources/licenses.json"

LINE_BREAK = re.compile(r"\s*<br\s*/?>\s*")

# LicensePlist ships the text but not the license's name, so the name is read off the text.
SPDX_MARKERS = [
    ("Apache-2.0", "Apache License 2.0", "Apache License"),
    ("MIT", "MIT License", "Permission is hereby granted, free of charge"),
    ("BSD-3-Clause", "BSD 3-Clause License", "Neither the name of"),
    ("BSD-2-Clause", "BSD 2-Clause License", "Redistribution and use in source and binary forms"),
    ("Zlib", "zlib License", "This software is provided 'as-is'"),
]


def fail(message):
    print(message, file=sys.stderr)
    sys.exit(1)


def run(command):
    result = subprocess.run(command, cwd=ROOT, capture_output=True, text=True)
    if result.returncode != 0:
        fail(f"{' '.join(command)} failed:\n{result.stdout}{result.stderr}")


def clean(text):
    return LINE_BREAK.sub("\n", text).strip()


def kotlin_entries():
    run(["./gradlew", ":shared-umbrella:exportLibraryDefinitions", "-q"])
    export = json.loads(KOTLIN_EXPORT.read_text())
    licenses = {
        key: {"name": value["name"], "url": value.get("url"), "text": clean(value["content"]) if value.get("content") else None}
        for key, value in export["licenses"].items()
    }
    entries = []
    for library in export["libraries"]:
        keys = library.get("licenses", [])
        with_text = next((key for key in keys if licenses.get(key, {}).get("text")), None)
        first = with_text or (keys[0] if keys else None)
        unique_id = library["uniqueId"]
        kind = "font" if unique_id.startswith("font:") else "asset" if unique_id.startswith("asset:") else "library"
        developers = ", ".join(d["name"] for d in library.get("developers", []) if d.get("name"))
        entries.append({
            "id": unique_id,
            "name": library["name"],
            "version": library.get("artifactVersion") or None,
            "authors": developers or (library.get("organization") or {}).get("name"),
            "kind": kind,
            "licenseName": ", ".join(licenses[key]["name"] for key in keys if key in licenses),
            "licenseUrl": licenses[first]["url"] if first in licenses else None,
            "licenseKey": with_text,
            "text": None,
        })
    used = {entry["licenseKey"] for entry in entries if entry["licenseKey"]}
    return entries, {key: value for key, value in licenses.items() if key in used}


def swift_entries():
    if not SOURCE_PACKAGES.exists():
        fail(f"{SOURCE_PACKAGES} is missing: run ./scripts/ios-build.sh first so the Swift packages are checked out.")
    versions = {
        pin["identity"]: pin["state"].get("version") or pin["state"].get("revision", "")[:7]
        for pin in json.loads(PACKAGE_RESOLVED.read_text())["pins"]
    }
    with tempfile.TemporaryDirectory() as out:
        run([
            "license-plist", "--force", "--fail-if-missing-license", "--suppress-opening-directory",
            "--package-path", str(PACKAGE_RESOLVED), "--package-sources-path", str(SOURCE_PACKAGES),
            "--output-path", out, "--prefix", "lp",
        ])
        index = plistlib.loads((Path(out) / "lp.plist").read_bytes())["PreferenceSpecifiers"]
        entries = []
        for item in index:
            if "File" not in item:
                continue
            identity = Path(item["File"]).name.lower()
            if identity not in versions:
                fail(f"LicensePlist listed {identity}, which is not in Package.resolved")
            text = plistlib.loads((Path(out) / f"{item['File']}.plist").read_bytes())["PreferenceSpecifiers"][0]["FooterText"]
            name, spdx_url = next(
                ((label, f"https://spdx.org/licenses/{spdx}.html") for spdx, label, marker in SPDX_MARKERS if marker in text),
                ("License", None),
            )
            entries.append({
                "id": f"swift:{identity}",
                "name": item["Title"],
                "version": versions[identity],
                "authors": None,
                "kind": "library",
                "licenseName": name,
                "licenseUrl": spdx_url,
                "licenseKey": None,
                "text": text.strip(),
            })
    missing = set(versions) - {entry["id"].removeprefix("swift:") for entry in entries}
    if missing:
        fail(f"No license found for: {', '.join(sorted(missing))}")
    return entries


def main():
    libraries, licenses = kotlin_entries()
    entries = libraries + swift_entries()
    entries.sort(key=lambda entry: entry["id"])
    OUTPUT.write_text(json.dumps({"licenses": licenses, "entries": entries}, indent=1, ensure_ascii=False, sort_keys=True) + "\n")
    print(f"Wrote {len(entries)} entries to {OUTPUT.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
