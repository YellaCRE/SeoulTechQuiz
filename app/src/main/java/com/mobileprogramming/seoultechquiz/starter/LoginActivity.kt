package com.mobileprogramming.seoultechquiz.starter

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobileprogramming.seoultechquiz.MainActivity
import com.mobileprogramming.seoultechquiz.R
import com.mobileprogramming.seoultechquiz.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val nextIntent = Intent(this, MainActivity::class.java)

        // 로그인
        binding.loginButton.setOnClickListener {
            val id = binding.id.text.toString() + "@seoultech.ac.kr" // 입력 받은 학번을 이메일 형식으로 변환
            val password = binding.password.text.toString()
            auth.signInWithEmailAndPassword(id, password) // 파이어베이스에 로그인 요청
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공 시, 로그인 성공 팝업 메시지 출력
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                        nextIntent.putExtra("userId", binding.id.text.toString())
                        startActivity(nextIntent)
                    } else {
                        // 로그인 실패 시, 로그인 실패 팝업 메시지 출력
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // 회원가입
        binding.guestButton.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java) // 회원가입 액티비티로 이동
            startActivity(intent)
        }
    }
}