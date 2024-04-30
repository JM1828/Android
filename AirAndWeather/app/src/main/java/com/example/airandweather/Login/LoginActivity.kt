package com.example.airandweather.Login

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.airandweather.ChosenActivity
import com.example.airandweather.databinding.ActivityLoginBinding
import com.example.airandweather.db.AppDatabase
import com.example.airandweather.db.MemberDao
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var db: AppDatabase
    lateinit var memberDao: MemberDao

    // 이메일 로그인 콜백
    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "로그인 실패 $error")
        } else if (token != null) {
            Log.e(TAG, "로그인 성공 ${token.accessToken}")
            loggedInSuccess(token)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        memberDao = db.getMemberDao()

        // 로그인 버튼 클릭 이벤트
        binding.loginButton.setOnClickListener {
            lifecycleScope.launch {
                login()
            }
        }

        // 회원가입 버튼 클릭 이벤트
        binding.buttonSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // 카카오 로그인 버튼 클릭 이벤트
        binding.buttonKakaoLogin.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                kakaoLoginWithTalk()
            } else {
                kakaoLoginWithAccount()
            }
        }
    }

    // 카카오톡으로 로그인 시도
    private fun kakaoLoginWithTalk() {
        UserApiClient.instance.loginWithKakaoTalk(this, callback = mCallback)
    }

    // 카카오 계정으로 로그인 시도
    private fun kakaoLoginWithAccount() {
        UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
    }

    // 로그인 성공 처리
    private fun loggedInSuccess(token: OAuthToken) {
        // 카카오톡 사용자 정보 가져오기만 하고 화면 전환은 fetchKakaoUserInfo 함수에서 처리
        fetchKakaoUserInfo()
    }

    // 카카오톡 사용자 정보 가져오기
    private fun fetchKakaoUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패 $error")
            } else if (user != null) {
                Log.i(TAG, "사용자 정보 요청 성공: ${user.kakaoAccount?.profile?.nickname}")

                // Shared Preferences에 로그인 상태와 닉네임 저장
                val prefs = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).edit()
                prefs.putBoolean("IsLoggedIn", true)
                prefs.putString("LoggedInNickname", user.kakaoAccount?.profile?.nickname) // 닉네임 저장
                prefs.apply()

                // 사용자 정보 요청이 성공한 후 ChosenActivity로 이동
                val intent = Intent(this@LoginActivity, ChosenActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    // 로그인
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

    // 로그인 성공 시 실행되는 함수
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

    // 로그인 실패 시 실행되는 함수
    private suspend fun showLoginError() {
        withContext(Dispatchers.Main) {
            showToast("이메일 또는 비밀번호가 잘못되었습니다.")
        }
    }

    // 로그인 성공 메세지 이 함수는 UI 스레드(메인 디스패처)에서 실행
    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}