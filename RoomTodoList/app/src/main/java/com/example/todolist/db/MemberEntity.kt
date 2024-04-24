package com.example.todolist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import androidx.room.Relation

// @Entity 어노테이션은 데이터베이스의 테이블을 나타내는 엔티티 클래스를 정의할 때 사용
// data class는 데이터를 담는 데 사용되며, 컴파일러가 자동으로 equals(), hashCode(), toString() 등의 메서드를 생성해줌
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

    // 회원이 설정한 지역의 날씨 정보
    @ColumnInfo(name = "place_weather")
    var placeWeather: String,

    // 회원이 설정한 지하철역 정보
    @ColumnInfo(name = "place_station")
    var placeStation: String,

    // 이미지를 나타내는 리소스의 식별자
    @ColumnInfo(name = "image")
    var image: String,

    // @Relation 어노테이션은 Room 라이브러리에서 사용됨, 이를 통해 엔티티 클래스 간의 관계를 정의할 수 있음
    // parentColumn은 부모 엔티티의 기본 키 열을 지정하는 데 사용, entityColumn은 자식 엔티티와 관계를 맺고 있는 외래 키 열
    @Relation(parentColumn = "mno", entityColumn = "mno")
    val todos: List<TodoEntity>? = null,

    @Relation(parentColumn = "mno", entityColumn = "mno")
    val communities: List<CommunityEntity>? = null
)