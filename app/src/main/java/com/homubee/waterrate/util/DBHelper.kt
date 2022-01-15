package com.homubee.waterrate.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "db", null, 1) {
    // id, type, name, count, list로 구성
    // list는 단어 리스트를 콤마로 구분되는 문자열 데이터로 가지고 있음
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table water_rate (" +
                    "_id integer primary key autoincrement," +
                    "type integer not null," +
                    "name text not null," +
                    "count integer not null," +
                    "list text)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}