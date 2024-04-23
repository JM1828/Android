package com.junmo.firebasecrud

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class UserDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("user")
    }

    // 등록
    fun add(user: User?): Task<Void> {
        return databaseReference!!.push().setValue(user)
    }

    // 조회
    fun getTodoList(): Query?{
        return databaseReference
    }
}