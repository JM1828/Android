package com.example.alertex

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "one-channel"
            val channelName = "My Channel One"
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )

            // 채널에 다양한 정보 설정
            channel.description = "My Channel One Description"
            channel.setShowBadge(true)
            val uri: Uri = RingtoneManager.getDefaultUri((RingtoneManager.TYPE_NOTIFICATION))
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            channel.setSound(uri, audioAttributes)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 100, 200)

            // 채널을 NotificationManager에 등록
            manager.createNotificationChannel(channel)

            //채널을 이용해 빌더 생성
            builder = NotificationCompat.Builder(this, channelId)

            // Notification 객체에 알림 정보 설정
            builder.setSmallIcon(android.R.drawable.ic_notification_overlay)
            builder.setWhen(System.currentTimeMillis())
            builder.setContentTitle("알림 제목")
            builder.setContentText("알림 내용")

            //알림 취소 막기(동작안됨)
            //알림 터치해도 사라지지 않음
//            builder.setAutoCancel(false)
            //알림 스와이프해도 사라지지않음
//            builder.setOngoing(true)

            //프로그레스 바
            builder.setProgress(100, 0, false)

            thread{
                for(i in 1 .. 100){
                    builder.setProgress(100,i,false)
                    manager.notify(11, builder.build())
                    SystemClock.sleep(100)
                }
            }

            //알림 발생
            manager.notify(11,builder.build())
        } else {
            builder = NotificationCompat.Builder(this)
        }

        val close_alert_Btn = findViewById<Button>(R.id.close_alert_Btn)
        close_alert_Btn.setOnClickListener{
            //알림제거
            manager.cancel(11)
        }
    }
//        val sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val ringtone = RingtoneManager.getRingtone(applicationContext,sound)
//        ringtone.play()

//        val player: MediaPlayer = MediaPlayer.create(this,R.raw.drum1)
//        player.start()

//        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val vibratorManager =
//                this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
//            vibratorManager.defaultVibrator
//        } else {
//            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
//        } else {
//            vibrator.vibrate(500)
//        }
//    }
}