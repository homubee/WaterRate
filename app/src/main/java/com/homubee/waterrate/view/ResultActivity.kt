package com.homubee.waterrate.view

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.GridLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.core.view.setPadding
import com.homubee.waterrate.R
import com.homubee.waterrate.databinding.ActivityResultBinding
import com.homubee.waterrate.model.WaterRate
import com.homubee.waterrate.util.DBHelper
import java.io.FileOutputStream


class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    lateinit var totalWaterRateList: MutableList<WaterRate>
    lateinit var thisMonthCountList: MutableList<Double>

    // 갤러리 인텐트에서 넘어온 이미지를 비트맵 객체로 만들어 화면에 출력
    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        try {
            var inputStream = contentResolver.openInputStream(result.data!!.data!!)
            val rootView = binding.root.rootView

            val option = BitmapFactory.Options()

            // 너무 큰 이미지는 미리 처리
            option.inJustDecodeBounds = true
            var bitmap = BitmapFactory.decodeStream(inputStream, null, option)

            option.inSampleSize = calculateInSampleSize(option, rootView.width, rootView.height)

            option.inJustDecodeBounds = false
            inputStream = contentResolver.openInputStream(result.data!!.data!!)
            bitmap = BitmapFactory.decodeStream(inputStream, null, option)

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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // pdf 저장 경로 선택
    private val pdfResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val uri = result.data?.data
        Log.d("URI", uri.toString())

        val document = PdfDocument()

        val content = binding.llMain
        Log.d("width", binding.root.width.toString())
        Log.d("height", binding.root.height.toString())

        val page = document.startPage(PdfDocument.PageInfo.Builder(content.width, content.height, 1).create())

        content.draw(page.canvas)
        document.finishPage(page)

        if (uri != null) {
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "w")
            document.writeTo(FileOutputStream(fileDescriptor?.fileDescriptor))
        } else {
            Toast.makeText(this, "파일 저장 실패", Toast.LENGTH_SHORT).show()
        }

        document.close()
    }

    // calculate inSampleSize function
    // code from https://developer.android.com/topic/performance/graphics/load-bitmap?hl=ko
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    // round function
    // Works as same as Excel
    private fun roundDigit(number : Double, digits : Int): Double {
        return Math.round(number * Math.pow(10.0, digits.toDouble())) / Math.pow(10.0, digits.toDouble())
    }

    // comma on number function
    private fun putComma(number: Int): String {
        var ret = StringBuilder(number.toString())
        var count = 0
        for (i in ret.length-1 downTo 0) {
            count++;
            if (count == 3 && i != 0) {
                ret.insert(i, ',')
                count = 0
            }
        }
        return ret.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "계산 결과"

        // intent 로부터 데이터 전달 받음
        val intent = intent
        totalWaterRateList = intent.getParcelableArrayListExtra<Parcelable>("waterRateList") as MutableList<WaterRate>
        thisMonthCountList = intent.getParcelableArrayListExtra<Parcelable>("thisMonthCountList") as MutableList<Double>
        val totalUsage = intent.getIntExtra("totalUsage", 0)
        val totalRate = intent.getIntExtra("totalRate", 0)
        val ratePerOne: Double = totalRate / totalUsage.toDouble()

        // 사용량 리스트, 요금 리스트, 반올림 요금 차이 리스트
        val usageList = mutableListOf<Double>()
        val rateList = mutableListOf<Int>()
        val diffRateList = mutableListOf<Int>()

        // 표 크기 공유 프리퍼런스 설정
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        binding.sbTableSize.progress = sharedPref.getInt("tableSize", 10)

        // 상위 3개 리스트 초기화
        for (i in totalWaterRateList.indices) {
            // 지침 대소관계가 뒤바뀌어 있다면 오버플로우이므로 감안하여 계산
            if (thisMonthCountList[i] >= totalWaterRateList[i].lastMonthCount) {
                usageList.add(thisMonthCountList[i] - totalWaterRateList[i].lastMonthCount)
            } else {
                usageList.add(10000 - totalWaterRateList[i].lastMonthCount + thisMonthCountList[i])
            }
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
            }
        }

        for (i in totalWaterRateList.indices) {
            // 공용 수도 아닐 경우 체크
            if (totalWaterRateList[i].type != 0) {
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
                textView.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                textView.setPadding(Math.round(0.1*resources.displayMetrics.density).toInt())
                textView.setBackgroundColor(Color.WHITE)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f)

                val glparams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setGravity(Gravity.FILL)
                }

                when(j) {
                    // 설비/상호명
                    1 -> {
                        textView.gravity = Gravity.CENTER
                        textView.text = totalWaterRateList[i].name
                        glparams.leftMargin = Math.round(1*resources.displayMetrics.density)
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    // 전월지침
                    2 -> {
                        textView.text = totalWaterRateList[i].lastMonthCount.toString() + " "
                        glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    // 금월지침
                    3 -> {
                        textView.text = thisMonthCountList[i].toString() + " "
                        glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    // 사용량
                    4 -> {
                        textView.text = usageList[i].toString() + " "
                        glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                        glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    }
                    // 요금
                    5 -> {
                        textView.text = putComma(rateList[i])
                        glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                        glparams.rightMargin = Math.round(1*resources.displayMetrics.density)
                    }
                    else -> ""
                }

                // 계량기 없는 경우는 지침과 사용량 정보를 표시하지 않음
                if (totalWaterRateList[i].type == 2 && j != 1 && j != 5) {
                    textView.text = ""
                }

                glparams.topMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                glparams.bottomMargin = Math.round(0.5*resources.displayMetrics.density).toInt()

                textView.layoutParams = glparams
                binding.glTable.addView(textView)
            }
        }

        // 전체 합산 수치 출력
        for (i in 1..3) {
            val textView = TextView(this)
            textView.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            textView.setPadding(Math.round(0.1*resources.displayMetrics.density).toInt())
            textView.setBackgroundColor(Color.WHITE)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f)
            textView.setTypeface(null, Typeface.BOLD)

            val glparams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setGravity(Gravity.FILL)
            }

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
                    textView.text = totalUsage.toDouble().toString() + " "
                    glparams.leftMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                    glparams.rightMargin = Math.round(0.5*resources.displayMetrics.density).toInt()
                }
                // 전체 요금
                3 -> {
                    textView.text = putComma(totalRate)
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

        binding.tvTableSize.apply {
            text = text.toString() + (binding.sbTableSize.progress).toString()
        }

        // seekBar를 통한 표 크기 조절
        binding.sbTableSize.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvTableSize.apply {
                    text = "표 크기 : " + (binding.sbTableSize.progress).toString()
                }
                for (textView in binding.glTable) {
                    if (textView is TextView) {
                        Log.d("Progress", (seekBar!!.progress).toString())
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, seekBar!!.progress.toFloat())
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        // 크기 결정 버튼
        binding.btnSet.setOnClickListener {
            binding.tvTableSize.visibility = View.GONE
            binding.btnSet.visibility = View.GONE
            binding.sbTableSize.visibility = View.GONE

            // 표 크기 정보 갱신
            sharedPref.edit().run {
                putInt("tableSize", binding.sbTableSize.progress)
                commit()
            }
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
            galleryResultLauncher.launch(intent)
            true
        }
        R.id.pdf -> {
            if (!binding.btnSet.isVisible) {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_TITLE, "수도 요금 정산 내역")
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, "content://com.android.externalstorage.documents/")
                }
                pdfResultLauncher.launch(intent)
                true
            } else {
                Toast.makeText(this, "표 크기 설정을 완료해야 합니다.", Toast.LENGTH_SHORT).show()
                false
            }
        }
        R.id.save -> {
            val db: SQLiteDatabase = DBHelper(applicationContext).writableDatabase
            for (i in totalWaterRateList.indices) {
                db.execSQL("update water_rate set count = " + thisMonthCountList[i] + " where name = '" + totalWaterRateList[i].name + "';")
            }
            Toast.makeText(this, "전월지침을 갱신하였습니다.", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}