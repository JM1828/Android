package com.example.ch13_activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // intent로부터 "data1"과 "data2"라는 이름의 데이터를 가져옴
        val data1 = intent.getStringExtra("selectedItem")
        // "data2"의 경우 기본값으로 0을 설정

        val textView1 = findViewById<TextView>(R.id.text1)
        textView1.text = "Todo상세 \n ${data1}"

        // 버튼 클릭시 동작
        val btn1 = findViewById<Button>(R.id.btn1)
        btn1.setOnClickListener {
            // "resultData"라는 이름으로 "다시 보내요~!"라는 문자열을 intent에 담아서 설정
            intent.putExtra("result", "다시 보내요~!")
            // setResult 메서드를 사용하여 현재 액티비티에게 성공적인 결과와 함께 intent를 반환
            // 이때 RESULT_OK는 성공을 나타내는 상수
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}