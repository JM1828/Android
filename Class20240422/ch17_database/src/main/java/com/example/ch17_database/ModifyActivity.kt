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
        // 뷰 바인딩 설정
        binding = ActivityModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트에서 데이터와 위치 정보 가져오기
        val receivedData = intent.getStringExtra("result")
        // 화면에 받아온 데이터 표시
        binding.editText.setText(receivedData)

        // 저장 버튼 클릭 시 동작 설정
        setupSaveButton(receivedData)

        // 뒤로가기 버튼 클릭 시 동작 설정
        setupBackButton()
    }

    // 저장 버튼 설정 함수
    private fun setupSaveButton(receivedData: String?) {
        // 저장 버튼 클릭 리스너 설정
        binding.btnSave.setOnClickListener {
            // 수정된 데이터를 editText로부터 가져옴
            val updatedData = binding.editText.text.toString()

            // 데이터베이스 업데이트를 위한 함수 호출
            updateDatabase(receivedData, updatedData)

            // 결과를 설정하고 현재 액티비티를 종료하는 함수 호출
            setResultAndFinish(updatedData)
        }
    }

    // 데이터베이스 업데이트를 위한 함수
    private fun updateDatabase(receivedData: String?, updatedData: String) {
        // DBHelper를 통해 쓰기 가능한 데이터베이스 인스턴스를 가져옴
        val db = DBHelper(this).writableDatabase
        // 데이터베이스의 TODO_TB 테이블에서, 받아온 기존 데이터(receivedData)를 새로운 데이터(updatedData)로 업데이트함
        db.execSQL(
            "UPDATE TODO_TB SET todo = ? WHERE todo = ?",
            arrayOf(updatedData, receivedData)
        )
        // 데이터베이스 사용 후 닫기
        db.close()
    }

    // 결과 설정 및 액티비티 종료를 위한 함수
    private fun setResultAndFinish(updatedData: String) {
        // 인텐트를 생성하여 수정된 데이터와 위치 정보를 포함시킴
        val resultIntent = Intent().apply {
            putExtra("updatedResult", updatedData)
            putExtra("position", intent.getIntExtra("position", -1))
        }
        // 결과를 설정하고 현재 액티비티 종료ㅋㅌ
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    // 뒤로가기 버튼 설정 함수
    private fun setupBackButton() {
        // 뒤로가기 버튼 클릭 리스너 설정, 현재 액티비티 종료
        binding.btnBack.setOnClickListener { finish() }
    }
}