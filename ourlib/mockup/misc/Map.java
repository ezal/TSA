package mockup.misc;

public interface Map<K,V> {

	public V put(K key, V value);
	public V get(Object key);
	public boolean containsKey(Object key);
	public Set<Map.Entry<K,V>> entrySet();
	
	public static interface Entry<K, V>{
		K getKey(); 
		V getValue();
	}
}
