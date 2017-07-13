package models;

public class Literal<T> {
	T element;
	Boolean holds;
	
	public Literal(T element, Boolean holds) {
		super();
		this.element = element;
		this.holds = holds;
	}
	public T getElement() {
		return element;
	}
	public void setElement(T element) {
		this.element = element;
	}
	public Boolean getHolds() {
		return holds;
	}
	public void setHolds(Boolean holds) {
		this.holds = holds;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		result = prime * result + ((holds == null) ? 0 : holds.hashCode());
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
		Literal<?> other = (Literal<?>) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		if (holds == null) {
			if (other.holds != null)
				return false;
		} else if (!holds.equals(other.holds))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Literal [element=" + element + ", holds=" + holds + "]";
	}
	
	
}
