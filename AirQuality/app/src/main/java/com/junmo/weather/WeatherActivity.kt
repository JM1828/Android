package com.junmo.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.junmo.airquality.LocationProvider
import com.junmo.airquality.databinding.ActivityWeatherBinding
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    lateinit var binding: ActivityWeatherBinding
    val API: String = "d1d0985df84937507fafb61d92f50091"

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locationProvider = LocationProvider(this)
        val latitude = locationProvider.getLocationLatitude()
        val longitude = locationProvider.getLocationLongitude()

        if (latitude != null && longitude != null) {
            if (checkPermission()) {
                // 권한이 이미 허용된 경우
                startWeatherTask(latitude, longitude)
            } else {
                // 권한을 요청하는 다이얼로그 표시
                requestPermission()
            }
        } else {
            // 위치 정보를 가져오지 못한 경우의 처리
            Toast.makeText(this, "위도, 경도 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우
                val locationProvider = LocationProvider(this)
                val latitude = locationProvider.getLocationLatitude()
                val longitude = locationProvider.getLocationLongitude()
                if (latitude != null && longitude != null) {
                    startWeatherTask(latitude, longitude)
                }
            } else {
                // 권한이 거부된 경우 처리
                Toast.makeText(this, "위치 권한을 허용해야 날씨 정보를 확인할 수 있습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startWeatherTask(latitude: Double, longitude: Double) {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&units=metric&appid=${API}&lang=kr"
        WeatherTask().execute(url)
    }

    inner class WeatherTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {
            val url = params[0]
            return try {
                URL(url).readText(Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    SimpleDateFormat("yyyy/MM/dd a hh:mm", Locale.KOREA).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Seoul")
                    }.format(Date(updatedAt * 1000))

                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                /* Populating extracted data into our views */
                binding.address.text = address
                binding.updatedAt.text = updatedAtText
                binding.status.text = weatherDescription.capitalize()
                binding.temp.text = temp
                binding.tempMin.text = tempMin
                binding.tempMax.text = tempMax

                binding.sunrise.text =
                    SimpleDateFormat("hh:mm a", Locale.KOREA).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Seoul")
                    }.format(Date(sunrise * 1000))

                binding.sunset.text =
                    SimpleDateFormat("hh:mm a", Locale.KOREA).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Seoul")
                    }.format(Date(sunset * 1000))

                binding.wind.text = windSpeed
                binding.pressure.text = pressure
                binding.humidity.text = humidity

                /* Views populated, Hiding the loader, Showing the main design */
                binding.loader.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE

            } catch (e: Exception) {
                binding.loader.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            }
        }
    }
}