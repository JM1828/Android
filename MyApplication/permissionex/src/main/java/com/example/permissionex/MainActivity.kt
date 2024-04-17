package com.example.permissionex

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestPermissionLauncher = registerForActivityResult(
            // 권한 요청을 처리하는 데 사용
            ActivityResultContracts.RequestPermission()
        ) {
            // "isGranted" 변수는 권한이 부여되었는지를 나타내는 불리언 값
                isGranted ->
            if (isGranted) {
                Log.d("test", "granted 콜백 실행됨~")
            } else {
                Log.d("test", "denied 콜백 실행됨..")
            }
        }

        val callBtn = findViewById<ImageView>(R.id.callBtn)

        callBtn.setOnClickListener {
            // ContextCompat.checkSelfPermission() 메소드는 지정된 앱에서 특정 권한이 부여되었는지 여부를 확인하는 데 사용
            // "android.permission.CALL_PHONE"는 전화 걸기 권한을 나타내며, 이 코드는 현재 앱에서 전화 걸기 권한이 부여되었는지를 확인하는 것
            val status = ContextCompat.checkSelfPermission(this, "android.permission.CALL_PHONE")
            // 전화 걸기 권한이 부여된 경우
            if (status == PackageManager.PERMISSION_GRANTED) {
                // 전화 걸기
                Toast.makeText(this, "전화를 겁니다!!", Toast.LENGTH_SHORT).show()
                // "전화를 걸기" 동작을 수행하도록 요청하는 인텐트를 생성
                var intent = Intent(Intent.ACTION_CALL)
                var num = "tel:010-5604-1828"
                // Uri.parse() 메서드는 문자열을 Uri로 변환하는 데 사용
                // "num" 문자열을 Uri로 변환하여 Intent의 데이터로 설정
                intent.data = Uri.parse(num)

                // 새로운 액티비티를 시작하는 데 사용되는 메소드
                startActivity(intent)
            } else {
                Toast.makeText(this, "권한을 허용해주세요!!", Toast.LENGTH_SHORT).show()
                // 퍼미션 허용 요청 실행
                // 사용자에게 권한을 요청하는 시스템 다이얼로그를 표시하고, 사용자가 권한을 부여하거나 거부할 때 등록된 콜백 함수가 실행
                requestPermissionLauncher.launch("android.permission.CALL_PHONE")
            }
        }

        val dateBtn = findViewById<Button>(R.id.dateBtn)

        dateBtn.setOnClickListener {
            // DatePickerDialog는 안드로이드에서 제공하는 시스템 다이얼로그 중 하나로, 사용자가 날짜를 선택할 수 있는 대화형 창을 표시
            // 날짜가 선택되었을 때의 동작을 정의하는 리스너 객체를 생성
            DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    Log.d("test", "year: ${year}, month: ${month + 1}, day: ${dayOfMonth}")
                }
                // 2024, 4, 16: DatePickerDialog가 표시될 때 초기에 선택되어 있는 년도, 월, 일을 나타냄
            }, 2024, 4, 16).show()
        }

        val timeBtn = findViewById<Button>(R.id.timeBtn)

        timeBtn.setOnClickListener {
            // TimePickerDialog는 안드로이드에서 제공하는 시스템 다이얼로그 중 하나로, 사용자가 시간을 선택할 수 있는 대화형 창을 표시
            // 시간이 선택되었을 때의 동작을 정의하는 리스너 객체를 생성
            TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    Log.d("test", "hourOfDay: ${hourOfDay}, minute: ${minute}")
                }
                // 13은 초기에 선택되어 있는 시간(시), 0은 초기에 선택되어 있는 시간(분), true는 24시간 형식을 사용할지 여부를 나타냄
            }, 13, 0, true).show()
        }

        val eventHandler = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Toast.makeText(applicationContext, "네 버튼을 눌렀어요!", Toast.LENGTH_SHORT).show()
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    Toast.makeText(applicationContext, "아니요 버튼을 눌렀어요!", Toast.LENGTH_SHORT).show()

                }
            }
        }

        //alertDialog 띄우기
        val alertBtn = findViewById<Button>(R.id.alertBtn)

        alertBtn.setOnClickListener {
            AlertDialog.Builder(this).run {
                setTitle("테스트 다이얼로그")
                setIcon(android.R.drawable.ic_dialog_alert)
                setMessage("정말 종료하시겠습니까??")
                setPositiveButton("네", eventHandler)
                setNegativeButton("아니오", eventHandler)
                setNeutralButton("더보기", null)
                setPositiveButton("YES", eventHandler)
                setNegativeButton("NO", eventHandler)
                show()


            }
        }


        val alertCheckBtn = findViewById<Button>(R.id.alertCheckBtn)

        alertCheckBtn.setOnClickListener {
            val items = arrayOf<String>("사과", "복숭아", "수박", "딸기")



            AlertDialog.Builder(this).run {
                setTitle("체크박스 다이얼로그")
                setIcon(android.R.drawable.ic_dialog_info)
                setMultiChoiceItems(
                    items,
                    booleanArrayOf(true, false, true, false),
                    object : DialogInterface.OnMultiChoiceClickListener {
                        override fun onClick(
                            dialog: DialogInterface?,
                            which: Int,
                            isChecked: Boolean
                        ) {
                            Toast.makeText(
                                applicationContext,
                                "${items[which]}이 ${if (isChecked) "선택되었습니다." else "선택 해제되었습니다."}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                setPositiveButton("닫기", null).show()
            }

        }
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater.inflate(R.layout.activity_one,null)

        val alertCustomBtn = findViewById<Button>(R.id.alertCustomBtn)

        alertCustomBtn.setOnClickListener {
            AlertDialog.Builder(this).run {
                setTitle("Input")
                setView(rootView)
                setPositiveButton("닫기", null)
                show()
            }
        }
        /*
        바인딩
        val customBtn = findViewById<Button>(R.id.customBtn)

        customBtn.setOnClickListener {
            val dialogBinding = DialogInputBinding.inflate(layoutInflater)
            AlertDialog.Builder(this).run {
                setTitle("Input")
                setView(dialogBinding.root)
                setPositiveButton("닫기",null)
                show()
            }
        }*/


    }



}