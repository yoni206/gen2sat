package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FormulaGenerator {
	static String DELIMITER = "_";
	static PropositionalConnective or = new PropositionalConnective("|", 2);
	static PropositionalConnective and = new PropositionalConnective("&", 2);
	static PropositionalConnective imp = new PropositionalConnective("->", 2);
	static PropositionalConnective neg = new PropositionalConnective("!", 1);
	static Set<PropositionalConnective> connectives = new LinkedHashSet<PropositionalConnective>(Arrays.asList(or, and, imp, neg));
	static Language l = new Language(null, connectives, null);
	static Formula atom = new AtomicFormula("p");
	static Formula top = new CompoundFormula(imp, Arrays.asList(atom, atom));
	static Formula bot = new CompoundFormula(neg, top);


	public static List<RothenbergFormula> getRothenbergs(int maxn, int interval) {
		List<RothenbergFormula> result = new ArrayList<RothenbergFormula>();
		for (int i=1; i<=4; i++) {			
			result.addAll(getRothenbergs(i, maxn, interval));
		}
		return result;
	}
	
	private static List<RothenbergFormula> getRothenbergs(int type, int maxn, int interval) {
		List<RothenbergFormula> result = new ArrayList<RothenbergFormula>();
		for (int n=interval; n<=maxn; n+=interval) {
				result.add(new RothenbergFormula(getRothenberg(type, n, true), type, n, true));
				result.add(new RothenbergFormula(getRothenberg(type, n, false), type, n, false));
		}
		return result;
	}

	public static Formula getRothenberg(int type, int n, boolean provable) {
		if (type <1 || type >4 || n<0) {
			throw new IllegalStateException();
		} else {
			Formula A = new AtomicFormula("A");
			Formula B = new AtomicFormula("B");
			
			Formula An = getXn(A, n);
			Formula Bn = getXn(B, n);
			Formula nA = getnX(A, n);
			Formula nB = getnX(B, n);
			Formula AORBn = getXn(new CompoundFormula(or, Arrays.asList(A,B)), n);
			Formula nAANDB = getnX(new CompoundFormula(and, Arrays.asList(A,B)), n);
			
			Formula left = null;
			Formula right = null;
			Formula result, negResult;
			switch (type) {
			case 1:
				left = new CompoundFormula(or, Arrays.asList(An, Bn));
				right = AORBn;
				break;
			case 2:
				left = AORBn;
				right = new CompoundFormula(or, Arrays.asList(An, Bn));
				break;
			case 3:
				left = nAANDB;
				right = new CompoundFormula(and, Arrays.asList(nA,nB));
				break;
			case 4:
				left = new CompoundFormula(and, Arrays.asList(nA,nB));
				right = nAANDB;
				break;
			}
			result = new CompoundFormula(imp, Arrays.asList(left, right));
			negResult = new CompoundFormula(neg, result);
			if (provable) {				
				return result;
			} else {
				return negResult;
			}
			
			
		}
	}

	private static Formula getnX(Formula X, int n) {
		if (n==0) {
			return bot;
		} else {
			return getCircledPlus(X, getnX(X,n-1));
		}
	}

	private static Formula getXn(Formula X, int n) {
		if (n == 0) {
			return top;
		} else {
			return getCircledDot(X, getXn(X,n-1));
		}
	}

	private static Formula getCircledDot(Formula X, Formula Y) {
		Formula negX = new CompoundFormula(neg, X);
		Formula negY = new CompoundFormula(neg, Y);
		Formula negXCircledPlusnegY = getCircledPlus(negX, negY);
		Formula result = new CompoundFormula(neg, negXCircledPlusnegY);
		return result;
	}

	private static Formula getCircledPlus(Formula X, Formula Y) {
		Formula negX = new CompoundFormula(neg, X);
		Formula result = new CompoundFormula(imp, Arrays.asList(negX,Y));
		return result;
	}
	
	public static void main(String[] args) {
		if (args == null || args.length < 3) {
			System.out.println("<name> <max size> <interval> [<type>]");
			System.out.println("For example: java -jar FormulaGenerator.java rothenberg 20 5  will one formula of each of the sizes 5,10,15,20, from each type of Rothenberg's formulas");
		} else {
			String name = args[0];
			if (!name.equalsIgnoreCase("rothenberg")) {
				System.out.println("for now, we only support Rothenberg's formulas");
				return;
			}
			int max = Integer.parseInt(args[1]);
			int interval = Integer.parseInt(args[2]);
			List<RothenbergFormula> detailedFormulas = null;
			if (args.length == 4) {
				int type = Integer.parseInt(args[3]);
				detailedFormulas = getRothenbergs(type, max, interval);
			} else {
				detailedFormulas = getRothenbergs(max, interval);
			}
			printDetailedFormulas(detailedFormulas, max);
		}
	}

	private static void printDetailedFormulas(List<RothenbergFormula> detailedFormulas, int maxn) {
		int lengthOfN = Integer.toString(maxn).length();
		for (RothenbergFormula detailedFormula : detailedFormulas) {
			System.out.println((detailedFormula.provable ? 1:0) + DELIMITER + detailedFormula.rothenbergType + DELIMITER + String.format("%0" + lengthOfN + "d", detailedFormula.rothenbergSize) + DELIMITER + detailedFormula.formula);
		}
		
	}

}