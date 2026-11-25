# OpenPres Viewer (Android)

> One of the OpenPres repositories: the server is [OpenPres](https://github.com/NathanDSanta/OpenPres).
> The viewer app is released here, on its own ([releases](https://github.com/NathanDSanta/OpenPres-V/releases)).
> For live translation on phones there's the [Translation app](https://github.com/NathanDSanta/OpenPres-T).

A simple viewer for OpenPres: it shows one of the server's screens, as big as it fits, and plays
one of its audio buses. It can't change anything on the server.

## Install

Get `OpenPres-Viewer-X.Y.Z.apk` from the
[latest release](https://github.com/NathanDSanta/OpenPres-V/releases/latest) and open it on the
phone or tablet (Android 7 or newer; allow installing apps from your browser or files app when
Android asks).

The app looks for a newer version when it opens, and offers to download it.

On the server, the admin lets viewers in: Admin → Settings → Server & security → **Viewers**,
with a viewer password of their own or none. Other devices also need "Let managers on other
computers and tablets connect".

## Development

Open the folder in Android Studio, or from a terminal with the Android SDK and JDK 17:

```sh
./gradlew lintDebug testDebugUnitTest   # what the checks run
./gradlew assembleDebug                 # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease -PversionName=0.8.0
```

A release build is signed with the key in `ANDROID_KEYSTORE_FILE` (with
`ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS` and `ANDROID_KEY_PASSWORD`) when that is set:
the release workflow takes them from the repository's secrets. Without it, the APK is signed
with a throwaway key, and a new version can only be installed after removing the old one.

### Branches and pull requests

Work on a branch named `feature/…`, `fix/…`, `docs/…`, `chore/…` or `ci/…`, and open the
pull request into `develop` with a title like `feat: …` or `fix: …`. The checks (lint and the
unit tests) run on every pull request; an APK is built only for releases. Betas and stable
releases: [docs/RELEASING.md](docs/RELEASING.md).
