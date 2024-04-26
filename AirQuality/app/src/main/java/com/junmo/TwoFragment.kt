package com.junmo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.junmo.airquality.LocationProvider
import com.junmo.airquality.MapActivity
import com.junmo.airquality.R
import com.junmo.airquality.databinding.ActivityWeatherBinding
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

class TwoFragment : Fragment() {
    private var _binding: FragmentTwoBinding? = null
    private val binding get() = _binding!!

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
                        updateUI()
                    }
                }
            }
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 여기서 UI와 관련된 초기 설정을 수행합니다.
        checkAllPermissions()
        setRefreshButton()
        updateUI()

        setFab()
    }

    // 현재 위치 정보를 가져와 UI를 업데이트하는 메서드
    private fun updateUI() {
        // LocationProvider를 초기화하여 현재 위치 정보를 가져옴
        context?.let {
            locationProvider = LocationProvider(it)
        }

        if (latitude == 0.0 && longitude == 0.0) {
            latitude = locationProvider.getLocationLatitude()
            longitude = locationProvider.getLocationLongitude()
        }

        if (latitude != null && longitude != null) {
            val address = getCurrentAddress(latitude!!, longitude!!)
            address?.let {
                val defaultText = "알 수 없는 도로"
                val addressText = address?.thoroughfare ?: address?.featureName ?: address?.subLocality ?: address?.locality ?: defaultText
                binding.tvLocationTitle.text = addressText
                binding.tvLocationSubtitle.text = "${it.countryName} ${it.adminArea}"
            }
            getWeatherData(latitude!!, longitude!!)
        } else {
            Toast.makeText(context, "위도, 경도 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(context, "최신 데이터 업데이트 완료!", Toast.LENGTH_LONG)
                        .show()
                    // 응답에 body 값이 null 이 아니면 UI를 업데이트
                    response.body()?.let { updateWeatherUI(it) }
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

    private fun updateWeatherUI(airQualityData: AirQualityResponse) {
        val currentWeather = airQualityData.data.current.weather
        val ts = airQualityData.data.current.weather.ts

        val zonedDateTime = ZonedDateTime.parse(ts)
        val koreaDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"))

        // 날짜 형식 지정
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd a hh:mm", Locale.KOREA)

        binding.tvCheckTime.text = koreaDateTime.format(dateFormatter).toString()
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

    // 새로고침 버튼을 눌렀을때 updateUI 함수를 실행
    private fun setRefreshButton() {
        binding.btnRefresh.setOnClickListener {
            updateUI()
        }
    }

    private fun setFab() {
        binding.fab.setOnClickListener {
            // Intent 생성 시 Context를 전달하기 위해 requireContext()를 사용
            val intent = Intent(requireContext(), MapActivity::class.java).apply {
                putExtra("currentLat", latitude)
                putExtra("currentLng", longitude)
            }
            // startMapActivityResult를 통해 Activity 시작
            startMapActivityResult.launch(intent)
        }
    }

    // getCurrentAddress 등의 추가적인 메서드 구현
    private fun getCurrentAddress(latitude: Double, longitude: Double): Address? {
        val geoCoder = Geocoder(requireContext(), Locale.KOREA) // 'context' 대신 'requireContext()' 사용
        val addresses: List<Address>?

        addresses = try {
            geoCoder.getFromLocation(latitude, longitude, 7)
        } catch (ioException: IOException) {
            Toast.makeText(requireContext(), "지오코더 서비스 사용불가합니다.", Toast.LENGTH_LONG).show()
            return null
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(requireContext(), "잘못된 위도, 경도 입니다.", Toast.LENGTH_LONG).show()
            return null
        }

        if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(requireContext(), "주소가 발견되지 않았습니다.", Toast.LENGTH_LONG).show()
            return null
        }
        return addresses[0]
    }

    private fun isLocationServicesAvailable(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun checkAllPermissions() {
        if (!isLocationServicesAvailable()) {
            showDialogForLocationServiceSetting()
        } else {
            isRunTimePermissionsGranted()
        }
    }

    private fun isRunTimePermissionsGranted() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(), // 'this' 대신 'requireContext()' 사용
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(), // 'this' 대신 'requireContext()' 사용
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( // 'ActivityCompat.requestPermissions' 대신 'requestPermissions' 사용
                REQUIRED_PERMISSIONS,
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var checkResult = true

            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false
                    break
                }
            }

            if (checkResult) {
                updateUI()
            } else {
                Toast.makeText(
                    requireContext(),
                    "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                activity?.finish()
            }
        }
    }

    private fun showDialogForLocationServiceSetting() {
        getGPSPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (isLocationServicesAvailable()) {
                isRunTimePermissionsGranted()
            } else {
                Toast.makeText(requireContext(), "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG)
                    .show()
                activity?.finish()
            }
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("위치 서비스가 꺼져있습니다. 설정해야 앱을 사용할 수 있습니다.")
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialogInterface, i ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        }
        builder.setNegativeButton("취소") { dialogInterface, i ->
            dialogInterface.cancel()
            Toast.makeText(requireContext(), "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG).show()
            activity?.finish()
        }
        builder.create().show()
    }
}