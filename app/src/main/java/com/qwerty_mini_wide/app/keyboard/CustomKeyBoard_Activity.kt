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

var currentLanguage:CurrentLanguage = CurrentLanguage.ENG

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
        
        // 30개의 육각별과 유니코드를 기본 텍스트로 설정
        val starText = """
            1. ✱ (U+2731) HEAVY ASTERISK OPERATOR
            2. ✲ (U+2732) OPEN CENTRE ASTERISK  
            3. ✳ (U+2733) EIGHT SPOKED ASTERISK
            4. ✶ (U+2736) SIX POINTED BLACK STAR
            5. 🔯 (U+1F52F) SIX POINTED STAR WITH MIDDLE DOT
            6. ⚹ (U+26B9) SEXTILE
            7. ＊ (U+FF0A) FULLWIDTH ASTERISK
            8. ⁎ (U+204E) LOW ASTERISK
            9. ✡ (U+2721) STAR OF DAVID
            10. ⛤ (U+26E4) PENTAGRAM
            11. ⛥ (U+26E5) RIGHT-HANDED INTERLACED PENTAGRAM
            12. ⛦ (U+26E6) LEFT-HANDED INTERLACED PENTAGRAM
            13. ⛧ (U+26E7) INVERTED PENTAGRAM
            14. ❅ (U+2745) TIGHT TRIFOLIATE SNOWFLAKE
            15. ❆ (U+2746) HEAVY CHEVRON SNOWFLAKE
            16. ❇ (U+2747) SPARKLE
            17. ❈ (U+2748) HEAVY SPARKLE
            18. ❉ (U+2749) BALLOON-SPOKED ASTERISK
            19. ❊ (U+274A) EIGHT TEARDROP-SPOKED PROPELLER ASTERISK
            20. ❋ (U+274B) HEAVY EIGHT TEARDROP-SPOKED PROPELLER ASTERISK
            21. ※ (U+203B) REFERENCE MARK
            22. ⁕ (U+2055) FLOWER PUNCTUATION MARK
            23. ⁜ (U+205C) DOTTED CROSS
            24. ✻ (U+273B) TEARDROP-SPOKED ASTERISK
            25. ✼ (U+273C) OPEN CENTRE TEARDROP-SPOKED ASTERISK
            26. ✽ (U+273D) HEAVY TEARDROP-SPOKED ASTERISK
            27. ✾ (U+273E) SIX PETALLED BLACK AND WHITE FLORETTE
            28. ✿ (U+273F) BLACK FLORETTE
            29. ❀ (U+2740) WHITE FLORETTE
            30. ❁ (U+2741) EIGHT PETALLED OUTLINED BLACK FLORETTE
        """.trimIndent()
        
        inputField.setText(starText)
        inputField.setSelection(starText.length)
        
        binding.btnBack.setOnClickListener{
            finish()
        }
        
        // 키보드 액션 리스너 설정
        binding.customKeyboard.setOnKeyboardActionListener(this)
    }

    override fun onKey(code: KeyType, text: String?) {
        // 적당한 햅틱 피드백 (100ms, 약한 강도)
        vibrator?.let { v ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 20ms 시간, 매우 약한 강도(30)로 진동
                v.vibrate(VibrationEffect.createOneShot(20, 30))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(20)
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
                // Korean keyboard removed - use English instead
                currentLanguage = CurrentLanguage.ENG
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