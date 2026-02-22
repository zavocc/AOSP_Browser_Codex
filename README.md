## AOSP Browser
A resurrected version of AOSP Browser, made to work with modern Android (currenly supports Android 5+ for now). Near-usable browser using WebView, reduced dependencies, and built to be compilable and independent outside of AOSP source tree.

As a bonus, it is as close to the AOSP browser, with minimal or fewer modifications.

## Why?
I've been using AOSP Browser since the early days of using Android before Chrome web browser was the default browser shipped to Android JellyBean. It's fast, straightforward, and lightweight to use.

As of Android 7+ the default browser shipped in AOSP is WebView Tester, also known as [Browser2](https://android.googlesource.com/platform/packages/apps/Browser2/) which is part of AOSP becoming more of a reference Android core where OEMs can build on top of and not something we can normally use unless GApps or replacement apps is shipped. The WebView Tester is not a proper web browser either as it only showcases the WebView widget for Android apps using the default WebView provider.

Because we now have tools like [Codex](https://openai.com/codex/) and the models themselves have gotten really good with long-running refactors and working with codebases, I had decided to push the limits of what Codex can do... also for my personal free time.

## Technical Specifications
The project is almost entirely done with Codex 5.3 while human-in-the-loop principle is still applied: stability and UX checks, functionality testing, and logcat capture.

Before making changes to Browser, instead of using `repo` tool. The source code is simply fetched by `git`

```
git clone https://android.googlesource.com/platform/packages/apps/Browser
```
Now it may look like it's a standalone app source code, but it is intertwined to AOSP source tree. This includes:

- No gradle files, just Android makefiles (Android.mk). This means Codex 5.3 had to generate gradle files in order to generate and run `./gradlew` script.

- A lot of imports coming from outside AOSP source code which would have been blocked outright without pulling the entire source or manually satisfy dependencies as some if not most imports are not available from the standard Android SDK. And, at the same time, while it's looking for dependencies it is replaced with either a stub or existing alternatives.

## What's changed
The last commit of this Browser app was from 2015, which is during the Android M release. This app was heavily outdated, and it has been more than decades apart.

For list of changes done by codex initially, please see [changes_codex_summary.md](./changes_codex_summary.md) and [git status output](./initial_commit_changed_and_new.txt)

Here are the user-facing and backend changes:
- The app is nearly isolated, enabling rootless installs:
    - Removed as a bookmarks provider (which is a cause of conflict during installation which collides with Bookmarks Provider `com.android.bookmarkprovider` in most ROMs), to ensure the Browser works as faithfully, the provider is private instead.
        - The bookmarks functionality is working and bookmarks saved is stored per-app data basis.
    - Any sync adapters or storage such as Google Sync Adapters functionality that used to sync browser bookmarks to desktop chrome is removed.
- Uses system WebView. This means, whatever the device has a WebView provider set (e.g. AS WebView Canary or Vanadium WebView), AOSP browser will use it. Many sites should work. At the same time, you are technically using AOSP Browser as a skin to Chrome.
- [Accessibility]: Inverted contrast is removed and changed to "Dark Mode" toggle instead as found in Settings > Accessibility due to WebView widget not supporting such functionality to alter site display when the app wants to.

## What works
- Bookmarks
- Site controls (e.g. JavaScript Toggle, Accessibility)
- [PARTIAL]: Web Permissions e.g. Location
- [LABS]: Quick Controls
- Media Playback
- App Intents
- Downloads
- Set image as wallpaper
- History
- Refresh/Partial Load/Stop
## What doesn't work
- "View Certificates" from Page Info
- Find in page (due to limitations with WebView widget)
- Widget (Bookmarks selector outright closes)

## Installation and Build
If you're looking to install, please see releases page of this repo. Install at your own risk! As I am not responsible for any damages done to your device.

### Prepare environment
Android Studio is not required, however keep in mind that this has been built with Ubuntu 24.04.

You only need Android SDK tools environment variables loaded such as `ANDROID_SDK_ROOT` and `ANDROID_HOME` as well as update your `PATH` pointing to commandline tools, here's an example to put on bashrc:
```bash
export ANDROID_SDK_ROOT=$HOME/android-sdk
export ANDROID_HOME=$ANDROID_SDK_ROOT
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools
```

Note that the `$ANDROID_SDK_ROOT` must have `cmdline-tools/latest` folder with `sdkmanager`:
```
$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager
```
Before you begin, you will need to have `openjdk-17-jdk` installed or higher.

If you're starting with fresh Ubuntu environment and after setting up the environment variables, you can accept, update, and install dependencies:
```bash
yes | sdkmanager --licenses
sdkmanager --install "platform-tools" "platforms;android-34" "build-tools;34.0.0" "platforms;android-30"
```

Then, you'll also need an up-to-date `gradle` package to be installed, you can [follow instructions](https://docs.gradle.org/current/userguide/installation.html) on how to do so.

Afterwards, you can test if the app can be built using `./gradlew :app:assembleDebug` and should produce an artifact under `app/build/outputs/apk/debug/app-debug.apk`

NOTE: The app's minimum SDK is 21, meaning this requires devices running Lollipop.

## FAQ
### Can I daily drive this?
Yes
### Is the browser secure to be used on a daily basis?
Yes, at least the underlying engine used is powered by Chromium and is updatable through Google Play.

Keep in mind that the provided APK is debuggable by default, Browser data such as history and bookmarks can easily be extracted through SQLite viewing tools and data can be pulled with `run-as` command.

Note that cookie data is handled with the system WebView using `android.webkit.CookieManager` data store API. See  
- src/com/android/browser/BrowserSettings.java:318
-  src/com/android/browser/BrowserSettings.java:443
- src/com/android/browser/BrowserWebViewFactory.java:69
- src/com/android/browser/WebViewCompatibility.java:37

For related vulnerabilities see: https://www.rootzwiki.com/threads/major-security-hole-in-aosp-web-browser.39576/

### Will you add more features like privacy protections and ad-blocking?
No, this app demos on what Codex is capable of, which is making legacy decade old apps to be functional on modern Android and to be decoupled from AOSP source.

The goal here is for the app to be as close to the mainstream as possible, not a recreation of the app.

### Why Codex 5.3 is used?
We found that Codex is reliable on heavy refactors, implements best practices, and reliable at precise and targeted multi-file search and edits. At the same time, keeping the application as closer to the mainstream as possible. Even with compaction (5 compactions), it was able to finish the task on-lock.

Note that it still takes human feedback involved, as during my initial testing, I found crashes, inconsistent UI such as mix of holo and material 2, and useful logcat logs such as permission denials but without crash handler due to missing replacement functionality. 

For successful initial rectification, it took me 1 + roughly 10 follow-up prompts to make this possible.

