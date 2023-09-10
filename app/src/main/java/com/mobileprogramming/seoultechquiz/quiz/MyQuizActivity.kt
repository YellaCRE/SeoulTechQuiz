package com.mobileprogramming.seoultechquiz.quiz

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
import com.mobileprogramming.seoultechquiz.starter.LoginActivity

class MyQuizActivity : AppCompatActivity() {
    private lateinit var classList: ListView
    private lateinit var database: FirebaseDatabase
    private lateinit var quizzesRef: DatabaseReference
    private lateinit var classListAdapter: ArrayAdapter<String>
    private lateinit var valueEventListener: ValueEventListener
    private var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myquiz)

        // 퀴즈추가버튼 액션 설정
        val btnAddQuiz: Button = findViewById(R.id.btnAddQuiz)
        btnAddQuiz.setOnClickListener {
            goToQuizActivity()
        }
        // 학번을 키 값으로 받기
        val studentId = intent.getStringExtra("userId").toString()

        classList = findViewById(R.id.classList)
        database = FirebaseDatabase.getInstance()
        quizzesRef = database.reference.child("quizzes").child(studentId) // DB 경로

        // 선택된 클래스 목록을 Firebase에서 가져와서 리스트로 보여주기
        classListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        classList.adapter = classListAdapter

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                classListAdapter.clear()
                for (classSnapshot in snapshot.children) {
                    val className = classSnapshot.key
                    className?.let {
                        classListAdapter.add(it) // db에서 불러온 class 이름 추가
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        }

        // ValueEventListener를 클래스 목록에 연결하여 실시간으로 업데이트
        quizzesRef.addValueEventListener(valueEventListener)

        // 'Edit' 버튼 클릭 시 편집 모드 변경
        val btnEdit: Button = findViewById(R.id.btnEdit)
        btnEdit.setOnClickListener {
            isEditMode = !isEditMode
            updateClassList()
            updateEditButton()
        }

        // 클래스 목록 항목 클릭 이벤트 설정
        classList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedClass = classListAdapter.getItem(position)
            if (isEditMode) {
                deleteClass(selectedClass)
            } else {
                navigateToQuizSetActivity(selectedClass)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // ValueEventListener 제거
        quizzesRef.removeEventListener(valueEventListener)
    }

    private fun updateClassList() {
        classListAdapter.notifyDataSetChanged()
    }

    private fun deleteClass(className: String?) {
        className?.let {
            quizzesRef.child(it).removeValue()
        }
    }

    private fun updateEditButton() {
        val btnEdit: Button = findViewById(R.id.btnEdit)
        if (isEditMode) {
            btnEdit.text = "Stop Edit"
        } else {
            btnEdit.text = "Edit"
        }
    }

    private fun navigateToQuizSetActivity(selectedClass: String?) {
        selectedClass?.let {
            // 키 값 저장
            val navIntent = Intent(this, QuizSetActivity::class.java)
            val studentId = intent.getStringExtra("userId").toString()
            navIntent.putExtra("selectedClass", it)
            navIntent.putExtra("userId", studentId)
            startActivity(navIntent)
        }
    }

    // 퀴즈 추가 액티비티로 이동
    private fun goToQuizActivity() {
        val quizIntent = Intent(this, QuizActivity::class.java)
        val studentId = intent.getStringExtra("userId").toString()
        quizIntent.putExtra("userId", studentId)
        startActivity(quizIntent)
    }

    // 앱 바 출력
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







