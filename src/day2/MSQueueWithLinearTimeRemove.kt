@file:Suppress("FoldInitializerAndIfToElvis", "DuplicatedCode")

package day2

import kotlinx.atomicfu.*

class MSQueueWithLinearTimeRemove<E> : QueueWithRemove<E> {
    private val head: AtomicRef<Node>
    private val tail: AtomicRef<Node>

    init {
        val dummy = Node(null)
        head = atomic(dummy)
        tail = atomic(dummy)
    }

    override fun enqueue(element: E) {
        val node = Node(element)
        while (true) {
            val curTail = tail.value
            var r = false
            if (curTail.next.compareAndSet(null, node)) {
                tail.compareAndSet(curTail, node)
                r = true
            } else {
                val next = curTail.next.value
                tail.compareAndSet(curTail, next!!)
            }
            if (r) {
                if (curTail.extractedOrRemoved) curTail.removePhysically()
                return
            }
        }
    }

    override fun dequeue(): E? {
        while (true) {
            val el = head.value
            val nextEl = el.next.value ?: return null
            if (head.compareAndSet(el, nextEl) && nextEl.markExtractedOrRemoved()) {
                return nextEl.element
            }
        }
    }

    override fun remove(element: E): Boolean {
        // Traverse the linked list, searching the specified
        // element. Try to remove the corresponding node if found.
        // DO NOT CHANGE THIS CODE.
        var node = head.value
        while (true) {
            val next = node.next.value
            if (next == null) return false
            node = next
            if (node.element == element && node.remove()) return true
        }
    }

    /**
     * This is an internal function for tests.
     * DO NOT CHANGE THIS CODE.
     */
    override fun checkNoRemovedElements() {
        check(tail.value.next.value == null) {
            "tail.next must be null"
        }
        var node = head.value
        // Traverse the linked list
        while (true) {
            if (node !== head.value && node !== tail.value) {
                check(!node.extractedOrRemoved) {
                    "Removed node with element ${node.element} found in the middle of this queue"
                }
            }
            node = node.next.value ?: break
        }
    }

    // TODO: Node is an inner class for accessing `head` in `remove()`
    private inner class Node(
        var element: E?
    ) {
        val next = atomic<Node?>(null)

        /**
         * TODO: Both [dequeue] and [remove] should mark
         * TODO: nodes as "extracted or removed".
         */
        private val _extractedOrRemoved = atomic(false)
        val extractedOrRemoved get() = _extractedOrRemoved.value

        fun markExtractedOrRemoved(): Boolean = _extractedOrRemoved.compareAndSet(false, true)

        /**
         * Removes this node from the queue structure.
         * Returns `true` if this node was successfully
         * removed, or `false` if it has already been
         * removed by [remove] or extracted by [dequeue].
         */
        fun remove(): Boolean {
            if (!markExtractedOrRemoved()) return false
            removePhysically()
            return true
        }

        fun removePhysically() {
            var prev: Node? = null
            var el: Node? = head.value
            while (el != null && el != this){
                prev = el
                el = el.next.value
            }
            if (el == null) return
            val next = el.next.value
            if (prev == null || next == null) return
            if (prev.next.compareAndSet(el, next)) {
                if (prev.extractedOrRemoved) prev.removePhysically()
                if (next.extractedOrRemoved) next.removePhysically()
            }
            // why always true???
            return
        }
    }
}
