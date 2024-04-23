package com.junmo.firebasecrud

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.junmo.firebasecrud.databinding.ActivityMainBinding
import com.junmo.firebasecrud.databinding.ActivityTodoListBinding

class TodoListActivity : AppCompatActivity() {

    lateinit var binding: ActivityTodoListBinding

    lateinit var dao: UserDao

    private lateinit var adapter: UserAdapter

    private lateinit var todoList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // todoList 초기화
        todoList = ArrayList()

        // dao 초기화
        dao = UserDao()

        // adapter 초기화
        adapter = UserAdapter(this, todoList)

        // recyclerView 초기화
        // RecyclerView에 어댑터를 설정하여 RecyclerView가 어댑터로부터 데이터를 가져오도록 함
        binding.recyclerview.adapter = adapter
        // RecyclerView에 LinearLayoutManager를 설정하여 아이템을 세로로 배치하도록 함
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        // 사용자 정보 가져오기
        loadData()
    }

    private fun loadData() {
        dao.getTodoList()?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {

                    val todo = dataSnapshot.getValue(User::class.java)

                    // 키값 가져오기
                    val key = dataSnapshot.key

                    // 사용자 정보에 키 값 담기
                    todo?.userKey = key.toString()

                    // 리스트에 담기
                    if (todo != null) {
                        todoList.add(todo)
                    }
                }

                // 데이터 적용용
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}