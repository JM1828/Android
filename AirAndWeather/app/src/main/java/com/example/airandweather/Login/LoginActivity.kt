package com.example.airandweather.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.airandweather.ChosenActivity
import com.example.airandweather.databinding.ActivityLoginBinding
import com.example.airandweather.db.AppDatabase
import com.example.airandweather.db.MemberDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var db: AppDatabase
    lateinit var memberDao: MemberDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        memberDao = db.getMemberDao()

        // 로그인
        binding.loginButton.setOnClickListener {
            lifecycleScope.launch {
                login()
            }
        }

        // 회원가입 버튼 클릭 리스너 설정
        binding.buttonSignUp.setOnClickListener {
            // SignUpActivity로 이동하기 위한 Intent 생성
            val intent = Intent(this, SignUpActivity::class.java)
            // Intent 시작
            startActivity(intent)
        }
    }

    // 로그인
    private suspend fun login() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val member = memberDao.findMemberByEmail(email)

        if (member != null && member.password == password) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()

                // 로그인 성공 시, 로그인 상태를 SharedPreferences에 저장
                getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).edit().apply {
                    putBoolean("IsLoggedIn", true)
                    apply()
                }

                // 로그인 성공 후 ChosenActivity로 이동
                val intent = Intent(this@LoginActivity, ChosenActivity::class.java)
                startActivity(intent)
                finish() // LoginActivity를 종료하여 뒤로 가기 버튼을 눌렀을 때 다시 나타나지 않게 함
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, "이메일 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}