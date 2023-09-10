package com.mobileprogramming.seoultechquiz.quiz

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.mobileprogramming.seoultechquiz.R


class QuizPlayActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var quizzesRef: DatabaseReference
    private lateinit var selectedClass: String
    private lateinit var valueEventListener: ValueEventListener
    private var currentQuizIndex: Int = 0
    private var quizSets: MutableList<QuizSet> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_play)
        // 학번을 키 값으로 받기
        val studentId = intent.getStringExtra("userId").toString()

        database = FirebaseDatabase.getInstance()
        quizzesRef = database.reference.child("quizzes").child(studentId)
        selectedClass = intent.getStringExtra("selectedClass").toString()

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                quizSets.clear()
                for (quizSnapshot in snapshot.children) {
                    val quizSetName = quizSnapshot.key
                    val word = quizSnapshot.child("word").getValue(String::class.java)
                    val definition = quizSnapshot.child("definition").getValue(String::class.java)
                    if (quizSetName != null && word != null && definition != null) {
                        val quizSet = QuizSet(quizSetName, word, definition)
                        quizSets.add(quizSet)
                    }
                }
                startQuiz()
            }

            override fun onCancelled(error: DatabaseError) {
               // 오류 처리
            }
        }

        // ValueEventListener를 클래스 목록에 연결하여 실시간으로 업데이트
        quizzesRef.child(selectedClass).addValueEventListener(valueEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()

        // ValueEventListener 제거
        quizzesRef.child(selectedClass).removeEventListener(valueEventListener)
    }

    private fun startQuiz() {
        if (quizSets.isEmpty()) {
            return
        }

        if (currentQuizIndex >= quizSets.size) {
            // 모든 퀴즈셋이 출력되면 종료
            finish()
            return
        }

        val quizSet = quizSets[currentQuizIndex]
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Quiz")

        if (quizSet.word != null && quizSet.definition != null) {
            // Word와 definition 모두 null이 아닐 때
            builder.setMessage("Word: ${quizSet.word}")
            val editTextDefinition = EditText(this)
            builder.setView(editTextDefinition)
            builder.setPositiveButton("Submit") { dialog, _ ->
                val userAnswer = editTextDefinition.text.toString().trim()
                if (userAnswer == quizSet.definition) {
                    showResultDialog("Correct!")
                } else {
                    showResultDialog("Incorrect!")
                }
                dialog.dismiss()
            }
        } else if (quizSet.word == null && quizSet.definition != null && quizSet.definition.contains("()")) {
            val definitionWithoutPlaceholder = quizSet.definition.replace(Regex("\\(.*?\\)"), "()")
            builder.setMessage("Definition: $definitionWithoutPlaceholder")
            val editTextDefinition = EditText(this)
            builder.setView(editTextDefinition)
            builder.setPositiveButton("Submit") { dialog, _ ->
                val userAnswer = editTextDefinition.text.toString().trim()
                val definitionWithAnswer = quizSet.definition.replaceFirst(Regex("\\(.*?\\)"), "($userAnswer)")
                if (userAnswer == definitionWithAnswer) {
                    showResultDialog("Correct!")
                } else {
                    showResultDialog("Incorrect!")
                }
                dialog.dismiss()
            }
        }

        builder.setOnCancelListener { dialog ->
            dialog.dismiss()
            finish()
        }
        builder.setCancelable(false)
        builder.show()

        currentQuizIndex++
    }

    private fun showResultDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Result")
        builder.setMessage(message)
        builder.setPositiveButton("Next Quiz") { dialog, _ ->
            dialog.dismiss()
            startQuiz()
        }
        builder.setOnCancelListener { dialog ->
            dialog.dismiss()
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }
}













