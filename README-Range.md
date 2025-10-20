# Range<T> - Immutable Range for Comparable Types

## Task Description

Create immutable class `Range<T>` for intervals [start, end] where T is a comparable type.

## Generic Constraint

```java
public final class Range<T extends Comparable<? super T>>
```

This means:
- `T` must implement `Comparable`
- `T` can be compared with its supertype
- Example: `Integer implements Comparable<Number>`

## Requirements

### Constructor

```java
public Range(T start, T end)
```
- Validates start <= end
- Throws exception if start > end or either is null

### Instance Methods

#### 1. `boolean contains(T value)`
Check if value is within range [start, end] (inclusive).

#### 2. `boolean overlaps(Range<? extends T> other)`
Check if two ranges have any overlap.
- Returns `true` if ranges share at least one point
- Returns `false` if no overlap

#### 3. `Range<T> intersect(Range<? extends T> other)`
Return intersection of two ranges.
- Returns new Range representing overlap
- Throws exception if ranges don't overlap

#### 4. `Range<T> span(Range<? extends T> other)`
Return range spanning both ranges (from minimum to maximum).
- Always succeeds (even if ranges don't overlap)
- Returns new Range [min(this.start, other.start), max(this.end, other.end)]

#### 5. `T clamp(T value)`
Clamp value to range boundaries.
- Returns `start` if value < start
- Returns `end` if value > end
- Returns `value` if within range

### Static Methods

#### 6. `static <E extends Comparable<? super E>> E max(Collection<? extends E> items)`
Find and return maximum element from collection.
- Throws exception if collection is null or empty

#### 7. `static <E extends Comparable<? super E>> Range<E> cover(Collection<? extends E> items)`
Create range covering all elements [min, max].
- Finds minimum and maximum in collection
- Returns new Range from min to max
- Throws exception if collection is null or empty

## Immutability Requirements

- Class must be `final`
- All fields must be `final`
- No setters
- Methods return new instances, never modify existing

## Example Usage

```java
Range<Integer> r1 = new Range<>(1, 10);
Range<Integer> r2 = new Range<>(5, 15);

boolean contains = r1.contains(7);           // true
boolean overlaps = r1.overlaps(r2);          // true
Range<Integer> intersection = r1.intersect(r2); // [5, 10]
Range<Integer> spanning = r1.span(r2);       // [1, 15]
Integer clamped = r1.clamp(20);              // 10

List<Integer> numbers = List.of(3, 7, 2, 9, 1);
Integer maximum = Range.max(numbers);         // 9
Range<Integer> coverage = Range.cover(numbers); // [1, 9]
```

## Success Criteria

- [ ] Class is `final` and immutable
- [ ] Generic bound `<T extends Comparable<? super T>>` used
- [ ] Constructor validates input
- [ ] All instance methods implemented correctly
- [ ] Both static methods implemented
- [ ] PECS pattern used in method parameters
- [ ] Null values handled appropriately
- [ ] All edge cases tested

## Hints

1. Use `compareTo()` for all comparisons
2. `<? extends T>` in parameters for flexibility
3. Static methods have their own type parameter `<E>`
4. Use `Stream.min()` and `Stream.max()` for static methods
5. Consider type erasure - some casts may need `@SuppressWarnings("unchecked")`
