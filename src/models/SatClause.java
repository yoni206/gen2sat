package models;

import java.util.LinkedHashSet;
import java.util.Set;

public class SatClause<T> {
	Set<Literal<T>> setOfLiterals;

	public SatClause() {
		this(new LinkedHashSet<Literal<T>>());
	}

	public SatClause(Set<Literal<T>> setOfLiterals) {
		super();
		this.setOfLiterals = setOfLiterals;
	}

	public Set<Literal<T>> getSetOfLiterals() {
		return setOfLiterals;
	}

	public void setSetOfLiterals(Set<Literal<T>> setOfLiterals) {
		this.setOfLiterals = setOfLiterals;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((setOfLiterals == null) ? 0 : setOfLiterals.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SatClause<T> other = (SatClause<T>) obj;
		if (setOfLiterals == null) {
			if (other.setOfLiterals != null)
				return false;
		} else if (!setOfLiterals.equals(other.setOfLiterals))
			return false;
		return true;
	}

	public void addPositiveLiteral(T t) {
		this.addLiteral(new Literal<T>(t, Boolean.TRUE));
	}

	public void addNegativeLiteral(T t) {
		this.addLiteral(new Literal<T>(t, Boolean.FALSE));
	}

	private void addLiteral(Literal<T> literal) {
		this.setOfLiterals.add(literal);

	}

	public Set<T> getVariables() {
		Set<T> result = new LinkedHashSet<T>();
		for (Literal<T> l : this.setOfLiterals) {
			result.add(l.element);
		}
		return result;
	}

	public int getNumOfLiterals() {
		return this.setOfLiterals.size();
	}

	@Override
	public String toString() {
		return "SatClause [setOfLiterals=" + setOfLiterals + "]";
	}

	public boolean isEmpty() {
		return this.getSetOfLiterals().isEmpty();
	}

}
