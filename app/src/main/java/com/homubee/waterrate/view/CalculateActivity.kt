package com.homubee.waterrate.view

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.homubee.waterrate.databinding.ActivityCalculateBinding
import com.homubee.waterrate.model.WaterRate
import com.homubee.waterrate.util.DBHelper

class CalculateActivity : AppCompatActivity() {
    lateinit var adapter: WaterRateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCalculateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // DB 데이터를 불러와 객체 생성
        val waterRateList = mutableListOf<WaterRate>()
        val db: SQLiteDatabase = DBHelper(applicationContext).writableDatabase
        var cursor = db.rawQuery("select * from water_rate", null)
        while(cursor.moveToNext()) {
            val type = cursor.getInt(1)
            val name = cursor.getString(2)
            val count = cursor.getInt(3)
            val list = mutableListOf<String>()
            val listData = cursor.getString(4).split(",")
            if (listData[0] != "") {
                for (i in listData.indices) {
                    list.add(listData[i])
                }
            }
            waterRateList.add(WaterRate(type, name, count, list))
        }

        // 내용 확인용 임시 리사이클러뷰
        adapter = WaterRateAdapter(1, waterRateList)
        binding.recyclerData.layoutManager = LinearLayoutManager(this)
        binding.recyclerData.adapter = adapter
    }
}