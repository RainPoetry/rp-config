package rainpoetry.config.rpconf.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RainPoetry
 */

public class Maps<K, V> {

	private final Map<K, V> maps;

	private Maps(Map<K, V> maps) {
		this.maps = maps;
	}

	public static <K, V> Maps newHashMap() {
		return new Maps<K, V>(new HashMap<K, V>());
	}

	public Maps pairs(K key, V v) {
		maps.put(key, v);
		return this;
	}

	public Map<K, V> toMap() {
		return maps;
	}

	public Map<K, V> unModify() {
		return Collections.unmodifiableMap(maps);
	}
}
