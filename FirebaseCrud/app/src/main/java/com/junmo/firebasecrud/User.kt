package com.junmo.firebasecrud

data class User(
    var userKey: String,
    var title: String,
    var importance: Int
) {
    constructor(): this("","",0)
}
