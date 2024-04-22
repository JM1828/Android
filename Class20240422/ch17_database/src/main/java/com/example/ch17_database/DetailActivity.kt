package com.example.ch17_database

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ch17_database.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var itemPosition: Int? = null  // 아이템의 위치를 저장하는 변수 추가

    // ModifyActivity로부터 결과를 받기 위한 ActivityResultLauncher 선언
    private lateinit var modifyActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // intent로부터 데이터와 위치 정보를 가져옴
        val data1 = intent.getStringExtra("updatedItem")
        itemPosition = intent.getIntExtra("itemPosition", -1)  // 기본값으로 -1 설정

        binding.text1.text = data1

        // 결과를 처리하는 ActivityResultLauncher 초기화
        modifyActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedData = result.data?.getStringExtra("updatedResult")
                updatedData?.let {
                    binding.text1.text = it
                }
            }
        }

        binding.btnBack.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("result", "다시 보내요~!")
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, ModifyActivity::class.java).apply {
                putExtra("updatedItem", data1)
                putExtra("itemPosition", itemPosition)
            }
            modifyActivityResultLauncher.launch(intent)
        }
    }
}
