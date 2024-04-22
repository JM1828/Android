package com.example.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.databinding.ActivityAddTodoBinding
import com.example.todolist.db.AppDatabase
import com.example.todolist.db.TodoDao
import com.example.todolist.db.TodoEntity

// ActivityAddTodoBinding : 레이아웃 내의 뷰 요소들을 바인딩하고 제어할 수 있는 메서드를 제공

class AddTodoActivity : AppCompatActivity() {
    // ActivityMainBinding : 레이아웃 내의 뷰 요소들을 바인딩하고 제어할 수 있는 메서드를 제공
    private lateinit var binding : ActivityAddTodoBinding
    // AppDatabase 타입의 데이터베이스 객체
    lateinit var db : AppDatabase
    // TodoDao 타입의 데이터 액세스 객체
    lateinit var todoDao : TodoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // getInstance(this) 메서드는 AppDatabase 클래스의 싱글톤 인스턴스를 반환하며, 해당 인스턴스는 앱의 라이프사이클과 연결되어 있음
        db = AppDatabase.getInstance(this)!!
        // db 인스턴스에서 TodoDao(데이터 액세스 객체)를 가져와서 todoDao 변수에 할당
        todoDao = db.getTodoDao()

        // "btnComplete"로 식별되는 버튼에 대한 클릭 리스너를 설정하고, 클릭 시에 insertTodo() 메서드를 호출
        binding.btnComplete.setOnClickListener {
            insertTodo()
        }
    }

    // insertTodo()라는 메서드로, 새로운 할 일을 추가하는 작업을 수행
    private fun insertTodo() {
        // 사용자가 입력한 할 일의 제목을 가져옴
        val todoTitle = binding.edtTitle.text.toString()
        // 라디오 그룹에서 선택된 라디오 버튼의 ID를 가져옴
        var todoImportance = binding.radioGroup.checkedRadioButtonId
        // 중요도를 저장할 변수를 초기화
        var impData = 0;
        // 선택된 라디오 버튼의 ID에 따라 중요도를 결정
        when(todoImportance) {
            R.id.btn_high -> {
                impData = 1;
            }
            R.id.btn_middle -> {
                impData = 2;
            }
            R.id.btn_low -> {
                impData = 3;
            }
        }

        // 중요도가 설정되지 않았거나 할 일 제목이 비어있는 경우
        if (impData == 0 || todoTitle.isBlank()) {
            Toast.makeText(this, "모든 항목을 채워주세요.", Toast.LENGTH_SHORT).show()
        } else {
            Thread{
                // 데이터베이스에 사용자가 입력한 할 일을 추가하는 작업을 수행
                todoDao.insertTodo(TodoEntity(null, todoTitle, impData))
                // UI 스레드에서 할 일이 추가되었다는 알림을 사용자에게 보여주고, 현재 액티비티를 종료하는 작업을 수행
                runOnUiThread {
                    Toast.makeText(this, "할 일이 추가되었습니다..", Toast.LENGTH_SHORT).show()
                    finish()
                }
                // start 를 해주어야 쓰레드가 실행됨
            }.start()
        }
    }
}