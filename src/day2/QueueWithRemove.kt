package day2

import day1.*

interface QueueWithRemove<E> : Queue<E> {
    /**
     * Removes the first occurrence of the specified [element].
     * Returns `true` if the element was removed; `false` otherwise.
     */
    fun remove(element: E): Boolean

    /**
     * This is an internal function for tests, do not change it.
     */
    fun checkNoRemovedElements()
}