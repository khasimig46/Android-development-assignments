package com.pes.lib

fun convert(names: Array<String?>) {
    for (name in names) {
        // Explicit null check
        if (name != null) {
            println("Explicit null check: ${name.uppercase()}")
        }

        // Safe call operator
        println("Safe call: ${name?.uppercase()}")

        // Elvis operator
        println("Elvis operator: ${name?.uppercase() ?: "UNKNOWN"}")

        // Assertion operator
        if (name != null) {
            println("Assertion operator: ${name!!.uppercase()}")
        }
    }
}

fun main() {
    convert(arrayOf("john", null, "merry", "robert"))
}
