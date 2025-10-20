import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TypeSafeRegistry {
    private final Map<Class<?>, Registration<?>> registrations;
    private final TypeSafeRegistry parent;

    public TypeSafeRegistry(TypeSafeRegistry parent) {
        this.registrations = new HashMap<>();
        this.parent = parent;
    }

    public <T> void register(Class<T> type, Supplier<? extends T> provider, Scope scope) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        if (scope == null) {
            throw new IllegalArgumentException("Scope cannot be null");
        }

        if (registrations.containsKey(type)) {
            throw new IllegalStateException("Type " + type.getName() + " is already registered");
        }

        registrations.put(type, new Registration<>(provider, scope));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }

        Registration<?> registration = registrations.get(type);

        if (registration == null) {
            if (parent != null) {
                return parent.get(type);
            }
            throw new IllegalArgumentException("Type " + type.getName() + " is not registered");
        }

        return (T) registration.getInstance();
    }

    private static class Registration<T> {
        private final Supplier<? extends T> provider;
        private final Scope scope;
        private T singletonInstance;

        public Registration(Supplier<? extends T> provider, Scope scope) {
            this.provider = provider;
            this.scope = scope;
        }

        public T getInstance() {
            if (scope == Scope.SINGLETON) {
                if (singletonInstance == null) {
                    singletonInstance = provider.get();
                }
                return singletonInstance;
            }
            return provider.get();
        }
    }
}
