package com.example.ch12_material

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ch12_material.databinding.ItemRecyclerviewBinding

// RecyclerView에서 사용될 뷰 홀더를 정의하는 클래스인 MyViewHolder
// RecyclerView.ViewHolder를 상속받고, 생성자를 통해 ItemRecyclerviewBinding을 매개변수로 받음
// 이를 통해 해당 뷰 홀더는 ItemRecyclerviewBinding을 가지고 있으며, binding.root를 통해 해당 뷰 홀더의 루트 뷰에 접근할 수 있음
class MyViewHolder(val binding: ItemRecyclerviewBinding): RecyclerView.ViewHolder(binding.root)

// RecyclerView.Adapter를 상속받으며, 생성자를 통해 MutableList<String> 타입의 datas를 받음
class MyAdapter(val datas: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    // getItemCount 메서드를 오버라이드하여 데이터의 개수를 반환
    override fun getItemCount(): Int{
        // datas 리스트의 크기를 반환하여 RecyclerView에 표시할 아이템의 개수를 지정
        return datas.size
    }

    // RecyclerView에 새로운 뷰 홀더를 생성하고, 해당 뷰 홀더를 RecyclerView에 바인딩하는 역할
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    // RecyclerView의 각 아이템에 데이터를 바인딩하는 역할 (바인딩(binding)은 데이터와 뷰를 연결하는 것을 의미)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as MyViewHolder).binding
        binding.itemData.text= datas[position]
    }
}

// RecyclerView.ItemDecoration을 상속받으며, getItemOffsets 메서드를 오버라이드하여 아이템의 간격을 설정
class MyDecoration(val context: Context): RecyclerView.ItemDecoration() {

    // 각 아이템의 위치에 따라 마진을 설정하는 역할
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        // index를 통해 아이템의 위치를 파악하고, 그에 따라 마진을 설정
        val index = parent.getChildAdapterPosition(view) + 1

        if (index % 3 == 0) //left, top, right, bottom
            outRect.set(10, 10, 10, 60)
        else
            outRect.set(10, 10, 10, 0)

        // 아이템의 배경색을 변경하고, ViewCompat.setElevation을 통해 아이템에 그림자를 추가
        view.setBackgroundColor(Color.parseColor("#28A0FF"))
        ViewCompat.setElevation(view, 20.0f)
    }
}