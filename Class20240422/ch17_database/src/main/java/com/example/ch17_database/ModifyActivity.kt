package com.example.ch17_database

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ch17_database.databinding.ActivityModifyBinding

class ModifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 뷰 바인딩을 통해 뷰 초기화
        val editText = binding.editText
        val btnSave = binding.btnSave
        val btnBack = binding.btnBack

        // 이전 액티비티에서 넘어온 데이터와 위치를 가져옴
        val receivedData = intent.getStringExtra("updatedItem")
        val itemPosition = intent.getIntExtra("itemPosition", -1) // 기본값 -1

        // 받아온 데이터를 화면에 표시
        editText.setText(receivedData)

        // 저장 버튼 클릭시 동작
        btnSave.setOnClickListener {
            val updatedData = editText.text.toString()
            // 데이터베이스를 열어 수정 작업 수행
            val db = DBHelper(this).writableDatabase
            db.execSQL("UPDATE TODO_TB SET todo = ? WHERE todo = ?",
                arrayOf(updatedData, receivedData))
            db.close()

            // 수정된 데이터와 위치를 인텐트에 담아 결과로 설정
            val resultIntent = Intent().apply {
                putExtra("updatedResult", updatedData)
                putExtra("itemPosition", itemPosition)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // 뒤로가기 버튼 클릭시 동작
        btnBack.setOnClickListener {
            // 아무 작업도 수행하지 않고 바로 종료
            finish()
        }
    }
}