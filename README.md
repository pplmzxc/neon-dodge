# 네온 피하기

안드로이드용 한 손 장애물 피하기 게임 시제품입니다.

- 손가락을 좌우로 움직여 분홍 장애물을 피하세요.
- 생존할수록 점수와 난이도가 올라갑니다.
- 최고 기록 저장, 재시작, 일시정지를 지원합니다.
- 광고·결제·네트워크 권한은 아직 없습니다.

## 테스트 APK
Actions → 성공한 Build test APK 실행 → Artifacts의 neon-dodge-test-apk를 다운로드하세요. ZIP을 풀면 app-debug.apk가 있습니다.
실제 기기에서 시작, 이동, 충돌, 재시작, 홈 화면 복귀, 최고 기록 저장을 확인해야 합니다.
스토어 출시용 서명이나 수익화 설정이 없는 테스트 버전입니다.

## 개발
JDK 17, Gradle 8.11.1, Android SDK 35에서 `gradle assembleDebug`로 빌드합니다.
GitHub Actions는 빌드 서버에 설치된 Android SDK를 사용합니다.
