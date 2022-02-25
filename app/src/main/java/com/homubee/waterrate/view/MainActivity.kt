package com.homubee.waterrate.view

import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.homubee.waterrate.databinding.ActivityMainBinding
import com.homubee.waterrate.util.DBHelper

/**
 * 메인 액티비티 클래스
 *
 * 초기화, 계산 액티비티로 이동 가능
 *
 * 메뉴바에는 도움말, 앱정보, 오픈소스 라이브러리 기능 제공
 */
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // DB 내용 empty 여부 확인 후 empty 일 경우 계산 버튼 비활성화
        val db: SQLiteDatabase = DBHelper(applicationContext).readableDatabase
        binding.btnCalculate.isEnabled = db.rawQuery("select * from water_rate", null).moveToNext()

        // 초기화 버튼 클릭 시 경고 다이얼로그 출력
        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("주의")
                setIcon(android.R.drawable.ic_dialog_alert)
                setMessage("정말 초기화하시겠습니까?")
                setPositiveButton("yes", DialogInterface.OnClickListener { p0, p1 ->
                    // DB 초기화
                    val db: SQLiteDatabase = DBHelper(applicationContext).writableDatabase
                    db.execSQL("delete from water_rate")

                    val intent = Intent(applicationContext, InitializePublicActivity::class.java)
                    startActivity(intent)
                })
                setNegativeButton("no", null)
                show()
            }
        }

        binding.btnCalculate.setOnClickListener {
            val intent = Intent(this, CalculateActivity::class.java)
            startActivity(intent)
        }
    }

    // 다시 액티비티에 복귀했을 시 DB 내용 empty 여부 확인 후 empty 일 경우 계산 버튼 비활성화
    override fun onRestart() {
        super.onRestart()
        val db: SQLiteDatabase = DBHelper(applicationContext).readableDatabase
        binding.btnCalculate.isEnabled = db.rawQuery("select * from water_rate", null).moveToNext()
    }

    // 메뉴 버튼 추가 및 액티비티 전환
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuHelp: MenuItem? = menu?.add(0, 0, 0, "도움말")
        val menuAppInfo: MenuItem? = menu?.add(0, 1, 0, "앱 정보")
        val menuLicense: MenuItem? = menu?.add(0, 2, 0, "오픈소스 라이선스")
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        0 -> {
            val intent = Intent(applicationContext, HelpActivity::class.java)
            startActivity(intent)
            true
        }
        1 -> {
            val intent = Intent(applicationContext, AppInfoActivity::class.java)
            startActivity(intent)
            true
        }
        2 -> {
            // OssLicense 활용
            val intent = Intent(applicationContext, OssLicensesMenuActivity::class.java)
            startActivity(intent)
            OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}