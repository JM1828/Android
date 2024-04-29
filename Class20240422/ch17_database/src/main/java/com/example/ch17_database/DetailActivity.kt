package com.example.ch17_database

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ch17_database.databinding.ActivityDetailBinding
import com.example.ch17_database.ModifyActivity

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var modifyActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // intent로부터 데이터와 위치 정보를 가져옴
        val name = intent.getStringExtra("name")
        val age = intent.getStringExtra("age")
        val phone = intent.getStringExtra("phone")
        val positionData = intent.getIntExtra("position", -1)

        binding.nameText.text = name
        binding.ageText.text = age
        binding.phoneText.text = phone

        // 결과를 처리하는 ActivityResultLauncher 초기화
        initModifyActivityResultLauncher(positionData)

        // 수정 버튼 클릭 시 ModifyActivity 클래스로 이동
        binding.btnEdit.setOnClickListener {
            launchModifyActivity(name, age, phone)
        }

        // 삭제 버튼 클릭 시
        binding.btnDelete.setOnClickListener {
            showDeleteConfirmDialog(name, age, phone, positionData)
        }

        // 뒤로 가기 버튼 클릭 시
        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    // ActivityResultLauncher를 초기화, 이 함수는 사용자가 수정 화면에서 데이터를 수정하고 돌아올 때 호출됨
    private fun initModifyActivityResultLauncher(positionData: Int) {
        modifyActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedName = result.data?.getStringExtra("updatedName")
                val updatedAge = result.data?.getStringExtra("updatedAge")
                val updatedPhone = result.data?.getStringExtra("updatedPhone")

                if (updatedName != null && updatedAge != null && updatedPhone != null) {
                    // 수정된 데이터를 텍스트 뷰에 업데이트하고 결과를 반환
                    updateDetailTextAndReturnResult(updatedName, updatedAge, updatedPhone, positionData)
                }
            }
        }
    }

    // 수정된 데이터를 상세 정보 텍스트 뷰에 업데이트하고, 수정된 데이터와 위치 정보를 포함하는 인텐트를 결과로 설정
    private fun updateDetailTextAndReturnResult(updatedName: String, updatedAge: String, updatedPhone: String, positionData: Int) {
        binding.nameText.text = updatedName // 이름 업데이트
        binding.ageText.text = updatedAge // 나이 업데이트
        binding.phoneText.text = updatedPhone // 전화번호 업데이트

        val returnIntent = Intent().apply {
            putExtra("updatedName", updatedName)
            putExtra("updatedAge", updatedAge)
            putExtra("updatedPhone", updatedPhone)
            putExtra("position", positionData) // 받은 위치 정보를 다시 반환
        }
        setResult(Activity.RESULT_OK, returnIntent) // 결과를 설정
    }

    // 수정 화면으로 이동하기 위해 ModifyActivity를 시작, 수정할 현재 데이터를 인텐트에 포함시킴
    private fun launchModifyActivity(detailName: String?, detailAge: String?, detailPhone: String?) {
        val intent = Intent(this, ModifyActivity::class.java).apply {
            putExtra("name", detailName)
            putExtra("age", detailAge)
            putExtra("phone", detailPhone)
        }
        modifyActivityResultLauncher.launch(intent) // ActivityResultLauncher를 사용하여 화면을 시작
    }

    // 데이터베이스에서 특정 데이터를 삭제하고, 삭제된 항목의 위치 정보를 결과로 반환한 후 화면을 종료
    private fun deleteDataAndReturnResult(detailName: String?, detailAge: String?, detailPhone: String?, positionData: Int) {
        DBHelper(this).writableDatabase.apply {
            execSQL("DELETE FROM MEMBER_TB WHERE name = ? AND age = ? AND phone = ?", arrayOf(detailName, detailAge, detailPhone)) // 데이터베이스에서 데이터를 삭제
            close() // 데이터베이스를 닫음
        }
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("position", positionData) // 삭제된 항목의 위치 정보를 인텐트에 포함시킴
        })
        finish() // 화면을 종료
    }

    // 사용자에게 데이터 삭제를 확인하는 대화 상자를 보여줌, '확인'을 선택하면 데이터를 삭제하고, '취소'를 선택하면 대화 상자를 닫음
    private fun showDeleteConfirmDialog(detailName: String?, detailAge: String?, detailPhone: String?,positionData: Int) {
        AlertDialog.Builder(this)
            .setMessage("정말 삭제하시겠습니까?")
            .setCancelable(false)
            // 사용자가 '확인'을 선택하면 데이터를 삭제
            .setPositiveButton("확인") { _, _ ->
                deleteDataAndReturnResult(detailName, detailAge, detailPhone, positionData)
            }
            // 사용자가 '취소'를 선택하면 대화 상자를 닫음
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show() // 대화 상자를 보여줌
    }
}
