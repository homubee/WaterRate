package com.homubee.waterrate.view

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import com.homubee.waterrate.databinding.ActivityCalculateBinding
import com.homubee.waterrate.model.WaterRate
import com.homubee.waterrate.util.DBHelper

class CalculateActivity : AppCompatActivity() {
    companion object {
        const val EDITTEXT_ID = 200
    }
    lateinit var binding: ActivityCalculateBinding
    val waterRateList = mutableListOf<WaterRate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "요금 계산"

        // DB 데이터를 불러와 객체 생성
        val db: SQLiteDatabase = DBHelper(applicationContext).writableDatabase
        var cursor = db.rawQuery("select * from water_rate", null)
        while(cursor.moveToNext()) {
            val type = cursor.getInt(1)
            val name = cursor.getString(2)
            val count = cursor.getDouble(3)
            val list = mutableListOf<String>()
            val listData = cursor.getString(4).split(",")
            if (listData[0] != "") {
                for (i in listData.indices) {
                    list.add(listData[i])
                }
            }
            waterRateList.add(WaterRate(type, name, count, list))
        }

        // 표 내용 생성
        for (i in waterRateList.indices) {
            for (j in 1..3) {
                // 3번째의 경우 금월지침 입력 받아야 하므로 EditText 객체를 생성
                val textView = if (j != 3) TextView(this) else EditText(this)
                textView.gravity = Gravity.CENTER
                textView.setPadding(Math.round(5*resources.displayMetrics.density))
                textView.setBackgroundColor(Color.WHITE)

                val glparams = GridLayout.LayoutParams()
                glparams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)

                when(j) {
                    // 설비/상호명
                    1 -> {
                        textView.text = waterRateList[i].name
                        glparams.leftMargin = Math.round(1*resources.displayMetrics.density)
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    // 전월지침
                    2 -> {
                        textView.text = waterRateList[i].lastMonthCount.toString()
                        // 계량기 없는 경우는 지침 정보를 표시하지 않음
                        if (waterRateList[i].type == 2) {
                            textView.text = ""
                        }
                        glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    // 금월지침 (EditText)
                    3 -> {
                        textView.id = EDITTEXT_ID + i
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        textView.hint = "(금월지침)"
                        textView.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                        // 계량기 없는 경우는 입력하지 못하도록 처리
                        if (waterRateList[i].type == 2) {
                            textView.hint = ""
                            textView.isEnabled = false
                        }
                        Log.d("lastMonth", textView.inputType.toString())
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

    // 메뉴 버튼 추가 및 액티비티 전환
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuNext: MenuItem? = menu?.add(0, 0, 0, "완료")
        menuNext?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_WITH_TEXT)
        return super.onCreateOptionsMenu(menu)
    }
    // 금월지침은 숫자만 입력 받고, 해당 내용은 인텐트로 넘겨줌, 기존 액티비티는 종료
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        0 -> {
            val thisMonthCountList = mutableListOf<Double>()
            for  (i in waterRateList.indices) {
                var thisMonthCount = findViewById<EditText>(EDITTEXT_ID + i).text.toString()
                if (waterRateList[i].type != 2 && thisMonthCount.isBlank()) {
                    Toast.makeText(applicationContext, "내용을 모두 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    break
                } else if (thisMonthCount.contains(' ') || thisMonthCount.contains('-') || thisMonthCount.contains(',')) {
                    Toast.makeText(applicationContext, "숫자만 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    break
                } else if (thisMonthCount.isNotBlank() && thisMonthCount.toDouble() > 9999.5) {
                    Toast.makeText(applicationContext, "지침 숫자는 9999.5를 넘을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    break
                }

                if (waterRateList[i].type == 2) thisMonthCount = "-1"
                thisMonthCountList.add(thisMonthCount.toDouble())
            }

            if (thisMonthCountList.size == waterRateList.size) {
                val totalUsage = binding.etTotalUsage.text.toString()
                val totalRate = binding.etTotalRate.text.toString()
                if (totalUsage.isBlank() || totalRate.isBlank()) {
                    Toast.makeText(applicationContext, "내용을 모두 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(applicationContext, ResultActivity::class.java)
                    intent.putExtra("waterRateList",  ArrayList(waterRateList))
                    intent.putExtra("thisMonthCountList", ArrayList(thisMonthCountList))
                    intent.putExtra("totalUsage", totalUsage.toInt())
                    intent.putExtra("totalRate", totalRate.toInt())
                    startActivity(intent)
                    finish()
                }
            }

            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}