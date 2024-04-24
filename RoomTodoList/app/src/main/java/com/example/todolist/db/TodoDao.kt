package com.example.todolist.db

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update

/*
@Dao
- 데이터 삽입, 갱신, 삭제, 조회 등의 데이터베이스 조작 기능을 메서드로 정의
- SQL 쿼리를 사용하여 데이터를 가져오는 메서드를 정의
- Room 라이브러리는 DAO에 정의된 메서드를 기반으로 데이터베이스 쿼리를 생성하고 실행
 */

@Dao
interface TodoDao {

    // 삽입
    @Insert
    suspend fun insertTodo(todo : TodoEntity)

    // suspend 키워드를 사용하면 직접적으로 Thread나 runOnUiThread를 사용할 필요가 없게된다.. 왜이걸 이제 알았을까
    // 수정
    @Update
    suspend fun updateTodo(todo: TodoEntity)

    // 삭제
    @Delete
    suspend fun deleteTodo(todo : TodoEntity)

    // 모든 할일 목록을 가져옴
    @Query("SELECT * FROM TodoEntity ORDER BY importance")
    suspend fun getAllTodo() : List<TodoEntity>

    // 특정 ID에 해당하는 할일을 가져옴
    @Query("SELECT * FROM TodoEntity WHERE tno = :tno")
    suspend fun getTodoById(tno: Int): TodoEntity
}