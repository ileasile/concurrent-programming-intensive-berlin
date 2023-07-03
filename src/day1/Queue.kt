package day1

interface Queue<E> {

    /**
     * Adds the specified [element] to the queue.
     */
    fun enqueue(element: E)

    /**
     * Retrieves the first element from the queue and returns it;
     * returns `null` if the queue is empty.
     */
    fun dequeue(): E?
}