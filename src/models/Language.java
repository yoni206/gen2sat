package models;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import utils.StringUtils;
public class Language {
	
	private Set<PropositionalVariable> propositionalVariables;
	private Set<PropositionalConnective> propositionalConnectives;
	private Set<NextOperator> nextOperators;
	
	public Language(Set<PropositionalVariable> propositionalVariables,
			Set<PropositionalConnective> propositionalConnectives,
			Set<NextOperator> nextOperators) {
		this.propositionalVariables = propositionalVariables;
		this.propositionalConnectives = propositionalConnectives;
		this.nextOperators = nextOperators;
	}
	
	public Set<PropositionalVariable> getPropositionalVariables() {
		return propositionalVariables;
	}
	public void setPropositionalVariables(
			Set<PropositionalVariable> propositionalVariables) {
		this.propositionalVariables = propositionalVariables;
	}
	public Set<PropositionalConnective> getPropositionalConnectives() {
		return propositionalConnectives;
	}
	public void setPropositionalConnectives(
			Set<PropositionalConnective> propositionalConnectives) {
		this.propositionalConnectives = propositionalConnectives;
	}
	public Set<NextOperator> getNextOperators() {
		return nextOperators;
	}
	public void setNextOperators(Set<NextOperator> nextOperators) {
		this.nextOperators = nextOperators;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nextOperators == null) ? 0 : nextOperators.hashCode());
		result = prime
				* result
				+ ((propositionalConnectives == null) ? 0
						: propositionalConnectives.hashCode());
		result = prime
				* result
				+ ((propositionalVariables == null) ? 0
						: propositionalVariables.hashCode());
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
		Language other = (Language) obj;
		if (nextOperators == null) {
			if (other.nextOperators != null)
				return false;
		} else if (!nextOperators.equals(other.nextOperators))
			return false;
		if (propositionalConnectives == null) {
			if (other.propositionalConnectives != null)
				return false;
		} else if (!propositionalConnectives
				.equals(other.propositionalConnectives))
			return false;
		if (propositionalVariables == null) {
			if (other.propositionalVariables != null)
				return false;
		} else if (!propositionalVariables.equals(other.propositionalVariables))
			return false;
		return true;
	}

	public static Language makeInstance(String propositionalVariables,
			String propositionalConnectives, String nextOperators) {
		if (propositionalConnectives == null) {
			propositionalConnectives = "";
		}
		if (propositionalVariables == null) {
			propositionalVariables = "";
		}
		if (nextOperators == null) {
			nextOperators = "";
		}
		Set<String> propVarsStrings = StringUtils.splitAndRemoveSpaces(propositionalVariables, ",");
		Set<String> propConnsStrings = StringUtils.splitAndRemoveSpaces(propositionalConnectives, ",");
		Set<String> nextOperatorsStrings = StringUtils.splitAndRemoveSpaces(nextOperators, ",");
		
		Set<PropositionalVariable> setOfPropVars = new LinkedHashSet<PropositionalVariable>();
		Set<PropositionalConnective> setOfPropConns = new LinkedHashSet<PropositionalConnective>();
		Set<NextOperator> setOfNextOps = new LinkedHashSet<NextOperator>();
		
		for (String propVarStr : propVarsStrings) {
			setOfPropVars.add(new PropositionalVariable(propVarStr));
		}
		for (String propConnStr : propConnsStrings) {
			String symbolAndArity[] = propConnStr.split(":");
			String symbol = symbolAndArity[0];
			int arity = Integer.parseInt(symbolAndArity[1]);
			setOfPropConns.add(new PropositionalConnective(symbol, arity));
		}
		for (String nextOp : nextOperatorsStrings) {
			setOfNextOps.add(new NextOperator(nextOp));
		}
		Language result = new Language(setOfPropVars, setOfPropConns, setOfNextOps);
		return result;
	}

	@Override
	public String toString() {
		return "Language [propositionalVariables=" + propositionalVariables
				+ ", propositionalConnectives=" + propositionalConnectives
				+ ", nextOperators=" + nextOperators + "]";
	}
	
	public boolean isStringAnAtomicFormula(String str) {
		boolean result = true;
		for (PropositionalConnective connective : propositionalConnectives) {
			if (str.contains(connective.getSymbol())) {
				result = false;
			}
		}
		for (NextOperator operator : nextOperators) {
			if (str.contains(operator.getSymbol())) {
				result = false;
			}
		}
		return result;
	}
	
	public Set<PropositionalConnective> getConnectivesWithArity(int n) {
		Set<PropositionalConnective> result = new LinkedHashSet<PropositionalConnective>();
		for (PropositionalConnective con : propositionalConnectives) {
			if (con.getArity() == n) {
				result.add(con);
			}
		}
		return result;
	}
	
	public Set<String> getSymbolsForConnectivesWithArity(int n) {
		Set<PropositionalConnective> conns = getConnectivesWithArity(n);
		Set<String> result = new LinkedHashSet<String>();
		for (PropositionalConnective conn : conns) {
			result.add(conn.getSymbol());
		}
		return result;
	}

	public PropositionalConnective getConnectiveBySymbol(String symbol) {
		for (PropositionalConnective conn : propositionalConnectives) {
			if (conn.getSymbol().equals(symbol)) {
				return conn;
			}
		}
		return null;
	}

	public int getMaxArity() {
		Set<Integer>  arities = new LinkedHashSet<Integer>();
		for (PropositionalConnective conn : propositionalConnectives) {
			arities.add(conn.getArity());
		}
		if (!nextOperators.isEmpty()) {
			arities.add(1);
		}
		return arities.isEmpty() ? 0 : Collections.max(arities);
	}

	public Set<String> getSymbolsForNextOperators() {
		Set<String> result = new LinkedHashSet<String>();
		for (NextOperator n : nextOperators) {
			result.add(n.getSymbol());
		}
		return result;
	}

	public NextOperator getNextOperatorBySymbol(String sym) {
		for (NextOperator n : nextOperators) {
			if (n.getSymbol().equals(sym)) {
				return n;
			}
		}
		return null;
	}

	public boolean hasNextOperators() {
		return this.getNextOperators() != null && !this.getNextOperators().isEmpty();
	}
	
	
	


}
