package com.example.todolist.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CommunityDao {
    // 새로운 커뮤니티 글을 추가
    @Insert
    suspend fun insert(community: CommunityEntity)

    // 커뮤니티 글을 업데이트
    @Update
    suspend fun update(community: CommunityEntity)

    // 커뮤니티 글을 삭제
    @Delete
    suspend fun delete(community: CommunityEntity)

    // 모든 커뮤니티 글 목록을 가져옴
    @Query("SELECT * FROM CommunityEntity")
    suspend fun getAllCommunities(): List<CommunityEntity>

    // 특정 ID에 해당하는 커뮤니티 글을 가져옴
    @Query("SELECT * FROM CommunityEntity WHERE cno = :cno")
    suspend fun getCommunityById(cno: Int): CommunityEntity
}