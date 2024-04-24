package com.example.todolist

// RecyclerView의 아이템을 길게 눌렀을 때의 동작을 정의하기 위한 것
interface OnItemLongClickListener {
    fun onLongClick(position : Int)
}