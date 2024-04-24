package com.example.todolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ItemTodoBinding
import com.example.todolist.db.TodoEntity

// 생성자를 통해 todoList(할 일 목록을 담고 있는 ArrayList)와 listener(아이템 긴 클릭 이벤트를 처리할 리스너)를 초기화
// 데이터와 뷰를 연결하고, 화면에 표시할 아이템의 개수 등을 관리할 수 있게 됨
class TodoRecyclerViewAdapter(
    private val todoList: ArrayList<TodoEntity>,
    private val listener: OnItemLongClickListener
) :
    RecyclerView.Adapter<TodoRecyclerViewAdapter.MyViewHolder>() {

    // MyViewHolder는 RecyclerView.ViewHolder를 상속하고 있는 내부 클래스(inner class)
    // RecyclerView는 화면에 표시되는 대량의 데이터를 효율적으로 관리하기 위한 위젯이며, 이를 위해 ViewHolder를 사용하여 화면에 보이는 아이템의 뷰를 재사용하고 데이터를 관리함
    // binding.root를 통해 해당 뷰 홀더가 관리하는 아이템의 루트 뷰에 접근할 수 있고, 이를 통해 해당 뷰와 관련된 작업을 수행할 수 있음
    inner class MyViewHolder(binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        val tv_importance = binding.tvImportance
        val tv_title = binding.tvTitle
        val root = binding.root
    }

    // onCreateViewHolder 메서드는 RecyclerView가 새로운 뷰 홀더를 만들어야 할 때 호출됨
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemTodoBinding =
        // LayoutInflater는 XML 레이아웃 파일을 실제 뷰 객체로 인스턴스화하는 데 사용됨
            // parent는 새로운 뷰를 추가할 부모 ViewGroup를 의미, false는 인플레이트된 뷰를 루트 ViewGroup에 즉시 추가하지 않도록 지정
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // onCreateViewHolder 메서드에서 뷰 홀더를 생성하고 반환
        return MyViewHolder(binding)
    }

    // onBindViewHolder 메서드는 RecyclerView에서 아이템의 데이터를 뷰 홀더에 바인딩하여 화면에 표시하는 역할
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // todoList는 RecyclerView에 표시될 데이터를 담고 있는 리스트이며, position은 현재 바인딩할 아이템의 위치를 가리킴
        val todoData = todoList[position]

        // todoData의 중요도에 따라 뷰 홀더의 tv_importance의 배경색을 설정
        when (todoData.importance) {
            "1" -> {
                holder.tv_importance.setBackgroundResource(R.color.red)
            }

            "2" -> {
                holder.tv_importance.setBackgroundResource(R.color.yellow)
            }

            "3" -> {
                holder.tv_importance.setBackgroundResource(R.color.green)
            }
        }
        // 해당 뷰 홀더의 tv_importance에는 todoData의 중요도를 문자열로 변환하여 설정
        holder.tv_importance.text = todoData.importance
        // 해당 뷰 홀더의 tv_title에는 todoData의 제목을 설정
        holder.tv_title.text = todoData.todoContent

        holder.root.setOnLongClickListener {
            // 롱 클릭 이벤트가 발생하면 listener를 통해 해당 위치(position)를 전달하여 처리
            listener.onLongClick(position)
            // false 라고 하면, 다른 클릭 이벤트들도 실행이 됨, true 라고 하면 오직 onLongClick 만 실행이 됨
            false
        }
    }

    // RecyclerView의 아이템 개수를 반환하는 메서드
    override fun getItemCount(): Int {
        return todoList.size
    }

}