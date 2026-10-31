#!/usr/bin/env bash
# Syncs the English string resources with Loco (localise.biz).
#   scripts/loco.sh push   upload Android strings.xml and iOS Localizable.xcstrings (needs loco.fullAccessKey)
#   scripts/loco.sh pull   download them back into the repo (needs loco.readKey)
# Keys live in the git-ignored local.properties. See docs/LOCALIZATION.md.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
API="https://localise.biz/api"
ANDROID_RES="$ROOT/android/core/localization/src/main/res"
IOS_CATALOG="$ROOT/iosApp/iosApp/Resources/Localizable.xcstrings"

# Loco locale code | Android values folder | iOS language code.
# Add a row (e.g. "de-AT|values-de-rAT|de-AT") when a translation joins.
LOCALES=(
  "en|values|en"
)

prop() {
  local value
  value="$(grep -E "^$1=" "$ROOT/local.properties" 2>/dev/null | head -1 | cut -d= -f2-)" || true
  [[ -n "$value" ]] || { echo "Missing $1 in local.properties" >&2; exit 1; }
  printf '%s' "$value"
}

# $1 = key. The key goes through a curl config on stdin, not the command line.
loco() {
  local key="$1"
  shift
  curl --fail-with-body -sS --config <(printf 'header = "Authorization: Loco %s"\n' "$key") "$@"
}

push() {
  local key
  key="$(prop loco.fullAccessKey)"
  tmp="$(mktemp -d)"
  trap 'rm -rf "$tmp"' EXIT

  for row in "${LOCALES[@]}"; do
    IFS='|' read -r locale folder lang <<<"$row"

    if ! loco "$key" -o /dev/null "$API/locales/$locale" 2>/dev/null; then
      echo "Creating locale $locale"
      loco "$key" -o /dev/null -d "code=$locale" "$API/locales"
    fi

    echo "Pushing Android $locale"
    loco "$key" -H 'Content-Type: text/plain; charset=utf-8' --data-binary "@$ANDROID_RES/$folder/strings.xml" \
      "$API/import/xml?locale=$locale&index=id&tag-all=android&format=android" | jq -r '.message'

    # Loco takes one locale per file, so cut the catalog down to this language first.
    jq --arg lang "$lang" '.sourceLanguage = $lang
      | .strings |= with_entries(select(.value.localizations[$lang] != null)
          | .value.localizations |= {($lang): .[$lang]})' "$IOS_CATALOG" >"$tmp/ios-$locale.xcstrings"
    echo "Pushing iOS $locale"
    loco "$key" -H 'Content-Type: text/plain; charset=utf-8' --data-binary "@$tmp/ios-$locale.xcstrings" \
      "$API/import/xcstrings?locale=$locale&index=id&tag-all=ios" | jq -r '.message'
  done
}

pull() {
  local key
  key="$(prop loco.readKey)"
  tmp="$(mktemp -d)"
  trap 'rm -rf "$tmp"' EXIT

  for row in "${LOCALES[@]}"; do
    IFS='|' read -r locale folder lang <<<"$row"

    echo "Pulling Android $locale"
    mkdir -p "$ANDROID_RES/$folder"
    loco "$key" -o "$tmp/strings.xml" "$API/export/locale/$locale.xml?filter=android&format=android"
    mv "$tmp/strings.xml" "$ANDROID_RES/$folder/strings.xml"

    # Merge into the local catalog so other languages (the existing "de") stay untouched.
    echo "Pulling iOS $locale"
    loco "$key" -o "$tmp/ios.xcstrings" "$API/export/locale/$locale.xcstrings?filter=ios"
    jq --arg lang "$lang" --slurpfile remote "$tmp/ios.xcstrings" '
      reduce ($remote[0].strings | to_entries[]) as $e (.;
        .strings[$e.key].extractionState = "manual"
        | .strings[$e.key].localizations[$lang] = $e.value.localizations[$lang])' \
      "$IOS_CATALOG" >"$tmp/merged.xcstrings"
    mv "$tmp/merged.xcstrings" "$IOS_CATALOG"
  done
}

case "${1:-}" in
  push) push ;;
  pull) pull ;;
  *) echo "Usage: $0 push|pull" >&2; exit 2 ;;
esac
