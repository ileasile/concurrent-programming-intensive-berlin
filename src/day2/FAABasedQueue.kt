package day2

import day1.*
import kotlinx.atomicfu.*
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicReferenceArray

// TODO: Copy the code from `FAABasedQueueSimplified`
// TODO: and implement the infinite array on a linked list
// TODO: of fixed-size segments.
class FAABasedQueue<E> : Queue<E> {
    private val SIZE = 2
    private val segments = AtomicReference(Node(AtomicReferenceArray<Any?>(SIZE), 0))

    private val enqIdx = atomic(0)
    private val deqIdx = atomic(0)

    private fun findSegment(segI: Int, doClear: Boolean): AtomicReferenceArray<Any?> {
        var el: Node<AtomicReferenceArray<Any?>> = segments.get()
        while (segI < el.idx) {
            val oldEl = el
            val nextNode = el.next.value
            el = if (nextNode == null) {
                val newNode = Node(AtomicReferenceArray<Any?>(SIZE), oldEl.idx + 1)
                if (!oldEl.next.compareAndSet(null, newNode)) {
                    oldEl.next.value!!
                } else {
                    newNode
                }
            } else {
                nextNode
            }

            if (doClear) {
                segments.compareAndSet(oldEl, el)
            }
        }
        return el.element
    }

    override fun enqueue(element: E) {
        while (true) {
            val i = enqIdx.getAndIncrement()
            val segment = findSegment(i / SIZE, false)
            if (segment.compareAndSet(i % SIZE, null, element)) return
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun dequeue(): E? {
        while (true) {
            var enqI = enqIdx.value
            var deqI = deqIdx.value
            while (true) {
                if (enqIdx.value == enqI) break
                enqI = enqIdx.value
                deqI = deqIdx.value
            }
            if (enqI <= deqI) return null

            val i = deqIdx.getAndIncrement()

            val s = findSegment(i / SIZE, true)

            if (s.compareAndSet(i % SIZE,null, POISONED)) continue
            return s[i % SIZE] as E
        }
    }

    private class Node<E>(
        var element: E,
        val idx: Int
    ) {
        val next = atomic<Node<E>?>(null)
    }
}

private val POISONED = Any()