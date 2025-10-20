# BoundedQueue with PECS Pattern

## Task Description

Implement `BoundedQueue<T>` - a queue with bounded capacity that demonstrates the PECS (Producer Extends, Consumer Super) pattern.

## Requirements

### Methods to Implement

#### 1. `boolean offer(T item)`
Add element to the queue.
- Returns `true` if element added successfully
- Returns `false` if queue is full

#### 2. `T poll()`
Remove and return element from the queue.
- Returns element of type `T` if queue is not empty
- Returns `null` if queue is empty

#### 3. `int offerAll(Collection<? extends T> src)`
Add all elements from source collection.
- **PECS: Producer Extends** - reading from source
- Returns number of elements successfully added
- Stops adding when queue reaches capacity

#### 4. `int drainTo(Collection<? super T> dst, Predicate<? super T> filter)`
Move filtered elements to destination collection.
- **PECS: Consumer Super** - writing to destination
- Applies filter to each element before moving
- Returns number of elements moved
- Elements are removed from queue after moving

## PECS Pattern

### Producer Extends: `<? extends T>`
Use when **reading** from a collection (producer of T).

```java
// Example: Can accept List<Integer> when T is Number
offerAll(Collection<? extends T> src)
```

### Consumer Super: `<? super T>`
Use when **writing** to a collection (consumer of T).

```java
// Example: Can accept List<Number> or List<Object> when T is Integer
drainTo(Collection<? super T> dst)
```

## Testing Requirements

Test with type hierarchy:
- `Number` (supertype)
- `Integer` (subtype)
- `Object` (top type)

Example:
```java
BoundedQueue<Number> queue = new BoundedQueue<>(10);
List<Integer> integers = List.of(1, 2, 3);
queue.offerAll(integers); // Should work - Integer extends Number

List<Object> objects = new ArrayList<>();
queue.drainTo(objects, n -> n.intValue() > 0); // Should work - Object is super of Number
```

## Success Criteria

- [ ] Queue has bounded capacity
- [ ] `offer()` returns true/false correctly
- [ ] `poll()` returns elements in FIFO order
- [ ] `offerAll()` accepts subtypes (Producer Extends)
- [ ] `drainTo()` accepts supertypes (Consumer Super)
- [ ] Filter predicate works with supertypes
- [ ] All edge cases handled (null, empty, full)
- [ ] Tests pass with Number/Integer/Object hierarchy

## Hints

1. Use `java.util.LinkedList` or `java.util.ArrayDeque` internally
2. Track capacity limit
3. `<? extends T>` - can read T or its subtypes
4. `<? super T>` - can write T or to its supertypes
5. `Predicate<? super T>` allows filter to work on supertypes
