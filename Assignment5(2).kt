package com.pes.lib

// Functional programming, collections
fun main() {
    val phoneNumbers = listOf(
        "1234567890",
        "123",
        "",
        "0987654321"
    )

    // 1. Using lambda, filter valid phone numbers - 10 digits
    val validPhoneNumbers = phoneNumbers.filter { it.length == 10 }

    println("Valid phone numbers:")
    validPhoneNumbers.forEach {
        println(it)
    }

    // 2. Using lambda, print all phone numbers,
    // if invalid - print 'NAN'
    phoneNumbers
        .map { phoneNumber ->
            if (phoneNumber.length == 10) phoneNumber else "NAN"
        }
        .forEach {
            println(it)
        }
}
