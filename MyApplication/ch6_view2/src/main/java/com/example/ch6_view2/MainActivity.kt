package com.example.ch6_view2

import android.os.Bundle
import android.service.voice.VoiceInteractionSession.ActivityId
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ch6_view2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confirmBtn.setOnClickListener {
            var inputPassword = binding.editPassword.text

            if (inputPassword.length <= 0) {
                Toast.makeText(this, "입력된 암호는? ", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(this, "입력된 암호는? ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}