package com.qwerty_mini_wide.app.keyboard.model

import android.content.res.Resources
import android.graphics.Color
import com.qwerty_mini_wide.app.R

object KeyLetter {
    // Determine light/dark mode
    var isLightMode: Boolean = false
       // get() = (Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
        set(value) {
            field = value}

    // Dynamic colors
    val subletterColor: Int
        get() = if (isLightMode) Color.parseColor("#808080") else Color.WHITE //Color.parseColor("#C4C4C4")
    val backgroundColor: Int
        get() = if (isLightMode) R.drawable.shape_white_key else R.drawable.shape_darkgrey_key
    val functionBg: Int
        get() = if (isLightMode) R.drawable.shape_grey_key else R.drawable.shape_function_grey_key
    val textColor: Int
        get() = if (isLightMode) Color.BLACK else Color.WHITE

    val shiftSelectBg:Int get() = if (isLightMode) R.drawable.shape_white_key else R.drawable.shape_white_key

    // Screen metrics
    private val displayMetrics = Resources.getSystem().displayMetrics
    private val screenWidthDp: Float
        get() = displayMetrics.widthPixels.toFloat() / displayMetrics.density
    private val screenHeightDp: Float
        get() = displayMetrics.heightPixels.toFloat() / displayMetrics.density

    // Key sizes
    val functionWidth: Float
        get() = if (screenWidthDp < screenHeightDp) 48.58f * screenWidthDp / 393f else 92.5f * screenWidthDp / 852f
    val keyWidth: Float
        get() = if (screenWidthDp < screenHeightDp) 37.67f * screenWidthDp / 393f else 75f * screenWidthDp / 852f
    val keyHeight: Int
        get() = if (screenWidthDp < screenHeightDp) 60 else 46

    val subKorSize = 16
    val largeEngSize = 20
    val smallEngSize = 25

    // Korean letters
    fun getKorLetters(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅂ", ltText = "ㅍ", mainColor = textColor, ltColor = subletterColor, ltTextSize = subKorSize, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 0),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅈ", ltText = "ㅊ", mainColor = textColor, ltColor = subletterColor, ltTextSize = subKorSize, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 1),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㄷ", ltText = "ㅌ", mainColor = textColor, ltColor = subletterColor, ltTextSize = subKorSize, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 2),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㄱ", ltText = "ㅋ", mainColor = textColor, ltColor = subletterColor, ltTextSize = subKorSize, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 3),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅅ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 4),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅗ", ltText = "ㅛ", mainColor = textColor, ltColor = subletterColor, ltTextSize = subKorSize, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 5),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅏ", ltText = "ㅑ", mainColor = textColor, ltColor = subletterColor, ltTextSize = subKorSize, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 6),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅣ", mainColor = textColor, rtColor = subletterColor, backgroundColor = backgroundColor, rtTextSize = subKorSize.toFloat(), height = keyHeight, width = keyWidth, tag = 7)
        ),
        listOf(
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅁ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 9),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㄴ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 10),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅇ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 11),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㄹ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 12),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅎ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 13),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅜ", ltText = "ㅠ", mainColor = textColor, ltColor = subletterColor, ltTextSize = subKorSize, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 14),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅓ", ltText = "ㅕ", mainColor = textColor, ltColor = subletterColor, ltTextSize = subKorSize, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 15),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅡ", rtText = "˙", mainColor = textColor, rtColor = textColor, rtTextSize = 30f, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 16, rtTextMarginTop = -25)
        )
    )

    // Korean function row
    fun getKorFunction(): List<KeyModel> = listOf(
        KeyModel(keyType = KeyType.SHIFT, image = R.drawable.ic_shift, selectImage = R.drawable.ic_shift_on, backgroundColor = functionBg, selectBackgroundColor = shiftSelectBg, height = keyHeight, width = functionWidth, layout_weight = 9f),
        KeyModel(keyType = KeyType.NUMBER, mainText = "123",mainTextSize = 15f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth, layout_weight = 9f),
        KeyModel(keyType = KeyType.SPACE, mainText = "스페이스", mainTextSize = 18f,mainColor = textColor, backgroundColor = backgroundColor),
        KeyModel(keyType = KeyType.RETURN, image = R.drawable.ic_enter, mainTextSize = 15f, selectImage = R.drawable.ic_enter, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth + keyWidth + 6, layout_weight = 8f),
        KeyModel(keyType = KeyType.DELETE, mainText = "⌫", mainTextSize = 20f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth, layout_weight = 9f),
        KeyModel(keyType = KeyType.NONE, mainText = "", mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth + keyWidth + 6, layout_weight = 9f)
    )

    // Korean shift letters
    fun getShiftLetter(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅃ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅉ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㄸ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㄲ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅆ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅙ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅒ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅣ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth)
        ),
        listOf(
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅁ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㄴ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅇ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㄹ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅎ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅞ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅖ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "ㅡ", mainColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth)
        )
    )

    // English function row
    fun getEngFunction(): List<KeyModel> = listOf(
        KeyModel(keyType = KeyType.SHIFT, image = R.drawable.ic_shift, selectImage = R.drawable.ic_shift_on, backgroundColor = functionBg, selectBackgroundColor = shiftSelectBg, height = keyHeight, width = functionWidth, layout_weight = 9f),
        KeyModel(keyType = KeyType.NUMBER, mainText = "123",mainTextSize = 15f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth, layout_weight = 9f),
        KeyModel(keyType = KeyType.SPACE, mainText = "Space",mainTextSize = 18f, mainColor = textColor, backgroundColor = backgroundColor),
        KeyModel(keyType = KeyType.RETURN, image = R.drawable.ic_enter, mainTextSize = 15f, selectImage = R.drawable.ic_enter, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth + keyWidth + 6, layout_weight = 8f),
        KeyModel(keyType = KeyType.DELETE, mainText = "⌫", mainTextSize = 20f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth, layout_weight = 9f),
        KeyModel(keyType = KeyType.NONE, mainText = "", mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth + keyWidth + 6, layout_weight = 9f)
    )


    // 5. English shift letters
    fun getEngLetter(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel(keyType = KeyType.LETTER, ltText = "w", rtText = "˙", rbText = "q", ltTextSize = smallEngSize, rtTextSize = 60f, rbTextSize = smallEngSize, ltColor = textColor, rtColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 0, ltTextMarginLeft = 10, ltTextMarginTop = -10, rbTextMarginRight = 25, rbTextMarginBottom = 15, rtTextMarginRight = 10, rtTextMarginTop = -25),
            KeyModel(keyType = KeyType.LETTER, ltText = "e", ltTextSize = smallEngSize, rbTextSize = smallEngSize,ltColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 1, ltTextMarginLeft = 10, ltTextMarginTop = -10),
        KeyModel(keyType = KeyType.LETTER, ltText = "r", rbText = "f", ltTextSize = smallEngSize, rbTextSize = smallEngSize,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 2, ltTextMarginLeft = 10, ltTextMarginTop = -10, rbTextMarginRight = 25, rbTextMarginBottom = 15),
    KeyModel(keyType = KeyType.LETTER, ltText = "t", rbText = "g",ltTextSize = smallEngSize, rbTextSize = smallEngSize, ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 3, ltTextMarginLeft = 10, ltTextMarginTop = -10, rbTextMarginRight = 25, rbTextMarginBottom = 15),
        KeyModel(keyType = KeyType.LETTER, ltText = "y", rbText = "p",ltTextSize = smallEngSize, rbTextSize = smallEngSize, ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 4, ltTextMarginLeft = 10, ltTextMarginTop = -10, rbTextMarginRight = 0, rbTextMarginBottom = 0),
    KeyModel(keyType = KeyType.LETTER, ltText = "u", ltTextSize = smallEngSize, rbTextSize = smallEngSize,ltColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 5, ltTextMarginLeft = 10, ltTextMarginTop = -10),
    KeyModel(keyType = KeyType.LETTER, ltText = "i", ltTextSize = smallEngSize, rbTextSize = smallEngSize,ltColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 6, ltTextMarginLeft = 10, ltTextMarginTop = -10),
    KeyModel(keyType = KeyType.LETTER, ltText = "o", rtText = "˙",ltTextSize = smallEngSize, rtTextSize = 60f, rbTextSize = smallEngSize, ltColor = textColor, rtColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 7, ltTextMarginLeft = 10, ltTextMarginTop = -10, rtTextMarginRight = 10, rtTextMarginTop = -25)
    ),
    listOf(
    KeyModel(keyType = KeyType.LETTER, ltText = "a", rtText = "˙", ltTextSize = smallEngSize, rtTextSize = 60f, ltColor = textColor, rtColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 9, ltTextMarginLeft = 10, ltTextMarginTop = -10, rtTextMarginRight = 10, rtTextMarginTop = -25),
    KeyModel(keyType = KeyType.LETTER, ltText = "s", rbText = "z",ltTextSize = smallEngSize, rbTextSize = smallEngSize, ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 10, ltTextMarginLeft = 10, ltTextMarginTop = -10, rbTextMarginRight = 0, rbTextMarginBottom = 0),
    KeyModel(keyType = KeyType.LETTER, ltText = "d", rbText = "x",ltTextSize = smallEngSize, rbTextSize = smallEngSize, ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 11, ltTextMarginLeft = 10, ltTextMarginTop = -10, rbTextMarginRight = 0, rbTextMarginBottom = 0),
        KeyModel(keyType = KeyType.LETTER, ltText = "c", rbText = "v", ltTextSize = smallEngSize, rbTextSize = smallEngSize,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 12, ltTextMarginLeft = 10, ltTextMarginTop = -10, rbTextMarginRight = 0, rbTextMarginBottom = 0),
    KeyModel(keyType = KeyType.LETTER, ltText = "h", rbText = "b", ltTextSize = smallEngSize, rbTextSize = smallEngSize,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 13, ltTextMarginLeft = 10, ltTextMarginTop = -10, rbTextMarginRight = 0, rbTextMarginBottom = 0),
    KeyModel(keyType = KeyType.LETTER, ltText = "n", rbText = "j",ltTextSize = smallEngSize, rbTextSize = smallEngSize, ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 14, ltTextMarginLeft = 10, ltTextMarginTop = -10, rbTextMarginRight = 0, rbTextMarginBottom = 0),
    KeyModel(keyType = KeyType.LETTER, ltText = "m", rbText = "k", ltTextSize = smallEngSize, rbTextSize = smallEngSize,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 15, ltTextMarginLeft = 10, ltTextMarginTop = -10, rbTextMarginRight = 25, rbTextMarginBottom = 15),
        KeyModel(keyType = KeyType.LETTER, ltText = "l", rtText = "˙", ltTextSize = smallEngSize, rtTextSize = 60f, ltColor = textColor, rtColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 16, ltTextMarginLeft = 10, ltTextMarginTop = -10, rtTextMarginRight = 10, rtTextMarginTop = -25)
    )
    )


    // 6. English main layout
    fun getEngShiftLetter(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel(KeyType.LETTER, ltText = "W", rtText = "˙", rbText = "Q", rtColor = textColor, ltColor = textColor, ltTextSize = largeEngSize, rtTextSize = 60f, rbTextSize = largeEngSize, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 0, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rbTextGravity = 85, rbTextMarginRight = 15, rbTextMarginBottom = 35, rtTextMarginRight = 10, rtTextMarginTop = -25),
            KeyModel(KeyType.LETTER, ltText = "E", ltColor = textColor, ltTextSize = largeEngSize, rbTextSize = largeEngSize,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 1, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15),
            KeyModel(KeyType.LETTER, ltText = "R", rbText = "F", ltColor = textColor, ltTextSize = largeEngSize, rbTextSize = largeEngSize,rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 2, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rbTextGravity = 85, rbTextMarginRight = 15, rbTextMarginBottom = 35),
            KeyModel(KeyType.LETTER, ltText = "T", rbText = "G", ltColor = textColor, ltTextSize = largeEngSize,rbTextSize = largeEngSize, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 3, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rbTextGravity = 85, rbTextMarginRight = 15, rbTextMarginBottom = 35),
            KeyModel(KeyType.LETTER, ltText = "Y", rbText = "P", ltColor = textColor, ltTextSize =  largeEngSize, rbTextSize = largeEngSize,rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 4, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rbTextGravity = 85, rbTextMarginRight = 15, rbTextMarginBottom = 35),
            KeyModel(KeyType.LETTER, ltText = "U", ltColor = textColor, ltTextSize = largeEngSize, rbTextSize = largeEngSize,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 5, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15),
            KeyModel(KeyType.LETTER, ltText = "I", ltColor = textColor, ltTextSize = largeEngSize, rbTextSize = largeEngSize,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 6, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15),
            KeyModel(KeyType.LETTER, ltText = "O", rtText = "˙", ltColor = textColor, rtColor = textColor, ltTextSize = largeEngSize, rtTextSize = 60f, rbTextSize = largeEngSize,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 7, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rtTextMarginRight = 10, rtTextMarginTop = -25)
        ),
        listOf(
            KeyModel(KeyType.LETTER, ltText = "A", rtText = "˙", ltTextSize = largeEngSize, rtTextSize = 60f, ltColor = textColor, rtColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 9, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rtTextMarginRight = 10, rtTextMarginTop = -25),
            KeyModel(KeyType.LETTER, ltText = "S", rbText = "Z", ltTextSize = largeEngSize,rbTextSize = largeEngSize,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 10, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rbTextGravity = 85, rbTextMarginRight = 15, rbTextMarginBottom = 35),
            KeyModel(KeyType.LETTER, ltText = "D", rbText = "X", ltTextSize = largeEngSize,rbTextSize = largeEngSize,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 11, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rbTextGravity = 85, rbTextMarginRight = 15, rbTextMarginBottom = 35),
            KeyModel(KeyType.LETTER, ltText = "C", rbText = "V", ltTextSize = largeEngSize,rbTextSize = largeEngSize,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 12, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rbTextGravity = 85, rbTextMarginRight = 15, rbTextMarginBottom = 35),
            KeyModel(KeyType.LETTER, ltText = "H", rbText = "B", ltTextSize = largeEngSize,rbTextSize = largeEngSize,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 13, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rbTextGravity = 85, rbTextMarginRight = 15, rbTextMarginBottom = 35),
            KeyModel(KeyType.LETTER, ltText = "N", rbText = "J", ltTextSize = largeEngSize,rbTextSize = largeEngSize,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 14, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rbTextGravity = 85, rbTextMarginRight = 15, rbTextMarginBottom = 35),
            KeyModel(KeyType.LETTER, ltText = "M", rbText = "K", ltTextSize = largeEngSize,rbTextSize = largeEngSize,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 15, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rbTextGravity = 85, rbTextMarginRight = 15, rbTextMarginBottom = 35),
            KeyModel(KeyType.LETTER, ltText = "L", rtText = "˙", ltTextSize = largeEngSize, rtTextSize = 60f, ltColor = textColor, rtColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 16, ltTextGravity = 3, ltTextMarginLeft = 10, ltTextMarginTop = 15, rtTextMarginRight = 10, rtTextMarginTop = -25)
            )
        )

    fun getNumberLetter(language: CurrentLanguage): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel(keyType = KeyType.LETTER, mainText = "1", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "2", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "3", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "4", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "5", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "6", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "7", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "8", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth)
        ),
        listOf(
            KeyModel(keyType = KeyType.LETTER, mainText = "0", mainColor = textColor, mainTextSize = 24f, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = ".", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = ",", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "?", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "!", mainColor = textColor, mainTextSize = 24f,ltColor = textColor, rbColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, ltText = "'", rbText = "\"", ltColor = textColor, rbColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, ltText = "-", rbText = "_", ltColor = textColor, rbColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "9", mainColor = textColor, mainTextSize = 24f,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth)
        )


    )
    fun getNumberFuntion( language: CurrentLanguage): List<KeyModel> = listOf(
        KeyModel(
            keyType = KeyType.SPECIAL, mainText = "#+=",mainTextSize = 15f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg,
            height = keyHeight, width = keyWidth),
        KeyModel(keyType =  if (language == CurrentLanguage.KOR) KeyType.KOR else KeyType.ENG, mainText = if (language == CurrentLanguage.KOR) "ㄱㄴㄷ" else "ABC", mainColor = textColor, mainTextSize = 15f, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = keyWidth, layout_weight = 9f),
        KeyModel(keyType = KeyType.SPACE, mainText = if (language == CurrentLanguage.KOR) "스페이스" else "Space",mainTextSize = 18f, mainColor = textColor, backgroundColor = backgroundColor),
        KeyModel(keyType = KeyType.RETURN, image = R.drawable.ic_enter, mainTextSize = 15f, selectImage = R.drawable.ic_enter, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth + keyWidth + 6, layout_weight = 8f),
        KeyModel(keyType = KeyType.DELETE, mainText = "⌫", mainTextSize = 20f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = keyWidth, layout_weight = 9f),
        KeyModel(keyType = KeyType.NONE, mainText = "", mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth + keyWidth + 6)
    )

    fun getSpecialLetter(language: CurrentLanguage): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel(KeyType.LETTER, ltText = "@", ltColor = textColor, ltTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "#", ltColor = textColor, ltTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "$", rbText = "￦", ltColor = textColor, ltTextSize = 20, rbTextSize = 20, rbColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "%", ltColor = textColor, ltTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "^", ltColor = textColor, ltTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "&", ltColor = textColor, ltTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "*", ltColor = textColor, ltTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "+", rbText = "=", ltColor = textColor, ltTextSize = 20, rbColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth)
        ),
        listOf(
            KeyModel(KeyType.LETTER, ltText = "~", ltColor = textColor,ltTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "/", ltColor = textColor,ltTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = ":", rbText = ";", ltColor = textColor, rbColor = textColor,ltTextSize = 20,rbTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "(", rbText = "<", ltColor = textColor, rbColor = textColor, ltTextSize = 20,rbTextSize = 20,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = ")", rbText = ">", ltColor = textColor, rbColor = textColor,ltTextSize = 20,rbTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "[", rbText = "{", ltColor = textColor, rbColor = textColor,ltTextSize = 20,rbTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "]", rbText = "}", ltColor = textColor, rbColor = textColor,ltTextSize = 20,rbTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(KeyType.LETTER, ltText = "|", rbText = "\\", ltColor = textColor, rbColor = textColor,ltTextSize = 20,rbTextSize = 20, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth)
        )
    )

    fun getSpectialFuntion( language: CurrentLanguage): List<KeyModel> = listOf(
        KeyModel(
            keyType = KeyType.NUMBER, mainText = "123", mainTextSize = 15f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg,
            height = keyHeight, width = keyWidth),
        KeyModel(
            keyType =  if (language == CurrentLanguage.KOR) KeyType.KOR else KeyType.ENG, mainText = if (language == CurrentLanguage.KOR) "ㄱㄴㄷ" else "ABC", mainColor = textColor,
            mainTextSize = 15f, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = keyWidth, layout_weight = 9f),
        KeyModel(keyType = KeyType.SPACE, mainText = if (language == CurrentLanguage.KOR) "스페이스" else "Space",mainTextSize = 18f, mainColor = textColor, backgroundColor = backgroundColor),
        KeyModel(keyType = KeyType.RETURN, image = R.drawable.ic_enter, mainTextSize = 15f, selectImage = R.drawable.ic_enter, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth + keyWidth + 6, layout_weight = 8f),
        KeyModel(keyType = KeyType.DELETE, mainText = "⌫", mainTextSize = 20f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = keyWidth, layout_weight = 9f),
        KeyModel(keyType = KeyType.NONE, mainText = "", mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth + keyWidth + 6)
    )

}