package models;

import java.util.Arrays;  
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.xplain.Xplain;

public class SatInstance<T> {
	private Set<SatClause<T>> setOfClauses;
	private BidiMap<T, Integer> fromTtoInt;
	private Xplain<ISolver> solver;
	private boolean satisfiable;
	private int[] counterModel;

	Collection<IConstr> explanation;

	public SatInstance() {
		this(new LinkedHashSet<SatClause<T>>());
	}
	
	public SatInstance(Set<SatClause<T>> setOfClauses) {
		super();
		this.setOfClauses = setOfClauses;
		solver = new Xplain<ISolver>(SolverFactory.newDefault());
		explanation = null;
	}

	public Set<SatClause<T>> getSetOfClauses() {
		return setOfClauses;
	}

	public void setSetOfClauses(Set<SatClause<T>> setOfClauses) {
		this.setOfClauses = setOfClauses;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((setOfClauses == null) ? 0 : setOfClauses.hashCode());
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
		SatInstance<?> other = (SatInstance<?>) obj;
		if (setOfClauses == null) {
			if (other.setOfClauses != null)
				return false;
		} else if (!setOfClauses.equals(other.setOfClauses))
			return false;
		return true;
	}

	public void addClause(SatClause<T> clause) {
		this.setOfClauses.add(clause);
		
	}
	
	public void addClauses(Collection<SatClause<T>> col) {
		for (SatClause<T> clo : col) {
			this.addClause(clo);
		}
	}

	public SatInstance<T> union(SatInstance<T> other) {
		SatInstance<T> result = new SatInstance<T>();
		result.addClauses(this.getSetOfClauses());
		result.addClauses(other.getSetOfClauses());
		return result;
	}
	
	public SatInstance<T> union(Set<SatInstance<T>> others) {
		SatInstance<T> result = new SatInstance<T>();
		result.addClauses(this.getSetOfClauses());
		for (SatInstance<T> other : others) {
			result.addClauses(other.getSetOfClauses());
		}
		return result;
	}

	private void addClauses(Set<SatClause<T>> clauses) {
		for (SatClause<T> c : clauses) {
			this.addClause(c);
		}
	}
	
	
	public void solve(DecisionProperties decisionProperties) throws MyTimeoutException, MyContradictionException {
		try {
			prepareToSolve(decisionProperties.getTimeoutInSeconds());
			satisfiable = solver.isSatisfiable();
			if (satisfiable && decisionProperties.includeDetails()) {
				counterModel = solver.model();
			} 
			if (!satisfiable && decisionProperties.includeDetails()) {
				explanation = solver.explain();
			}
			} catch (MyContradictionException e) {
				satisfiable = false;
			} catch (TimeoutException e) {
				throw new MyTimeoutException();
			}
	}
	

	public SatInstance<T> getExplanation() {
		if (explanation == null) {
			return null;
		} else {
			SatInstance<T> result = new SatInstance<T>();
			for (IConstr con : explanation) {
				SatClause<T> clause = new SatClause<T>();
				for (int i=0; i<con.size(); i++) {			
					int l = (con.get(i) % 2) == 0 ? con.get(i)/2 : (-1)*(con.get(i)-1)/2;
					if (this.fromTtoInt.containsValue(Math.abs(l))) {
						if (l>0) {
							clause.addPositiveLiteral(fromTtoInt.getKey(Math.abs(l)));
						} else {
							 clause.addNegativeLiteral(fromTtoInt.getKey(Math.abs(l)));
						}
					}
				}
				if (!clause.isEmpty()) {					
					result.addClause(clause);
				}
			}
			return result;
		}
	}

	
	

	private Map<T, Boolean> integerListToMap(List<Integer> l) {
		int[] intArray = new int[l.size()];
		for (int i=0; i<l.size(); i++) {
			intArray[i] = l.get(i);
		}
		return intArrayToMap(intArray);
	}

	private Map<T, Boolean> intArrayToMap(int[] counterModel) {
		Map<T, Boolean> result = new HashMap<T, Boolean>();
		for (int i=0; i< counterModel.length; i++) {
			if (counterModel[i] < 0) {
				result.put(fromTtoInt.getKey(-1*counterModel[i]), Boolean.FALSE);
			} else {
				result.put(fromTtoInt.getKey(counterModel[i]), Boolean.TRUE);
			}
		}
		return result;
	}

	private int[] getRealClause(SatClause<T> c) {
		int[] result = new int[c.getNumOfLiterals()];
		int i=0;
		for (Literal<T> l : c.getSetOfLiterals()) {
			int sign = l.getHolds() ? 1 : -1 ;
			int absValue = fromTtoInt.get(l.getElement());
			result[i] = sign * absValue;
			i++;
		}
		return result;
	}

	public int getNumberOfClauses() {
		return this.setOfClauses.size();
	}

	public int getNumberOfVariables() {
		return getVariables().size();
	}

	public void prepareToSolve(int timeout) throws MyContradictionException {
		generateMapping();
		solver.setTimeout(timeout);
		solver.newVar(this.getNumberOfVariables());
		solver.setExpectedNumberOfClauses(this.getNumberOfClauses());
		for (SatClause<T> c : this.setOfClauses) {
			int[] realClause = getRealClause(c);
			try {
				solver.addClause(new VecInt(realClause));
			} catch (ContradictionException e) {
				throw new MyContradictionException();
			}
		}


		
	}

	private void generateMapping() {
		fromTtoInt = new DualHashBidiMap<T, Integer>();
		int i=1;
		for (T t : getVariables()) {
			fromTtoInt.put(t, i);
			i++;
		}
	}

	public Set<T> getVariables() {
		Set<T> result = new LinkedHashSet<T>();
		for (SatClause<T> c : this.setOfClauses) {
			result.addAll(c.getVariables());
		}
		return result;
	}

	@Override
	public String toString() {
		return "SatInstance [setOfClauses=" + setOfClauses + ", counterModel="
				+ Arrays.toString(counterModel) + ", explanation="
				+ explanation.toString() + "]";
	}
	
	public boolean isSatisfiable() {
		return satisfiable;
	}

	public Map<T, Boolean> getCounterModel() {
		return intArrayToMap(counterModel);
	}
	
	

	
}
