package com.qwerty_mini_wide.app.keyboard.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.KeyboardView
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.media.AudioManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.qwerty_mini_wide.app.R
import com.qwerty_mini_wide.app.databinding.ServiceKeyboardviewBinding
import com.qwerty_mini_wide.app.keyboard.CustomKeyboardView
import com.qwerty_mini_wide.app.keyboard.currentLanguage
// import com.qwerty_mini_wide.app.keyboard.manager.HangulAutomata // Korean support removed
import com.qwerty_mini_wide.app.keyboard.model.KeyLetter
import com.qwerty_mini_wide.app.keyboard.model.KeyType

class CustomKeyBoard_Service: InputMethodService() , CustomKeyboardView.OnKeyboardActionListener,
    KeyboardView.OnKeyboardActionListener, RecognitionListener {
    var actionId: Int = 0
    private val typedBuffer = StringBuilder()
    private lateinit var binding: ServiceKeyboardviewBinding
    private var previousSelStart: Int = -1
    private val handler = Handler(Looper.getMainLooper())
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var vibrator: Vibrator? = null
    private var audioManager: AudioManager? = null

    override fun onCreate() {
        super.onCreate()
        try {
            val view = layoutInflater.inflate(R.layout.service_keyboardview, null)
            binding = ServiceKeyboardviewBinding.bind(view)
            vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator
            audioManager = getSystemService(AUDIO_SERVICE) as? AudioManager
        } catch (e: Exception) {
            Log.e("CustomKeyBoard_Service", "Error in onCreate: ${e.message}", e)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        // 커스텀 키보드 레이아웃 inflate
        try {
            // 기존 뷰가 부모를 가지고 있으면 제거
            if (::binding.isInitialized) {
                (binding.root.parent as? android.view.ViewGroup)?.removeView(binding.root)
            }
            
            // 새로운 뷰 생성
            val view = layoutInflater.inflate(R.layout.service_keyboardview, null)
            binding = ServiceKeyboardviewBinding.bind(view)

            binding.customKeyboard.setOnKeyboardActionListener(this)

            setBackgroundBg()
            setupSuggestionBar()
            // suggestion bar를 보이도록 설정 - 숨김 처리 제거

            val extracted = currentInputConnection?.getExtractedText(ExtractedTextRequest(), 0)
            previousSelStart = extracted?.selectionStart ?: -1

            typedBuffer.clear()
            //requestLocationPermission()
            return binding.root
        } catch (e: Exception) {
            Log.e("CustomKeyBoard_Service", "Error in onCreateInputView: ${e.message}", e)
            // 기본 뷰를 반환하여 크래시 방지
            return View(this)
        }
    }



    fun setBackgroundBg(){
        // 시스템 테마에 따라 자동으로 적절한 색상이 선택됨
        binding.customKeyboard.setBackgroundColor(ContextCompat.getColor(this, R.color.keyboard_bg))
    }


    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        try {
            if (!::binding.isInitialized) {
                return
            }
            // 키보드 상태 및 뷰 완전 초기화
            binding.customKeyboard.composingLength = 0
            // binding.customKeyboard.automata = HangulAutomata() // Korean support removed
            binding.customKeyboard.currentState = KeyType.ENG // 기본값(필요시 변경)
            currentLanguage = com.qwerty_mini_wide.app.keyboard.model.CurrentLanguage.ENG // 기본값(필요시 변경)
            actionId = (editorInfo?.imeOptions ?: 0) and EditorInfo.IME_MASK_ACTION
            binding.customKeyboard.initViews(actionId) // 뷰도 항상 초기화
            typedBuffer.clear()
        } catch (e: Exception) {
            Log.e("CustomKeyBoard_Service", "Error in onStartInputView: ${e.message}", e)
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

        lastKeyTypedTime = System.currentTimeMillis()
        val ic = currentInputConnection
        val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
        Log.i("Onkey","Onkey로 왔어")
        when (code) {
            KeyType.NONE -> return
            KeyType.LETTER -> {
                ic.commitText(text, 1)
                typedBuffer.append(text)
            }
            KeyType.SPACE -> {
                ic.finishComposingText()
                ic.commitText(" ",1)
                typedBuffer.append(" ")
            }
            KeyType.DELETE -> {
                val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
                val selectionLength = (extracted?.selectionEnd ?: 0) - (extracted?.selectionStart ?: 0)
                if (selectionLength > 0) {
                    // 텍스트 선택됨 → 삭제
                    ic.commitText("", 1)
                    // Korean support removed - no automata handling
                    binding.customKeyboard.composingLength = 0
                } else {
                    ic.deleteSurroundingText(1, 0)
                    if (typedBuffer.isNotEmpty()) {
                            typedBuffer.setLength(typedBuffer.length - 1)
                        }
                }
            }
            KeyType.KOR -> ic.commitText(text ?: "", 1) // Korean support removed - treat as normal text
            KeyType.ENG -> ic.finishComposingText()
            KeyType.CHN -> ic.finishComposingText()
            KeyType.NUMBER -> ic.finishComposingText()
            KeyType.SPECIAL -> return
            KeyType.SHIFT -> return
            KeyType.ONSHIFT -> return
            KeyType.RETURN ->{
                ic.finishComposingText()
                // 2) 저장된 editorInfo에서 imeOptions의 액션 마스크를 꺼냄

                val imeAction = actionId

                when (imeAction) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        // 검색 버튼이 달려있는 EditText인 경우
                        ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH)
                    }
                    EditorInfo.IME_ACTION_GO -> {
                        ic.performEditorAction(EditorInfo.IME_ACTION_GO)
                    }
                    EditorInfo.IME_ACTION_DONE -> {
                        ic.performEditorAction(EditorInfo.IME_ACTION_DONE)
                    }
                    EditorInfo.IME_ACTION_SEND -> {
                        ic.performEditorAction(EditorInfo.IME_ACTION_SEND)
                    }
                    else -> {
                        // 특별한 액션이 지정되지 않은 경우에는 기존처럼 줄바꿈
                        ic.commitText("\n", 1)
                    }
                }
            }
            KeyType.LOCKSHIFT -> return
            KeyType.EMPTY -> return
        }
    }




    override fun onPress(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onRelease(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onKey(p0: Int, p1: IntArray?) {
        TODO("Not yet implemented")
    }

    override fun onText(p0: CharSequence?) {
        TODO("Not yet implemented")
    }

    override fun swipeLeft() {
        TODO("Not yet implemented")
    }

    override fun swipeRight() {
        TODO("Not yet implemented")
    }

    override fun swipeDown() {
        TODO("Not yet implemented")
    }

    override fun swipeUp() {
        TODO("Not yet implemented")
    }
    private var lastKeyTypedTime: Long = 0

    override fun onUpdateSelection(
        oldSelStart: Int, oldSelEnd: Int,
        newSelStart: Int, newSelEnd: Int,
        candidatesStart: Int, candidatesEnd: Int
    ) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
// 커서가 이동했을 때

        // 커서 이동이 감지되었고, 최근 입력이 없었다면 손 터치로 판단
        val now = System.currentTimeMillis()
        val typedRecently = now - lastKeyTypedTime < 300

        if ((newSelStart != previousSelStart || newSelEnd != previousSelStart) && !typedRecently) {
            // Korean support removed - no automata handling
            binding.customKeyboard.composingLength = 0
            currentInputConnection.finishComposingText()
        }

        previousSelStart = newSelStart





    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        try {
            applySystemTheme()
        } catch (e: Exception) {
            Log.e("CustomKeyBoard_Service", "Error in onStartInput: ${e.message}", e)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            // 다크 모드 상태 변경 감지 및 적용
            val nightModeFlags = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
            KeyLetter.isLightMode = !isNightMode
            Log.d("CustomKeyBoard_Service", "Configuration changed - Dark mode: $isNightMode")
            
            // 화면 회전 시 키보드 재초기화
            if (::binding.isInitialized) {
                // 키보드 뷰 재생성 필요시 처리
                val orientation = newConfig.orientation
                Log.d("CustomKeyBoard_Service", "Configuration changed - Orientation: $orientation")
                
                // 기존 뷰의 parent를 제거하고 새로운 뷰를 생성
                (binding.root.parent as? android.view.ViewGroup)?.removeView(binding.root)
                
                // 새로운 뷰를 inflate
                val view = layoutInflater.inflate(R.layout.service_keyboardview, null)
                binding = ServiceKeyboardviewBinding.bind(view)
                
                // 리스너 재설정
                binding.customKeyboard.setOnKeyboardActionListener(this)
                
                // 입력 뷰 설정
                setInputView(binding.root)
                
                // 키보드 상태 복원
                // 반응형 코드 주석처리 - 고정 레이아웃 사용
                // binding.customKeyboard.updateConfiguration()
                binding.customKeyboard.requestLayout()
                binding.customKeyboard.invalidate()
                
                // 테마 및 제안 바 업데이트
                applySystemTheme()
                setupSuggestionBar()
                
                // 다크모드 변경 시 키 배경색 업데이트
                binding.customKeyboard.setupKeys()
                
                // 현재 액션 ID 유지
                binding.customKeyboard.initViews(actionId)
            }
        } catch (e: Exception) {
            Log.e("CustomKeyBoard_Service", "Error in onConfigurationChanged: ${e.message}", e)
            // 실패 시 기본 뷰 유지
            try {
                if (::binding.isInitialized) {
                    binding.customKeyboard.requestLayout()
                    binding.customKeyboard.invalidate()
                }
            } catch (fallbackError: Exception) {
                Log.e("CustomKeyBoard_Service", "Fallback error: ${fallbackError.message}", fallbackError)
            }
        }
    }

    private fun applySystemTheme() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        updateKeyboardAppearance(!isNightMode)
    }

    private fun updateKeyboardAppearance(isDay: Boolean) {
        try {
            if (!::binding.isInitialized) {
                return
            }
            // CustomKeyboardView의 init에서 다크모드를 처리하므로 여기서는 initViews만 호출
            // KeyLetter.isLightMode는 CustomKeyboardView에서 관리
            Log.i("Is LightMode : ", isDay.toString())
            binding.customKeyboard.initViews(actionId)
            setBackgroundBg()
        } catch (e: Exception) {
            Log.e("CustomKeyBoard_Service", "Error in updateKeyboardAppearance: ${e.message}", e)
        }
    }

    private fun setComposingTextWithoutUnderline(text: String?) {
        val ic = currentInputConnection
        if (ic != null && text != null) {
            val ssb = SpannableStringBuilder(text)
            
            // 투명한 밑줄로 설정 (사실상 제거)
            ssb.setSpan(object : UnderlineSpan() {
                override fun updateDrawState(tp: TextPaint) {
                    tp.isUnderlineText = false
                }
            }, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            
            ic.setComposingText(ssb, 1)
        }
    }

    fun getTypedBuffer(): String {
        return typedBuffer.toString()
    }

    fun startSpeechRecognition() {
        Log.d("SpeechRecognizer", "startSpeechRecognition called, isListening: $isListening")
        
        if (isListening) {
            Log.d("SpeechRecognizer", "Already listening, stopping...")
            stopSpeechRecognition()
            return
        }
        
        // Check if SpeechRecognizer is available
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("SpeechRecognizer", "Speech recognition not available")
            Toast.makeText(this, "음성 인식을 사용할 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.e("SpeechRecognizer", "RECORD_AUDIO permission not granted")
            showPermissionDialog()
            return
        }
        
        try {
            if (speechRecognizer == null) {
                Log.d("SpeechRecognizer", "Creating new SpeechRecognizer")
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
                speechRecognizer?.setRecognitionListener(this)
            }
            
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false) // 온라인 우선, 오프라인도 허용
                putExtra(RecognizerIntent.EXTRA_PROMPT, "말하세요...") // 음성인식 프롬프트
            }
            
            Log.d("SpeechRecognizer", "Starting speech recognition...")
            speechRecognizer?.startListening(intent)
            isListening = true
            Toast.makeText(this, "음성 인식 시작", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "Error starting speech recognition", e)
            Toast.makeText(this, "음성 인식 시작 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            isListening = false
            }
    }
    
    private fun stopSpeechRecognition() {
        Log.d("SpeechRecognizer", "Stopping speech recognition")
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "Error stopping speech recognition", e)
        }
        isListening = false
    }
    
    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        binding.customKeyboard.release()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        binding.customKeyboard.release()
    }

    // RecognitionListener methods
    override fun onReadyForSpeech(params: Bundle?) {
        Log.d("SpeechRecognizer", "Ready for speech")
    }

    override fun onBeginningOfSpeech() {
        Log.d("SpeechRecognizer", "Beginning of speech")
    }

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        Log.d("SpeechRecognizer", "End of speech")
        isListening = false
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "오디오 녹음 오류"
            SpeechRecognizer.ERROR_CLIENT -> "클라이언트 오류"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "권한 부족"
            SpeechRecognizer.ERROR_NETWORK -> "네트워크 오류"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 타임아웃"
            SpeechRecognizer.ERROR_NO_MATCH -> "음성을 인식할 수 없습니다"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "음성 인식기 사용 중"
            SpeechRecognizer.ERROR_SERVER -> "서버 오류"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "음성 입력 시간 초과"
            else -> "알 수 없는 오류: $error"
        }
        Log.e("SpeechRecognizer", "Error: $error - $errorMessage")
        
        if (error != SpeechRecognizer.ERROR_NO_MATCH) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
        
        isListening = false
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
            val recognizedText = matches[0]
            Log.d("SpeechRecognizer", "Recognized: $recognizedText")
            
            // Insert recognized text
            val ic = currentInputConnection
            ic?.finishComposingText()
            ic?.commitText(recognizedText, 1)
            typedBuffer.append(recognizedText)
        }
        isListening = false
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
            val partialText = matches[0]
            Log.d("SpeechRecognizer", "Partial: $partialText")
            // You can show partial results in UI if needed
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
    
    private fun showPermissionDialog() {
        try {
            val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            builder.setTitle("마이크 권한 필요")
            builder.setMessage("음성 인식 기능을 사용하려면 마이크 권한이 필요합니다. 설정에서 권한을 허용해 주세요.")
            builder.setPositiveButton("설정") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "마이크 권한이 맀됐되었습니다", Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.window?.setType(android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            dialog.show()
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "Error showing permission dialog", e)
            Toast.makeText(this, "설정 > 앱 > QWERTY_mini_wide > 권한에서 마이크 권한을 허용해 주세요", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupSuggestionBar() {
        val suggestionBar = binding.customKeyboard.findViewById<LinearLayout>(R.id.suggestion_bar)
        val displayMetrics = resources.displayMetrics
        val screenHeightDp = displayMetrics.heightPixels.toFloat() / displayMetrics.density
        val screenWidthDp = displayMetrics.widthPixels.toFloat() / displayMetrics.density
        
        // 화면 방향에 따라 높이 비율 설정 (세로: 5%, 가로: 10%)
        val heightRatio = if (screenWidthDp < screenHeightDp) 0.05f else 0.10f
        val suggestionBarHeight = (displayMetrics.heightPixels * heightRatio).toInt()
        
        // suggestion bar의 높이 설정
        val layoutParams = suggestionBar.layoutParams
        layoutParams.height = suggestionBarHeight
        suggestionBar.layoutParams = layoutParams
        
        // suggestion bar 배경색을 키 색상과 일치시키기
        if (KeyLetter.isLightMode) {
            suggestionBar.setBackgroundColor(ContextCompat.getColor(this, R.color.key_white))
        } else {
            // iOS 스타일 다크 모드 색상 적용
            suggestionBar.setBackgroundColor(ContextCompat.getColor(this, R.color.suggestion_bar_dark_bg))
        }
        
        // 화살표 버튼 크기 조정 및 색상 설정
        val arrowSize = (suggestionBarHeight * 0.5f).toInt()
        val btnArrowDown = suggestionBar.findViewById<android.widget.ImageButton>(R.id.btnArrowDown)
        val btnArrowUp = suggestionBar.findViewById<android.widget.ImageButton>(R.id.btnArrowUp)
        
        // 화살표 버튼 색상 설정 (다크 모드에서 iOS 스타일)
        if (!KeyLetter.isLightMode) {
            btnArrowDown?.setColorFilter(ContextCompat.getColor(this, R.color.suggestion_arrow_inactive))
            btnArrowUp?.setColorFilter(ContextCompat.getColor(this, R.color.suggestion_arrow_inactive))
        }
        
        btnArrowDown?.let { btn ->
            val params = btn.layoutParams
            params.width = arrowSize
            params.height = arrowSize
            btn.layoutParams = params
        }
        btnArrowUp?.let { btn ->
            val params = btn.layoutParams
            params.width = arrowSize
            params.height = arrowSize
            btn.layoutParams = params
        }
        
        // 텍스트 크기 조정 및 색상 설정
        val tvDone = suggestionBar.findViewById<android.widget.TextView>(R.id.tvDone)
        tvDone?.textSize = suggestionBarHeight * 0.35f / displayMetrics.density
        
        // 다크 모드에서 '완료' 버튼 색상 설정
        if (!KeyLetter.isLightMode) {
            tvDone?.setTextColor(ContextCompat.getColor(this, R.color.suggestion_btn_text))
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
                        Log.d("VibrationIntensity", "Found key: $key with value: $intensity")
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
                            Log.d("VibrationIntensity", "Found global key: $key with value: $intensity")
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
                Log.d("VibrationIntensity", "Ringer mode: $ringerMode")
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
            
            Log.d("VibrationIntensity", "Final intensity value: $result (raw: $intensity)")
            result
        } catch (e: Exception) {
            Log.e("VibrationIntensity", "Error getting vibration intensity", e)
            100 // 오류 시 기본값 반환
        }
    }
}