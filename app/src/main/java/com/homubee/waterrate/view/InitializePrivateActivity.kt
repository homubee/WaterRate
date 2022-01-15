package com.homubee.waterrate.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.homubee.waterrate.databinding.ActivityInitializePrivateBinding
import com.homubee.waterrate.databinding.DialogWaterInputBinding
import com.homubee.waterrate.model.WaterRate

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
        publicDataList = intent.getParcelableArrayListExtra<Parcelable>("public") as MutableList<WaterRate>
        adapter = WaterRateAdapter(1, mutableListOf<WaterRate>())
        binding.recyclerPrivate.layoutManager = LinearLayoutManager(this)
        binding.recyclerPrivate.adapter = adapter

        // 모든 내용이 삭제되었을 시 설명을 출력할 수 있도록 삭제 시마다 콜백함수를 통해 체크 및 작업 수행
        // 내용 삭제 시 내부에 가지고 있는 리스트 목록에서도 삭제
        adapter.buttonClickListener = object: WaterRateAdapter.ButtonCallbackListener{
            override fun callBack(name: String) {
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
            dialogBinding.tvName.text = "상호명"
            AlertDialog.Builder(this).apply {
                setTitle("개인 수도 입력")
                setIcon(android.R.drawable.ic_menu_edit)

                // 체크 박스 동적으로 추가
                val checkBoxList = mutableListOf<CheckBox>()
                for (i in publicDataList.indices) {
                    checkBoxList.add(CheckBox(applicationContext))
                    val llparams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    llparams.leftMargin = Math.round(10*resources.displayMetrics.density)
                    checkBoxList[i].layoutParams = llparams
                    checkBoxList[i].id = CHECKBOX_ID+i
                    checkBoxList[i].text = publicDataList[i].name
                    dialogBinding.root.addView(checkBoxList[i])
                }
                setView(dialogBinding.root)

                // 확인 버튼 클릭 시 내용 예외처리 후 데이터 객체 생성 및 리사이클러뷰에 추가
                // 공용, 개인 수도의 수도 리스트 역시 이때 처리
                setPositiveButton("확인", DialogInterface.OnClickListener { p0, p1 ->
                    val name = dialogBinding.etName.text.toString()
                    val count = dialogBinding.etCount.text.toString()
                    val publicList = mutableListOf<String>()

                    for (i in publicDataList.indices) {
                        val publicCheckBox = dialogBinding.root.findViewById<CheckBox>(CHECKBOX_ID+i)
                        if (publicCheckBox.isChecked) {
                            publicDataList[i].waterRateList.add(name)
                            publicList.add(publicCheckBox.text.toString())
                        }
                    }

                    if (name.isBlank() || count.isBlank()) {
                        Toast.makeText(context, "내용을 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else if (count.contains(' ') || count.contains('-') || count.contains(',') || count.contains('.')) {
                        Toast.makeText(context, "숫자만 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.add(WaterRate(1, name, count.toInt(), publicList))
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        0 -> {
            Toast.makeText(applicationContext, "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}