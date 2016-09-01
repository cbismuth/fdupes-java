package fdups;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static com.codahale.metrics.MetricRegistry.name;
import static java.util.stream.Collector.Characteristics.UNORDERED;

final class MultimapCollector<T, K, V> implements Collector<T, Multimap<K, V>, Multimap<K, V>> {

    private final MetricRegistry metricRegistry;
    private final String name;

    private final Function<T, K> keyGetter;
    private final Function<T, V> valueGetter;

    private MultimapCollector(final MetricRegistry metricRegistry,
                              final String name,
                              final Function<T, K> keyGetter,
                              final Function<T, V> valueGetter) {
        super();

        this.metricRegistry = metricRegistry;
        this.name = name;

        this.keyGetter = keyGetter;
        this.valueGetter = valueGetter;
    }

    static <T, K, V> MultimapCollector<T, K, T> toMultimap(final MetricRegistry metricRegistry, final String name, final Function<T, K> keyGetter) {
        return toMultimap(metricRegistry, name, keyGetter, v -> v);
    }

    static <T, K, V> MultimapCollector<T, K, V> toMultimap(final MetricRegistry metricRegistry, final String name, final Function<T, K> keyGetter, final Function<T, V> valueGetter) {
        return new MultimapCollector<>(metricRegistry, name, keyGetter, valueGetter);
    }

    @Override
    public Supplier<Multimap<K, V>> supplier() {
        return ArrayListMultimap::create;
    }

    @Override
    public BiConsumer<Multimap<K, V>, T> accumulator() {
        return (map, element) -> {
            metricRegistry.counter(name(name, "counter")).inc();

            final K key = keyGetter.apply(element);
            final V value = valueGetter.apply(element);

            if (map.containsKey(key)) {
                metricRegistry.counter(name(name, "duplicates", "counter")).inc();
            }

            map.put(key, value);
        };
    }

    @Override
    public BinaryOperator<Multimap<K, V>> combiner() {
        return (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        };
    }

    @Override
    public Function<Multimap<K, V>, Multimap<K, V>> finisher() {
        metricRegistry.register(name(name, "finished"), (Gauge<Boolean>) () -> true);

        return map -> map;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return ImmutableSet.of(UNORDERED);
    }

}