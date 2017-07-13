package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class StringUtils {
	
	public static Set<String> split(String str, String seperator) {
		Set<String> splitted = new LinkedHashSet<String>(Arrays.asList(str.split(seperator)));
		Set<String> noEmptyElements = new LinkedHashSet<String>();
		for (String s : splitted) {
			if (!s.isEmpty() && !s.replace(" ", "").isEmpty()) {
				noEmptyElements.add(s);
			}
		}
		return noEmptyElements;
	}
	
	public static Set<String> trim(Collection<String> col) {
		Set<String> result = new LinkedHashSet<String>();
		for (String str : col) {
			if (!str.trim().equals("")) {				
				result.add(str.trim());
			}
		}
		return result;
	}
	
	public static Set<String> splitAndTrim(String str, String seperator) {
		return trim(split(str, seperator));
	}
	
	public static List<String> splitToList(String str, String seperator) {
		List<String> result = new ArrayList<String>(Arrays.asList(str.split(seperator)));
		return result;
	}
	
	public static List<String> trimToList(Collection<String> col) {
		List<String> result = new ArrayList<String>();
		for (String str : col) {
			if (!str.trim().equals("")) {				
				result.add(str.trim());
			}
		}
		return result;
	}
	
	public static List<String> splitAndTrimToList(String str, String seperator) {
		return trimToList(splitToList(str, seperator));
	}
	
	public static String removeAllParenthesis(String str) {
		return str.replace("(", "").replace(")", "").trim();
	}
	
	public static String removeWrappingParenthesis(String str) {
		String bestBalanced = str.substring(0);
		if (wrappedByParenthesis(bestBalanced)) {
			String attempt = bestBalanced.substring(1, bestBalanced.length()-1);
			if (balanced(attempt)) {
				bestBalanced = removeWrappingParenthesis(attempt);
			}
		}
		return bestBalanced;
	}
	
	private static boolean wrappedByParenthesis(String str) {
		return str.startsWith("(") && str.endsWith(")");
	}

	public static List<String> getSortedByLength(List<String> list) {
		
		Collections.sort(list, new Comparator<String>() {
			public int compare(String a, String b) {
				if (((String)a).length() < ((String)b).length()) {
					return -1;
				}
				
				if (((String)a).length() > ((String)b).length()) {
					return 1;
				}				
				return 0;
			}
		} );
		return list;
	}
	
	public static List<String> getSortedByLength(Set<String> set) {
		String arr[] = {};
		return getSortedByLength(Arrays.asList(set.toArray(arr)));
	}

	public static List<Integer> getIndexesOfSubstring(String str, String substr) {
		List<Integer> result = new ArrayList<Integer>();
		for (int index = str.indexOf(substr); index != -1; index = str.indexOf(substr, index+substr.length())) {
			if (index != -1) {
				result.add(index);
			}
		}
		return result;
		
	}

	public static boolean balanced(String str) {
		int unclosed = 0;
		boolean unclosedWasNegative = false;
		for (int i=0; i<str.length(); i++) {
			if (str.charAt(i) == '(') {
				unclosed ++;
			} else if (str.charAt(i)==')') {
				unclosed--;
			}
			if (unclosed < 0) {
				unclosedWasNegative = true;
				break;
			}
		}
		return (!unclosedWasNegative) && (unclosed == 0);
	}

	public static String removeAllSpaces(String str) {
		return str.replace(" ", "");
	}
	
	public static Set<String> removeAllSpaces(Collection<String> coll) {
		Set<String> result = new LinkedHashSet<String>();
		for (String str : coll) {
			result.add(removeAllSpaces(str));
		}
		return result;
	}

	public static Set<String> splitAndTrimBalanced(String str,
			String seperator) {
		return trim(splitBalanced(str, seperator));
	}

	public static Set<String> splitBalanced(String str, String seperator) {
		Set<String> result = new LinkedHashSet<String>();
		str = seperator + str + seperator;
		List<Integer> indexesOfSeperator = getIndexesOfSubstring(str, seperator);
		List<Integer> indexesOfBalancedSeperator = new ArrayList<Integer>();
		for (int index : indexesOfSeperator) {
			String left = str.substring(0, index);
			String right = str.substring(index + seperator.length());
			if (balanced(left) && balanced(right)) {
				indexesOfBalancedSeperator.add(index);
			}
		}
		
		for (int i=0; i<indexesOfBalancedSeperator.size()-1; i++) {
			result.add(str.substring(indexesOfBalancedSeperator.get(i)+1, indexesOfBalancedSeperator.get(i+1)));
		}
		return result;
	}
	
	public static List<String> splitBalancedToList(String str, String seperator) {
		List<String> result = new ArrayList<String>();
		str = seperator + str + seperator;
		List<Integer> indexesOfSeperator = getIndexesOfSubstring(str, seperator);
		List<Integer> indexesOfBalancedSeperator = new ArrayList<Integer>();
		for (int index : indexesOfSeperator) {
			String left = str.substring(0, index);
			String right = str.substring(index + seperator.length());
			if (balanced(left) && balanced(right)) {
				indexesOfBalancedSeperator.add(index);
			}
		}
		
		for (int i=0; i<indexesOfBalancedSeperator.size()-1; i++) {
			result.add(str.substring(indexesOfBalancedSeperator.get(i)+1, indexesOfBalancedSeperator.get(i+1)));
		}
		return result;
	}

	public static List<String> splitAndTrimBalancedToList(
			String str, String seperator) {
		
		return trimToList(splitBalancedToList(str, seperator));
	}
	
	public static<T> String getCommaSeperatedList(Collection<T> col) {
		return getSeperatedList(col, ",");
	}
	
	public static<T> String getSeperatedList(Collection<T> col, String seperator) {
		String result = "";
		for (T t : col) {
			result += t.toString() + seperator;
		}
		if (result.endsWith(seperator)) {
			result = result.substring(0, result.length() - seperator.length());
		}
		return result;
	}

	public static List<String> removeDollars(List<String> list) {
		List<String> result = new ArrayList<String>();
		for (String str : list) {
			result.add(removeSymbol(str, "$"));
		}
		return result;
	}

	private static String removeSymbol(String str, String symbolToRemove) {
		return str.replace(symbolToRemove, "");
	}

	public static String displayMode(String str) {
		return " $$ " + makeTexNeededChanges(str.replace("$", "")) + " $$ ";
	}
	
	public static String mathMode(String str) {
		return " $ " + makeTexNeededChanges(str.replace("$", "")) + "$"; 
	}
	
	public static String makeTexNeededChanges(String str) {
		String result = str;
		result = escapeTildas(result);
		result = escapeAmpersands(result);
		result = makeTexSpaces(result);
		return result;
	}
	
	private static String escapeAmpersands(String str) {
		return str.replace("&", "\\\\&");
	}

	private static String escapeTildas(String str) {
		return str.replace("~", " \\\\tilde ");
	}

	public static String makeTexSpaces(String str) {
		return str.replace(" ", "~");
	}

	public static String getDfrac(String up, String down) {
		String result = "\\\\dfrac{" + up + "}" + "{" + down + "}";
		return result;
	}

	public static int countLines(String str) {
		return getIndexesOfSubstring(str, "\n").size();
	}
	
	public static Set<String> splitAndRemoveSpaces(String str,
			String seperator) {
		return removeAllSpaces(split(str, seperator));
	}
	

	public static String texify(String str) {
		return str.replace("AND", "\\wedge ")
		.replace("OR", "\\vee ")
		.replace("IMPLIES", "\\rightarrow ")
		.replace("NOT", "\\neg ").replace("\\tilde", "\\neg ")
		.replace("~", "\\neg ").replace("TOP", "\\top ")
		.replace("BOTTOM", "\\perp ")
		.replace("=>", "\\Rightarrow ")
		.replace("BOX", "\\Box")
		.trim();
	}

	public static MultiMap<String, String> getLinesAsMultiMap(String stringOfLines,
			String delimiter) {
		MultiMap<String, String> result = new MultiMap<String, String>();
		List<String> linesStrings = StringUtils.splitAndTrimToList(stringOfLines, "\n");
		for (String line : linesStrings) {
			String key = line.substring(0, line.indexOf(delimiter));
			String value = line.substring(line.indexOf(delimiter)+1);
			key = key.trim();
			value = value.trim();
			result.put(key, value);
		}
		return result;
		

	}

	public static boolean isEmpty(String name) {
		return name == null || name.trim().length() ==0;
	}
	
	public static String toStringNoBrackets(Collection<?> c) {
		String result = "";
		for (Object o : c) {
			result+=o.toString() + ",";
		}
		if (result.length()==0) {
			return result;
		} else {			
			return result.substring(0, result.length()-1);
		}
	}

	
}
