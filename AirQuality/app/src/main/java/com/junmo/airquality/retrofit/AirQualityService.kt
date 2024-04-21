package com.junmo.airquality.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//  Retrofit을 사용하여 RESTful API 호출을 위한 인터페이스를 정의하고, 해당 인터페이스를 통해 특정 엔드포인트에 대한 요청을 구성
interface AirQualityService {
    // 이 요청은 가장 가까운 도시의 대기질 데이터를 가져오는 것
    @GET("nearest_city")
    fun getAirQualityData(
        @Query("lat") let: String,
        @Query("lon") lon: String,
        @Query("key") key: String
        // Call 은 요청을 처리하는 객체
    ): Call<AirQualityResponse>
}