# Intensive Course on Concurrent Programming

## Day 1

### Coarse-grained bank

In [`src/day1/CoarseGrainedBank.kt`](src/day1/CoarseGrainedBank.kt),
make the sequential bank implementation thread-safe.
Please follow the *coarse-grained locking* scheme to make synchronization efficient.
For that, you need to use a single lock to protect all bank operations.

To test your solution, please run:

* `./gradlew test --tests CoarseGrainedBankTest` on Linux or MacOS
* `gradlew test --tests CoarseGrainedBankTest` on Windows

### Fine-grained bank

In [`src/day1/FineGrainedBank.kt`](src/day1/FineGrainedBank.kt),
make the sequential bank implementation thread-safe.
Please follow the *fine-grained locking* scheme to make synchronization efficient.
For that, you need to use per-account locks, thus, ensuring natural parallelism
when accessing different accounts. The `totalAmount()` function should acquire
all the locks to get a consistent snapshot, while `transfer(..)` should acquire
the corresponding account locks.

To test your solution, please run:

* `./gradlew test --tests FineGrainedBankTest` on Linux or MacOS
* `gradlew test --tests FineGrainedBankTest` on Windows

### Treiber stack

In [`src/day1/TreiberStack.kt`](src/day1/TreiberStack.kt),
implement the classic Treiber stack algorithm.

To test your solution, please run:

* `./gradlew test --tests TreiberStackTest` on Linux or MacOS
* `gradlew test --tests TreiberStackTest` on Windows

### Treiber stack with elimination

In [`src/day1/TreiberStackWithElimination.kt`](src/day1/TreiberStackWithElimination.kt),
implement the classic Treiber stack algorithm with the elimination technique.

To test your solution, please run:

* `./gradlew test --tests TreiberStackWithEliminationTest` on Linux or MacOS
* `gradlew test --tests TreiberStackWithEliminationTest` on Windows

### Michael-Scott queue

In [`src/day1/MSQueue.kt`](src/day1/MSQueue.kt),
implement the Michael-Scott queue algorithm.
You might also be interested in the [original paper](http://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf).

To test your solution, please run:

* `./gradlew test --tests MSQueueTest` on Linux or MacOS
* `gradlew test --tests MSQueueTest` on Windows



## Day 2

### FAA-based queue: simplified

In [`src/day2/FAABasedQueueSimplified.kt`](src/day2/FAABasedQueueSimplified.kt),
implement a concurrent queue that leverages the `Fetch-and-Add` synchronization primitive.
The high-level design of this queue bases on a conceptually infinite array for storing elements and manipulates
`enqIdx` and `deqIdx` counters, which reference the next working cells in the infinite array for `enqueue(..)`
and `dequeue()` operations.

In this task, use a big plain array as the infinite array implementation.

To test your solution, please run:

* `./gradlew test --tests FAABasedQueueSimplifiedTest` on Linux or MacOS
* `gradlew test --tests FAABasedQueueSimplifiedTest` on Windows


### FAA-based queue

In [`src/day2/FAABasedQueue.kt`](src/day2/FAABasedQueue.kt),
implement a concurrent queue that leverages the `Fetch-and-Add` synchronization primitive.
The high-level design of this queue bases on a conceptually infinite array for storing elements and manipulates
`enqIdx` and `deqIdx` counters, which reference the next working cells in the infinite array for `enqueue(..)`
and `dequeue()` operations.

The infinite array implementation should be simulated via a linked list of
fixed-size segments. The overall algorithm should be obstruction-free or lock-free.

To test your solution, please run:

* `./gradlew test --tests FAABasedQueueTest` on Linux or MacOS
* `gradlew test --tests FAABasedQueueTest` on Windows


### Logical removals in Michael-Scott queue

In [`src/day2/MSQueueWithOnlyLogicalRemove.kt`](src/day2/MSQueueWithOnlyLogicalRemove.kt),
implement a Michael-Scott queue with an additional `remove(element)` operation.
The implementation should remove elements only logically, keeping the corresponding nodes
in the linked list physically, but marking them as removed.

To test your solution, please run:

* `./gradlew test --tests MSQueueWithOnlyLogicalRemoveTest` on Linux or MacOS
* `gradlew test --tests MSQueueWithOnlyLogicalRemoveTest` on Windows


### Linear-time removals in Michael-Scott queue

In [`src/day2/MSQueueWithLinearTimeRemove.kt`](src/day2/MSQueueWithLinearTimeRemove.kt),
implement a Michael-Scott queue with an additional `remove(element)` operation.
The implementation should find the first node that contains the specified element
in linear time and then remove this node also in linear time.

To test your solution, please run:

* `./gradlew test --tests MSQueueWithLinearTimeRemoveTest` on Linux or MacOS
* `gradlew test --tests MSQueueWithLinearTimeRemoveTest` on Windows


### Constant-time removals in Michael-Scott queue

In [`src/day2/MSQueueWithLinearTimeRemove.kt`](src/day2/MSQueueWithLinearTimeRemove.kt),
implement a Michael-Scott queue with an additional `remove(element)` operation.
The implementation should find the first node that contains the specified element
in linear time, but remove this node in _constant_ time.

* `./gradlew test --tests MSQueueWithConstantTimeRemoveTest` on Linux or MacOS
* `gradlew test --tests MSQueueWithConstantTimeRemoveTest` on Windows

## Day 3

### CAS2: Simplified

In [`src/day3/AtomicArrayWithCAS2Simplified.kt`](src/day3/AtomicArrayWithCAS2Simplified.kt),
implement the `cas2(..)` operation. In this data task, all successful updates
install unique values in the array cells.

To test your solution, please run:

* `./gradlew test --tests AtomicArrayWithCAS2SimplifiedTest` on Linux or MacOS
* `gradlew test --tests AtomicArrayWithCAS2SimplifiedTest` on Windows

### Double-Compare-Single-Set

In [`src/day3/AtomicArrayWithDCSS.kt`](src/day3/AtomicArrayWithDCSS.kt),
implement the `dcss(..)` operation. Similarly to CAS2, it requires
allocating a descriptor and installing it in the updating memory location.
We need the `dcss(..)` operation for the next task, to resolve the ABA-problem
in the CAS2 algorithm.

To test your solution, please run:

* `./gradlew test --tests AtomicArrayWithDCSSTest` on Linux or MacOS
* `gradlew test --tests AtomicArrayWithDCSSTest` on Windows

### CAS2

In [`src/day3/AtomicArrayWithCAS2.kt`](src/day3/AtomicArrayWithCAS2.kt),
implement the `cas2(..)` operation. 
Unlike in the "CAS2: Simplified" task, updates are no longer unique.
This can lead to the ABA problem. To solve it, please use
the Double-Compare-Single-Set operation when installing CAS2 descriptors.

To test your solution, please run:

* `./gradlew test --tests AtomicArrayWithCAS2Test` on Linux or MacOS
* `gradlew test --tests AtomicArrayWithCAS2Test` on Windows
