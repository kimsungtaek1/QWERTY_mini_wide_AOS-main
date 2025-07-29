package com.qwerty_mini_wide.app.keyboard.manager

// Required imports (if any)
// No specific imports needed for standard library types used here

// Enum translation
enum class HangulStatus {
    START, //s0
    CHOSUNG, //s1
    JOONGSUNG, DJOONGSUNG, //s2,s3
    JONGSUNG, DJONGSUNG, //s4, s5
    ENDONE, ENDTWO //s6,s7
}

//입력된 키의 종류 판별 정의
enum class HangulCHKind {
    CONSONANT, //자음
    VOWEL  //모음
}

//키 입력마다 쌓이는 입력 스택 정의
data class InpStack(
    var curhanst: HangulStatus, //상태
    var key: Int, //방금 입력된 키 코드 (UInt32 translated to Int)
    var charCode: String, //조합된 코드
    var chKind: HangulCHKind // 입력된 키가 자음인지 모임인지
)

class HangulAutomata {

    var buffer: MutableList<String> = mutableListOf()

    var inpStack: MutableList<InpStack> = mutableListOf()

    var currentHangulState: HangulStatus? = null

    private var chKind = HangulCHKind.VOWEL

    private var charCode: String = ""
    private var oldKey: Int = 0 // UInt32 translated to Int
    private var oldChKind: HangulCHKind? = null
    private var keyCode: Int = 0 // UInt32 translated to Int

    private val chosungTable: List<String> = listOf("ㄱ","ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ")

    private val joongsungTable: List<String> = listOf("ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅘ", "ㅙ", "ㅚ", "ㅛ", "ㅜ", "ㅝ", "ㅞ", "ㅟ", "ㅠ", "ㅡ", "ㅢ", "ㅣ")

    private val jongsungTable: List<String> = listOf(" ", "ㄱ", "ㄲ", "ㄳ", "ㄴ", "ㄵ", "ㄶ", "ㄷ", "ㄹ", "ㄺ", "ㄻ", "ㄼ", "ㄽ", "ㄾ", "ㄿ","ㅀ", "ㅁ", "ㅂ", "ㅄ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ")

    private val dJoongTable: List<List<String>> = listOf(
        listOf("ㅗ","ㅏ","ㅘ"),
        listOf("ㅗ","ㅐ","ㅙ"),
        listOf("ㅗ","ㅣ","ㅚ"),
        listOf("ㅜ","ㅓ","ㅝ"),
        listOf("ㅜ","ㅔ","ㅞ"),
        listOf("ㅜ","ㅣ","ㅟ"),
        listOf("ㅡ","ㅣ","ㅢ"),
        listOf("ㅏ","ㅣ","ㅐ"),
        listOf("ㅓ","ㅣ","ㅔ"),
        listOf("ㅕ","ㅣ","ㅖ"),
        listOf("ㅑ","ㅣ","ㅒ"),
        listOf("ㅘ","ㅣ","ㅙ"),
        listOf("ㅝ","ㅣ","ㅞ"),
    )

    private val dJongTable: List<List<String>> = listOf(
        listOf("ㄱ","ㅅ","ㄳ"),
        listOf("ㄴ","ㅈ","ㄵ"),
        listOf("ㄴ","ㅎ","ㄶ"),
        listOf("ㄹ","ㄱ","ㄺ"),
        listOf("ㄹ","ㅁ","ㄻ"),
        listOf("ㄹ","ㅂ","ㄼ"),
        listOf("ㄹ","ㅅ","ㄽ"),
        listOf("ㄹ","ㅌ","ㄾ"),
        listOf("ㄹ","ㅍ","ㄿ"),
        listOf("ㄹ","ㅎ","ㅀ"),
        listOf("ㅂ","ㅅ","ㅄ")
    )

    private fun joongsungPair(): Boolean {
        for (i in 0 until dJoongTable.size) {
            if (dJoongTable[i][0] == joongsungTable[oldKey] && dJoongTable[i][1] == joongsungTable[keyCode]) {
                // Translate firstIndex(of: ...) ?? 0
                keyCode = joongsungTable.indexOf(dJoongTable[i][2]).let { if (it == -1) 0 else it }
                return true
            }
        }
        return false
    }

    private fun jongsungPair(): Boolean {
        for (i in 0 until dJongTable.size) {
            if (dJongTable[i][0] == jongsungTable[oldKey] && dJongTable[i][1] == chosungTable[keyCode]) {
                // Translate firstIndex(of: ...) ?? 0
                keyCode = jongsungTable.indexOf(dJongTable[i][2]).let { if (it == -1) 0 else it }
                return true
            }
        }
        return false
    }

    private fun isJoongSungPair(first: String, result: String): Boolean {
        for (i in 0 until dJoongTable.size) {
            if (dJoongTable[i][0] == first && dJoongTable[i][2] == result) {
                return true
            }
        }
        return false
    }

    // Translate UInt32 to Int for charCode parameter and return type
    private fun decompositionChosung(charCode: Int): Int {
        val unicodeHangul = charCode - 0xAC00
        val jongsung = (unicodeHangul) % 28
        val joongsung = ((unicodeHangul - jongsung) / 28) % 21
        val chosung = (((unicodeHangul - jongsung) / 28) - joongsung) / 21
        return chosung
    }

    // Translate UInt32 to Int for charCode parameter and return type
    private fun decompositionChosungJoongsung(charCode: Int): Int {
        val unicodeHangul = charCode - 0xAC00
        val jongsung = (unicodeHangul) % 28
        val joongsung = ((unicodeHangul - jongsung) / 28) % 21
        val chosung = (((unicodeHangul - jongsung) / 28) - joongsung) / 21
        return combinationHangul(chosung = chosung, joongsung = joongsung, jongsung = keyCode)
    }

    // Translate UInt32 to Int for parameters and return type
    private fun combinationHangul(chosung: Int = 0, joongsung: Int, jongsung: Int = 0): Int {
        return (((chosung * 21) + joongsung) * 28) + jongsung + 0xAC00
    }


    fun deleteBuffer() {
        if (inpStack.size == 0) {
            if (buffer.size > 0) {
                buffer.removeAt(buffer.size - 1)
            }
        } else {
            // Swift's popLast() returns Optional, removeLast() throws if empty.
            // Since we checked size > 0, removeLast() is safe here.
            val popHanguel = inpStack.removeAt(inpStack.lastIndex)

            if (popHanguel.curhanst == HangulStatus.CHOSUNG) {
                buffer.removeAt(buffer.lastIndex)
            } else if (popHanguel.curhanst == HangulStatus.JOONGSUNG || popHanguel.curhanst == HangulStatus.DJOONGSUNG) {
                // Check if inpStack is not empty before accessing last()
                if (inpStack.size > 0) {
                    if (inpStack.last().curhanst == HangulStatus.JONGSUNG || inpStack.last().curhanst == HangulStatus.DJONGSUNG) {
                        buffer.removeAt(buffer.lastIndex)
                    }
                    // This line is outside the inner if in Swift, applies to both cases
                    buffer[buffer.size - 1] = inpStack.last().charCode
                } else {
                    // This case seems unlikely based on the logic, but handle defensively
                    // The original Swift would likely crash here with index out of bounds.
                    // Removing the last buffer element seems like a plausible fallback.
                    if (buffer.size > 0) {
                        buffer.removeAt(buffer.lastIndex)
                    }
                }
            } else { // jongsung, dJongsung, endOne, endTwo
                if (inpStack.isEmpty()) {
                    buffer.removeAt(buffer.lastIndex)
                } else { // inpStack is not empty
                    if (popHanguel.chKind == HangulCHKind.VOWEL) {
                        if (inpStack.last().curhanst == HangulStatus.JONGSUNG) {
                            // This condition seems odd in Swift, jongsung state implies consonant input
                            if (inpStack.last().chKind == HangulCHKind.VOWEL) {
                                if (isJoongSungPair(first = joongsungTable[inpStack.last().key] , result = joongsungTable[popHanguel.key])) {
                                    buffer[buffer.size - 1] = inpStack.last().charCode
                                } else {
                                    buffer.removeAt(buffer.lastIndex)
                                }
                            }
                            // Swift code does not have an explicit else for inpStack.last().chKind != .vowel
                            // If the inner 'if' fails, the code continues after this block.
                        } else { // inpStack.last().curhanst is not .jongsung
                            buffer.removeAt(buffer.lastIndex)
                        }
                    } else { // popHanguel.chKind == .consonant
                        buffer[buffer.size - 1] = inpStack.last().charCode
                    }
                }
            }
            // State and key updates after the main buffer logic
            if (inpStack.isEmpty()) {
                currentHangulState = null
            } else {
                currentHangulState = inpStack.last().curhanst
                oldKey = inpStack.last().key
                oldChKind = inpStack.last().chKind
                charCode = inpStack.last().charCode
            }
        }
    }

    // Swift extension methods are placed directly in the class body in Kotlin
    fun hangulAutomata(key: String) {

        var canBeJongsung: Boolean = false
        if (joongsungTable.contains(key)) {
            chKind = HangulCHKind.VOWEL
            // Translate UInt32(joongsungTable.firstIndex(of: key) ?? 0)
            keyCode = joongsungTable.indexOf(key).let { if (it == -1) 0 else it }
        } else {
            chKind = HangulCHKind.CONSONANT
            // Translate UInt32(chosungTable.firstIndex(of: key) ?? 0)
            keyCode = chosungTable.indexOf(key).let { if (it == -1) 0 else it }
            if (!((key == "ㄸ") || (key == "ㅉ") || (key == "ㅃ"))) {
                canBeJongsung = true
            }
        }
        if(key == "ㄸ" || key == "ㅉ" || key == "ㅃ"){
            if(currentHangulState == HangulStatus.JOONGSUNG){
                buffer.add("")
                charCode = key
                currentHangulState = HangulStatus.CHOSUNG
                chKind = HangulCHKind.CONSONANT
                keyCode = chosungTable.indexOf(key)
                inpStack.add(InpStack(currentHangulState!!,keyCode,charCode,chKind))
                buffer[buffer.count() - 1] = charCode
                return
            }
        }




        if (currentHangulState != null) {
            // Accessing last element, need size check (though logic implies stack is not empty here)
            if (inpStack.size > 0) {
                oldKey = inpStack.last().key
                oldChKind = inpStack.last().chKind
            }
        } else {
            currentHangulState = HangulStatus.START
            buffer.add("")
        }

        //MARK: - 오토마타 전이 알고리즘
        when (currentHangulState) {
            HangulStatus.START -> {
                if (chKind == HangulCHKind.CONSONANT) {
                    currentHangulState = HangulStatus.CHOSUNG
                } else {
                    currentHangulState =
                        HangulStatus.JONGSUNG // This transition seems unusual (vowel -> jongsung from start) but translating exactly
                }
            }
            HangulStatus.CHOSUNG -> {
                if (chKind == HangulCHKind.VOWEL) {
                    currentHangulState = HangulStatus.JOONGSUNG
                } else {
                    currentHangulState = HangulStatus.ENDONE
                }
            }
            HangulStatus.JOONGSUNG -> {
                if (canBeJongsung) {
                    currentHangulState = HangulStatus.JONGSUNG
                } else if (joongsungPair()) {
                    currentHangulState = HangulStatus.DJOONGSUNG
                } else {
                    currentHangulState = HangulStatus.ENDONE
                }
            }
            HangulStatus.DJOONGSUNG -> {
                //추가
                if (joongsungPair()) {
                    currentHangulState = HangulStatus.DJOONGSUNG
                } else if (canBeJongsung) {
                    currentHangulState = HangulStatus.JONGSUNG
                } else {
                    currentHangulState = HangulStatus.ENDONE
                }
            }
            HangulStatus.JONGSUNG -> {
                if ((chKind == HangulCHKind.CONSONANT) && jongsungPair()) {
                    currentHangulState = HangulStatus.DJONGSUNG
                } else if (chKind == HangulCHKind.VOWEL) {
                    currentHangulState = HangulStatus.ENDTWO
                } else {
                    currentHangulState = HangulStatus.ENDONE
                }
            }
            HangulStatus.DJONGSUNG -> {
                if (chKind == HangulCHKind.VOWEL) {
                    currentHangulState = HangulStatus.ENDTWO
                } else {
                    currentHangulState = HangulStatus.ENDONE
                }
            }
            else -> { // Swift's default: break covers nil case
                // In Kotlin, when is exhaustive if not nullable, or needs else.
                // Since currentHangulState is nullable, this else handles the null case.
            }
        }
        //MARK: - 오토마타 상태 별 작업 알고리즘

        when (currentHangulState) {
            HangulStatus.CHOSUNG -> {
                charCode = chosungTable[keyCode]
            }
            HangulStatus.JOONGSUNG -> {
                // Translate String(Unicode.Scalar(combinationHangul(...)) ?? Unicode.Scalar(0))
                // Assuming Unicode.Scalar(0) is fallback for invalid scalar, which maps to U+0000
                charCode = combinationHangul(chosung = oldKey, joongsung = keyCode).toChar().toString()
            }
            HangulStatus.DJOONGSUNG -> {
                // Translate decompositionChosung(charCode: Unicode.Scalar(charCode)?.value ?? 0)
                // Assuming Unicode.Scalar(charCode)?.value ?? 0 means charCode.firstOrNull()?.code ?: 0
                val currentChosung = decompositionChosung(charCode.firstOrNull()?.code ?: 0)
                // Translate String(Unicode.Scalar(combinationHangul(...)) ?? Unicode.Scalar(0))
                charCode = combinationHangul(chosung = currentChosung, joongsung = keyCode).toChar().toString()
            }
            HangulStatus.JONGSUNG -> {
                if (canBeJongsung) {
                    // Translate UInt32(jongsungTable.firstIndex(of: key) ?? 0)
                    keyCode = jongsungTable.indexOf(key).let { if (it == -1) 0 else it }
                    // Translate Unicode.Scalar(charCode)?.value ?? 0
                    val currentCharCode =  charCode.firstOrNull()?.code ?: 0
                    // Translate String(Unicode.Scalar(decompositionChosungJoongsung(charCode: currentCharCode)) ?? Unicode.Scalar(0))
                    charCode = decompositionChosungJoongsung(charCode = currentCharCode).toChar().toString()
                } else {
                    charCode = key
                }
            }
            HangulStatus.DJONGSUNG -> {
                // Translate Unicode.Scalar(charCode)?.value ?? 0
                val currentCharCode = charCode.firstOrNull()?.code ?: 0
                // Translate String(Unicode.Scalar(decompositionChosungJoongsung(charCode: currentCharCode)) ?? Unicode.Scalar(0))
                charCode = decompositionChosungJoongsung(charCode = currentCharCode).toChar().toString()
                // Translate UInt32(jongsungTable.firstIndex(of: key) ?? 0)
                keyCode = jongsungTable.indexOf(key).let { if (it == -1) 0 else it }
            }
            HangulStatus.ENDONE -> {
                if (chKind == HangulCHKind.CONSONANT) {
                    charCode = chosungTable[keyCode]
                    currentHangulState = HangulStatus.CHOSUNG
                } else {
                    charCode = joongsungTable[keyCode]
                    currentHangulState =
                        HangulStatus.JONGSUNG // This transition seems unusual (vowel -> jongsung from endOne) but translating exactly
                }
                buffer.add("")
            }
            HangulStatus.ENDTWO -> {
                if (oldChKind == HangulCHKind.CONSONANT) {
                    // Translate UInt32(chosungTable.firstIndex(of: jongsungTable[Int(oldKey)]) ?? 0)
                    oldKey = chosungTable.indexOf(jongsungTable[oldKey]).let { if (it == -1) 0 else it }
                    // Translate String(Unicode.Scalar(combinationHangul(chosung: oldKey, joongsung: keyCode)) ?? Unicode.Scalar(0))
                    charCode = combinationHangul(chosung = oldKey, joongsung = keyCode).toChar().toString()
                    currentHangulState = HangulStatus.JOONGSUNG
                    // Accessing element at index count - 2, need size check
                    if (inpStack.size >= 2) {
                        buffer[buffer.size - 1] = inpStack[inpStack.size - 2].charCode
                    } else {
                        // This case seems unlikely based on the logic, but handle defensively
                        // The original Swift would crash here with index out of bounds.
                        // Leaving the buffer element unchanged seems like a plausible fallback
                        // if the required stack history isn't available.
                    }
                    buffer.add("")
                } else { // oldChKind == .vowel
                    // This check seems odd here, joongsungPair uses oldKey and keyCode from *current* input, not previous state
                    if (!joongsungPair()) {
                        buffer.add("")
                    }
                    charCode = joongsungTable[keyCode]
                    currentHangulState = null // Swift sets to nil, then immediately to .jongsung
                    currentHangulState = HangulStatus.JONGSUNG
                }
            }
            else -> {
                // Handles null state, no action needed based on Swift's default: break
            }
        }
        // Translate String(Unicode.Scalar(charCode) ?? Unicode.Scalar(0))
        // Assuming Unicode.Scalar(charCode) where charCode is String means charCode.firstOrNull()?.code
        // And String(scalar ?? Unicode.Scalar(0)) means converting the scalar to String, or "\u0000" if scalar is nil (which shouldn't happen after ?? 0)
        // This simplifies to taking the first char's code, converting to Char, then String, or "\u0000" if original string was empty.
        val stackCharCode = charCode.firstOrNull()?.code?.toChar()?.toString() ?: "\u0000"
        inpStack.add(InpStack(curhanst = currentHangulState ?: HangulStatus.START, key = keyCode, charCode = stackCharCode, chKind = chKind))
        buffer[buffer.size - 1] = charCode
    }
}