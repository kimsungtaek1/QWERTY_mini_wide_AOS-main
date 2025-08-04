# QWERTY Mini Wide 영어 키보드 로직 문서

## 1. 키보드 레이아웃

### 1.1 기본 레이아웃 (소문자)
```
첫번째 줄: w/q  e  r/f  t/g  y/p  u  i  o
두번째 줄: a    s/z  d/x  c/v  h/b  n/j  m/k  l
```

- 일부 키는 연타(multi-tap) 기능 지원
  - 예: w 키를 한 번 누르면 'w', 연타하면 'q'
  - 점(˙)이 표시된 키는 동시입력 시 특수문자 출력

### 1.2 Shift 레이아웃 (대문자)
- 모든 문자가 대문자로 변환
- 연타 시에도 대문자 유지 (W → Q)

## 2. 주요 기능

### 2.1 연타(Multi-tap) 처리
- **타임아웃**: 300ms
- **동작 방식**:
  1. 첫 번째 탭: ltText 출력 (왼쪽 위 문자)
  2. 두 번째 탭: rbText 출력 (오른쪽 아래 문자)
  3. 300ms 이내에 다시 누르지 않으면 리셋

### 2.2 Shift 키 처리
- **단일 탭**: ONSHIFT 상태 (일시적 대문자)
- **더블 탭**: LOCKSHIFT 상태 (대문자 고정)
- **자동 리셋**: 
  - 문자 입력 후 350ms 후 자동으로 shift 해제
  - 연타 중에는 shift 상태 유지

### 2.3 동시입력 처리
- 두 키를 100ms 이내에 동시에 누르면 특수 처리
- 점(˙)이 있는 키와의 조합은 해당 키의 rbText 출력

## 3. 핵심 코드 구조

### 3.1 InputManager.kt
```kotlin
class InputManager {
    // 연타 상태 관리
    private var lastKeyButton: CustomKeyButton? = null
    var tapCount = 0
    private var lastTapTime: Long = 0
    private val multiTapInterval = 300L // ms

    fun handleTap(keyButton: CustomKeyButton, currentState: KeyType, isShiftOn: Boolean): String {
        // 연타 카운트 업데이트
        val now = SystemClock.elapsedRealtime()
        if (lastKeyButton?.keyModel?.tag == keyButton.keyModel?.tag 
            && now - lastTapTime < multiTapInterval) {
            tapCount = if (keyButton.isDoubleLetter()) tapCount + 1 else 1
        } else {
            lastKeyButton = keyButton
            tapCount = 1
        }
        lastTapTime = now

        // 영어 모드 처리
        return when (currentState) {
            KeyType.ENG -> handleOtherTap(keyButton, currentState, isShiftOn)
            // ...
        }
    }

    private fun handleOtherTap(keyButton: CustomKeyButton, state: KeyType, isShiftOn: Boolean): String {
        return when (tapCount % 2) {
            1 -> // 첫 번째 탭: ltText
            0 -> // 두 번째 탭: rbText (있으면), 없으면 ltText
        }
    }
}
```

### 3.2 CustomKeyboardView.kt
```kotlin
class CustomKeyboardView {
    // Shift 자동 리셋을 위한 핸들러
    private val shiftResetHandler = Handler(Looper.getMainLooper())
    private val shiftResetRunnable = Runnable {
        if (currentState == KeyType.ENG) {
            resetShift()
        }
    }

    fun tapLetter(keyButton: CustomKeyButton) {
        val ch = InputManager.shared.handleTap(keyButton, currentState, isShiftOn())
        
        // 문자 출력 처리
        if (InputManager.shared.tapCount == 1) {
            // 첫 번째 탭
            listener?.onKey(KeyType.LETTER, ch)
        } else {
            // 연타: 이전 문자 삭제 후 새 문자 입력
            listener?.onKey(KeyType.DELETE, "")
            listener?.onKey(KeyType.LETTER, ch)
        }
        
        // Shift 자동 리셋 스케줄링 (350ms 후)
        if (currentState == KeyType.ENG) {
            shiftResetHandler.removeCallbacks(shiftResetRunnable)
            shiftResetHandler.postDelayed(shiftResetRunnable, 350)
        }
    }
}
```

## 4. 주요 버그 수정 내역

### 4.1 Shift 연타 버그 (2025-07-30)
**문제**: Shift를 누른 후 WQ 키를 연타하면 소문자 q가 출력됨
**원인**: 
1. 첫 번째 문자 입력 후 즉시 shift가 리셋됨
2. 연타 시 rbText 처리에서 shift 상태가 제대로 적용되지 않음

**해결**:
1. 연타가 끝날 때까지 shift 상태 유지
2. 350ms 타이머로 shift 자동 리셋
3. handleOtherTap에서 영어 모드일 때 항상 shift 상태 확인

## 5. 키 데이터 구조

### KeyModel
```kotlin
data class KeyModel(
    val keyType: KeyType,
    val ltText: String = "",      // 왼쪽 위 (기본 문자)
    val rtText: String = "",      // 오른쪽 위 (특수 표시)
    val rbText: String = "",      // 오른쪽 아래 (연타 문자)
    val mainText: String = "",    // 중앙 (한글 등)
    // ... 기타 스타일 속성들
)
```

### 영어 키 정의 예시
```kotlin
KeyModel(
    keyType = KeyType.LETTER, 
    ltText = "w",           // 첫 번째 탭
    rtText = "˙",          // 동시입력 표시
    rbText = "q",          // 두 번째 탭
    // ...
)
```

## 6. 이벤트 처리 흐름

1. **키 터치** → setupKeys()의 OnClickListener
2. **문자 키 판별** → keyType == KeyType.LETTER
3. **연타 처리** → InputManager.handleTap()
4. **Shift 적용** → isShiftOn() 확인 및 대문자 변환
5. **문자 출력** → listener?.onKey()
6. **Shift 리셋** → 350ms 후 자동 리셋

## 7. 특수 기능

### 7.1 동시입력 (Simultaneous Input)
- 100ms 이내 두 키 동시 입력 감지
- 특수 문자 조합 출력
- 점(˙) 표시가 있는 키와의 조합 우선 처리

### 7.2 더블 레터 (Double Letter)
- 특정 키는 2개 이상의 문자 순환
- 한글 모드와 영어 모드에서 다르게 동작

## 8. 주의사항

1. **Shift 상태 관리**: 연타 중에는 shift를 리셋하지 않도록 주의
2. **타이머 정리**: Handler 사용 시 메모리 누수 방지를 위해 적절히 정리
3. **이벤트 순서**: 터치 이벤트와 클릭 이벤트의 처리 순서 고려
4. **상태 동기화**: InputManager와 CustomKeyboardView 간 상태 동기화 유지