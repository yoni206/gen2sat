package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import models.PropositionalConnective;


public class CollectionUtils {
	public static<T> List<List<T>> divideToListsOfSize(Collection<T> list, int size) {
		List<List<T>> result = new ArrayList<List<T>>();
		for (T t : list) {
			int numOfLists = result.size();
			if (numOfLists == 0 || result.get(numOfLists-1).size() == size) {
				List<T> newList = new ArrayList<T>();
				result.add(newList);
				numOfLists++;
			}
			result.get(numOfLists-1).add(t);
		}
		return result;
	}

	public static<T> List<T> toList(Collection<T> col) {
		return divideToListsOfSize(col, col.size()).get(0);
	}

	public static<T> Set<T> toSet(List<T> list) {
		Set<T> result = new LinkedHashSet<T>();
		for (T t : list) {
			result.add(t);
		}
		return result;
	}

	public static<T> Set<T> diff(Collection<T> first,
			Set<T> second) {
		Set<T> result = new LinkedHashSet<T>();
		for (T t : first) {
			if (!second.contains(t)) {
				result.add(t);
			}
		}
		for (T t: second) {
			if (!first.contains(t)) {
				result.add(t);
			}
		}
		return result;
	}
	
	  // The method that prints all possible strings of length k.  It is
    //  mainly a wrapper over recursive function printAllKLengthRec()
    public static<T> Set<List<T>> getAllWordsLengthK(Set<T> alphabet, int k) {
    		List<T> alphabetList = toList(alphabet);
        Set<List<T>> result = new LinkedHashSet<List<T>>();
    		int n = alphabet.size();        
        getAllWordsLengthKRec(alphabetList, new ArrayList<T>(), n, k, result);
        return result;
    }
 
    // The main recursive method to print all possible strings of length k
    private static<T> void getAllWordsLengthKRec(List<T> alphabet, List<T> prefix, int n, int k, Set<List<T>> result) {
         
        // Base case: k is 0, print prefix
        if (k == 0) {
            result.add(prefix);
            return;
        }
 
        // One by one add all characters from set and recursively 
        // call for k equals to k-1
        for (int i = 0; i < n; ++i) {
             
            // Next character of input added
            List<T> newPrefix = new ArrayList<T>();
            newPrefix.addAll(prefix);
            newPrefix.add(alphabet.get(i)); 
             
            // k is decreased, because we have added a new character
            getAllWordsLengthKRec(alphabet, newPrefix, n, k - 1, result); 
        }
    }
	
	

	public static List<String> toList(String[] array) {
		return Arrays.asList(array);
	}
	
	public static void main(String[] args) {
		Set<String> set = new LinkedHashSet<String>();
		set.add("A");
		set.add("B");
		set.add("C");
		System.out.println(nonEmptySubsets(set));
	}

	public static<T> Set<T> intersection(Set<T> A, Set<T> B) {
		Set<T> intersection = new LinkedHashSet<T>(A); // use the copy constructor
		intersection.retainAll(B);
		return intersection;
	}

	public static<T> Set<Set<T>> subsets(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<Set<T>>();
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<T>());
			return sets;
		}
		List<T> list = new ArrayList<T>(originalSet);
		T head = list.get(0);
		Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
		for (Set<T> set : subsets(rest)) {
			Set<T> newSet = new HashSet<T>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
	}	
	
	public static<T> Set<Set<T>> nonEmptySubsets(Set<T> originalSet) {
		Set<Set<T>> result = subsets(originalSet);
		Set<T> emptySet = new LinkedHashSet<T>();
		result.remove(emptySet);
		return result;
	}

	/**
	 * 
	 * @param set: in sets
	 * @param sets: a set of sets, set is one of them. 
	 * @return: true iff there is no superset of set in sets.
	 */
	public static<T> boolean isMaximalSet(Set<T> set,
			Set<Set<T>> sets) {
		for (Set<T> s : sets) {
			if (s!=set && s.containsAll(set)) {
				return false;
			}
		}
		return true;
	}

	public static<T> List<T> toList(T[] array) {
		List<T> result = new ArrayList<T>();
		if (array == null) {
			return result;
		} else {
			for (int i=0; i<array.length; i++) {
				result.add(array[i]);
			}
		}
		return result;
	}
	

}
