package com.homubee.waterrate.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.homubee.waterrate.databinding.ActivityInitializePublicBinding
import com.homubee.waterrate.databinding.DialogPublicInputBinding
import com.homubee.waterrate.model.PublicRate

class InitializePublicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInitializePublicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataList: MutableList<PublicRate> = mutableListOf<PublicRate>()
        val adapter = PublicRateAdapter(dataList)
        binding.recyclerPublic.layoutManager = LinearLayoutManager(this)
        binding.recyclerPublic.adapter = adapter

        binding.mainFab.setOnClickListener {
            val dialogBinding = DialogPublicInputBinding.inflate(layoutInflater)
            AlertDialog.Builder(this).run {
                setTitle("공용 수도 설비 입력")
                setIcon(android.R.drawable.ic_menu_edit)
                setView(dialogBinding.root)
                setPositiveButton("확인", DialogInterface.OnClickListener { p0, p1 ->
                    val name = dialogBinding.etName.text.toString()
                    val count = dialogBinding.etCount.text.toString()

                    if (count.contains(' ') || count.contains('-')) {
                        Toast.makeText(context, "숫자만 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.add(PublicRate(name, count.toInt()))
                    }
                })
                setNegativeButton("취소", null)
                show()
            }
        }
    }
}