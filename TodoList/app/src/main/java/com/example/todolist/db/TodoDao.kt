package com.example.todolist.db

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class TodoDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("todo")
    }

    // 등록
    fun add(todo: Todo?): Task<Void> {
        return databaseReference!!.push().setValue(todo)
    }

    // 조회
    fun getTodoList(): Query?{
        return databaseReference
    }

    // 수정
    fun todoUpdate(key: String, hashMap: HashMap<String, Any>): Task<Void> {
        return databaseReference!!.child(key)!!.updateChildren(hashMap)
    }

    // 삭제
    fun todoDelete(key: String): Task<Void> {
        return databaseReference!!.child(key).removeValue()
    }
}