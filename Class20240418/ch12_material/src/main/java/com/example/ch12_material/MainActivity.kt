package com.example.ch12_material

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ch12_material.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    // FragmentStateAdapter는 ViewPager2와 함께 사용되어 여러 개의 프래그먼트를 전환하고 표시하는 데 사용
    // 생성자에서 FragmentActivity를 매개변수로 받고 있으며, 이를 FragmentStateAdapter의 생성자에 전달
    class MyFragmentPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        val fragments: List<Fragment>

        // 이니셜라이저 블록에서 fragments 리스트를 초기화
        init {
            // OneFragment, TwoFragment, ThreeFragment를 리스트로 묶어 fragments에 할당
            fragments = listOf(OneFragment(), TwoFragment(), ThreeFragment())
        }

        // getItemCount() 함수를 오버라이드하여 fragments 리스트의 크기를 반환
        // 이 값은 ViewPager2에서 페이지의 개수를 나타내는 데 사용
        override fun getItemCount(): Int = fragments.size

        // createFragment() 함수를 오버라이드하여 해당 위치의 프래그먼트를 반환
        // ViewPager2에서 페이지를 생성하는 데 사용
        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바를 액티비티의 액션바로 설정
        setSupportActionBar(binding.toolbar)
        // ActionBarDrawerToggle을 생성하여 네비게이션 드로어와 툴바를 연결
        toggle = ActionBarDrawerToggle(
            this, binding.drawer, R.string.drawer_opened, R.string.drawer_closed
        )
        // 네비게이션 아이콘을 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 토글 상태를 동기화
        toggle.syncState()

        // MyFragmentPagerAdapter를 생성하고 ViewPager에 어댑터를 설정
        val adapter = MyFragmentPagerAdapter(this)
        binding.viewpager.adapter = adapter
        // TabLayout과 ViewPager를 연결하고, 탭의 텍스트를 설정
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = "Tab${(position + 1)}"
        }.attach()

        // NavigationView를 찾음
        val naviItem = findViewById<NavigationView>(R.id.main_drawer_view)
        // 아이템 선택 이벤트에 대한 리스너를 설정
        naviItem.setNavigationItemSelectedListener {
            // 람다 함수 내에서 선택된 아이템의 title을 로그에 출력하고, true를 반환하여 선택된 아이템 이벤트를 소비하도록 함
            Log.d("test", "${it.title} 선택!!!")
            true
        }

        // 버튼 액션 조절
        val fab = findViewById<ExtendedFloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            when (fab.isExtended) {  // 플로팅 버튼이 확장 상태인지 체크
                true -> fab.shrink() // 확장 상태이면 축소
                false -> fab.extend() // 축소 상태이면 확장
            }
        }
    }

    // 액티비티의 옵션 메뉴를 생성하는 메서드인 onCreateOptionsMenu를 오버라이드
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // menu_main 리소스를 inflate하여 메뉴를 생성
        // 이렇게 하면 menu_main.xml에서 정의한 메뉴 아이템들이 액션 바에 표시됨
        menuInflater.inflate(R.menu.menu_main, menu)
        // 기본 동작을 수행하고, true를 반환하여 메뉴를 표시
        return super.onCreateOptionsMenu(menu)
    }

    // 액티비티의 옵션 메뉴 아이템을 선택했을 때의 동작을 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 토글 버튼에서 제공된 이벤트인지를 확인
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        // 기본 동작을 수행
        return super.onOptionsItemSelected(item)
    }
}