package com.example.galleryex

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.galleryex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 적정한 비율로 이미지 크기 줄이기
    // fileUri로 전달된 이미지 파일의 URI와 요청된 폭(reqWidth) 및 높이(reqHeight)를 기반으로 이미지의 샘플링 크기를 계산
    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
        // BitmapFactory.Options 객체를 생성
        val option = BitmapFactory.Options()
        // 이미지를 실제로 디코딩하지 않고 이미지의 크기 정보만을 읽어오도록 설정
        option.inJustDecodeBounds = true

        try {
            // contentResolver를 사용하여 fileUri로 지정된 파일의 입력 스트림을 열고 있음
            var inputStream = contentResolver.openInputStream(fileUri)
            // inputStream에서 이미지를 디코딩하고, option을 통해 이미지의 크기 정보를 읽어오고 있음
            BitmapFactory.decodeStream(inputStream, null, option)
            // inputStream을 닫고, null로 설정하여 자원을 정리
            inputStream!!.close()
            inputStream = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // BitmapFactory.Options 객체에 저장된 이미지의 너비와 높이를 가져옴
        val (height: Int, width: Int) = option.run { outHeight to outWidth }
        // 이미지의 샘플링 크기를 1로 초기화
        var inSampleSize = 1

        // inSampleSize 비율 계산
        // 이미지의 높이와 너비가 요청된 높이(reqHeight) 및 너비(reqWidth)보다 큰지를 확인
        if (height > reqHeight || width > reqWidth) {
            // 이미지의 높이와 너비의 절반 값을 halfHeight와 halfWidth 변수에 각각 저장
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            // halfHeight와 halfWidth를 현재의 이미지 샘플링 크기(inSampleSize)로 나눈 값이 요청된 높이(reqHeight)와 너비(reqWidth)보다 크거나 같은지
            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth
            ) {
                // 샘플링 크기를 2의 지수로 증가시킴으로써 이미지를 효과적으로 축소
                inSampleSize *= 2
            }
        }
        // 이미지의 최종 샘플링 크기를 반환
        return inSampleSize
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // calculateInSampleSize() 함수를 이용해 이미지를 불러옴
        // ActivityResultContracts.StartActivityForResult를 사용하여 갤러리 런처 액티비티를 실행하고,
        // 결과를 처리하기 위한 requestGalleryLancher라는 변수를 생성
        val requestGalleryLancher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        {
            try {
                // inSampleSize 비율 계산, 지정
                // calculateInSampleSize 함수를 호출하여 이미지의 축소 비율(calRatio)을 계산
                val calRatio = calculateInSampleSize(
                    // it.data!!.data!!를 통해 선택된 이미지의 URI를 전달
                    it.data!!.data!!,
                    // resources.getDimensionPixelOffset을 사용하여 이미지의 크기를 지정
                    resources.getDimensionPixelOffset(R.dimen.imgSize),
                    resources.getDimensionPixelOffset(R.dimen.imgSize)
                )
                // BitmapFactory.Options 객체를 생성하고, 이미지의 샘플링 크기를 이전에 계산된 calRatio 값으로 설정
                val option = BitmapFactory.Options()
                option.inSampleSize = calRatio

                // 이미지 불러옴
                // contentResolver를 사용하여 선택된 이미지의 URI를 통해 inputStream을 열고 있음
                var inputStream = contentResolver.openInputStream(it.data!!.data!!)
                // inputStream을 사용하여 BitmapFactory를 통해 이미지를 디코딩하고, 그 결과를 bitmap에 저장
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                // 이미지를 디코딩한 후에는 inputStream을 닫음
                inputStream!!.close()
                // null로 설정하여 자원을 정리
                inputStream = null
                // bitmap이 null이 아니면 binding.gallertResult에 이미지를 설정하고, null이면 "bitmap null!!!"이라는 로그를 출력
                bitmap?.let {
                    binding.gallertResult.setImageBitmap(bitmap)
                } ?: let {
                    Log.d("test", "bitmap null!!!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 갤러리 앱 버튼 클릭 시
        binding.button.setOnClickListener {
            // 갤러리에서 이미지를 선택하기 위한 인텐트를 생성하고, 이를 requestGalleryLancher를 통해 실행
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            requestGalleryLancher.launch(intent)
        }
    }
}