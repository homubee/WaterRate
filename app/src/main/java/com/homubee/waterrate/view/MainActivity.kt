package com.homubee.waterrate.view

import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.homubee.waterrate.databinding.ActivityMainBinding
import com.homubee.waterrate.util.DBHelper

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // DB 내용 empty 여부 확인 및 버튼 비활성화 설정
        val db: SQLiteDatabase = DBHelper(applicationContext).readableDatabase
        binding.btnCalculate.isEnabled = db.rawQuery("select * from water_rate", null).moveToNext()

        // 초기화 버튼 클릭 시 경고 다이얼로그 출력
        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("주의")
                setIcon(android.R.drawable.ic_dialog_alert)
                setMessage("정말 초기화하시겠습니까?")
                setPositiveButton("yes", DialogInterface.OnClickListener { p0, p1 ->
                    val db: SQLiteDatabase = DBHelper(applicationContext).writableDatabase
                    db.execSQL("delete from water_rate")
                    Toast.makeText(applicationContext, "reset", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, InitializePublicActivity::class.java)
                    startActivity(intent)
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

    // 다시 액티비티에 복귀했을 시 DB 내용 empty 여부 확인 및 버튼 비활성화 설정
    override fun onRestart() {
        super.onRestart()
        val db: SQLiteDatabase = DBHelper(applicationContext).readableDatabase
        binding.btnCalculate.isEnabled = db.rawQuery("select * from water_rate", null).moveToNext()
    }
}