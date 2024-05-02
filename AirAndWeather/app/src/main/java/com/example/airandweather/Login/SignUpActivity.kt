package com.example.airandweather.Login

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.airandweather.MainActivity
import com.example.airandweather.R
import com.example.airandweather.databinding.ActivitySignupBinding
import com.example.airandweather.db.AppDatabase
import com.example.airandweather.db.Converters
import com.example.airandweather.db.MemberDao
import com.example.airandweather.db.MemberEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    lateinit var db: AppDatabase
    lateinit var memberDao: MemberDao
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userImageView.clipToOutline = true
        db = AppDatabase.getInstance(this)!!
        memberDao = db.getMemberDao()

        // 홈 버튼 클릭시 메인 엑티비티
        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 회원가입 화면에서의 사용자 상호작용을 설정
        binding.signupButton.setOnClickListener {
            lifecycleScope.launch {
                signUp()
            }
        }

        // 이미지 선택 및 회원가입 버튼에 대한 클릭 리스너 설정
        binding.btnImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }
    }

    // 회원가입 로직을 처리하는 함수
    private suspend fun signUp() {
        // 입력 필드에서 값 추출
        val email = binding.profileEmail.text.toString()
        val password = binding.profilePassword.text.toString()
        val nickName = binding.profileNickname.text.toString()

        // 입력 유효성 검사 실패 시 함수 종료
        if (!validateInputs(email, password, nickName)) return

        // 이미지 처리 및 바이트 배열 가져오기
        val imageByteArray: ByteArray = processImageForSignUp() ?: return
        // 이미지 바이트 배열 로깅
        Log.d("imageByteArray", "$imageByteArray")

        // 회원정보 등록
        registerNewMember(email, password, nickName, imageByteArray)
    }

    // 갤러리에서 이미지 선택했을 때의 콜백 처리
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            result.data!!.data?.let { uri ->
                handleImageSelection(uri)
                imageUri = uri
            }
        } else {
            Log.d("ImageSelection", "이미지 선택 실패: 사용자가 이미지를 선택하지 않았거나 기타 오류가 발생했습니다.")
        }
    }

    // 사용자가 선택한 이미지 처리
    private fun handleImageSelection(uri: Uri) {
        lifecycleScope.launch {
            val byteData = convertUriToByteArray(uri)
            val bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.size)
            // ImageView 업데이트
            binding.userImageView.setImageBitmap(bitmap)
            Log.d("ImageSelection", "Selected image URI: $uri")
        }
    }

    // 특정 URI의 이미지를 비트맵으로 변환
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            contentResolver.openInputStream(uri).use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: FileNotFoundException) {
            Log.e("uriToBitmap", "File not found", e)
            null
        }
    }

    // URI에서 바이트 배열로 변환
    private suspend fun convertUriToByteArray(uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        uriToBitmap(uri)?.let { bitmap ->
            ByteArrayOutputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            }
        } ?: byteArrayOf().apply {
            Log.e("convertUriToByteArray", "Bitmap conversion failed")
        }
    }

    // 기본 이미지를 바이트 배열로 변환
    private suspend fun getDefaultImageByteArray(): ByteArray = withContext(Dispatchers.IO) {
        try {
            val bitmap =
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.profileee)
            ByteArrayOutputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            }
        } catch (e: Exception) {
            Log.e("getDefaultImage", "Default image loading failed", e)
            byteArrayOf()
        }
    }

    // 회원가입을 위한 이미지 처리
    private suspend fun processImageForSignUp(): ByteArray? = withContext(Dispatchers.IO) {
        Log.d("imageConversion", "이미지 변환 작업을 시작합니다.")
        val converters = Converters()
        imageUri?.let {
            Log.d("imageUri", "현재 imageUri = $it")
            uriToBitmap(it)?.let { bitmap ->
                val byteArray = converters.fromBitmap(bitmap)
                Log.d("imageConversion", "이미지 변환 작업이 완료되었습니다.")
                byteArray
            } ?: run {
                Log.d("imageConversion", "비트맵으로의 변환 실패. 빈 바이트 배열을 반환합니다.")
                byteArrayOf()
            }
        } ?: run {
            Log.d("imageConversion", "imageUri가 null입니다. 기본 이미지로 대체합니다.")
            getDefaultImageByteArray()
        }
    }

    // 새로운 회원 정보를 데이터베이스에 등록
    private fun registerNewMember(
        email: String,
        password: String,
        nickName: String,
        imageByteArray: ByteArray
    ) {
        lifecycleScope.launch {
            val existingMember = memberDao.findMemberByEmail(email)
            if (existingMember == null) {
                val newMember = MemberEntity(null, email, password, nickName, imageByteArray)
                memberDao.insertMember(newMember)
                showToast("회원가입이 완료되었습니다.")
                saveImageAndPathInPreferences(imageByteArray, nickName)
                navigateToLogin()
            } else {
                showToast("이미 가입된 계정입니다.")
            }
        }
    }

    // 이미지 파일을 저장하고, 파일 경로를 SharedPreferences에 저장
    private fun saveImageAndPathInPreferences(imageByteArray: ByteArray, nickName: String) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${nickName}_$timeStamp.jpg"

        // 파일을 내부 저장소에 저장
        val file = File(this@SignUpActivity.filesDir, fileName)
        file.writeBytes(imageByteArray)

        // 파일 경로를 SharedPreferences에 저장
        getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).edit().apply {
            putString("UserImageFilePath", file.absolutePath)
            apply()
        }
    }

    // ============== 회원가입 유효성 검사 ===============
    // 비밀번호가 유효한지 확인, 유효한 비밀번호는 길이가 6자 이상
    private fun isValidPassword(password: String): Boolean = password.length >= 6

    // 이메일 유효성 검사를 위해 정규 표현식을 사용
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    // 이메일, 비밀번호, 닉네임 입력값의 유효성을 검사
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

    // Toast 메시지를 사용하여 사용자에게 짧은 메시지를 표시
    private fun showToast(message: String) {
        runOnUiThread {
            if (!isFinishing) {
                Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 회원가입이 성공적으로 완료된 후 로그인 화면으로 이동
    private fun navigateToLogin() {
        val intent = Intent(this@SignUpActivity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}
