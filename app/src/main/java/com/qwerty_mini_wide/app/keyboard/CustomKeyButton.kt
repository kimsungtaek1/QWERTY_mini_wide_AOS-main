package com.qwerty_mini_wide.app.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.qwerty_mini_wide.app.R
import com.qwerty_mini_wide.app.keyboard.model.CurrentLanguage
import com.qwerty_mini_wide.app.keyboard.model.KeyLetter
import com.qwerty_mini_wide.app.keyboard.model.KeyModel
import com.qwerty_mini_wide.app.keyboard.model.KeyType


class CustomKeyButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    var keyModel:KeyModel? = null
    private val tv_lt: TextView
    private val tv_rt: TextView
    private val tv_rb: TextView
    private val tvMain: TextView
    private val ivIcon: ImageView
    private var iconRes: Int = 0
    private var pressed_overlay:View

    init {
        LayoutInflater.from(context).inflate(R.layout.key_button, this, true)
        tv_lt  = findViewById(R.id.tv_lt)
        tvMain = findViewById(R.id.tvMain)
        ivIcon = findViewById(R.id.ivIcon)

        tv_rt = findViewById(R.id.tv_rt)
        tv_rb = findViewById(R.id.tv_rb)
        pressed_overlay = findViewById(R.id.pressed_overlay) as View

        context.theme.obtainStyledAttributes(
            attrs, R.styleable.CustomKeyButton, 0, 0
        ).apply {
            try {
                val lt   = getString(R.styleable.CustomKeyButton_lt_Text)
                val rt   = getString(R.styleable.CustomKeyButton_rt_Text)
                val rb   = getString(R.styleable.CustomKeyButton_rb_Text)
                val main  = getString(R.styleable.CustomKeyButton_mainText)
                iconRes   = getResourceId(R.styleable.CustomKeyButton_icon, 0)
                val style = getInt(R.styleable.CustomKeyButton_keyStyle, 0)
                tvMain.setTextColor(Color.BLACK)
                // 배경
                setBackgroundResource(
                    if (style == 1) R.drawable.shape_grey_key
                    else           R.drawable.shape_white_key
                )

                val textColor = ContextCompat.getColor(
                    context,
                    R.color.key_text_primary
                )
                tvMain.setTextColor(textColor)
                tv_lt.setTextColor(
                    ContextCompat.getColor(context, R.color.key_text_secondary)
                )

                // altText
                lt?.let {
                    tv_lt.text = it
                    tv_lt.visibility = VISIBLE
                }

                rt?.let {
                    tv_rt.text = it
                    tv_rt.visibility = VISIBLE
                }

                rb?.let {
                    tv_rb.text = it
                    tv_rb.visibility = VISIBLE
                }



                // mainText
                main?.let { tvMain.text = it }

                // icon
                if (iconRes != 0) {
                    ivIcon.setImageResource(iconRes)
                    ivIcon.visibility = VISIBLE
                    tv_lt.visibility = GONE
                }
            } finally {
                recycle()
            }
        }

        isClickable = true
        isFocusable = true
    }

   // fun getKeyModel(): KeyModel? = keyModel

    fun getMainText(): String = tvMain.text.toString()
    fun getIconRes(): Int    = iconRes
    fun getIcImageView():ImageView = ivIcon

    @SuppressLint("ResourceType")
    fun setData(_keyModel: KeyModel, returnType: Int = EditorInfo.IME_ACTION_NONE) {
        val density = context.resources.displayMetrics.density

        clipToOutline = true
        keyModel = _keyModel
        isSelected = false

        tvMain.setTextSize(TypedValue.COMPLEX_UNIT_PX,_keyModel.mainTextSize.toFloat() * density)

        tv_lt.text = _keyModel.ltText
        tv_lt.setTextColor(_keyModel.ltColor)
        if (_keyModel.ltTextSize != 0) {
            tv_lt.setTextSize(TypedValue.COMPLEX_UNIT_PX,_keyModel.ltTextSize.toFloat() * density)
        }
        
        // Apply individual margins and gravity to lt text
        tv_lt.gravity = _keyModel.ltTextGravity
        val ltParams = tv_lt.layoutParams as FrameLayout.LayoutParams
        
        // 백분율 기반 위치 계산 (키 크기 기준)
        val keyWidthPx = (_keyModel.width * density).toInt()
        val keyHeightPx = (_keyModel.height * density).toInt()
        
        val calculatedMarginLeft = if (_keyModel.ltTextMarginLeft == -1) (keyWidthPx * 0.1f).toInt() else _keyModel.ltTextMarginLeft
        val calculatedMarginTop = if (_keyModel.ltTextMarginTop == -1) (keyHeightPx * 0.07f).toInt() else _keyModel.ltTextMarginTop
        
        ltParams.gravity = _keyModel.ltTextGravity
        ltParams.setMargins(
            calculatedMarginLeft,
            calculatedMarginTop,
            _keyModel.ltTextMarginRight,
            _keyModel.ltTextMarginBottom
        )
        tv_lt.layoutParams = ltParams

        tv_rt.text = _keyModel.rtText
        tv_rt.setTextColor(_keyModel.rtColor)
        if (_keyModel.rtTextSize != 0f) {
            tv_rt.setTextSize(TypedValue.COMPLEX_UNIT_PX,_keyModel.rtTextSize.toFloat() * density)
        }
        
        // Apply individual margins and gravity to rt text
        tv_rt.gravity = _keyModel.rtTextGravity
        val rtParams = tv_rt.layoutParams as FrameLayout.LayoutParams
        rtParams.gravity = _keyModel.rtTextGravity
        rtParams.setMargins(
            _keyModel.rtTextMarginLeft,
            _keyModel.rtTextMarginTop,
            _keyModel.rtTextMarginRight,
            _keyModel.rtTextMarginBottom
        )
        tv_rt.layoutParams = rtParams

        tv_rb.text = _keyModel.rbText
        tv_rb.setTextColor(_keyModel.rbColor)
        if (_keyModel.rbTextSize != 0) {
            tv_rb.setTextSize(TypedValue.COMPLEX_UNIT_PX,_keyModel.rbTextSize.toFloat() * density)
        }
        
        // Apply individual margins and gravity to rb text
        tv_rb.gravity = _keyModel.rbTextGravity
        val rbParams = tv_rb.layoutParams as FrameLayout.LayoutParams
        
        // Q는 우측 10%, 하단 7% 위치 계산
        val calculatedMarginRight = if (_keyModel.rbTextMarginRight == -1) (keyWidthPx * 0.1f).toInt() else _keyModel.rbTextMarginRight
        val calculatedMarginBottom = if (_keyModel.rbTextMarginBottom == -1) (keyHeightPx * 0.07f).toInt() else _keyModel.rbTextMarginBottom
        
        rbParams.gravity = _keyModel.rbTextGravity
        rbParams.setMargins(
            _keyModel.rbTextMarginLeft,
            _keyModel.rbTextMarginTop,
            calculatedMarginRight,
            calculatedMarginBottom
        )
        tv_rb.layoutParams = rbParams



        if (_keyModel.keyType == KeyType.RETURN) {
            when (returnType) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    android.util.Log.d("CustomKeyButton", "Search action detected! returnType=$returnType")
                    tvMain.text = ""
                    setBackgroundDrawable(resources.getDrawable(R.drawable.shape_search_key))
                    tvMain.setTextColor(Color.WHITE)
                    val searchIcon = ContextCompat.getDrawable(context, R.drawable.ic_action_search)
                    searchIcon?.setTint(Color.WHITE)
                    ivIcon.setImageDrawable(searchIcon)
                    ivIcon.visibility = View.VISIBLE
                }
                EditorInfo.IME_ACTION_SEND -> {
                    tvMain.text = ""
                    setBackgroundDrawable(resources.getDrawable(R.drawable.shape_search_key))
                    tvMain.setTextColor(Color.WHITE)
                    val sendIcon = ContextCompat.getDrawable(context, R.drawable.ic_action_send)
                    sendIcon?.setTint(Color.WHITE)
                    ivIcon.setImageDrawable(sendIcon)
                    ivIcon.visibility = View.VISIBLE
                }
                EditorInfo.IME_ACTION_GO -> {
                    tvMain.text = ""
                    setBackgroundDrawable(resources.getDrawable(R.drawable.shape_search_key))
                    tvMain.setTextColor(Color.WHITE)
                    val goIcon = ContextCompat.getDrawable(context, R.drawable.ic_action_go)
                    goIcon?.setTint(Color.WHITE)
                    ivIcon.setImageDrawable(goIcon)
                    ivIcon.visibility = View.VISIBLE
                }
                else -> {
                    // 기본 엔터키 설정
                    tvMain.text = _keyModel.mainText
                    tvMain.setTextColor(_keyModel.mainColor)
                    tvMain.gravity = _keyModel.mainTextGravity
                    
                    // Apply individual margins to main text
                    val params = tvMain.layoutParams as FrameLayout.LayoutParams
                    params.setMargins(
                        _keyModel.mainTextMarginLeft,
                        _keyModel.mainTextMarginTop,
                        _keyModel.mainTextMarginRight,
                        _keyModel.mainTextMarginBottom
                    )
                    tvMain.layoutParams = params
                    
                    setBackgroundDrawable(resources.getDrawable(_keyModel.backgroundColor))

                    if (_keyModel.image != 0) {
                        val image = ContextCompat.getDrawable(context, _keyModel.image.toInt())
                        image?.setTint(if (isLightMode(context)) Color.BLACK else Color.WHITE)
                        ivIcon.setImageDrawable(resources.getDrawable( _keyModel.image))
                    } else {
                        ivIcon.setImageDrawable(null)
                    }
                }
            }
        } else {
            tvMain.text = _keyModel.mainText
            tvMain.setTextColor(_keyModel.mainColor)
            tvMain.gravity = _keyModel.mainTextGravity
            
            // Apply individual margins to main text
            val params = tvMain.layoutParams as FrameLayout.LayoutParams
            params.setMargins(
                _keyModel.mainTextMarginLeft,
                _keyModel.mainTextMarginTop,
                _keyModel.mainTextMarginRight,
                _keyModel.mainTextMarginBottom
            )
            tvMain.layoutParams = params
            
            setBackgroundDrawable(resources.getDrawable(_keyModel.backgroundColor))

            if (_keyModel.image != 0) {
                val image = ContextCompat.getDrawable(context, _keyModel.image.toInt())
                image?.setTint(if (isLightMode(context)) Color.BLACK else Color.WHITE)
                ivIcon.setImageDrawable(resources.getDrawable( _keyModel.image))

            } else {
                ivIcon.setImageDrawable(null)
            }
        }

        if(_keyModel.height != 0){
            val dp = _keyModel.height
            val density = resources.displayMetrics.density
            val px = (dp * density + 0.5f).toInt()
            layoutParams.height = px
        }








        if(_keyModel.layout_weight != 0f){
            val params1 = layoutParams as LinearLayout.LayoutParams
            params1.weight = _keyModel.layout_weight
            params1.width = 0  // Weight를 사용할 때는 width를 0으로 설정해야 함
            layoutParams = params1
            android.util.Log.d("CustomKeyButton", "Setting weight for ${_keyModel.keyType}: ${_keyModel.layout_weight}, mainText: ${_keyModel.mainText}")
        }


        if(_keyModel.keyType == KeyType.NONE){
            visibility = View.GONE
        }else{
            visibility = View.VISIBLE
        }
    }

    fun isLightMode(context: Context): Boolean {
        return KeyLetter.isLightMode
        /*val nightModeFlags = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags != Configuration.UI_MODE_NIGHT_YES*/
    }

    fun isDoubleLetter() :Boolean{
        var textCount = 0

        if(keyModel?.mainText == "ㅅ"){
            textCount += 1
        }

        if (keyModel?.mainText != ""){
            textCount += 1
        }

        if (keyModel?.ltText != ""){
            textCount += 1
        }

        if (keyModel?.rbText != "" && keyModel?.rbText != "·"){
            textCount += 1
        }

        return textCount > 1
    }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        // 눌림 상태라면 보이고, 아니라면 숨긴다
        pressed_overlay.visibility = if (pressed) View.VISIBLE else View.GONE
    }
}
