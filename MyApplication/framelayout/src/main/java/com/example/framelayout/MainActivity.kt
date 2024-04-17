package com.example.framelayout

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.framelayout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.b1.setOnClickListener{
            binding.cat1.visibility = View.VISIBLE
            binding.cat2.visibility = View.INVISIBLE


        }
        binding.b2.setOnClickListener{
            binding.cat1.visibility = View.INVISIBLE
            binding.cat2.visibility = View.VISIBLE


        }
    }
}