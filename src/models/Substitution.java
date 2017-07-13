package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Substitution {
	Map<Formula, Formula> map;

	public Substitution() {
		this(new HashMap<Formula, Formula>());
	}
	public Substitution(Map<Formula, Formula> map) {
		super();
		this.map = map;
	}
	
	public void put(Formula a, Formula b) {
		map.put(a, b);
	}
	
	public Formula get(Formula a) {
		if (getDomain().contains(a)) {
			return map.get(a);
		} else {
			if (a instanceof AtomicFormula) {
				throw new NullPointerException();
			} else {
				CompoundFormula ca = (CompoundFormula)a;
				PropositionalConnective c = ca.getConnective();
				List<Formula> subs = ca.getImmediateSubformulas();
				List<Formula> appliedSubs = this.applyOn(subs);
				return new CompoundFormula(c, appliedSubs);
			}
		}
	}
	
	private List<Formula> applyOn(List<Formula> subs) {
		List<Formula> result = new ArrayList<Formula>();
		for (Formula f: subs) {
			result.add(this.get(f));
		}
		return result;

	}
	//try to create a substitution that sends sources(i) to destinations(i)
	public static Substitution make(List<Formula> sources,
			List<Formula> destinations) {
		Substitution result = new Substitution();
		for (int i=0; i< sources.size(); i++) {
			boolean success = result.expand(sources.get(i), destinations.get(i));
			if (!success) {
				return null;
			}
		}
		return result;
	}
	
	public void closeDomainUnderSubformulas(Set<PropositionalConnective> unaryConnectives) {
		Set<Formula> subformulas = Formula.getSubformulasOfASet(this.getDomain(), unaryConnectives);
		for (Formula f : subformulas) {
			if (!getDomain().contains(f)) {
				put(f, get(f));
			}
		}
	}
	
	
	private boolean expand(Formula source, Formula destination) {
		if (this.map.containsKey(source) && !this.get(source).equals(destination)) {
			return false;
		} else {
			if (source instanceof AtomicFormula) {
				this.put(source, destination);
			} else {
				CompoundFormula compundSource = (CompoundFormula) source;
				if (destination instanceof AtomicFormula) {
					return false;
				} else {
					CompoundFormula compoundDestination = (CompoundFormula) destination;
					if (!compundSource.getConnective().equals(compoundDestination.getConnective())) {
						return false;
					} else {
						this.put(source, destination);
						for (int i=0; i<compundSource.getConnective().getArity(); i++) {
							boolean b = expand(compundSource.getImmediateSubformulas().get(i), 
									compoundDestination.getImmediateSubformulas().get(i));
							if (b == false) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	public Set<Formula> getDomain() {
		return map.keySet();
	}
	
	//only applicable if the set is in the domain of the substitution
	public Set<Formula> applyOn(Set<Formula> set) {
		Set<Formula> result = new LinkedHashSet<Formula>();
		for (Formula f: set) {
			result.add(this.get(f));
		}
		return result;
	}
	
	public Sequent applyOn(Sequent s) {
		Sequent result = new Sequent();
		result.setLeftSide(this.applyOn(s.getLeftSide()));
		result.setRightSide(this.applyOn(s.getRightSide()));
		return result;
	}
	@Override
	public String toString() {
		return "Substitution [map=" + map + "]";
	}
	
	
	
	
	
}
