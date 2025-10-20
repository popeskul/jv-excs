import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

class TypeSafeRegistryTest {

    @Nested
    @DisplayName("register() tests")
    class RegisterTests {

        private TypeSafeRegistry registry;

        @BeforeEach
        void setUp() {
            registry = new TypeSafeRegistry(null);
        }

        @Test
        @DisplayName("should register type with SINGLETON scope")
        void shouldRegisterType_WithSingletonScope() {
            registry.register(String.class, () -> "test", Scope.SINGLETON);

            String result = registry.get(String.class);

            assertThat(result).isEqualTo("test");
        }

        @Test
        @DisplayName("should register type with PROTOTYPE scope")
        void shouldRegisterType_WithPrototypeScope() {
            AtomicInteger counter = new AtomicInteger(0);
            registry.register(Integer.class, counter::incrementAndGet, Scope.PROTOTYPE);

            Integer result = registry.get(Integer.class);

            assertThat(result).isEqualTo(1);
        }

        @Test
        @DisplayName("should throw exception when type is null")
        void shouldThrowException_WhenTypeIsNull() {
            assertThatThrownBy(() -> registry.register(null, () -> "test", Scope.SINGLETON))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw exception when provider is null")
        void shouldThrowException_WhenProviderIsNull() {
            assertThatThrownBy(() -> registry.register(String.class, null, Scope.SINGLETON))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw exception when scope is null")
        void shouldThrowException_WhenScopeIsNull() {
            assertThatThrownBy(() -> registry.register(String.class, () -> "test", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw exception when type already registered")
        void shouldThrowException_WhenTypeAlreadyRegistered() {
            registry.register(String.class, () -> "test1", Scope.SINGLETON);

            assertThatThrownBy(() -> registry.register(String.class, () -> "test2", Scope.SINGLETON))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already registered");
        }
    }

    @Nested
    @DisplayName("get() tests")
    class GetTests {

        private TypeSafeRegistry registry;

        @BeforeEach
        void setUp() {
            registry = new TypeSafeRegistry(null);
        }

        @Test
        @DisplayName("should return registered instance")
        void shouldReturnInstance_WhenTypeRegistered() {
            registry.register(String.class, () -> "hello", Scope.SINGLETON);

            String result = registry.get(String.class);

            assertThat(result).isEqualTo("hello");
        }

        @Test
        @DisplayName("should throw exception when type is null")
        void shouldThrowException_WhenTypeIsNull() {
            assertThatThrownBy(() -> registry.get(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw exception when type not registered")
        void shouldThrowException_WhenTypeNotRegistered() {
            assertThatThrownBy(() -> registry.get(String.class))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not registered");
        }
    }

    @Nested
    @DisplayName("SINGLETON scope tests")
    class SingletonScopeTests {

        private TypeSafeRegistry registry;

        @BeforeEach
        void setUp() {
            registry = new TypeSafeRegistry(null);
        }

        @Test
        @DisplayName("should return same instance for SINGLETON")
        void shouldReturnSameInstance_ForSingleton() {
            registry.register(String.class, () -> new String("test"), Scope.SINGLETON);

            String first = registry.get(String.class);
            String second = registry.get(String.class);

            assertThat(first).isSameAs(second);
        }

        @Test
        @DisplayName("should call provider only once for SINGLETON")
        void shouldCallProviderOnce_ForSingleton() {
            AtomicInteger callCount = new AtomicInteger(0);
            registry.register(String.class, () -> {
                callCount.incrementAndGet();
                return "test";
            }, Scope.SINGLETON);

            registry.get(String.class);
            registry.get(String.class);
            registry.get(String.class);

            assertThat(callCount.get()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("PROTOTYPE scope tests")
    class PrototypeScopeTests {

        private TypeSafeRegistry registry;

        @BeforeEach
        void setUp() {
            registry = new TypeSafeRegistry(null);
        }

        @Test
        @DisplayName("should return new instance for PROTOTYPE")
        void shouldReturnNewInstance_ForPrototype() {
            registry.register(String.class, () -> new String("test"), Scope.PROTOTYPE);

            String first = registry.get(String.class);
            String second = registry.get(String.class);

            assertThat(first).isNotSameAs(second);
        }

        @Test
        @DisplayName("should call provider every time for PROTOTYPE")
        void shouldCallProviderEveryTime_ForPrototype() {
            AtomicInteger callCount = new AtomicInteger(0);
            registry.register(Integer.class, callCount::incrementAndGet, Scope.PROTOTYPE);

            Integer first = registry.get(Integer.class);
            Integer second = registry.get(Integer.class);
            Integer third = registry.get(Integer.class);

            assertThat(first).isEqualTo(1);
            assertThat(second).isEqualTo(2);
            assertThat(third).isEqualTo(3);
            assertThat(callCount.get()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Parent registry tests")
    class ParentRegistryTests {

        @Test
        @DisplayName("should find type in parent when not in child")
        void shouldFindInParent_WhenNotInChild() {
            TypeSafeRegistry parent = new TypeSafeRegistry(null);
            parent.register(String.class, () -> "from parent", Scope.SINGLETON);

            TypeSafeRegistry child = new TypeSafeRegistry(parent);

            String result = child.get(String.class);

            assertThat(result).isEqualTo("from parent");
        }

        @Test
        @DisplayName("should prefer child registration over parent")
        void shouldPreferChild_OverParent() {
            TypeSafeRegistry parent = new TypeSafeRegistry(null);
            parent.register(String.class, () -> "from parent", Scope.SINGLETON);

            TypeSafeRegistry child = new TypeSafeRegistry(parent);
            child.register(String.class, () -> "from child", Scope.SINGLETON);

            String result = child.get(String.class);

            assertThat(result).isEqualTo("from child");
        }

        @Test
        @DisplayName("should search through multiple parent levels")
        void shouldSearchThroughMultipleLevels() {
            TypeSafeRegistry grandparent = new TypeSafeRegistry(null);
            grandparent.register(String.class, () -> "from grandparent", Scope.SINGLETON);

            TypeSafeRegistry parent = new TypeSafeRegistry(grandparent);
            TypeSafeRegistry child = new TypeSafeRegistry(parent);

            String result = child.get(String.class);

            assertThat(result).isEqualTo("from grandparent");
        }

        @Test
        @DisplayName("should throw exception when type not found in hierarchy")
        void shouldThrowException_WhenNotFoundInHierarchy() {
            TypeSafeRegistry parent = new TypeSafeRegistry(null);
            TypeSafeRegistry child = new TypeSafeRegistry(parent);

            assertThatThrownBy(() -> child.get(String.class))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not registered");
        }
    }

    @Nested
    @DisplayName("PECS - Producer Extends tests")
    class PECSTests {

        private TypeSafeRegistry registry;

        @BeforeEach
        void setUp() {
            registry = new TypeSafeRegistry(null);
        }

        @Test
        @DisplayName("should accept subtype provider")
        void shouldAcceptSubtypeProvider() {
            registry.register(Number.class, () -> Integer.valueOf(42), Scope.SINGLETON);

            Number result = registry.get(Number.class);

            assertThat(result).isInstanceOf(Integer.class);
            assertThat(result.intValue()).isEqualTo(42);
        }

        @Test
        @DisplayName("should work with inheritance hierarchy")
        void shouldWorkWithInheritanceHierarchy() {
            registry.register(CharSequence.class, () -> "test", Scope.SINGLETON);

            CharSequence result = registry.get(CharSequence.class);

            assertThat(result).isInstanceOf(String.class);
            assertThat(result.toString()).isEqualTo("test");
        }
    }
}
