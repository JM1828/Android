package com.example.fragmentex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.fragmentex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FragmentTransection의 객체를 생성해서 프레그먼트를 화면에 출력
        val fragment1 = OneFragment()
        val fragment2 = TwoFragment()

        // 처음 실행 시 프래그먼트1 나오게 하기 위해 호출!!
        changeContent(fragment1)

        binding.navMenu.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.item_f1 -> {
                    changeContent(fragment1)
                    true
                }
                R.id.item_f2 -> {
                    changeContent(fragment2)
                    true
                }
                else -> false
            }
        }
        /*
            add - 새로운 프래그먼트를 화면에 추가
            replace() - 추가된 프래그먼트를 대체함
            remove() - 추가된 프래그먼트를 제거
            commit() - 화면에 적용
         */
    }

    // 프래그먼트 전환 함수
     fun changeContent(fragment: Fragment) {
        val fragmentManager : FragmentManager = supportFragmentManager
        val transaction : FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.main, fragment)
        transaction.commit()
    }
}