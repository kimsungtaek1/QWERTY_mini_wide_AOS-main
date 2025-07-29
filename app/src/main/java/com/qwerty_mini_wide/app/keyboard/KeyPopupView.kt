package com.qwerty_mini_wide.app.keyboard

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.TextView
import com.qwerty_mini_wide.app.R
import com.qwerty_mini_wide.app.keyboard.model.KeyLetter

class KeyPopupView(private val context: Context) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var popupView: View? = null
    private var isShowing = false
    private val layoutParams: WindowManager.LayoutParams
    
    init {
        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0
        }
    }
    
    fun show(keyButton: CustomKeyButton, text: String) {
        if (isShowing) {
            updateContent(text)
            updatePosition(keyButton)
            return
        }
        
        if (popupView == null) {
            popupView = LayoutInflater.from(context).inflate(R.layout.key_popup_layout, null)
        }
        
        popupView?.let { view ->
            updateContent(text)
            updatePosition(keyButton)
            updateStyle()
            
            try {
                windowManager.addView(view, layoutParams)
                isShowing = true
                showAnimation(view)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun hide() {
        if (!isShowing) return
        
        popupView?.let { view ->
            hideAnimation(view) {
                try {
                    if (isShowing) {
                        windowManager.removeView(view)
                        isShowing = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    private fun updateContent(text: String) {
        popupView?.findViewById<TextView>(R.id.popup_text)?.apply {
            this.text = text
            textSize = when {
                text.length > 1 -> 18f
                else -> 24f
            }
        }
    }
    
    private fun updatePosition(keyButton: CustomKeyButton) {
        val location = IntArray(2)
        keyButton.getLocationInWindow(location)
        
        val keyWidth = keyButton.width
        val keyHeight = keyButton.height
        
        // Fixed popup size
        val popupWidth = 60
        val popupHeight = 68 // 60dp + 8dp for tail
        
        // Center horizontally over the key
        layoutParams.x = location[0] + (keyWidth - popupWidth) / 2
        
        // Position above the key with some offset
        layoutParams.y = location[1] - popupHeight - 5
        
        // Make sure popup doesn't go off screen
        val screenWidth = context.resources.displayMetrics.widthPixels
        if (layoutParams.x < 0) {
            layoutParams.x = 10
        } else if (layoutParams.x + popupWidth > screenWidth) {
            layoutParams.x = screenWidth - popupWidth - 10
        }
        
        if (isShowing) {
            windowManager.updateViewLayout(popupView, layoutParams)
        }
    }
    
    private fun updateStyle() {
        val isLightMode = KeyLetter.isLightMode
        popupView?.findViewById<View>(R.id.popup_container)?.apply {
            setBackgroundResource(R.drawable.bubble_background)
        }
        
        popupView?.findViewById<TextView>(R.id.popup_text)?.apply {
            setTextColor(context.getColor(android.R.color.black))
        }
    }
    
    private fun showAnimation(view: View) {
        val scaleAnimation = ScaleAnimation(
            0.8f, 1.0f,
            0.8f, 1.0f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 100
        }
        
        val alphaAnimation = AlphaAnimation(0f, 1f).apply {
            duration = 100
        }
        
        val animationSet = AnimationSet(true).apply {
            addAnimation(scaleAnimation)
            addAnimation(alphaAnimation)
        }
        
        view.startAnimation(animationSet)
    }
    
    private fun hideAnimation(view: View, onEnd: () -> Unit) {
        val scaleAnimation = ScaleAnimation(
            1.0f, 0.9f,
            1.0f, 0.9f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 80
        }
        
        val alphaAnimation = AlphaAnimation(1f, 0f).apply {
            duration = 80
        }
        
        val animationSet = AnimationSet(true).apply {
            addAnimation(scaleAnimation)
            addAnimation(alphaAnimation)
            setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    onEnd()
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
        }
        
        view.startAnimation(animationSet)
    }
    
    fun release() {
        hide()
        popupView = null
    }
}