package com.example.stopwatch

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer
import kotlin.concurrent.timer

// View(예: 버튼, 이미지뷰 등)를 클릭했을 때 발생하는 이벤트를 처리하기 위한 리스너 인터페이스
class UpgradeMainActivity : AppCompatActivity(), View.OnClickListener {

    // lateinit 키워드는 나중에 초기화될 것임을 나타내며, 이들은 나중에 코드에서 초기화될 것
    private lateinit var btn_start: Button
    private lateinit var btn_refresh: Button
    private lateinit var tv_minute: TextView
    private lateinit var tv_second: TextView
    private lateinit var tv_millisecond: TextView

    private var isRunning = false

    // 타이머를 나타내는 Timer 객체를 가리키는 변수를 선언
    private var timer: Timer? = null

    // 타이머의 현재 시간을 나타내는 변수를 선언
    private var time = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_upgrade)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btn_start = findViewById(R.id.btn_start)
        btn_refresh = findViewById(R.id.btn_refresh)
        tv_minute = findViewById(R.id.tv_minute)
        tv_second = findViewById(R.id.tv_second)
        tv_millisecond = findViewById(R.id.tv_millisecond)

        // 클릭 이벤트가 발생하면 onClick 메소드가 호출
        btn_start.setOnClickListener(this)
        btn_refresh.setOnClickListener(this)
    }

    // 클릭 이벤트가 발생했을 때 호출되는 메서드로, 클릭된 View의 id에 따라 다양한 동작을 처리
    override fun onClick(view: View?) {
        // 클릭된 View의 id에 따라 다른 동작을 수행하기 위해 when 표현식을 사용
        when (view?.id) {
            // 만약 클릭된 View의 id가 btn_start와 일치하면, isRunning 상태에 따라 pause() 또는 start() 메서드를 호출
            R.id.btn_start -> {
                if (isRunning) {
                    pause()
                } else {
                    start()
                }
            }
            // 만약 클릭된 View의 id가 btn_start와 일치하면, refresh() 를 호출하여 초기화
            R.id.btn_refresh -> {
                refresh()
            }
        }
    }

    // 타이머를 시작하는 메서드
    private fun start() {
        // 버튼의 텍스트를 일시정지로 변경
        btn_start.text = getString(R.string.btn_pause_eng)
        isRunning = true

        // 백그라운드 쓰레드에서는 UI 자원에 접근할수 없다.
        // UI 자원에 접근하기 위해서는 runOnUiThread 함수를 사용하면 된다.
        // 10밀리초마다 time 변수를 증가시키는 타이머를 생성
        timer = timer(period = 10) {
            time++

            // 전체 시간을 100으로 나눈 나머지를 밀리초로 표현
            val milli_second = time % 100
            // 전체 시간을 6000으로 나눈 나머지를 구한 후 100으로 나누어 초를 표현
            val second = (time % 6000) / 100
            // 전체 시간을 6000으로 나눈 몫을 분으로 표현
            val minute = time / 6000

            // runOnUiThread 메서드는 안드로이드에서 UI 업데이트를 수행하는 데 사용
            // UI 업데이트는 메인 스레드에서 이루어져야 함
            // runOnUiThread 메서드를 사용하면 백그라운드 스레드에서 UI를 안전하게 업데이트할 수 있음
            runOnUiThread {
                // isRunning 가 true 인 경우 실행
                if (isRunning) {
                    // milli_second가 10보다 작은 경우 ".0"과 milli_second 값을 붙여 설정
                    tv_millisecond.text = if (milli_second < 10) {
                        ".0${milli_second}"
                        // 그렇지 않은 경우에는 "."과 milli_second 값을 붙여 설정
                    } else {
                        ".${milli_second}"
                    }

                    // second가 10보다 작은 경우 ":0"과 second 값을 붙여 설정
                    tv_second.text = if (second < 10) {
                        ":0${second}"
                        // 그렇지 않은 경우에는 ":"과 second 값을 붙여 설정
                    } else {
                        ":${second}"
                    }

                    // minute의 값을 그대로 설정
                    tv_minute.text = "${minute}"
                }
            }
        }
    }

    // "일시정지" 기능을 하는 메서드
    private fun pause() {
        // "시작" 버튼의 텍스트를 "시작"으로 변경
        btn_start.text = getString(R.string.btn_start_eng)

        // isRunning 변수를 false로 설정하여 타이머가 현재 실행 중이지 않음을 나타냄
        isRunning = false
        // 현재 실행 중인 타이머를 취소
        timer?.cancel()
    }

    // "초기화" 기능을 하는 메서드
    private fun refresh() {
        // 현재 실행 중인 타이머를 취소
        timer?.cancel()
        // "시작" 버튼의 텍스트를 "시작"으로 변경
        btn_start.text = getString(R.string.btn_start_eng)
        // isRunning 변수를 false로 설정하여 타이머가 현재 실행 중이지 않음을 나타냄
        isRunning = false
        // time 변수를 0으로 초기화
        time = 0
        // 각각의 TextView에 "00"으로 시간을 초기화하여 화면에 표시
        tv_millisecond.text = ",00"
        tv_second.text = ":00"
        tv_minute.text = "00"
    }
}