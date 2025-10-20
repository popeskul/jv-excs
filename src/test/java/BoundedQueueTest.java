import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for BoundedQueue demonstrating PECS pattern.
 * Tests with Number, Integer, Object hierarchy.
 */
public class BoundedQueueTest {
    // ============================================
    // 1. BASIC OPERATIONS
    // ============================================
    @Nested
    @DisplayName("Basic operations")
    class BasicOperations {
        @Test
        @DisplayName("should add and retrieve element")
        void shouldAddAndRetrieve() {
            BoundedQueue<Integer> queue = new BoundedQueue<>(5);
            queue.offer(42);
            assertThat(queue.poll()).isEqualTo(42);
        }

        @Test
        @DisplayName("should return null when empty")
        void shouldReturnNullWhenEmpty() {
            BoundedQueue<Integer> queue = new BoundedQueue<>(5);
            assertThat(queue.poll()).isNull();
        }

        @ParameterizedTest
        @CsvSource({"5, 3, 3", "5, 7, 5", "1, 2, 1"})
        @DisplayName("should respect capacity")
        void shouldRespectCapacity(int capacity, int items, int expected) {
            BoundedQueue<Integer> queue = new BoundedQueue<>(capacity);
            int added = 0;
            for (int i = 0; i < items; i++) {
                if (queue.offer(i)) added++;
            }
            assertThat(added).isEqualTo(expected);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 0, -100})
        @DisplayName("should reject invalid capacity")
        void shouldRejectInvalid(int invalid) {
            assertThrows(IllegalArgumentException.class, () -> new BoundedQueue<>(invalid));
        }
    }

    // ============================================
    // 2. PECS: PRODUCER EXTENDS (offerAll)
    // ============================================
    @Nested
    @DisplayName("PECS: Producer Extends - offerAll(Collection<? extends T>)")
    class ProducerExtendsTests {
        @Test
        @DisplayName("Integer -> Number queue (Integer extends Number)")
        void shouldAcceptInteger_WhenQueueIsNumber() {
            BoundedQueue<Number> queue = new BoundedQueue<>(10);
            List<Integer> integers = List.of(1, 2, 3);

            int added = queue.offerAll(integers);

            assertThat(added).isEqualTo(3);
            assertThat(queue.poll()).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("Double -> Number queue (Double extends Number)")
    void shouldAcceptDouble_WhenQueueIsNumber() {
        BoundedQueue<Number> queue = new BoundedQueue<>(10);
        List<Double> doubles = List.of(1.5, 2.5, 3.5);

        int added = queue.offerAll(doubles);

        assertThat(added).isEqualTo(3);
        assertThat(queue.poll()).isEqualTo(1.5);
    }

    @Test
    @DisplayName("Integer -> Object queue (Integer extends Object)")
    void shouldAcceptInteger_WhenQueueIsObject() {
        BoundedQueue<Object> queue = new BoundedQueue<>(10);
        List<Integer> integers = List.of(1, 2, 3);

        int added = queue.offerAll(integers);

        assertThat(added).isEqualTo(3);
        assertThat(queue.poll()).isEqualTo(1);
    }

    @Test
    @DisplayName("Number -> Object queue (Number extends Object)")
    void shouldAcceptNumber_WhenQueueIsObject() {
        BoundedQueue<Object> queue = new BoundedQueue<>(10);
        List<Number> numbers = List.of(1, 2.5, 3L);

        int added = queue.offerAll(numbers);

        assertThat(added).isEqualTo(3);
    }

    @Test
    @DisplayName("Mixed Number subtypes -> Number queue")
    void shouldAcceptMixedNumberSubtypes() {
        BoundedQueue<Number> queue = new BoundedQueue<>(20);

        int addedIntegers = queue.offerAll(List.of(1, 2, 3));
        int addedDoubles = queue.offerAll(List.of(1.5, 2.5));
        int addedLongs = queue.offerAll(List.of(100L, 200L));

        assertThat(addedIntegers).isEqualTo(3);
        assertThat(addedDoubles).isEqualTo(2);
        assertThat(addedLongs).isEqualTo(2);
    }

    // ============================================
    // 3. PECS: CONSUMER SUPER (drainTo)
    // ============================================
    @Nested
    @DisplayName("PECS: Consumer Super - drainTo(Collection<? super T>)")
    class ConsumerSuperTests {
        @Test
        @DisplayName("Integer queue → Number collection (Number super Integer)")
        void shouldDrainInteger_ToNumber() {
            BoundedQueue<Integer> queue = new BoundedQueue<>(10);
            queue.offer(1);
            queue.offer(2);
            queue.offer(3);
            List<Number> numbers = new ArrayList<>();

            int drained = queue.drainTo(numbers, x -> x > 0);

            assertThat(drained).isEqualTo(3);
            assertThat(numbers).containsExactly(1, 2, 3);
            assertThat(queue.poll()).isNull(); // Queue is empty
        }

        @Test
        @DisplayName("Integer queue → Object collection (Object super Integer)")
        void shouldDrainInteger_ToObject() {
            BoundedQueue<Integer> queue = new BoundedQueue<>(10);
            queue.offer(1);
            queue.offer(2);
            List<Object> objects = new ArrayList<>();

            int drained = queue.drainTo(objects, x -> x > 0);

            assertThat(drained).isEqualTo(2);
            assertThat(objects).containsExactly(1, 2);
        }

        @Test
        @DisplayName("Number queue → Object collection (Object super Number)")
        void shouldDrainNumber_ToObject() {
            BoundedQueue<Number> queue = new BoundedQueue<>(10);
            queue.offer(1);
            queue.offer(2.5);
            queue.offer(3L);
            List<Object> objects = new ArrayList<>();

            int drained = queue.drainTo(objects, x -> true);

            assertThat(drained).isEqualTo(3);
            assertThat(objects).containsExactly(1, 2.5, 3L);
        }

        @Test
        @DisplayName("Predicate on supertype (Number filter for Integer queue)")
        void shouldUseSuperTypePredicate() {
            BoundedQueue<Integer> queue = new BoundedQueue<>(10);
            queue.offer(1);
            queue.offer(2);
            queue.offer(3);
            List<Number> numbers = new ArrayList<>();

            Predicate<Number> numberFilter = n -> n.doubleValue() > 1.5;

            int drained = queue.drainTo(numbers, numberFilter);

            assertThat(drained).isEqualTo(2);
            assertThat(numbers).containsExactly(2, 3);
        }

        @Test
        @DisplayName("Filter and drain partial elements")
        void shouldDrainOnlyFilteredElements() {
            BoundedQueue<Integer> queue = new BoundedQueue<>(10);
            queue.offer(1);
            queue.offer(2);
            queue.offer(3);
            queue.offer(4);
            List<Number> numbers = new ArrayList<>();

            int drained = queue.drainTo(numbers, x -> x % 2 == 0);

            assertThat(drained).isEqualTo(2);
            assertThat(numbers).containsExactly(2, 4);
            assertThat(queue.poll()).isEqualTo(1); // Odd numbers remain
            assertThat(queue.poll()).isEqualTo(3);
        }
    }

    // ============================================
    // 4. FULL TYPE HIERARCHY TESTS
    // ============================================
    @Nested
    @DisplayName("Full type hierarchy: Object → Number → Integer")
    class FullHierarchyTests {

        @Test
        @DisplayName("Complete flow: Integer → Number queue → Object collection")
        void shouldWorkWithFullTypeHierarchy() {
            BoundedQueue<Number> queue = new BoundedQueue<>(10);
            List<Integer> integers = List.of(1, 2, 3);
            List<Object> objects = new ArrayList<>();

            int added = queue.offerAll(integers);

            int drained = queue.drainTo(objects, x -> x.intValue() > 0);

            assertThat(added).isEqualTo(3);
            assertThat(drained).isEqualTo(3);
            assertThat(objects).containsExactly(1, 2, 3);
            assertThat(queue.poll()).isNull();
        }

        @Test
        @DisplayName("Three-level hierarchy with mixed types")
        void shouldHandleThreeLevelHierarchy() {
            BoundedQueue<Object> queue = new BoundedQueue<>(20);

            queue.offerAll(List.of(1, 2, 3));           // Integer
            queue.offerAll(List.of("a", "b"));          // String
            queue.offerAll(List.of(1.5, 2.5));          // Double

            assertThat(queue.poll()).isEqualTo(1);      // Integer
            assertThat(queue.poll()).isEqualTo(2);
            assertThat(queue.poll()).isEqualTo(3);
            assertThat(queue.poll()).isEqualTo("a");    // String
            assertThat(queue.poll()).isEqualTo("b");
            assertThat(queue.poll()).isEqualTo(1.5);    // Double
        }
    }

    // ============================================
    // 5. EDGE CASES & VALIDATION
    // ============================================
    @Nested
    @DisplayName("Edge cases and validation")
    class EdgeCasesTests {
        @Test
        @DisplayName("should reject negative capacity")
        void shouldRejectNegativeCapacity() {
            assertThrows(IllegalArgumentException.class, () -> {
                new BoundedQueue<>(-1);
            });
        }

        @Test
        @DisplayName("should reject zero capacity")
        void shouldRejectZeroCapacity() {
            Exception ex = assertThrows(IllegalArgumentException.class, () -> {
                new BoundedQueue<>(0);
            });
            assertThat(ex.getMessage()).contains("Capacity must be positive");
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 10, 100})
        @DisplayName("should handle different capacities")
        void shouldHandleDifferentCapacities(int capacity) {
            BoundedQueue<Integer> queue = new BoundedQueue<>(capacity);

            for (int i = 0; i < capacity; i++) {
                assertThat(queue.offer(i)).isTrue();
            }

            assertThat(queue.offer(999)).isFalse();
        }
    }
}