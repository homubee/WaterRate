package com.homubee.waterrate.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.homubee.waterrate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalculate.isEnabled = false

        // 초기화 버튼 클릭 시 경고 다이얼로그 출력
        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("주의")
                setIcon(android.R.drawable.ic_dialog_alert)
                setMessage("정말 초기화하시겠습니까?")
                setPositiveButton("yes", DialogInterface.OnClickListener { p0, p1 ->
                    Toast.makeText(applicationContext, "reset", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, InitializePublicActivity::class.java)
                    startActivity(intent)
                    binding.btnCalculate.isEnabled = true
                })
                setNegativeButton("no", null)
                show()
            }
        }

        binding.btnCalculate.setOnClickListener {
            Toast.makeText(this, "calculate", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CalculateActivity::class.java)
            startActivity(intent)
        }
    }
}