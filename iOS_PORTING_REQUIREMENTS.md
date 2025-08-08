# QWERTY Mini Wide iOS 포팅 요구사항

## 📱 프로젝트 개요
QWERTY Mini Wide는 컴팩트한 QWERTY 레이아웃을 제공하는 영어 키보드 Android 애플리케이션입니다.

### 핵심 특징
- 영어 전용 QWERTY 키보드
- 멀티탭 입력 시스템
- 동시 키 입력 지원
- 음성 입력 기능
- 진동 피드백
- 다크/라이트 테마 자동 전환
- 숫자 및 특수문자 키보드

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

#### 영어 소문자 레이아웃 (기본)
```
[제안 바 - 화살표 버튼, 완료 버튼]
[w(q)•] [e] [r(f)] [t(g)] [y(p)] [u(')]  [i] [o•]
[a•] [s(z)] [d(x)] [c(v)] [h(b)] [n(j)] [m(k)] [l•]
[Shift] [123] [Space] [Enter] [⌫] [빈공간]
```

#### 영어 대문자 레이아웃 (Shift 활성)
```
[제안 바 - 화살표 버튼, 완료 버튼]
[W(Q)•] [E] [R(F)] [T(G)] [Y(P)] [U(')]  [I] [O•]
[A•] [S(Z)] [D(X)] [C(V)] [H(B)] [N(J)] [M(K)] [L•]
[Shift(on)] [123] [Space] [Enter] [⌫] [빈공간]
```

#### 숫자 레이아웃
```
[제안 바]
[1] [2] [3] [4] [5] [6] [7] [8]
[0] [.] [,] [?] [!] ['(") ] [-(_)] [9]
[#+=] [ABC] [Space] [Enter] [⌫] [빈공간]
```

#### 특수문자 레이아웃
```
[제안 바]
[@] [#] [$(£)] [%(¥)] [^] [&(•)] [*] [+(=)]
[~] [.] [:(;)] [(<)] [(>)] [{([)] [}(])] [|(\)]
[123] [ABC] [Space] [Enter] [⌫] [빈공간]
```

#### 특수문자 색상 규칙
- **메인 텍스트 (좌상단)**: #000000 (검은색)
- **보조 텍스트 (우하단 괄호 안)**: 
  - 일반 문자: #8F8F8F (회색)
  - 특수 기호 (£, ¥, •, =, ;, <, >, [, ], \\): #000000 (검은색)

#### 점(Dot) 표시 규칙
- **점 위치**: W, O, A, L 키에만 위치
- **점 크기**: 
  - 세로 모드: 60pt (DOT_SIZE)
  - 가로 모드: 90pt (DOT_SIZE)  
- **점 색상**: 
  - 모든 점(W, O, A, L): #000000 (검은색)
- **점 위치 상세**:
  - W키: 우상단 (rtTextMarginRight: 10, rtTextMarginTop: DOT_MARGIN_TOP)
  - O키: 우상단 (rtTextMarginRight: 10, rtTextMarginTop: DOT_MARGIN_TOP)
  - A키: 우상단 (rtTextMarginRight: 10, rtTextMarginTop: DOT_MARGIN_TOP)
  - L키: 우상단 (rtTextMarginRight: 10, rtTextMarginTop: DOT_MARGIN_TOP)
- **DOT_MARGIN_TOP**: 
  - 세로 모드: 키 높이의 -40%
  - 가로 모드: 키 높이의 -45%

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
    - 키보드 타입 전환 (영어/숫자/특수문자)
}
```

#### 2. CustomKeyboardView (CustomKeyboardView.kt 대체)
```swift
class CustomKeyboardView: UIView {
    // 필수 구현 사항
    - 멀티터치 제스처 인식
    - 키 프리뷰 팝업 표시
    - 멀티탭 입력 처리 (300ms 타이머)
    - Shift 상태 관리 (일반/활성/잠금)
    - 동시 키 입력 조합
    - 대소문자 전환
    
    // Shift 로직 상세
    enum ShiftState {
        case off        // 소문자 모드
        case on         // 대문자 모드 (한 번 입력 후 off로 전환)
        case locked     // Caps Lock (계속 대문자)
    }
    
    // Shift 동작 규칙
    - 1회 탭: off → on (다음 입력만 대문자)
    - 2회 연속 탭: on → locked (Caps Lock)
    - locked 상태에서 탭: locked → off
    - on 상태에서 문자 입력 시: 자동으로 off로 전환
    - 특수 키(숫자, 기호) 입력 시: Shift 상태 유지
}
```

#### 3. CustomKeyButton (CustomKeyButton.kt 대체)
```swift
class CustomKeyButton: UIControl {
    // 4개 텍스트 위치 지원
    var mainText: String      // 중앙 메인 텍스트
    var leftTopText: String   // 좌상단 보조 텍스트 (메인 키)
    var rightTopText: String  // 우상단 점(•) 또는 텍스트
    var rightBottomText: String // 우하단 보조 텍스트 (괄호 안 키)
    
    // 반응형 텍스트 크기 조정
    // 세로 모드 (Portrait)
    - 영어 대문자: 20pt * scaleFactor
    - 영어 소문자: 21pt * scaleFactor  
    - 특수문자: 20pt * scaleFactor
    - 숫자: 24pt * scaleFactor
    - 기능키: 15pt * scaleFactor
    - Space: 18pt * scaleFactor
    - Delete: 20pt * scaleFactor
    - 점(•): 60pt * scaleFactor
    
    // 가로 모드 (Landscape)
    - 영어 대문자: 30pt * scaleFactor
    - 영어 소문자: 31.5pt * scaleFactor
    - 특수문자: 30pt * scaleFactor
    - 숫자: 36pt * scaleFactor
    - 기능키: 22.5pt * scaleFactor
    - Space: 27pt * scaleFactor
    - Delete: 30pt * scaleFactor
    - 점(•): 90pt * scaleFactor
    
    // scaleFactor 계산
    let scaleFactor = min(screenWidth/393, screenHeight/852)
    
    // 백그라운드 상태
    - 일반/누름 상태 처리
    - 반투명 오버레이 (#20000000)
}
```


#### 4. InputManager (InputManager.kt 대체)
```swift
class InputManager {
    // 입력 처리 로직
    - 멀티탭 감지 (300ms 타임아웃)
    - 동시 키 조합 처리
    - Shift 상태에 따른 대소문자 변환
    - 특수 키 조합 (W+O, W+A 등)
    - 키보드 타입별 입력 처리
}
```

## 🎨 UI 구성 요소 및 동작 규칙

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
let keyWhite = UIColor(hex: "#FFFFFF")        // 일반 키 배경
let keyGrey = UIColor(hex: "#B8BFCB")         // 특수키 배경 (shift, 123, enter, del)
let keyDarkGrey = UIColor(hex: "#464747")     // 다크모드 특수키 배경
let keyKeyDarkGrey = UIColor(hex: "#676767")  // 다크모드 일반키 배경
let bgDarkKeyboard = UIColor(hex: "#323232")  // 다크모드 키보드 배경
let suggestionBg = UIColor(hex: "#F3F3F8")    // 제안 바 배경
let keyboardBg = UIColor(hex: "#D0D3D9")      // 키보드 바깥쪽 배경
let searchColor = UIColor(hex: "#007AFF")     // 검색 버튼 색상

// 텍스트 색상
let keyTextPrimary = UIColor(hex: "#000000")    // 키 기본 텍스트 (검은색)
let keyTextSecondary = UIColor(hex: "#8F8F8F") // 키 보조 텍스트 (회색)
```

### 타이포그래피 (반응형)
```swift
// 화면 크기 기반 계산
let screenWidth = UIScreen.main.bounds.width
let screenHeight = UIScreen.main.bounds.height
let isLandscape = screenWidth > screenHeight
let scaleFactor = min(screenWidth/393, screenHeight/852)

// 세로 모드 텍스트 크기
let portraitTextSizes = [
    "largeEng": 20 * scaleFactor,
    "smallEng": 21 * scaleFactor,
    "special": 20 * scaleFactor,
    "number": 24 * scaleFactor,
    "function": 15 * scaleFactor,
    "space": 18 * scaleFactor,
    "delete": 20 * scaleFactor,
    "dot": 60 * scaleFactor
]

// 가로 모드 텍스트 크기
let landscapeTextSizes = [
    "largeEng": 30 * scaleFactor,
    "smallEng": 31.5 * scaleFactor,
    "special": 30 * scaleFactor,
    "number": 36 * scaleFactor,
    "function": 22.5 * scaleFactor,
    "space": 27 * scaleFactor,
    "delete": 30 * scaleFactor,
    "dot": 90 * scaleFactor
]
```

### 간격 및 크기 (반응형)
```swift
// 키보드 레이아웃 - 반응형 크기
// 세로 모드
let portraitKeyWidth = screenWidth * 0.096
let portraitKeyHeight = screenHeight * 0.07
let portraitFunctionWidth = screenWidth * 0.124

// 가로 모드
let landscapeKeyWidth = screenWidth * 0.088
let landscapeKeyHeight = screenHeight * 0.117
let landscapeFunctionWidth = screenWidth * 0.109

// 반응형 마진 (키 너비 기반 백분율)
let margins = [
    "xsmall": keyWidth * 0.08,    // 극소 여백
    "small": keyWidth * 0.13,     // 작은 여백
    "smallRight": keyWidth * 0.20, // 우하단 키용
    "medium": keyWidth * 0.27,    // 보통 여백
    "large": keyWidth * 0.53,     // 큰 여백
    "xlarge": keyWidth * 0.66,    // 더 큰 여백
    "xxlarge": keyWidth * 0.80,   // 아주 큰 여백
    "xxxlarge": keyWidth * 0.93   // 최대 여백
]

// 점 마진 (키 높이 기반)
let dotMarginTop = isLandscape ? 
    -(keyHeight * 0.45) : -(keyHeight * 0.40)

// 기본 설정
let keySpacing: CGFloat = 6        // 키 간격
let edgeMargin: CGFloat = 2        // 가장자리 여백
let keyCornerRadius: CGFloat = 6   // 키 모서리 반경
let iconSize: CGFloat = 24         // 아이콘 크기
```

## 🌏 로컬라이제이션

### 필수 문자열
```swift
// Localizable.strings (ko)
"app_name" = "QWERTY_mini_wide";
"settings" = "설정";
"back" = "뒤로가기";
"keyboard_label" = "Keyboard";
"how_to_use" = "How to Use Keyboard";
"keyboard_preview" = "Keyboard Preview";
"terms_privacy" = "Terms & Privacy";
"share_app" = "Share App";
"open_system_settings" = "Open System Settings";
"share_message" = "Share QWERTY_mini_wide App!";
```


## 🔧 핵심 기능 구현 체크리스트

### 필수 기능
- [ ] 영어 입력 시스템
- [ ] 멀티탭 입력 시스템
- [ ] 동시 키 입력 처리
- [ ] Shift 키 상태 관리 (일반/활성/잠금)
- [ ] 키보드 타입 전환 (영어/숫자/특수문자)
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

### 3. Auto Layout 제약사항 및 반응형 디자인
```swift
// 키보드 높이 제약
let portraitHeight: CGFloat = 216
let landscapeHeight: CGFloat = 162

// Safe Area 고려
view.safeAreaLayoutGuide

// 화면 회전 감지 및 레이아웃 업데이트
override func viewWillTransition(to size: CGSize, 
    with coordinator: UIViewControllerTransitionCoordinator) {
    super.viewWillTransition(to: size, with: coordinator)
    
    coordinator.animate(alongsideTransition: { _ in
        // 가로/세로 모드 전환
        let isLandscape = size.width > size.height
        self.updateKeyboardLayout(isLandscape: isLandscape)
        self.updateTextSizes(isLandscape: isLandscape)
        self.updateKeyDimensions(isLandscape: isLandscape)
    })
}

// 반응형 키보드 업데이트 함수
func updateKeyboardLayout(isLandscape: Bool) {
    // 키 크기 업데이트
    keyWidth = isLandscape ? 
        screenWidth * 0.088 : screenWidth * 0.096
    keyHeight = isLandscape ? 
        screenHeight * 0.117 : screenHeight * 0.07
    functionWidth = isLandscape ? 
        screenWidth * 0.109 : screenWidth * 0.124
    
    // 텍스트 크기 업데이트
    updateAllKeyTextSizes()
    
    // 레이아웃 재구성
    invalidateIntrinsicContentSize()
    setNeedsLayout()
}
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
- [ ] 영어 입력 정확성
- [ ] 대소문자 전환
- [ ] 숫자/특수문자 입력
- [ ] 멀티탭 동작
- [ ] 동시 키 입력
- [ ] 백스페이스 동작
- [ ] 키보드 타입 전환
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

## 🎯 개발 우선순위

### Phase 1 - 핵심 기능 (1-2주)
1. 기본 키보드 레이아웃 구현
2. 영어 입력 기능
3. 기본 UI 구성

### Phase 2 - 고급 입력 기능 (2-3주)
1. 멀티탭 입력 최적화
2. 동시 키 입력 처리

### Phase 3 - 추가 기능 (1-2주)
1. 음성 입력
2. 진동 피드백
3. 테마 지원
4. 특수문자 키보드

### Phase 4 - 최적화 및 완성 (1주)
1. 성능 최적화
2. 버그 수정
3. App Store 제출 준비

## ⚠️ 주의사항 및 특수 케이스 처리

### 일반 주의사항
1. **메모리 관리**: iOS 키보드 확장은 엄격한 메모리 제한이 있음 (약 50MB)
2. **권한 처리**: RequestsOpenAccess 설정 시 사용자 동의 필요
3. **앱 그룹**: 메인 앱과 데이터 공유 시 App Groups 설정 필수
4. **키보드 높이**: 시스템 권장 높이 준수 필요
5. **응답성**: 입력 지연 최소화를 위한 최적화 필수

### 특수 케이스 및 예외 처리

#### 멀티탭 예외 상황
```swift
// 특수키 멀티탭 예외
- Space, Delete, Enter: 멀티탭 미적용
- 숫자키: 멀티탭 미적용
- Shift 후 멀티탭: Shift 상태 유지
- 키보드 전환 후: 멀티탭 타이머 리셋
```

#### 동시 키 입력 예외
```swift
// 3개 이상 키 동시 입력
if activeKeys.count >= 3 {
    // 무시하거나 첫 2개만 처리
    let firstTwo = Array(activeKeys.prefix(2))
    processKeys(firstTwo)
}

// 기능키 포함 동시 입력
if activeKeys.contains("shift") || 
   activeKeys.contains("delete") {
    // 기능키 우선 처리
    processFunctionKey()
    return
}
```

#### Shift 상태에서의 특수 동시 키 입력 규칙
```swift
// Shift가 켜진 상태에서 W/O 키 조합 처리
func handleShiftCombination(first: KeyButton, second: KeyButton) {
    guard isShiftOn() else { return }
    
    let isFirstW = first.leftTopText == "W"
    let isSecondW = second.leftTopText == "W" 
    let isFirstO = first.leftTopText == "O"
    let isSecondO = second.leftTopText == "O"
    
    if (isFirstW || isSecondW || isFirstO || isSecondO) {
        if (isFirstW && isSecondO) || (isFirstO && isSecondW) {
            // W+O 조합: 대문자 W 출력
            insertText("W")
        } else if isFirstW || isSecondW {
            // W + 다른 키 처리
            let wKey = isFirstW ? first : second
            let otherKey = isFirstW ? second : first
            
            // 다른 키가 점(dot)을 가진 키인지 확인 (L, A, O 키)
            let hasDot = otherKey.rightTopText != nil && !otherKey.rightTopText.isEmpty
            
            if hasDot {
                // W + 점키(L, A, O): W의 rbText(Q) 출력
                if let text = wKey.rightBottomText {
                    insertText(text) // 예: W+L → "Q"
                }
            } else {
                // W + 일반키: 다른 키의 ltText 출력
                if let text = otherKey.leftTopText {
                    insertText(text) // 예: W+M → "M"
                }
            }
        } else if isFirstO || isSecondO {
            // O + 다른 키: 다른 키의 rbText(서브키) 또는 ltText 출력
            let otherKey = isFirstO ? second : first
            let outputText = otherKey.rightBottomText ?? otherKey.leftTopText
            if let text = outputText {
                insertText(text)
            }
        }
    }
}

// 동시 입력 판단 기준
let SIMULTANEOUS_THRESHOLD: TimeInterval = 0.1 // 100ms
// 두 키가 100ms 이내에 눌리면 동시 입력으로 처리

// 점(dot)을 가진 키 목록
let dotKeys = ["W", "O", "A", "L"] // 이 키들은 rtText에 점(˙)을 가짐
```

#### 화면 회전 시 처리
```swift
// 회전 중 입력 차단
var isRotating = false

override func viewWillTransition(to size: CGSize, 
    with coordinator: UIViewControllerTransitionCoordinator) {
    isRotating = true
    
    coordinator.animate(alongsideTransition: { _ in
        // 레이아웃 업데이트
    }, completion: { _ in
        self.isRotating = false
    })
}

func handleKeyInput(key: String) {
    guard !isRotating else { return }
    // 정상 처리
}
```

#### 키보드 나타남/사라짐 처리
```swift
// 키보드 나타날 때
override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    // 상태 초기화
    resetAllKeyStates()
    loadUserPreferences()
}

// 키보드 사라질 때
override func viewWillDisappear(_ animated: Bool) {
    super.viewWillDisappear(animated)
    // 타이머 정리
    cancelAllTimers()
    // 진행 중인 애니메이션 정리
    layer.removeAllAnimations()
}
```

#### 메모리 부족 처리
```swift
override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // 캐시 정리
    clearImageCache()
    // 불필요한 뷰 제거
    removeUnusedViews()
}
```

#### 텍스트 필드별 처리
```swift
// 비밀번호 필드
if textDocumentProxy.keyboardType == .asciiCapable {
    // 자동 완성 비활성화
    disableAutocomplete()
}

// 이메일 필드
if textDocumentProxy.keyboardType == .emailAddress {
    // @ 키 강조
    highlightAtKey()
}

// 숫자 필드
if textDocumentProxy.keyboardType == .numberPad {
    // 숫자 키보드로 자동 전환
    switchToNumericKeyboard()
}
```

#### 빠른 연속 입력 처리
```swift
// 입력 큐 관리
var inputQueue: [String] = []
var isProcessing = false

func queueInput(key: String) {
    inputQueue.append(key)
    processQueue()
}

func processQueue() {
    guard !isProcessing, !inputQueue.isEmpty else { return }
    
    isProcessing = true
    let key = inputQueue.removeFirst()
    
    // 실제 입력 처리
    insertText(key)
    
    // 다음 입력까지 최소 대기
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.01) {
        self.isProcessing = false
        self.processQueue()
    }
}
```

#### 접근성 지원
```swift
// VoiceOver 지원
override var accessibilityTraits: UIAccessibilityTraits {
    get { [.keyboardKey] }
    set {}
}

// 각 키별 접근성 레이블
keyButton.accessibilityLabel = "W, 멀티탭으로 Q 입력 가능"
keyButton.accessibilityHint = "두 번 탭하여 Q 입력"
```

---

## 📌 최종 키보드 색상 사양

### 라이트 모드 (기본)
- **키보드 바깥쪽 배경**: #D0D3D9
- **일반 키 배경**: #FFFFFF (흰색)
- **특수키 배경** (Shift, 123, Enter, Delete): #B8BFCB
- **Space 키 배경**: #FFFFFF (흰색)
- **제안 바 배경**: #F3F3F8

### 텍스트 색상
- **메인 텍스트** (키의 주요 문자): #000000 (검은색)
- **보조 텍스트** (괄호 안 문자): 
  - 일반 문자: #8F8F8F (회색)
  - 특수 기호 (£, ¥, •, =, ;, <, >, [, ], \\): #000000 (검은색)
- **가운데 점 (˙)**: #000000 (검은색) - W, O, A, L 키에만 표시
- **특수키 텍스트**: #000000 (검은색)

### 다크 모드
- **키보드 바깥쪽 배경**: #323232
- **일반 키 배경**: #676767
- **특수키 배경**: #464747
- **텍스트 색상**: #FFFFFF (흰색)

---

*이 문서는 QWERTY Mini Wide 영어 키보드 Android 앱의 iOS 포팅을 위한 종합 가이드입니다.*
*마지막 업데이트: 2025년 8월 8일*