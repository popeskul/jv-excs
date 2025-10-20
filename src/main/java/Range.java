import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

/**
 * Immutable range [start, end] for comparable types.
 * Demonstrates bounds with Comparable and PECS pattern.
 *
 * @param <T> the type of elements (must be comparable)
 */
public final class Range<T extends Comparable<? super T>> {
    private final T start;
    private final T end;

    public Range(T start, T end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end cannot be null");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Start must be <= end");
        }
        this.start = start;
        this.end = end;
    }

    public T getStart() {
        return start;
    }

    public T getEnd() {
        return end;
    }

    public boolean contains(T value) {
        if (value == null) {
            return false;
        }

        return value.compareTo(start) >= 0 && value.compareTo(end) <= 0;
    }

    public boolean overlaps(Range<? extends T> other) {
        if (other == null) {
            return false;
        }

        return this.start.compareTo(other.getEnd()) <= 0
                && this.end.compareTo(other.getStart()) >= 0;
    }

    public Range<T> intersect(Range<? extends T> other) {
        if (!overlaps(other)) {
            throw new IllegalArgumentException("Ranges do not overlap");
        }

        T newStart = (T) (this.start.compareTo(other.getStart()) >= 0
                ? this.start
                : other.getStart());

        T newEnd = (T) (this.end.compareTo(other.getEnd()) <= 0
                ? this.end
                : other.getEnd());

        return new Range<>(newStart, newEnd);
    }

    public Range<T> span(Range<? extends T> other) {
        T newStart = (T) (this.start.compareTo(other.getStart()) <= 0
                ? this.start
                : other.getStart());

        T newEnd = (T) (this.end.compareTo(other.getEnd()) >= 0
                ? this.end
                : other.getEnd());

        return new Range<>(newStart, newEnd);
    }

    public T clamp(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        // If value < start, return start
        if (value.compareTo(start) < 0) {
            return start;
        }

        // If value > end, return end
        if (value.compareTo(end) > 0) {
            return end;
        }

        return value;
    }

    public static <E extends Comparable<? super E>> E max(Collection<? extends E> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Collection cannot be null or empty");
        }

        E maxValue = null;
        for (E item : items) {
            if (maxValue == null || item.compareTo(maxValue) > 0) {
                maxValue = item;
            }
        }
        return maxValue;
    }

    public static <E extends Comparable<? super E>> Range<E> cover(Collection<? extends E> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Collection cannot be null or empty");
        }

        E min = items.stream()
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("Collection is empty"));

        E max = items.stream()
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("Collection is empty"));

        return new Range<>(min, max);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range<?> range = (Range<?>) o;
        return Objects.equals(start, range.start) && Objects.equals(end, range.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }
}
