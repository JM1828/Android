package com.junmo.airquality

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.junmo.OneFragment
import com.junmo.airquality.databinding.ActivityMainBinding
import com.junmo.airquality.retrofit.AirQualityResponse
import com.junmo.airquality.retrofit.AirQualityService
import com.junmo.airquality.retrofit.RetrofitConnection
import com.junmo.weather.WeatherActivity
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.lang.IllegalArgumentException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var locationProvider: LocationProvider

    // 위치 권한 요청에 사용되는 요청 코드를 정의
    private val PERMISSIONS_REQUEST_CODE = 100

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
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    if (result?.resultCode ?: 0 == Activity.RESULT_OK) {
                        latitude = result?.data?.getDoubleExtra("latitude", 0.0) ?: 0.0
                        longitude = result?.data?.getDoubleExtra("longitude", 0.0) ?: 0.0
//                        updateUI()
                    }
                }
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.weatherIcon.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
        }

        // 앱이 실행될 때, onCreate() 함수가 호출되면서 checkAllPermissions() 함수가 실행
        checkAllPermissions()
        setRefreshButton()
        updateUI()

        setFab()
    }

     // 현재 위치 정보를 가져와 UI를 업데이트
    private fun updateUI() {
        // LocationProvider를 초기화하여 현재 위치 정보를 가져옴
        locationProvider = LocationProvider(this)

        if (latitude == 0.0 && longitude == 0.0) {
            // latitude와 longitude 변수를 통해 현재 위치의 위도와 경도를 가져옴
            latitude = locationProvider.getLocationLatitude()
            longitude = locationProvider.getLocationLongitude()
        }

        // 만약 latitude와 longitude가 모두 null이 아니라면, 현재 위치의 주소 정보를 가져와서 UI를 업데이트
        if (latitude != null && longitude != null) {
            // 1. 현재 위치가져오고 UI 업데이트
            val address = getCurrentAddress(latitude!!, longitude!!)

            // address가 null이 아닐 때, 해당 주소 정보를 사용하여 UI를 업데이트
            address?.let {
                // thoroughfare : 동을 나타냄 (수영동, 광안동)
                binding.tvLocationTitle.text = "${it.thoroughfare}"
                // countryName : 나라 이름
                binding.tvLocationSubtitle.text = "${it.countryName} ${it.adminArea}"
            }
            // 2. 미세먼지 농도 가져오고 UI 업데이트
            getAirQualityData(latitude!!, longitude!!)
        } else {
            Toast.makeText(this, "위도, 경도 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private fun getAirQualityData(latitude: Double, longitude: Double) {
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
                    Toast.makeText(this@MainActivity, "최신 데이터 업데이트 완료!", Toast.LENGTH_LONG).show()
                    // 응답에 body 값이 null 이 아니면 UI를 업데이트
                    response.body()?.let { updateAirUI(it) }
                } else {
                    Toast.makeText(this@MainActivity, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<AirQualityResponse>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@MainActivity, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateAirUI(airQualityData: AirQualityResponse) {
        val pollutionData = airQualityData.data.current.pollution

        // 수치를 지정
        binding.tvCount.text = pollutionData.aqius.toString()

        // 측정된 날짜
        val dateTime =
            ZonedDateTime.parse(pollutionData.ts).withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        binding.tvCheckTime.text = dateTime.format(dateFormatter).toString()

        when (pollutionData.aqius) {
            in 0..50 -> {
                binding.tvTitle.text = "좋음"
                binding.imgBg.setImageResource(R.drawable.bg_good)
            }
            in 51..150 -> {
                binding.tvTitle.text = "보통"
                binding.imgBg.setImageResource(R.drawable.bg_soso)
            }
            in 151..200 -> {
                binding.tvTitle.text = "나쁨"
                binding.imgBg.setImageResource(R.drawable.bg_bad)
            }
            else -> {
                binding.tvTitle.text = "매우 나쁨"
                binding.imgBg.setImageResource(R.drawable.bg_worst)
            }
        }
    }

    // 새로고침 버튼을 눌렀을때 updateUI 함수를 실행
    private fun setRefreshButton() {
        binding.btnRefresh.setOnClickListener {
//            updateUI()
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

    // 현재 위치의 위도와 경도를 이용하여 해당 위치의 주소 정보를 가져오는 함수
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

    // 앱에서 필요로 하는 모든 권한을 확인하는 역할
    private fun checkAllPermissions() {
        // isLocationServicesAvailable() 함수를 사용하여 위치 서비스가 사용 가능한지 확인
        if (!isLocationServicesAvailable()) {
            // 만약 위치 서비스가 비활성화되어 있다면
            // showDialogForLocationServiceSetting() 함수를 호출하여 사용자에게 위치 서비스 설정을 변경할 수 있는 다이얼로그를 보여줌
            showDialogForLocationServiceSetting()
        } else {
            // 만약 위치 서비스가 활성화되어 있다면, isRunTimePermissionsGranted() 함수를 호출하여 런타임 권한이 부여되었는지 확인
            isRunTimePermissionsGranted()
        }
    }

    // GPS 또는 네트워크 위치 제공자 중 하나라도 활성화되어 있으면 true를 반환하고, 그렇지 않으면 false를 반환
    private fun isLocationServicesAvailable(): Boolean {
        // 시스템 서비스에서 위치 관리자(LocationManager)를 가져오는 부분으로, 이를 통해 위치 관련 기능을 사용할 수 있음
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // 각각 GPS 및 네트워크 위치 제공자가 활성화되어 있는지를 확인하는 부분
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        ))
    }

    // 사용자에게 위치 권한이 부여되었는지 확인하고, 필요한 권한이 없는 경우 사용자에게 권한을 요청하는 데 사용될 수 있음
    private fun isRunTimePermissionsGranted() {
        // 현재 앱이 특정 위치 권한을 보유하고 있는지 확인하고, 그 결과를 각각 변수에 저장
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // 앱이 위치 권한을 보유하고 있는지 확인
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 "ActivityCompat.requestPermissions"를 사용하여 사용자에게 권한을 요청
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    // 사용자의 권한 부여 응답을 처리하고, 모든 권한이 부여되었는지 확인하여 그에 따른 처리를 하는 부분
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // "requestCode"와 "grantResults" 배열의 크기를 확인하여 요청 코드와 권한 부여 결과의 크기가 일치하는지 확인
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var checkResult = true

            // 모든 권한이 부여되었는지 확인하기 위해 "grantResults" 배열을 순회하면서 권한이 부여되었는지 확인
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false
                    break;
                }
            }

            // 만약 모든 권한이 부여되었다면 "위치값을 가져올 수 있음"이라는 처리
            if (checkResult) {
                // 위치값을 가져올 수 있음
//                updateUI()
            } else {
                // 그렇지 않을 경우 "퍼미션이 거부되었습니다. 앱을 실행하여 퍼미션을 허용해주세요."라는 메시지를 사용자에게 보여주고 앱을 종료
                Toast.makeText(
                    this,
                    "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    // 사용자에게 위치 서비스가 비활성화되어 있음을 알리고, 사용자가 위치 설정을 변경할 수 있는 옵션을 제공하는 다이얼로그를 보여줌
    private fun showDialogForLocationServiceSetting() {
        // registerForActivityResult를 사용하여 GPS 권한 요청의 결과를 처리하기 위한 로직을 정의
        getGPSPermissionLauncher = registerForActivityResult(
            // 액티비티를 시작하고 그 결과를 처리하기 위한 계약
            ActivityResultContracts.StartActivityForResult()
            // GPS 권한 요청이 완료된 후 호출되는 콜백에서는 결과코드가 Activity.RESULT_OK인지 확인
        ) { result ->
                //  isLocationServicesAvailable() 함수를 사용하여 위치 서비스가 사용 가능한지 확인
                if (isLocationServicesAvailable()) {
                    // isRunTimePermissionsGranted() 함수를 호출하여 권한을 확인
                    isRunTimePermissionsGranted()
                } else {
                    Toast.makeText(this, "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
            }

        // AlertDialog.Builder를 사용하여 다이얼로그를 생성
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("위치 서비스가 꺼져있습니다. 설정해야 앱을 사용할 수 있습니다.")
        // 다이얼로그가 취소 가능하도록 설정되고, "설정" 및 "취소" 두 개의 버튼이 추가됨
        builder.setCancelable(true)
        // "설정" 버튼을 누를 경우, 위치 설정을 변경할 수 있는 화면으로 이동하는 인텐트를 생성
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialogInterface, i ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            // getGPSPermissionLauncher를 사용하여 해당 인텐트를 실행
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        })
        // "취소" 버튼을 누를 경우, 다이얼로그를 취소하고 사용자에게 "위치 서비스를 사용할 수 없습니다."라는 메시지를 보여주며, 앱을 종료
        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
            Toast.makeText(this, "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG).show()
            finish()
        })
        // AlertDialog.Builder로부터 생성된 다이얼로그를 화면에 표시하는 역할
        builder.create().show()
    }
}
