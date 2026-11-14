# Releasing Android (Google Play)

How a `prod` release bundle is signed, built and uploaded. The Play Console entry itself (listing, forms, policy answers) is in [`PLAY_CONSOLE_CHECKLIST.md`](PLAY_CONSOLE_CHECKLIST.md); the store texts and graphics are in [`store/google-play/`](../store/google-play/README.md).

## Signing model

MobTT uses **Play App Signing**. Google holds the *app signing key* that users' devices verify. We hold only an **upload key**, which proves to Play that an upload comes from us. If the upload key is lost or leaked, Play support can reset it; the app signing key stays safe. The first upload to a new app must be signed with the upload key you register.

`androidApp/build.gradle.kts` reads the upload key from settings that are never committed. Each is `release.<name>` in the untracked `local.properties`, or `RELEASE_<NAME>` in the environment (CI):

| Setting | `local.properties` | Environment | Meaning |
|---|---|---|---|
| Keystore path | `release.storeFile` | `RELEASE_STOREFILE` | path to the `.jks`, absolute or relative to the repo root |
| Keystore password | `release.storePassword` | `RELEASE_STOREPASSWORD` | |
| Key alias | `release.keyAlias` | `RELEASE_KEYALIAS` | |
| Key password | `release.keyPassword` | `RELEASE_KEYPASSWORD` | |

With `storeFile` unset, the release build type has no signing config and produces an **unsigned** bundle. That keeps `./gradlew check` and CI without secrets working. `.gitignore` excludes `*.jks`, `*.keystore` and `*.p12`.

## Create the upload key (once)

Keep it outside the repository, for example in `~/.android-keys/`:

```sh
mkdir -p ~/.android-keys
keytool -genkeypair -v -keystore ~/.android-keys/mobtt-upload.jks \
  -alias mobtt-upload -keyalg RSA -keysize 2048 -validity 10000
```

Then in `local.properties`:

```properties
release.storeFile=/Users/<you>/.android-keys/mobtt-upload.jks
release.storePassword=...
release.keyAlias=mobtt-upload
release.keyPassword=...
```

**Back the keystore and both passwords up in a password manager now.** Play can reset a lost upload key, but only through a support request that takes days. In Play Console, choose Play App Signing when creating the app and let Google generate the app signing key (the default).

## Build the release bundle

```sh
./gradlew :androidApp:bundleProdRelease
```

Output: `androidApp/build/outputs/bundle/prodRelease/androidApp-prod-release.aab`. It needs the real, untracked config files for a useful build: `androidApp/src/prod/google-services.json` (otherwise the app ships with Firebase off), `xttv.apiKey` in `local.properties` or `XTTV_API_KEY` (otherwise it shows no data), and optionally `support.url`. See [`BUILD_VARIANTS.md`](BUILD_VARIANTS.md).

Check the result before uploading:

```sh
BT=$(ls -d ~/Library/Android/sdk/build-tools/* | tail -1)
# the AAB is signed with the upload key
jarsigner -verify -verbose:summary androidApp/build/outputs/bundle/prodRelease/androidApp-prod-release.aab | head -3
keytool -list -v -keystore ~/.android-keys/mobtt-upload.jks -alias mobtt-upload | grep SHA256
```

To try the exact release code on a device, build the APK with the same settings (`./gradlew :androidApp:assembleProdRelease`) and install `androidApp/build/outputs/apk/prod/release/androidApp-prod-release.apk`. It installs as `at.stefanirndorfer.mobtt`, next to the other environments.

### What the release build does

- **R8 is on** (`isMinifyEnabled` and `isShrinkResources`). `proguard-rules.pro` is empty on purpose: the libraries (Koin, Ktor, kotlinx.serialization, SQLDelight, Firebase) ship consumer rules. The `prod` APK was run on an emulator and loaded Associations from XTTV; still tap through all screens in the release APK before the first upload, and add `-keep` rules if something misbehaves.
- **Mapping file:** the AAB carries `proguard.map` for Play's deobfuscation. The Crashlytics Gradle plugin also uploads the mapping (`uploadCrashlyticsMappingFileProdRelease`) as part of a release build with a `prod` `google-services.json`.
- **16 KB page size and 64-bit:** the only native libraries (`libandroidx.graphics.path.so`, `libdatastore_shared_counter.so`) are 16 KB aligned and ship `arm64-v8a` and `x86_64` next to the 32-bit ABIs.
- `debRelease` is disabled; `prod` is the only flavor that goes to Play.

## Versioning

`versionCode` and `versionName` are read from Gradle properties and default to `1` and `1.0` in `androidApp/build.gradle.kts`. **Play rejects an upload whose `versionCode` it has already seen**, even from a draft. For each release either raise the defaults in the file and commit, or pass them on the command line (for CI: the run number):

```sh
./gradlew :androidApp:bundleProdRelease -PversionCode=2 -PversionName=1.0.1
```

`versionName` is also sent to XTTV in the `User-Agent` (`AppConfig.appVersion`). Tag the commit you released (`android-v1.0.1`) so the mapping file and the Play build can be matched later.

## CI

Store the keystore as a base64 secret and write it to disk in the job, then pass the four `RELEASE_*` variables plus `XTTV_API_KEY` and the `prod` `google-services.json` from secrets:

```sh
echo "$RELEASE_KEYSTORE_B64" | base64 --decode > "$RUNNER_TEMP/upload.jks"
RELEASE_STOREFILE="$RUNNER_TEMP/upload.jks" ./gradlew :androidApp:bundleProdRelease -PversionCode="$GITHUB_RUN_NUMBER"
```

No CI exists in the repo yet; this is the shape it needs.

## Upload

First release: create the app in Play Console, finish the checklist, upload the AAB to **Internal testing**, install from the opt-in link, then promote to Production. The new-account testing requirement for personal developer accounts is in the checklist. Later releases: build with a higher `versionCode`, upload to the track, add release notes (copy from `store/google-play/<locale>/release-notes.txt`).
