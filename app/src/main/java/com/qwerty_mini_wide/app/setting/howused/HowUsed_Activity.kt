package com.qwerty_mini_wide.app.setting.howused

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.qwerty_mini_wide.app.R
import com.qwerty_mini_wide.app.databinding.ActivityHowusedBinding

class HowUsed_Activity: AppCompatActivity() {
    private lateinit var binding: ActivityHowusedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHowusedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
        attribute()
    }

    fun bind(){
        Log.i("여기","여기오냐?")
        binding.btnBack2.setOnClickListener {
            finish()
        }
    }

    fun attribute(){
        val fullText = binding.txtNote.text
// 1) SpannableStringBuilder에 전체 문자열 담기
        val spannable = SpannableStringBuilder(fullText)

// 2) 색을 바꿀 구간의 시작·끝 인덱스 계산
        val start = fullText.indexOf("QWERTY mini")
        val end   = start + "QWERTY mini".length

// 3) ForegroundColorSpan을 붙여서 색 지정
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.primaryBlue)),  // 원하는 색
            start, end,
            SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.txtNote.text = spannable
    }

}