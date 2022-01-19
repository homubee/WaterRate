package com.homubee.waterrate.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.homubee.waterrate.databinding.ActivityResultBinding
import com.homubee.waterrate.model.WaterRate
import kotlin.math.roundToInt

class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // intent 로부터 데이터 전달 받음
        val intent = intent
        val totalWaterRateList = intent.getParcelableArrayListExtra<Parcelable>("waterRateList") as MutableList<WaterRate>
        val thisMonthCountList = intent.getParcelableArrayListExtra<Parcelable>("thisMonthCountList") as MutableList<Double>
        val totalUsage = intent.getIntExtra("totalUsage", 0)
        val totalRate = intent.getIntExtra("totalRate", 0)
        val ratePerOne: Double = totalRate / totalUsage.toDouble()

        val usageList = mutableListOf<Double>()
        val rateList = mutableListOf<Int>()

        // 사용량 리스트와 요금 리스트 초기화
        for (i in totalWaterRateList.indices) {
            usageList.add(thisMonthCountList[i] - totalWaterRateList[i].lastMonthCount)
            rateList.add((ratePerOne*usageList[i]).roundToInt())
        }

        // 요금 데이터에 공용 수도 요금 분배
        for (i in totalWaterRateList.indices) {
            // 공용 수도일 경우 체크
            if (totalWaterRateList[i].type == 0) {
                var splitRate = rateList[i].toDouble() / totalWaterRateList[i].waterRateList.size
                // name으로 공용 수도에 해당하는 개인 수도 찾아서 분할 요금 추가
                for (j in totalWaterRateList[i].waterRateList.indices) {
                    for (k in totalWaterRateList.indices) {
                        if (totalWaterRateList[i].waterRateList[j] == totalWaterRateList[k].name) {
                            rateList[k] = (rateList[k] + splitRate).roundToInt()
                        }
                    }
                }
            }
        }

        // 표 내용 생성
        for (i in totalWaterRateList.indices) {
            for (j in 1..5) {
                val textView = TextView(this)
                textView.gravity = Gravity.CENTER
                textView.setPadding(Math.round(5*resources.displayMetrics.density))
                textView.setBackgroundColor(Color.WHITE)

                val glparams = GridLayout.LayoutParams()
                glparams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)

                when(j) {
                    // 설비/상호명
                    1 -> {
                        textView.text = totalWaterRateList[i].name
                        glparams.leftMargin = Math.round(1*resources.displayMetrics.density)
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    // 전월지침
                    2 -> {
                        textView.text = totalWaterRateList[i].lastMonthCount.toString()
                        glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    // 금월지침
                    3 -> {
                        textView.text = thisMonthCountList[i].toString()
                        glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    // 사용량
                    4 -> {
                        textView.text = usageList[i].toString()
                        glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    // 요금
                    5 -> {
                        textView.text = rateList[i].toString()
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
                glparams.bottomMargin = Math.round(0.5*resources.displayMetrics.density).toInt()

                textView.layoutParams = glparams
                binding.glTable.addView(textView)
            }
        }

        // 전체 합산 수치 출력
        for (i in 1..3) {
            val textView = TextView(this)
            textView.gravity = Gravity.CENTER
            textView.setPadding(Math.round(5*resources.displayMetrics.density))
            textView.setBackgroundColor(Color.WHITE)

            val glparams = GridLayout.LayoutParams()
            glparams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)

            when(i) {
                // 빈 공백, 표 가로 3칸 차지
                1 -> {
                    textView.text = ""
                    glparams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 3,1f)
                    glparams.leftMargin = Math.round(1*resources.displayMetrics.density)
                    glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                }
                // 전체 사용량
                2 -> {
                    textView.text = totalUsage.toDouble().toString()
                    glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                }
                // 전체 요금
                3 -> {
                    textView.text = totalRate.toString()
                    glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    glparams.rightMargin = Math.round(1*resources.displayMetrics.density)
                }
                else -> ""
            }

            glparams.topMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
            glparams.bottomMargin = Math.round(1*resources.displayMetrics.density)

            textView.layoutParams = glparams
            binding.glTable.addView(textView)
        }
    }
}