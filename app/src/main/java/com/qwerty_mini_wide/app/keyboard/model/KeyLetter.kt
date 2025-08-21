package com.qwerty_mini_wide.app.keyboard.model

import android.content.res.Resources
import android.graphics.Color
import com.qwerty_mini_wide.app.R

object KeyLetter {
    // Screen metrics
    private val displayMetrics = Resources.getSystem().displayMetrics
    private val screenWidthDp: Float
        get() = displayMetrics.widthPixels.toFloat() / displayMetrics.density
    private val screenHeightDp: Float
        get() = displayMetrics.heightPixels.toFloat() / displayMetrics.density
    private val scaleFactor: Float
        get() = minOf(screenWidthDp / 393f, screenHeightDp / 852f) // Base on common phone size
    
    // Responsive text sizes (scale with screen size)
    private val LARGE_ENG_SIZE: Int
        get() = ((if (screenWidthDp > screenHeightDp) 30 else 20) * scaleFactor).toInt()
    private val SMALL_ENG_SIZE: Int
        get() = ((if (screenWidthDp > screenHeightDp) 31.5f else 21f) * scaleFactor).toInt()
    private val SPECIAL_TEXT_SIZE: Int
        get() = ((if (screenWidthDp > screenHeightDp) 30 else 20) * scaleFactor).toInt()
    private val NUMBER_TEXT_SIZE: Float
        get() = (if (screenWidthDp > screenHeightDp) 36f else 24f) * scaleFactor
    private val FUNCTION_TEXT_SIZE: Float
        get() = (if (screenWidthDp > screenHeightDp) 22.5f else 15f) * scaleFactor
    private val SPACE_TEXT_SIZE: Float
        get() = (if (screenWidthDp > screenHeightDp) 27f else 18f) * scaleFactor
    private val DELETE_TEXT_SIZE: Float
        get() = (if (screenWidthDp > screenHeightDp) 30f else 20f) * scaleFactor
    private val DOT_SIZE: Float
        get() = (if (screenWidthDp > screenHeightDp) 60f else 45f) * scaleFactor
    
    // 반응형 마진 값 (키 너비의 백분율로 계산) - 5의 배수
    private val MARGIN_5: Int
        get() = (keyWidth * 0.05f).toInt()
    private val MARGIN_10: Int
        get() = (keyWidth * 0.10f).toInt()
    private val MARGIN_15: Int
        get() = (keyWidth * 0.15f).toInt()
    private val MARGIN_20: Int
        get() = (keyWidth * 0.20f).toInt()
    private val MARGIN_25: Int
        get() = (keyWidth * 0.25f).toInt()
    private val MARGIN_30: Int
        get() = (keyWidth * 0.30f).toInt()
    private val MARGIN_35: Int
        get() = (keyWidth * 0.35f).toInt()
    private val MARGIN_40: Int
        get() = (keyWidth * 0.40f).toInt()
    private val MARGIN_45: Int
        get() = (keyWidth * 0.45f).toInt()
    private val MARGIN_50: Int
        get() = (keyWidth * 0.50f).toInt()
    private val MARGIN_55: Int
        get() = (keyWidth * 0.55f).toInt()
    private val MARGIN_60: Int
        get() = (keyWidth * 0.60f).toInt()
    private val MARGIN_65: Int
        get() = (keyWidth * 0.65f).toInt()
    private val MARGIN_70: Int
        get() = (keyWidth * 0.70f).toInt()
    private val MARGIN_75: Int
        get() = (keyWidth * 0.75f).toInt()
    private val MARGIN_80: Int
        get() = (keyWidth * 0.80f).toInt()
    private val MARGIN_85: Int
        get() = (keyWidth * 0.85f).toInt()
    private val MARGIN_90: Int
        get() = (keyWidth * 0.90f).toInt()
    private val MARGIN_95: Int
        get() = (keyWidth * 0.95f).toInt()
    
    // 기존 코드 호환성을 위한 별칭
    private val MARGIN_XSMALL: Int get() = MARGIN_10
    private val MARGIN_SMALL: Int get() = MARGIN_15  
    private val MARGIN_SMALL_RIGHT: Int get() = MARGIN_20
    private val MARGIN_MEDIUM: Int get() = MARGIN_30
    private val MARGIN_LARGE: Int get() = MARGIN_50
    private val MARGIN_XLARGE: Int get() = MARGIN_65
    private val MARGIN_XXLARGE: Int get() = MARGIN_80
    private val MARGIN_XXXLARGE: Int get() = MARGIN_95
    
    // 반응형 점키 상단 마진 (키 높이의 백분율로 계산)
    // 1열 점키용 (위로 올림)
    private val DOT_MARGIN_TOP_ROW1: Int
        get() = -(keyHeight * 0.10f).toInt()
    
    // 2열 점키용 (아래로 내림) 
    private val DOT_MARGIN_TOP_ROW2: Int
        get() = (keyHeight * 1.2f).toInt()
    
    // Gravity constants
    private const val GRAVITY_LEFT = 3
    private const val GRAVITY_RIGHT = 5
    private const val GRAVITY_CENTER = 17
    private const val GRAVITY_BOTTOM = 48
    private const val GRAVITY_BOTTOM_80 = 80
    private const val GRAVITY_BOTTOM_RIGHT = 85
    private const val GRAVITY_BOTTOM_LEFT = 83
    private const val GRAVITY_BOTTOM_CENTER = 50
    
    // Determine light/dark mode
    var isLightMode: Boolean = true
        set(value) {
            field = value
        }

    // Dynamic colors
    val subletterColor: Int
        get() = if (isLightMode) Color.parseColor("#8F8F8F") else Color.WHITE //Color.parseColor("#C4C4C4")
    val backgroundColor: Int
        get() = if (isLightMode) R.drawable.shape_white_key else R.drawable.shape_darkgrey_key
    val functionBg: Int
        get() = if (isLightMode) R.drawable.shape_grey_key else R.drawable.shape_function_grey_key
    val textColor: Int
        get() = if (isLightMode) Color.parseColor("#000000") else Color.WHITE

    val shiftSelectBg:Int get() = if (isLightMode) R.drawable.shape_white_key else R.drawable.shape_white_key

    // Key sizes (responsive based on screen width)
    val functionWidth: Float
        get() = screenWidthDp * (if (screenWidthDp < screenHeightDp) 0.124f else 0.109f)
    val keyWidth: Float
        get() = screenWidthDp * (if (screenWidthDp < screenHeightDp) 0.096f else 0.088f)
    val keyHeight: Int
        get() = if (screenWidthDp < screenHeightDp) 60 else 42  // 세로 60dp, 가로 42dp

    // Helper function to create a letter key
    private fun createLetterKey(
        ltText: String = "",
        rtText: String = "",
        rbText: String = "",
        ltTextSize: Int = SMALL_ENG_SIZE,
        rtTextSize: Float = DOT_SIZE,
        rbTextSize: Int = SMALL_ENG_SIZE,
        ltGravity: Int = GRAVITY_LEFT,
        ltMarginLeft: Int = MARGIN_80,
        ltMarginTop: Int = -5,
        rtGravity: Int = GRAVITY_RIGHT,
        rtMarginRight: Int = MARGIN_30,
        rtMarginTop: Int = -25,
        rbGravity: Int = GRAVITY_BOTTOM_RIGHT,
        rbMarginRight: Int = MARGIN_65,
        rbMarginBottom: Int = MARGIN_30,
        tag: Int = 0
    ): KeyModel = KeyModel(
        keyType = KeyType.LETTER,
        ltText = ltText,
        rtText = rtText,
        rbText = rbText,
        ltTextSize = ltTextSize,
        rtTextSize = rtTextSize,
        rbTextSize = rbTextSize,
        ltColor = textColor,
        rtColor = if (rtText.isNotEmpty()) textColor else 0,
        rbColor = if (rbText.isNotEmpty()) subletterColor else 0,
        backgroundColor = backgroundColor,
        height = keyHeight,
        width = keyWidth,
        tag = tag,
        ltTextGravity = ltGravity,
        ltTextMarginLeft = ltMarginLeft,
        ltTextMarginTop = ltMarginTop,
        rtTextGravity = rtGravity,
        rtTextMarginRight = rtMarginRight,
        rtTextMarginTop = rtMarginTop,
        rbTextGravity = rbGravity,
        rbTextMarginRight = rbMarginRight,
        rbTextMarginBottom = rbMarginBottom
    )
    
    // Helper function to create a function key
    private fun createFunctionKey(
        keyType: KeyType,
        mainText: String = "",
        image: Int = 0,
        selectImage: Int = 0,
        width: Float = functionWidth,
        layoutWeight: Float = 9f
    ): KeyModel = KeyModel(
        keyType = keyType,
        mainText = mainText,
        mainTextSize = if (mainText.isNotEmpty()) FUNCTION_TEXT_SIZE else 0f,
        mainColor = Color.parseColor("#000000"),
        image = image,
        selectImage = selectImage,
        backgroundColor = if (keyType == KeyType.SPACE) backgroundColor else functionBg,
        selectBackgroundColor = if (keyType == KeyType.SHIFT) shiftSelectBg else functionBg,
        height = keyHeight,
        width = width,
        layout_weight = layoutWeight
    )
    
    // English function row
    fun getEngFunction(): List<KeyModel> = listOf(
        createFunctionKey(KeyType.SHIFT, image = R.drawable.ic_shift, selectImage = R.drawable.ic_shift_on, layoutWeight = 4f),
        createFunctionKey(KeyType.NUMBER, mainText = "123", layoutWeight = 4f),
        KeyModel(keyType = KeyType.SPACE, mainText = "Space", mainTextSize = SPACE_TEXT_SIZE, mainColor = textColor, backgroundColor = backgroundColor, layout_weight = 15f),
        createFunctionKey(KeyType.RETURN, image = R.drawable.ic_enter, selectImage = R.drawable.ic_enter, width = functionWidth + keyWidth + 6, layoutWeight = 5f),
        KeyModel(keyType = KeyType.DELETE, mainText = "⌫", mainTextSize = DELETE_TEXT_SIZE, mainColor = Color.parseColor("#000000"), backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth, layout_weight = 4f)
    )


    // 5. English shift letters
    fun getEngLetter(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel(keyType = KeyType.LETTER, ltText = "w", rtText = "˙", rbText = "q", ltTextSize = SMALL_ENG_SIZE, rtTextSize = DOT_SIZE, rbTextSize = SMALL_ENG_SIZE, ltColor = textColor, rtColor = Color.parseColor("#000000"), rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 0, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_45, rbTextMarginBottom = 10, rtTextGravity = GRAVITY_RIGHT, rtTextMarginRight = 10, rtTextMarginTop = DOT_MARGIN_TOP_ROW1),
            KeyModel(keyType = KeyType.LETTER, ltText = "e", ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE,ltColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 1, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0),
        KeyModel(keyType = KeyType.LETTER, ltText = "r", rbText = "f", ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 2, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_45, rbTextMarginBottom = 10),
    KeyModel(keyType = KeyType.LETTER, ltText = "t", rbText = "g",ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE, ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 3, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_45, rbTextMarginBottom = 10),
        KeyModel(keyType = KeyType.LETTER, ltText = "y", rbText = "p",ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE, ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 4, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_45, rbTextMarginBottom = 10),
    KeyModel(keyType = KeyType.LETTER, ltText = "u", rbText = "'", ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 5, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_50 else MARGIN_60, rbTextMarginBottom = 5),
    KeyModel(keyType = KeyType.LETTER, ltText = "i", ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE,ltColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 6, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_45 else MARGIN_70, ltTextMarginTop = 0),
    KeyModel(keyType = KeyType.LETTER, ltText = "o", rtText = "˙",ltTextSize = SMALL_ENG_SIZE, rtTextSize = DOT_SIZE, rbTextSize = SMALL_ENG_SIZE, ltColor = textColor, rtColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 7, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rtTextGravity = GRAVITY_RIGHT, rtTextMarginRight = 10, rtTextMarginTop = DOT_MARGIN_TOP_ROW1)
    ),
    listOf(
    KeyModel(keyType = KeyType.LETTER, ltText = "a", rtText = "˙", ltTextSize = SMALL_ENG_SIZE, rtTextSize = DOT_SIZE, ltColor = textColor, rtColor = Color.parseColor("#8F8F8F"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 9, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rtTextGravity = GRAVITY_RIGHT, rtTextMarginRight = 10, rtTextMarginTop = DOT_MARGIN_TOP_ROW2),
    KeyModel(keyType = KeyType.LETTER, ltText = "s", rbText = "z",ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE, ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 10, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_45, rbTextMarginBottom = 10),
    KeyModel(keyType = KeyType.LETTER, ltText = "d", rbText = "x",ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE, ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 11, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_45, rbTextMarginBottom = 10),
        KeyModel(keyType = KeyType.LETTER, ltText = "c", rbText = "v", ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 12, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_45, rbTextMarginBottom = 10),
    KeyModel(keyType = KeyType.LETTER, ltText = "h", rbText = "b", ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 13, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_45, rbTextMarginBottom = 10),
    KeyModel(keyType = KeyType.LETTER, ltText = "n", rbText = "j",ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE, ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 14, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_45 else MARGIN_60, rbTextMarginBottom = 10),
    KeyModel(keyType = KeyType.LETTER, ltText = "m", rbText = "k", ltTextSize = SMALL_ENG_SIZE, rbTextSize = SMALL_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 15, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_55, ltTextMarginTop = 0, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_45, rbTextMarginBottom = 10),
        KeyModel(keyType = KeyType.LETTER, ltText = "l", rtText = "˙", ltTextSize = SMALL_ENG_SIZE, rtTextSize = DOT_SIZE, ltColor = textColor, rtColor = Color.parseColor("#8F8F8F"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 16, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_45 else MARGIN_70, ltTextMarginTop = 0, rtTextGravity = GRAVITY_RIGHT, rtTextMarginRight = 10, rtTextMarginTop = DOT_MARGIN_TOP_ROW2)
    )
    )


    // 6. English main layout
    fun getEngShiftLetter(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel(KeyType.LETTER, ltText = "W", rtText = "˙", rbText = "Q", rtColor = Color.parseColor("#000000"), ltColor = textColor, ltTextSize = LARGE_ENG_SIZE, rtTextSize = DOT_SIZE, rbTextSize = LARGE_ENG_SIZE, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 0, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_30 else MARGIN_50, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_30 else MARGIN_50, rbTextMarginBottom = 10, rtTextMarginRight = 10, rtTextMarginTop = DOT_MARGIN_TOP_ROW1),
            KeyModel(KeyType.LETTER, ltText = "E", ltColor = textColor, ltTextSize = LARGE_ENG_SIZE, rbTextSize = LARGE_ENG_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 1, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 5),
            KeyModel(KeyType.LETTER, ltText = "R", rbText = "F", ltColor = textColor, ltTextSize = LARGE_ENG_SIZE, rbTextSize = LARGE_ENG_SIZE,rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 2, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "T", rbText = "G", ltColor = textColor, ltTextSize = LARGE_ENG_SIZE,rbTextSize = LARGE_ENG_SIZE, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 3, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "Y", rbText = "P", ltColor = textColor, ltTextSize =  LARGE_ENG_SIZE, rbTextSize = LARGE_ENG_SIZE,rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 4, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "U", rbText = "'", ltColor = textColor, rbColor = subletterColor, ltTextSize = LARGE_ENG_SIZE, rbTextSize = LARGE_ENG_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 5, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_45 else MARGIN_60, rbTextMarginBottom = 5),
            KeyModel(KeyType.LETTER, ltText = "I", ltColor = textColor, ltTextSize = LARGE_ENG_SIZE, rbTextSize = LARGE_ENG_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 6, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_60, ltTextMarginTop = 5),
            KeyModel(KeyType.LETTER, ltText = "O", rtText = "˙", ltColor = textColor, rtColor = Color.parseColor("#000000"), ltTextSize = LARGE_ENG_SIZE, rtTextSize = DOT_SIZE, rbTextSize = LARGE_ENG_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 7, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_50, ltTextMarginTop = 5, rtTextMarginRight = 10, rtTextMarginTop = DOT_MARGIN_TOP_ROW1)
        ),
        listOf(
            KeyModel(KeyType.LETTER, ltText = "A", rtText = "˙", ltTextSize = LARGE_ENG_SIZE, rtTextSize = DOT_SIZE, ltColor = textColor, rtColor = Color.parseColor("#8F8F8F"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 9, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_50, ltTextMarginTop = 5, rtTextMarginRight = 10, rtTextMarginTop = DOT_MARGIN_TOP_ROW2),
            KeyModel(KeyType.LETTER, ltText = "S", rbText = "Z", ltTextSize = LARGE_ENG_SIZE,rbTextSize = LARGE_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 10, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "D", rbText = "X", ltTextSize = LARGE_ENG_SIZE,rbTextSize = LARGE_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 11, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "C", rbText = "V", ltTextSize = LARGE_ENG_SIZE,rbTextSize = LARGE_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 12, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "H", rbText = "B", ltTextSize = LARGE_ENG_SIZE,rbTextSize = LARGE_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 13, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "N", rbText = "J", ltTextSize = LARGE_ENG_SIZE,rbTextSize = LARGE_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 14, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_60, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "M", rbText = "K", ltTextSize = LARGE_ENG_SIZE,rbTextSize = LARGE_ENG_SIZE,ltColor = textColor, rbColor = subletterColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 15, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_30 else MARGIN_50, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = if (screenWidthDp > screenHeightDp) MARGIN_35 else MARGIN_55, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "L", rtText = "˙", ltTextSize = LARGE_ENG_SIZE, rtTextSize = DOT_SIZE, ltColor = textColor, rtColor = Color.parseColor("#8F8F8F"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, tag = 16, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = if (screenWidthDp > screenHeightDp) MARGIN_40 else MARGIN_60, ltTextMarginTop = 5, rtTextMarginRight = 10, rtTextMarginTop = DOT_MARGIN_TOP_ROW2)
            )
        )

    fun getNumberLetter(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel(keyType = KeyType.LETTER, mainText = "1", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "2", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "3", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "4", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "5", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "6", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "7", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "8", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth)
        ),
        listOf(
            KeyModel(keyType = KeyType.LETTER, mainText = "0", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = ".", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = ",", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "?", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, mainText = "!", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,ltColor = textColor, rbColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth),
            KeyModel(keyType = KeyType.LETTER, ltText = "'", rbText = "\"", ltTextSize = LARGE_ENG_SIZE, rbTextSize = LARGE_ENG_SIZE, ltColor = textColor, rbColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 25, rbTextMarginBottom = 10),
            KeyModel(keyType = KeyType.LETTER, ltText = "-", rbText = "_", ltTextSize = LARGE_ENG_SIZE, rbTextSize = LARGE_ENG_SIZE, ltColor = textColor, rbColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 25, rbTextMarginBottom = 10),
            KeyModel(keyType = KeyType.LETTER, mainText = "9", mainColor = textColor, mainTextSize = NUMBER_TEXT_SIZE,backgroundColor = backgroundColor, height = keyHeight, width = keyWidth)
        )


    )
    fun getNumberFuntion(): List<KeyModel> = listOf(
        KeyModel(
            keyType = KeyType.SPECIAL, mainText = "#+=",mainTextSize = 15f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg,
            height = keyHeight, width = keyWidth, layout_weight = 4f),
        KeyModel(keyType = KeyType.ENG, mainText = "ABC", mainColor = textColor, mainTextSize = 15f, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = keyWidth, layout_weight = 4f),
        KeyModel(keyType = KeyType.SPACE, mainText = "Space",mainTextSize = 18f, mainColor = textColor, backgroundColor = backgroundColor, layout_weight = 15f),
        KeyModel(keyType = KeyType.RETURN, image = R.drawable.ic_enter, mainTextSize = 15f, selectImage = R.drawable.ic_enter, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth + keyWidth + 6, layout_weight = 5f),
        KeyModel(keyType = KeyType.DELETE, mainText = "⌫", mainTextSize = 20f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = keyWidth, layout_weight = 4f)
    )

    fun getSpecialLetter(): List<List<KeyModel>> = listOf(
        listOf(
            KeyModel(KeyType.LETTER, ltText = "@", ltColor = textColor, ltTextSize = SPECIAL_TEXT_SIZE, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 15, ltTextMarginTop = 5),
            KeyModel(KeyType.LETTER, ltText = "#", ltColor = textColor, ltTextSize = SPECIAL_TEXT_SIZE, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5),
            KeyModel(KeyType.LETTER, ltText = "$", rbText = "£", ltColor = textColor, ltTextSize = SPECIAL_TEXT_SIZE, rbTextSize = SPECIAL_TEXT_SIZE, rbColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 25, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "%", rbText = "¥", ltColor = textColor, ltTextSize = SPECIAL_TEXT_SIZE, rbTextSize = SPECIAL_TEXT_SIZE, rbColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 25, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "^", ltColor = textColor, ltTextSize = SPECIAL_TEXT_SIZE, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5),
            KeyModel(KeyType.LETTER, ltText = "&", rbText = "•", ltColor = textColor, ltTextSize = SPECIAL_TEXT_SIZE, rbTextSize = SPECIAL_TEXT_SIZE, rbColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 25, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "*", ltColor = textColor, ltTextSize = SPECIAL_TEXT_SIZE, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5),
            KeyModel(KeyType.LETTER, ltText = "+", rbText = "=", ltColor = textColor, ltTextSize = SPECIAL_TEXT_SIZE, rbTextSize = SPECIAL_TEXT_SIZE, rbColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 25, rbTextMarginBottom = 10)
        ),
        listOf(
            KeyModel(KeyType.LETTER, ltText = "~", ltTextSize = SPECIAL_TEXT_SIZE, ltColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5),
            KeyModel(KeyType.LETTER, ltText = "/", ltTextSize = SPECIAL_TEXT_SIZE, ltColor = textColor, backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5),
            KeyModel(KeyType.LETTER, ltText = ":", rbText = ";", ltTextSize = SPECIAL_TEXT_SIZE, rbTextSize = SPECIAL_TEXT_SIZE, ltColor = textColor, rbColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 20, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 20, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "(", rbText = "<", ltTextSize = SPECIAL_TEXT_SIZE, rbTextSize = SPECIAL_TEXT_SIZE, ltColor = textColor, rbColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 25, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = ")", rbText = ">", ltTextSize = SPECIAL_TEXT_SIZE, rbTextSize = SPECIAL_TEXT_SIZE, ltColor = textColor, rbColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 25, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "{", rbText = "[", ltTextSize = SPECIAL_TEXT_SIZE, rbTextSize = SPECIAL_TEXT_SIZE, ltColor = textColor, rbColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 25, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "}", rbText = "]", ltTextSize = SPECIAL_TEXT_SIZE, rbTextSize = SPECIAL_TEXT_SIZE, ltColor = textColor, rbColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 25, rbTextMarginBottom = 10),
            KeyModel(KeyType.LETTER, ltText = "|", rbText = "\\", ltTextSize = SPECIAL_TEXT_SIZE, rbTextSize = SPECIAL_TEXT_SIZE, ltColor = textColor, rbColor = Color.parseColor("#000000"), backgroundColor = backgroundColor, height = keyHeight, width = keyWidth, ltTextGravity = GRAVITY_LEFT, ltTextMarginLeft = 25, ltTextMarginTop = 5, rbTextGravity = GRAVITY_BOTTOM_RIGHT, rbTextMarginRight = 30, rbTextMarginBottom = 10)
        )
    )

    fun getSpectialFuntion(): List<KeyModel> = listOf(
        KeyModel(
            keyType = KeyType.NUMBER, mainText = "123", mainTextSize = 15f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg,
            height = keyHeight, width = keyWidth, layout_weight = 4f),
        KeyModel(
            keyType = KeyType.ENG, mainText = "ABC", mainColor = textColor,
            mainTextSize = 15f, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = keyWidth, layout_weight = 4f),
        KeyModel(keyType = KeyType.SPACE, mainText = "Space",mainTextSize = 18f, mainColor = textColor, backgroundColor = backgroundColor, layout_weight = 15f),
        KeyModel(keyType = KeyType.RETURN, image = R.drawable.ic_enter, mainTextSize = 15f, selectImage = R.drawable.ic_enter, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = functionWidth + keyWidth + 6, layout_weight = 5f),
        KeyModel(keyType = KeyType.DELETE, mainText = "⌫", mainTextSize = 20f, mainColor = textColor, backgroundColor = functionBg, selectBackgroundColor = functionBg, height = keyHeight, width = keyWidth, layout_weight = 4f)
    )

}