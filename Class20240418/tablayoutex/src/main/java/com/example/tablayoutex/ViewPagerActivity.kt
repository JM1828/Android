package com.example.tablayoutex

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.ch12_material.OneFragment
import com.example.ch12_material.ThreeFragment
import com.example.ch12_material.TwoFragment

import com.example.tablayoutex.databinding.ActivityMainViewpagerBinding
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding = ActivityMainViewpagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tabLayout = binding.tabs
        val viewPager = binding.viewpager

        //프래그먼트 어댑터이용
        viewPager.adapter =MyFragmentAdapter(this)
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        //탭과 뷰페이지 연동
        TabLayoutMediator(tabLayout, viewPager){tab,position ->
            tab.text = "탭${(position+1)}"

        }.attach()
    }
}
class MyFragmentAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity){
    val fragments: List<Fragment>
    init{
        fragments = listOf(OneFragment(), TwoFragment(), ThreeFragment())

    }

    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}