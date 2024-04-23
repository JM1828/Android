package com.example.todolist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity 어노테이션은 데이터베이스의 테이블을 나타내는 엔티티 클래스를 정의할 때 사용
@Entity
// data class는 데이터를 담는 데 사용되며, 컴파일러가 자동으로 equals(), hashCode(), toString() 등의 메서드를 생성해줌
data class MemberEntity (
    // 노테이션은 해당 필드가 엔티티의 기본 키 임을 나타냄, autoGenerate = true는 이 기본 키가 자동으로 생성되도록 지정
    // var id : Int? = null는 id 필드가 nullable하며, 초기값으로 null을 가질 수 있도록 함
    @PrimaryKey(autoGenerate = true)
    var mno : Int? = null,

    // @ColumnInfo 어노테이션은 엔티티의 컬럼을 나타내는 필드를 정의할 때 사용
    // 회원의 이메일
    @ColumnInfo(name="email")
    var email : String,

    // 회원의 비밀번호
    @ColumnInfo(name="password")
    var password : String,

    // 회원의 닉네임
    @ColumnInfo(name="nick_name")
    var nickName : String,

    // 회원이 설정한 지역의 날씨 정보
    @ColumnInfo(name="place_weather")
    var placeWeather : String,

    // 회원이 설정한 지하철역 정보
    @ColumnInfo(name="place_station")
    var placeStation : String,

    // 이미지를 나타내는 리소스의 식별자
    @ColumnInfo(name="image")
    var image : Int,
)