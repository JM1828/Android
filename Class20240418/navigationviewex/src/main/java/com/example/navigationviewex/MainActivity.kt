package com.example.navigationviewex

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val naviItem = findViewById<NavigationView>(R.id.main_drawer_view)
        naviItem.setNavigationItemSelectedListener {
            Log.d("test", "${it.title} 선택!!!")
            true
        }

        // 버튼 액션 조절
        val fab = findViewById<ExtendedFloatingActionButton>(R.id.extendedFab)
        fab.setOnClickListener {
            when(fab.isExtended) {  // 플로팅 버튼이 확장 상태인지 체크
                true -> fab.shrink() // 확장 상태이면 축소
                false -> fab.extend() // 축소 상태이면 확장
            }
        }
    }
}