# TypeSafeRegistry - Type-Safe Dependency Injection Container

## Task Description

Implement type-safe dependency injection container with lifecycle scopes and parent registry support.

## Requirements

### Enum Scope

Create enum with two lifecycle scopes:

```java
public enum Scope {
    SINGLETON,  // Single shared instance
    PROTOTYPE   // New instance on each request
}
```

### TypeSafeRegistry Class

#### Constructor

```java
public TypeSafeRegistry(TypeSafeRegistry parent)
```
- Accepts parent registry (can be null for root registry)
- Creates empty registry

#### Methods

##### 1. `<T> void register(Class<T> type, Supplier<? extends T> provider, Scope scope)`

Register type with provider and scope.

Parameters:
- `type` - Class object for type T
- `provider` - Supplier that creates instances (can return subtypes of T)
- `scope` - SINGLETON or PROTOTYPE

Behavior:
- Stores registration in internal map
- Validates all parameters are not null
- Throws exception if type already registered

##### 2. `<T> T get(Class<T> type)`

Retrieve instance of registered type.

Parameters:
- `type` - Class object for type T

Returns:
- Instance of type T

Behavior:
- For SINGLETON: Returns same instance on every call (lazy initialization)
- For PROTOTYPE: Creates new instance on every call
- If not found in current registry, searches in parent
- Throws exception if type not registered anywhere in hierarchy

## Scope Behavior

### SINGLETON
- Provider called only once (on first `get()`)
- Same instance returned for all subsequent `get()` calls
- Instance cached in registry

### PROTOTYPE
- Provider called on every `get()`
- New instance created each time
- No caching

## Parent Registry

Registries can form hierarchy:
```java
TypeSafeRegistry parent = new TypeSafeRegistry(null);
parent.register(Logger.class, () -> new ConsoleLogger(), Scope.SINGLETON);

TypeSafeRegistry child = new TypeSafeRegistry(parent);
child.get(Logger.class); // Finds in parent
```

Lookup order:
1. Search in current registry
2. If not found and parent exists, search in parent
3. If not found anywhere, throw exception

## PECS Pattern

`Supplier<? extends T>` - Producer Extends
- Provider can return T or any subtype of T
- Allows flexibility in what provider creates

Example:
```java
registry.register(Number.class, () -> Integer.valueOf(42), Scope.SINGLETON);
Number n = registry.get(Number.class); // Returns Integer
```

## Example Usage

```java
TypeSafeRegistry registry = new TypeSafeRegistry(null);

// Register SINGLETON
registry.register(String.class, () -> "Hello, World!", Scope.SINGLETON);
String s1 = registry.get(String.class);
String s2 = registry.get(String.class);
// s1 == s2 (same instance)

// Register PROTOTYPE
AtomicInteger counter = new AtomicInteger(0);
registry.register(Integer.class, counter::incrementAndGet, Scope.PROTOTYPE);
Integer i1 = registry.get(Integer.class); // 1
Integer i2 = registry.get(Integer.class); // 2
Integer i3 = registry.get(Integer.class); // 3
// i1 != i2 != i3 (different instances)

// Subtype registration
registry.register(Number.class, () -> Double.valueOf(3.14), Scope.SINGLETON);
Number num = registry.get(Number.class); // Returns Double

// Re-registration fails
registry.register(String.class, () -> "Another", Scope.PROTOTYPE); // Throws exception
```

## Success Criteria

- [ ] Enum `Scope` with SINGLETON and PROTOTYPE
- [ ] Constructor accepts parent registry
- [ ] `register()` validates parameters
- [ ] `register()` prevents re-registration
- [ ] `get()` returns correct type
- [ ] SINGLETON returns same instance
- [ ] PROTOTYPE creates new instance each time
- [ ] Parent registry lookup works
- [ ] Hierarchical search works correctly
- [ ] Exception thrown for unregistered types
- [ ] PECS pattern with `Supplier<? extends T>`

## Hints

1. Use `Map<Class<?>, Registration<?>>` internally
2. Create inner class `Registration<T>` to store provider, scope, and singleton instance
3. For SINGLETON: Check if instance already created, if not call provider
4. For PROTOTYPE: Always call provider
5. Use recursion or loop for parent lookup
6. Generic method signatures ensure type safety
7. `@SuppressWarnings("unchecked")` may be needed for type casting from raw types
