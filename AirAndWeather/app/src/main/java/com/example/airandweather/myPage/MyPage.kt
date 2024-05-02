package com.example.airandweather.myPage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.airandweather.MainActivity
import com.example.airandweather.R
import com.example.airandweather.databinding.ActivityMypageBinding
import com.example.airandweather.db.AppDatabase
import com.example.airandweather.db.MemberDao
import com.example.airandweather.util.drawerUtil.DrawerUtil


// 액티비티의 메인 클래스를 정의합니다.
class MyPage : AppCompatActivity() {

    // 레이아웃 바인딩, 데이터베이스 객체, DAO, 레이아웃 컨트롤러들 선언
    private lateinit var binding: ActivityMypageBinding
    lateinit var db: AppDatabase
    lateinit var memberDao: MemberDao
    private var isDrawerOpen = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // XML 레이아웃을 View와 바인딩합니다.
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences에서 사용자 정보 가져오기
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val userNickName = sharedPreferences.getString("Nickname", "")
        val userEmail = sharedPreferences.getString("Email", "")
        val userPassword = sharedPreferences.getString("Password", "")

        // TextView에 사용자 정보 표시
        binding.savedEmail.text = userEmail
        binding.savedPassword.text = userPassword
        binding.savedNickname.text = userNickName

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

        // 메뉴 아이콘 클릭 시 네비게이션 드로어의 가시성을 토글
        binding.menuIcon.setOnClickListener {
            isDrawerOpen = DrawerUtil.toggleDrawer(binding.navigationDrawer, isDrawerOpen)
        }

        // 메인 레이아웃에 터치 리스너를 설정
        binding.root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && isDrawerOpen) {
                if (!DrawerUtil.isPointInsideView(event.rawX, event.rawY, binding.navigationDrawer)) {
                    isDrawerOpen = DrawerUtil.closeDrawer(binding.navigationDrawer, isDrawerOpen)
                }
            }
            false
        }
    }

    // Activity가 다시 시작될 때마다 호출되는 onResume() 메소드 오버라이드
    override fun onResume() {
        super.onResume()
        loadUserProfileImage()
    }

    // 사용자 프로필 이미지를 로드하는 메소드
    private fun loadUserProfileImage() {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val imagePath = sharedPreferences.getString("UserImageFilePath", null)

        if (imagePath != null) {
            // 저장된 이미지 경로에서 비트맵을 디코딩
            val bitmap = BitmapFactory.decodeFile(imagePath)
            // 디코딩된 비트맵을 ImageView에 설정하여 사용자 프로필 이미지로 표시
            binding.editImage.setImageBitmap(bitmap)
        } else {
            // 기본 이미지 설정
            binding.editImage.setImageResource(R.drawable.profileee)
        }
    }
}
















