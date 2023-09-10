package com.mobileprogramming.seoultechquiz.starter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 스플래시 화면 출력 후 로그인 액티비티로 이동
        val nextIntent = Intent(this, LoginActivity::class.java)
        startActivity(nextIntent)
    }
}