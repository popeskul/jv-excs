import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Bounded queue with limited capacity.
 * Demonstrates PECS pattern (Producer Extends, Consumer Super).
 *
 * @param <T> the type of elements held in this queue
 */
public class BoundedQueue<T> {
    private final Deque<T> queue;
    private final int capacity;

    public BoundedQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.capacity = capacity;
        this.queue = new ArrayDeque<>(capacity);
    }

    /**
     * Attempts to add an element to the queue.
     *
     * @param item the element to add
     * @return true if added successfully, false if queue is full
     */
    public boolean offer(T item) {
        if (this.queue.size() < this.capacity) {
            this.queue.add(item);
            return true;
        }

        return false;
    }

    /**
     * Retrieves and removes the head of the queue.
     *
     * @return the head element, or null if queue is empty
     */
    public T poll() {
        return this.queue.poll();
    }

    //
    public int offerAll(Collection<? extends T> src) {
        int availableSpace = this.capacity - this.queue.size();
        int toAdd = Math.min(src.size(), availableSpace);

        int added = 0;
        for (T item : src) {
            if (added >= toAdd) {
                break;
            }

            this.queue.add(item);
            added++;
        }

        return added;
    }

    /**
     * Adds all elements from source collection (PECS: Producer Extends).
     * Stops when queue reaches capacity.
     *
     * @param src the source collection to read from (producer)
     * @return number of elements actually added
     */
    public int offerAllWithStream(Collection<? extends T> src) {
        int toAdd = Math.min(src.size(), capacity - queue.size());

        src.stream()
                .limit(toAdd)
                .forEach(this.queue::add);

        return toAdd;
    }

    /**
     * Moves filtered elements to destination collection (PECS: Consumer Super).
     * Elements are removed from this queue after transfer.
     *
     * @param dst    the destination collection to write to (consumer)
     * @param filter predicate to test each element
     * @return number of elements transferred
     */
    public int drainTo(Collection<? super T> dst, Predicate<? super T> filter) {
        int drained = 0;
        Iterator<T> iterator = this.queue.iterator();

        while (iterator.hasNext()) {
            T item = iterator.next();
            if (filter.test(item)) {
                dst.add(item);
                iterator.remove();
                drained++;
            }
        }

        return drained;
    }
}
