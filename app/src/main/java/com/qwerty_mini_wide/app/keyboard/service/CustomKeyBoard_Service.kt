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
import com.qwerty_mini_wide.app.keyboard.manager.HangulAutomata
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

    override fun onCreate() {
        super.onCreate()
        val view = layoutInflater.inflate(R.layout.service_keyboardview, null)
        binding = ServiceKeyboardviewBinding.bind(view)
        vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        // 커스텀 키보드 레이아웃 inflate


        binding.customKeyboard.setOnKeyboardActionListener(this)

        setBackgroundBg()
        setupSuggestionBar()
        binding.customKeyboard.findViewById<LinearLayout>(R.id.suggestion_bar).visibility = GONE

        val extracted = currentInputConnection.getExtractedText(ExtractedTextRequest(), 0)
        previousSelStart = extracted?.selectionStart ?: -1

        typedBuffer.clear()
        //requestLocationPermission()
        return binding.root
    }



    fun setBackgroundBg(){
        if(KeyLetter.isLightMode){
            binding.customKeyboard.setBackgroundColor(resources.getColor(R.color.keyboard_bg) )
        }else{
            binding.customKeyboard.setBackgroundColor(resources.getColor(R.color.bg_darkkeyboard) )
        }
    }


    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        // 키보드 상태 및 뷰 완전 초기화
        binding.customKeyboard.composingLength = 0
        binding.customKeyboard.automata = HangulAutomata()
        binding.customKeyboard.currentState = KeyType.ENG // 기본값(필요시 변경)
        currentLanguage = com.qwerty_mini_wide.app.keyboard.model.CurrentLanguage.ENG // 기본값(필요시 변경)
        binding.customKeyboard.initViews() // 뷰도 항상 초기화
        actionId = (editorInfo?.imeOptions ?: 0) and EditorInfo.IME_MASK_ACTION
        typedBuffer.clear()
    }

    override fun onKey(code: KeyType, text: String?) {
        // 햅틱 피드백 (100ms, 강도 100)
        vibrator?.let { v ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 100ms 시간, 강도 100으로 진동
                v.vibrate(VibrationEffect.createOneShot(100, 150))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(150)
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
                    binding.customKeyboard.automata.deleteBuffer()
                    binding.customKeyboard.composingLength = 0
                    binding.customKeyboard.automata = HangulAutomata()
                } else {
                    ic.deleteSurroundingText(1, 0)
                    if (typedBuffer.isNotEmpty()) {
                            typedBuffer.setLength(typedBuffer.length - 1)
                        }
                }
            }
            KeyType.KOR -> setComposingTextWithoutUnderline(text)
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
            binding.customKeyboard.automata.deleteBuffer()
            binding.customKeyboard.composingLength = 0
            binding.customKeyboard.automata = HangulAutomata()
            currentInputConnection.finishComposingText()
        }

        previousSelStart = newSelStart





    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        applySystemTheme()
    }

    private fun applySystemTheme() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        updateKeyboardAppearance(!isNightMode)
    }

    private fun updateKeyboardAppearance(isDay: Boolean) {

        KeyLetter.isLightMode = isDay
        Log.i("Is LightMode : ", KeyLetter.isLightMode.toString())
        Log.i("Is LightMode : ", isDay.toString())
        binding.customKeyboard.initViews()
        setBackgroundBg()

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
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setupSuggestionBar()
        binding.customKeyboard.updateConfiguration()
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
        
        // 화살표 버튼 크기 조정
        val arrowSize = (suggestionBarHeight * 0.5f).toInt()
        val btnArrowDown = suggestionBar.findViewById<View>(R.id.btnArrowDown)
        val btnArrowUp = suggestionBar.findViewById<View>(R.id.btnArrowUp)
        
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
        
        // 텍스트 크기 조정
        val tvDone = suggestionBar.findViewById<android.widget.TextView>(R.id.tvDone)
        tvDone?.textSize = suggestionBarHeight * 0.35f / displayMetrics.density
    }
}