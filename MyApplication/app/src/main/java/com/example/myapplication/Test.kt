package com.example.myapplication

class Test {
}

fun main() {
//    val str1 ="안녕 \n 하세요"
//    val str2 = """
//        반갑습니다 ~  하하
//        저희는 이제 코틀린을 배웁니다
//    """.trimIndent()
//    println("str1 : $str1")
//    println("str2 : $str2")

    //no는 매개변수의 이름이고, Int는 해당 매개변수의 타입
    //정수형(Int) 값을 반환
//    fun sum(no: Int) : Int {
//        var sum = 0
//        for(i in 1..no){
//            sum += i
//        }
//        return sum
//    }
//
//    val name: String = "홍길동"
//    println("name: $name, sum: ${sum(10)}, calc: ${1+2}")

//     2~9단 가로 2 x 1 = 2 .... 2 x 9 = 18
//     2~9단 2x1 =2 --- 9x9=81

//    for(i in 2..9){
//        for(j in 1 ..9){
//            print("${i} x ${j} = ${i * j}\t")
//        }
//        println()
//    }
//    println("----------------------------------------")
//        for (i in 1..9) {
//            for (j in 2..9) {
//                print("$j x $i = ${i * j}\t")
//            }
//            println()
//        }
//    println("----------------------------------------")
//    for (i in 2..9) {
//        for( j in 1..9){
//            print("${i} x ${j} = ${i * j}\t")
//        }
//        println()
//    }
    println("----------------------------------------")
    //1~100까지 짝수의 합
    var sum1: Int = 0
    for (i in 1..100) {
        if (i % 2 == 0) {
            sum1 += i
        }
    }
    println("1~100까지 짝수의 합 = ${sum1}")
    //1~100까지 홀수의 합
    var sum2: Int = 0
    for (i in 1..100) {
        if (i % 2 == 1) {
            sum2 += i
        }
    }
    println("1~100까지 홀수의 합 = ${sum2}")
    //[75, 100, 69, 99, 83] 배열의 평균 성적

    val scores = arrayOf(75, 100, 69, 99, 83)
    val average = scores.average()
    println("평균 성적: $average")

    for (score in scores) {
        var grade = when {
            score >= 90 -> "A"
            score >= 80 -> "B"
            score >= 70 -> "C"
            score >= 60 -> "D"
            else -> "F"
        }
        println("점수 : ${score} 학점 : ${grade}")
    }

    class User (name: String, count: Int){
        var name : String
        var count: Int
        init{
            this.name = name
            this.count = count
        }
        fun someFun() {
            println ("name: $name, count: $count")
        }
    }
    val user = User("dd", 3)
    user.someFun();
    }