package com.pes.lib

// Exception handling for converting String to Int
fun getNumber(data: String): Int {
    return try {
        data.toInt()
    } catch (e: NumberFormatException) {
        println("Invalid number: $data")
        -1
    }
}

fun main() {
    println(getNumber("12"))
    println(getNumber("a1as"))
}
