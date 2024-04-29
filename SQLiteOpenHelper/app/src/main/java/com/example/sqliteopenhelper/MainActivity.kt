package com.example.sqliteopenhelper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sqliteopenhelper.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var datas: MutableList<UserData> = mutableListOf()
    private lateinit var adapter: MyAdapter
    private lateinit var detailActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var addActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeData()
        setupActivityResultLaunchers()
        setupRecyclerView()
        setupFabClickListener()
    }

    // 데이터 초기화 및 데이터베이스에서 데이터 로드
    private fun initializeData() {
        // DBHelper 클래스를 사용하여 읽기 가능한 데이터베이스를 가져옴
        val db = DBHelper(this).readableDatabase
        // "TODO_TB" 테이블로부터 모든 열을 선택하는 쿼리를 실행하고, 결과를 cursor에 저장
        val cursor = db.rawQuery("select * from MEMBER_TB", null)
        // cursor의 범위 내에서 아래의 코드 블록을 실행, use를 사용하면 쿼리 결과를 처리한 후 자동으로 Cursor를 닫아줌
        cursor.use {
            // cursor가 다음 행으로 이동할 수 있는 경우까지 반복
            while (it.moveToNext()) {
                // 현재 행의 이름, 나이, 핸드폰번호를 가져와서 datas 리스트에 추가
                val name = it.getString(1)
                val age = it.getString(2)
                val phone = it.getString(3)
                datas.add(UserData(name, age, phone))
            }
        }
        db.close()
    }

    // RecyclerView 설정
    private fun setupRecyclerView() {
        // MyAdapter라는 이름의 어댑터를 생성, datas라는 데이터 리스트와 detailActivityLauncher라는 컴포넌트를 인자로 받음
        adapter = MyAdapter(datas, detailActivityLauncher)
        binding.mainRecyclerView.apply {
            // RecyclerView에는 어떻게 항목들을 배치할지를 결정하는 레이아웃 매니저가 필요
            layoutManager = LinearLayoutManager(this@MainActivity)
            // 생성한 어댑터를 RecyclerView의 어댑터로 설정
            adapter = this@MainActivity.adapter
            // RecyclerView의 각 아이템 사이에 수직 방향의 구분선을 추가
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
    }

    // ActivityResultLauncher 설정
    private fun setupActivityResultLaunchers() {
        addActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleAddActivityResult(result)
        }

        detailActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleDetailActivityResult(result)
        }
    }

    // FloatingActionButton 클릭 리스너 설정
    private fun setupFabClickListener() {
        binding.mainFab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            addActivityLauncher.launch(intent)
        }
    }

    // AddActivity의 결과 처리
    private fun handleAddActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            // 등록된 데이터를 받아옴
            val name = result.data?.getStringExtra("name")
            val age = result.data?.getStringExtra("age")
            val phone = result.data?.getStringExtra("phone")

            // 받아온 데이터가 모두 null이 아닌 경우에 처리
            if (name != null && age != null && phone != null) {
                // 받아온 데이터를 활용하여 작업 수행
                val userData = UserData(name, age, phone)
                datas.add(userData)
                adapter.notifyDataSetChanged()
            }
        }
    }

    // DetailActivity의 결과 처리
    private fun handleDetailActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            // 업데이트된 데이터를 받아옴
            val updatedName = result.data?.getStringExtra("updatedName")
            val updatedAge = result.data?.getStringExtra("updatedAge")
            val updatedPhone = result.data?.getStringExtra("updatedPhone")
            // 어떤 위치의 데이터가 업데이트되었는지를 가져옴
            val position = result.data?.getIntExtra("position", -1) ?: -1
            // 업데이트된 데이터가 있고, 유효한 위치가 지정된 경우
            if (updatedName != null && updatedAge != null && updatedPhone != null && position != -1) {
                // 해당 위치의 데이터를 업데이트하고, 어댑터에 변화를 알림
                val updatedData = UserData(updatedName, updatedAge, updatedPhone)
                datas[position] = updatedData
                adapter.notifyItemChanged(position)
            } else if (updatedName == null && updatedAge == null && updatedPhone == null && position != -1) {
                // 해당 위치의 데이터를 삭제하고, 어댑터에 항목 삭제를 알림
                datas.removeAt(position)
                adapter.notifyItemRemoved(position)
                // 해당 위치 이후의 항목들에 대해 범위가 변경되었음을 알림
                adapter.notifyItemRangeChanged(position, datas.size)
            }
        }
    }
}