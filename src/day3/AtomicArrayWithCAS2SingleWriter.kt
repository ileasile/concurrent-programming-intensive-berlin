package day3

import day3.AtomicArrayWithCAS2SingleWriter.Status.*
import kotlinx.atomicfu.*

// This implementation never stores `null` values.
class AtomicArrayWithCAS2SingleWriter<E : Any>(size: Int, initialValue: E) {
    private val array = atomicArrayOfNulls<Any?>(size)

    init {
        // Fill array with the initial value.
        for (i in 0 until size) {
            array[i].value = initialValue
        }
    }

    operator fun get(index: Int): E {
        return when(val value =  array[index].value) {
            is AtomicArrayWithCAS2SingleWriter<*>.CAS2Descriptor -> {
                if (index == value.index1) {
                    if (value.status.value == SUCCESS) {
                        value.update1
                    } else {
                        value.expected1
                    }
                } else {
                    if (value.status.value == SUCCESS) {
                        value.update2
                    } else {
                        value.expected2
                    }
                }
            }
            else -> value
        } as E
    }

    fun cas2(
        index1: Int, expected1: E, update1: E,
        index2: Int, expected2: E, update2: E
    ): Boolean {
        require(index1 != index2) { "The indices should be different" }

        val descriptor = CAS2Descriptor(index1, expected1, update1, index2, expected2, update2)
        if (!array[index1].compareAndSet(expected1, descriptor)) return false
        if (!array[index2].compareAndSet(expected2, descriptor)) return false
        descriptor.apply()
        return true
    }

    inner class CAS2Descriptor(
        val index1: Int,
        val expected1: E,
        val update1: E,
        val index2: Int,
        val expected2: E,
        val update2: E
    ) {
        val status = atomic(UNDECIDED)

        fun apply() {

//            if (!array[index1].compareAndSet(expected1, this)) {
//                status.value = FAILED
//            }
//            if (!array[index2].compareAndSet(expected2, this)) {
//                status.value = FAILED
//            }
            status.value = SUCCESS

            array[index1].compareAndSet(this, update1)
            array[index2].compareAndSet(this, update2)
        }
    }

    enum class Status {
        UNDECIDED, SUCCESS, FAILED
    }
}