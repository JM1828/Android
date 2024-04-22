package com.example.databaseex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // "testdb"라는 이름의 데이터베이스를 생성하고, MODE_PRIVATE로 설정하여 해당 앱에서만 이 데이터베이스에 접근할 수 있도록 함
        val db = openOrCreateDatabase("testdb", Context.MODE_PRIVATE, null)

        try {
            // "user_tb"라는 테이블을 생성
            // "id"라는 INTEGER PRIMARY KEY AUTOINCREMENT 속성을 가지고 있으며, "name"과 "phone" 두 개의 컬럼을 가지고 있음
            db.execSQL("CREATE TABLE user_tb (" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "name TEXT NOT NULL, " + "phone)")
            // SQLite 데이터베이스의 "user_tb" 테이블에 "name"과 "phone" 컬럼에 각각 "홍길동"과 "01012345678" 값을 가진 레코드를 삽입
            db.execSQL(
                "INSERT INTO user_tb (name, phone) VALUES(?, ?)",
                arrayOf<String>("홍길동", "01012345678")
            )
            db.execSQL(
                "INSERT INTO user_tb (name, phone) VALUES(?, ?)",
                arrayOf<String>("짱구", "01012342222")
            )
            db.execSQL(
                "INSERT INTO user_tb (name, phone) VALUES(?, ?)",
                arrayOf<String>("흰둥이", "01012343333")
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val button = findViewById<Button>(R.id.button)
        val selectData = findViewById<TextView>(R.id.selectData)
        button.setOnClickListener {
            // SQLite 데이터베이스의 "user_tb" 테이블에서 "name"과 "phone" 컬럼을 선택하여 모든 레코드를 조회
            val cursor = db.rawQuery("SELECT name, phone FROM user_tb", null)
            var data: String = ""
            // cursor를 사용하여 "user_tb" 테이블에서 각 레코드의 "name"과 "phone" 값을 가져와서 문자열을 생성
            while (cursor.moveToNext()) {
                val name = cursor.getString(0)
                val phone = cursor.getString(1)
                data += "이름: ${name} \t 연락처: ${phone} \n"
            }
            selectData.text = data
        }
        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}