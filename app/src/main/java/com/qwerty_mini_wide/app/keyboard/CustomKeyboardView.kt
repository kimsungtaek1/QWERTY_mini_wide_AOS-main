package com.qwerty_mini_wide.app.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.qwerty_mini_wide.app.R
import com.qwerty_mini_wide.app.databinding.CustomKeyboardViewBinding
import com.qwerty_mini_wide.app.keyboard.model.CurrentLanguage
import com.qwerty_mini_wide.app.keyboard.model.KeyLetter
import com.qwerty_mini_wide.app.keyboard.model.KeyModel
import com.qwerty_mini_wide.app.keyboard.model.KeyType
import com.qwerty_mini_wide.app.keyboard.manager.HangulAutomata
import com.qwerty_mini_wide.app.keyboard.manager.HanjaManager
import com.qwerty_mini_wide.app.keyboard.manager.InputManager
import com.qwerty_mini_wide.app.keyboard.model.HanjaEntry
import com.qwerty_mini_wide.app.keyboard.service.CustomKeyBoard_Service
import com.qwerty_mini_wide.app.keyboard.viewholder.Hanja_Adapter
import java.io.BufferedReader
import java.io.InputStreamReader


class CustomKeyboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs),Hanja_Adapter.OnItemClickListener {




    private val binding: CustomKeyboardViewBinding
    var composingLength = 0
    var currentState:KeyType = KeyType.ENG
    var currentLanguage: CurrentLanguage = CurrentLanguage.ENG
    var automata = HangulAutomata()
    //var proxy: InputConnection? = null
    var touchSize:Int = 0
    private var keyPopupWindow: KeyPopupWindow? = null
    
    // Variables for tracking repeated key presses
    private var lastPressedKey: CustomKeyButton? = null
    private var lastPressTime = 0L

    // 더블탭 간격 기준(ms)
    private val DOUBLE_TAP_THRESHOLD = 300L

    // 마지막 Shift 클릭 시각
    private var lastShiftClickTime = 0L
    
    // Shift 키 터치 상태 추적

    // ↓ 이 부분을 클래스 바로 안쪽(다른 멤버 변수들 옆)에 추가
    private val deleteHandler = Handler(Looper.getMainLooper())
    private val deleteRunnable = object : Runnable {
        override fun run() {
            baseDelete()
           // listener?.onKey(KeyType.DELETE, "")
            // 50ms 간격으로 반복
            deleteHandler.postDelayed(this, 50)
        }
    }
    
    // Shift reset handler for multi-tap
    private val shiftResetHandler = Handler(Looper.getMainLooper())
    private val shiftResetRunnable = Runnable {
        if (currentState == KeyType.ENG) {
            resetShift()
        }
    }

    fun baseDelete(){
        if (composingLength > 0) {

            automata.deleteBuffer()
            val composed = automata.buffer.joinToString("")
            listener?.onKey(KeyType.KOR, composed)
            //proxy?.commitText(composed, 1)
            composingLength = composed.length
        } else {
            listener?.onKey(KeyType.DELETE, "")

        }
    }

    interface OnKeyboardActionListener {
        /** code: Unicode int or special KEYCODE_* */
        fun onKey(code: KeyType, text: String?)
    }

    private var listener: OnKeyboardActionListener? = null

    init {
        orientation = VERTICAL
        val view = inflate(context, R.layout.custom_keyboard_view, this)
        binding = CustomKeyboardViewBinding.bind(view)
        setupSuggestionBar()
        initViews()
        binding.hanjaRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.hanjaRecycler.adapter = Hanja_Adapter(this)

        HanjaManager.init(context)
        
        // Initialize popup window
        keyPopupWindow = KeyPopupWindow(context)
        

    }

    fun initViews(){
        setupKeys()
        if(currentState == KeyType.ENG) {
            setLetter(KeyLetter.getEngLetter())
            setFuntion(KeyLetter.getEngFunction())
        } else {
            setLetter(KeyLetter.getKorLetters())
            setFuntion(KeyLetter.getKorFunction())
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onItemClick(hanjaEntry: HanjaEntry) {

        Log.i("chnLastWord.count()",chnLastWord.count().toString())
        Log.i("chnLastWord",chnLastWord)
        for(i in 0..<chnLastWord.count()){
            listener?.onKey(KeyType.DELETE, "")
           // proxy?.deleteSurroundingText(1, 0)
        }
        listener?.onKey(KeyType.LETTER, hanjaEntry.hanja)
        //proxy?.commitText(hanjaEntry.hanja,1)
        (binding.hanjaRecycler.adapter as Hanja_Adapter).setitem(arrayListOf())
        automata.deleteBuffer()
        composingLength = 0
       // proxy?.finishComposingText()
        automata = HangulAutomata()

        hanjaLinearIsVisible(false)
    }

    // 마지막 단어 저장용
    var chnLastWord: String = ""

    fun chnTap() {
        // 1) 커서 앞 텍스트 모두 가져오기 (최대 1000자)

        val service = context as? CustomKeyBoard_Service
        var before = service?.currentInputConnection
            ?.getTextBeforeCursor(1000, 0)
            ?.toString() ?: ""

        // 2. 만약 정말 빈 문자열("", null 등)이라면, 자체 버퍼를 fallback으로 쓴다
        if (before.isEmpty()) {
            //val service = context as? CustomKeyBoard_Service
            before = service?.getTypedBuffer().orEmpty()
            Log.i("CustomKeyboardView", "fallback to typedBuffer: '$before'")
        }
        Log.i("Porxy",before)
        // 2) 공백·개행·구두점으로 분리
        val separators = Regex("[\\s\\p{Punct}]+")
        val tokens = before
            .split(separators)
            .filter { it.isNotEmpty() }

        // 마지막 토큰이 마지막 단어
        var lastWord = tokens.lastOrNull() ?: ""
        Log.i("lastWord",lastWord)
        if(lastWord != ""){
            lastWord = (lastWord[lastWord.count() - 1] ?: "").toString()
        }


        // 3) before에서 마지막 단어가 등장한 인덱스 찾기
        val idx = before.lastIndexOf(lastWord)
        if (idx != -1) {
            // 4) 시작 인덱스부터 끝까지 잘라내기
            val lastWordWithSpaces = before.substring(idx)
            // 이미 공백이 포함되어 있으면 뭔가 빠진 거니 리턴
            if (lastWordWithSpaces.contains(" ")) {
                return
            }
            Log.d("MyKeyboardService", ">>> '$lastWordWithSpaces'")
        }

        // 5) ViewModel에 한자 조회 결과 전달
        val entries = HanjaManager.entries(lastWord) ?: emptyList()
        if(entries.count() ==0){
            hanjaLinearIsVisible(false)
        }else{
            hanjaLinearIsVisible(true)
            (binding.hanjaRecycler.adapter as Hanja_Adapter).setitem(entries)
            // 6) 마지막 단어 저장
            chnLastWord = lastWord
            Log.d("MyKeyboardService", "word : $lastWord")
        }



    }

    fun setOnKeyboardActionListener(l: OnKeyboardActionListener) {
        listener = l
    }

    private fun setupSuggestionBar() {
        findViewById<ImageButton>(R.id.btnArrowDown).setOnClickListener {
            listener?.onKey(KeyType.LETTER, "ㅏ")
        }
        findViewById<ImageButton>(R.id.btnArrowUp).setOnClickListener {
           // listener?.onKey(CODE_NEXT, null)
        }
        findViewById<TextView>(R.id.tvDone).setOnClickListener {
           // listener?.onKey(CODE_DONE, null)
        }
    }

    fun setFuntion(fountions:List<KeyModel>){
        var count = 0
        for(i in 0 until binding.funtionLinear.childCount){
            if(binding.funtionLinear.getChildAt(i) is CustomKeyButton){
                val keyButon = binding.funtionLinear.getChildAt(i) as CustomKeyButton
                val service = context as? CustomKeyBoard_Service

                keyButon.setData(fountions[count],service?.actionId ?: 0)
                count++
            }
        }
    }

    @SuppressLint("ResourceType")
    fun resetShift(){
        for(i in 0 until binding.funtionLinear.childCount){
            if(binding.funtionLinear.getChildAt(i) is CustomKeyButton){
                val keyButon = binding.funtionLinear.getChildAt(i) as CustomKeyButton
                // Only reset ONSHIFT, not LOCKSHIFT
                if(keyButon.keyModel?.keyType == KeyType.ONSHIFT){
                    keyButon.setBackgroundDrawable(resources.getDrawable(keyButon.keyModel!!.backgroundColor))
                    keyButon.getIcImageView().setImageDrawable(resources.getDrawable(keyButon.keyModel!!.image))
                    keyButon.keyModel!!.keyType = KeyType.SHIFT
                    
                    // Reset keyboard layout only if it was ONSHIFT
                    if(currentLanguage == CurrentLanguage.KOR){
                        setLetter(KeyLetter.getKorLetters())
                    }else{
                        setLetter(KeyLetter.getEngLetter())
                    }
                }
            }
        }
    }

    fun setLetter(letters:List<List<KeyModel>>){

        var count = 0
        for(i in 0 until binding.firstLinear.childCount){
            if(binding.firstLinear.getChildAt(i) is CustomKeyButton){
                val keyButon = binding.firstLinear.getChildAt(i) as CustomKeyButton
                keyButon.setData(letters[0][count])
                count++

            }
        }

        count = 0
        for(i in 0 until binding.secondLinear.childCount){
            if(binding.secondLinear.getChildAt(i) is CustomKeyButton){
                val keyButon = binding.secondLinear.getChildAt(i) as CustomKeyButton
                keyButon.setData(letters[1][count])
                count++
            }
        }
    }

    fun hanjaLinearIsVisible(isVisible:Boolean){

        if(isVisible){
            binding.hanjaLinear.visibility = VISIBLE
        }else{
            binding.hanjaLinear.visibility = GONE
        }
    }

    fun Context.loadJsonFromAssets(fileName: String): String {
        assets.open(fileName).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                return reader.readText()
            }
        }
    }




    @SuppressLint("ResourceType", "ClickableViewAccessibility")
    private fun setupKeys() {


        // 뷰 트리 순회해서 CustomKeyButton 모두 찾기
        val keys = mutableListOf<CustomKeyButton>()
        fun traverse(v: View) {
            if (v is CustomKeyButton) keys += v
            else if (v is ViewGroup) {
                for (i in 0 until v.childCount) traverse(v.getChildAt(i))
            }
        }
        traverse(this)

        // 클릭 리스너 설정
        keys.forEach { key ->

            key.setOnLongClickListener {
                when {
                    key.keyModel?.keyType == KeyType.ENG ->{
                        Log.i("setOnLongClickListener_2","setOnLongClickListener")
                        chnTap()
                        commitText()
                        listener?.onKey(KeyType.CHN,"")
                    }
                }
                true
            }

            key.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Show popup for letter keys
                        if (key.keyModel?.keyType == KeyType.LETTER && shouldShowPopup(key)) {
                            showKeyPopup(key)
                        }
                        
                        // DELETE key long-press handling
                        if (key.keyModel?.keyType == KeyType.DELETE) {
                            baseDelete()
                            deleteHandler.postDelayed(deleteRunnable, 500)
                        }
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        // Hide popup
                        if (key.keyModel?.keyType == KeyType.LETTER) {
                            keyPopupWindow?.hide()
                        }
                        
                        // Stop delete repetition
                        if (key.keyModel?.keyType == KeyType.DELETE) {
                            deleteHandler.removeCallbacks(deleteRunnable)
                        }
                    }
                }
                // Don't consume touch event for letter keys
                key.keyModel?.keyType == KeyType.DELETE
            }


            key.setOnClickListener {
                hanjaLinearIsVisible(false)
                val icon = key.getIconRes()
                val main = key.getMainText()
                val keyModel = key.keyModel

                when{
                    keyModel!!.keyType == KeyType.ENG ->{
                        commitText()
                        currentState = KeyType.ENG
                       // binding.spaceEnter.visibility = GONE
                        currentLanguage = CurrentLanguage.ENG
                        setLetter(KeyLetter.getEngLetter())
                        setFuntion(KeyLetter.getEngFunction())
                        listener?.onKey(KeyType.ENG,"")
                    }

                    keyModel.keyType == KeyType.KOR ->{
                        commitText()
                        currentState = KeyType.KOR
                       // binding.spaceEnter.visibility = VISIBLE
                        currentLanguage = CurrentLanguage.KOR
                        setLetter(KeyLetter.getKorLetters())
                        setFuntion(KeyLetter.getKorFunction())
                        listener?.onKey(KeyType.KOR,"")
                    }

                    keyModel.keyType == KeyType.LETTER ->{
                        if(touchSize == 0){
                            tapLetter(key)
                        }
                        // Don't reset shift here - let tapLetter handle it
                    }

                    keyModel.keyType == KeyType.DELETE ->{

                        baseDelete()

                    }

                    keyModel.keyType == KeyType.NUMBER ->{
                        automata.deleteBuffer()
                        currentState = KeyType.NUMBER
                        //binding.spaceEnter.visibility = GONE
                        setLetter(KeyLetter.getNumberLetter(currentLanguage))
                        setFuntion(KeyLetter.getNumberFuntion(currentLanguage))
                        listener?.onKey(KeyType.NUMBER,"")
                    }

                    keyModel.keyType == KeyType.SPECIAL ->{
                        automata.deleteBuffer()
                        currentState = KeyType.SPECIAL
                       // binding.spaceEnter.visibility = GONE
                        setLetter(KeyLetter.getSpecialLetter(currentLanguage))
                        setFuntion(KeyLetter.getSpectialFuntion(currentLanguage))
                    }

                    keyModel.keyType == KeyType.SPACE ->{
                        commitText()
                        listener?.onKey(KeyType.SPACE, " ")
                       // proxy?.commitText(" ",1)
                    }

                    keyModel.keyType == KeyType.CHN ->{
                        chnTap()
                        commitText()
                        listener?.onKey(KeyType.CHN,"")


                    }

                    keyModel.keyType == KeyType.ONSHIFT ->{
                        val now = SystemClock.elapsedRealtime()
                        if (now - lastShiftClickTime < DOUBLE_TAP_THRESHOLD) {
                            // ─── 더블 클릭: Shift 잠금 토글 ───
                            keyModel.keyType = KeyType.LOCKSHIFT

                        } else {
                            key.setBackgroundDrawable(resources.getDrawable(keyModel.backgroundColor))
                            // key.setBackgroundColor(keyModel.backgroundColor)
                            key.getIcImageView().setImageDrawable(resources.getDrawable(keyModel.image))
                            keyModel.keyType = KeyType.SHIFT
                            if(currentLanguage == CurrentLanguage.KOR){
                                setLetter(KeyLetter.getKorLetters())
                            }else{
                                setLetter(KeyLetter.getEngLetter())
                            }
                        }


                       // setFuntion(KeyLetter.getKorFunction())
                    }

                    keyModel.keyType == KeyType.LOCKSHIFT ->{
                        key.setBackgroundDrawable(resources.getDrawable(keyModel.backgroundColor))
                        // key.setBackgroundColor(keyModel.backgroundColor)
                        key.getIcImageView().setImageDrawable(resources.getDrawable(keyModel.image))
                        keyModel.keyType = KeyType.SHIFT
                        if(currentLanguage == CurrentLanguage.KOR){
                            setLetter(KeyLetter.getKorLetters())
                        }else{
                            setLetter(KeyLetter.getEngLetter())
                        }
                    }

                    keyModel.keyType == KeyType.SHIFT ->{
                        key.setBackgroundDrawable(resources.getDrawable(keyModel.selectBackgroundColor))
                        key.getIcImageView()
                            .setImageDrawable(resources.getDrawable(keyModel.selectImage))
                        keyModel.keyType = KeyType.ONSHIFT
                        if (currentLanguage == CurrentLanguage.KOR) {
                            setLetter(KeyLetter.getShiftLetter())
                        } else {
                            setLetter(KeyLetter.getEngShiftLetter())
                        }
                        lastShiftClickTime = SystemClock.elapsedRealtime()
                    }

                    keyModel.keyType == KeyType.RETURN -> {
                        commitText() // 기존 조합 중인 글자 정리
                        listener?.onKey(KeyType.RETURN, "\n")
                    }


                }




            }
        }
    }

    fun commitText(){
        automata.deleteBuffer()
        composingLength = 0
        val service = context as? com.qwerty_mini_wide.app.keyboard.service.CustomKeyBoard_Service
        service?.currentInputConnection?.finishComposingText()
        automata = HangulAutomata()
    }

    private fun isShiftOn(): Boolean {
        for(i in 0 until binding.funtionLinear.childCount){
            if(binding.funtionLinear.getChildAt(i) is CustomKeyButton){
                val keyButton = binding.funtionLinear.getChildAt(i) as CustomKeyButton
                if(keyButton.keyModel?.keyType == KeyType.ONSHIFT || keyButton.keyModel?.keyType == KeyType.LOCKSHIFT){
                    return true
                }
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun tapLetter(keyButton: CustomKeyButton) {
        // base.currentState: KeyType, base.proxy: ProxyInterface?, base.automata: Automata, base.composingLength: Int
        val ch = InputManager.shared.handleTap(keyButton, currentState, isShiftOn())

        Log.d("Keyboard", "ch : $ch, isShiftOn: ${isShiftOn()}, tapCount: ${InputManager.shared.tapCount}")

        if (InputManager.shared.tapCount == 1) {


            if (currentState == KeyType.KOR) {

                // 2) 새로운 키 처리
                automata.hangulAutomata(ch)
                val composed = automata.buffer.joinToString("")
                Log.d("Keyboard", "composed : $composed")
                // 3) 새로 조합된 문자열 삽입
                listener?.onKey(KeyType.KOR, composed)
               // proxy?.commitText(composed,0)

                // 4) 삭제할 길이 갱신
                composingLength = composed.length
            } else {
                listener?.onKey(KeyType.LETTER, ch)
               // proxy?.commitText(ch,1)
            }

        } else {
            if (currentState == KeyType.KOR) {
                if(arrayOf("ㅅ","ㅗ","ㅏ","ㅜ","ㅓ","ㅜ").contains(keyButton.keyModel!!.mainText) ){
                            if(InputManager.shared.tapCount > 2){
                                InputManager.shared.tapCount = 1
                            }else{
                                automata.deleteBuffer()
                            }
                }else{
                    if(InputManager.shared.tapCount > 3){
                        InputManager.shared.tapCount = 1
                    }else{
                        automata.deleteBuffer()
                    }
                }

                // 2) 새로운 키 처리
                automata.hangulAutomata(ch)
                val composed = automata.buffer.joinToString("")
                listener?.onKey(KeyType.KOR, composed)
                // 3) 새로 조합된 문자열 삽입
                //proxy?.commitText(composed,1)

                // 4) 삭제할 길이 갱신
                composingLength = composed.length
            } else {

                if (InputManager.shared.tapCount > 1){
                    InputManager.shared.tapCount = 0
                }else{
                   automata.deleteBuffer()
                }
                listener?.onKey(KeyType.DELETE, "")
                listener?.onKey(KeyType.LETTER, ch)
             //   proxy?.deleteSurroundingText(1,0)
               // proxy?.commitText(ch,1)
            }
        }
        
        // Schedule shift reset after multi-tap timeout
        if (currentState == KeyType.ENG) {
            // Cancel any pending shift reset
            shiftResetHandler.removeCallbacks(shiftResetRunnable)
            // Schedule new shift reset after 300ms (multi-tap timeout)
            shiftResetHandler.postDelayed(shiftResetRunnable, 350)
        }
    }

    // 첫/두 번째 터치된 키 버튼을 저장할 변수
    private var firstTouchedKey: CustomKeyButton? = null
    private var secondTouchedKey: CustomKeyButton? = null
    private var firstTouchTime: Long = 0
    private val SIMULTANEOUS_THRESHOLD = 100L // 100ms

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val currentTime = System.currentTimeMillis()
                val newKey = findEnclosingKeyButton(
                    findChildUnder(this, ev.x.toInt(), ev.y.toInt())
                )
                
                // 이전 키가 있고 시간차가 100ms 이내라면 동시 입력으로 처리
                if (firstTouchedKey != null && newKey != null && 
                    currentTime - firstTouchTime <= SIMULTANEOUS_THRESHOLD) {
                    
                    // 동시 입력 처리
                    handleSimultaneousInput(firstTouchedKey!!, newKey)
                    
                    // 처리 후 초기화
                    firstTouchedKey = null
                    firstTouchTime = 0
                    return true
                } else {
                    // 새로운 첫 번째 키로 설정
                    firstTouchedKey = newKey
                    firstTouchTime = currentTime
                    touchSize = 0
                    return super.dispatchTouchEvent(ev)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                touchSize = ev.pointerCount
                if (ev.pointerCount >= 2) {
                    // 이 이벤트를 발생시킨 포인터의 인덱스
                    val idx = ev.actionIndex
                    val x = ev.getX(idx).toInt()
                    val y = ev.getY(idx).toInt()

                    secondTouchedKey = findEnclosingKeyButton(
                        findChildUnder(this, x, y)
                    )

                    // 첫 번째, 두 번째 키가 모두 잡혔으면 처리
                    firstTouchedKey?.let { first ->
                        secondTouchedKey?.let { second ->
                            handleSimultaneousInput(first, second)
                        }
                    }
                    return true
                }

            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /** 원래의 findChildUnder() 그대로 사용합니다 */
    private fun findChildUnder(parent: ViewGroup, x: Int, y: Int): View? {
        for (i in parent.childCount - 1 downTo 0) {
            val child = parent.getChildAt(i)
            // (1) 내부 뷰그룹부터 재귀
            if (child is ViewGroup) {
                val cx = x - child.left
                val cy = y - child.top
                findChildUnder(child, cx, cy)?.let { return it }
            }
            // (2) 자기 자신의 히트 박스 검사
            val rect = Rect().apply { child.getHitRect(this) }
            if (rect.contains(x, y)) {
                return child
            }
        }
        return null
    }

    /**
     * findChildUnder()로 찾은 뷰가 TextView든 ImageView든,
     * 그 조상 중 CustomKeyButton이 있으면 리턴. 없으면 null.
     */
    private fun findEnclosingKeyButton(view: View?): CustomKeyButton? {
        var v = view
        while (v != null && v !is CustomKeyButton) {
            val parent = v.parent
            v = if (parent is View) parent else null
        }
        return v as? CustomKeyButton
    }
    
    private fun handleSimultaneousInput(first: CustomKeyButton, second: CustomKeyButton) {
        Log.i(
            "Keyboard",
            "동시입력 - 첫번째: lt=${first.keyModel?.ltText}, rt=${first.keyModel?.rtText}, rb=${first.keyModel?.rbText} | " +
            "두번째: lt=${second.keyModel?.ltText}, rt=${second.keyModel?.rtText}, rb=${second.keyModel?.rbText}"
        )
        
        val touchedButtons = mutableListOf<CustomKeyButton>()
        touchedButtons.add(first)
        touchedButtons.add(second)
        
        val ch = InputManager.shared
            .handleSimultaneous(touchedButtons.take(2), currentLanguage) ?: ""
        val split = ch.split(",")
        Log.i("더블터치","result: $ch, currentLanguage: $currentLanguage, currentState: $currentState")
        
        if(ch.isEmpty()) {
            Log.w("더블터치", "Empty result from simultaneous input")
            return
        }
        
        if (currentState == KeyType.KOR) {
            for(i in 0..split.count() - 1){
                // 2) 새로운 키 처리
                automata.hangulAutomata(split[i])
                val composed = automata.buffer.joinToString("")
                Log.d("Keyboard", "composed : $composed")
                // 3) 새로 조합된 문자열 삽입
                listener?.onKey(KeyType.KOR, composed)
                // 4) 삭제할 길이 갱신
                composingLength = composed.length
            }
        } else if (currentState == KeyType.ENG) {
            // 영어 모드에서 동시 입력 처리
            Log.d("Keyboard", "ENG mode simultaneous: $ch")
            listener?.onKey(KeyType.LETTER, ch)
        } else {
            listener?.onKey(KeyType.LETTER, ch)
        }
    }
    
    fun updateMicrophoneState(isListening: Boolean) {
        // Microphone feature removed for now
    }
    
    private fun shouldShowPopup(key: CustomKeyButton): Boolean {
        // Don't show popup for keys with special types
        val keyType = key.keyModel?.keyType
        return keyType == KeyType.LETTER
    }
    
    private fun showKeyPopup(key: CustomKeyButton) {
        val keyModel = key.keyModel ?: return
        
        // Check if this is a repeated press of the same key
        val currentTime = System.currentTimeMillis()
        val isRepeatPress = lastPressedKey == key && (currentTime - lastPressTime) < DOUBLE_TAP_THRESHOLD
        
        // Update tracking variables
        lastPressedKey = key
        lastPressTime = currentTime
        
        // Calculate the actual character that will be input based on current state and rules
        val popupText = calculateActualInputChar(key, isRepeatPress)
        
        if (popupText.isNotEmpty()) {
            keyPopupWindow?.show(key, popupText)
        }
    }
    
    private fun calculateActualInputChar(key: CustomKeyButton, isRepeatPress: Boolean): String {
        val keyModel = key.keyModel ?: return ""
        
        // Simulate what InputManager.handleTap would return
        return when (currentState) {
            KeyType.KOR -> {
                when {
                    // Multi-tap handling for Korean
                    isRepeatPress -> {
                        when (keyModel.mainText) {
                            "ㅂ" -> if (InputManager.shared.tapCount % 3 == 1) "ㅃ" else "ㅂ"
                            "ㅈ" -> if (InputManager.shared.tapCount % 3 == 1) "ㅉ" else "ㅈ"
                            "ㄷ" -> if (InputManager.shared.tapCount % 3 == 1) "ㄸ" else "ㄷ"
                            "ㄱ" -> if (InputManager.shared.tapCount % 3 == 1) "ㄲ" else "ㄱ"
                            "ㅅ" -> "ㅆ"
                            "ㅗ", "ㅏ", "ㅜ", "ㅓ" -> keyModel.ltText.ifEmpty { keyModel.mainText }
                            else -> keyModel.mainText
                        }
                    }
                    else -> keyModel.mainText
                }
            }
            KeyType.ENG -> {
                val baseChar = when {
                    isRepeatPress && keyModel.rbText.isNotEmpty() -> keyModel.rbText
                    keyModel.ltText.isNotEmpty() -> keyModel.ltText
                    else -> keyModel.mainText
                }
                // Apply shift if active
                if (isShiftOn()) baseChar.uppercase() else baseChar
            }
            KeyType.NUMBER -> {
                when {
                    isRepeatPress && keyModel.rbText.isNotEmpty() -> keyModel.rbText
                    keyModel.mainText.isNotEmpty() -> keyModel.mainText
                    else -> keyModel.ltText
                }
            }
            KeyType.SPECIAL -> {
                when {
                    isRepeatPress && keyModel.rbText.isNotEmpty() -> keyModel.rbText
                    keyModel.ltText.isNotEmpty() -> keyModel.ltText
                    else -> keyModel.mainText
                }
            }
            else -> keyModel.mainText.ifEmpty { keyModel.ltText }
        }
    }
    
    fun release() {
        keyPopupWindow?.release()
        keyPopupWindow = null
    }


}