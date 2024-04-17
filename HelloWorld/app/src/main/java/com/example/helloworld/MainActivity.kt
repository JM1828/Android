package com.example.helloworld

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        settingButton()
    }

    fun settingButton() {
        // "findViewById" 메서드는 주어진 ID에 해당하는 뷰를 찾는 메서드
        // "R.id.button"은 리소스 파일에서 해당 버튼의 ID를 나타냄
        val button = findViewById<Button>(R.id.button)
        // "button.setOnClickListener"는 버튼의 클릭 이벤트에 대한 리스너를 설정하는 부분
        button.setOnClickListener {
            // "Intent"은 안드로이드 애플리케이션 간 또는 같은 애플리케이션 내에서 활동(Activity)을 시작하거나 데이터를 전달하기 위해 사용되는 객체
            // "this"는 현재 활성화된 액티비티를 가리키고, "SubActivity::class.java"는 이동하고자 하는 액티비티의 클래스를 가리킴
            val intent = Intent(this, SubActivity::class.java)
            // "startActivity(intent)"는 액티비티 전환을 위해 새로운 액티비티를 시작하는 부분
            startActivity(intent)
        }
    }
}