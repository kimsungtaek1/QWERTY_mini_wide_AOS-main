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
            KeyType.KOR -> handleKorTap(keyButton)
            KeyType.ENG, KeyType.NUMBER, KeyType.SPECIAL -> handleOtherTap(keyButton, currentState, isShiftOn)
            else -> ""
        }
    }


    private fun handleKorTap(keyButton: CustomKeyButton): String {
        if(getThirdTapKey(keyButton) != null){
            return when (tapCount % 3) {
                1 -> keyButton.keyModel?.mainText.orEmpty()
                2 ->
                    (if(keyButton.keyModel?.ltText == ""){
                        if(keyButton.keyModel?.mainText == "ㅅ"){
                            return "ㅆ"
                        } else {
                            return ""
                        }
                    }else{
                        return keyButton.keyModel?.ltText ?: ""
                    }).toString()



                0 -> getThirdTapKey(keyButton)
                    ?: keyButton.keyModel?.mainText.orEmpty()
                else -> ""
            }


        }else{
            return when (tapCount % 2) {
                1 -> keyButton.keyModel?.mainText.orEmpty()
                0 -> (if(keyButton.keyModel?.ltText == ""){
                    if(keyButton.keyModel?.mainText == "ㅅ"){
                        return "ㅆ"
                    } else {
                        return ""
                    }
                }else{
                    return keyButton.keyModel?.ltText ?: ""
                }).toString()
                else -> ""
            }
        }



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
                    val text = keyButton.keyModel?.rbText
                        .takeIf { it?.isNotEmpty() ?: false }
                        ?: keyButton.keyModel?.ltText.orEmpty()
                    if (isShiftOn) text.uppercase() else text
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

    private fun getThirdTapKey(keyButton: CustomKeyButton): String? {
        return when (keyButton.keyModel?.mainText) {
            "ㅂ" -> "ㅃ"
            "ㅈ" -> "ㅉ"
            "ㄷ" -> "ㄸ"
            "ㄱ" -> "ㄲ"
            else -> null
        }
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
            // wq 키 찾기
            val wqKey = keyViews.find { it.keyModel?.ltText == "w" && it.keyModel?.rbText == "q" }
            // o 키 찾기
            val oKey = keyViews.find { it.keyModel?.ltText == "o" }
            // a 키 찾기
            val aKey = keyViews.find { it.keyModel?.ltText == "a" }
            // l 키 찾기
            val lKey = keyViews.find { it.keyModel?.ltText == "l" }
            
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
            
            // 다른 통합키 찾기 (wq가 아닌 다른 통합키)
            val otherDualKey = keyViews.find { 
                !it.keyModel?.rbText.isNullOrEmpty() && 
                !(it.keyModel?.ltText == "w" && it.keyModel?.rbText == "q")
            }
            
            // wq 또는 o와 다른 통합키의 조합
            if ((wqKey != null || oKey != null) && otherDualKey != null) {
                android.util.Log.d("InputManager", "WQ/O combo with dual key: ${otherDualKey.keyModel?.ltText}")
                return otherDualKey.keyModel?.ltText
            }
            
            // a 또는 l과 다른 통합키의 조합
            if ((aKey != null || lKey != null) && otherDualKey != null) {
                android.util.Log.d("InputManager", "A/L combo with dual key: ${otherDualKey.keyModel?.rbText}")
                return otherDualKey.keyModel?.rbText
            }
            
            // 점이 있는 키와의 조합
            if (isDotKey(keyViews)) {
                val dotKey = keyViews.find { it.keyModel?.rtText == "˙" }
                android.util.Log.d("InputManager", "ENG Dot key combo - dotKey: ${dotKey?.keyModel?.ltText}, rbText: ${dotKey?.keyModel?.rbText}")
                
                // 점이 있는 키의 rbText 반환
                dotKey?.keyModel?.rbText?.let { 
                    if (it.isNotEmpty()) return it 
                }
            }
            
            // 두 키 모두 rbText를 가지고 있는 경우 처리
            val key0 = keyViews[0]
            val key1 = keyViews[1]
            
            // 첫 번째 키가 rbText를 가지고 있는 경우
            if (!key0.keyModel?.rbText.isNullOrEmpty()) {
                android.util.Log.d("InputManager", "Key0 has rbText: ${key0.keyModel?.ltText} -> ${key0.keyModel?.rbText}")
                return key0.keyModel?.rbText
            }
            
            // 두 번째 키가 rbText를 가지고 있는 경우
            if (!key1.keyModel?.rbText.isNullOrEmpty()) {
                android.util.Log.d("InputManager", "Key1 has rbText: ${key1.keyModel?.ltText} -> ${key1.keyModel?.rbText}")
                return key1.keyModel?.rbText
            }
        }
        
        val text0 = keyViews[0].keyModel?.mainText.orEmpty()
        val text1 = keyViews[1].keyModel?.mainText.orEmpty()
        if (isNumberOrSymbol(text0) && isNumberOrSymbol(text1)) {
            return text0 + text1
        }
        return when (currLanguage) {
            CurrentLanguage.KOR -> {
                // Korean keyboard removed - return English handling
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

