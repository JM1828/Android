package com.example.class20240418

import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.class20240418.databinding.ActivityMainBinding
import com.example.class20240418.databinding.ActivityMainCollapsingBinding
import com.example.class20240418.databinding.ActivityMainNestedScrollBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val binding = ActivityMainBinding.inflate(layoutInflater)
//        val binding = ActivityMainNestedScrollBinding.inflate(layoutInflater)
        val binding = ActivityMainCollapsingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 액션바 나오게
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_test, menu)
        return super.onCreateOptionsMenu(menu)
    }
}