package com.homubee.waterrate.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.homubee.waterrate.databinding.ActivityInitializePublicBinding
import com.homubee.waterrate.databinding.DialogWaterInputBinding
import com.homubee.waterrate.model.WaterRate

class InitializePublicActivity : AppCompatActivity() {
    lateinit var adapter: WaterRateAdapter
    lateinit var nameSet: MutableSet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInitializePublicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = WaterRateAdapter(mutableListOf<WaterRate>())
        binding.recyclerPublic.layoutManager = LinearLayoutManager(this)
        binding.recyclerPublic.adapter = adapter

        nameSet = mutableSetOf()

        // 모든 내용이 삭제되었을 시 설명을 출력할 수 있도록 삭제 시마다 콜백함수를 통해 체크 및 작업 수행
        // 내용 삭제 시 이름 중복 방지 집합에서도 제거함
        adapter.buttonClickListener = object: WaterRateAdapter.ButtonCallbackListener {
            override fun callBack(name: String) {
                nameSet.remove(name)
                if (adapter.dataList.isEmpty()) binding.tvAddNotice.visibility = View.VISIBLE
            }
        }

        // 추가 버튼 클릭 시 다이얼로그 출력
        binding.fabAdd.setOnClickListener {
            val dialogBinding = DialogWaterInputBinding.inflate(layoutInflater)
            AlertDialog.Builder(this).apply {
                setTitle("공용 수도 입력")
                setIcon(android.R.drawable.ic_menu_edit)
                setView(dialogBinding.root)

                // 확인 버튼 클릭 시 내용 예외처리 후 데이터 객체 생성 및 리사이클러뷰에 추가
                setPositiveButton("확인", DialogInterface.OnClickListener { p0, p1 ->
                    val name = dialogBinding.etName.text.toString()
                    val count = dialogBinding.etCount.text.toString()
                    val privateList = mutableListOf<String>()

                    if (name.isBlank() || count.isBlank()) {
                        Toast.makeText(context, "내용을 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else if (nameSet.contains(name)) {
                        Toast.makeText(context, "이름이 중복되지 않아야 합니다.", Toast.LENGTH_SHORT).show()
                    } else if (count.contains(' ') || count.contains('-') || count.contains(',')) {
                        Toast.makeText(context, "숫자만 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                    } else if (count.toDouble() > 9999.5) {
                        Toast.makeText(context, "지침 숫자는 9999.5를 넘을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        nameSet.add(name)
                        adapter.add(WaterRate(0, name, count.toDouble(), privateList))
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
        val menuNext: MenuItem? = menu?.add(0, 0, 0, "다음")
        menuNext?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_WITH_TEXT)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        0 -> {
            val intent = Intent(applicationContext, InitializePrivateActivity::class.java)
            intent.putExtra("public",  ArrayList(adapter.dataList))
            intent.putExtra("nameSet", LinkedHashSet(nameSet))
            startActivity(intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}