package com.homubee.waterrate.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.homubee.waterrate.R
import com.homubee.waterrate.databinding.ActivityInitializePublicBinding
import com.homubee.waterrate.databinding.ActivityMainBinding
import com.homubee.waterrate.model.PublicRate

class InitializePublicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInitializePublicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 임시 테스트 데이터
        val tempList: MutableList<PublicRate> = mutableListOf(
                PublicRate("화장실(1층)", 500),
                PublicRate("화장실(2층)", 178),
                PublicRate("음수대", 20)
        )
        val adapter = PublicRateAdapter(tempList)
        binding.recyclerPublic.layoutManager = LinearLayoutManager(this)
        binding.recyclerPublic.adapter = adapter

        binding.mainFab.setOnClickListener {
            adapter.add(PublicRate("임시 - " + (adapter.itemCount+1), 0))
        }
    }
}