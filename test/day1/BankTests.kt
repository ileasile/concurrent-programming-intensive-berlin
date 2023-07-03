@file:Suppress("unused")

package day1

import TestBase
import day1.Bank.Companion.MAX_AMOUNT
import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.paramgen.*

class FineGrainedBankTest : AbstractBankTest(FineGrainedBank(accountsNumber = ACCOUNTS_NUMBER))
class CoarseGrainedBankTest : AbstractBankTest(CoarseGrainedBank(accountsNumber = ACCOUNTS_NUMBER))

@Param(name = "id", gen = IntGen::class, conf = "0:${ACCOUNTS_NUMBER - 1}")
@Param(name = "amount", gen = LongGen::class, conf = "1:10")
abstract class AbstractBankTest(
    private val bank: Bank
) : TestBase(
    sequentialSpecification = BankSequential::class,
    checkObstructionFreedom = false
), Bank {

    @Operation
    override fun getAmount(@Param(name = "id") id: Int): Long =
        bank.getAmount(id)

    @Operation
    override fun deposit(@Param(name = "id") id: Int, @Param(name = "amount") amount: Long): Long =
        bank.deposit(id, amount)

    @Operation
    override fun withdraw(@Param(name = "id") id: Int, @Param(name = "amount") amount: Long): Long =
        bank.withdraw(id, amount)

    @Operation
    override fun transfer(@Param(name = "id") fromId: Int, @Param(name = "id") toId: Int, @Param(name = "amount") amount: Long) {
        if (fromId != toId) bank.transfer(fromId, toId, amount)
    }
}

class BankSequential {
    private val accounts: Array<Account> = Array(ACCOUNTS_NUMBER) { Account() }

    fun getAmount(index: Int): Long {
        return accounts[index].amount
    }

    fun deposit(index: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val account = accounts[index]
        check(!(amount > MAX_AMOUNT || account.amount + amount > MAX_AMOUNT)) { "Overflow" }
        account.amount += amount
        return account.amount
    }

    fun withdraw(index: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val account = accounts[index]
        check(account.amount - amount >= 0) { "Underflow" }
        account.amount -= amount
        return account.amount
    }

    fun transfer(fromIndex: Int, toIndex: Int, amount: Long) {
        require(amount > 0) { "Invalid amount: $amount" }
        if (fromIndex == toIndex) return
        val from = accounts[fromIndex]
        val to = accounts[toIndex]
        check(amount <= from.amount) { "Underflow" }
        check(!(amount > MAX_AMOUNT || to.amount + amount > MAX_AMOUNT)) { "Overflow" }
        from.amount -= amount
        to.amount += amount
    }

    class Account {
        var amount: Long = 0
    }
}

private const val ACCOUNTS_NUMBER = 3