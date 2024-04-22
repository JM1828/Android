package com.example.contractex

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contractex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // ActivityResultLauncher는 Android에서 액티비티나 프래그먼트 간에 결과를 처리하기 위한 API
    lateinit var requestContactsLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 주소록 앱을 실행한 후 callback 처리
        requestContactsLauncher =
                // 새로운 액티비티를 시작하고 해당 액티비티의 결과를 처리하기 위해 ActivityResultLauncher를 등록하는 과정
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // 이전에 실행된 액티비티의 결과가 OK인지 확인
                if (it.resultCode == RESULT_OK) {
                    // contentResolver를 사용하여 주소록 데이터에 대한 쿼리를 수행
                    val cursor = contentResolver.query(
                        // it 변수를 통해 결과 Intent에 접근  it의 data 속성이 null이 아닌지, 그리고 data의 data 속성이 null이 아님을 확신
                        it!!.data!!.data!!,
                        // 연락처의 표시 이름과 전화번호를 가져올 수 있도록 설정
                        arrayOf<String>(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                            // 필터링 조건, 조건을 충족시키는 값을 대체하는데 사용되는 문자열 배열, 결과를 정렬하는 데 사용되는 정렬 순서를 나타냄
                            // 모든 연락처를 가져오기 위해 null로 설정
                        ), null, null, null
                    )
                    // 커서가 첫 번째 항목을 가리킬 때, 즉 주소록에 데이터가 있는 경우에만 다음 작업을 수행
                    if (cursor!!.moveToFirst()) {
                        // 커서의 현재 위치에서 첫 번째 및 두 번째 열의 값을 가져와서 name 및 phone 변수에 저장
                        val name = cursor?.getString(0)
                        val phone = cursor?.getString(1)
                        // 가져온 이름과 전화번호를 화면에 출력하기 위해 binding.textView.text에 문자열 템플릿을 사용하여 설정
                        binding.textView.text = "name : ${name}, phone : ${phone}"
                    }
                }
            }

        binding.button.setOnClickListener {
                // 주소록에서 연락처를 선택하기 위한 암시적 인텐트를 생성
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                // 주소록에서 연락처를 선택하는 작업을 요청하고, 선택된 연락처에 대한 결과를 처리할 수 있음
                requestContactsLauncher.launch(intent)
            }
        }
}
