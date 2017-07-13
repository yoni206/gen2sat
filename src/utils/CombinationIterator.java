package utils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.Combinations;

public class CombinationIterator<T> implements Iterator<Set<T>> {
	
	Map<Integer, T> map;
	Iterator<int[]> cnkIterator;
	
	public CombinationIterator(Collection<T> col, int k) {
		map = new HashMap<Integer, T>();
		int i = 0;
		for (T elem : col) {
			map.put(i, elem);
			i++;
		}
		cnkIterator = new Combinations(col.size(), k).iterator();
	}
	
	@Override
	public boolean hasNext() {
		return cnkIterator.hasNext();
	}

	@Override
	public Set<T> next() {
		return integerToT(cnkIterator.next());
	}

	private Set<T> integerToT(int[] integers) {
		Set<T> result = new LinkedHashSet<T>();
		for (int i=0 ;i< integers.length; i++) {
			result.add(map.get(integers[i]));
		}
		return result;
	}

	@Override
	public void remove() {
		
	}
	

}
