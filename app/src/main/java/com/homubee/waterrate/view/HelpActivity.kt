package com.homubee.waterrate.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.homubee.waterrate.R

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        supportActionBar?.title = "도움말"
    }
}