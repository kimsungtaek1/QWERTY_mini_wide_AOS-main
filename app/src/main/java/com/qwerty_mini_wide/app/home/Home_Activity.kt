package com.qwerty_mini_wide.app.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.qwerty_mini_wide.app.databinding.ActivityHomeBinding
import com.qwerty_mini_wide.app.setting.Setting_Activity

class Home_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestMicrophonePermissionIfNeeded()

        bind()
    }

    fun bind(){
        Log.i("여기","여기오냐?")
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, Setting_Activity::class.java))

        }

        binding.btnGoSettings.setOnClickListener{
            baseContext.openInputMethodSettings()
        }


    }


    fun Context.openInputMethodSettings() {
        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
            // non-Activity context(예: Service)에서 실행할 땐 필요
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    companion object {
        private const val REQ_MICROPHONE_PERMISSION = 1001
    }

    
    private fun requestMicrophonePermissionIfNeeded() {
        val microphonePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
        if (microphonePermission != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청 이유를 설명하는 다이얼로그 표시
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                showMicrophonePermissionDialog()
            } else {
                // 처음 요청하는 경우 바로 권한 요청
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQ_MICROPHONE_PERMISSION
                )
            }
        }
    }
    
    private fun showMicrophonePermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("마이크 권한 필요")
            .setMessage("키보드의 음성 인식 기능을 사용하기 위해 마이크 권한이 필요합니다.")
            .setPositiveButton("허용") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQ_MICROPHONE_PERMISSION
                )
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "음성 인식 기능을 사용할 수 없습니다", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            REQ_MICROPHONE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Home_Activity", "Microphone permission granted")
                    Toast.makeText(this, "마이크 권한이 허용되었습니다. 이제 음성 인식 기능을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("Home_Activity", "Microphone permission denied")
                    Toast.makeText(this, "마이크 권한이 거부되었습니다. 음성 인식 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}