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

    // AppDatabase 타입의 데이터베이스 객체
    lateinit var db: AppDatabase
    lateinit var memberDao: MemberDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // getInstance(this) 메서드는 AppDatabase 클래스의 싱글톤 인스턴스를 반환하며, 해당 인스턴스는 앱의 라이프사이클과 연결되어 있음
        db = AppDatabase.getInstance(this)!!
        // db 인스턴스에서 TodoDao(데이터 액세스 객체)를 가져와서 todoDao 변수에 할당
        memberDao = db.getMemberDao()

        binding.signupButton.setOnClickListener {
            lifecycleScope.launch {
                signUp()
            }
        }
    }

    // 비밀번호 유효성 검사 함수
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    // 이메일 유효성 검사 함수
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    // 회원 가입
    private suspend fun signUp() {
        val email = binding.profileEmail.text.toString()
        val password = binding.profilePassword.text.toString()
        val nickName = binding.profileNickname.text.toString()
        val placeStation = binding.profileStationt.text.toString()

        if (email.isEmpty() || password.isEmpty() || nickName.isEmpty() || placeStation.isEmpty()/*|| address.isEmpty()*/) {
            // 하나라도 비어있으면 알림 표시
            Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail(email)) {
            // 이메일이 유효하지 않은 경우 알림 표시
            Toast.makeText(this, "올바른 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidPassword(password)) {
            // 비밀번호가 유효하지 않은 경우 알림 표시
            Toast.makeText(this, "비밀번호는 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 입력 값이 모두 존재하는 경우
        lifecycleScope.launch {
            if (memberDao.findMemberByEmail(email) != null) {
                // 중복된 이메일이 있으므로 알림을 표시
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "이미 가입된 계정입니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                // 회원 정보를 데이터베이스에 저장
                val newMember =
                    MemberEntity(null, email, password, nickName, placeStation)
                memberDao.insertMember(newMember)
                // 회원 가입 성공 알림
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

                // 회원가입 성공 후 LoginActivity로 이동
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
                finish() // LoginActivity를 종료하여 뒤로 가기 버튼을 눌렀을 때 다시 나타나지 않게 함
            }
        }
    }
}
