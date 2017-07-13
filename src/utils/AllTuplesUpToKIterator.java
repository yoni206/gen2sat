package utils;

import java.util.ArrayList; 
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.commons.math3.util.Combinations;

public class AllTuplesUpToKIterator implements Iterator<List<Integer>> {
	private Iterator<int[]> combinationIterator;
	private PermutationIterator<Integer> permutationIterator;
	int i;
	int k;
	int n;

	public AllTuplesUpToKIterator(int n, int k) {
		this.i = 1;
		this.k = k;
		this.n = n;
		combinationIterator = new Combinations(n, 1).iterator();
		permutationIterator = new PermutationIterator<Integer>(intArrayToIntegerList(combinationIterator.next()));
	}
	
	@Override
	public boolean hasNext() {
		if (permutationIterator.hasNext()) {
			return true;
		} else if (combinationIterator.hasNext()) {
			return true;
		} else if (i<k) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Integer> next() {
		if (!permutationIterator.hasNext()) {
			if (!combinationIterator.hasNext()) {
				i++;
				combinationIterator = new Combinations(n, i).iterator();
			}
			permutationIterator = new PermutationIterator<Integer>(intArrayToIntegerList(combinationIterator.next()));			
		}
		return permutationIterator.next();
	}

	@Override
	public void remove() {

	}
	
	public static List<Integer> intArrayToIntegerList(int[] arr) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i=0; i< arr.length; i++) {
			result.add(arr[i]);
		}
		return result;
	}

	
	public static void main(String[] args) {
		int n = 4;
		int k = 2;
		for (AllTuplesUpToKIterator iter= new AllTuplesUpToKIterator(n, k); iter.hasNext();) {
			System.out.println(iter.next());
		}
		
	}
}
