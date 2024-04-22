package com.junmo.airquality.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit을 사용하여 API 호출을 위한 기본 설정을 담고 있으며, 동반 객체를 활용하여 싱글톤으로 Retrofit 인스턴스를 관리
class RetrofitConnection {
    companion object {
        // 기본 URL을 상수로 정의
        private const val BASE_URL = "https://api.airvisual.com/v2/"
        // Retrofit의 인스턴스를 저장하기 위한 변수를 선언
        private var INSTANCE: Retrofit? = null

        fun getInstance() : Retrofit {
            if (INSTANCE == null) {
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    // GsonConverterFactory를 Retrofit에 추가함으로써 Retrofit은 서버로부터 받은 JSON 응답을 자바 객체로 변환할 수 있음
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return INSTANCE!!
        }
    }
}