package com.qwerty_mini_wide.app.keyboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.media.AudioManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.qwerty_mini_wide.app.R
import com.qwerty_mini_wide.app.databinding.ViewCustomkeyboardBinding
import com.qwerty_mini_wide.app.keyboard.model.CurrentLanguage
import com.qwerty_mini_wide.app.keyboard.model.KeyLetter
import com.qwerty_mini_wide.app.keyboard.model.KeyType
import android.content.res.Configuration

var currentLanguage:CurrentLanguage = CurrentLanguage.ENG

class CustomKeyBoard_Activity: AppCompatActivity() , CustomKeyboardView.OnKeyboardActionListener {
    private lateinit var binding: ViewCustomkeyboardBinding
    private lateinit var inputField: EditText
    private var searchField: EditText? = null
    private var sendField: EditText? = null
    private var goField: EditText? = null
    private var vibrationIntensityText: TextView? = null
    private var vibrator: Vibrator? = null
    private var audioManager: AudioManager? = null
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateVibrationIntensityDisplay()
            handler.postDelayed(this, 500) // 0.5초마다 업데이트
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewCustomkeyboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator
        audioManager = getSystemService(AUDIO_SERVICE) as? AudioManager

        // CustomKeyboardView의 init에서 다크모드 설정을 처리하므로 여기서는 제거
        // 실제 키보드와 동일하게 R.color.keyboard_bg 사용 (시스템이 다크모드에 따라 자동 선택)
        binding.customKeyboard.setBackgroundColor(resources.getColor(R.color.keyboard_bg))
        
        // 키보드 레이아웃 업데이트 (가로/세로 모드에 따른 패딩 적용)
        updateKeyboardLayout()

        bind()
    }

    fun bind(){
        inputField = binding.inputField
        searchField = binding.searchField
        sendField = binding.sendField
        goField = binding.goField
        vibrationIntensityText = binding.vibrationIntensityText
        
        // 각 필드의 포커스 변경 시 키보드 업데이트
        setupFieldFocusListeners()
        
        // 각 필드의 액션 처리
        setupFieldActions()
        
        // 30개의 육각별과 유니코드를 기본 텍스트로 설정
        val starText = """
            1. * (U+002A) ASTERISK
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
        
        // inputField.setText(starText)
        // inputField.setSelection(starText.length)
        
        binding.btnBack.setOnClickListener{
            finish()
        }
        
        // 키보드 액션 리스너 설정
        binding.customKeyboard.setOnKeyboardActionListener(this)
        
        // Suggestion bar 버튼 설정
        setupSuggestionBarButtons()
        
        // 시스템 진동 강도 표시 시작
        updateVibrationIntensityDisplay()
        handler.post(updateRunnable)
    }
    
    private fun setupSuggestionBarButtons() {
        // Suggestion bar 버튼 찾기
        val suggestionBar = binding.customKeyboard.findViewById<android.widget.LinearLayout>(R.id.suggestion_bar)
        val btnArrowUp = binding.customKeyboard.findViewById<android.widget.ImageButton>(R.id.btnArrowUp)
        val btnArrowDown = binding.customKeyboard.findViewById<android.widget.ImageButton>(R.id.btnArrowDown)
        val tvDone = binding.customKeyboard.findViewById<android.widget.TextView>(R.id.tvDone)
        
        // 다크 모드에서 화살표 버튼 및 텍스트 색상 설정
        if (!KeyLetter.isLightMode) {
            btnArrowUp?.setColorFilter(resources.getColor(R.color.suggestion_arrow_inactive))
            btnArrowDown?.setColorFilter(resources.getColor(R.color.suggestion_arrow_inactive))
            tvDone?.setTextColor(resources.getColor(R.color.suggestion_btn_text))
        }
        
        // Suggestion bar 배경색을 실제 키보드와 동일하게 설정
        if (KeyLetter.isLightMode) {
            suggestionBar?.setBackgroundColor(resources.getColor(R.color.key_white))
        } else {
            // iOS 스타일 다크 모드 색상 적용
            suggestionBar?.setBackgroundColor(resources.getColor(R.color.suggestion_bar_dark_bg))
        }
        
        // iOS 스타일 버튼 기능 설정 (미리보기에서는 포커스 이동 시뮬레이션)
        btnArrowUp?.setOnClickListener {
            // 이전 필드로 포커스 이동
            when (currentFocus) {
                searchField -> inputField.requestFocus()
                sendField -> searchField?.requestFocus()
                goField -> sendField?.requestFocus()
                else -> { /* 첫 번째 필드에서는 아무 동작 없음 */ }
            }
        }
        
        btnArrowDown?.setOnClickListener {
            // 다음 필드로 포커스 이동
            when (currentFocus) {
                inputField -> searchField?.requestFocus()
                searchField -> sendField?.requestFocus()
                sendField -> goField?.requestFocus()
                else -> { /* 마지막 필드에서는 아무 동작 없음 */ }
            }
        }
        
        tvDone?.setOnClickListener {
            // 키보드 숨기기 시뮬레이션 (포커스 제거)
            currentFocus?.clearFocus()
            Toast.makeText(this, "완료", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupFieldFocusListeners() {
        inputField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.customKeyboard.initViews(0) // 기본 엔터키
            }
        }
        
        searchField?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.customKeyboard.initViews(EditorInfo.IME_ACTION_SEARCH)
            }
        }
        
        sendField?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.customKeyboard.initViews(EditorInfo.IME_ACTION_SEND)
            }
        }
        
        goField?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.customKeyboard.initViews(EditorInfo.IME_ACTION_GO)
            }
        }
    }
    
    private fun setupFieldActions() {
        searchField?.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = textView.text.toString()
                Toast.makeText(this, "검색: $text", Toast.LENGTH_SHORT).show()
                android.util.Log.d("CustomKeyBoard_Activity", "Search: $text")
                true
            } else {
                false
            }
        }
        
        sendField?.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val text = textView.text.toString()
                Toast.makeText(this, "전송: $text", Toast.LENGTH_SHORT).show()
                android.util.Log.d("CustomKeyBoard_Activity", "Send: $text")
                true
            } else {
                false
            }
        }
        
        goField?.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val text = textView.text.toString()
                Toast.makeText(this, "이동: $text", Toast.LENGTH_SHORT).show()
                android.util.Log.d("CustomKeyBoard_Activity", "Go: $text")
                true
            } else {
                false
            }
        }
    }
    
    private fun updateVibrationIntensityDisplay() {
        // 고정된 진동 강도 값 30 사용
        val intensity = 30
        
        // Ringer Mode 정보도 함께 표시
        val ringerMode = audioManager?.ringerMode ?: -1
        val ringerModeText = when (ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> "무음"
            AudioManager.RINGER_MODE_VIBRATE -> "진동"
            AudioManager.RINGER_MODE_NORMAL -> "소리"
            else -> "알수없음"
        }
        
        val displayText = "진동강도: $intensity | 모드: $ringerModeText"
        vibrationIntensityText?.text = displayText
    }
    
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 화면 회전 시 키보드 레이아웃 업데이트
        updateKeyboardLayout()
        // 키보드 뷰 재초기화
        binding.customKeyboard.updateConfiguration()
        binding.customKeyboard.requestLayout()
        binding.customKeyboard.invalidate()
    }
    
    private fun updateKeyboardLayout() {
        // 실제 키보드 서비스와 동일하게 가로/세로 모드에 따른 레이아웃 업데이트
        val configuration = resources.configuration
        val orientation = configuration.orientation
        
        // 키보드가 다시 초기화되도록 요청
        binding.customKeyboard.post {
            binding.customKeyboard.requestLayout()
            binding.customKeyboard.invalidate()
        }
    }

    override fun onKey(code: KeyType, text: String?) {
        // 햅틱 피드백 - 고정 강도로 설정
        vibrator?.let { v ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 고정 강도 30 (시스템 설정과 무관하게 일정)
                val fixedIntensity = 30
                
                // 100ms 시간, 고정 강도로 진동
                v.vibrate(VibrationEffect.createOneShot(100, fixedIntensity))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(100)
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
            KeyType.RETURN -> {
                // 현재 포커스된 필드 확인
                val currentFocus = currentFocus as? EditText
                when (currentFocus) {
                    searchField -> {
                        Toast.makeText(this, "검색: ${searchField?.text}", Toast.LENGTH_SHORT).show()
                    }
                    sendField -> {
                        Toast.makeText(this, "전송: ${sendField?.text}", Toast.LENGTH_SHORT).show()
                    }
                    goField -> {
                        Toast.makeText(this, "이동: ${goField?.text}", Toast.LENGTH_SHORT).show()
                    }
                    else -> inputField.append("\n")
                }
            }
            KeyType.LOCKSHIFT -> { /* Handle caps lock */ }
            KeyType.EMPTY -> { /* Do nothing */ }
        }
    }
    
    private fun getSystemVibrationIntensity(): Int {
        return try {
            // 삼성 기기의 진동 강도 설정 키들
            val possibleKeys = listOf(
                "VIB_FEEDBACK_MAGNITUDE",  // 삼성 터치 진동 강도
                "VIB_RECVCALL_MAGNITUDE",  // 삼성 통화 진동 강도
                "SEM_VIBRATION_NOTIFICATION_INTENSITY",  // 삼성 알림 진동
                "SEM_VIBRATION_FORCE_TOUCH_INTENSITY"   // 삼성 터치 강도
            )
            
            var intensity = -1
            
            // Settings.System에서 시도
            for (key in possibleKeys) {
                try {
                    intensity = Settings.System.getInt(contentResolver, key)
                    if (intensity != -1) {
                        // 찾았으면 로그 출력하고 break
                        android.util.Log.d("VibrationIntensity", "Found key: $key with value: $intensity")
                        break
                    }
                } catch (e: Exception) {
                    // 이 키가 없으면 다음 키 시도
                }
            }
            
            // Settings.Global에서도 시도
            if (intensity == -1) {
                for (key in possibleKeys) {
                    try {
                        intensity = Settings.Global.getInt(contentResolver, key)
                        if (intensity != -1) {
                            android.util.Log.d("VibrationIntensity", "Found global key: $key with value: $intensity")
                            break
                        }
                    } catch (e: Exception) {
                        // 이 키가 없으면 다음 키 시도
                    }
                }
            }
            
            // AudioManager를 통해 진동 모드 확인
            if (intensity == -1 && audioManager != null) {
                val ringerMode = audioManager!!.ringerMode
                android.util.Log.d("VibrationIntensity", "Ringer mode: $ringerMode")
                intensity = when (ringerMode) {
                    AudioManager.RINGER_MODE_SILENT -> 0
                    AudioManager.RINGER_MODE_VIBRATE -> 2
                    AudioManager.RINGER_MODE_NORMAL -> 3
                    else -> 2
                }
            }
            
            // 삼성 기기는 0-5 범위, 다른 기기는 0-3 범위
            val result = when (intensity) {
                -1 -> 100  // 찾지 못함
                0 -> 0     // 진동 끔
                1 -> 40    // 매우 약함
                2 -> 80    // 약함  
                3 -> 120   // 중간
                4 -> 160   // 강함
                5 -> 200   // 매우 강함
                else -> {
                    // 다른 범위의 값이면 스케일링
                    if (intensity > 5) {
                        (intensity * 255 / 100).coerceIn(0, 255)
                    } else {
                        100 // 기본값
                    }
                }
            }
            
            android.util.Log.d("VibrationIntensity", "Final intensity value: $result (raw: $intensity)")
            result
        } catch (e: Exception) {
            android.util.Log.e("VibrationIntensity", "Error getting vibration intensity", e)
            100 // 오류 시 기본값 반환
        }
    }
}