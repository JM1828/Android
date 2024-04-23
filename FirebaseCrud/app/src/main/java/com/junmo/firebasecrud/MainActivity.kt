package com.junmo.firebasecrud

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.junmo.firebasecrud.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 데이터베이스 클래스 객체 생성
        var dao = UserDao()

        binding.btnComplete.setOnClickListener {

            // 사용자가 입력한 할 일의 제목을 가져옴
            val todoTitle = binding.edtTitle.text.toString()
            // 라디오 그룹에서 선택된 라디오 버튼의 ID를 가져옴
            var todoImportance = binding.radioGroup.checkedRadioButtonId

            var impData = 0;
            // 선택된 라디오 버튼의 ID에 따라 중요도를 결정
            when (todoImportance) {
                R.id.btn_high -> {
                    impData = 1;
                }

                R.id.btn_middle -> {
                    impData = 2;
                }

                R.id.btn_low -> {
                    impData = 3;
                }
            }

            // 중요도가 설정되지 않았거나 할 일 제목이 비어있는 경우
            if (impData == 0 || todoTitle.isBlank()) {
                Toast.makeText(this, "모든 항목을 채워주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val user = User("", todoTitle, impData)
                dao.add(user)?.addOnSuccessListener {
                    Toast.makeText(this, "할 일이 추가되었습니다..", Toast.LENGTH_SHORT).show()
                }?.addOnFailureListener {
                    Toast.makeText(this, "등록에 실패하였습니다.. : ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 사용자 목록 버튼 이벤트
        binding.btnList.setOnClickListener {
            val intent: Intent = Intent(this, TodoListActivity::class.java)
            startActivity(intent)
        }
    }
}