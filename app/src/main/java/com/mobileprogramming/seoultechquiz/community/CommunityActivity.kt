package com.mobileprogramming.seoultechquiz.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.mobileprogramming.seoultechquiz.MainActivity
import com.mobileprogramming.seoultechquiz.R
import com.mobileprogramming.seoultechquiz.quiz.QuizSetActivity

class CommunityActivity : AppCompatActivity() {
    private lateinit var classList: ListView
    private lateinit var database: FirebaseDatabase
    private lateinit var quizzesRef: DatabaseReference
    private lateinit var classListAdapter: ArrayAdapter<String>
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        classList = findViewById(R.id.classList)
        database = FirebaseDatabase.getInstance()
        quizzesRef = database.reference.child("quizzes") // DB 경로

        // 선택된 클래스 목록을 Firebase에서 가져와서 리스트로 보여주기
        classListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        classList.adapter = classListAdapter

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                classListAdapter.clear()
                for (idSnapshot in snapshot.children) {
                    for (classSnapshot in idSnapshot.children) {
                        val className = classSnapshot.key
                        className?.let {
                            classListAdapter.add(it+" by ${idSnapshot.key}")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        }

        // ValueEventListener를 클래스 목록에 연결하여 실시간으로 업데이트
        quizzesRef.addValueEventListener(valueEventListener)

        // 클래스 목록 항목 클릭 이벤트 설정
        classList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedClass = classListAdapter.getItem(position)?.split(" by ")?.get(0)
            val selectedId = classListAdapter.getItem(position)?.split(" by ")?.get(1)
            navigateToQuizSetActivity(selectedClass, selectedId) // 각각의 DB에 접근하기 위해 작성자의 학번이 키 값으로 필요
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // ValueEventListener 제거
        quizzesRef.removeEventListener(valueEventListener)
    }

    private fun navigateToQuizSetActivity(selectedClass: String?, selectedId: String?) {
        selectedClass?.let {
            val navIntent = Intent(this, CommunitySetActivity::class.java)
            navIntent.putExtra("selectedClass", it)
            navIntent.putExtra("selectedId", selectedId)
            //앱바를 위한 학번 정보
            val studentId = intent.getStringExtra("userId").toString()
            navIntent.putExtra("userId", studentId)

            startActivity(navIntent)
        }
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
                val nextIntent = Intent(this, MainActivity::class.java) // 메인 화면 이동
                val studentId = intent.getStringExtra("userId").toString()
                nextIntent.putExtra("userId", studentId) // 메인 화면에 이름과 전공을 불러오기 위한 학번
                startActivity(nextIntent)
                true
            }
            else -> false
        }
    }
}







