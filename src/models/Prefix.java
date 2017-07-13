package models;

import java.util.ArrayList;
import java.util.List;

public class Prefix {
	List<NextOperator> pref;

	public Prefix() {
		this(new ArrayList<NextOperator>());
	}
	
	public Prefix(List<NextOperator> pref) {
		super();
		this.pref = pref;
	}

	public List<NextOperator> getPref() {
		return pref;
	}

	public void setPref(List<NextOperator> pref) {
		this.pref = pref;
	}

	@Override
	public String toString() {
		return "Prefix [pref=" + pref + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pref == null) ? 0 : pref.hashCode());
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
		Prefix other = (Prefix) obj;
		if (pref == null) {
			if (other.pref != null)
				return false;
		} else if (!pref.equals(other.pref))
			return false;
		return true;
	}
	
	public int getLength() {
		return pref.size();
	}
	
	public NextOperator getFirstOperator() {
		return getOperator(0);
	}
	
	public NextOperator getOperator(int i) {
		return pref.get(i);
	}

	//Doesn't change this, return a new one.
	public void append(NextOperator operator) {
		getPref().add(operator);
	}

	public boolean isTheEmptyPrefix() {
		return pref.size() == 0;
	}

	public Prefix getSubPrefix(int i) {
		return getSubPrefix(i, pref.size());
	}

	private Prefix getSubPrefix(int i, int j) {
		return new Prefix(this.getPref().subList(i, j));
		
	}

	public boolean isPrefixOf(Prefix other) {
		boolean result = true;
		if (this.getLength() > other.getLength()) {
			result = false;
		} else {			
			for (int i=0; i<this.getLength(); i++) {
				if (!this.getOperator(i).equals(other.getOperator(i))) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	public void append(Prefix otherPrefix) {
		this.pref.addAll(otherPrefix.getPref());
		
	}
	
	
}
