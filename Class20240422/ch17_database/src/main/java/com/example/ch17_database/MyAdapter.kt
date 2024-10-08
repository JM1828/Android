package com.example.ch17_database

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.ch17_database.databinding.ItemRecyclerviewBinding


// 각 RecyclerView 아이템의 뷰를 관리하는 ViewHolder 클래스
class MyViewHolder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

// RecyclerView의 데이터와 아이템 뷰를 연결하는 Adapter 클래스
class MyAdapter(
    private val datas: MutableList<String>?,
    private val detailActivityLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<MyViewHolder>() {

    // RecyclerView에 표시할 아이템의 총 개수를 반환
    override fun getItemCount(): Int = datas?.size ?: 0

    // 새로운 뷰 홀더 객체를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(
            ItemRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    // 각 아이템에 대한 데이터를 뷰 홀더에 바인딩
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // 클릭된 데이터와 위치 정보를 Intent에 담아 DetailActivity를 시작
        val clickedData = datas!![position].split(", ")  // 클릭된 데이터를 쉼표 기준으로 분리하여 리스트로 만듦
        // 현재 위치의 데이터를 가져와서 텍스트 뷰에 설정
        holder.binding.itemName.text = clickedData[0]
        holder.binding.itemAge.text = clickedData[1]
        holder.binding.itemPhone.text = clickedData[2]


        // 아이템 클릭 이벤트 처리
        holder.binding.itemRoot.setOnClickListener {
            // 클릭된 데이터와 위치 정보를 Intent에 담아 DetailActivity를 시작
            val intent =
                Intent(holder.binding.root.context, DetailActivity::class.java).apply {
                    putExtra("name", clickedData[0])
                    putExtra("age", clickedData[1])
                    putExtra("phone", clickedData[2])
                    putExtra("position", position)
                }
            // ActivityResultLauncher를 사용하여 DetailActivity를 시작하고 결과를 받음
            detailActivityLauncher.launch(intent)
        }
    }
}
