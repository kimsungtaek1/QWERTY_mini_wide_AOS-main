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
        Log.i("here","are you coming here?")
        binding.btnBack2.setOnClickListener {
            finish()
        }
    }

    fun attribute(){
        val fullText = binding.txtInstructions?.text ?: return
// 1) Put the entire string in SpannableStringBuilder
        val spannable = SpannableStringBuilder(fullText)

// 2) Calculate start and end indices of the section to change color
        val searchText = "QWERTY Mini English Wide"
        val start = fullText.indexOf(searchText)
        if (start != -1) {
            val end = start + searchText.length

// 3) Apply ForegroundColorSpan to set the color
            spannable.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.primaryBlue)),  // desired color
                start, end,
                SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.txtInstructions?.text = spannable
    }

}