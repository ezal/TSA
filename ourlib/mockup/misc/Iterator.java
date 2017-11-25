package mockup.misc;

public class Iterator<E> {
	private E x;
	public Iterator(E e) {
		x = e;
	}
	public E next() {
		return x;
	}

	public boolean hasNext() {
		return false;
	}
	
	public void remove() {
	}
}
