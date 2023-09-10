package com.mobileprogramming.seoultechquiz.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.*
import com.mobileprogramming.seoultechquiz.R
import com.mobileprogramming.seoultechquiz.ScanActivity

class QuizActivity : AppCompatActivity() {
    private lateinit var quizSetLayout: LinearLayout
    private lateinit var database: FirebaseDatabase
    private lateinit var quizzesRef: DatabaseReference
    private lateinit var selectedClass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        // 학번을 키 값으로 받기
        val studentId = intent.getStringExtra("userId").toString()

        quizSetLayout = findViewById(R.id.quizSetLayout)
        database = FirebaseDatabase.getInstance()
        quizzesRef = database.reference.child("quizzes").child(studentId) // DB 경로
        selectedClass = findViewById<EditText>(R.id.selectedClass).text.toString()
    }
    // 스캔버튼 클릭 시, 스캔 액티비티로 이동
    fun scan(view: View) {
        val scanIntent = Intent(this, ScanActivity::class.java)
        startActivity(scanIntent)
    }
    // 퀴즈 작성란 생성
    fun addQuiz(view: View) {
        val inflater = LayoutInflater.from(this)
        val quizView = inflater.inflate(R.layout.quiz_item, null)

        quizSetLayout.addView(quizView)
    }
    // 퀴즈 생성 기능
    fun createQuizSet(view: View) {
        val selectedClass = findViewById<EditText>(R.id.selectedClass).text.toString()
        val quizSet = ArrayList<Quiz>()
        // 퀴즈 생성
        for (i in 0 until quizSetLayout.childCount) {
            val quizItem = quizSetLayout.getChildAt(i)
            val word = quizItem.findViewById<EditText>(R.id.word).text.toString()
            val definition = quizItem.findViewById<EditText>(R.id.definition).text.toString()

            val quiz = Quiz(word, definition)
            quizSet.add(quiz)
        }

        // 기존의 퀴즈셋을 가져옴
        val classRef = quizzesRef.child(selectedClass)
        classRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingQuizSet = snapshot.getValue(object : GenericTypeIndicator<List<Quiz>>() {})
                val mergedQuizSet = existingQuizSet?.plus(quizSet) ?: quizSet
                classRef.setValue(mergedQuizSet)
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })

        // 예시로 저장된 퀴즈 세트 확인
        classRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val savedQuizSet = snapshot.getValue(object : GenericTypeIndicator<List<Quiz>>() {})
                    savedQuizSet?.forEach { quiz ->
                        println("Word: ${quiz.word}, Definition: ${quiz.definition}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })

        finish()
    }

    // 퀴즈 제거
    fun deleteQuiz(view: View) {
        if (quizSetLayout.childCount > 0) {
            quizSetLayout.removeViewAt(quizSetLayout.childCount - 1)
        }
    }
}





