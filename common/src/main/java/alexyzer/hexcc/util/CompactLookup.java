package alexyzer.hexcc.util;

import alexyzer.hexcc.client.LanguageWorkaround;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * Provides K->V mapping optimized for a small subset of used keys out of many.<p>
 * Since there are so many language keys, ~7k vanilla, to let's say ~11k modded, it would really waste (4+4)*n bytes more using a Object2ObjectOpenHashMap on its own;<p>
 * Is saving 10Kb necessary? For an addon, I think yes, especially for a utility method such as {@linkplain LanguageWorkaround#getEnglishMishapMessage}.<p>
 * Any extra memory taken makes garbage cleaner cycles more frequent. That stacks a ton with mods.<p>
 * Also, having continuous reference arrays used rarely gives more chances for the data to end up in swap.<p>
 * Heap object overhead for stored still exists though, accounting for 32*n bytes or 40Kb average.
 */
@SuppressWarnings({"unchecked"})
public class CompactLookup<K, V> implements Function<K, V> {

    protected final Object[] keys;
    protected final Object[] values;

    protected final Object2ObjectOpenHashMap<Object, Object> cache = new Object2ObjectOpenHashMap<>();

    protected CompactLookup(K[] keys, V[] values) {
        this.keys = keys;
        this.values = values;
    }

    public CompactLookup(java.util.Map<K, V> source) {

        //This constructor is optimized for large HashMaps with >~10k entries

        this.keys = new Object[source.size()];
        int[] i = {0};
        //Copy the values with a method that is probably implemented efficiently
        source.forEach((k, ignored) -> keys[i[0]++] = k);

        //Only sort keys themselves to reduce pointer chasing in context of O(N^(>1)) time of sort()
        Arrays.sort(keys);

        this.values = new Object[i[0]];
        for (int j = 0; j < keys.length; j++)
            //Assuming source is fresh HashMap, this is superior for locality, but with slightly bigger O(N) cost
            values[j] = source.get((K) keys[j]);
    }

    public void clearCache() {
        cache.clear();
        cache.trim();
    }

    @Override
    public V apply(K key) {
        return (V) cache.computeIfAbsent(key, k -> {
            var i = Arrays.binarySearch(keys, k);
            if (i >= 0) return values[i];
            return null;
        });
    }

    public static class Map<K, V> extends CompactLookup<K, V> implements java.util.Map<K, V> {

        public Map(java.util.Map<K, V> source) {
            super(source);
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object key) {
            return apply((K) key) != null;
        }

        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V get(Object key) {
            return apply((K) key);
        }

        @Override
        public @Nullable V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(@NotNull java.util.Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull Set<K> keySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull Collection<V> values() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull Set<Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }
}