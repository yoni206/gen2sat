package models;

import java.util.ArrayList;
import java.util.List;

import utils.Pair;
import utils.StringUtils;

public class CompoundFormula extends Formula {
	private PropositionalConnective connective;
	private List<Formula> immediateSubformulas;
	
	public CompoundFormula(PropositionalConnective connective,
			List<Formula> immediateSubformulas) {
		this.connective = connective;
		this.immediateSubformulas = immediateSubformulas;
	}

	public CompoundFormula(PropositionalConnective unaryConnective,
			Formula subformula) {
		List<Formula> imm = new ArrayList<Formula>();
		imm.add(subformula);
		this.connective = unaryConnective;
		this.immediateSubformulas = imm;
	}

	//copy constructor
	public CompoundFormula(CompoundFormula formula) {
		this(formula.getConnective(), formula.getImmediateSubformulas());
	}

	public PropositionalConnective getConnective() {
		return connective;
	}

	public void setConnective(PropositionalConnective connective) {
		this.connective = connective;
	}

	public List<Formula> getImmediateSubformulas() {
		return immediateSubformulas;
	}

	public void setImmediateSubformulas(List<Formula> immediateSubformulas) {
		this.immediateSubformulas = immediateSubformulas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((connective == null) ? 0 : connective.hashCode());
		result = prime
				* result
				+ ((immediateSubformulas == null) ? 0 : immediateSubformulas
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompoundFormula other = (CompoundFormula) obj;
		if (connective == null) {
			if (other.connective != null)
				return false;
		} else if (!connective.equals(other.connective))
			return false;
		if (immediateSubformulas == null) {
			if (other.immediateSubformulas != null)
				return false;
		} else if (!immediateSubformulas.equals(other.immediateSubformulas))
			return false;
		return true;
	}
	
	
	public static CompoundFormula makeInstance(String str, Language L) {
		str = StringUtils.removeAllSpaces(str);
		str = StringUtils.removeWrappingParenthesis(str);
		Pair<? extends PropositionalConnective, Integer> pair = getMainConnectiveAndItsIndex(str, L);
		if (pair == null) {
			System.out.println("CompoundFormula.makeInstance: " + str);
		}
		PropositionalConnective mainConnective = pair.getFirst();
		int mainConnectiveIndex = pair.getSecond();
		switch (mainConnective.getArity()) {
			case 0:		return parseZeroArity(mainConnective);
			case 1: 		return parseUnaryArity(str, mainConnective, L);
			case 2: 		return parseBinaryArity(str, mainConnective, mainConnectiveIndex, L);
			default:		return parsePolish(str, mainConnective, L);
		}
	}

	private static CompoundFormula parsePolish(String str, PropositionalConnective mainConnective, Language L) {
		List<Formula> immediateSubFormulas = new ArrayList<Formula>();
		String immSubFormString = str.substring(mainConnective.getSymbol().length()+1, str.length()-1);
		List<String> immSubStrings = StringUtils.splitAndTrimBalancedToList(immSubFormString, ",");
		for (String immsubFormula : immSubStrings) {
			immediateSubFormulas.add(Formula.makeInstance(immsubFormula, L));
		}
		return new CompoundFormula(mainConnective, immediateSubFormulas);
	}

	private static CompoundFormula parseBinaryArity(String str,
			PropositionalConnective mainConnective, int mainConnectiveIndex, Language L) {
		List<Formula> immediateSubformulas = new ArrayList<Formula>();
		String left = str.substring(0,mainConnectiveIndex);
		String right = str.substring(mainConnectiveIndex+mainConnective.getSymbol().length());
		immediateSubformulas.add(Formula.makeInstance(left, L));
		immediateSubformulas.add(Formula.makeInstance(right, L));
		return new CompoundFormula(mainConnective, immediateSubformulas);
	}

	private static Pair<? extends PropositionalConnective, Integer> getMainConnectiveAndItsIndex(String str, Language L) {
		
		
		//0-ary
		for (PropositionalConnective conn: L.getConnectivesWithArity(0)) {
			if (str.equals(conn.getSymbol())) {
				return new Pair<PropositionalConnective,Integer>(conn,0);
			}
		}
		
		//2-ary
		for (String symbol : StringUtils.getSortedByLength(L.getSymbolsForConnectivesWithArity(2))) { 
			int lengthOfSymbol = symbol.length();
			List<Integer> indexesOfSymbol = StringUtils.getIndexesOfSubstring(str, symbol); 
			for (int indexOfSymbol : indexesOfSymbol) {
				String left = str.substring(0,indexOfSymbol);
				String right = str.substring(indexOfSymbol+lengthOfSymbol);
				if (StringUtils.balanced(left) && StringUtils.balanced(right)) {
					return new Pair<PropositionalConnective,Integer>(L.getConnectiveBySymbol(symbol),indexOfSymbol);
				}
			}
		}
		
		//other-ary
		for (int i=1;i<=L.getMaxArity(); i++) {
			if (i!=2) {				
				for (String sym : StringUtils.getSortedByLength(L.getSymbolsForConnectivesWithArity(i))) {
					if (str.startsWith(sym)) {
						return new Pair<PropositionalConnective,Integer>(L.getConnectiveBySymbol(sym),0);
					}
				}
			}
		}
		
		//next operators
		for (String sym  : StringUtils.getSortedByLength(L.getSymbolsForNextOperators())) {
			if (str.startsWith(sym)) {
				return new Pair<NextOperator, Integer>(L.getNextOperatorBySymbol(sym), 0);
			}
		}
		
		return null;
		
		
	}

	private static CompoundFormula parseUnaryArity(String str, PropositionalConnective mainConnective, Language L) {
		int i = str.indexOf(mainConnective.getSymbol());
		String stringOfImmediateSubformula = str.substring(i+mainConnective.getSymbol().length(), str.length()-i);
		List<Formula> immediateSubformulas = new ArrayList<Formula>();
		immediateSubformulas.add(Formula.makeInstance(stringOfImmediateSubformula, L));
		return new CompoundFormula(mainConnective, immediateSubformulas);
	}

	private static CompoundFormula parseZeroArity(PropositionalConnective mainConnective) {
		return new CompoundFormula(mainConnective, new ArrayList<Formula>());
	}

	@Override
	public String toString() {
		if (connective.getArity() == 0) {
			return connective.toString();
		} else if (connective.getArity() == 1) {
			String immString = (immediateSubformulas.get(0) instanceof AtomicFormula) ?
					" " + immediateSubformulas.get(0).toString() : 
						"(" + immediateSubformulas.get(0) + ")";
			return connective.toString() +immString;
		} else if (connective.getArity() == 2) {
			String left = immediateSubformulas.get(0).toString();
			String right = immediateSubformulas.get(1).toString();
			
			if (needToAddParenthesis(immediateSubformulas.get(0))) {
				left = "(" + left + ")";
			}
			if (needToAddParenthesis(immediateSubformulas.get(1))) {
				right = "(" + right + ")";
			}
			
			return left + " " + connective + " " + right;
		} else {
			return connective + immediateSubformulas.toString().replace("[", "(").replace("]", ")") ;
		}
	}

	private boolean needToAddParenthesis(Formula formula) {
		return !(formula instanceof AtomicFormula || 
				(formula instanceof CompoundFormula && ((CompoundFormula)formula).getConnective().getArity() == 0));
	}

	public boolean hasPrefix(Prefix prefix) {
		return prefix.isPrefixOf(this.getMaximalPrefix());
	}

	public Prefix getMaximalPrefix() {
		Prefix prefix = new Prefix();
		if (connective instanceof NextOperator) {
			prefix.append((NextOperator)connective);
			prefix.append(immediateSubformulas.get(0).getMaximalPrefix());
		}
		return prefix;
		
	}

	@Override
	public boolean isAtomic() {
		return false;
	}
	
	
	
	
	
	
	
	
}
