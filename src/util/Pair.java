package util;

import java.util.Objects;

/*
 * copied from http://stackoverflow.com/questions/5303539/didnt-java-once-have-a-pair-class
 */
public class Pair<F, S> {
    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) o;
        return p.first.equals(first) && p.second.equals(second);
    }

    public int hashCode() {
    	return Objects.hash(first, second);
    }

//	@Override
//	public int compareTo(Pair<F, S> o) {
//		if (first.equals(o.first))
//			return second.compareTo(o.second);
//		
//		return first.compareTo(o.first);
//	}
}
