package com.qwerty_mini_wide.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.qwerty_mini_wide.app.home.Home_Activity

class Splash_Activity : AppCompatActivity() {

    // Splash 화면 표시 시간 (밀리초)
    private val SPLASH_DELAY: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        // 지정 시간 후 MainActivity로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, Home_Activity::class.java))
            finish()  // 뒤로 가기 시 다시 돌아오지 않도록
        }, SPLASH_DELAY)
    }
}