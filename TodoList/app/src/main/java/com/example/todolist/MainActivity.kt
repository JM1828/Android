package com.example.todolist

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.db.AppDatabase
import com.example.todolist.db.TodoDao
import com.example.todolist.db.TodoEntity

class MainActivity : AppCompatActivity(), OnItemLongClickListener {

    private lateinit var binding: ActivityMainBinding

    // AppDatabase 타입의 데이터베이스 객체
    private lateinit var db: AppDatabase

    // TodoDao 타입의 데이터 액세스 객체
    private lateinit var todoDao: TodoDao

    // todoList 변수는 TodoEntity 객체들의 목록을 보유하고 있으며, 이를 통해 앱에서 할 일 목록을 효과적으로 관리하고 조작할 수 있음
    private lateinit var todoList: ArrayList<TodoEntity>

    // adapter 변수는 TodoRecyclerViewAdapter 클래스의 객체를 참조하고 있으며, RecyclerView와 연결된 어댑터 역할을 수행
    private lateinit var adapter: TodoRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // AppDatabase 클래스의 싱글톤 인스턴스를 획득하여 변수 db에 할당
        db = AppDatabase.getInstance(this)!!
        // db 인스턴스에서 TodoDao(데이터 액세스 객체)를 가져와서 todoDao 변수에 할당
        todoDao = db.getTodoDao()

        // 할 일 목록을 불러오는 메서드
        getAllTodoList()

        // 버튼이 클릭될 때 AddTodoActivity로의 새로운 화면 전환을 위한 인텐트를 생성하고 실행
        binding.btnAddTodo.setOnClickListener {
            val intent = Intent(this, AddTodoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAllTodoList() {
        Thread {
            // 백그라운드 스레드에서 실행되는 코드 블록 안에서 todoDao.getAllTodo()를 호출하여 모든 Todo를 가져옴
            todoList = ArrayList(todoDao.getAllTodo())
            setRecyclerView()
            // start 를 해줘야 getAllTodoList 의 쓰레드가 실행이 됨
        }.start()
    }

    // setRecyclerView라는 이름의 메서드로, RecyclerView를 설정하고 화면에 표시하기 위한 작업을 수행
    private fun setRecyclerView() {
        // setRecyclerView 함수가 쓰레드에서 호출되고있기 떄문에(UI 에 나타나는 작업은 메인쓰레드 에서 수행되어야 함)
        runOnUiThread {
            // TodoRecyclerViewAdapter를 생성하고, todoList와 현재 활동(Activity)을 인자로 전달하여 어댑터를 초기화
            adapter = TodoRecyclerViewAdapter(todoList, this)
            // RecyclerView에 어댑터를 설정하여 RecyclerView가 어댑터로부터 데이터를 가져오도록 함
            binding.recyclerview.adapter = adapter
            // RecyclerView에 LinearLayoutManager를 설정하여 아이템을 세로로 배치하도록 함
            binding.recyclerview.layoutManager = LinearLayoutManager(this)
        }
    }

    // onRestart 메서드는 활동이 중단된 후에 재시작될 때 호출되는 메서드
    override fun onRestart() {
        // 부모 클래스의 onRestart 메서드를 호출하는 것을 의미
        super.onRestart()
        // etAllTodoList()는 활동이 다시 시작될 때 모든 할 일 목록을 다시 불러오는 작업을 수행하는 메서드
        getAllTodoList()
    }

    // 아이템을 길게 눌렀을 때 해당 아이템을 삭제할지 여부를 사용자에게 묻는 다이얼로그를 표시하는 작업을 수행
    override fun onLongClick(position: Int) {
        // AlertDialog를 생성하기 위한 빌더 객체를 초기화
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alert_title))
        builder.setMessage(getString(R.string.alert_message))
        builder.setNegativeButton(getString(R.string.alert_no), null)
        builder.setPositiveButton(getString(R.string.alert_yes),
            // object 키워드를 사용하여 DialogInterface.OnClickListener를 익명 객체로 생성하고, 해당 객체 안에서 "예" 버튼이 클릭되었을 때의 동작을 정의
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, p1: Int) {
                    deleteTodo(position)
                }
            })
        // 설정한 내용대로 다이얼로그를 표시
        builder.show()
    }

    // 할 일 목록에서 특정 위치의 할 일을 삭제하는 작업을 수행하는 메서드
    private fun deleteTodo(position: Int) {
        Thread {
            // 데이터베이스에서 해당 위치의 할 일을 삭제하는 작업을 수행
            todoDao.deleteTodo(todoList[position])
            // 할 일 목록에서 해당 위치의 할 일을 제거
            todoList.removeAt(position)
            // UI 스레드에서 어댑터에 변경 사항을 알리고, 사용자에게 "삭제되었습니다."라는 짧은 시간동안 보여줄 알림을 생성
            runOnUiThread {
                // 어댑터에 데이터가 변경되었음을 알리고, RecyclerView를 새로고침하여 변경된 데이터를 반영
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }
}