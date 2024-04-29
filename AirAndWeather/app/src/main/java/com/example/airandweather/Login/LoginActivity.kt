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

        binding.loginButton.setOnClickListener {
            lifecycleScope.launch {
                login()
            }
        }

        binding.buttonSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun login() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("이메일과 비밀번호를 입력해주세요.")
            return
        }

        val member = memberDao.findMemberByEmail(email)

        if (member != null && member.password == password) {
            loginSuccess()
        } else {
            showLoginError()
        }
    }

    private suspend fun loginSuccess() {
        withContext(Dispatchers.Main) {
            showToast("로그인 성공!")
            getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).edit().apply {
                putBoolean("IsLoggedIn", true)
                apply()
            }
            val intent = Intent(this@LoginActivity, ChosenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private suspend fun showLoginError() {
        withContext(Dispatchers.Main) {
            showToast("이메일 또는 비밀번호가 잘못되었습니다.")
        }
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}