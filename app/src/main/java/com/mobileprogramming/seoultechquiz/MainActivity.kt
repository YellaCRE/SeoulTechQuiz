package com.mobileprogramming.seoultechquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mobileprogramming.seoultechquiz.community.CommunityActivity
import com.mobileprogramming.seoultechquiz.databinding.ActivityMainBinding
import com.mobileprogramming.seoultechquiz.quiz.MyQuizActivity
import com.mobileprogramming.seoultechquiz.starter.LoginActivity
import com.mobileprogramming.seoultechquiz.starter.ProfileActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val myRef = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // 학번을 키 값으로 받기
        val studentId = intent.getStringExtra("userId").toString()

        // 메인화면에 표시되는 이름, 전공 불러오기
        myRef.child("users").child(studentId).child("username").get().addOnSuccessListener {
            var name = "${it.value}"
            if (name == ""){ binding.nameCode.text ="익명 ($studentId)" } // 이름 입력을 안 했을 때 처리
            else{ binding.nameCode.text = "$name ($studentId)" }
        }.addOnFailureListener{
            binding.nameCode.text = "익명 ($studentId)"
        }

        myRef.child("users").child(studentId).child("major").get().addOnSuccessListener {
            var major = "${it.value}"
            if (major == ""){ binding.major.text = "서울과학기술대학교" } // 전공 입력을 안 했을 때 처리
            else{ binding.major.text = "$major" }
        }.addOnFailureListener{
            binding.major.text = "서울과학기술대학교"    //예외처리
        }

        // 로그아웃
        val logoutIntent = Intent(this, LoginActivity::class.java)
        binding.logoutButton.setOnClickListener {
            startActivity(logoutIntent)
        }
        // 마이페이지
        val profileIntent = Intent(this, ProfileActivity::class.java)
        binding.mypageButton.setOnClickListener {
            profileIntent.putExtra("userId", studentId)
            startActivity(profileIntent)
        }
        // 퀴즈 이동
        val quizIntent = Intent(this, MyQuizActivity::class.java)
        binding.quizButton.setOnClickListener {
            quizIntent.putExtra("userId", studentId)
            startActivity(quizIntent)
        }
        // 커뮤니티 이동
        val communityIntent = Intent(this, CommunityActivity::class.java)
        binding.communityButton.setOnClickListener {
            communityIntent.putExtra("userId", studentId)
            startActivity(communityIntent)
        }
        // 문서스캔 이동
        val scanIntent = Intent(this, ScanActivity::class.java)
        binding.scanButton.setOnClickListener {
            startActivity(scanIntent)
        }
    }
}