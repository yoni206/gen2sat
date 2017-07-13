package models;

public class PropositionalConnective {
	
	protected String symbol;
	private int arity;
	
	public PropositionalConnective(String symbol, int arity) {
		this.symbol = symbol.trim();
		this.arity = arity;
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getArity() {
		return arity;
	}
	public void setArity(int arity) {
		this.arity = arity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + arity;
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
		PropositionalConnective other = (PropositionalConnective) obj;
		if (arity != other.arity)
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	@Override
	public String toString() {
		//return "PropositionalConnective [symbol=" + symbol + ", arity=" + arity		+ "]";
		return getSymbol();
	}
	
	
	

}
