package com.example.databaseex

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.databaseex.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // "testdb"라는 이름의 데이터베이스를 생성하고, MODE_PRIVATE로 설정하여 해당 앱에서만 이 데이터베이스에 접근할 수 있도록 함
        val db = openOrCreateDatabase("testdb", Context.MODE_PRIVATE, null)

        binding.button.setOnClickListener {
            try {
                // ContentValues를 사용하여 각 필드에 대한 값을 설정
                val values = ContentValues()
                values.put("name", binding.editName.text.toString())
                values.put("phone", binding.editPhone.text.toString())
                // db.insert를 통해 레코드를 삽입
                db.insert("user_tb", null, values)

                //  "user_tb" 테이블에서 "name"과 "phone" 열을 모두 선택하여 cursor에 저장
                val cursor =
                    db.query("user_tb", arrayOf("name", "phone"), null, null, null, null, null)
                var data: String = ""
                while (cursor.moveToNext()) {
                    val name = cursor.getString(0)
                    val phone = cursor.getString(1)
                    data += "이름: ${name} \t 휴대폰: ${phone} \n"
                }
                binding.selectData.text = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}