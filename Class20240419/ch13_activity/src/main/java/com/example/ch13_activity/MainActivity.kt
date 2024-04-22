package com.example.ch13_activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ch13_activity.databinding.ActivityMainBinding
import com.example.ch13_activity.databinding.ItemRecyclerviewBinding

class MainActivity : AppCompatActivity() {

    // ActivityMainBinding 클래스의 인스턴스를 나중에 초기화할 수 있도록 선언
    lateinit var binding: ActivityMainBinding
    // MutableList<String> 타입의 데이터를 담는 변수를 선언
    var datas: MutableList<String>? = null
    // MyAdapter 클래스의 인스턴스를 나중에 초기화할 수 있도록 선언
    lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Activity Result API를 사용하여 액티비티 결과를 처리하기 위한 ActivityResultLauncher를 등록하는 역할
        val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            // Intent로부터 "result"라는 이름의 문자열 데이터를 가져옴
            it.data!!.getStringExtra("result")?.let {
                // 가져온 문자열을 datas에 추가
                datas?.add(it)
                // 어댑터에 데이터가 변경되었음을 알리고, 화면을 갱신
                adapter.notifyDataSetChanged()
            }
        }

        // mainFab 을 클릭했을 때, AddActivity를 시작
        binding.mainFab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            requestLauncher.launch(intent)
        }

        // 액티비티의 이전 상태에서 "datas"라는 키로 저장된 문자열 목록을 가져와서 데이터를 복원하거나, 저장된 데이터가 없는 경우 새로운 빈 리스트를 생성하는 역할
        datas = savedInstanceState?.let {
            it.getStringArrayList("datas")?.toMutableList()
        } ?: let {
            mutableListOf<String>()
        }

        // RecyclerView에 사용될 LinearLayoutManager를 생성
        val layoutManager = LinearLayoutManager(this)
        // 생성된 LinearLayoutManager을 RecyclerView의 레이아웃 매니저로 설정
        binding.mainRecyclerView.layoutManager = layoutManager
        // RecyclerView에 사용될 어댑터를 초기화
        adapter = MyAdapter(datas)
        // 해당 어댑터를 RecyclerView에 설정
        binding.mainRecyclerView.adapter = adapter
        // RecyclerView에 수직 구분선을 추가하기 위해 addItemDecoration을 사용하여 DividerItemDecoration을 설정
        binding.mainRecyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // "datas"라는 키로 문자열 목록을 번들에 추가하여 현재 액티비티의 상태를 저장하는 역할
        outState.putStringArrayList("datas", ArrayList(datas))
    }
}