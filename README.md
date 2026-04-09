This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM), Server.

## 개발 기록

### 2026-04-08

- Google 로그인 이후에 SMS 핸드폰 인증 흐름을 추가했습니다.
- 전화번호 인증은 `인증번호 요청`과 `인증번호 확인` 2개의 서버 API로 나뉩니다.
- 서버는 6자리 인증번호를 생성해서 3분 TTL 임시 저장소에 보관합니다. 현재 구현은 `PhoneVerificationCodeStore`의 in-memory TTL 버전입니다.
- SMS 발송은 `SolapiSmsSender`를 직접 사용합니다.
- 인증이 완료되면 `app_users` 테이블의 사용자 레코드에 국제전화 형식 전화번호를 저장합니다. 예: `+821012345678`, `+14155552671`.
- 사용자 테이블은 내부 식별용 UUID `id`를 별도로 가지도록 바꿨고, `google_subject`는 유니크 키로 유지합니다.
- Android 화면에 국가 코드, 전화번호, 인증번호 입력 UI를 추가했습니다.
- Google 로그인 후 서버 응답에 저장된 전화번호가 있으면 앱에 바로 표시합니다.
- 구현 위치:
  - 공통 UI: [`composeApp/src/commonMain/kotlin/com/molla/mollaai/App.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/commonMain/kotlin/com/molla/mollaai/App.kt)
  - Android 진입점: [`composeApp/src/androidMain/kotlin/com/molla/mollaai/MainActivity.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/androidMain/kotlin/com/molla/mollaai/MainActivity.kt)
  - Android 전화 인증 클라이언트: [`composeApp/src/androidMain/kotlin/com/molla/mollaai/BackendPhoneAuthClient.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/androidMain/kotlin/com/molla/mollaai/BackendPhoneAuthClient.kt)
  - 서버 전화 인증 컨트롤러: [`server/src/main/kotlin/com/molla/mollaai/controller/PhoneAuthController.kt`](/Users/ralph/BackEnd/MollaAI/server/src/main/kotlin/com/molla/mollaai/controller/PhoneAuthController.kt)
  - 서버 전화 인증 서비스: [`server/src/main/kotlin/com/molla/mollaai/phone/service/PhoneVerificationService.kt`](/Users/ralph/BackEnd/MollaAI/server/src/main/kotlin/com/molla/mollaai/phone/service/PhoneVerificationService.kt)
  - 서버 JWT 검증 서비스: [`server/src/main/kotlin/com/molla/mollaai/auth/service/JwtAccessTokenService.kt`](/Users/ralph/BackEnd/MollaAI/server/src/main/kotlin/com/molla/mollaai/auth/service/JwtAccessTokenService.kt)
  - 서버 사용자 엔티티: [`server/src/main/kotlin/com/molla/mollaai/auth/entity/AppUserEntity.kt`](/Users/ralph/BackEnd/MollaAI/server/src/main/kotlin/com/molla/mollaai/auth/entity/AppUserEntity.kt)
  - 전화번호 정규화 테스트: [`server/src/test/kotlin/com/molla/mollaai/PhoneVerificationServiceTest.kt`](/Users/ralph/BackEnd/MollaAI/server/src/test/kotlin/com/molla/mollaai/PhoneVerificationServiceTest.kt)

### 2026-04-06

- Android 시작 화면을 `molla AI` 온보딩 카드 구조로 정리했습니다.
- Google Credential Manager 기반 로그인 흐름을 Android에 추가했습니다.
- 로그인 성공 시 앱 안에서 Google 계정 이메일과 이름을 표시하도록 연결했습니다.
- Google 로그인 직후 `idToken`을 Ktor 서버의 `/auth/google`로 전송하고, 서버에서 Google 서명 검증 후 앱 세션 JWT를 발급하는 흐름을 추가했습니다.
- 서버는 인메모리 사용자 저장소를 사용해 Google 사용자 레코드를 유지합니다.
- Android는 서버가 발급한 JWT를 `EncryptedSharedPreferences` 기반 안전 저장소에 보관하고, 앱 재실행 시 복원합니다.
- 서버 설정을 `server/src/main/resources/application.yaml` 기반으로 바꾸고, `AppConfig`는 그 YAML을 읽어 typed config로 변환하도록 변경했습니다.
- 구현 위치:
  - 공통 UI: [`composeApp/src/commonMain/kotlin/com/molla/mollaai/App.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/commonMain/kotlin/com/molla/mollaai/App.kt)
  - Android 진입점: [`composeApp/src/androidMain/kotlin/com/molla/mollaai/MainActivity.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/androidMain/kotlin/com/molla/mollaai/MainActivity.kt)
  - Android Google 로그인 코디네이터: [`composeApp/src/androidMain/kotlin/com/molla/mollaai/GoogleSignInCoordinator.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/androidMain/kotlin/com/molla/mollaai/GoogleSignInCoordinator.kt)
  - Android 서버 인증 클라이언트: [`composeApp/src/androidMain/kotlin/com/molla/mollaai/BackendAuthClient.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/androidMain/kotlin/com/molla/mollaai/BackendAuthClient.kt)
  - Android 세션 저장소: [`composeApp/src/androidMain/kotlin/com/molla/mollaai/AuthSessionStore.kt`](/Users/ralph/BackEnd/MollaAI/composeApp/src/androidMain/kotlin/com/molla/mollaai/AuthSessionStore.kt)
  - 서버 인증 서비스: [`server/src/main/kotlin/com/molla/mollaai/auth/GoogleIdTokenAuthService.kt`](/Users/ralph/BackEnd/MollaAI/server/src/main/kotlin/com/molla/mollaai/auth/GoogleIdTokenAuthService.kt)
  - 서버 사용자 저장소: [`server/src/main/kotlin/com/molla/mollaai/auth/UserRepository.kt`](/Users/ralph/BackEnd/MollaAI/server/src/main/kotlin/com/molla/mollaai/auth/UserRepository.kt)
  - 서버 인증 라우트: [`server/src/main/kotlin/com/molla/mollaai/Application.kt`](/Users/ralph/BackEnd/MollaAI/server/src/main/kotlin/com/molla/mollaai/Application.kt)
  - 서버 설정 파일: [`server/src/main/resources/application.yaml`](/Users/ralph/BackEnd/MollaAI/server/src/main/resources/application.yaml)
- Android 설정 변경:
  - `composeApp/src/androidMain/AndroidManifest.xml`에 `INTERNET` 권한 추가
  - `composeApp/build.gradle.kts`에 Credential Manager / Google ID 의존성 추가
  - `composeApp/build.gradle.kts`에 Ktor Client 의존성 추가
  - `composeApp/build.gradle.kts`에 Android Security Crypto 의존성 추가
  - `gradle/libs.versions.toml`에 관련 버전 추가
  - `composeApp/src/androidMain/res/values/strings.xml`에 Google Web Client ID와 서버 주소 추가
- 서버 실행 전 환경변수:
  - `GOOGLE_WEB_CLIENT_ID`
  - `APP_JWT_SECRET`
  - 선택: `APP_JWT_ISSUER`, `APP_JWT_AUDIENCE`
- 서버 설정 파일 예시:
  - `app.googleWebClientId: $GOOGLE_WEB_CLIENT_ID`
  - `app.jwtSecret: $APP_JWT_SECRET`
- Android 컴파일 확인:
  - `./gradlew :composeApp:compileDebugKotlinAndroid`
  - 결과: 성공
- 서버 컴파일 확인:
  - `./gradlew :server:compileKotlin`
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
