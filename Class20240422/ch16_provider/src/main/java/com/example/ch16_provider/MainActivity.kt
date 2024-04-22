package com.example.ch16_provider

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.ch16_provider.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ActivityResultContracts.StartActivityForResult를 사용하여 갤러리 런처 액티비티를 실행하고,
        // 결과를 처리하기 위한 requestGalleryLancher라는 변수를 생성
        val requestGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            try {
                // calculateInSampleSize 함수를 호출하여 이미지의 축소 비율(calRatio)을 계산
                val calRatio = calculateInSampleSize(
                    // it.data!!.data!!를 통해 선택된 이미지의 URI를 전달
                    it.data!!.data!!,
                    // resources.getDimensionPixelSize를 사용하여 이미지의 크기를 지정
                    resources.getDimensionPixelSize(R.dimen.imgSize),
                    resources.getDimensionPixelSize(R.dimen.imgSize)
                )
                // BitmapFactory.Options 객체를 생성하고, 이미지의 샘플링 크기를 이전에 계산된 calRatio 값으로 설정
                // 이를 통해 이미지를 적절히 축소하여 로드할 수 있음
                val option = BitmapFactory.Options()
                option.inSampleSize = calRatio

                // contentResolver를 사용하여 선택된 이미지의 URI를 통해 inputStream을 열고,
                var inputStream = contentResolver.openInputStream(it.data!!.data!!)
                // BitmapFactory.decodeStream을 사용하여 해당 inputStream에서 비트맵을 디코딩
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                inputStream!!.close()
                inputStream = null

                // 만약 bitmap이 null이 아니면 binding.userImageView에 이미지를 설정하고, null이면 "bitmap null"이라는 로그를 출력
                bitmap?.let {
                    binding.userImageView.setImageBitmap(bitmap)
                } ?: let {
                    Log.d("kkang", "bitmap null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 클릭 시, 외부 저장소의 이미지를 선택할 수 있는 액션을 지정한 인텐트를 생성하고 requestGalleryLauncher를 통해 실행
        binding.galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            requestGalleryLauncher.launch(intent)
        }

        // 카메라 앱을 실행하여 파일을 생성하기 위한 ActivityResultContracts.StartActivityForResult()를 사용하여
        // 결과를 처리할 수 있는 ActivityResultLauncher를 등록
        val requestCameraFileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // calculateInSampleSize 함수를 사용하여 파일 경로(filePath)를 통해 이미지의 축소 비율(calRatio)을 계산
            val calRatio = calculateInSampleSize(
                Uri.fromFile(File(filePath)),
                // resources.getDimensionPixelSize를 사용하여 이미지의 크기를 지정
                resources.getDimensionPixelSize(R.dimen.imgSize),
                resources.getDimensionPixelSize(R.dimen.imgSize)
            )
            // BitmapFactory.Options 객체를 생성하고, 이미지의 샘플링 크기를 이전에 계산된 calRatio 값으로 설정
            val option = BitmapFactory.Options()
            option.inSampleSize = calRatio
            // 해당 옵션을 사용하여 파일로부터 비트맵을 디코딩
            val bitmap = BitmapFactory.decodeFile(filePath, option)
            // binding.userImageView에 이미지를 설정
            bitmap?.let {
                binding.userImageView.setImageBitmap(bitmap)
            }
        }


        binding.cameraButton.setOnClickListener {
            val timeStamp: String =
                // SimpleDateFormat을 사용하여 현재 날짜와 시간을 "yyyyMMdd_HHmmss" 형식으로 포맷
                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            // getExternalFilesDir을 사용하여 사진을 저장할 디렉토리를 가져오고 있음
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            // "JPEG_타임스탬프_"로 시작하고 확장자가 .jpg인 임시 파일을 storageDir 디렉토리에 생성
            // createTempFile 함수를 사용하여 임시 파일을 생성
            val file = File.createTempFile(
                "JPEG_${timeStamp}_", ".jpg", storageDir
            )
            // file의 절대 경로를 filePath에 저장
            filePath = file.absolutePath
            // FileProvider.getUriForFile 함수를 사용하여 file을 나타내는 content URI를 가져옴
            val photoURI: Uri = FileProvider.getUriForFile(
                this, "com.example.ch16_provider.fileprovider", file
            )
            // 카메라 앱을 실행하기 위한 인텐트를 생성
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // EXTRA_OUTPUT extra를 통해 photoURI에 지정된 파일에 캡처된 이미지를 저장하도록 설정
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            // requestCameraFileLauncher를 통해 실행
            requestCameraFileLauncher.launch(intent)
        }
    }

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
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                // 샘플링 크기를 2의 지수로 증가시킴으로써 이미지를 효과적으로 축소
                inSampleSize *= 2
            }
        }
        // 이미지의 최종 샘플링 크기를 반환
        return inSampleSize
    }
}