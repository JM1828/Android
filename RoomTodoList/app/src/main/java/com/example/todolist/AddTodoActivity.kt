package com.example.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityAddTodoBinding
import com.example.todolist.db.AppDatabase
import com.example.todolist.db.TodoDao
import com.example.todolist.db.TodoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ActivityAddTodoBinding : 레이아웃 내의 뷰 요소들을 바인딩하고 제어할 수 있는 메서드를 제공

class AddTodoActivity : AppCompatActivity() {
    // ActivityMainBinding : 레이아웃 내의 뷰 요소들을 바인딩하고 제어할 수 있는 메서드를 제공
    private lateinit var binding: ActivityAddTodoBinding

    // AppDatabase 타입의 데이터베이스 객체
    lateinit var db: AppDatabase

    // TodoDao 타입의 데이터 액세스 객체
    lateinit var todoDao: TodoDao

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
            // 코루틴을 사용하여 insertTodo 메서드를 호출
            // 코루틴은 비동기 프로그래밍을 위한 Kotlin의 기능 중 하나이다
            // 코루틴을 사용하면 콜백이나 복잡한 스레드 처리를 사용하지 않고도 비동기 코드를 작성할 수 있으며, 코드의 가독성과 유지보수가 향상된다
            // 또한 안드로이드에서는 UI 업데이트와 같은 작업을 간편하게 처리할 수 있도록 지원하고 있다.
            CoroutineScope(Dispatchers.IO).launch {
                insertTodo()
            }
        }
    }

    // insertTodo()라는 메서드로, 새로운 할 일을 추가하는 작업을 수행
    private suspend fun insertTodo() {
        // 사용자가 입력한 할 일의 제목을 가져옴
        val todoTitle = binding.edtTitle.text.toString()
        // 라디오 그룹에서 선택된 라디오 버튼의 ID를 가져옴
        var todoImportance = when (binding.radioGroup.checkedRadioButtonId) {
            R.id.btn_high -> "1"
            R.id.btn_middle -> "2"
            R.id.btn_low -> "3"
            else -> ""
        }

        // 중요도가 설정되지 않았거나 할 일 제목이 비어있는 경우
        if (todoImportance == "" || todoTitle.isBlank()) {
            // withContext(Dispatchers.Main)을 사용하면 UI와 상호작용하는 작업을 수행할 수 있음
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddTodoActivity, "모든 항목을 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        } else {

            // 데이터베이스에 사용자가 입력한 할 일을 추가하는 작업을 수행
            todoDao.insertTodo(TodoEntity(null, todoTitle, todoImportance))
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddTodoActivity, "할 일이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}