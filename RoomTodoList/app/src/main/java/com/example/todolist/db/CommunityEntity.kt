package com.example.todolist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

// @Entity 어노테이션은 데이터베이스의 테이블을 나타내는 엔티티 클래스를 정의할 때 사용
// data class는 데이터를 담는 데 사용되며, 컴파일러가 자동으로 equals(), hashCode(), toString() 등의 메서드를 생성해줌
@Entity
data class CommunityEntity(
    // 엔티티의 기본 키(primary key)이며, 자동으로 생성되도록 지정
    @PrimaryKey(autoGenerate = true)
    var cno: Int? = null,

    // 회원 번호, 커뮤니티 글을 작성한 회원의 식별을 위한 값
    @ColumnInfo(name = "mno")
    var mno: Int,

    // 커뮤니티 글의 카테고리
    @ColumnInfo(name = "category")
    var category: String,

    // 커뮤니티 글이 작성된 장소
    @ColumnInfo(name = "com_place")
    var comPlace: String,

    // 커뮤니티 글의 내용
    @ColumnInfo(name = "com_content")
    var comContent: String,

    // 커뮤니티 글이 작성된 시간
    @ColumnInfo(name = "com_reg_time")
    var comRegTime: LocalTime,
)