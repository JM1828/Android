package com.example.todolist.db

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

/*
@Dao
- 데이터 삽입, 갱신, 삭제, 조회 등의 데이터베이스 조작 기능을 메서드로 정의
- SQL 쿼리를 사용하여 데이터를 가져오는 메서드를 정의
- Room 라이브러리는 DAO에 정의된 메서드를 기반으로 데이터베이스 쿼리를 생성하고 실행
 */

@Dao
interface TodoDao {

    // 모든 TodoEntity를 가져오는 메서드를 정의
    @Query("SELECT * FROM todo ORDER BY importance")
    fun getAllTodo() : List<TodoEntity>

    // 데이터베이스에 데이터를 삽입하는 메서드를 정의
    @Insert
    fun insertTodo(todo : TodoEntity)

    // 데이터베이스에서 데이터를 삭제하는 메서드를 정의
    @Delete
    fun deleteTodo(todo : TodoEntity)
}