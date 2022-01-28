package com.homubee.waterrate.view

import android.content.ContentValues
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.LinearLayoutManager
import com.homubee.waterrate.databinding.ActivityInitializePrivateBinding
import com.homubee.waterrate.databinding.DialogWaterInputBinding
import com.homubee.waterrate.model.WaterRate
import com.homubee.waterrate.util.DBHelper

class InitializePrivateActivity : AppCompatActivity() {
    companion object {
        const val CHECKBOX_ID = 100
    }
    lateinit var publicDataList: MutableList<WaterRate>
    lateinit var adapter: WaterRateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInitializePrivateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // intent 로부터 데이터 전달 받음
        val intent = intent
        val nameSet = intent.getSerializableExtra("nameSet") as MutableSet<String>
        publicDataList = intent.getParcelableArrayListExtra<Parcelable>("public") as MutableList<WaterRate>
        adapter = WaterRateAdapter(mutableListOf<WaterRate>())
        binding.recyclerPrivate.layoutManager = LinearLayoutManager(this)
        binding.recyclerPrivate.adapter = adapter

        // 모든 내용이 삭제되었을 시 설명을 출력할 수 있도록 삭제 시마다 콜백함수를 통해 체크 및 작업 수행
        // 내용 삭제 시 내부에 가지고 있는 리스트 목록에서도 삭제
        // 내용 삭제 시 이름 중복 방지 집합에서도 제거함
        adapter.buttonClickListener = object: WaterRateAdapter.ButtonCallbackListener{
            override fun callBack(name: String) {
                nameSet.remove(name)
                for (i in publicDataList.indices) {
                    if (publicDataList[i].name == name) {
                        publicDataList.removeAt(i)
                    }
                }

                if (adapter.dataList.isEmpty()) binding.tvAddNotice.visibility = View.VISIBLE
            }
        }

        // 추가 버튼 클릭 시 다이얼로그 출력
        binding.fabAdd.setOnClickListener {
            val dialogBinding = DialogWaterInputBinding.inflate(layoutInflater)
            val firstView = dialogBinding.root.getChildAt(0) as LinearLayout
            dialogBinding.tvName.text = "상호명"
            AlertDialog.Builder(this).apply {
                setTitle("개인 수도 입력")
                setIcon(android.R.drawable.ic_menu_edit)

                // 계량기 유무 뷰그룹 활성화
                dialogBinding.llCounter.visibility = View.VISIBLE
                // 텍스트뷰 동적으로 추가
                val textView = TextView(applicationContext)
                textView.text = "공용 수도 선택"
                firstView.addView(textView)
                val llparams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                llparams.leftMargin = Math.round(10*resources.displayMetrics.density)
                llparams.rightMargin = Math.round(10*resources.displayMetrics.density)
                llparams.topMargin = Math.round(5*resources.displayMetrics.density)
                llparams.bottomMargin = Math.round(10*resources.displayMetrics.density)
                textView.layoutParams = llparams

                // 체크 박스 동적으로 추가
                val checkBoxList = mutableListOf<CheckBox>()
                for (i in publicDataList.indices) {
                    checkBoxList.add(CheckBox(applicationContext))
                    val llparams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    llparams.leftMargin = Math.round(10*resources.displayMetrics.density)
                    checkBoxList[i].layoutParams = llparams
                    checkBoxList[i].id = CHECKBOX_ID+i
                    checkBoxList[i].text = publicDataList[i].name
                    firstView.addView(checkBoxList[i])
                }
                setView(dialogBinding.root)

                // 확인 버튼 클릭 시 내용 예외처리 후 데이터 객체 생성 및 리사이클러뷰에 추가
                // 공용, 개인 수도의 수도 리스트 역시 이때 처리
                setPositiveButton("확인", DialogInterface.OnClickListener { p0, p1 ->
                    val name = dialogBinding.etName.text.toString()
                    var count = dialogBinding.etCount.text.toString()
                    val publicList = mutableListOf<String>()

                    for (i in publicDataList.indices) {
                        val publicCheckBox = firstView.findViewById<CheckBox>(CHECKBOX_ID+i)
                        if (publicCheckBox.isChecked) {
                            publicDataList[i].waterRateList.add(name)
                            publicList.add(publicCheckBox.text.toString())
                        }
                    }

                    var type = 1
                    if (dialogBinding.rgCounter.checkedRadioButtonId == dialogBinding.rdbtnNoCounter.id) {
                        type = 2
                    }

                    // 입력 예외처리
                    if (name.isBlank() || (type != 2 && count.isBlank())) {
                        Toast.makeText(context, "내용을 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else if (nameSet.contains(name)) {
                        Toast.makeText(context, "이름이 중복되지 않아야 합니다.", Toast.LENGTH_SHORT).show()
                    } else if (count.contains(' ') || count.contains('-') || count.contains(',')) {
                        Toast.makeText(context, "숫자만 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else if (count.isNotBlank() && count.toDouble() > 9999.5) {
                        Toast.makeText(context, "지침 숫자는 9999.5를 넘을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    } else if (dialogBinding.rgCounter.checkedRadioButtonId == -1) {
                        Toast.makeText(context, "계량기 유무를 체크해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        if (type == 2) count = "-1"
                        nameSet.add(name)
                        adapter.add(WaterRate(type, name, count.toDouble(), publicList))
                        if (binding.tvAddNotice.isVisible) binding.tvAddNotice.visibility = View.GONE
                    }
                })
                setNegativeButton("취소", null)
                show()
            }
        }
    }

    // 메뉴 버튼 추가 및 액티비티 전환
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuNext: MenuItem? = menu?.add(0, 0, 0, "완료")
        menuNext?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_WITH_TEXT)
        return super.onCreateOptionsMenu(menu)
    }
    // DB에 데이터 저장 및 액티비티 종료
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        0 -> {
            val db: SQLiteDatabase = DBHelper(applicationContext).writableDatabase
            val privateDataList = adapter.dataList
            for (i in privateDataList.indices) {
                val values = ContentValues()
                var list = ""
                values.put("type", privateDataList[i].type)
                values.put("name", privateDataList[i].name)
                values.put("count", privateDataList[i].lastMonthCount)
                for (j in privateDataList[i].waterRateList.indices) {
                    list += privateDataList[i].waterRateList[j] + if (j == privateDataList[i].waterRateList.size-1) {""} else {","}
                }
                values.put("list", list)
                db.insert("water_rate", null, values)
            }
            for (i in publicDataList.indices) {
                val values = ContentValues()
                var list = ""
                values.put("type", publicDataList[i].type)
                values.put("name", publicDataList[i].name)
                values.put("count", publicDataList[i].lastMonthCount)
                for (j in publicDataList[i].waterRateList.indices) {
                    list += publicDataList[i].waterRateList[j] + if (j == publicDataList[i].waterRateList.size-1) {""} else {","}
                }
                values.put("list", list)
                db.insert("water_rate", null, values)
            }

            Toast.makeText(applicationContext, "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}