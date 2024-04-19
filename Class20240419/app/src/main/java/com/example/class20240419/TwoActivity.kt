package com.example.class20240419

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TwoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)

        // intent.data를 사용하여 전달된 데이터를 받아옴
        val data = intent.data
        // R.id.text1로 지정된 TextView를 찾아내고, 받아온 데이터를 문자열로 변환하여 해당 TextView에 표시
        val text1 = findViewById<TextView>(R.id.text1)
        text1.text = data.toString()

        // 웹뷰에 intent로 받아온 url 결과 띄움
        val web1: WebView = findViewById(R.id.web)
        web1.webViewClient = WebViewClient()
        web1.loadUrl(data.toString())
        web1.settings.javaScriptEnabled = true

        // 버튼 클릭시 동작
        val btn1 = findViewById<Button>(R.id.btn1)
        btn1.setOnClickListener {
            // "resultData"라는 이름으로 "다시 보내요~!"라는 문자열을 intent에 담아서 설정
            intent.putExtra("resultData", "다시 보내요~!")
            // setResult 메서드를 사용하여 현재 액티비티에게 성공적인 결과와 함께 intent를 반환
            // 이때 RESULT_OK는 성공을 나타내는 상수
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}