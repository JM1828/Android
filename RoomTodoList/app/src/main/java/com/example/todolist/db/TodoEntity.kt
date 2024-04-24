package com.example.todolist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

// @Entity 어노테이션은 데이터베이스의 테이블을 나타내는 엔티티 클래스를 정의할 때 사용
// data class는 데이터를 담는 데 사용되며, 컴파일러가 자동으로 equals(), hashCode(), toString() 등의 메서드를 생성해줌
@Entity
data class TodoEntity(
    // 할 일 목록 엔티티의 기본 키(primary key)
    @PrimaryKey(autoGenerate = true)
    var tno: Int? = null,

    // 회원 번호, 해당 할 일을 생성한 회원의 식별을 위한 값
//    @ColumnInfo(name = "mno")
//    var mno: String,

    // 할 일의 내용
    @ColumnInfo(name = "todo_content")
    var todoContent: String,

    // 할 일의 중요도
    @ColumnInfo(name = "importance")
    var importance: String,
//
//    // 할 일의 시간 정보
//    @ColumnInfo(name = "time")
//    var time: LocalTime,
//
//    // 할 일의 날짜
//    @ColumnInfo(name = "day")
//    var day: LocalDate,
//
//    // 할 일이 완료되었는지 여부
//    @ColumnInfo(name = "checked")
//    var checked: Boolean = false,
//
//    // 할 일을 등록한 시간 정보
//    @ColumnInfo(name = "todo_reg_time")
//    var todoRegTime: LocalTime,
)