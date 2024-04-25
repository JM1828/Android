package com.junmo.weather

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.junmo.airquality.LocationProvider
import com.junmo.airquality.MapActivity
import com.junmo.airquality.R
import com.junmo.airquality.databinding.ActivityWeatherBinding
import com.junmo.airquality.retrofit.AirQualityResponse
import com.junmo.airquality.retrofit.AirQualityService
import com.junmo.airquality.retrofit.RetrofitConnection
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.lang.IllegalArgumentException
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class WeatherActivity : AppCompatActivity() {

    lateinit var binding: ActivityWeatherBinding
    lateinit var locationProvider: LocationProvider

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    var latitude: Double? = 0.0
    var longitude: Double? = 0.0

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
                        updateUI()
                    }
                }
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUI()
        setFab()
    }

    private fun updateUI() {
        // LocationProvider를 초기화하여 현재 위치 정보를 가져옴
        locationProvider = LocationProvider(this)

        if (latitude == 0.0 && longitude == 0.0) {
            // latitude와 longitude 변수를 통해 현재 위치의 위도와 경도를 가져옴
            latitude = locationProvider.getLocationLatitude()
            longitude = locationProvider.getLocationLongitude()
        }
        if (latitude != null && longitude != null) {
            // 1. 현재 위치가져오고 UI 업데이트
            val address = getCurrentAddress(latitude!!, longitude!!)
//            if (checkPermission()) {
//                // 권한이 이미 허용된 경우
//                startWeatherTask(latitude, longitude)
//            } else {
//                // 권한을 요청하는 다이얼로그 표시
//                requestPermission()
//            }

            // address가 null이 아닐 때, 해당 주소 정보를 사용하여 UI를 업데이트
            address?.let {
                // thoroughfare : 동을 나타냄 (수영동, 광안동)
                binding.locationTitle.text = "${it.thoroughfare}"
                // countryName : 나라 이름
                binding.address.text = "${it.countryName} ${it.adminArea}"
            }
            getWeatherData(latitude!!, longitude!!)
        } else {
            // 위치 정보를 가져오지 못한 경우의 처리
            Toast.makeText(this, "위도, 경도 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private fun getWeatherData(latitude: Double, longitude: Double) {
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
                    Toast.makeText(this@WeatherActivity, "최신 데이터 업데이트 완료!", Toast.LENGTH_LONG).show()
                    // 응답에 body 값이 null 이 아니면 UI를 업데이트
                    response.body()?.let { updateWeatherUI(it) }
                } else {
                    Toast.makeText(this@WeatherActivity, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<AirQualityResponse>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@WeatherActivity, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateWeatherUI(airQualityData: AirQualityResponse) {
        val currentWeather = airQualityData.data.current.weather
        val ts = airQualityData.data.current.weather.ts

        val zonedDateTime = ZonedDateTime.parse(ts)
        val koreaDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"))

        // 날짜 형식 지정
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd a hh:mm", Locale.KOREA)

        binding.updatedAt.text = koreaDateTime.format(dateFormatter).toString()
        binding.temp.text = "${currentWeather.tp}°C"
        binding.humidity.text = "${currentWeather.hu}%"
        binding.wind.text = "${currentWeather.ws} m/s"
        val weatherIconResourceId = getWeatherDescription(currentWeather.ic)
        weatherIconResourceId?.let {
            binding.status.setImageResource(it)
        }
    }

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

    private fun setFab() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("currentLat", latitude)
            intent.putExtra("currentLng", longitude)
            startMapActivityResult.launch(intent)
        }
    }

    private fun getCurrentAddress(latitude: Double, longitude: Double): Address? {
        // Geocoder를 사용하여 현재 위치의 위도와 경도를 이용하여 해당 위치의 주소 정보를 가져옴
        val geoCoder = Geocoder(this, Locale.KOREA)
        val addresses: List<Address>?

        addresses = try {
            // getFromLocation 함수를 사용하여 위도와 경도로부터 주소 리스트를 가져오며, 최대 7개의 결과를 반환
            geoCoder.getFromLocation(latitude, longitude, 7)
        } catch (ioException: IOException) {
            Toast.makeText(this, "지오코더 서비스 사용불가합니다.", Toast.LENGTH_LONG).show()
            return null
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 위도, 경도 입니다.", Toast.LENGTH_LONG).show()
            return null
        }

        // 주소 리스트가 null이거나 비어있는 경우에는 해당 위치의 주소가 발견되지 않았다는 메시지를 표시하고 null을 반환
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(this, "주소가 발견되지 않았습니다.", Toast.LENGTH_LONG).show()
            return null
        }
        // 현재 위치의 위도와 경도를 이용하여 해당 위치의 주소 정보를 가져온 후, 주소 리스트 중 첫 번째 주소를 반환
        return addresses[0]
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 권한이 허용된 경우
//                val locationProvider = LocationProvider(this)
//                val latitude = locationProvider.getLocationLatitude()
//                val longitude = locationProvider.getLocationLongitude()
//                if (latitude != null && longitude != null) {
//                    startWeatherTask(latitude, longitude)
//                }
//            } else {
//                // 권한이 거부된 경우 처리
//                Toast.makeText(this, "위치 권한을 허용해야 날씨 정보를 확인할 수 있습니다.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    private fun startWeatherTask(latitude: Double, longitude: Double) {
//        val url =
//            "https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&units=metric&appid=${API}&lang=kr"
//        WeatherTask().execute(url)
//    }
//
//    inner class WeatherTask : AsyncTask<String, Void, String>() {
//        override fun doInBackground(vararg params: String?): String? {
//            val url = params[0]
//            return try {
//                URL(url).readText(Charsets.UTF_8)
//            } catch (e: Exception) {
//                null
//            }
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            try {
//                // JSON 객체에서 각각의 필요한 정보를 가져옴
//                val jsonObj = JSONObject(result)
//                val main = jsonObj.getJSONObject("main")
//                val sys = jsonObj.getJSONObject("sys")
//                val wind = jsonObj.getJSONObject("wind")
//                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
//
//                // JSON 객체에서 "dt" 키에 해당하는 값을 가져와 updatedAt에 저장
//                val updatedAt: Long = jsonObj.getLong("dt")
//                // updatedAt를 "yyyy/MM/dd a hh:mm" 형식의 문자열로 변환하여 updatedAtText에 저장
//                val updatedAtText =
//                    SimpleDateFormat("yyyy/MM/dd a hh:mm", Locale.KOREA).apply {
//                        timeZone = TimeZone.getTimeZone("Asia/Seoul")
//                    }.format(Date(updatedAt * 1000))
//
//                // 각각의 기상 정보를 가져와 변수에 저장
//                val temp = main.getString("temp") + "°C"
//                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
//                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
//                val pressure = main.getString("pressure")
//                val humidity = main.getString("humidity")
//
//                // 일출, 일몰, 풍속, 날씨 설명 등의 정보를 가져와 변수에 저장
//                val sunrise: Long = sys.getLong("sunrise")
//                val sunset: Long = sys.getLong("sunset")
//                val windSpeed = wind.getString("speed")
//                val weatherDescription = weather.getString("description")
//
//                // 도시 이름과 국가 코드를 조합하여 address에 저장
//                val address = jsonObj.getString("name") + ", " + sys.getString("country")
//
//                // 바인딩을 사용하여 해당 정보를 화면의 특정 텍스트뷰에 나타냄
//                binding.address.text = address
//                binding.updatedAt.text = updatedAtText
//                binding.status.text = weatherDescription.capitalize()
//                binding.temp.text = temp
//                binding.tempMin.text = tempMin
//                binding.tempMax.text = tempMax
//
//                binding.sunrise.text =
//                    SimpleDateFormat("hh:mm a", Locale.KOREA).apply {
//                        timeZone = TimeZone.getTimeZone("Asia/Seoul")
//                    }.format(Date(sunrise * 1000))
//
//                binding.sunset.text =
//                    SimpleDateFormat("hh:mm a", Locale.KOREA).apply {
//                        timeZone = TimeZone.getTimeZone("Asia/Seoul")
//                    }.format(Date(sunset * 1000))
//
//                binding.wind.text = windSpeed
//                binding.pressure.text = pressure
//                binding.humidity.text = humidity
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
}