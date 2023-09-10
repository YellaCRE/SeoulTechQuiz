package com.mobileprogramming.seoultechquiz.community

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.mobileprogramming.seoultechquiz.MainActivity
import com.mobileprogramming.seoultechquiz.R
import com.mobileprogramming.seoultechquiz.quiz.QuizPlayActivity

class CommunitySetActivity : AppCompatActivity() {
    private lateinit var communitySetList: ListView
    private lateinit var database: FirebaseDatabase
    private lateinit var quizzesRef: DatabaseReference
    private lateinit var selectedClass: String
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var communitySetAdapter: CommunitySetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_set)

        communitySetList = findViewById(R.id.communitySetList)
        database = FirebaseDatabase.getInstance()
        quizzesRef = database.reference.child("quizzes") // DB 경로
        selectedClass = intent.getStringExtra("selectedClass").toString()

        // 파이어베이스에서 Selected class와 일치하는 퀴즈셋 리스트 불러오기
        communitySetAdapter = CommunitySetAdapter(this)
        communitySetList.adapter = communitySetAdapter

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val communitySets : MutableList<CommunitySet> = mutableListOf()
                for (quizSnapshot in snapshot.children) {
                    val quizSetName = quizSnapshot.key
                    val word = quizSnapshot.child("word").getValue(String::class.java)
                    val definition = quizSnapshot.child("definition").getValue(String::class.java)
                    if (quizSetName != null && word != null && definition != null) {
                        val communitySet = CommunitySet(quizSetName, word, definition)
                        communitySets.add(communitySet)
                    }
                }
                communitySetAdapter.setQuizSets(communitySets)
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
           }
        }

        // ValueEventListener를 클래스 목록에 연결하여 실시간으로 업데이트
        val studentId = intent.getStringExtra("selectedId")
        // 작성자의 학번을 받아옴으로써 다른 사용자들이 퀴즈셋에 접근가능
        quizzesRef.child(studentId!!).child(selectedClass).addValueEventListener(valueEventListener)

        val btnPlay: Button = findViewById(R.id.btnPlay)
        btnPlay.setOnClickListener {
            val playIntent = Intent(this@CommunitySetActivity, QuizPlayActivity::class.java)
            playIntent.putExtra("selectedClass", selectedClass)
            playIntent.putExtra("userId", studentId)
            startActivity(playIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // ValueEventListener 제거
        quizzesRef.child(selectedClass).removeEventListener(valueEventListener)
    }

    // 앱 바 생성
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.top_nav_menu, menu)
        return true
    }

    // 앱 바 기능 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.homeKey -> {
                val nextIntent = Intent(this, MainActivity::class.java) // 메인 화면으로 이동
                val studentId = intent.getStringExtra("userId").toString()
                nextIntent.putExtra("userId", studentId) // 메인 화면에 이름과 전공을 불러오기 위한 학번
                startActivity(nextIntent)
                true
            }
            else -> false
        }
    }
}







