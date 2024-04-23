package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityAddTodoBinding
import com.example.todolist.db.Todo
import com.example.todolist.db.TodoDao

// ActivityAddTodoBinding : 레이아웃 내의 뷰 요소들을 바인딩하고 제어할 수 있는 메서드를 제공

class AddTodoActivity : AppCompatActivity() {
    // ActivityMainBinding : 레이아웃 내의 뷰 요소들을 바인딩하고 제어할 수 있는 메서드를 제공
    private lateinit var binding: ActivityAddTodoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 데이터베이스 클래스 객체 생성
        var dao = TodoDao()

        binding.btnComplete.setOnClickListener {

            // 사용자가 입력한 할 일의 제목을 가져옴
            val todoTitle = binding.edtTitle.text.toString()
            // 라디오 그룹에서 선택된 라디오 버튼의 ID를 가져옴

            // 선택된 라디오 버튼의 ID에 따라 중요도를 결정
            val todoImportance = when (binding.radioGroup.checkedRadioButtonId) {
                R.id.btn_high -> "1"
                R.id.btn_middle -> "2"
                R.id.btn_low -> "3"
                else -> ""
            }

            // 중요도가 설정되지 않았거나 할 일 제목이 비어있는 경우
            if (todoImportance == "" || todoTitle.isBlank()) {
                Toast.makeText(this, "모든 항목을 채워주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val todo = Todo("", todoTitle, todoImportance)
                dao.add(todo)?.addOnSuccessListener {
                    Toast.makeText(this, "할 일이 추가되었습니다..", Toast.LENGTH_SHORT).show()

                    // 목록으로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }?.addOnFailureListener {
                    Toast.makeText(this, "등록에 실패하였습니다.. : ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}