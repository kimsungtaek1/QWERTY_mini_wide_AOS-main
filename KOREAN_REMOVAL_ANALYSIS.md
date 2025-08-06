# 한글 기능 제거 시 발생 가능한 문제점 및 해결 방안

## 📋 목차
1. [현재 한글 기능 의존성 분석](#현재-한글-기능-의존성-분석)
2. [한글 제거 시 발생 가능한 에러](#한글-제거-시-발생-가능한-에러)
3. [안전한 한글 기능 제거 방법](#안전한-한글-기능-제거-방법)
4. [영어 전용 키보드 구현 가이드](#영어-전용-키보드-구현-가이드)
5. [리팩토링 체크리스트](#리팩토링-체크리스트)

## 🔍 현재 한글 기능 의존성 분석

### 1. 핵심 한글 관련 파일
| 파일명 | 역할 | 의존도 |
|--------|------|---------|
| `HangulAutomata.kt` | 한글 자모 조합 엔진 | **높음** |
| `CustomKeyboardView.kt` | 한글 입력 처리 로직 포함 | **높음** |
| `InputManager.kt` | 언어별 입력 처리 | **중간** |
| `KeyModel.kt` | CurrentLanguage enum 정의 | **낮음** |

### 2. 한글 기능 코드 위치

#### HangulAutomata 클래스 사용 위치
```kotlin
// CustomKeyboardView.kt
- Line 27: import com.qwerty_mini_wide.app.keyboard.manager.HangulAutomata
- Line 48: var automata = HangulAutomata()
- Line 146: automata = HangulAutomata()
- Line 633: automata = HangulAutomata()
- Line 661: automata.hangulAutomata(ch)
- Line 692: automata.hangulAutomata(ch)
- Line 858: automata.hangulAutomata(split[i])

// CustomKeyBoard_Service.kt
- Line 37: import com.qwerty_mini_wide.app.keyboard.manager.HangulAutomata
- Line 95: binding.customKeyboard.automata = HangulAutomata()
- Line 138: binding.customKeyboard.automata = HangulAutomata()
- Line 235: binding.customKeyboard.automata = HangulAutomata()
```

#### CurrentLanguage enum 사용
```kotlin
// KeyModel.kt
enum class CurrentLanguage {
    KOR, ENG, CHN
}

// CustomKeyboardView.kt
- Line 47: var currentLanguage: CurrentLanguage = CurrentLanguage.ENG
```

## ⚠️ 한글 제거 시 발생 가능한 에러

### 1. 컴파일 타임 에러

#### A. Import 관련 에러
```kotlin
// 에러 발생 위치
CustomKeyboardView.kt:27: Unresolved reference: HangulAutomata
CustomKeyBoard_Service.kt:37: Unresolved reference: HangulAutomata
```
**원인**: HangulAutomata 클래스 삭제 시 import 문 해결 불가

#### B. 타입 참조 에러
```kotlin
// 에러 발생 위치
CustomKeyboardView.kt:48: Unresolved reference: HangulAutomata
CustomKeyboardView.kt:661: Unresolved reference: hangulAutomata
```
**원인**: HangulAutomata 인스턴스 및 메서드 호출 실패

#### C. Enum 값 참조 에러
```kotlin
// CurrentLanguage.KOR 제거 시
CustomKeyboardView.kt: Unresolved reference: KOR
```
**원인**: KOR enum 값이 제거되었지만 코드에서 여전히 참조

### 2. 런타임 에러

#### A. NullPointerException
```kotlin
// 위험 코드
if (currentLanguage == CurrentLanguage.KOR) {
    automata.hangulAutomata(key) // automata가 null일 경우
}
```
**원인**: 한글 모드 진입 시 초기화되지 않은 객체 참조

#### B. IndexOutOfBoundsException
```kotlin
// 한글 키 레이아웃 참조 시
setLetter(KeyLetter.getKorLetters()) // 메서드가 없거나 빈 리스트 반환
```
**원인**: 존재하지 않는 한글 레이아웃 데이터 접근

#### C. IllegalStateException
```kotlin
when (currentLanguage) {
    CurrentLanguage.ENG -> processEnglish()
    CurrentLanguage.KOR -> processKorean() // 메서드 없음
    // else 처리 없음
}
```
**원인**: 처리되지 않은 상태로 인한 예외

### 3. 기능적 문제

#### A. 언어 전환 버그
- 한글 버튼 클릭 시 앱 크래시
- 언어 전환 로직 무한 루프
- 잘못된 언어 상태 표시

#### B. 입력 처리 문제
- 백스페이스 처리 시 한글 관련 로직 호출
- 조합 중인 문자 처리 실패
- 입력 버퍼 관리 오류

## ✅ 안전한 한글 기능 제거 방법

### 1단계: 의존성 분리
```kotlin
// 1. Interface 생성
interface LanguageProcessor {
    fun processInput(key: String): String
    fun deleteLastChar()
    fun reset()
}

// 2. 영어 전용 구현
class EnglishProcessor : LanguageProcessor {
    override fun processInput(key: String): String {
        return key // 단순 반환
    }
    
    override fun deleteLastChar() {
        // 영어 삭제 로직
    }
    
    override fun reset() {
        // 초기화
    }
}
```

### 2단계: 조건부 컴파일
```kotlin
// BuildConfig 활용
object LanguageConfig {
    const val KOREAN_ENABLED = false // 빌드 설정으로 제어
}

// 사용 예
if (LanguageConfig.KOREAN_ENABLED) {
    // 한글 관련 코드
}
```

### 3단계: 점진적 제거
```kotlin
// 1. Deprecated 마킹
@Deprecated("Korean support will be removed")
class HangulAutomata { ... }

// 2. 빈 구현으로 대체
class HangulAutomata {
    fun hangulAutomata(key: String) {
        // 아무 동작 없음
    }
}

// 3. 최종 제거
// HangulAutomata 클래스 삭제
```

## 🔧 영어 전용 키보드 구현 가이드

### 1. 필수 수정 파일

#### A. CustomKeyboardView.kt
```kotlin
class CustomKeyboardView : LinearLayout {
    // 제거
    // var automata = HangulAutomata()
    // var currentLanguage: CurrentLanguage = CurrentLanguage.ENG
    
    // 추가
    private var isShiftPressed = false
    private var isCapsLock = false
    
    // 수정된 입력 처리
    private fun processKeyInput(key: String) {
        val output = when {
            isCapsLock || isShiftPressed -> key.uppercase()
            else -> key.lowercase()
        }
        listener?.onKey(KeyType.LETTER, output)
    }
    
    // 백스페이스 처리 단순화
    private fun handleBackspace() {
        listener?.onDelete()
    }
}
```

#### B. KeyModel.kt
```kotlin
// 수정된 enum
enum class CurrentLanguage {
    ENG  // KOR, CHN 제거
}

// 또는 완전히 제거하고 boolean 사용
class CustomKeyboardView {
    private var isNumberMode = false
    private var isSpecialMode = false
}
```

#### C. CustomKeyBoard_Service.kt
```kotlin
class CustomKeyBoard_Service : InputMethodService() {
    override fun onCreate() {
        super.onCreate()
        // HangulAutomata 초기화 제거
        // binding.customKeyboard.automata = HangulAutomata() // 삭제
    }
    
    // 언어 전환 메서드 제거 또는 수정
    private fun switchLanguage() {
        // 영어/숫자/특수문자만 전환
        when (currentMode) {
            Mode.ALPHABET -> currentMode = Mode.NUMBER
            Mode.NUMBER -> currentMode = Mode.SPECIAL
            Mode.SPECIAL -> currentMode = Mode.ALPHABET
        }
    }
}
```

### 2. 키 레이아웃 수정

#### A. 언어 전환 버튼 제거/변경
```kotlin
// KeyLetter.kt
fun getEngFunction(): List<KeyModel> = listOf(
    createFunctionKey(KeyType.SHIFT, ...),
    createFunctionKey(KeyType.NUMBER, mainText = "123"), // 한글 버튼 대신
    createFunctionKey(KeyType.SPACE, ...),
    // KeyType.KOR 관련 버튼 제거
)
```

#### B. 멀티탭 로직 제거
```kotlin
// CustomKeyboardView.kt
// 한글 조합을 위한 멀티탭 로직 제거
private fun handleKeyPress(key: String) {
    // 단순 입력 처리만
    processKeyInput(key)
    
    // 제거할 코드:
    // if (isMultiTap) { ... }
    // automata.hangulAutomata(key)
}
```

### 3. 메모리 및 성능 최적화

#### A. 불필요한 객체 제거
```kotlin
// 제거할 객체들
- HangulAutomata 인스턴스
- 한글 조합 버퍼
- 한글 상태 변수
- 한글 테이블 데이터 (초성, 중성, 종성)
```

#### B. 이벤트 리스너 단순화
```kotlin
interface KeyboardListener {
    fun onKey(type: KeyType, value: String)
    fun onDelete()
    fun onEnter()
    // 한글 관련 콜백 제거
}
```

## 📝 리팩토링 체크리스트

### 필수 작업
- [ ] HangulAutomata.kt 파일 삭제
- [ ] HangulAutomata import 문 제거
- [ ] automata 변수 선언 제거
- [ ] hangulAutomata() 메서드 호출 제거
- [ ] CurrentLanguage.KOR enum 값 제거
- [ ] 한글 관련 조건문 제거/수정
- [ ] 한글 키 레이아웃 데이터 제거
- [ ] 한글 관련 리소스 (strings, drawables) 제거

### 코드 수정 위치
- [ ] `CustomKeyboardView.kt` - 한글 입력 로직 제거
- [ ] `CustomKeyBoard_Service.kt` - HangulAutomata 초기화 제거
- [ ] `InputManager.kt` - 한글 처리 로직 제거
- [ ] `KeyModel.kt` - CurrentLanguage enum 수정
- [ ] `KeyLetter.kt` - 한글 레이아웃 함수 제거 (있을 경우)

### 테스트 항목
- [ ] 영어 입력 정상 동작
- [ ] 대소문자 전환 동작
- [ ] 숫자/특수문자 입력
- [ ] 백스페이스 동작
- [ ] 언어 전환 버튼 동작 (숫자/특수문자)
- [ ] 앱 크래시 없음
- [ ] 메모리 누수 없음

### 선택적 개선사항
- [ ] 영어 자동 완성 기능 추가
- [ ] 영어 맞춤법 검사 추가
- [ ] 이모지 키보드 추가
- [ ] 제스처 입력 지원
- [ ] 키보드 테마 기능 강화

## 🚀 단계별 구현 계획

### Phase 1: 준비 (1일)
1. 현재 코드 백업
2. 브랜치 생성 (`feature/remove-korean`)
3. 테스트 환경 구성

### Phase 2: 코드 정리 (2-3일)
1. HangulAutomata 관련 코드 주석 처리
2. 컴파일 에러 수정
3. 런타임 에러 수정
4. 기본 동작 테스트

### Phase 3: 리팩토링 (2-3일)
1. 불필요한 코드 완전 제거
2. 영어 전용 로직 최적화
3. UI/UX 개선
4. 성능 최적화

### Phase 4: 테스트 및 안정화 (2일)
1. 단위 테스트 작성
2. 통합 테스트
3. 사용자 테스트
4. 버그 수정

### Phase 5: 배포 준비 (1일)
1. 코드 리뷰
2. 문서 업데이트
3. 빌드 최적화
4. 릴리즈 노트 작성

## 💡 추가 고려사항

### 1. 사용자 마이그레이션
- 기존 한글 사용자를 위한 안내
- 대체 한글 키보드 추천
- 설정 마이그레이션

### 2. 앱 크기 최적화
- 한글 폰트 제거로 약 2-3MB 절감 예상
- 한글 처리 라이브러리 제거로 추가 절감

### 3. 유지보수성 향상
- 코드 복잡도 감소
- 테스트 커버리지 향상
- 빌드 시간 단축

### 4. 향후 확장성
- 다른 언어 지원 고려
- 플러그인 아키텍처 도입
- 모듈화 설계

## 📌 중요 경고사항

1. **백업 필수**: 모든 변경 전 전체 프로젝트 백업
2. **점진적 변경**: 한 번에 모든 것을 삭제하지 말 것
3. **테스트 우선**: 각 단계마다 충분한 테스트
4. **롤백 계획**: 문제 발생 시 즉시 롤백 가능하도록 준비
5. **사용자 피드백**: 베타 테스트를 통한 사용자 의견 수렴

---

*이 문서는 QWERTY Mini Wide 키보드에서 한글 기능을 안전하게 제거하기 위한 종합 가이드입니다.*
*마지막 업데이트: 2025년 8월 6일*