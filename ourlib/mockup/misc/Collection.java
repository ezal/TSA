package mockup.misc;

public interface Collection<T> {
	public Iterator<T> iterator();
	public boolean add(T x);
	public boolean addAll(Collection<? extends T> x);
	public Object[] toArray();
}
