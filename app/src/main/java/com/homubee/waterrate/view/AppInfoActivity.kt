package com.homubee.waterrate.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.homubee.waterrate.BuildConfig
import com.homubee.waterrate.R
import com.homubee.waterrate.databinding.ActivityAppInfoBinding

class AppInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityAppInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "앱 정보"

        binding.tvAppInfo.text = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME
    }
}