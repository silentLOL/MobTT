#!/usr/bin/env bash
# Mechanical rules from CLAUDE.md that ktlint cannot express. Exits non-zero on any violation.
set -u
cd "$(dirname "$0")/.."

status=0
fail() {
  echo "$1"
  status=1
}

android_sources() {
  find android androidApp -path '*/src/main/*' -name '*.kt' -not -path '*/build/*'
}

kotlin_sources() {
  find android androidApp core domain shared-umbrella -name '*.kt' -not -path '*/build/*'
}

# §10: every file declaring a Composable ships a @BatScreenPreview.
for file in $(android_sources); do
  case "$file" in
    */designsystem/theme/* | */designsystem/preview/* | */core/ui/*/ObserveAsEvents.kt) continue ;;
  esac
  if grep -q '^@Composable' "$file" && ! grep -q '@BatScreenPreview' "$file"; then
    fail "$file: declares a @Composable without a @BatScreenPreview (CLAUDE.md §10)"
  fi
done

# §9: design-system components take sizes from tokens, not dp literals.
for file in $(android_sources); do
  case "$file" in
    */designsystem/ui/components/*) ;;
    *) continue ;;
  esac
  grep -n '[0-9]\.dp\b' "$file" | sed "s|^|$file:|; s|$| <- dp literal, use BatDesign.spacing/sizing (CLAUDE.md §9)|" | while read -r line; do echo "$line"; done
  if grep -q '[0-9]\.dp\b' "$file"; then
    status=1
  fi
done

# §9: icons go through BatIcon.
for file in $(android_sources); do
  case "$file" in
    */designsystem/icon/BatIcon.kt) continue ;;
  esac
  if grep -n 'R\.drawable\.' "$file" > /dev/null; then
    fail "$file: references R.drawable directly, use BatIcon (CLAUDE.md §9)"
  fi
done

# §5: if/else bodies are braced.
for file in $(kotlin_sources); do
  hits=$(grep -nE '(^|[^a-zA-Z_])(if|else if) \(.*\) +[^{ ]|(^|[^a-zA-Z_])else +[^{ ]' "$file" | grep -vE '\{$|&&$|\|\|$|,$|^[0-9]+: *(//|\*)|^[0-9]+: *else ->' || true)
  if [ -n "$hits" ]; then
    fail "$file: unbraced if/else (CLAUDE.md §5)"
    echo "$hits" | sed 's/^/    /'
  fi
done

# A test class must not reuse the name of a main class in the same package: Kotlin accepts it, and the build
# then fails at runtime with NoSuchMethodError.
for main_dir in $(find core domain shared-umbrella -type d -path '*/src/commonMain/kotlin' -not -path '*/build/*'); do
  test_dir="${main_dir%commonMain/kotlin}commonTest/kotlin"
  [ -d "$test_dir" ] || continue
  for test_file in $(find "$test_dir" -name '*.kt'); do
    for name in $(grep -oE '^(public |internal )?(data |sealed |enum |fun )?(class|object|interface) [A-Z][A-Za-z0-9]*' "$test_file" | awk '{print $NF}'); do
      relative_dir=$(dirname "${test_file#"$test_dir"/}")
      if grep -qE "^(public |internal )?(data |sealed |enum |fun )?(class|object|interface) $name\\b" "$main_dir/$relative_dir"/*.kt 2>/dev/null; then
        fail "$test_file: declares $name, which already exists in commonMain of the same package"
      fi
    done
  done
done

# A Kotlin exception reaches Swift as an NSError, so `catch is <Kotlin type>` compiles and never matches (docs/SKIE_BRIDGE.md).
for file in $(find iosApp -name '*.swift' -not -path '*/build/*'); do
  hits=$(grep -nE 'catch (is|let [a-z]+ as) (DataUnavailable|Kotlin[A-Za-z]*)' "$file" || true)
  if [ -n "$hits" ]; then
    fail "$file: catches a Kotlin exception type, which never matches; use a DataResult (docs/SKIE_BRIDGE.md)"
    echo "$hits" | sed 's/^/    /'
  fi
done

exit "$status"
