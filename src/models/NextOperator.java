package models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NextOperator extends PropositionalConnective{
	
 
	public NextOperator(String symbol) {
		super(symbol.trim(), 1);
	} 

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
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
		NextOperator other = (NextOperator) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	public Set<Formula> addToAllFormulas(Set<? extends Formula> set) {
		Set<Formula> result = new LinkedHashSet<Formula>();
		for (Formula formula : set) {
			List<Formula> listOfFormula = new ArrayList<Formula>();
			listOfFormula.add(formula);
			result.add(new CompoundFormula(this, listOfFormula));
		}
		return result;
	}
	
	

}
