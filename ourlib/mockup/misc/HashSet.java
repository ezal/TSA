package mockup.misc;

public class HashSet<E> implements Set<E> {
	private E x;

	@Override
	public Iterator<E> iterator() {
		Iterator<E> it = new Iterator<E>(x);
		return it;
	}

	@Override
	public boolean add(E y) {
		x = y;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		Iterator<? extends E> i = c.iterator();
		x = i.next();
		return true;
	}

	@Override
	public Object[] toArray() {
		Object[] a = new Object[1];
		a[0] = x;
		return a;
	}

	@Override
	public E get() {
		return x;
	}

}
