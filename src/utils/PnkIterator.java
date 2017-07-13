package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.commons.math3.util.Combinations;

public class PnkIterator implements Iterator<List<Integer>> {
	private Iterator<int[]> combinationIterator;
	private PermutationIterator<Integer> permutationIterator; 

	public PnkIterator(int n, int k) {
		combinationIterator = new Combinations(n, k).iterator();
		permutationIterator = new PermutationIterator<Integer>(intArrayToIntegerList(combinationIterator.next()));
	}
	
	@Override
	public boolean hasNext() {
		if (permutationIterator.hasNext()) {
			return true;
		} else if (combinationIterator.hasNext()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Integer> next() {
		if (!permutationIterator.hasNext()) {
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
		
		for (Iterator<int[]> iter = new Combinations(5, 2).iterator(); iter.hasNext();) {
			System.out.println(Arrays.toString(iter.next()));
		}
	}
}
