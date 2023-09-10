package com.mobileprogramming.seoultechquiz.starter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mobileprogramming.seoultechquiz.MainActivity
import com.mobileprogramming.seoultechquiz.R
import com.mobileprogramming.seoultechquiz.databinding.ActivityJoinProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityJoinProfileBinding
    private val myRef = Firebase.database.reference // 파이어베이스 DB 호출

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_profile)

        // 데이터 저장 함수
        fun writeNewUser(userId: String, name: String, major: String) {
            val user = User(userId, name, major)
            myRef.child("users").child(userId).setValue(user)
        }

        val nextIntent = Intent(this, MainActivity::class.java)
        val studentId = intent.getStringExtra("userId").toString()

        // 학번을 키 값으로 이름,전공 DB에 저장
        binding.saveButton.setOnClickListener {
            writeNewUser(studentId, binding.name.text.toString(), binding.major.text.toString())
            nextIntent.putExtra("userId", studentId)
            startActivity(nextIntent)
        }
    }
}
