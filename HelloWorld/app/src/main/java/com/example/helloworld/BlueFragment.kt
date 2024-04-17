package com.example.helloworld

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

//  Fragment는 액티비티 내에서 UI나 동작을 표현하는 작은 모듈이며, 화면의 일부를 구성
class BlueFragment : Fragment() {
    // onCreateView 메서드는 Fragment가 처음으로 그려질 때 호출되는 메서드
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        // 인자로 전달된 inflater를 사용하여 R.layout.fragment_blue에 정의된 XML 레이아웃을 실제 뷰로 확장하여 반환
    ): View? {
        // XML 레이아웃 파일(R.layout.fragment_blue)을 실제 뷰로 확장하는 역할
        // container는 부모 뷰그룹이며, false는 인플레이션 후 뷰를 즉시 부모에 붙이지 않음을 의미
        return inflater.inflate(R.layout.fragment_blue, container, false)
    }
}