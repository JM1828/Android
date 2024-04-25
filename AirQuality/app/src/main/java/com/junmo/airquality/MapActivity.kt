package com.junmo.airquality

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.junmo.airquality.databinding.ActivityMapBinding
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

private val PERMISSIONS_REQUEST_CODE = 100
    lateinit var binding: ActivityMapBinding

    private var mMap: GoogleMap? = null
    var currentLat: Double = 0.0
    var currentLng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyDHHREmWj526xBYkWxgp3nNJ9XkPFcsNzg")
        }

        currentLat = intent.getDoubleExtra("currentLat", 0.0)
        currentLng = intent.getDoubleExtra("currentLng", 0.0)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setButton()
    }

    private fun setButton() {
        binding.btnCheckHere.setOnClickListener {
            mMap?.let {
                val intent = Intent()
                intent.putExtra("latitude", it.cameraPosition.target.latitude)
                intent.putExtra("longitude", it.cameraPosition.target.longitude)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        binding.searchEdittext.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, PERMISSIONS_REQUEST_CODE)
        }

        binding.fabCurrentLocation.setOnClickListener {
            val locationProvider = LocationProvider(this)
            val latitude = locationProvider.getLocationLatitude()
            val longitude = locationProvider.getLocationLongitude()

            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!,longitude!!), 16f))
            setMarker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                // 검색 결과를 올바르게 받았을 때 맵을 이동시키는 코드
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 16f))
                setMarker()
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // 검색 중 오류가 발생했을 때의 처리
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.e("MapActivity", status.statusMessage!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // 사용자가 검색을 취소했을 때의 처리
                Log.d("MapActivity", "사용자가 검색을 취소했습니다.")
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        mMap?.let {
            val currentLocation = LatLng(currentLat, currentLng)
            it.setMaxZoomPreference(20.0f)
            it.setMinZoomPreference(12.0f)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f))
            setMarker()
        }
    }

    private fun setMarker() {
        mMap?.let {
            it.clear()
            val markerOption = MarkerOptions()
            markerOption.position(it.cameraPosition.target)
            markerOption.title("마커 위치")
            var marker = it.addMarker(markerOption)

            it.setOnCameraMoveListener {
                marker?.let { marker ->
                    marker.position = it.cameraPosition.target
                }
            }
        }
    }
}