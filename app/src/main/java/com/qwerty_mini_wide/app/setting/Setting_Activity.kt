package com.qwerty_mini_wide.app.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.qwerty_mini_wide.app.R
import com.qwerty_mini_wide.app.databinding.ActivitySettingBinding
import com.qwerty_mini_wide.app.keyboard.CustomKeyBoard_Activity
import com.qwerty_mini_wide.app.setting.agree.Agree_Activity
import com.qwerty_mini_wide.app.setting.howused.HowUsed_Activity

class Setting_Activity  : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
    }

    fun  bind(){
        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnHowTo.setOnClickListener{
            startActivity(Intent(this, HowUsed_Activity::class.java))
        }

        binding.btnPreview.setOnClickListener{
            startActivity(Intent(this, CustomKeyBoard_Activity::class.java))
        }

        binding.itemTerms.setOnClickListener{
            startActivity(Intent(this, Agree_Activity::class.java))
        }

        binding.itemShare.setOnClickListener{
            baseContext.shareAppLink()
        }

        binding.itemSystem.setOnClickListener{
            baseContext.openGeneralSettings()
        }
    }

    fun Context.shareAppLink() {
        val appPackageName = packageName
        val playStoreUrl = "https://play.google.com/store/apps/details?id=$appPackageName"

        // 1) 공유용 Intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            putExtra(Intent.EXTRA_TEXT, "Share QWERTY mini wide app!\n\n$playStoreUrl")
        }

        // 2) Chooser Intent에 NEW_TASK 플래그 추가
        val chooser = Intent.createChooser(shareIntent, "Share App").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // 3) 실행
        startActivity(chooser)
    }

    fun Context.openGeneralSettings() {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}