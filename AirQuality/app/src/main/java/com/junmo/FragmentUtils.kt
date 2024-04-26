package com.junmo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.junmo.airquality.LocationProvider
import com.junmo.airquality.MapActivity
import com.junmo.airquality.R
import com.junmo.airquality.databinding.FragmentOneBinding
import com.junmo.airquality.databinding.FragmentTwoBinding
import com.junmo.airquality.retrofit.AirQualityResponse
import com.junmo.airquality.retrofit.AirQualityService
import com.junmo.airquality.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.lang.IllegalArgumentException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object FragmentUtils {

    private var _binding1: FragmentOneBinding? = null
    private var _binding2: FragmentTwoBinding? = null
    private val Airbinding get() = _binding1!!
    private val Weatherbinding get() = _binding2!!

    // 위치 권한 요청에 사용되는 요청 코드를 정의
    private val PERMISSIONS_REQUEST_CODE = 100

    private lateinit var locationProvider: LocationProvider
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    // 필요한 위치 권한을 배열로 정의
    val REQUIRED_PERMISSIONS = arrayOf(
        // 정확한 위치 정보에 접근하기 위한 권한을 나타냄
        Manifest.permission.ACCESS_FINE_LOCATION,
        // 대략적인 위치 정보에 접근하기 위한 권한을 나타냄
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    // Intent를 입력으로 받아 액티비티 결과를 처리하는 데 사용
    lateinit var getGPSPermissionLauncher: ActivityResultLauncher<Intent>

    val startMapActivityResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    if (result?.resultCode ?: 0 == Activity.RESULT_OK) {
                        latitude = result?.data?.getDoubleExtra("latitude", 0.0) ?: 0.0
                        longitude = result?.data?.getDoubleExtra("longitude", 0.0) ?: 0.0
                        updateUI(context)
                    }
                }
            }
        )

    // 현재 위치 정보를 가져와 UI를 업데이트하는 메서드
    fun updateUI(context: Context) {
        // LocationProvider를 초기화하여 현재 위치 정보를 가져옴
        context?.let {
            locationProvider = LocationProvider(it)
        }

        if (latitude == 0.0 && longitude == 0.0) {
            latitude = locationProvider.getLocationLatitude()
            longitude = locationProvider.getLocationLongitude()
        }

        if (latitude != null && longitude != null) {
            val address = getCurrentAddress(latitude!!, longitude!!, context)
            address?.let {
                val defaultText = "알 수 없는 도로"
                val addressText = address?.thoroughfare ?: address?.featureName ?: address?.subLocality ?: address?.locality ?: defaultText
                Airbinding.tvLocationTitle.text = addressText
                Airbinding.tvLocationSubtitle.text = "${it.countryName} ${it.adminArea}"
                Weatherbinding.tvLocationTitle.text = addressText
                Weatherbinding.tvLocationSubtitle.text = "${it.countryName} ${it.adminArea}"
            }
            getWeatherData(latitude!!, longitude!!, context)
        } else {
            Toast.makeText(context, "위도, 경도 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
        }
    }

    fun getWeatherData(latitude: Double, longitude: Double, context: Context) {
        var retrofitAPI = RetrofitConnection.getInstance().create(
            AirQualityService::class.java
        )
        // Retrofit을 사용하여 API 요청을 보냄, 응답을 비동기적으로 처리하기 위해 enqueue 메서드를 사용하고, 응답을 처리하기 위한 Callback을 정의
        retrofitAPI.getAirQualityData(
            latitude.toString(),
            longitude.toString(),
            "2d32be22-a9fc-41b7-8d13-85c79fc1272b"
        ).enqueue(object : retrofit2.Callback<AirQualityResponse> {
            // 응답이 성공적이면 사용자에게 메시지를 표시하고, 받은 데이터를 UI에 업데이트하는 updateAirUI 함수를 호출
            override fun onResponse(
                call: Call<AirQualityResponse>,
                response: Response<AirQualityResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "최신 데이터 업데이트 완료!", Toast.LENGTH_LONG)
                        .show()
                    // 응답에 body 값이 null 이 아니면 UI를 업데이트
                    response.body()?.let {
                        updateWeatherUI(it)
                        updateAirUI(it)
                    }
                } else {
                    Toast.makeText(context, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<AirQualityResponse>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(context, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    // 미세먼지 UI 업데이트
    private fun updateAirUI(airQualityData: AirQualityResponse) {
        val pollutionData = airQualityData.data.current.pollution

        // 수치를 지정
        Airbinding.tvCount.text = pollutionData.aqius.toString()

        // 측정된 날짜
        val dateTime =
            ZonedDateTime.parse(pollutionData.ts).withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        Airbinding.tvCheckTime.text = dateTime.format(dateFormatter).toString()

        when (pollutionData.aqius) {
            in 0..50 -> {
                Airbinding.tvTitle.text = "좋음"
                Airbinding.imgBg.setImageResource(R.drawable.bg_good)
            }
            in 51..150 -> {
                Airbinding.tvTitle.text = "보통"
                Airbinding.imgBg.setImageResource(R.drawable.bg_soso)
            }
            in 151..200 -> {
                Airbinding.tvTitle.text = "나쁨"
                Airbinding.imgBg.setImageResource(R.drawable.bg_bad)
            }
            else -> {
                Airbinding.tvTitle.text = "매우 나쁨"
                Airbinding.imgBg.setImageResource(R.drawable.bg_worst)
            }
        }
    }

    // 날씨 UI 업데이트
    private fun updateWeatherUI(airQualityData: AirQualityResponse) {
        val currentWeather = airQualityData.data.current.weather
        val ts = airQualityData.data.current.weather.ts

        val zonedDateTime = ZonedDateTime.parse(ts)
        val koreaDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"))

        // 날짜 형식 지정
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd a hh:mm", Locale.KOREA)

        Weatherbinding.tvCheckTime.text = koreaDateTime.format(dateFormatter).toString()
        Weatherbinding.temp.text = "${currentWeather.tp}°C"
        Weatherbinding.humidity.text = "${currentWeather.hu}%"
        Weatherbinding.wind.text = "${currentWeather.ws} m/s"
        val weatherIconResourceId = getWeatherDescription(currentWeather.ic)
        weatherIconResourceId?.let {
            Weatherbinding.status.setImageResource(it)
        }
    }

    // 날씨에 따라 아이콘 변경
    private fun getWeatherDescription(weatherIcon: String): Int? {
        return when (weatherIcon) {
            "01d" -> R.drawable.icon_01d
            "01n" -> R.drawable.icon_01n
            "02d" -> R.drawable.icon_02d
            "02n" -> R.drawable.icon_02n
            "03d", "03n" -> R.drawable.icon_03d
            "04d", "04n" -> R.drawable.icon_04d
            "09d", "09n" -> R.drawable.icon_09d
            "10d", "10n" -> R.drawable.icon_10d
            "11d", "11n" -> R.drawable.icon_11d
            "13d", "13n" -> R.drawable.icon_13d
            "50d", "50n" -> R.drawable.icon_50d
            else -> null
        }
    }

    private fun setRefreshButtonAir(context: Context) {
        Airbinding.btnRefresh.setOnClickListener {
            updateUI(context)
        }
    }

    private fun setRefreshButtonWeather(context: Context) {
        Airbinding.btnRefresh.setOnClickListener {
            updateUI(context)
        }

         fun setFab(context: Context) {
             Airbinding.fab.setOnClickListener {
                // Intent 생성 시 Context를 전달하기 위해 requireContext()를 사용
                val intent = Intent(context, MapActivity::class.java).apply {
                    putExtra("currentLat", latitude)
                    putExtra("currentLng", longitude)
                }
                // startMapActivityResult를 통해 Activity 시작
                startMapActivityResult.launch(intent)
            }
        }
    }

    // getCurrentAddress 등의 추가적인 메서드 구현
    private fun getCurrentAddress(latitude: Double, longitude: Double, context: Context): Address? {
        val geoCoder = Geocoder(context, Locale.KOREA) // 'context' 대신 'requireContext()' 사용
        val addresses: List<Address>?

        addresses = try {
            geoCoder.getFromLocation(latitude, longitude, 7)
        } catch (ioException: IOException) {
            Toast.makeText(context, "지오코더 서비스 사용불가합니다.", Toast.LENGTH_LONG).show()
            return null
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(context, "잘못된 위도, 경도 입니다.", Toast.LENGTH_LONG).show()
            return null
        }

        if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(context, "주소가 발견되지 않았습니다.", Toast.LENGTH_LONG).show()
            return null
        }
        return addresses[0]
    }
}