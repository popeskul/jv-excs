# Java Generics & PECS Pattern - Practical Exercises

This module contains three practical exercises for learning Java Generics and the PECS (Producer Extends, Consumer Super) pattern.

## Exercises

### 1. BoundedQueue<T> - PECS Pattern
**File:** `README-BoundedQueue.md`

Implement a queue with bounded capacity demonstrating:
- Producer Extends (`<? extends T>`)
- Consumer Super (`<? super T>`)
- Wildcard variance in collections

**Key Methods:**
- `offer(T item)` - Add element
- `poll()` - Remove element
- `offerAll(Collection<? extends T> src)` - Producer Extends
- `drainTo(Collection<? super T> dst, Predicate<? super T> filter)` - Consumer Super

### 2. Range<T> - Generic Bounds
**File:** `README-Range.md`

Create immutable range class with generic bounds:
- `<T extends Comparable<? super T>>`
- Range operations (contains, overlaps, intersect, span, clamp)
- Static utility methods (max, cover)

**Key Concepts:**
- Generic bounds with Comparable
- Immutability pattern
- Wildcard variance in method parameters
- Type erasure handling

### 3. TypeSafeRegistry - Type-Safe DI Container
**File:** `README-TypeSafeRegistry.md`

Implement dependency injection container with:
- Type-safe registration: `register(Class<T>, Supplier<? extends T>, Scope)`
- Type-safe retrieval: `get(Class<T>)`
- Lifecycle scopes: SINGLETON vs PROTOTYPE
- Parent registry hierarchy

**Key Concepts:**
- Runtime type tokens (`Class<T>`)
- Producer Extends with Supplier
- Lazy initialization for SINGLETON
- Registry pattern with hierarchy

## Running Tests

### Compile project
```bash
mvn clean compile
```

### Run all tests
```bash
mvn test
```

### Run specific test
```bash
mvn test -Dtest=BoundedQueueTest
mvn test -Dtest=RangeTest
mvn test -Dtest=TypeSafeRegistryTest
```

## Project Structure

```
exercises/
├── README.md                           # This file
├── README-BoundedQueue.md              # Exercise 1
├── README-Range.md                     # Exercise 2
├── README-TypeSafeRegistry.md          # Exercise 3
├── pom.xml
├── src/
│   ├── main/java/
│   │   ├── BoundedQueue.java
│   │   ├── Range.java
│   │   ├── TypeSafeRegistry.java
│   │   └── Scope.java
│   └── test/java/
│       ├── BoundedQueueTest.java
│       ├── RangeTest.java
│       └── TypeSafeRegistryTest.java
```

## Learning Objectives

### PECS Pattern
Understand when to use wildcards:
- **Producer Extends** (`<? extends T>`): Reading from collection
- **Consumer Super** (`<? super T>`): Writing to collection
- **Invariance** (exact `T`): Both reading and writing

### Generic Bounds
Master different types of bounds:
- Upper bound: `<T extends SomeClass>`
- Recursive bound: `<T extends Comparable<? super T>>`
- Multiple bounds: `<T extends Class & Interface>`

### Type Safety
Learn compile-time type checking:
- Generic methods with type parameters
- Runtime type tokens with `Class<T>`
- Type erasure and `@SuppressWarnings`
- Wildcard capture

### Design Patterns
Apply common patterns:
- Immutability (final class/fields)
- Dependency Injection
- Factory pattern with Supplier
- Registry pattern with hierarchy

## Common Pitfalls

1. **Mixing Producer and Consumer**: Don't use `<? extends T>` when writing to collection
2. **Forgetting type erasure**: Generic type info not available at runtime
3. **Wildcard capture**: Some operations not allowed on wildcard types
4. **Raw types**: Avoid using raw types, always use generics
5. **Null safety**: Always validate null inputs

## Additional Resources

- [Java Generics Tutorial](https://docs.oracle.com/javase/tutorial/java/generics/)
- [Effective Java - Items 26-33](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [PECS Principle Explained](https://stackoverflow.com/questions/2723397/what-is-pecs-producer-extends-consumer-super)
