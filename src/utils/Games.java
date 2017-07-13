package utils;

import java.util.Iterator;
import java.util.Set;

import javax.script.ScriptException;

import org.apache.commons.math3.util.Combinations;

public class Games {

	public static void main(String[] args) throws ScriptException  {
		compareApacheToReflection(50, 3);
	}
	
	public static void compareApacheToReflection(int n, int k) throws ScriptException {
		long startTime;
		long endTime;
		
		startTime = System.nanoTime();
		apache(n,k);
		endTime = System.nanoTime();
		long apacheTime = endTime - startTime;
		
		startTime = System.nanoTime();
		reflection(n,k);
		endTime = System.nanoTime();
		long reflectionTime = endTime - startTime;
		
		System.out.println("Apache:\t\t" + apacheTime + "\nReflection:\t" + reflectionTime);

	}

	private static void apache(int n, int k) throws ScriptException {
		runIterator( n,  k, true);
	}
	
	private static void reflection(int n, int k) throws ScriptException {
		runIterator( n, k, false);
	}

	private static void runIterator(int n, int k, boolean apache) throws ScriptException {
		Iterator it;
		if (apache) {
			System.out.println("Apache:");
			it = new Combinations(n,k).iterator();
		} else {
			System.out.println("Reflection:");
			it = new ReflectionCombinationIterator(n, k);
		}
		
		for (int i=0; it.hasNext(); i++) {
			System.out.println(i+ ": " + printSet(it.next(),apache));
		}
		if (!apache) System.out.println("time to generate loops: " + ((ReflectionCombinationIterator)it).timeToGenerateLoops);
	}

	private static String printSet(Object set, boolean apache) {
		String result = "[";
		if (apache) {
			int[] realSet = (int[]) set;
			for (int i=0; i<realSet.length; i++) {
				result+= ("" + realSet[i] +", ");
			}
			result = result.substring(0, result.length()-2);
			result+="]";
		} else {
			result =  ((Set<Integer>) set).toString();
		}
		return result;
	}

}
