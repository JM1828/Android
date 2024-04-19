package com.example.class20240419

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    /*
        이 코드에서 ActivityResultContracts.StartActivityForResult()는 ActivityResultLauncher를 생성하기 위한 계약(Contract)을 정의
        이 계약은 다른 액티비티를 시작하고 그 결과를 처리하기 위한 메커니즘을 제공
        그리고 registerForActivityResult 메서드를 사용하여 이 계약을 등록하고, 그 결과로 ActivityResultLauncher를 얻게됨
        이렇게 생성된 requestLauncher를 통해 다른 액티비티를 시작하고 결과를 처리할 수 있음
     */

    // Activity Result API를 사용하여 액티비티 결과를 처리하기 위해 ActivityResultLauncher를 등록하는 부분
    val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        // ActivityResultLauncher를 생성하고, registerForActivityResult 메서드를 사용하여 이를 requestLauncher에 등록
        ActivityResultContracts.StartActivityForResult()
    )
    {
        // callback 실행
        // 결과로 받은 데이터에서 "resultData"라는 키를 가진 문자열을 가져와 result 변수에 저장
        val result = it.data?.getStringExtra("resultData")
        // R.id.text1로 지정된 TextView를 찾아냄
        val textView1 = findViewById<TextView>(R.id.text1)
        // TextView에 "detailActivity에서 받아온 값: ${result}" 형식의 문자열을 설정하여 화면에 표시
        textView1.text = "datailActivity에서 받아온 값: ${result}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 버튼 클릑시 동작
        val btn1 = findViewById<Button>(R.id.btn1)
        btn1.setOnClickListener {
            // DetailActivity로 이동하는 Intent를 생성하고, "data1"에 "안녕하세요~"라는 문자열을, "data2"에 100이라는 정수를 담아 설정
            val intent: Intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("data1", "안녕하세요~")
            intent.putExtra("data2", 100)
//        startActivity(intent)
            // startActivityForResult 메서드를 사용하여 새로운 액티비티를 시작하고, 그 결과를 받아오기 위해 요청 코드로 999를 전달
//            startActivityForResult(intent, 999)
            // launch 메서드를 사용하여 intent를 전달하면, 등록된 ActivityResultCallback이 실행되어 액티비티의 결과를 처리
            requestLauncher.launch(intent)
        }

        val btn2 = findViewById<Button>(R.id.btn2)
        btn2.setOnClickListener {
            // "ACTION_EDIT" 액션과 "http://www.google.com" 주소 정보를 가진 Intent를 생성
            val intent2 = Intent("ACTION_EDIT", Uri.parse("http://www.google.com"))
            // 생성된 Intent를 사용하여 startActivity 메서드를 호출하여 새로운 액티비티를 시작
            startActivity(intent2)
        }
    }

    // 이 메서드는 다른 액티비티에서 결과를 받아올 때 호출됨
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // requestCode는 요청 코드를, resultCode는 결과 코드를, data는 결과 데이터를 포함하는 Intent를 나타냄
        super.onActivityResult(requestCode, resultCode, data)
        //  requestCode가 999이고 resultCode가 Activity.RESULT_OK일 때의 조건을 확인
        // 이는 다른 액티비티에서 돌려받은 결과가 성공적이고 요청 코드가 999일 때를 의미
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            // 결과로 받은 데이터에서 "resultData"라는 키를 가진 문자열을 가져와 result 변수에 저장
            val result = data?.getStringExtra("resultData")
            // R.id.text1로 지정된 TextView를 찾아냄
            val textView1 = findViewById<TextView>(R.id.text1)
            // TextView에 "detailActivity에서 받아온 값: ${result}" 형식의 문자열을 설정하여 화면에 표시
            textView1.text = "datailActivity에서 받아온 값: ${result}"
        }
    }
}