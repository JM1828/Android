package com.example.ch17_database

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ch17_database.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var datas: MutableList<String> = mutableListOf()
    lateinit var adapter: MyAdapter
    private lateinit var detailActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val updatedItem = data.getStringExtra("updatedItem")
                    val itemPosition = data.getIntExtra("itemPosition", -1)

                    if (updatedItem != null && itemPosition != -1) {
                        // 데이터 리스트 업데이트
                        datas[itemPosition] = updatedItem
                        // Adapter에 데이터가 변경되었다고 알림
                        adapter.notifyItemChanged(itemPosition)
                    }
                }
            }
        }

        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra("result")?.let { updatedData ->
                    datas?.add(updatedData)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        binding.mainFab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            requestLauncher.launch(intent)
        }


        datas = mutableListOf<String>()

        // DBHelper 클래스를 사용하여 읽기 가능한 데이터베이스를 가져옴
        val db = DBHelper(this).readableDatabase
        // "TODO_TB" 테이블로부터 모든 열을 선택하는 쿼리를 실행하고, 결과를 cursor에 저장
        val cursor = db.rawQuery("select * from TODO_TB", null)
        // cursor의 범위 내에서 아래의 코드 블록을 실행
        cursor.run {
            // cursor가 다음 행으로 이동할 수 있는 경우까지 반복
            while(moveToNext()){
                // 현재 행의 두 번째 열(인덱스는 0부터 시작)에 있는 값을 가져와서 datas 리스트에 추가
                datas?.add(cursor.getString(1))
            }
        }
        db.close()

        // RecyclerView의 레이아웃 매니저를 생성, 레이아웃 매니저는 수직 방향으로 아이템을 배치
        val layoutManager = LinearLayoutManager(this)
        // RecyclerView에 방금 생성한 레이아웃 매니저를 설정
        binding.mainRecyclerView.layoutManager = layoutManager
        // 데이터를 표시하기 위한 어댑터를 생성, datas는 RecyclerView에 표시될 데이터를 담고 있는 리스트
        adapter = MyAdapter(datas, detailActivityLauncher)
        // 방금 생성한 어댑터를 RecyclerView에 설정
        binding.mainRecyclerView.adapter = adapter
        // RecyclerView 아이템 사이에 구분선을 추가, 여기서는 수직 방향으로 구분선을 추가
        binding.mainRecyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // "menu_main.xml" 파일에 정의된 메뉴 리소스를 인플레이트하여 옵션 메뉴를 생성
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 선택된 메뉴 아이템의 아이디가 "menu_main_setting"인지 확인
        if(item.itemId === R.id.menu_main_setting){
            // SettingActivity로 이동하기 위한 Intent를 생성하고 해당 Activity를 시작
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}