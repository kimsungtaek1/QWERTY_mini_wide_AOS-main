package com.qwerty_mini_wide.app.keyboard.manager

import android.os.SystemClock
import com.qwerty_mini_wide.app.keyboard.CustomKeyButton
import com.qwerty_mini_wide.app.keyboard.model.CurrentLanguage
import com.qwerty_mini_wide.app.keyboard.model.KeyType

class InputManager private constructor() {
    companion object {
        val shared: InputManager by lazy { InputManager() }
    }

    // 연속탭 상태
    private var lastKeyButton: CustomKeyButton? = null
    var tapCount = 0
    private var lastTapTime: Long = 0
    private val multiTapInterval = 300L // ms

    // 동시탭 상태
    // private val simFirstKeys = setOf(KeyType.WQ, KeyType.OK) // 예시
    // private val simSecondKeys = setOf(KeyType.A, KeyType.L)

    /**
     * 단일 또는 연속탭 처리:
     * 누른 키와 시간 기준으로 tapCount 증가 → 적절한 문자 반환
     */
    fun handleTap(keyButton: CustomKeyButton, currentState: KeyType, isShiftOn: Boolean = false): String {
        val now = SystemClock.elapsedRealtime()

        if (lastKeyButton?.keyModel?.tag == keyButton.keyModel?.tag
            && now - lastTapTime < multiTapInterval) {

            tapCount = if (keyButton.isDoubleLetter()) tapCount + 1 else 1
        } else {
            lastKeyButton = keyButton
            tapCount = 1
        }
        lastTapTime = now

        return when (currentState) {
            KeyType.KOR -> "" // Korean support removed
            KeyType.ENG, KeyType.NUMBER, KeyType.SPECIAL -> handleOtherTap(keyButton, currentState, isShiftOn)
            else -> ""
        }
    }


    @Deprecated("Korean support has been removed")
    private fun handleKorTap(keyButton: CustomKeyButton): String {
        // Korean handling removed - return empty string
        return ""
    }

    private fun handleOtherTap(keyButton: CustomKeyButton, state: KeyType, isShiftOn: Boolean = false): String {
        return when (tapCount % 2) {
            1 -> when (state) {
                KeyType.ENG, KeyType.SPECIAL -> {
                    val text = keyButton.keyModel?.ltText.orEmpty()
                    if (isShiftOn && state == KeyType.ENG) text.uppercase() else text
                }
                KeyType.NUMBER -> keyButton.keyModel?.mainText
                    .takeIf { it?.isNotEmpty() ?: false }
                    ?: keyButton.keyModel?.ltText.orEmpty()
                else -> keyButton.keyModel?.mainText.orEmpty()
            }
            0 -> when (state) {
                KeyType.ENG -> {
                    val baseText = keyButton.keyModel?.rbText
                        .takeIf { it?.isNotEmpty() ?: false }
                        ?: keyButton.keyModel?.ltText.orEmpty()
                    if (isShiftOn) baseText.uppercase() else baseText
                }
                KeyType.SPECIAL -> {
                    val text = keyButton.keyModel?.rbText
                        .takeIf { it?.isNotEmpty() ?: false }
                        ?: keyButton.keyModel?.ltText.orEmpty()
                    text
                }
                KeyType.NUMBER -> keyButton.keyModel?.rbText
                    .takeIf { it?.isNotEmpty() ?: false }
                    ?: keyButton.keyModel?.mainText.orEmpty()
                else -> keyButton.keyModel?.ltText.orEmpty()
            }
            else -> ""
        }
    }

    /**
     * 기능성 키 여부 판별 함수
     */
    private fun isFunctionalKey(keyType: KeyType?): Boolean {
        return when (keyType) {
            KeyType.DELETE, KeyType.ENG, KeyType.KOR, KeyType.CHN,
            KeyType.SPACE, KeyType.RETURN, KeyType.LOCKSHIFT, KeyType.ONSHIFT, KeyType.EMPTY, KeyType.NONE -> true
            else -> false
        }
    }

    /**
     * 숫자 또는 특수문자인지 텍스트로 판별
     */
    private fun isNumberOrSymbol(text: String): Boolean {
        // 숫자 또는 키보드에 표시된 모든 특수문자 (정규식 안전하게 작성)
        return text.matches(
            Regex("[0-9!@#\\$%\\^&*()_=+\\[\\]{};:'\",.<>/?|`~₩/:;<>-]")
        )
    }

    /**
     * 동시탭 처리: 두 개의 CustomKeyButton 중 Sim 키가 어떤 역할인지 보고 primary/secondary 결정
     */
    fun handleSimultaneous(keyButtons: List<CustomKeyButton>, currLanguage: CurrentLanguage): String? {
        if (keyButtons.size != 2) return null
        // 기능성 키가 하나라도 포함되어 있으면 동시탭 무효
        if (isFunctionalKey(keyButtons[0].keyModel?.keyType) || isFunctionalKey(keyButtons[1].keyModel?.keyType)) {
            return null
        }
        return getSimultaneousKey(keyButtons, currLanguage)
    }

    @Deprecated("Korean support has been removed")
    private fun getThirdTapKey(keyButton: CustomKeyButton): String? {
        // Korean handling removed
        return null
    }


    /**
     * 현재 연속 탭이 진행 중인지 확인
     */
    fun isMultiTapInProgress(): Boolean {
        val now = SystemClock.elapsedRealtime()
        return lastKeyButton != null && now - lastTapTime < multiTapInterval
    }

    fun getSimultaneousKey(
        keyViews: List<CustomKeyButton>,
        currLanguage: CurrentLanguage
    ): String? {
        if (isFunctionalKey(keyViews[0].keyModel?.keyType) || isFunctionalKey(keyViews[1].keyModel?.keyType)) {
            return null
        }
        
        // 영어 모드에서 특수 조합 체크
        if (currLanguage == CurrentLanguage.ENG) {
            // 디버그: 입력된 키 확인
            android.util.Log.d("InputManager", "=== Simultaneous Key Debug ===")
            android.util.Log.d("InputManager", "Key1: ltText=${keyViews[0].keyModel?.ltText}, rbText=${keyViews[0].keyModel?.rbText}")
            android.util.Log.d("InputManager", "Key2: ltText=${keyViews[1].keyModel?.ltText}, rbText=${keyViews[1].keyModel?.rbText}")
            
            // wq 키 찾기 (소문자 또는 대문자)
            val wqKey = keyViews.find { 
                (it.keyModel?.ltText == "w" && it.keyModel?.rbText == "q") ||
                (it.keyModel?.ltText == "W" && it.keyModel?.rbText == "Q")
            }
            // o 키 찾기 (소문자 또는 대문자)
            val oKey = keyViews.find { it.keyModel?.ltText == "o" || it.keyModel?.ltText == "O" }
            // a 키 찾기 (소문자 또는 대문자)
            val aKey = keyViews.find { it.keyModel?.ltText == "a" || it.keyModel?.ltText == "A" }
            // l 키 찾기 (소문자 또는 대문자)
            val lKey = keyViews.find { it.keyModel?.ltText == "l" || it.keyModel?.ltText == "L" }
            
            android.util.Log.d("InputManager", "Found keys - wqKey: ${wqKey != null}, oKey: ${oKey != null}, aKey: ${aKey != null}, lKey: ${lKey != null}")
            
            // wq + l = q (대소문자 구분)
            if (wqKey != null && lKey != null) {
                val isUpperCase = wqKey.keyModel?.ltText == "W"
                val result = if (isUpperCase) "Q" else "q"
                android.util.Log.d("InputManager", "WQ + L combo: returning $result")
                return result
            }
            
            // wq + o = w
            if (wqKey != null && oKey != null) {
                android.util.Log.d("InputManager", "WQ + O combo: returning w")
                return "w"
            }
            
            // wq + a = w
            if (wqKey != null && aKey != null) {
                android.util.Log.d("InputManager", "WQ + A combo: returning w")
                return "w"
            }
            
            // wq + e = e (e 키 찾기)
            val eKey = keyViews.find { it.keyModel?.ltText == "e" || it.keyModel?.ltText == "E" }
            if (wqKey != null && eKey != null) {
                val isUpperCase = wqKey.keyModel?.ltText == "W"
                val result = if (isUpperCase) "E" else "e"
                android.util.Log.d("InputManager", "WQ + E combo: returning $result")
                return result
            }
            
            // wq + i = i (i 키 찾기)
            val iKey = keyViews.find { it.keyModel?.ltText == "i" || it.keyModel?.ltText == "I" }
            if (wqKey != null && iKey != null) {
                val isUpperCase = wqKey.keyModel?.ltText == "W"
                val result = if (isUpperCase) "I" else "i"
                android.util.Log.d("InputManager", "WQ + I combo: returning $result")
                return result
            }
            
            // o + l = o (대소문자 유지)
            if (oKey != null && lKey != null) {
                val result = oKey.keyModel?.ltText?.lowercase() ?: "o"
                android.util.Log.d("InputManager", "O + L combo: returning $result")
                return result
            }
            
            // a + l = a (대소문자 유지)
            if (aKey != null && lKey != null) {
                val result = aKey.keyModel?.ltText?.lowercase() ?: "a"
                android.util.Log.d("InputManager", "A + L combo: returning $result")
                return result
            }
            
            // ===== 1열 점 키 규칙 먼저 처리 (W/Q, O) =====
            // O와 다른 키의 조합 (1열 점 키 규칙: ltText 반환)
            if (oKey != null && lKey == null && wqKey == null && aKey == null) {  // 특수 조합은 제외
                val otherKey = keyViews.find { it != oKey }
                if (otherKey != null) {
                    // O키는 1열 점 키이므로 상대 키의 좌상단 텍스트 반환
                    val isUpperCase = oKey.keyModel?.ltText == "O"
                    val result = if (isUpperCase) {
                        otherKey.keyModel?.ltText?.uppercase() ?: otherKey.keyModel?.ltText
                    } else {
                        otherKey.keyModel?.ltText
                    }
                    android.util.Log.d("InputManager", "O combo (Row 1 dot key): otherKey=${otherKey.keyModel?.ltText}, returning ltText=$result (uppercase=$isUpperCase)")
                    return result
                }
            }
            
            // W/Q와 다른 키의 조합 (특수 조합 제외)
            if (wqKey != null && lKey == null && oKey == null && aKey == null && eKey == null && iKey == null) {
                val otherKey = keyViews.find { it != wqKey }
                if (otherKey != null) {
                    val isUpperCase = wqKey.keyModel?.ltText == "W"
                    val result = if (isUpperCase) {
                        otherKey.keyModel?.ltText?.uppercase() ?: otherKey.keyModel?.ltText
                    } else {
                        otherKey.keyModel?.ltText
                    }
                    android.util.Log.d("InputManager", "WQ combo (Row 1 dot key): returning ltText=$result (uppercase=$isUpperCase)")
                    return result
                }
            }
            
            // ===== 2열 점 키 규칙 나중에 처리 (A, L) =====
            // A와 다른 키의 조합 (2열 점 키 규칙: rbText 반환)
            if (aKey != null && lKey == null && wqKey == null && oKey == null) {  // 특수 조합 제외
                val otherKey = keyViews.find { it != aKey }
                if (otherKey != null && !otherKey.keyModel?.rbText.isNullOrEmpty()) {
                    // A키는 2열 점 키이므로 상대 키의 우하단 텍스트 반환
                    val isUpperCase = aKey.keyModel?.ltText == "A"
                    val result = if (isUpperCase) {
                        otherKey.keyModel?.rbText?.uppercase() ?: otherKey.keyModel?.rbText
                    } else {
                        otherKey.keyModel?.rbText
                    }
                    android.util.Log.d("InputManager", "A combo (Row 2 dot key): returning rbText=$result (uppercase=$isUpperCase)")
                    return result
                }
            }
            
            // L과 다른 키의 조합 (2열 점 키 규칙: rbText 반환) - 특수 조합 이후에 처리
            if (lKey != null && aKey == null && oKey == null && wqKey == null) {
                val otherKey = keyViews.find { it != lKey }
                if (otherKey != null && !otherKey.keyModel?.rbText.isNullOrEmpty()) {
                    // L키는 2열 점 키이므로 상대 키의 우하단 텍스트 반환
                    val isUpperCase = lKey.keyModel?.ltText == "L"
                    val result = if (isUpperCase) {
                        otherKey.keyModel?.rbText?.uppercase() ?: otherKey.keyModel?.rbText
                    } else {
                        otherKey.keyModel?.rbText
                    }
                    android.util.Log.d("InputManager", "L combo (Row 2 dot key): returning rbText=$result (uppercase=$isUpperCase)")
                    return result
                }
            }
            
            // 다른 통합키 찾기 (wq가 아닌 다른 통합키)
            val otherDualKey = keyViews.find { 
                !it.keyModel?.rbText.isNullOrEmpty() && 
                !((it.keyModel?.ltText == "w" && it.keyModel?.rbText == "q") ||
                  (it.keyModel?.ltText == "W" && it.keyModel?.rbText == "Q"))
            }
            
            // wq와 다른 통합키의 조합
            if (wqKey != null && otherDualKey != null) {
                android.util.Log.d("InputManager", "WQ combo with dual key: ${otherDualKey.keyModel?.ltText}")
                return otherDualKey.keyModel?.ltText
            }
            
            // 기타 통합키 조합 처리 (점 키 규칙이 적용되지 않는 경우)
            // 두 키 모두 rbText를 가지고 있고, 점 키가 아닌 경우
            val key0 = keyViews[0]
            val key1 = keyViews[1]
            
            // 점 키가 포함되지 않은 경우에만 rbText 처리
            if (oKey == null && aKey == null && lKey == null && wqKey == null) {
                // 첫 번째 키가 rbText를 가지고 있는 경우
                if (!key0.keyModel?.rbText.isNullOrEmpty()) {
                    android.util.Log.d("InputManager", "Non-dot key combo: Key0 has rbText: ${key0.keyModel?.ltText} -> ${key0.keyModel?.rbText}")
                    return key0.keyModel?.rbText
                }
                
                // 두 번째 키가 rbText를 가지고 있는 경우
                if (!key1.keyModel?.rbText.isNullOrEmpty()) {
                    android.util.Log.d("InputManager", "Non-dot key combo: Key1 has rbText: ${key1.keyModel?.ltText} -> ${key1.keyModel?.rbText}")
                    return key1.keyModel?.rbText
                }
            }
        }
        
        val text0 = keyViews[0].keyModel?.mainText.orEmpty()
        val text1 = keyViews[1].keyModel?.mainText.orEmpty()
        if (isNumberOrSymbol(text0) && isNumberOrSymbol(text1)) {
            return text0 + text1
        }
        return when (currLanguage) {
            CurrentLanguage.KOR -> {
                // Korean support removed - fallback to English
                keyViews[0].keyModel?.ltText.orEmpty()
            }

            CurrentLanguage.ENG -> {
                // 점이 있는 키 조합은 위에서 이미 처리되므로 여기서는 기본값만 반환
                keyViews[0].keyModel?.ltText.orEmpty()
            }
            
            CurrentLanguage.CHN -> {
                // CHN 언어 처리 (현재는 기본값 반환)
                keyViews[1].keyModel?.ltText.orEmpty()
            }
        }
    }

    // --- 헬퍼 함수 예시 구현 ---
    private fun isKorKeyView(keyViews: List<CustomKeyButton>, target: String): Boolean =
        keyViews[0].keyModel?.mainText == target

    private fun isKorShiftView(keyViews: List<CustomKeyButton>, type: KeyType): Boolean =
        keyViews.any { kv -> kv.keyModel?.keyType == type }

    private fun isEngKeyView(keyViews: List<CustomKeyButton>, target: String): Boolean =
         keyViews[0].keyModel?.ltText == target
    
    private fun isDotKey(keyViews: List<CustomKeyButton>): Boolean =
        keyViews.any { it.keyModel?.rtText == "˙" }

    private fun isKorShiftCombo(keyButtons: List<CustomKeyButton>, first: String, second: String): Boolean {
        val hasFirst = keyButtons.any { it.keyModel?.mainText == first }
        val hasSecond = keyButtons.any { it.keyModel?.mainText == second }
        return hasFirst && hasSecond
    }

    private fun isEngCombo(keyButtons: List<CustomKeyButton>, letters: List<String>): Boolean {
        return keyButtons.any { it.keyModel?.ltText in letters }
    }
}

