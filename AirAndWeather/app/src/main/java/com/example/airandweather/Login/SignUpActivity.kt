package com.example.airandweather.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.airandweather.databinding.ActivitySignupBinding
import com.example.airandweather.db.AppDatabase
import com.example.airandweather.db.MemberDao
import com.example.airandweather.db.MemberEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    lateinit var db: AppDatabase
    lateinit var memberDao: MemberDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        memberDao = db.getMemberDao()

        binding.signupButton.setOnClickListener {
            lifecycleScope.launch {
                signUp()
            }
        }
    }

    private fun isValidPassword(password: String): Boolean = password.length >= 6

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    private suspend fun signUp() {
        val email = binding.profileEmail.text.toString()
        val password = binding.profilePassword.text.toString()
        val nickName = binding.profileNickname.text.toString()

        if (!validateInputs(email, password, nickName)) return

        if (memberDao.findMemberByEmail(email) != null) {
            showToast("이미 가입된 계정입니다.")
        } else {
            val newMember = MemberEntity(null, email, password, nickName)
            memberDao.insertMember(newMember)

            showToast("회원가입이 완료되었습니다.")
            navigateToLogin()
        }
    }

    private fun validateInputs(email: String, password: String, nickName: String): Boolean {
        when {
            email.isEmpty() || password.isEmpty() || nickName.isEmpty() -> {
                showToast("모든 정보를 입력해주세요.")
                return false
            }
            !isValidEmail(email) -> {
                showToast("올바른 이메일 주소를 입력해주세요.")
                return false
            }
            !isValidPassword(password) -> {
                showToast("비밀번호는 6자 이상이어야 합니다.")
                return false
            }
            else -> return true
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            if (!isFinishing) {
                Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this@SignUpActivity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}
