package com.junmo.airquality

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.junmo.airquality.databinding.FragmentOneBinding
import com.junmo.airquality.databinding.FragmentTwoBinding
import com.junmo.airquality.retrofit.AirQualityResponse
import java.io.IOException
import java.lang.IllegalArgumentException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TwoFragment : Fragment() {
    // 뷰모델 및 바인딩 변수 선언
    private lateinit var viewModel: LocationViewModel
    private lateinit var airBinding: FragmentOneBinding
    private lateinit var weatherBinding: FragmentTwoBinding

    // 맵 액티비티 결과를 처리하는 콜백 등록
    private val startMapActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    viewModel.latitude = data.getDoubleExtra("latitude", 0.0)
                    viewModel.longitude = data.getDoubleExtra("longitude", 0.0)
                    updateUI()
                }
            }
        }

    // onCreateView 메서드: 프래그먼트의 뷰를 생성하고 초기화
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // 두 개의 Binding 객체를 초기화
        airBinding = FragmentOneBinding.inflate(inflater, container, false)
        weatherBinding = FragmentTwoBinding.inflate(inflater, container, false)

        // ViewModel을 초기화하고, 현재 액티비티와 연결
        viewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)

        // 주소 텍스트에 대한 LiveData를 관찰하고 UI를 업데이트
        viewModel.addressTextLiveData.observe(viewLifecycleOwner, Observer { addressText ->
            weatherBinding.tvLocationTitle.text = addressText
        })

        // 위치 소제목에 대한 LiveData를 관찰하고 UI를 업데이트
        viewModel.locationSubtitleLiveData.observe(
            viewLifecycleOwner,
            Observer { locationSubtitle ->
                weatherBinding.tvLocationSubtitle.text = locationSubtitle
            })

        // 공기질 데이터에 대한 LiveData를 관찰하고 UI를 업데이트
        viewModel.airQualityData.observe(viewLifecycleOwner, Observer { airQualityResponse ->
            airQualityResponse?.let {
                updateAirUI(it)
            }
        })

        // 날씨 데이터에 대한 LiveData를 관찰하고 UI를 업데이트합니다.
        viewModel.weatherQualityData.observe(viewLifecycleOwner, Observer { weatherQualityData ->
            weatherQualityData?.let {
                updateWeatherUI(it)
            }
        })

        // 추가적인 UI 업데이트를 수행
        updateUI()

        // weatherBinding의 root 뷰를 반환
        return weatherBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRefreshButton()
        setFab()
    }

    // UI 업데이트 메소드
    private fun updateUI() {
        // LocationProvider를 초기화하여 현재 위치 정보를 가져옴
        context?.let {
            val locationProvider = LocationProvider(it)
            viewModel.updateUI(locationProvider, this::getCurrentAddress)
        }
    }

    // FAB(플로팅 액션 버튼)을 설정
    private fun setFab() {
        weatherBinding.searchIcon.setOnClickListener {
            val intent = Intent(requireContext(), MapActivity::class.java).apply {
                putExtra("currentLat", viewModel.latitude)
                putExtra("currentLng", viewModel.longitude)
            }
            startMapActivityResult.launch(intent)
        }
    }

    // 현재 위치의 주소를 가져오는 함수
    private fun getCurrentAddress(latitude: Double, longitude: Double): Address? {
        val geoCoder = Geocoder(requireContext(), Locale.KOREA)
        try {
            // 지정된 위도, 경도로부터 주소를 조회합니다. 최대 결과 개수는 7개
            val addresses = geoCoder.getFromLocation(latitude, longitude, 7)
            if (addresses.isNullOrEmpty()) {
                Toast.makeText(context, "주소가 발견되지 않았습니다.", Toast.LENGTH_LONG).show()
                return null
            }
            // 첫 번째 주소를 반환
            return addresses[0]
        } catch (ioException: IOException) {
            Toast.makeText(context, "지오코더 서비스 사용불가합니다.", Toast.LENGTH_LONG).show()
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(context, "잘못된 위도, 경도 입니다.", Toast.LENGTH_LONG).show()
        }
        return null
    }

    // 날씨 데이터를 UI에 업데이트하는 함수
    private fun updateWeatherUI(weatherData: AirQualityResponse.Data.Current.Weather) {
        // 측정 시간을 'Asia/Seoul' 시간대로 변환하여 표시합니다.
        val zonedDateTime =
            ZonedDateTime.parse(weatherData.ts).withZoneSameInstant(ZoneId.of("Asia/Seoul"))
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd a hh:mm", Locale.KOREA)
        weatherBinding.tvCheckTime.text = zonedDateTime.format(dateFormatter).toString()
        // 온도, 습도, 풍속을 표시합니다.
        weatherBinding.temp.text = "${weatherData.tp}°C"
        weatherBinding.humidity.text = "${weatherData.hu}%"
        weatherBinding.wind.text = "${weatherData.ws} m/s"
        // 날씨 아이콘을 설정합니다.
        getWeatherDescription(weatherData.ic)?.let {
            weatherBinding.status.setImageResource(it)
        }
    }

    // 날씨 아이콘 코드에 따른 리소스 ID 반환 함수
    private fun getWeatherDescription(weatherIcon: String): Int? {
        return when (weatherIcon) {
            "01d" -> R.drawable.icon_01d  // 맑은 낮
            "01n" -> R.drawable.icon_01n  // 맑은 밤
            "02d" -> R.drawable.icon_02d  // 구름이 조금 있는 낮
            "02n" -> R.drawable.icon_02n  // 구름이 조금 있는 밤
            "03d", "03n" -> R.drawable.icon_03d  // 흐림
            "04d", "04n" -> R.drawable.icon_04d  // 구름 많음
            "09d", "09n" -> R.drawable.icon_09d  // 소나기
            "10d", "10n" -> R.drawable.icon_10d  // 비
            "11d", "11n" -> R.drawable.icon_11d  // 천둥번개
            "13d", "13n" -> R.drawable.icon_13d  // 눈
            "50d", "50n" -> R.drawable.icon_50d  // 안개
            else -> null  // 일치하는 아이콘 없음
        }
    }

    // 공기질 데이터를 UI에 업데이트하는 함수
    private fun updateAirUI(airQualityData: AirQualityResponse) {
        val pollutionData = airQualityData.data.current.pollution
        // AQI(공기질 지수)를 표시
        airBinding.tvCount.text = pollutionData.aqius.toString()
        // 측정 시간을 'Asia/Seoul' 시간대로 변환하여 표시
        val dateTime =
            ZonedDateTime.parse(pollutionData.ts).withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        airBinding.tvCheckTime.text = dateTime.format(dateFormatter).toString()
        // AQI 값에 따라 UI를 업데이트
        when (pollutionData.aqius) {
            in 0..50 -> {
                airBinding.tvTitle.text = "좋음"
                airBinding.imgBg.setImageResource(R.drawable.bg_good)
            }

            in 51..150 -> {
                airBinding.tvTitle.text = "보통"
                airBinding.imgBg.setImageResource(R.drawable.bg_soso)
            }

            in 151..200 -> {
                airBinding.tvTitle.text = "나쁨"
                airBinding.imgBg.setImageResource(R.drawable.bg_bad)
            }

            else -> {
                airBinding.tvTitle.text = "매우 나쁨"
                airBinding.imgBg.setImageResource(R.drawable.bg_worst)
            }
        }
    }

    // 새로고침 버튼 설정 함수
    private fun setRefreshButton() {
        weatherBinding.btnRefresh.setOnClickListener {
            updateUI()  // UI 업데이트 메소드 호출
        }
    }
}