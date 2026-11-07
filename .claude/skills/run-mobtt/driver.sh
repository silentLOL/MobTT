#!/usr/bin/env bash
# Drives MobTT on the Android emulator (adb) and the iOS simulator (simctl). Run from anywhere; paths resolve from the repo root.
set -u
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
ANDROID_PKG=at.stefanirndorfer.mobtt.deb
IOS_BUNDLE=at.stefanirndorfer.mobtt.deb
IOS_APP="$ROOT/build/xcode/Build/Products/Debug-iphonesimulator/MobTT.app"
AVD="${AVD:-Pixel_9a}"
ios_sim() { echo "${IOS_SIMULATOR_ID:-$(xcrun simctl list devices available | grep -m1 iPhone | grep -oE '[0-9A-F]{8}-([0-9A-F]{4}-){3}[0-9A-F]{12}')}"; }

ui_dump() { adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; adb shell cat /sdcard/ui.xml; }

# Prints "text | content-desc | bounds" for every element that has a text or description.
ui_list() {
  ui_dump | python3 -c '
import sys,re
x=sys.stdin.read()
for m in re.finditer(r"text=\"([^\"]*)\"[^>]*content-desc=\"([^\"]*)\"[^>]*bounds=\"(\[[^\]]*\]\[[^\]]*\])\"",x):
    if m.group(1) or m.group(2): print(" | ".join(m.groups()))
'
}

# Taps the centre of the Nth (default 1st) element whose text or content-desc contains $1.
tap_text() {
  local pt
  pt=$(ui_dump | python3 -c '
import sys,re
needle=sys.argv[1]; n=int(sys.argv[2]); x=sys.stdin.read()
for m in re.finditer(r"text=\"([^\"]*)\"[^>]*content-desc=\"([^\"]*)\"[^>]*bounds=\"\[(\d+),(\d+)\]\[(\d+),(\d+)\]\"",x):
    if needle in m.group(1) or needle in m.group(2):
        n-=1
        if n==0:
            print((int(m.group(3))+int(m.group(5)))//2, (int(m.group(4))+int(m.group(6)))//2); break
' "$1" "${2:-1}")
  [ -n "$pt" ] || { echo "no element containing: $1" >&2; return 1; }
  # shellcheck disable=SC2086
  adb shell input tap $pt
}

case "${1:-help}" in
  android-boot)
    adb get-state >/dev/null 2>&1 || { nohup "$HOME/Library/Android/sdk/emulator/emulator" -avd "$AVD" -no-snapshot-save -no-audio >/dev/null 2>&1 & }
    adb wait-for-device
    until [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = 1 ]; do sleep 2; done
    echo "emulator ready" ;;
  android-install) (cd "$ROOT" && ./gradlew :androidApp:installDebDebug --console=plain) ;;
  android-install-clean)
    # Fixes "is located outside the root directory" after the project folder was renamed or moved.
    cd "$ROOT" || exit 1
    tasks=()
    while IFS= read -r d; do
      m=${d%/build/intermediates/runtime_library_classes_dir}
      tasks+=("$(echo ":${m}" | tr / :):clean")
    done < <(find android androidApp -path '*/build/intermediates/runtime_library_classes_dir' -type d -prune)
    ./gradlew "${tasks[@]}" :androidApp:installDebDebug --no-build-cache --console=plain ;;
  android-launch) adb shell am force-stop $ANDROID_PKG; adb shell monkey -p $ANDROID_PKG -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1; sleep 4 ;;
  android-ss) adb exec-out screencap -p > "${2:?usage: android-ss FILE.png}" ;;
  android-ui) ui_list ;;
  android-tap) tap_text "${2:?usage: android-tap TEXT [N]}" "${3:-1}" ;;
  android-swipe-up) adb shell input swipe 540 1800 540 400 250 ;;
  ios-build) (cd "$ROOT" && IOS_SIMULATOR_ID="$(ios_sim)" ./scripts/ios-build.sh) ;;
  ios-launch)
    sim="$(ios_sim)"
    xcrun simctl boot "$sim" 2>/dev/null
    xcrun simctl bootstatus "$sim" -b >/dev/null 2>&1
    xcrun simctl install "$sim" "$IOS_APP" && xcrun simctl launch "$sim" $IOS_BUNDLE
    sleep 4 ;;
  ios-ss) xcrun simctl io "$(ios_sim)" screenshot "${2:?usage: ios-ss FILE.png}" ;;
  *) sed -n '2p' "$0"; echo "commands: android-boot android-install android-install-clean android-launch android-ss FILE android-ui android-tap TEXT [N] android-swipe-up ios-build ios-launch ios-ss FILE" ;;
esac
