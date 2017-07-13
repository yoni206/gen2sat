package utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TupleIterator<T> implements Iterator<List<T>> {
	
	Map<Integer, T> map;
	PnkIterator pnkIterator;
	
	public TupleIterator(Collection<T> col, int k) {
		map = new HashMap<Integer, T>();
		int i = 0;
		for (T elem : col) {
			map.put(i, elem);
			i++;
		}
		pnkIterator = new PnkIterator(col.size(), k);
	}
	
	@Override
	public boolean hasNext() {
		return pnkIterator.hasNext();
	}

	@Override
	public List<T> next() {
		return integerToT(pnkIterator.next());
	}

	private List<T> integerToT(List<Integer> integers) {
		List<T> result = new ArrayList<T>();
		for (Integer integer : integers) {
			result.add(map.get(integer));
		}
		return result;
	}

	@Override
	public void remove() {
		
	}
	

}
