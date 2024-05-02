package com.example.airandweather.myPage

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
import com.example.airandweather.Login.LoginActivity
import com.example.airandweather.MainActivity
import com.example.airandweather.R
import com.example.airandweather.databinding.ActivityMypageEditBinding
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

class MyPageEdit : AppCompatActivity() {
    // 뷰 바인딩, 데이터베이스, DAO 초기화
    private lateinit var binding: ActivityMypageEditBinding
    lateinit var db: AppDatabase
    lateinit var memberDao: MemberDao
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 레이아웃 설정
        binding = ActivityMypageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 프로필 이미지의 모서리를 둥글게 처리
        binding.editImage.clipToOutline = true

        // 데이터베이스 인스턴스 초기화
        db = AppDatabase.getInstance(this)!!
        memberDao = db.getMemberDao()

        // SharedPreferences로부터 사용자 정보 가져오기
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val userNickName = sharedPreferences.getString("Nickname", "")
        val userEmail = sharedPreferences.getString("Email", "")
        val userPassword = sharedPreferences.getString("Password", "")
        val base64ImageString = sharedPreferences.getString("ProfileImageUrl", null)

        // SharedPreferences로부터 받은 이미지 데이터 처리
        base64ImageString?.let {
            val imageBytes = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.editImage.setImageBitmap(bitmap)
        }

        // TextView에 밑줄이 있는 텍스트 설정
        binding.editTextEmail.setText(userEmail)
        binding.editTextNickName.setText(userNickName)
        binding.editTextPassword.setText(userPassword)

        // 회원정보 수정 버튼 클릭 이벤트 설정
        binding.updateButton.setOnClickListener {
            lifecycleScope.launch {
                update() // 사용자 정보 업데이트 함수
            }
        }

        // 프로필 이미지 선택 이벤트 설정
        binding.editImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            activityResult.launch(intent)
        }
    }

    private suspend fun update() {
        val updateEmail = binding.editTextEmail.text.toString()
        val updatePassword = binding.editTextPassword.text.toString()
        val updateNickName = binding.editTextNickName.text.toString()

        val sharedPreferences = this.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val userMno = sharedPreferences.getString("mno", null)?.toIntOrNull()

        // 입력 유효성 검사 실패 시 함수 종료
        if (!validateInputs(updateEmail, updatePassword, updateNickName)) return

        // 이미지 처리 및 바이트 배열 가져오기
        val imageByteArray: ByteArray = processImageForSignUp() ?: return
        // 이미지 바이트 배열 로깅
        Log.d("imageByteArray", "$imageByteArray")

        lifecycleScope.launch {
            // userMno가 null이 아닐 때만 조회
            val existingMember = userMno?.let { memberDao.getMemberById(it) }

            if (existingMember != null) {
                // 기존 사용자 정보를 새 정보로 업데이트합니다.
                val updateEntity = MemberEntity(userMno, updateEmail, updatePassword, updateNickName, imageByteArray)
                memberDao.updateMember(updateEntity)

                Toast.makeText(this@MyPageEdit, "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()

                // 업데이트된 정보를 SharedPreferences에 저장
                val editor = sharedPreferences.edit()
                editor.putString("Nickname", updateEntity.nickName)
                editor.putString("Email", updateEntity.email)
                editor.putString("Password", updateEntity.password)

                if (imageByteArray.isNotEmpty()) {
                    saveImageAndPathInPreferences(imageByteArray, updateNickName)
                }
                editor.apply()

                navigateToLogin()
            } else {
                // 현재는 userMno을 통해 동일한 회원을 찾지 못하는 경우가 매우 드물 것으로 예상됩니다.
                // 로직에 따라 적절한 처리가 필요할 수 있습니다.
                Toast.makeText(this@MyPageEdit, "회원 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
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
            binding.editImage.setImageBitmap(bitmap)
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

    // 추가: 이미지를 파일로 저장하고 파일 경로를 SharedPreferences에 저장하는 함수
    private fun saveImageAndPathInPreferences(imageByteArray: ByteArray, updateNickName: String) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${updateNickName}_$timeStamp.jpg"

        // 파일을 내부 저장소에 저장
        val file = File(this@MyPageEdit.filesDir, fileName)
        file.writeBytes(imageByteArray)

        // 파일 경로를 SharedPreferences에 저장
        getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).edit().apply {
            putString("UserImageFilePath", file.absolutePath)
            apply()
        }
    }

    // ============== 수정 페이지 유효성 검사 ====================
    // 비밀번호 유효성 검사 함수
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    // 이메일 유효성 검사 함수
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
                Toast.makeText(this@MyPageEdit, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 수정이 성공적으로 완료된 후 마이페이지 화면으로 이동
    private fun navigateToLogin() {
        val intent = Intent(this@MyPageEdit, MyPage::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}









