package net.drmirror;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiMap<K,V> {

	private Map<K,List<V>> map;
	
	public MultiMap() {
		map = new HashMap<K,List<V>>();
	}

	public void put (K key, V value) {
		List<V> values = map.get(key);
		if (values == null) {
			values = new LinkedList<V>();
			map.put(key, values);
		}
		values.add(value);
	}

	public List<V> getAll (K key) {
		return map.get(key);
	}
	
	public Set<K> keySet() {
		return map.keySet();
	}
	
	public int size() {
		return map.size();
	}

}
