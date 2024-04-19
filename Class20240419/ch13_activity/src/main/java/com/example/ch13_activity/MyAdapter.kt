package com.example.ch13_activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ch13_activity.databinding.ItemRecyclerviewBinding

// RecyclerView.ViewHolder를 상속하며, ItemRecyclerviewBinding을 매개변수로 받는 생성자를 가지고 있음
// 이 클래스는 RecyclerView의 각 아이템에 대한 뷰 홀더를 나타냄
class MyViewHolder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

// RecyclerView.Adapter를 상속하며, MutableList<String> 타입의 데이터를 받는 생성자를 가지고 있음
class MyAdapter(val datas: MutableList<String>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // getItemCount 메서드를 오버라이드하여 데이터 크기를 반환
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    // onCreateViewHolder 메서드를 오버라이드하여 뷰 홀더를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MyViewHolder(
            ItemRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    // RecyclerView에서 각 아이템에 대한 데이터를 설정하고, 클릭 이벤트를 처리하는 역할
    // holder를 통해 각 아이템의 View와 데이터를 관리
    // position: 현재 아이템의 위치를 나타내는 인덱스
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // holder를 MyViewHolder로 캐스팅하여 해당 ViewHolder의 바인딩을 가져옴
        val binding = (holder as MyViewHolder).binding
        // datas에서 현재 위치(position)에 해당하는 데이터를 가져와 item 변수에 할당
        val item: String = datas!![position]

        // ViewHolder의 바인딩을 통해 itemData라는 TextView에 item을 설정하여 해당 아이템의 데이터를 표시
        binding.itemData.text = item
        binding.itemRoot.setOnClickListener {
            // 클릭 시, DetailActivity로 이동하는 Intent를 생성하고, 선택된 아이템의 데이터를 "selectedItem"이라는 키로 담아 전송
            val intent = Intent(binding.root.context, DetailActivity::class.java)
            intent.putExtra("selectedItem", item)
            // 해당 Intent를 실행하여 DetailActivity를 시작
            binding.root.context.startActivity(intent)
        }
    }
}

//override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//    val binding=(holder as MyViewHolder).binding
//    binding.itemData.text= datas!![position]
//
//    binding.itemData.setOnClickListener{
//        val clickedData = datas!![position]
//        val intent = Intent(binding.root.context,DetailActivity::class.java)
//        intent.putExtra("data", clickedData)
//        binding.root.context.startActivity(intent)
//    }
//}