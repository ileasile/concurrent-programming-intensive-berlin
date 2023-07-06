package day3

import day3.AtomicArrayWithCAS2Simplified.Status.*
import kotlinx.atomicfu.*


// This implementation never stores `null` values.
class AtomicArrayWithCAS2Simplified<E : Any>(size: Int, initialValue: E) {
    private val array = atomicArrayOfNulls<Any?>(size)

    init {
        // Fill array with the initial value.
        for (i in 0 until size) {
            array[i].value = initialValue
        }
    }

    fun get(index: Int): E {
        return when(val value =  array[index].value) {
            is AtomicArrayWithCAS2Simplified<*>.CAS2Descriptor -> {
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

        if (index1 < index2) return cas2(index2, expected2, update2, index1, expected1, update1)

        val descriptor = CAS2Descriptor(index1, expected1, update1, index2, expected2, update2)

        descriptor.apply()
        return descriptor.status.value == SUCCESS
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

        private fun tryInstallDescriptor(): Boolean {
            return tryInstallDescriptor(index1, expected1) && tryInstallDescriptor(index2, expected2)
        }

        private fun tryInstallDescriptor(index: Int, expected: E): Boolean {
            while (true) {
                val value = array[index].value
                when {
                    value === this -> return true
                    value is AtomicArrayWithCAS2Simplified<*>.CAS2Descriptor -> value.apply()
                    value === expected -> {
                        if (array[index].compareAndSet(expected, this)) return true
                    }
                    else -> {
                        return false
                    }
                }
            }
        }

        private fun updateValues() {
            if (status.value == SUCCESS) {
                array[index1].compareAndSet(this, update1)
                array[index2].compareAndSet(this, update2)
            } else {
                array[index1].compareAndSet(this, expected1)
                array[index2].compareAndSet(this, expected2)
            }
        }

        fun apply() {
            if (status.value == UNDECIDED) {
                if (tryInstallDescriptor()) {
                    status.compareAndSet(UNDECIDED, SUCCESS)
                } else {
                    status.compareAndSet(UNDECIDED, FAILED)
                }
            }
            updateValues()
        }
    }

    enum class Status {
        UNDECIDED, SUCCESS, FAILED
    }
}