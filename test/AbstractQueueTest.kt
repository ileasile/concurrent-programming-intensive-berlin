@file:Suppress("unused")

import day1.*
import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.paramgen.*

@Param(name = "element", gen = IntGen::class, conf = "0:3")
abstract class AbstractQueueTest(
    private val queue: Queue<Int>,
    checkObstructionFreedom: Boolean = true
) : TestBase(
    sequentialSpecification = IntQueueSequential::class,
    checkObstructionFreedom = checkObstructionFreedom
) {
    @Operation
    fun enqueue(@Param(name = "element") element: Int) = queue.enqueue(element)

    @Operation
    fun dequeue() = queue.dequeue()
}

class IntQueueSequential {
    private val q = ArrayList<Int>()

    fun enqueue(element: Int) {
        q.add(element)
    }

    fun dequeue() = q.removeFirstOrNull()
    fun remove(element: Int) = q.remove(element)
}
