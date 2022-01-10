package com.homubee.waterrate.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.homubee.waterrate.databinding.ActivityInitializePublicBinding
import com.homubee.waterrate.databinding.DialogPublicInputBinding
import com.homubee.waterrate.model.PublicRate

class InitializePublicActivity : AppCompatActivity() {
    lateinit var adapter: PublicRateAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInitializePublicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PublicRateAdapter(mutableListOf<PublicRate>())
        binding.recyclerPublic.layoutManager = LinearLayoutManager(this)
        binding.recyclerPublic.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val dialogBinding = DialogPublicInputBinding.inflate(layoutInflater)
            AlertDialog.Builder(this).run {
                setTitle("공용 수도 설비 입력")
                setIcon(android.R.drawable.ic_menu_edit)
                setView(dialogBinding.root)
                setPositiveButton("확인", DialogInterface.OnClickListener { p0, p1 ->
                    val name = dialogBinding.etName.text.toString()
                    val count = dialogBinding.etCount.text.toString()
                    val privateList = mutableListOf<String>()

                    if (count.contains(' ') || count.contains('-')) {
                        Toast.makeText(context, "숫자만 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.add(PublicRate(name, count.toInt(), privateList))
                    }
                })
                setNegativeButton("취소", null)
                show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuNext: MenuItem? = menu?.add(0, 0, 0, "다음")
        menuNext?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_WITH_TEXT)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        0 -> {
            val intent = Intent(applicationContext, InitializePrivateActivity::class.java)
            intent.putExtra("public",  ArrayList(adapter.datas))
            startActivity(intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}