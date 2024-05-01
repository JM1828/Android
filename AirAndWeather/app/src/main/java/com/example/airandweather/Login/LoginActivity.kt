package com.example.airandweather.Login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.example.airandweather.ChosenActivity
import com.example.airandweather.R
import com.example.airandweather.databinding.ActivityLoginBinding
import com.example.airandweather.db.AppDatabase
import com.example.airandweather.db.MemberDao
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginActivity : AppCompatActivity() {
    // 레이아웃 및 데이터베이스 관련 멤버 변수
    private lateinit var binding: ActivityLoginBinding
    lateinit var db: AppDatabase
    lateinit var memberDao: MemberDao

    private val tag = "LoginActivity"
    private val rcSignIn = 100

    // Google 로그인 관련 멤버 변수
    private lateinit var googleSignInClient: GoogleSignInClient
    private var googleSignInAccount: GoogleSignInAccount? = null
    private lateinit var mAuth: FirebaseAuth

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
        // Layout binding 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        memberDao = db.getMemberDao()

        // 파이어베이스 인증 객체 초기화
        mAuth = FirebaseAuth.getInstance()

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

        // 구글 로그인 옵션 설정
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.buttonGoogleLogin.setOnClickListener {
            // 기존에 로그인한 계정 확인
            googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
            if (googleSignInAccount != null)
                signOut() // 이미 로그인 상태라면 로그아웃 실행
            else
                signIn() // 로그인 상태가 아니라면 로그인 실행
        }
    }

    private fun signIn() {
        // GoogleSignInClient를 통해 인텐트를 생성하고 startActivityForResult를 호출합니다.
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, rcSignIn) // REQ_CODE_SIGN_IN은 요청 코드 상수입니다.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    /* 사용자 정보 가져오기 */
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account.idToken!!)
            }
        } catch (e: ApiException) {
            Log.e(tag, "signInResult:failed code=" + e.statusCode)
        }
    }

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "signInWithCredential:success")
                    Toast.makeText(this@LoginActivity, "로그인 성공입니다!", Toast.LENGTH_SHORT).show()
                    val user = mAuth.currentUser
                    googleUpdateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "로그인 실패입니다!", Toast.LENGTH_SHORT).show()
                    googleUpdateUI(null)
                }
            }
    }

    private fun googleUpdateUI(user: FirebaseUser?) {
        if (user != null) {
            // 사용자가 로그인에 성공했을 경우
            Log.d(tag, "updateUI: Login success with ${user.displayName}")

            // 사용자 정보(이름 및 프로필 사진 URL)를 ChosenActivity로 전달
            val intent = Intent(this, ChosenActivity::class.java).apply {
                putExtra("Nickname", user.displayName) // 닉네임 전달
                putExtra("ProfileImageUrl", user.photoUrl.toString()) // 프로필 사진 URL 전달
            }
            startActivity(intent)
            finish() // 현재 LoginActivity를 종료하여 뒤로 가기 버튼 히트 시 LoginActivity로 돌아오는 것을 방지
        } else {
            // 로그인 실패 혹은 사용자 정보가 없을 경우
            Log.w(tag, "updateUI: No user info available after login.")
            Toast.makeText(this, "로그인에 실패하였습니다: 사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signOut() {
        // Google 로그아웃
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // FirebaseAuth에서도 로그아웃
            mAuth.signOut()
            Toast.makeText(this@LoginActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            // 로그아웃 후 UI 업데이트 과정 추가 (예: 로그인 화면으로 이동)
//            updateUIAfterSignOut()
        }
    }

    /* 로그아웃 후 실행할 UI 업데이트 */
//    private fun updateUIAfterSignOut() {
//        val intent = Intent(this, LoginActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        finish() // 현재 액티비티 종료
//    }

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
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                val nickname = user.kakaoAccount?.profile?.nickname
                val profileImageUrl = user.kakaoAccount?.profile?.profileImageUrl // 프로필 이미지 URL 추가
                if (nickname != null && profileImageUrl != null) {
                    // Shared Preferences에 로그인 상태, 닉네임, 프로필 이미지 URL 저장
                    val prefs = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).edit()
                    prefs.putBoolean("IsLoggedIn", true)
                    prefs.putString("Nickname", nickname) // 닉네임 저장
                    prefs.putString("ProfileImageUrl", profileImageUrl) // 프로필 이미지 URL 저장
                    prefs.apply()

                    // 사용자 정보 요청이 성공한 후 ChosenActivity로 이동
                    val intent = Intent(this@LoginActivity, ChosenActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e(TAG, "닉네임이나 프로필 이미지 URL이 없습니다.")
                }
            }
        }
    }

    // ===========일반 로그인 ===========
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
