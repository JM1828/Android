package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.R
import com.example.todolist.databinding.ActivityTodoUpdateBinding
import com.example.todolist.db.TodoDao

class TodoUpdateActivity : AppCompatActivity() {

    lateinit var binding: ActivityTodoUpdateBinding

    lateinit var sId: String
    lateinit var sTitle: String
    lateinit var sImportance: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 데이터베이스 객체
        val dao = TodoDao()

        // 데이터 null체크
        if (intent.hasExtra("id") && intent.hasExtra("title") && intent.hasExtra("importance")) {

            // 데이터 담기
            sId = intent.getStringExtra("id")!!
            sTitle = intent.getStringExtra("title")!!
            sImportance = intent.getStringExtra("importance")!!

            // 데이터 보여주기
            binding.upTitle.setText(sTitle)

            when (sImportance) {
                "1" -> binding.upRadioGroup.check(binding.upBtnHigh.id)
                "2" -> binding.upRadioGroup.check(binding.upBtnMiddle.id)
                "3" -> binding.upRadioGroup.check(binding.upBtnLow.id)
            }

            // 사용자정보 수정버튼 이벤트
            binding.upBtnComplete.setOnClickListener {

                // 입력값
                val uTitle = binding.upTitle.text.toString()
                val uImportance = when (binding.upRadioGroup.checkedRadioButtonId) {
                    R.id.up_btn_high -> "1"
                    R.id.up_btn_middle -> "2"
                    R.id.up_btn_low -> "3"
                    else -> ""
                }

                // 파라미터 셋팅
                val hasMap: HashMap<String, Any> = HashMap()
                hasMap["title"] = uTitle
                hasMap["importance"] = uImportance

                // 중요도가 설정되지 않았거나 할 일 제목이 비어있는 경우
                if (uImportance == "" || uTitle.isBlank()) {
                    Toast.makeText(this, "모든 항목을 채워주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    dao.todoUpdate(sId, hasMap).addOnSuccessListener {
                        Toast.makeText(applicationContext, "수정 성공", Toast.LENGTH_SHORT).show()

                        // 목록으로 이동
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }.addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "수정 실패: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}