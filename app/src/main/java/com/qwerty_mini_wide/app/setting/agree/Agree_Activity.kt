package com.qwerty_mini_wide.app.setting.agree

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.qwerty_mini_wide.app.databinding.ActivityAgreeBinding

class Agree_Activity: AppCompatActivity() {
    private lateinit var binding: ActivityAgreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAgreeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
    }

    fun bind(){
        Log.i("here","are you coming here?")
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}