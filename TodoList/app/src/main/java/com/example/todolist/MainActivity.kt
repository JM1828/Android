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

    // ActivityMainBinding : 레이아웃 내의 뷰 요소들을 바인딩하고 제어할 수 있는 메서드를 제공
    private lateinit var binding : ActivityMainBinding

    // AppDatabase 타입의 데이터베이스 객체
    private lateinit var db : AppDatabase
    // TodoDao 타입의 데이터 액세스 객체
    private lateinit var todoDao : TodoDao
    // todoList 변수는 TodoEntity 객체들의 목록을 보유하고 있으며, 이를 통해 앱에서 할 일 목록을 효과적으로 관리하고 조작할 수 있음
    private lateinit var todoList : ArrayList<TodoEntity>
    // adapter 변수는 TodoRecyclerViewAdapter 클래스의 객체를 참조하고 있으며, RecyclerView와 연결된 어댑터 역할을 수행
    // 이를 통해 앱에서 할 일 목록을 효과적으로 화면에 표시하고 관리할 수 있음
    private lateinit var adapter : TodoRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ActivityMainBinding 클래스의 inflate 메서드를 사용하여 액티비티의 레이아웃을 인플레이트(부풀리다)하는 과정을 나타냄
        // 이 과정은 XML 레이아웃 파일을 바인딩 클래스를 통해 메모리에 로드하는 것을 의미
        binding = ActivityMainBinding.inflate(layoutInflater)
        // setContentView 메서드를 사용하여 액티비티의 레이아웃으로 binding.root를 설정
        // binding.root는 해당 레이아웃의 최상위 뷰를 나타냄
        setContentView(binding.root)

        // AppDatabase 클래스의 싱글톤 인스턴스를 획득하여 변수 db에 할당
        // getInstance(this) 메서드는 AppDatabase 클래스의 싱글톤 인스턴스를 반환하며, 해당 인스턴스는 앱의 라이프사이클과 연결되어 있음
        // 느낌표(!!)는 null이 아님을 확신하는 연산자로, 이 경우에는 반환된 값이 null이 아님을 가정하고 해당 값을 db 변수에 할당합
        db = AppDatabase.getInstance(this)!!
        // db 인스턴스에서 TodoDao(데이터 액세스 객체)를 가져와서 todoDao 변수에 할당
        todoDao = db.getTodoDao()

        // 할 일 목록을 불러오는 메서드
        getAllTodoList()

        // "btnAddTodo"로 식별되는 버튼에 대한 클릭 리스너를 설정하는 부분
        binding.btnAddTodo.setOnClickListener {
            // 버튼이 클릭될 때 AddTodoActivity로의 새로운 화면 전환을 위한 인텐트를 생성하고 실행
            val intent = Intent(this, AddTodoActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
        // runOnUiThread 를 통해 메인쓰레드에서 실행 되게 해주도록 함
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
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        // 다이얼로그의 제목을 설정
        builder.setTitle(getString(R.string.alert_title))
        // 다이얼로그의 메시지를 설정
        builder.setMessage(getString(R.string.alert_message))
        // 다이얼로그에 "아니요" 버튼을 추가하고, 클릭 시 아무 동작도 수행하지 않도록 함
        builder.setNegativeButton(getString(R.string.alert_no), null)
        // 다이얼로그에 "예" 버튼을 추가하고, 클릭 시에는 해당 아이템을 삭제하는 동작을 수행
        builder.setPositiveButton(getString(R.string.alert_yes),
            // object 키워드를 사용하여 DialogInterface.OnClickListener를 익명 객체로 생성하고, 해당 객체 안에서 "예" 버튼이 클릭되었을 때의 동작을 정의
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, p1: Int) {
                    // 사용자가 "예"를 선택한 경우, 현재 위치에 해당하는 할 일을 삭제하는 작업을 수행
                    deleteTodo(position)
                }
            })
        // 설정한 내용대로 다이얼로그를 표시
        builder.show()
    }

    // 할 일 목록에서 특정 위치의 할 일을 삭제하는 작업을 수행하는 메서드
    private fun deleteTodo(position: Int) {
        Thread{
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