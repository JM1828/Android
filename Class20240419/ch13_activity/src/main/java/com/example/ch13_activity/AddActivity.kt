package com.example.ch13_activity

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.ch13_activity.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {
    // ActivityAddBinding 클래스의 인스턴스를 나중에 초기화할 수 있도록 선언
    lateinit var binding: ActivityAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ActivityAddBinding 클래스를 사용하여 현재 액티비티의 레이아웃을 인플레이트하고, 바인딩 객체에 할당
        binding= ActivityAddBinding.inflate(layoutInflater)
        // 액티비티의 레이아웃으로 binding.root를 설정하여 화면에 표시
        setContentView(binding.root)
    }

    // onCreateOptionsMenu 메서드를 오버라이드하여 액티비티의 액션바에 메뉴를 추가하는 부분
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // R.menu.menu_add를 사용하여 메뉴 리소스를 inflate하여 메뉴에 추가
        menuInflater.inflate(R.menu.menu_add, menu)
        // 부모 클래스의 onCreateOptionsMenu 메서드를 호출하여 메뉴가 생성되었음을 나타내는 값을 반환
        return super.onCreateOptionsMenu(menu)
    }

    // onOptionsItemSelected 메서드를 오버라이드하여 액션바의 메뉴 아이템을 처리하는 부분
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        // when 구문을 사용하여 item.itemId에 따라 다른 동작을 수행
        when(item.itemId){
            // "menu_add_save" 메뉴 아이템이 선택되었을 때의 동작을 정의
        R.id.menu_add_save -> {
            val intent = intent
            // "result"라는 키로 EditText에 입력된 내용을 인텐트에 추가
            intent.putExtra("result", binding.addEditView.text.toString())
            // 결과 코드를 설정하고, 인텐트를 포함하여 현재 액티비티를 종료
            setResult(Activity.RESULT_OK, intent)
            finish()
            // 해당 이벤트를 소비하고 처리했음을 나타내는 값을 반환
            true
        }
        else -> true
    }

}