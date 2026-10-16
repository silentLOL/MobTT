#!/usr/bin/env bash
# Builds the iosApp scheme for a simulator and prints only errors and the build result.
# Set IOS_SIMULATOR_ID to override the first available iPhone simulator.
set -u
cd "$(dirname "$0")/.."

simulator_id="${IOS_SIMULATOR_ID:-$(xcrun simctl list devices available | grep -m1 iPhone | grep -oE '[0-9A-F]{8}-([0-9A-F]{4}-){3}[0-9A-F]{12}')}"
if [ -z "$simulator_id" ]; then
  echo "No available iPhone simulator found; set IOS_SIMULATOR_ID" >&2
  exit 1
fi

xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug \
  -destination "id=$simulator_id" -derivedDataPath build/xcode \
  CODE_SIGNING_ALLOWED=NO build 2>&1 | grep -E "error:|^\*\* BUILD" | tee /dev/stderr | grep -q "BUILD SUCCEEDED"
