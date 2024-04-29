package com.example.airandweather.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MemberEntity(
    // autoGenerate = true는 이 기본 키가 자동으로 생성되도록 지정
    @PrimaryKey(autoGenerate = true)
    var mno: Int? = null,

    // 회원의 이메일
    @ColumnInfo(name = "email")
    var email: String,

    // 회원의 비밀번호
    @ColumnInfo(name = "password")
    var password: String,

    // 회원의 닉네임
    @ColumnInfo(name = "nick_name")
    var nickName: String,

//    @ColumnInfo(name = "address")
//    var address: String,

    // 회원이 설정한 지하철역 정보
    @ColumnInfo(name = "place_station")
    var placeStation: String,

//    @ColumnInfo(name = "image")
//    var image: String?,
//
//    @Relation(parentColumn = "mno", entityColumn = "mno")
//    val todos: List<TodoEntity>? = null,
//
//    @Relation(parentColumn = "mno", entityColumn = "mno")
//    val communication: List<Communication>? = null

)