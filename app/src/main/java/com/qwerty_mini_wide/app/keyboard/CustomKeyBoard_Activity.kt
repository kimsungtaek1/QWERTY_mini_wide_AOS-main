package com.qwerty_mini_wide.app.keyboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.qwerty_mini_wide.app.R
import com.qwerty_mini_wide.app.databinding.ViewCustomkeyboardBinding
import com.qwerty_mini_wide.app.keyboard.model.CurrentLanguage
import com.qwerty_mini_wide.app.keyboard.model.KeyLetter
import com.qwerty_mini_wide.app.keyboard.model.KeyType

var currentLanguage:CurrentLanguage = CurrentLanguage.KOR

class CustomKeyBoard_Activity: AppCompatActivity() , CustomKeyboardView.OnKeyboardActionListener {
    private lateinit var binding: ViewCustomkeyboardBinding
    private lateinit var inputField: EditText
    private var vibrator: Vibrator? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewCustomkeyboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator

        if(KeyLetter.isLightMode){
            binding.customKeyboard.setBackgroundColor(resources.getColor(R.color.keyboard_bg) )
        }else{
            binding.customKeyboard.setBackgroundColor(resources.getColor(R.color.bg_darkkeyboard) )
        }

        bind()
    }

    fun bind(){
        inputField = binding.inputField
        
        binding.btnBack.setOnClickListener{
            finish()
        }
        
        // 키보드 액션 리스너 설정
        binding.customKeyboard.setOnKeyboardActionListener(this)
    }

    override fun onKey(code: KeyType, text: String?) {
        // 강한 진동 햅틱 200ms
        vibrator?.let { v ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 최대 강도(255)로 200ms 진동
                v.vibrate(VibrationEffect.createOneShot(200, 255))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(200)
            }
        }
        
        when (code) {

            KeyType.NONE -> { /* Do nothing */ }
            KeyType.LETTER -> { 
                text?.let { inputField.append(it) }
            }
            KeyType.SPACE -> inputField.append(" ")
            KeyType.DELETE -> { 
                val currentText = inputField.text.toString()
                if (currentText.isNotEmpty()) {
                    inputField.setText(currentText.substring(0, currentText.length - 1))
                    inputField.setSelection(inputField.text.length)
                }
            }
            KeyType.KOR -> { 
                currentLanguage = CurrentLanguage.KOR
                text?.let { inputField.append(it) }
            }
            KeyType.ENG -> { 
                currentLanguage = CurrentLanguage.ENG
            }
            KeyType.CHN -> { 
                currentLanguage = CurrentLanguage.CHN
            }
            KeyType.NUMBER -> { 
                text?.let { inputField.append(it) }
            }
            KeyType.SPECIAL -> { 
                text?.let { inputField.append(it) }
            }
            KeyType.SHIFT -> { /* Handle shift mode */ }
            KeyType.ONSHIFT -> { /* Handle shift on mode */ }
            KeyType.RETURN -> inputField.append("\n")
            KeyType.LOCKSHIFT -> { /* Handle caps lock */ }
            KeyType.EMPTY -> { /* Do nothing */ }
        }
    }
}