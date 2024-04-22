package com.example.ch14_receiver

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ch14_receiver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // registerForActivityResult를 사용하여 ActivityResultContracts.RequestMultiplePermissions를 등록하고, 권한 요청의 결과를 처리
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            // 모든 권한이 허용되었을 경우에는 MyReceiver 클래스로부터 전달받은 intent를 브로드캐스트하여 처리
            if (it.all { permission -> permission.value == true }) {
                val intent = Intent(this, MyReceiver::class.java)
                sendBroadcast(intent)
            } else {
                Toast.makeText(this, "permission denied...", Toast.LENGTH_SHORT).show()
            }
        }

        // 배터리 상태 변경 액션에 대한 브로드캐스트를 동적으로 수신하기 위해 registerReceiver를 사용
        // apply 함수는 객체의 컨텍스트를 변경하지 않고 객체 자체를 반환하는 데 사용
        registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))!!.apply {
            // 배터리가 충전 중인지, 방전 중인지, 혹은 다른 상태에 있는지 등을 확인
            when (getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                // 만약 배터리가 충전 중이면..
                BatteryManager.BATTERY_STATUS_CHARGING -> {
                    // 배터리가 연결된 장치의 정보를 나타내며, getIntExtra 함수는 해당 정보를 인텐트로부터 추출하여 반환
                    when (getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                        // 만약 배터리가 USB 연결을 통해 충전 중이면..
                        BatteryManager.BATTERY_PLUGGED_USB -> {
                            // chargingResultView에 "USB Plugged" 문자열을 설정
                            binding.chargingResultView.text = "USB Plugged"
                            // chargingImageView에 R.drawable.usb 리소스를 설정하여 USB 아이콘 이미지를 표시
                            binding.chargingImageView.setImageBitmap(
                                BitmapFactory.decodeResource(
                                    resources, R.drawable.usb
                                )
                            )
                        }
                        // 만약 AC 전원으로 배터리가 충전 중이면..
                        BatteryManager.BATTERY_PLUGGED_AC -> {
                            // chargingResultView에 "AC Plugged" 문자열을 설정
                            binding.chargingResultView.text = "AC Plugged"
                            // chargingImageView에 R.drawable.ac 리소스를 설정하여 AC 충전기 아이콘 이미지를 표시
                            binding.chargingImageView.setImageBitmap(
                                BitmapFactory.decodeResource(
                                    resources, R.drawable.ac
                                )
                            )
                        }
                    }

                }
                // 배터리가 AC나 USB로 충전 중이 아닌 경우
                else -> {
                    binding.chargingResultView.text = "Not Plugged"
                }
            }
            // getIntExtra 함수를 사용하여 배터리의 잔여량(level)과 배터리의 최대 잔여량(scale)을 가져온 후, 배터리의 백분율 값을 계산
            val level = getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level / scale.toFloat() * 100
            // 계산된 값을 binding을 통해 percentResultView에 표시
            binding.percentResultView.text = "$batteryPct %"
        }

        binding.button.setOnClickListener {
            // 주어진 코드에서는 안드로이드 버전이 Tiramisu (가령 9.0) 이상인지를 확인하는 조건
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // 현재 앱이 특정 권한을 가지고 있는지 확인한 후, PackageManager.PERMISSION_GRANTED와 비교하여 권한이 부여되었는지를 확인
                if (ContextCompat.checkSelfPermission(
                        this,
                        "android.permission.POST_NOTIFICATIONS"
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // 액티비티에서 MyReceiver로 브로드캐스트를 전송
                    val intent = Intent(this, MyReceiver::class.java)
                    sendBroadcast(intent)
                } else {
                    // permissionLauncher는 앱의 권한 요청 및 결과 처리를 위해 사용
                    permissionLauncher.launch(
                        arrayOf(
                            "android.permission.POST_NOTIFICATIONS"
                        )
                    )
                }
            } else {
                // 액티비티에서 MyReceiver로 브로드캐스트를 전송
                val intent = Intent(this, MyReceiver::class.java)
                sendBroadcast(intent)
            }

        }

    }
}