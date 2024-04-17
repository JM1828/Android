package com.example.recyclerviewex

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.recyclerviewex.databinding.ActivityMainBinding
import com.example.recyclerviewex.databinding.ItemMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datas = mutableListOf<String>()
        for (i in 1..10) {
            datas.add("아이템 ${i}")
        }

        // 레이아웃 매니저 등록
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        // 어댑터 등록
        binding.recyclerView.adapter = MyAdapter(datas)

//        binding.recyclerView.addItemDecoration(
//            DividerItemDecoration(
//                this,
//                LinearLayoutManager.VERTICAL
//            )
//        )
        binding.recyclerView.addItemDecoration(MyDecoration(this))
    }
}

// 뷰 홀더 준비
class MyViewHolder(val binding: ItemMainBinding) : ViewHolder(binding.root)

// 어댑터 준비
class MyAdapter(val datas: MutableList<String>) : RecyclerView.Adapter<ViewHolder>() {

    // 항목의 개수를 판단하려고 자동으로 호출됨
    override fun getItemCount(): Int = datas.size

    // 항목의 뷰를 가지는 뷰 홀더를 준비하려고 자동으로 호출됨
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        MyViewHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    // 뷰 홀더의 뷰에 데이터를 출력하려고 자동으로 호출됨
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("test", "onBindViewHolder: ${position}")
        val binding = (holder as MyViewHolder).binding

        // 뷰에 데이터 출력
        binding.itemData.text = datas[position]

        // 뷰에 데이터 추가
        binding.itemRoot.setOnClickListener {
            Log.d("test", "itemRoot 클릭: ${position}")
        }
    }
}

// ItemDecoration 을 상속받아서 꾸미는 class
class MyDecoration(val context: Context) : RecyclerView.ItemDecoration() {
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        // 뷰 크기 계산
        val width = parent.width
        val height = parent.height
        // 이미지 크기 계산
        val dr: Drawable? = ResourcesCompat.getDrawable(context.resources, R.drawable.stadium, null)
        val drWidth = dr?.intrinsicWidth
        val drHeight = dr?.intrinsicHeight
        // 이미지가 그려질 위치 계산
        val left = width / 2 - drWidth?.div(2) as Int
        val top = height / 2 - drHeight?.div(2) as Int
        c.drawBitmap(
            BitmapFactory.decodeResource(context.getResources(), R.drawable.stadium),
            left.toFloat(),
            top.toFloat(),
            null
        )
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val index = parent.getChildAdapterPosition(view) + 1
        if (index % 3 == 0) {
            outRect.set(10, 10, 10, 60)
        } else {
            outRect.set(10, 10, 10, 0)
        }
        view.setBackgroundColor(Color.GREEN)
        ViewCompat.setElevation(view, 20.0f)
    }
}