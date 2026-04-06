This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM), Server.

## 개발 기록

### 2026-04-06

- Android 시작 화면을 `molla AI` 온보딩 카드 구조로 정리했습니다.
- `ChatGPT 연결하기` 버튼을 추가하고, Android에서 OpenAI 공식 API 키 발급 페이지(`https://platform.openai.com/api-keys`)를 브라우저로 여는 연결 로직을 넣었습니다.
- Google Credential Manager 기반 로그인 흐름을 Android에 추가했습니다.
- 로그인 성공 시 앱 안에서 Google 계정 이메일과 이름을 표시하도록 연결했습니다.
- 구현 위치:
  - 공통 UI: [`composeApp/src/commonMain/kotlin/com/molla/mollaai/App.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/commonMain/kotlin/com/molla/mollaai/App.kt)
  - Android 진입점: [`composeApp/src/androidMain/kotlin/com/molla/mollaai/MainActivity.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/androidMain/kotlin/com/molla/mollaai/MainActivity.kt)
  - Android Google 로그인 코디네이터: [`composeApp/src/androidMain/kotlin/com/molla/mollaai/GoogleSignInCoordinator.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/androidMain/kotlin/com/molla/mollaai/GoogleSignInCoordinator.kt)
  - Android 외부 링크 실행: [`composeApp/src/androidMain/kotlin/com/molla/mollaai/OpenAIConnectionLauncher.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/androidMain/kotlin/com/molla/mollaai/OpenAIConnectionLauncher.kt)
- Android 설정 변경:
  - `composeApp/src/androidMain/AndroidManifest.xml`에 `INTERNET` 권한 추가
  - `composeApp/build.gradle.kts`에 Credential Manager / Google ID 의존성 추가
  - `gradle/libs.versions.toml`에 관련 버전 추가
- Android 컴파일 확인:
  - `./gradlew :composeApp:compileDebugKotlinAndroid`
  - 결과: 성공

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
      folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:

- for the Wasm target (faster, modern browsers):
    - on macOS/Linux
      ```shell
      ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
      ```
    - on Windows
      ```shell
      .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
      ```
- for the JS target (slower, supports older browsers):
    - on macOS/Linux
      ```shell
      ./gradlew :composeApp:jsBrowserDevelopmentRun
      ```
    - on Windows
      ```shell
      .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
      ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack
channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).
