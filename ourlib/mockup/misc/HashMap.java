package mockup.misc;


public class HashMap<K,V> implements Map<K, V> {
	private K k;
	private V v;
	
	private Set<Map.Entry<K, V>> entrySet;
	
	static {
		// java.util.LinkedList seems to implement <clinit>. So we need to implement it as well.
		int z = 1;
	}
	
	public HashMap() {
		entrySet = new HashSet<Map.Entry<K, V>>();
	}

	@Override
	public V put(K key, V value) {
		entrySet.add(new Node<K,V>(key, value));
		return value;
	}

	@Override
	public V get(Object key) {
		Map.Entry<K,V> r = entrySet.get();
		return r.getValue();
	}
	
	@Override
	public boolean containsKey(Object key) {
		return true;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return entrySet;
	}
	
	static class Node<K,V> implements Map.Entry<K,V>{
		K key; 
		V val;
		
		Node(K key, V val){
			this.key = key;
			this.val = val;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return val;
		}		
	}
}
