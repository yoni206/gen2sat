package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ReflectionCombinationIterator implements Iterator<Set<Integer>> {
	private int  currIndex;
	private List<Set<Integer>> allCombinations;
	public long timeToGenerateLoops;

	
	public ReflectionCombinationIterator(int n, int k) throws ScriptException {
		currIndex = 0;
		timeToGenerateLoops = 0;
		createAllCombinations(n, k);
	}

	private void createAllCombinations(int n, int k) throws ScriptException {
		long startTime;
		long endTime;
		
		startTime = System.nanoTime();
		String nestedLoops = createNestedLoops(n,k);
		endTime = System.nanoTime();
		timeToGenerateLoops = endTime - startTime;

		String resultOfLoops = runLoops(nestedLoops);
		List<String> listOfCombinationStrings = StringUtils.splitAndTrimToList(resultOfLoops, "-");
		List<Set<String>> listOfSetsOfStrings = new ArrayList<Set<String>>();
		for (String combinationString : listOfCombinationStrings) {
			Set<String> specificCombinationAsString = StringUtils.splitAndTrim(combinationString, ",");
			listOfSetsOfStrings.add(specificCombinationAsString);
		}
		allCombinations = convertToListOfSetsOfIntegers(listOfSetsOfStrings);
	}

	private static String runLoops(String nestedLoops) throws ScriptException {
		System.out.println("Generated Code:");
		System.out.println(nestedLoops);
		// create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from String
        String str = "";
        engine.put("result", str);
        engine.eval(nestedLoops);
        return (String) engine.get("result");

	}

	private static String createNestedLoops(int n, int k) {
		String result = "";
		String previousI = "-1";
		for (int index=0; index < k; index ++) {
			String currentI = "i" + index;
			String loopLine = 
					"for (" + currentI + "=" + previousI + "+1; " + 
							currentI + "<" + n + "; " + 
							currentI + "++" +
							") {";
			result+=loopLine;
			previousI = currentI;
		}
		String command = "result = result + '-' +";
		for (int index=0; index < k; index ++) {
			String currentI = "i" + index;
			command+= currentI + "+',' +";
		}
		if (command.endsWith("+")) {
			command = command.substring(0, command.length()-1);
		}
		command+=";";
		result+=command;
		for (int i=0; i<k; i++) {
			result+="}";
		}
		return result;
	}

	private List<Set<Integer>> convertToListOfSetsOfIntegers(
			List<Set<String>> listOfSetsOfStrings) {
		
		List<Set<Integer>> result = new ArrayList<Set<Integer>>();
		for (Set<String> setOfStrings : listOfSetsOfStrings) {
			Set<Integer> setOfIntegers = convertToSetOfIntegers(setOfStrings);
			result.add(setOfIntegers);
		}
		return result;
	}

	private Set<Integer> convertToSetOfIntegers(Set<String> setOfStrings) {
		Set<Integer> result = new TreeSet<Integer>();
		for (String str : setOfStrings) {
			Integer integer = new Integer(str);
			result.add(integer);
		}
		return result;
	}

	@Override
	public boolean hasNext() {
		return currIndex < allCombinations.size();
	}

	@Override
	public Set<Integer> next() {
		return allCombinations.get(currIndex++);
	}

	@Override
	public void remove() {
		
	}

	
	public static void main(String[] args) throws ScriptException {
		int n = Integer.parseInt(args[0]);
		int k = Integer.parseInt(args[1]) ;
		System.out.println("n=" + n);
		System.out.println("k=" + k);
		ReflectionCombinationIterator it = new ReflectionCombinationIterator(n, k);
		for (int i=0; it.hasNext();i++) {
	
			System.out.println(i+": " + it.next());
		}
		
	}
}
