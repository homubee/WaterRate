package com.homubee.waterrate.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.homubee.waterrate.R

/**
 * 도움말 액티비티 클래스
 *
 * 앱 사용 방법 소개
 */
class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        supportActionBar?.title = "도움말"
    }
}