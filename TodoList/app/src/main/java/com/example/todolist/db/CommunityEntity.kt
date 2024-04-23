package com.example.todolist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

// @Entity 어노테이션은 데이터베이스의 테이블을 나타내는 엔티티 클래스를 정의할 때 사용
@Entity(tableName = "todo")
// data class는 데이터를 담는 데 사용되며, 컴파일러가 자동으로 equals(), hashCode(), toString() 등의 메서드를 생성해줌
data class CommunityEntity (
    // 노테이션은 해당 필드가 엔티티의 기본 키 임을 나타냄, autoGenerate = true는 이 기본 키가 자동으로 생성되도록 지정
    // var tno : Int? = null는 tno 필드가 nullable하며, 초기값으로 null을 가질 수 있도록 함
    // 엔티티의 기본 키(primary key)이며, 자동으로 생성되도록 지정
    @PrimaryKey(autoGenerate = true)
    var cno : Int? = null,

    @ColumnInfo(name="mno")
    var mno : Int,

    // 카테고리
    @ColumnInfo(name="category")
    var category : String,

    //
    @ColumnInfo(name="com_place")
    var comPlace : String,

    // 커뮤니티 글 내용
    @ColumnInfo(name="com_content")
    var comContent : String,

    // 커뮤니티 글 작성 시간
    @ColumnInfo(name="com_reg_time")
    var comRegTime : LocalTime,
)