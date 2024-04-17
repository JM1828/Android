package com.example.helloworld

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TwoColorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_two_color)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        settingButtons()
    }
    fun settingButtons() {
        // 레이아웃 XML 파일에서 정의된 버튼 뷰를 찾아오는 역할
        val button_rad = findViewById<Button>(R.id.button_red_fragment)
        val button_blue = findViewById<Button>(R.id.button_blue_fragment)

        // 버튼을 클릭했을 때 수행할 동작을 정의
        button_rad.setOnClickListener {
            // Fragment를 추가하거나, 교체하거나, 제거하는 작업은 FragmentTransaction을 사용하여 정의하고,
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            // frame_layout 영역에 새로운 RedFragment로 대체(replace)하겠다는 것을 의미
            fragmentTransaction.replace(R.id.frame_layout, RedFragment())
            // commit 메서드를 호출하여 이를 적용
            fragmentTransaction.commit()
        }

        // // 버튼을 클릭했을 때 수행할 동작을 정의
        button_blue.setOnClickListener {
            // Fragment를 추가하거나, 교체하거나, 제거하는 작업은 FragmentTransaction을 사용하여 정의하고,
            // commit 메서드를 호출하여 이를 적용
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            // frame_layout 영역에 새로운 RedFragment로 대체(replace)하겠다는 것을 의미
            fragmentTransaction.replace(R.id.frame_layout, BlueFragment())
            // commit 메서드를 호출하여 이를 적용
            fragmentTransaction.commit()
        }

    }
}