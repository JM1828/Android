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

        // ActivityResultLauncher를 사용하여 액티비티 간의 결과를 처리하는 데 사용될 변수를 선언
        val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            // ActivityResultContracts.StartActivityForResult를 사용하여 결과를 처리할 ActivityResultLauncher를 등록
            ActivityResultContracts.StartActivityForResult()){
            // 결과 Intent에서 "result"라는 키로 전달된 문자열을 가져와서 처리
            it.data!!.getStringExtra("result")?.let {
                // 가져온 문자열을 datas에 추가
                datas?.add(it)
                // 어댑터에 데이터가 변경되었음을 알리고, 화면을 갱신
                adapter.notifyDataSetChanged()
            }
        }

        binding.mainFab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            requestLauncher.launch(intent)
        }

        datas = savedInstanceState?.let {
            it.getStringArrayList("datas")?.toMutableList()
        } ?: let {
            mutableListOf<String>()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.layoutManager=layoutManager
        adapter=MyAdapter(datas)
        binding.mainRecyclerView.adapter=adapter
        binding.mainRecyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("datas", ArrayList(datas))
    }
}