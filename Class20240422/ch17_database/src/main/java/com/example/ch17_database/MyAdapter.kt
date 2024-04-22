package com.example.ch17_database

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.ch17_database.databinding.ItemRecyclerviewBinding


// RecyclerView.ViewHolder를 상속하며, ItemRecyclerviewBinding을 매개변수로 받는 생성자를 가지고 있음
// 이 클래스는 RecyclerView의 각 아이템에 대한 뷰 홀더를 나타냄
class MyViewHolder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

// RecyclerView.Adapter를 상속하며, MutableList<String> 타입의 데이터를 받는 생성자를 가지고 있음
class MyAdapter(val datas: MutableList<String>?, private val detailActivityLauncher: ActivityResultLauncher<Intent>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        // 특정 위치의 뷰 홀더를 캐스팅하여 해당 뷰 홀더에 바인딩된 데이터바인딩을 가져오는 역할
        val binding = (holder as MyViewHolder).binding
        // 데이터바인딩을 통해 특정 위치(position)에 있는 데이터를 해당하는 텍스트 뷰에 설정하는 역할
        binding.itemData.text= datas!![position]

        binding.itemData.setOnClickListener {
            // 특정 위치의 데이터를 가져와서 clickedData 변수에 저장
            val clickedData = datas!![position]
            // binding.root의 컨텍스트에서 DetailActivity로 이동하기 위한 Intent를 생성
            val intent = Intent(binding.root.context, DetailActivity::class.java).apply {
                putExtra("updatedItem", clickedData)
                putExtra("itemPosition", position)
            }
            // 여기서는 startActivity 대신 detailActivityLauncher를 사용
            detailActivityLauncher.launch(intent)
        }
    }
}
