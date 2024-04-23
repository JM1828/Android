package com.example.todolist.db

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.TodoUpdateActivity
import com.example.todolist.databinding.ItemTodoBinding

class TodoAdapter(
    private val context: Context, private var userList: ArrayList<Todo>
) : RecyclerView.Adapter<TodoAdapter.MyViewHolder>() {

    // MyViewHolder는 RecyclerView.ViewHolder를 상속하고 있는 내부 클래스(inner class)
    // RecyclerView는 화면에 표시되는 대량의 데이터를 효율적으로 관리하기 위한 위젯이며, 이를 위해 ViewHolder를 사용하여 화면에 보이는 아이템의 뷰를 재사용하고 데이터를 관리함
    // binding.root를 통해 해당 뷰 홀더가 관리하는 아이템의 루트 뷰에 접근할 수 있고, 이를 통해 해당 뷰와 관련된 작업을 수행할 수 있음
    inner class MyViewHolder(binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        val tv_importance = binding.tvImportance
        val tv_title = binding.tvTitle
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val todo: Todo = userList[position]

        when (todo.importance) {
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
        holder.tv_importance.text = todo.importance
        // 해당 뷰 홀더의 tv_title에는 todoData의 제목을 설정
        holder.tv_title.text = todo.title


        holder.itemView.setOnClickListener {

            // Todo 수정 화면으로 이동
            val intent = Intent(context, TodoUpdateActivity::class.java)
            intent.putExtra("id", todo.id)
            intent.putExtra("title", todo.title)
            intent.putExtra("importance", todo.importance)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

    // RecyclerView의 아이템 개수를 반환하는 메서드
    override fun getItemCount(): Int {
        return userList.size
    }
}



