package com.example.ch17_database

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.ch17_database.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // menuInflater를 사용하여 menu_add라는 메뉴 리소스를 inflate하여 옵션 메뉴를 생성
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        // item.itemId를 통해 선택된 메뉴 아이템의 ID를 확인하고, R.id.menu_add_save인 경우에는 특정 동작을 수행
        when (item.itemId) {
            // menu_add_save 아이템이 선택되었을 때, binding.addEditView에서 텍스트를 가져와 inputData에 저장
            R.id.menu_add_save -> {
                val inputData = binding.addEditView.text.toString()
                // 데이터베이스를 열어 앱이나 사용자의 입력에 대한 데이터를 저장하거나 가져오기 위한 목적으로 사용
                val db = DBHelper(this).writableDatabase
                // TODO_TB라는 테이블에 inputData 값을 todo 열에 삽입하는 INSERT 쿼리를 실행
                db.execSQL(
                    "insert into TODO_TB (todo) values (?)",
                    arrayOf<String>(inputData)
                )
                db.close()
                val intent = intent
                // Intent에 "result"라는 이름으로 inputData를 추가
                intent.putExtra("result", inputData)
                // setResult를 사용하여 현재 액티비티의 결과를 설정하고, Activity.RESULT_OK를 반환
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            else -> true
        }
}