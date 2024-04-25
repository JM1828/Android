package com.junmo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.junmo.airquality.databinding.ActivityFragmentBinding

class FragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 프래그먼트 어댑터 이용
        binding.viewpager.adapter = MyFragmentPagerAdapter(this)
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    }

    // 프래그먼트 어댑터 이용
    class MyFragmentPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        val fragment: List<Fragment>
        init {
            fragment = listOf(OneFragment(), TwoFragment())
        }

        override fun getItemCount(): Int {
            return fragment.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragment[position]
        }
    }
}