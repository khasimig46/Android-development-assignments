package com.pes.lib

fun main() {
    val data = "Hi! How are you?"

    // 1. Count vowels in the string
    var vowels = 0
    var spaces = 0

    for (ch in data) {
        when (ch.lowercaseChar()) {
            'a', 'e', 'i', 'o', 'u' -> vowels++
            ' ' -> spaces++
        }
    }

    println("Number of vowels: $vowels")
    println("Number of spaces: $spaces")
}
