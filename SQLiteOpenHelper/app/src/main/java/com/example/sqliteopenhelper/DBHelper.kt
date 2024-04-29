package com.example.sqliteopenhelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


// SQLiteOpenHelper를 상속하여 SQLite 데이터베이스를 생성하고 관리하는 역할
class DBHelper(context: Context): SQLiteOpenHelper(context, "memberdb", null, 1) {
    override fun onCreate(p0: SQLiteDatabase?) {
        // 이름, 나이, 핸드폰번호를 받는 테이블 추가
        p0?.execSQL("create table MEMBER_TB (" +
                "_id integer primary key autoincrement," +
                "name text not null," +
                "age text not null," +
                "phone text not null)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}