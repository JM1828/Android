package com.junmo.firebasecrud

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.junmo.firebasecrud.databinding.ItemTodoBinding

class UserAdapter(
    private val context: Context,
    private var userList: ArrayList<User>
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_importance = itemView.findViewById<TextView>(R.id.tv_importance)
        val tv_title = itemView.findViewById<TextView>(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // LayoutInflater는 XML 레이아웃 파일을 실제 뷰 객체로 인스턴스화하는 데 사용됨
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_todo, parent, false)
        // onCreateViewHolder 메서드에서 뷰 홀더를 생성하고 반환
        return UserViewHolder(view)
    }
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user: User = userList[position]

        when (user.importance) {
            1 -> {
                holder.tv_importance.setBackgroundResource(R.color.red)
            }
            2 -> {
                holder.tv_importance.setBackgroundResource(R.color.yellow)
            }
            3 -> {
                holder.tv_importance.setBackgroundResource(R.color.green)
            }
        }

        // 해당 뷰 홀더의 tv_importance에는 todoData의 중요도를 문자열로 변환하여 설정
        holder.tv_importance.text = user.importance.toString()
        // 해당 뷰 홀더의 tv_title에는 todoData의 제목을 설정
        holder.tv_title.text = user.title
    }

    // RecyclerView의 아이템 개수를 반환하는 메서드
    override fun getItemCount(): Int {
        return userList.size
    }

}


