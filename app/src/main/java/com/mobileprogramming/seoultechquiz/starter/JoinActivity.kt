package com.mobileprogramming.seoultechquiz.starter

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobileprogramming.seoultechquiz.R
import com.mobileprogramming.seoultechquiz.databinding.ActivityJoinBinding

class JoinActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 파이어베이스 회원가입 권한
        auth = Firebase.auth
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)
        binding.joinButton.setOnClickListener {
            var isGoToJoin = true
            val userId = binding.idGuest.text.toString() + "@seoultech.ac.kr"
            val password = binding.passwordGuest.text.toString()
            val passwordCheck = binding.checkPassword.text.toString()
            val nextIntent = Intent(this, ProfileActivity::class.java)

            // 형식에 맞지 않는 데이터 입력 시, 에러메시지 출력
            if (userId.isEmpty()) {
                Toast.makeText(this, "학번을 입력해주세요.", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "password1을 입력해주세요.", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            if (passwordCheck.isEmpty()) {
                Toast.makeText(this, "password2을 입력해주세요.", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            if (password != passwordCheck) {
                Toast.makeText(this, "비밀번호를 똑같이 입력해주세요.", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            if (password.length < 6) {
                Toast.makeText(this, "비밀번호를 6자리 이상으로 입력해주세요.", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            // 회원가입 시도
            if (isGoToJoin) {
                auth.createUserWithEmailAndPassword(userId, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공 시, 팝업 메시지 출력 후 다음 액티비티로 이동
                            Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                            // 학번을 키 값으로 유지
                            nextIntent.putExtra("userId", binding.idGuest.text.toString())
                            startActivity(nextIntent)
                        } else {
                            // 회원가입 실패 시, 팝업 메시지 출력
                            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}