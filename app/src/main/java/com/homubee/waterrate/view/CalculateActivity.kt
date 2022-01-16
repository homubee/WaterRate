package com.homubee.waterrate.view

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.homubee.waterrate.databinding.ActivityCalculateBinding
import com.homubee.waterrate.model.WaterRate
import com.homubee.waterrate.util.DBHelper

class CalculateActivity : AppCompatActivity() {
    companion object {
        const val EDITTEXT_ID = 200
    }

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

        for (i in waterRateList.indices) {
            for (j in 1..3) {
                val textView = if (j != 3) TextView(this) else EditText(this)
                textView.gravity = Gravity.CENTER
                textView.setPadding(Math.round(5*resources.displayMetrics.density))
                textView.setBackgroundColor(Color.WHITE)

                val glparams = GridLayout.LayoutParams()
                glparams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)

                when(j) {
                    1 -> {
                        textView.text = waterRateList[i].name
                        glparams.leftMargin = Math.round(1*resources.displayMetrics.density)
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    2 -> {
                        textView.text = waterRateList[i].lastMonthCount.toString()
                        glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    3 -> {
                        textView.id = EDITTEXT_ID + i
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                        textView.hint = "여기에 입력하세요"
                        textView.inputType = InputType.TYPE_CLASS_NUMBER
                        glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                        glparams.rightMargin = Math.round(1*resources.displayMetrics.density)
                    }
                    else -> ""
                }
                glparams.topMargin = if (i == 0) {
                    Math.round(0.5*resources.displayMetrics.density).toInt()
                } else {
                    Math.round(1*resources.displayMetrics.density)
                }
                glparams.bottomMargin = if (i == waterRateList.size-1) {
                    Math.round(1*resources.displayMetrics.density)
                } else {
                    Math.round(0.5*resources.displayMetrics.density).toInt()
                }

                textView.layoutParams = glparams
                binding.glTable.addView(textView)
            }
        }
    }
}