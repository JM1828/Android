package com.example.todolist

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.db.Todo
import com.example.todolist.db.TodoAdapter
import com.example.todolist.db.TodoDao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // todoList 변수는 TodoEntity 객체들의 목록을 보유하고 있으며, 이를 통해 앱에서 할 일 목록을 효과적으로 관리하고 조작할 수 있음
    private lateinit var todoList: ArrayList<Todo>

    // adapter 변수는 TodoRecyclerViewAdapter 클래스의 객체를 참조하고 있으며, RecyclerView와 연결된 어댑터 역할을 수행
    private lateinit var adapter: TodoAdapter

    // 데이터베이스 클래스 객체 생성
    lateinit var dao: TodoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // todoList 초기화
        todoList = ArrayList()

        // dao 초기화
        dao = TodoDao()

        // adapter 초기화
        adapter = TodoAdapter(this, todoList)

        // recyclerView 초기화
        // RecyclerView에 어댑터를 설정하여 RecyclerView가 어댑터로부터 데이터를 가져오도록 함
        binding.recyclerview.adapter = adapter
        // RecyclerView에 LinearLayoutManager를 설정하여 아이템을 세로로 배치하도록 함
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        // 할 일 목록을 불러오는 메서드
        getAllTodoList()

        // 할 일 목록 삭제 기능
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                // 해당 위치 값 변수에 담기
                val position = viewHolder.bindingAdapterPosition

                when(direction) {
                    ItemTouchHelper.LEFT -> {
                        val key = todoList[position].id

                        dao.todoDelete(key).addOnSuccessListener {// 성공 이벤트
                            Toast.makeText(this@MainActivity, "삭제 성공", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {// 실패 이벤트
                            Toast.makeText(this@MainActivity, "삭제 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                // 스와이프 꾸미기
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(Color.RED)
                    .addSwipeLeftActionIcon(R.drawable.icon_delete)
                    .addSwipeLeftLabel("삭제")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(binding.recyclerview)

        // 할 일 추가하기 버튼 클릭시 AddTodoActivity 로 이동
        binding.btnAddTodo.setOnClickListener {
            val intent = Intent(this, AddTodoActivity::class.java)
            startActivity(intent)
        }
    } // onCreate()

    private fun getAllTodoList() {
        dao.getTodoList()?.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                // 리스트 초기화
                todoList.clear()

                for (dataSnapshot in snapshot.children) {

                    val todo = dataSnapshot.getValue(Todo::class.java)

                    // 키값 가져오기
                    val key = dataSnapshot.key

                    // 사용자 정보에 키 값 담기
                    todo?.id = key.toString()

                    // 리스트에 담기
                    if (todo != null) {
                        todoList.add(todo)
                    }
                }

                // 데이터 적용용
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}