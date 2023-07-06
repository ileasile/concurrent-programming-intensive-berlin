@file:Suppress("DuplicatedCode")

package day3

import kotlinx.atomicfu.*

// This implementation never stores `null` values.
class AtomicArrayWithCAS2<E : Any>(size: Int, initialValue: E) {
    private val array = atomicArrayOfNulls<Any?>(size)

    init {
        // Fill array with the initial value.
        for (i in 0 until size) {
            array[i].value = initialValue
        }
    }

    fun cas(index: Int, expected: E, update: E): Boolean {
        while (true) {
            val x = array[index].value
            when {
                x is AtomicArrayWithCAS2<*>.DCSSDescriptor -> x.apply(false)
                x is AtomicArrayWithCAS2<*>.CAS2Descriptor -> x.apply()
                x === expected -> {
                    if (array[index].compareAndSet(expected, update)) return true
                }
                else -> {
                    return false
                }
            }
        }
    }

    fun get(index: Int): E {
        var el: Any? = array[index].value
        while (true) {
            el = when(val value =  el) {
                is AtomicArrayWithCAS2<*>.CAS2Descriptor -> {
                    if (index == value.index1) {
                        if (value.status.value == Status.SUCCESS) {
                            value.update1
                        } else {
                            value.expected1
                        }
                    } else {
                        if (value.status.value == Status.SUCCESS) {
                            value.update2
                        } else {
                            value.expected2
                        }
                    }
                }
                is AtomicArrayWithCAS2<*>.DCSSDescriptor -> {
                    if (value.status.value == Status.SUCCESS) {
                        value.updateEl
                    } else {
                        value.expectedEl
                    }
                }
                else -> return value as E
            }
        }
    }

    fun cas2(
        index1: Int, expected1: E, update1: E,
        index2: Int, expected2: E, update2: E
    ): Boolean {
        require(index1 != index2) { "The indices should be different" }

        if (index1 < index2) return cas2(index2, expected2, update2, index1, expected1, update1)

        val descriptor = CAS2Descriptor(index1, expected1, update1, index2, expected2, update2)

        descriptor.apply()
        return descriptor.status.value == Status.SUCCESS
    }

    fun dcssWithStatus(
        index: Int, expectedEl: Any, updateEl: Any,
        obj1: CAS2Descriptor, expectedStatus: Status
    ): Boolean {
        val descriptor = DCSSDescriptor(index, expectedEl, updateEl, obj1, expectedStatus)
        descriptor.apply(true)

        return descriptor.status.value == Status.SUCCESS
    }

    inner class DCSSDescriptor(
        val index: Int,
        val expectedEl: Any,
        val updateEl: Any,
        val obj1: CAS2Descriptor,
        val expectedStatus: Status
    ) {
        val status = atomic(Status.UNDECIDED)

        fun apply(root: Boolean) {
            if (status.value == Status.UNDECIDED) {
                if ((!root || tryInstall()) && afterCheck()) {
                    status.compareAndSet(Status.UNDECIDED, Status.SUCCESS)
                } else {
                    status.compareAndSet(Status.UNDECIDED, Status.FAILED)
                }
            }
            updateValue()
        }

        private fun afterCheck(): Boolean {
            return obj1.status.value === expectedStatus
        }

        private fun tryInstall(): Boolean {
            while (true) {
                val x = array[index].value
                when {
                    x === this -> return true
                    x === expectedEl -> {
                        if (array[index].compareAndSet(expectedEl, this)) return true
                    }
                    x is AtomicArrayWithCAS2<*>.DCSSDescriptor -> x.apply(false)
                    x is AtomicArrayWithCAS2<*>.CAS2Descriptor -> x.apply()
                    else -> return false
                }
            }
        }

        private fun updateValue() {
            val s = status.value
            when(s) {
                Status.SUCCESS -> array[index].compareAndSet(this, updateEl)
                else -> array[index].compareAndSet(this, expectedEl)
            }
        }
    }

    inner class CAS2Descriptor(
        val index1: Int,
        val expected1: E,
        val update1: E,
        val index2: Int,
        val expected2: E,
        val update2: E
    ) {
        val status = atomic(Status.UNDECIDED)

        private fun tryInstallDescriptor(): Boolean {
            return tryInstallDescriptor(index1, expected1) && tryInstallDescriptor(index2, expected2)
        }

        private fun tryInstallDescriptor(index: Int, expected: E): Boolean {
            while (true) {
                val value = array[index].value
                when {
                    value === this -> return true
                    value === expected -> {
                        if (status.value != Status.UNDECIDED) return false
                        if (dcssWithStatus(index, expected, this, this, Status.UNDECIDED)) return true
                    }
                    value is AtomicArrayWithCAS2<*>.DCSSDescriptor -> value.apply(false)
                    value is AtomicArrayWithCAS2<*>.CAS2Descriptor -> value.apply()
                    else -> {
                        return false
                    }
                }
            }
        }

        private fun updateValues() {
            if (status.value == Status.SUCCESS) {
                array[index1].compareAndSet(this, update1)
                array[index2].compareAndSet(this, update2)
            } else {
                array[index1].compareAndSet(this, expected1)
                array[index2].compareAndSet(this, expected2)
            }
        }

        fun apply() {
            if (status.value == Status.UNDECIDED) {
                if (tryInstallDescriptor()) {
                    status.compareAndSet(Status.UNDECIDED, Status.SUCCESS)
                } else {
                    status.compareAndSet(Status.UNDECIDED, Status.FAILED)
                }
            }
            updateValues()
        }
    }

    enum class Status {
        UNDECIDED, SUCCESS, FAILED
    }
}