package com.example.tablayoutex

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ch12_material.OneFragment
import com.example.ch12_material.ThreeFragment
import com.example.ch12_material.TwoFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //탭 생성 및 추가
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val tab1: TabLayout.Tab = tabLayout.newTab()
        tab1.text = "탭1"
        tabLayout.addTab(tab1)

        val tab2: TabLayout.Tab = tabLayout.newTab()
        tab2.text = "탭2"
        tabLayout.addTab(tab2)

        val tab3: TabLayout.Tab = tabLayout.newTab()
        tab3.text = "탭3"
        tabLayout.addTab(tab3)
        //시작 시 첫번째 탭이 선택된 상태로ㅛ 하기 위해서 추가
        supportFragmentManager.beginTransaction().replace(R.id.tabContent, OneFragment()).commit()

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            //탭버튼을 선택할 대 이벤트
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()
                when(tab?.text){
                    "탭1" -> transaction.replace(R.id.tabContent, OneFragment())
                    "탭2" -> transaction.replace(R.id.tabContent, TwoFragment())
                    "탭3" -> transaction.replace(R.id.tabContent, ThreeFragment())
                }
                transaction.commit()
            }

            //다른 탭 버튼을 눌러 선택된 탭 버튼이 해제될 때 이벤트
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            //선택된 탭 버튼을 다시 선택할 때 이벤트
            override fun onTabReselected(tab: TabLayout.Tab?) {}


        })

    }
}