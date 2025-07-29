package com.qwerty_mini_wide.app.keyboard

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.PopupWindow
import android.widget.TextView
import com.qwerty_mini_wide.app.R
import com.qwerty_mini_wide.app.keyboard.model.KeyLetter

class KeyPopupWindow(private val context: Context) {
    private var popupWindow: PopupWindow? = null
    private var popupView: View? = null
    
    init {
        setupPopupView()
    }
    
    private fun setupPopupView() {
        popupView = LayoutInflater.from(context).inflate(R.layout.key_popup_layout, null)
        
        popupWindow = PopupWindow(popupView).apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isFocusable = false
            isTouchable = false
            isClippingEnabled = false
            setBackgroundDrawable(null)
            elevation = 12f
        }
    }
    
    fun show(anchor: View, text: String) {
        updateContent(text)
        updateStyle()
        
        // Measure the popup view to get its dimensions
        popupView?.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = popupView?.measuredWidth ?: 0
        val popupHeight = popupView?.measuredHeight ?: 0
        
        // Get anchor location on screen
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        
        val anchorX = location[0]
        val anchorY = location[1]
        val anchorWidth = anchor.width
        val anchorHeight = anchor.height
        
        // Calculate popup position (centered above the key)
        val xOffset = (anchorWidth - popupWidth) / 2
        val yOffset = -(popupHeight + anchorHeight - 5) // Position closer to the key
        
        try {
            popupWindow?.showAsDropDown(anchor, xOffset, yOffset, Gravity.NO_GRAVITY)
            showAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun hide() {
        if (popupWindow?.isShowing == true) {
            hideAnimation {
                try {
                    popupWindow?.dismiss()
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
                text.length > 1 -> 12f
                else -> 14f
            }
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
    
    private fun showAnimation() {
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
        
        popupView?.startAnimation(animationSet)
    }
    
    private fun hideAnimation(onEnd: () -> Unit) {
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
        
        popupView?.startAnimation(animationSet)
    }
    
    fun release() {
        hide()
        popupWindow = null
        popupView = null
    }
}