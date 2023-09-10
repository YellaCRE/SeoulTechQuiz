package com.mobileprogramming.seoultechquiz.quiz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.mobileprogramming.seoultechquiz.MainActivity
import com.mobileprogramming.seoultechquiz.R

class QuizSetActivity : AppCompatActivity() {
    private lateinit var quizSetList: ListView
    private lateinit var database: FirebaseDatabase
    private lateinit var quizzesRef: DatabaseReference
    private lateinit var selectedClass: String
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var quizSetAdapter: QuizSetAdapter
    private var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_set)
        // 학번을 키 값으로 받기
        val studentId = intent.getStringExtra("userId").toString()

        quizSetList = findViewById(R.id.quizSetList)
        database = FirebaseDatabase.getInstance()
        quizzesRef = database.reference.child("quizzes").child(studentId) // DB 경로
        selectedClass = intent.getStringExtra("selectedClass").toString()

        // 파이어베이스에서 Selected class와 일치하는 퀴즈셋 리스트 불러오기
        quizSetAdapter = QuizSetAdapter(this)
        quizSetList.adapter = quizSetAdapter

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val quizSets: MutableList<QuizSet> = mutableListOf()
                for (quizSnapshot in snapshot.children) {
                    val quizSetName = quizSnapshot.key
                    val word = quizSnapshot.child("word").getValue(String::class.java)
                    val definition = quizSnapshot.child("definition").getValue(String::class.java)
                    if (quizSetName != null && word != null && definition != null) {
                        val quizSet = QuizSet(quizSetName, word, definition)
                        quizSets.add(quizSet)
                    }
                }
                quizSetAdapter.setQuizSets(quizSets)
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        }

        // ValueEventListener를 클래스 목록에 연결하여 실시간으로 업데이트
        quizzesRef.child(selectedClass).addValueEventListener(valueEventListener)

        // 'Edit'버튼에 EditMode 토글 기능 설정
        val btnEdit: Button = findViewById(R.id.btnEdit)
        btnEdit.setOnClickListener {
            isEditMode = !isEditMode
            updateQuizSetList()
            updateEditButton()
        }

        // 아이템 클릭 시 기능 설정
        quizSetList.setOnItemClickListener { _, _, position, _ ->
            if (isEditMode) {
                // EditMode가 켜져 있으면 다이얼로그 형식으로 데이터 수정 가능
                val selectedQuizSet = quizSetAdapter.getItem(position)
                showEditDialog(selectedQuizSet, position)
            }
        }

        val btnPlay: Button = findViewById(R.id.btnPlay)
        btnPlay.setOnClickListener {
            val playIntent = Intent(this@QuizSetActivity, QuizPlayActivity::class.java)
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

    private fun updateQuizSetList() {
        quizSetAdapter.notifyDataSetChanged()
    }

    private fun updateEditButton() {
        val btnEdit: Button = findViewById(R.id.btnEdit)
        if (isEditMode) {
            btnEdit.text = "Stop Edit"
        } else {
            btnEdit.text = "Edit"
        }
    }

    private fun showEditDialog(quizSet: QuizSet, position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_quiz_set, null)
        val editTextWord = dialogView.findViewById<EditText>(R.id.editTextWord)
        val editTextDefinition = dialogView.findViewById<EditText>(R.id.editTextDefinition)

        // 퀴즈 문제와 답을 setText로 설정
        editTextWord.setText(quizSet.word)
        editTextDefinition.setText(quizSet.definition)

        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Edit Quiz Set")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val updatedWord = editTextWord.text.toString()
                val updatedDefinition = editTextDefinition.text.toString()
                val updatedQuizSet = QuizSet(quizSet.quizSetName, updatedWord, updatedDefinition)
                quizSetAdapter.updateQuizSet(position, updatedQuizSet)
                // 파이어베이스에 퀴즈셋 업데이트
                updateQuizSetInFirebase(updatedQuizSet)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Delete") { _, _ ->
                // 퀴즈셋 삭제
                deleteQuizSet(position)
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteQuizSet(position: Int) {
        val quizSet = quizSetAdapter.getItem(position)
        // 파이어베이스에서 퀴즈셋 제거
        quizSet.quizSetName?.let { deleteQuizSetInFirebase(it) }
        // 어댑터에서 퀴즈셋 제거
        quizSetAdapter.removeQuizSet(position)
    }

    private fun deleteQuizSetInFirebase(quizSetName: String) {
        quizzesRef.child(selectedClass)
            .child(quizSetName)
            .removeValue()
    }

    // 퀴즈셋 업데이트
    private fun updateQuizSetInFirebase(quizSet: QuizSet) {
        quizSet.quizSetName?.let {
            quizzesRef.child(selectedClass)
                .child(it)
                .child("word")
                .setValue(quizSet.word)
        }

        quizSet.quizSetName?.let {
            quizzesRef.child(selectedClass)
                .child(it)
                .child("definition")
                .setValue(quizSet.definition)
        }
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







