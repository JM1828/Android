package com.example.receiverex

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            Intent.ACTION_SCREEN_ON -> Log.d("test", "화면이 켜졌습니다~")
            Intent.ACTION_SCREEN_OFF -> Log.d("test", "화면이 off됐습니다!!!")
        }
    }
}