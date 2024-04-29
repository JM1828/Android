package com.example.project.AirAndWeather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.project.databinding.ActivityFragmentBinding

// FragmentActivity: 여러 프래그먼트를 관리하기 위한 액티비티
class FragmentActivity : AppCompatActivity() {
    // View Binding을 사용하여 레이아웃 요소에 쉽게 접근
    lateinit var binding: ActivityFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View Binding 초기화
        binding = ActivityFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewPager2에 프래그먼트 어댑터 설정
        binding.viewpager.adapter = MyFragmentPagerAdapter(this)
        // 슬라이드 방향을 가로로 설정
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    // MyFragmentPagerAdapter: ViewPager2에 사용될 프래그먼트 어댑터
    class MyFragmentPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        // 표시될 프래그먼트 목록
        private val fragments: List<Fragment> = listOf(OneFragment(), TwoFragment())

        // 프래그먼트의 총 개수 반환
        override fun getItemCount(): Int = fragments.size

        // 특정 위치에 해당하는 프래그먼트 반환
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}