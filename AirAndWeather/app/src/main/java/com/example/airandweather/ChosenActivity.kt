package com.example.airandweather

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.bumptech.glide.Glide
import com.example.airandweather.AirAndWeather.FragmentActivity
import com.example.airandweather.AirAndWeather.OneFragment
import com.example.airandweather.Login.LoginActivity
import com.example.airandweather.databinding.ActivityChosenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChosenActivity : AppCompatActivity() {
    // 프래그먼트 인스턴스 초기화
    var oneFragment: OneFragment? = null
    // 뷰 바인딩 변수 초기화
    lateinit var binding: ActivityChosenBinding
    // 위치 권한 요청 결과를 처리하기 위한 ActivityResultLauncher 초기화
    lateinit var getGPSPermissionLauncher: ActivityResultLauncher<Intent>

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    // 권한 요청 코드
    private val PERMISSIONS_REQUEST_CODE = 100
    // 요청할 권한 목록
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION, // 정확한 위치 정보
        Manifest.permission.ACCESS_COARSE_LOCATION, // 대략적인 위치 정보
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 뷰 바인딩 설정
        binding = ActivityChosenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기존 LoginActivity에서 하셨던 것처럼 초기화
        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        scope.launch {
            loginUpdateUI() // Coroutine 안에서 UI 업데이트
        }

        binding.weatherView.setOnClickListener {
            scope.launch {
                handleWeatherViewClick() // 로그인 상태를 비동기적으로 처리
            }
        }

        // 모든 권한 요청 체크
        checkAllPermissions()

        // 위치 서비스 활성화 확인 후 권한 체크
        getGPSPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (isLocationServicesAvailable()) {
                // 위치 서비스 활성화된 경우 권한 체크
                isRunTimePermissionsGranted()
            } else {
                // 위치 서비스 비활성화된 경우 앱 종료 안내
                Toast.makeText(this, "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private suspend fun loginUpdateUI() {
        withContext(Dispatchers.Main) {
            val prefs = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val isLoggedIn = prefs.getBoolean("IsLoggedIn", false)
            val nickname = if (isLoggedIn) prefs.getString("Nickname", "LOGIN") else "LOGIN"
            val profileImageUrl = prefs.getString("ProfileImageUrl", "")

            binding.textLogin.text = nickname

            if (profileImageUrl!!.isNotEmpty()) {
                // Glide를 사용하여 프로필 이미지 보여주기
                Glide.with(this@ChosenActivity).load(profileImageUrl).override(150, 150).into(binding.profileImage)
            }

            binding.placeBlock1.setOnClickListener {
                if (isLoggedIn) {
                    // 로그아웃 로직
                    prefs.edit().apply {
                        putBoolean("IsLoggedIn", false)
                        remove("LoginType")
                        remove("Nickname")
                        remove("ProfileImageUrl")
                        apply()
                        signOut()
                    }

                    Toast.makeText(this@ChosenActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

                    CoroutineScope(Dispatchers.Main).launch {
                        loginUpdateUI()
                    }
                } else {
                    val intent = Intent(this@ChosenActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private suspend fun handleWeatherViewClick() {
        withContext(Dispatchers.Main) {
            val isLoggedIn = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                .getBoolean("IsLoggedIn", false)

            if (isLoggedIn) {
                val intent = Intent(this@ChosenActivity, FragmentActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@ChosenActivity, "로그인 후 이용 가능합니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel() // Coroutine 작업을 취소하여 리소스 방출
    }

    private fun signOut() {
        val googleSignInClient: GoogleSignInClient
        // GoogleSignInOptions 및 GoogleSignInClient 인스턴스 초기화
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google 로그아웃
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Firebase에서 로그아웃
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 모든 권한 체크 메소드
    private fun checkAllPermissions() {
        if (!isLocationServicesAvailable()) {
            // 위치 서비스가 비활성화된 경우 설정 대화상자 표시
            showDialogForLocationServiceSetting()
        } else {
            // 위치 서비스가 활성화된 경우 권한 체크
            isRunTimePermissionsGranted()
        }
    }

    // 위치 서비스 사용 가능 여부 확인
    private fun isLocationServicesAvailable(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // 런타임 권한 부여 여부 확인
    private fun isRunTimePermissionsGranted() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        // 필요한 권한이 부여되지 않은 경우 사용자에게 권한 요청
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
            hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            // 모든 권한이 부여되었는지 확인하기 위해 "grantResults" 배열을 순회하면서 권한이 부여되었는지 확인
            val checkResult = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (checkResult) {
            // 모든 권한이 부여된 경우 UI 업데이트
                oneFragment?.updateUI()
            } else {
                // 권한이 거부된 경우 앱 종료 안내
                Toast.makeText(
                    this,
                    "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    // 위치 서비스 활성화 요청 대화상자 표시
    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("위치 서비스가 꺼져있습니다. 설정해야 앱을 사용할 수 있습니다.")
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialogInterface, i ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
            Toast.makeText(this, "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG).show()
            finish()
        })
        builder.create().show()
    }
}