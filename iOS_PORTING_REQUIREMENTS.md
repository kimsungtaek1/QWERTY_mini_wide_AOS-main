# QWERTY Mini Wide iOS 포팅 요구사항

## 📱 프로젝트 개요
QWERTY Mini Wide는 한국어 키보드 앱으로, 컴팩트한 QWERTY 레이아웃과 고급 한글 조합 기능을 제공하는 Android 키보드 애플리케이션입니다.

### 핵심 특징
- 한글 자모 자동 조합 엔진
- 다중 언어 지원 (한국어/영어)
- 멀티탭 입력 시스템
- 동시 키 입력 지원
- 음성 입력 기능
- 진동 피드백
- 다크/라이트 테마 자동 전환

## 🏗️ iOS 앱 구조

### 1. 메인 앱 구조
```
iOS App
├── Launch Screen (Splash_Activity 대체)
├── Main View Controller (Home_Activity 대체)
├── Settings View Controller (Setting_Activity 대체)
├── Keyboard Preview Controller (CustomKeyBoard_Activity 대체)
├── Terms & Privacy Controller (Agree_Activity 대체)
├── Usage Guide Controller (HowUsed_Activity 대체)
└── Keyboard Extension Target (CustomKeyBoard_Service 대체)
```

### 2. 필요한 권한 및 Capabilities
- **Microphone** - 음성 입력을 위한 마이크 권한
- **Haptic Feedback** - 키 입력 시 진동 피드백
- **Network Access** - 업데이트 및 데이터 다운로드
- **App Groups** - 메인 앱과 키보드 확장 간 데이터 공유

## ⌨️ 키보드 확장 (Keyboard Extension)

### 키보드 레이아웃 구조
```
[제안 바 - 화살표 버튼, 완료 버튼]
[ㅂ(ㅍ)] [ㅈ(ㅊ)] [ㄷ(ㅌ)] [ㄱ(ㅋ)] [ㅅ] [ㅗ(ㅛ)] [ㅏ(ㅑ)] [ㅣ]
[ㅁ] [ㄴ] [ㅇ] [ㄹ] [ㅎ] [ㅜ(ㅠ)] [ㅓ(ㅕ)] [ㅡ]
[Shift] [123] [Space] [Enter] [Backspace]
```

### 핵심 클래스 및 iOS 대체 구현

#### 1. CustomKeyboardViewController (CustomKeyBoard_Service 대체)
```swift
class CustomKeyboardViewController: UIInputViewController {
    // 필수 구현 사항
    - 키보드 뷰 초기화
    - 텍스트 입력 프로토콜 구현
    - 테마 전환 (다크/라이트 모드)
    - 진동 피드백 (UIFeedbackGenerator)
    - 음성 인식 (SFSpeechRecognizer)
    - 커서 위치 추적
    - 언어 전환 (한/영/숫자)
}
```

#### 2. CustomKeyboardView (CustomKeyboardView.kt 대체)
```swift
class CustomKeyboardView: UIView {
    // 필수 구현 사항
    - 멀티터치 제스처 인식
    - 키 프리뷰 팝업 표시
    - 한글 자모 조합 처리
    - 멀티탭 입력 처리 (300ms 타이머)
    - Shift 상태 관리 (일반/활성/잠금)
    - 동시 키 입력 조합
}
```

#### 3. CustomKeyButton (CustomKeyButton.kt 대체)
```swift
class CustomKeyButton: UIControl {
    // 4개 텍스트 위치 지원
    var mainText: String      // 중앙 메인 텍스트
    var leftTopText: String   // 좌상단 보조 텍스트
    var rightTopText: String  // 우상단 보조 텍스트
    var rightBottomText: String // 우하단 보조 텍스트
    
    // 동적 텍스트 크기 조정
    - 메인 텍스트: 18pt
    - 보조 텍스트: 10pt
    
    // 백그라운드 상태
    - 일반/누름 상태 처리
    - 반투명 오버레이 (#20000000)
}
```

#### 4. HangulAutomata (HangulAutomata.kt 대체)
```swift
class HangulAutomata {
    // 한글 조합 엔진
    - 초성/중성/종성 분리 및 조합
    - 복합 모음 지원 (ㅘ, ㅙ, ㅚ 등)
    - 쌍자음 지원 (ㄲ, ㄸ, ㅃ, ㅆ, ㅉ)
    - 백스페이스 시 자소 분해
    - 실시간 음절 조합/분해
    
    // 상태 관리
    - 현재 조합 중인 자소 버퍼
    - 조합 완성 상태 추적
}
```

#### 5. InputManager (InputManager.kt 대체)
```swift
class InputManager {
    // 입력 처리 로직
    - 멀티탭 감지 (300ms 타임아웃)
    - 동시 키 조합 처리
    - 언어별 입력 규칙 적용
    - Shift 상태에 따른 대소문자 변환
    - 특수 키 조합 (W+O, W+A 등)
}
```

## 🎨 UI 구성 요소

### 1. 키 프리뷰 팝업
```swift
class KeyPopupView: UIView {
    // 버블 디자인 구현
    - 그라데이션 배경 (#FFFFFF → #F8F8F8)
    - 12pt 그림자 효과
    - 8dp 모서리 반경
    - 꼬리 부분 45도 회전
    
    // 애니메이션
    - 표시: Scale 0.8→1.0, Alpha 0→1 (100ms)
    - 숨김: Scale 1.0→0.9, Alpha 1→0 (80ms)
}
```

### 2. 제안 바 (Suggestion Bar)
```swift
class SuggestionBar: UIView {
    // 구성 요소
    - 아래 화살표 버튼
    - 위 화살표 버튼
    - 유연한 스페이서
    - "완료" 버튼 (파란색 텍스트)
    
    // 스타일
    - 배경색: #F3F3F8
    - 패딩: 8pt 좌우, 4pt 상하
}
```

## 🎨 디자인 시스템

### 색상 팔레트
```swift
// 기본 색상
let black = UIColor(hex: "#000000")
let white = UIColor(hex: "#FFFFFF")

// 주요 UI 색상
let primaryBlue = UIColor(hex: "#3478F5")  // 버튼 파란색
let textPrimary = UIColor(hex: "#222222")  // 제목 텍스트
let textSecondary = UIColor(hex: "#666666") // 설명 텍스트

// 키보드 색상
let keyWhite = UIColor(hex: "#FFFFFF")
let keyGrey = UIColor(hex: "#AAB0C0")
let keyDarkGrey = UIColor(hex: "#464747")
let bgDarkKeyboard = UIColor(hex: "#323232")
let suggestionBg = UIColor(hex: "#F3F3F8")
let keyboardBg = UIColor(hex: "#D0D5DD")
```

### 타이포그래피
```swift
// 텍스트 크기
let mainKeyText: CGFloat = 18      // 메인 키 텍스트
let subKeyText: CGFloat = 10       // 보조 키 텍스트
let suggestionText: CGFloat = 14   // 제안 텍스트
let titleText: CGFloat = 26        // 제목 텍스트
let bodyText: CGFloat = 16         // 본문 텍스트
```

### 간격 및 크기
```swift
// 키보드 레이아웃
let keySpacing: CGFloat = 6        // 키 간격
let edgeMargin: CGFloat = 2        // 가장자리 여백
let keyCornerRadius: CGFloat = 6   // 키 모서리 반경

// 패딩
let standardPadding: CGFloat = 16
let largePadding: CGFloat = 24
let smallPadding: CGFloat = 8

// 아이콘 크기
let iconSize: CGFloat = 24
```

## 🌏 로컬라이제이션

### 필수 문자열
```swift
// Localizable.strings (ko)
"app_name" = "QWERTY_mini_wide";
"settings" = "설정";
"back" = "뒤로가기";
"keyboard_label" = "키보드";
"how_to_use" = "키보드 사용방법";
"keyboard_preview" = "키보드 미리보기";
"terms_privacy" = "이용약관 및 개인정보 보호";
"share_app" = "앱 공유하기";
"open_system_settings" = "시스템 설정 열기";
"share_message" = "QWERTY_mini_wide 앱을 공유합니다!";
```


## 🔧 핵심 기능 구현 체크리스트

### 필수 기능
- [ ] 한글 자모 조합 엔진
- [ ] 멀티탭 입력 시스템
- [ ] 동시 키 입력 처리
- [ ] Shift 키 상태 관리 (일반/활성/잠금)
- [ ] 언어 전환 (한/영/숫자)
- [ ] 키 프리뷰 팝업
- [ ] 진동 피드백
- [ ] 다크/라이트 테마 자동 전환
- [ ] 음성 입력

### 메인 앱 기능
- [ ] 스플래시 화면 (2초 딜레이)
- [ ] 홈 화면 (설정 접근)
- [ ] 설정 화면
- [ ] 키보드 미리보기
- [ ] 사용 방법 안내
- [ ] 이용약관 화면
- [ ] 앱 공유 기능
- [ ] 시스템 설정 연결

### UI/UX 요소
- [ ] 반응형 레이아웃 (세로/가로)
- [ ] 키 버튼 커스텀 뷰
- [ ] 4개 텍스트 위치 지원
- [ ] 동적 텍스트 크기 조정
- [ ] 키 누름 시각 피드백
- [ ] 제안 바 구현

## 🚀 iOS 특화 구현 사항

### 1. 키보드 확장 제약사항
- 메모리 제한: 키보드 확장은 제한된 메모리 내에서 작동
- 네트워크 제한: RequestsOpenAccess 권한 필요
- 오디오 재생 제한: 시스템 사운드만 가능

### 2. iOS 프레임워크 활용
```swift
// 필수 프레임워크
import UIKit            // UI 구성
import Foundation       // 기본 기능
import AVFoundation     // 오디오/진동
import Speech          // 음성 인식
import CoreHaptics     // 햅틱 피드백
```

### 3. Auto Layout 제약사항
```swift
// 키보드 높이 제약
let portraitHeight: CGFloat = 216
let landscapeHeight: CGFloat = 162

// Safe Area 고려
view.safeAreaLayoutGuide
```

### 4. 제스처 인식기
```swift
// 필요한 제스처
- UITapGestureRecognizer       // 탭
- UILongPressGestureRecognizer // 롱프레스
- UIPanGestureRecognizer       // 팬 (스와이프)
- 동시 제스처 인식 설정 필요
```

## 📝 테스트 체크리스트

### 기능 테스트
- [ ] 한글 입력 정확성
- [ ] 영어 입력 정확성
- [ ] 숫자/특수문자 입력
- [ ] 멀티탭 동작
- [ ] 동시 키 입력
- [ ] 백스페이스 자소 분해
- [ ] 언어 전환
- [ ] Shift 키 동작

### UI 테스트
- [ ] 다크/라이트 모드 전환
- [ ] 세로/가로 모드 전환
- [ ] 키 프리뷰 표시
- [ ] 제안 바 동작
- [ ] 각종 iPhone 모델 호환성

### 성능 테스트
- [ ] 메모리 사용량 최적화
- [ ] 입력 지연 최소화
- [ ] 배터리 소모 최적화

## 🔄 Android → iOS 마이그레이션 매핑

| Android | iOS |
|---------|-----|
| InputMethodService | UIInputViewController |
| LinearLayout | UIStackView |
| ConstraintLayout | Auto Layout |
| RecyclerView | UICollectionView/UITableView |
| SharedPreferences | UserDefaults |
| Vibrator | UIFeedbackGenerator |
| SpeechRecognizer | SFSpeechRecognizer |
| MotionEvent | UITouch/UIGestureRecognizer |
| Toast | UIAlertController/Custom View |
| WindowManager | UIWindow |
| SpannableString | NSAttributedString |
| Handler/Runnable | DispatchQueue/Timer |

## 📱 지원 디바이스 및 OS

### 최소 요구사항
- iOS 13.0 이상
- iPhone 6s 이상
- iPad (선택사항)

### 권장 요구사항
- iOS 15.0 이상
- iPhone 8 이상

## 🛠️ 개발 도구 요구사항
- Xcode 14.0 이상
- Swift 5.7 이상
- CocoaPods 또는 Swift Package Manager

## 📚 참고 자료
- [iOS Keyboard Extension Programming Guide](https://developer.apple.com/documentation/uikit/keyboards_and_input)
- [Human Interface Guidelines - Custom Keyboards](https://developer.apple.com/design/human-interface-guidelines/inputs-and-interactions#Custom-keyboards)
- [Korean Input Method Documentation](https://developer.apple.com/documentation/uikit/uitextinputmode)

## 🎯 개발 우선순위

### Phase 1 - 핵심 기능 (1-2주)
1. 기본 키보드 레이아웃 구현
2. 영어 입력 기능
3. 기본 UI 구성

### Phase 2 - 한글 지원 (2-3주)
1. 한글 자모 조합 엔진
2. 멀티탭 입력

### Phase 3 - 고급 기능 (1-2주)
1. 음성 입력
2. 동시 키 입력
3. 진동 피드백
4. 테마 지원

### Phase 4 - 최적화 및 완성 (1주)
1. 성능 최적화
2. 버그 수정
3. App Store 제출 준비

## ⚠️ 주의사항

1. **메모리 관리**: iOS 키보드 확장은 엄격한 메모리 제한이 있음
2. **권한 처리**: RequestsOpenAccess 설정 시 사용자 동의 필요
3. **앱 그룹**: 메인 앱과 데이터 공유 시 App Groups 설정 필수
4. **키보드 높이**: 시스템 권장 높이 준수 필요
5. **응답성**: 입력 지연 최소화를 위한 최적화 필수

---

*이 문서는 QWERTY Mini Wide Android 앱의 완전한 iOS 포팅을 위한 종합 가이드입니다.*
*마지막 업데이트: 2025년 8월 6일*