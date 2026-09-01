package com.pes.lib

/*
 * Create class BankAccount
 *     - accName - Account Holder Name
 *     - accNumber - Int - account number - readOnly
 *     - balance - Int - always initialized to 0
 *     - displayInfo() - print all properties
 */
open class BankAccount(
    val accName: String,
    val accNumber: Int,
    protected var balance: Int = 0
) {
    open fun displayInfo() {
        println("Account Holder Name: $accName")
        println("Account Number: $accNumber")
        println("Balance: $balance")
    }
}

/*
 * SavingAccount inherited from BankAccount
 *     - interest rate - Float
 *     - credit(amount) - increment balance
 *     - debit(amount) - decrement balance
 *     - override displayInfo - prints interest rate as well
 */
class SavingAccount(
    accName: String,
    accNumber: Int,
    val interestRate: Float
) : BankAccount(accName, accNumber) {

    fun credit(amount: Int) {
        balance += amount
    }

    fun debit(amount: Int) {
        balance -= amount
    }

    override fun displayInfo() {
        super.displayInfo()
        println("Interest Rate: $interestRate")
    }
}

fun main() {
    val account = SavingAccount(
        accName = "John",
        accNumber = 1001,
        interestRate = 4.5f
    )

    account.displayInfo()

    println("\nAfter credit:")
    account.credit(5000)
    account.displayInfo()

    println("\nAfter debit:")
    account.debit(1500)
    account.displayInfo()
}
