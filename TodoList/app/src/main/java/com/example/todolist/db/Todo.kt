package com.example.todolist.db

data class Todo(
    var id: String,
    var title: String,
    var importance: String
) {
    constructor(): this("","", "")
}
