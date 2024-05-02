package com.example.airandweather.myPage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.airandweather.AirAndWeather.FragmentActivity
import com.example.airandweather.MainActivity
import com.example.airandweather.R
import com.example.airandweather.databinding.ActivityMypageBinding
import com.example.airandweather.db.AppDatabase
import com.example.airandweather.db.MemberDao


// 액티비티의 메인 클래스를 정의합니다.
class MyPage : AppCompatActivity() {

    // 레이아웃 바인딩, 데이터베이스 객체, DAO, 레이아웃 컨트롤러들 선언
    private lateinit var binding: ActivityMypageBinding
    lateinit var db: AppDatabase
    lateinit var memberDao: MemberDao

    private lateinit var drawerLayout: FrameLayout
    private var isDrawerOpen = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // XML 레이아웃을 View와 바인딩합니다.
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이미지 뷰의 윤곽을 클립합니다.
        binding.imageView.clipToOutline = true

        // Room 데이터베이스 인스턴스화 및 DAO 가져오기
        db = AppDatabase.getInstance(this)!!
        memberDao = db.getMemberDao()

        // 홈 버튼 클릭시 메인 엑티비티
        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 수정 버튼 리스너 설정
        binding.editBtn.setOnClickListener {
            val intent = Intent(this, MyPageEdit::class.java)
            startActivity(intent)
        }

        // SharedPreferences에서 사용자 정보 가져오기
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val userNickName = sharedPreferences.getString("Nickname", "")
        val userEmail = sharedPreferences.getString("Email", "")
        val userPassword = sharedPreferences.getString("Password", "")
        val base64ImageString = sharedPreferences.getString("ProfileImageUrl", null)

        // 프로필 이미지를 Base64 문자열에서 비트맵으로 디코딩하여 설정
        base64ImageString?.let {
            val imageBytes = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.editImage.setImageBitmap(bitmap)
        }

        // TextView에 사용자 정보 표시
        binding.savedEmail.text = userEmail
        binding.savedPassword.text = userPassword
        binding.savedNickname.text = userNickName

        // 네비게이션 드로어 토글 준비
        drawerLayout = findViewById(R.id.navigation_drawer)
        val showNavigationButton = findViewById<View>(R.id.menu_icon)
        showNavigationButton.setOnClickListener {
            toggleDrawer() // 네비게이션 드로어의 가시성을 토글합니다.
        }

        // 메인 레이아웃 터치 리스너 설정
        val mainLayout: View = findViewById(android.R.id.content)
        mainLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && isDrawerOpen) {
                if (!isPointInsideView(event.rawX, event.rawY, drawerLayout)) {
                    closeDrawer() // 드로어 밖의 영역이 터치되면 드로어를 닫습니다.
                }
            }
            false
        }
    }

    // 네비게이션 드로어의 가시성을 토글하는 함수
    private fun toggleDrawer() {
        if(isDrawerOpen) {
            drawerLayout.visibility = View.GONE
        } else {
            drawerLayout.visibility = View.VISIBLE
        }
        isDrawerOpen = !isDrawerOpen
    }

    // 특정 View 내부의 점을 확인하는 함수
    private fun isPointInsideView(x: Float, y: Float, view: View): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val rect = Rect(location[0], location[1], location[0] + view.width, location[1] + view.height)
        return rect.contains(x.toInt(), y.toInt())
    }

    private fun closeDrawer() {
        drawerLayout.visibility = View.GONE
        isDrawerOpen = false
    }
}
















