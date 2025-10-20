import org.junit.jupiter.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class RangeTest {

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create range when start <= end")
        void shouldCreateRange_WhenStartLessThanOrEqualEnd() {
            Range<Integer> range = new Range<>(1, 10);

            assertThat(range.getStart()).isEqualTo(1);
            assertThat(range.getEnd()).isEqualTo(10);
        }

        @Test
        @DisplayName("should throw exception when start is null")
        void shouldThrowException_WhenStartIsNull() {
            assertThatThrownBy(() -> new Range<>(null, 10)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw exception when end is null")
        void shouldThrowException_WhenEndIsNull() {
            assertThatThrownBy(() -> new Range<>(1, null)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw exception when start > end")
        void shouldThrowException_WhenStartGreaterThanEnd() {
            assertThatThrownBy(() -> new Range<>(10, 1)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("must be <= end");
        }
    }

    @Nested
    @DisplayName("contains() tests")
    class ContainsTests {

        private Range<Integer> range;

        @BeforeEach
        void setUp() {
            range = new Range<>(1, 10);
        }

        @Test
        @DisplayName("should return true when value is within range")
        void shouldReturnTrue_WhenValueWithinRange() {
            assertThat(range.contains(5)).isTrue();
        }

        @Test
        @DisplayName("should return true when value equals start")
        void shouldReturnTrue_WhenValueEqualsStart() {
            assertThat(range.contains(1)).isTrue();
        }

        @Test
        @DisplayName("should return true when value equals end")
        void shouldReturnTrue_WhenValueEqualsEnd() {
            assertThat(range.contains(10)).isTrue();
        }

        @Test
        @DisplayName("should return false when value is below range")
        void shouldReturnFalse_WhenValueBelowRange() {
            assertThat(range.contains(0)).isFalse();
        }

        @Test
        @DisplayName("should return false when value is above range")
        void shouldReturnFalse_WhenValueAboveRange() {
            assertThat(range.contains(11)).isFalse();
        }

        @Test
        @DisplayName("should return false when value is null")
        void shouldReturnFalse_WhenValueIsNull() {
            assertThat(range.contains(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("overlaps() tests")
    class OverlapsTests {

        @Test
        @DisplayName("should return true when ranges overlap")
        void shouldReturnTrue_WhenRangesOverlap() {
            Range<Integer> range1 = new Range<>(1, 10);
            Range<Integer> range2 = new Range<>(5, 15);

            assertThat(range1.overlaps(range2)).isTrue();
            assertThat(range2.overlaps(range1)).isTrue();
        }

        @Test
        @DisplayName("should return true when ranges touch at boundary")
        void shouldReturnTrue_WhenRangesTouchAtBoundary() {
            Range<Integer> range1 = new Range<>(1, 10);
            Range<Integer> range2 = new Range<>(10, 20);

            assertThat(range1.overlaps(range2)).isTrue();
        }

        @Test
        @DisplayName("should return true when one range contains another")
        void shouldReturnTrue_WhenOneRangeContainsAnother() {
            Range<Integer> range1 = new Range<>(1, 20);
            Range<Integer> range2 = new Range<>(5, 10);

            assertThat(range1.overlaps(range2)).isTrue();
            assertThat(range2.overlaps(range1)).isTrue();
        }

        @Test
        @DisplayName("should return false when ranges do not overlap")
        void shouldReturnFalse_WhenRangesDoNotOverlap() {
            Range<Integer> range1 = new Range<>(1, 5);
            Range<Integer> range2 = new Range<>(10, 15);

            assertThat(range1.overlaps(range2)).isFalse();
        }

        @Test
        @DisplayName("should return false when other is null")
        void shouldReturnFalse_WhenOtherIsNull() {
            Range<Integer> range = new Range<>(1, 10);

            assertThat(range.overlaps(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("intersect() tests")
    class IntersectTests {

        @Test
        @DisplayName("should return intersection when ranges overlap")
        void shouldReturnIntersection_WhenRangesOverlap() {
            Range<Integer> range1 = new Range<>(1, 10);
            Range<Integer> range2 = new Range<>(5, 15);

            Range<Integer> result = range1.intersect(range2);

            assertThat(result.getStart()).isEqualTo(5);
            assertThat(result.getEnd()).isEqualTo(10);
        }

        @Test
        @DisplayName("should return same range when one contains another")
        void shouldReturnInnerRange_WhenOneContainsAnother() {
            Range<Integer> outer = new Range<>(1, 20);
            Range<Integer> inner = new Range<>(5, 10);

            Range<Integer> result = outer.intersect(inner);

            assertThat(result).isEqualTo(inner);
        }

        @Test
        @DisplayName("should throw exception when ranges do not overlap")
        void shouldThrowException_WhenRangesDoNotOverlap() {
            Range<Integer> range1 = new Range<>(1, 5);
            Range<Integer> range2 = new Range<>(10, 15);

            assertThatThrownBy(() -> range1.intersect(range2)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("do not overlap");
        }
    }

    @Nested
    @DisplayName("span() tests")
    class SpanTests {

        @Test
        @DisplayName("should return spanning range when ranges overlap")
        void shouldReturnSpan_WhenRangesOverlap() {
            Range<Integer> range1 = new Range<>(1, 10);
            Range<Integer> range2 = new Range<>(5, 15);

            Range<Integer> result = range1.span(range2);

            assertThat(result.getStart()).isEqualTo(1);
            assertThat(result.getEnd()).isEqualTo(15);
        }

        @Test
        @DisplayName("should return spanning range when ranges do not overlap")
        void shouldReturnSpan_WhenRangesDoNotOverlap() {
            Range<Integer> range1 = new Range<>(1, 5);
            Range<Integer> range2 = new Range<>(10, 15);

            Range<Integer> result = range1.span(range2);

            assertThat(result.getStart()).isEqualTo(1);
            assertThat(result.getEnd()).isEqualTo(15);
        }

        @Test
        @DisplayName("should return outer range when one contains another")
        void shouldReturnOuter_WhenOneContainsAnother() {
            Range<Integer> outer = new Range<>(1, 20);
            Range<Integer> inner = new Range<>(5, 10);

            Range<Integer> result = outer.span(inner);

            assertThat(result).isEqualTo(outer);
        }
    }

    @Nested
    @DisplayName("clamp() tests")
    class ClampTests {

        private Range<Integer> range;

        @BeforeEach
        void setUp() {
            range = new Range<>(10, 20);
        }

        @Test
        @DisplayName("should return value when within range")
        void shouldReturnValue_WhenWithinRange() {
            assertThat(range.clamp(15)).isEqualTo(15);
        }

        @Test
        @DisplayName("should return start when value below range")
        void shouldReturnStart_WhenValueBelowRange() {
            assertThat(range.clamp(5)).isEqualTo(10);
        }

        @Test
        @DisplayName("should return end when value above range")
        void shouldReturnEnd_WhenValueAboveRange() {
            assertThat(range.clamp(25)).isEqualTo(20);
        }

        @Test
        @DisplayName("should return start when value equals start")
        void shouldReturnStart_WhenValueEqualsStart() {
            assertThat(range.clamp(10)).isEqualTo(10);
        }

        @Test
        @DisplayName("should return end when value equals end")
        void shouldReturnEnd_WhenValueEqualsEnd() {
            assertThat(range.clamp(20)).isEqualTo(20);
        }

        @Test
        @DisplayName("should throw exception when value is null")
        void shouldThrowException_WhenValueIsNull() {
            assertThatThrownBy(() -> range.clamp(null)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cannot be null");
        }
    }

    @Nested
    @DisplayName("max() static method tests")
    class MaxTests {

        @Test
        @DisplayName("should return max element from collection")
        void shouldReturnMax_FromCollection() {
            List<Integer> numbers = List.of(1, 5, 3, 9, 2);

            Integer max = Range.max(numbers);

            assertThat(max).isEqualTo(9);
        }

        @Test
        @DisplayName("should return single element from single element collection")
        void shouldReturnElement_FromSingleElementCollection() {
            List<Integer> numbers = List.of(42);

            Integer max = Range.max(numbers);

            assertThat(max).isEqualTo(42);
        }

        @Test
        @DisplayName("should throw exception when collection is null")
        void shouldThrowException_WhenCollectionIsNull() {
            assertThatThrownBy(() -> Range.max(null)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cannot be null or empty");
        }

        @Test
        @DisplayName("should throw exception when collection is empty")
        void shouldThrowException_WhenCollectionIsEmpty() {
            assertThatThrownBy(() -> Range.max(List.of())).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cannot be null or empty");
        }
    }

    @Nested
    @DisplayName("cover() static method tests")
    class CoverTests {

        @Test
        @DisplayName("should create range from min to max")
        void shouldCreateRange_FromMinToMax() {
            List<Integer> numbers = List.of(5, 1, 9, 3, 7);

            Range<Integer> range = Range.cover(numbers);

            assertThat(range.getStart()).isEqualTo(1);
            assertThat(range.getEnd()).isEqualTo(9);
        }

        @Test
        @DisplayName("should create range with same start and end for single element")
        void shouldCreateSameRange_ForSingleElement() {
            List<Integer> numbers = List.of(42);

            Range<Integer> range = Range.cover(numbers);

            assertThat(range.getStart()).isEqualTo(42);
            assertThat(range.getEnd()).isEqualTo(42);
        }

        @Test
        @DisplayName("should throw exception when collection is null")
        void shouldThrowException_WhenCollectionIsNull() {
            assertThatThrownBy(() -> Range.cover(null)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cannot be null or empty");
        }

        @Test
        @DisplayName("should throw exception when collection is empty")
        void shouldThrowException_WhenCollectionIsEmpty() {
            assertThatThrownBy(() -> Range.cover(List.of())).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cannot be null or empty");
        }
    }
}
