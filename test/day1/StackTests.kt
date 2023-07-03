@file:Suppress("unused")

package day1

import TestBase
import org.jetbrains.kotlinx.lincheck.annotations.*

class TreiberStackTest : StackTest(TreiberStack())
class TreiberStackWithEliminationTest : StackTest(TreiberStackWithElimination())

abstract class StackTest(
    private val stack: Stack<Int>
) : TestBase(sequentialSpecification = IntStackSequential::class) {
    @Operation
    fun push(element: Int) = stack.push(element)

    @Operation
    fun pop() = stack.pop()
}

class IntStackSequential {
    private val q = ArrayDeque<Int>()

    fun push(element: Int) {
        q.addLast(element)
    }

    fun pop(): Int? = q.removeLastOrNull()
}