package utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MultiMap<K,T> {

	private Map<K,Set<T>> internalMap;

	
	
	public MultiMap() {
		internalMap = new LinkedHashMap<K, Set<T>>();
	}
	
	public T getSome(K key) {
		T result = null;
		Set<T> all = getAll(key);
		if (all != null) {
			result = all.iterator().next();
		}
		return result;
	}



	public void put(K key, T value) {
		if (!internalMap.containsKey(key)) {
			internalMap.put(key, new LinkedHashSet<T>());
		}
		internalMap.get(key).add(value);
	}
	
	public void putAll(K key, Collection<T> values) {
		for (T value : values) {
			put(key, value);
		}
	}
	
	public Set<T> getAll(K key) {
		return internalMap.get(key);
	}
	
	public Set<K> keySet() {
		return internalMap.keySet();
	}
	
	public Set<T> values() {
		Set<T> result = new LinkedHashSet<T>();
		for (Set<T> set : internalMap.values()) {
			result.addAll(set);
		}
		return result;
	}
	
	public boolean containsKey(K key) {
		return internalMap.containsKey(key);
	}
}
