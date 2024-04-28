package com.example.ch17_database

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.ch17_database.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {
    // 뷰 바인딩 객체 선언
    private lateinit var binding: ActivityAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 뷰 바인딩 초기화 및 레이아웃 설정
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 옵션 메뉴 생성을 위해 menu_add 리소스를 inflate
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_add_save -> {
                // '저장' 메뉴 아이템 선택 시 수행할 동작
                // EditText에서 사용자 입력 텍스트를 가져옴
                val inputData = binding.addEditView.text.toString()
                // DBHelper를 사용하여 데이터베이스에 쓰기 가능한 인스턴스 획득
                DBHelper(this).writableDatabase.use { db ->
                    // TODO_TB 테이블에 새로운 할 일(todo) 추가
                    db.execSQL("INSERT INTO TODO_TB (todo) VALUES (?)", arrayOf(inputData))
                }
                // 결과를 인텐트에 담아서 설정하고 액티비티 종료
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra("result", inputData)
                })
                finish() // 액티비티 종료
                true // 이벤트 처리 완료
            }

            else -> super.onOptionsItemSelected(item) // 그 외의 항목은 기본 처리 방식을 따름
        }
}