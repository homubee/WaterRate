package com.homubee.waterrate.view

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.homubee.waterrate.databinding.ActivityInitializePrivateBinding
import com.homubee.waterrate.databinding.DialogPublicInputBinding
import com.homubee.waterrate.model.PrivateRate
import com.homubee.waterrate.model.PublicRate

class InitializePrivateActivity : AppCompatActivity() {
    val CHECKBOX_ID = 100
    lateinit var publicDataList: MutableList<PublicRate>
    lateinit var adapter: PrivateRateAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInitializePrivateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        publicDataList = intent.getParcelableArrayListExtra<Parcelable>("public") as MutableList<PublicRate>
        adapter = PrivateRateAdapter(mutableListOf<PrivateRate>())
        binding.recyclerPrivate.layoutManager = LinearLayoutManager(this)
        binding.recyclerPrivate.adapter = adapter
        adapter.buttonClickListener = object: PrivateRateAdapter.ButtonCallbackListener{
            override fun callBack() {
                if (adapter.datas.isEmpty()) binding.tvAddNotice.visibility = View.VISIBLE
            }
        }

        binding.fabAdd.setOnClickListener {
            val dialogBinding = DialogPublicInputBinding.inflate(layoutInflater)
            AlertDialog.Builder(this).run {
                setTitle("개인 수도 설비 입력")
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

                setPositiveButton("확인", DialogInterface.OnClickListener { p0, p1 ->
                    val name = dialogBinding.etName.text.toString()
                    val count = dialogBinding.etCount.text.toString()
                    val publicList = mutableListOf<String>()

                    for (i in publicDataList.indices) {
                        val publicCheckBox = dialogBinding.root.findViewById<CheckBox>(CHECKBOX_ID+i)
                        if (publicCheckBox.isChecked) {
                            publicDataList[i].privateList.add(name)
                            publicList.add(publicCheckBox.text.toString())
                        }
                    }

                    if (name.isBlank() || count.isBlank()) {
                        Toast.makeText(context, "내용을 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else if (count.contains(' ') || count.contains('-')) {
                        Toast.makeText(context, "숫자만 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.add(PrivateRate(name, count.toInt(), publicList))
                        if (binding.tvAddNotice.isVisible) binding.tvAddNotice.visibility = View.GONE
                    }
                })
                setNegativeButton("취소", null)
                show()
            }
        }
    }
}