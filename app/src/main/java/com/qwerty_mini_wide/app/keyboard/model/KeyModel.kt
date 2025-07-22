package com.qwerty_mini_wide.app.keyboard.model

import android.graphics.Color
import androidx.annotation.ColorInt

/**
 * Supported languages for the keyboard
 */
enum class CurrentLanguage {
    KOR, ENG, CHN
}

/**
 * Types of keys on the keyboard
 */
enum class KeyType {
    NONE,
    LETTER,
    SPACE,
    DELETE,
    KOR,
    ENG,
    CHN,
    NUMBER,
    SPECIAL,
    SHIFT,
    ONSHIFT,
    RETURN,
    LOCKSHIFT,
    EMPTY
}

/**
 * Model representing a single key on the custom keyboard
 */
class KeyModel(
    var keyType: KeyType = KeyType.NONE,
    var mainText: String = "",
    var ltText: String = "",
    var rtText: String = "",
    var rbText: String = "",
    @ColorInt var mainColor: Int = Color.BLACK,
    var mainTextSize: Float = 21f,
    @ColorInt var ltColor: Int = Color.BLACK,
    var ltTextSize: Int = 16,
    @ColorInt var rtColor: Int = Color.BLACK,
    var rtTextSize: Float = 20f,
    @ColorInt var rbColor: Int = Color.BLACK,
    var rbTextSize: Int = 23,
    var image: Int = 0,
    var selectImage: Int = 0,
    var backgroundColor: Int = Color.WHITE,
    var selectBackgroundColor: Int = Color.WHITE,
    var height: Int = 0,
    var width: Float = 0f,
    var tag: Int = 0,
    var layout_weight:Float = 0f,
    var mainTextGravity: Int = 17, // Gravity.CENTER = 17
    var mainTextMarginLeft: Int = 0,
    var mainTextMarginTop: Int = 0,
    var mainTextMarginRight: Int = 0,
    var mainTextMarginBottom: Int = 0,
    var rbTextMarginLeft: Int = 0,
    var rbTextMarginTop: Int = 0,
    var rbTextMarginRight: Int = 0,
    var rbTextMarginBottom: Int = 0,
    var ltTextMarginLeft: Int = 0,
    var ltTextMarginTop: Int = 0,
    var ltTextMarginRight: Int = 0,
    var ltTextMarginBottom: Int = 0,
    var ltTextGravity: Int = 3, // Gravity.LEFT = 3
    var rbTextGravity: Int = 85 // Gravity.BOTTOM | Gravity.RIGHT = 80 | 5 = 85
)