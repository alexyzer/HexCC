package alexyzer.hexcc.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LuaOps implements DynamicOps<Object> {
    public static final LuaOps INSTANCE = new LuaOps();

    @Override
    public Object empty() {
        return new Object2ObjectLinkedOpenHashMap<>();
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, Object input) {
        return switch (input) {
            case null -> outOps.empty();
            case String str -> outOps.createString(str);
            case Number num -> outOps.createNumeric(num);
            case Boolean bool -> outOps.createBoolean(bool);
            case Map<?, ?> map -> convertMap(outOps, map);
            case List<?> list -> convertList(outOps, list);
            default -> throw new IllegalStateException("Don't know how to convert " + input);
        };
    }

    @Override
    public DataResult<Number> getNumberValue(Object input) {
        return input instanceof Number num ? DataResult.success(num) : DataResult.error(() -> "Not a number: " + input);
    }

    @Override
    public Object createNumeric(Number num) {
        return num;
    }

    @Override
    public DataResult<String> getStringValue(Object input) {
        return input instanceof String str ? DataResult.success(str) : DataResult.error(() -> "Not a string: " + input);
    }

    @Override
    public Object createString(String str) {
        return str;
    }

    @Override
    public DataResult<Object> mergeToList(Object list, Object value) {
        if (list instanceof Map<?, ?> table) {
            //noinspection unchecked
            ((Map<Object, Object>) table).put(table.size() + 1.0, value);
            return DataResult.success(table);
        }
        return DataResult.error(() -> "Not a list: " + list);
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, Object key, Object value) {
        if (map instanceof Map<?, ?> table) {
            //noinspection unchecked
            ((Map<Object, Object>) table).put(key, value);
            return DataResult.success(map);
        }
        return DataResult.error(() -> "Not a map: " + map);
    }

    @Override
    public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object input) {
        if (input instanceof Map<?, ?> map) {
            Stream.Builder<Pair<Object, Object>> stream = Stream.builder();
            map.forEach((k, v) -> {
                if (k instanceof String) stream.accept(new Pair<>(k, v));
            });
            return DataResult.success(stream.build());
        }
        return DataResult.error(() -> "Not a map: " + input);
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> map) {
        @SuppressWarnings("unchecked")
        var table = ((Map<Object, Object>) empty());

        map.forEachOrdered(entry -> {
            var k = entry.getFirst();
            if (!(k instanceof String))
                throw new IllegalArgumentException("Map key should be string: " + k);
            table.put(k, entry.getSecond());
        });

        return table;
    }

    @Override
    public DataResult<Stream<Object>> getStream(Object input) {
        if (input instanceof final Map<?, ?> table) {
            var values = Stream.builder();
            for (double i = 1; i <= table.size(); i++) {
                var elem = table.get(i);
                if (elem == null) break;
                values.accept(elem);
            }
            return DataResult.success(values.build());
        }
        return DataResult.error(() -> "Not an list: " + input);
    }

    @Override
    public Object createList(Stream<Object> input) {
        @SuppressWarnings("unchecked")
        var sequence = (Map<Object, Object>) empty();

        double[] i = {1.0};
        input.forEachOrdered(v -> sequence.put(i[0]++, v));

        return sequence;
    }

    @Override
    public Object remove(Object input, String key) {
        if (input instanceof final Map<?, ?> map) map.remove(key);
        return input;
    }
}
