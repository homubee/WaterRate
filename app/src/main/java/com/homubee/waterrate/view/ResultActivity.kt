package com.homubee.waterrate.view

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import com.homubee.waterrate.R
import com.homubee.waterrate.databinding.ActivityResultBinding
import com.homubee.waterrate.model.WaterRate
import java.io.File


class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    // 갤러리 인텐트에서 넘어온 이미지를 비트맵 객체로 만들어 화면에 출력
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        try {
            val option = BitmapFactory.Options()
            // 임의로 설정한 값, 알고리즘 적용하여 설정해야 함
            option.inSampleSize = 3

            var inputStream = contentResolver.openInputStream(result.data!!.data!!)
            val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
            inputStream!!.close()
            inputStream = null
            bitmap?.let {
                binding.ivPaper.apply {
                    setImageBitmap(bitmap)
                    visibility = View.VISIBLE
                }
            } ?: let {
                Log.d("null", "bitmap null.............")
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // round function
    // Works as same as Excel
    private fun roundDigit(number : Double, digits : Int): Double {
        return Math.round(number * Math.pow(10.0, digits.toDouble())) / Math.pow(10.0, digits.toDouble())
    }

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

        // 사용량 리스트, 요금 리스트, 반올림 요금 차이 리스트
        val usageList = mutableListOf<Double>()
        val rateList = mutableListOf<Int>()
        val diffRateList = mutableListOf<Int>()

        // 상위 3개 리스트 초기화
        for (i in totalWaterRateList.indices) {
            usageList.add(thisMonthCountList[i] - totalWaterRateList[i].lastMonthCount)
            rateList.add(roundDigit(ratePerOne*usageList[i], -1).toInt())
            diffRateList.add(roundDigit(ratePerOne*usageList[i], 0).toInt())
        }

        // 요금 데이터에 공용 수도 요금 분배
        var rateSum = 0
        var maxIndex = 0
        var minIndex = 0
        for (i in totalWaterRateList.indices) {
            // 공용 수도일 경우 체크
            if (totalWaterRateList[i].type == 0) {
                var splitRate = rateList[i].toDouble() / totalWaterRateList[i].waterRateList.size
                // name으로 공용 수도에 해당하는 개인 수도 찾아서 분할 요금 추가
                for (j in totalWaterRateList[i].waterRateList.indices) {
                    for (k in totalWaterRateList.indices) {
                        if (totalWaterRateList[i].waterRateList[j] == totalWaterRateList[k].name) {
                            rateList[k] = roundDigit(rateList[k] + splitRate, -1).toInt()
                            diffRateList[k] = roundDigit(diffRateList[k] + splitRate, 0).toInt()
                        }
                    }
                }
            } else {
                // 공용 수도 아닌 경우 인덱스 체크하며 실제값과 차이가 가장 큰 것과 작은 것 계산, 반올림 요금 차이 리스트도 여기서 완전히 확정됨
                if (maxIndex == 0) {
                    maxIndex = i
                    minIndex = i
                }
                Log.d("rateList " + i.toString(), rateList[i].toString())
                Log.d("diffRateList " + i.toString(), diffRateList[i].toString())
                rateSum += rateList[i]
                diffRateList[i] -= rateList[i]
                maxIndex = if (diffRateList[i] < diffRateList[maxIndex]) { maxIndex } else { i }
                minIndex = if (diffRateList[i] < diffRateList[minIndex]) { i } else { minIndex }
            }
        }

        // 실제 요금합과 청구된 요금 값이 차이가 있는 경우, 가장 차이가 큰 요금에서 차감, 가산
        if (rateSum > totalRate) {
            rateList[minIndex] += totalRate - rateSum
        } else if (rateSum < totalRate) {
            rateList[maxIndex] += totalRate - rateSum
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

    // 메뉴 등록
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_result, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 메뉴별 결과
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.gallery -> {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            resultLauncher.launch(intent)
            true
        }
        R.id.pdf -> {
            val document = PdfDocument()

            val content = binding.llMain
            Log.d("width", binding.root.width.toString())
            Log.d("height", binding.root.height.toString())

            val page = document.startPage(PdfDocument.PageInfo.Builder(content.width, content.height, 1).create())

            content.draw(page.canvas)
            document.finishPage(page)

            val file = File(filesDir, "result.pdf")
            document.writeTo(file.outputStream())
            document.close()

            Toast.makeText(applicationContext, "pdf 파일이 생성되었습니다.", Toast.LENGTH_SHORT)
            true
        }
        R.id.save -> {
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}