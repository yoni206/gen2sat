package models;

import java.util.ArrayList;
import java.util.List;

import utils.StringUtils;

public class AtomicFormula extends Formula {
	private String symbol;
	
	public AtomicFormula(String symbol) {
		this.symbol = symbol;
	}

	
	
	public String getSymbol() {
		return symbol;
	}



	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}



	public static Formula makeInstance(String str) {
		return new AtomicFormula(StringUtils.removeAllParenthesis(str));
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
		AtomicFormula other = (AtomicFormula) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}



	@Override
	public String toString() {
		//return "AtomicFormula [symbol=" + symbol + "]";
		return getSymbol();
	}



	@Override
	public boolean isAtomic() {
		return true;
	}



	@Override
	public List<Formula> getImmediateSubformulas() {
		return new ArrayList<Formula>();
	}
	
	
	
	

}
