package models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;



public abstract class Formula implements Comparable<Formula> {
	
	public Formula() {
		
	}
	


	public abstract boolean isAtomic();

	public Formula copyFormula(Formula formula) {
		if (formula instanceof AtomicFormula) {
			AtomicFormula atomicFormula = (AtomicFormula)formula;
			return new AtomicFormula(atomicFormula.getSymbol());
		} else {
			CompoundFormula compundFormula = (CompoundFormula) formula;
			return new CompoundFormula(compundFormula.getConnective(), compundFormula.getImmediateSubformulas());
		}
	}





	public static Formula makeInstance(String str, Language L) {
		str = str.replace(" ", ""); 
		Formula result = null;
		if (L.isStringAnAtomicFormula(str)) {
			result = AtomicFormula.makeInstance(str);
		} else {
			result = CompoundFormula.makeInstance(str, L);
		}
		if (result == null) {
			throw new NullPointerException();
		} else {
			return result;
		}
	}

	public static Formula extractUnaryDefinedConnective(PropositionalConnective c, Formula definition, Formula originalFormula) throws TooManyAtomsException {
		if (originalFormula.isAtomic()) {
			return originalFormula;
		} else {   
			CompoundFormula originalCompundFormula = (CompoundFormula)originalFormula;
			PropositionalConnective mainConnective = originalCompundFormula.getConnective();
			if (mainConnective.equals(c)) {
				if (definition.numberOfAtoms()>1) {
					throw new TooManyAtomsException();
				} else {
					Substitution substitution = new Substitution();
					AtomicFormula p = definition.getAnAtom();
					substitution.put(p, originalCompundFormula.getImmediateSubformulas().get(0));
					return extractUnaryDefinedConnective(c, definition, substitution.get(definition));
				}
			} else {
				List<Formula> immediateSubformulas = originalCompundFormula.getImmediateSubformulas();
				List<Formula> newImmediateSubformulas = new ArrayList<Formula>();
				for (Formula immediateSubformula : immediateSubformulas) {
					newImmediateSubformulas.add(extractUnaryDefinedConnective(c, definition, immediateSubformula));
				}
				return new CompoundFormula(mainConnective, newImmediateSubformulas);
			}
		}
		
	}



	private int numberOfAtoms() {
		return getAtoms().size();
	}



	private AtomicFormula getAnAtom() {
		AtomicFormula[] array = new AtomicFormula[1];
		return getAtoms().toArray(array)[0];
	}



	private Set<AtomicFormula> getAtoms() {
		if (this.isAtomic()) {
			Set<AtomicFormula> result = new LinkedHashSet<AtomicFormula>();
			result.add((AtomicFormula)this);
			return result;
		} else {
			CompoundFormula compoundFormula = (CompoundFormula)this;
			Set<AtomicFormula> result = new LinkedHashSet<AtomicFormula>();
			for (Formula f : compoundFormula.getImmediateSubformulas()) {
				result.addAll(f.getAtoms());
			}
			return result;
		}
	}



	public Set<Formula> getLocalFormulas(Set<PropositionalConnective> unaryConnectives) {
		//Set<Formula> result = getLocalFormulasNoRecursion(unaryConnectives);
		Set<Formula> result = new LinkedHashSet<Formula>();
		getLocalFormulasRecursive(unaryConnectives, result);
		return result;
	}

	public void getLocalFormulasRecursive(Set<PropositionalConnective> unaryConnectives, Set<Formula> result) {
		result.add(this);
		if (! (this instanceof AtomicFormula)) {
			CompoundFormula cThis = (CompoundFormula) this;
			PropositionalConnective mainConnective = cThis.getConnective();
			if (mainConnective instanceof NextOperator) {
				NextOperator nextOperator = (NextOperator) mainConnective;
				Set<Formula> resultBeforePrefix = new LinkedHashSet<Formula>();
				cThis.getImmediateSubformulas().get(0).getLocalFormulasRecursive(unaryConnectives, resultBeforePrefix);
				result.addAll(nextOperator.addToAllFormulas(resultBeforePrefix));
			}
			else {
				for (Formula immediateSubformula : cThis.getImmediateSubformulas()) {
					for (PropositionalConnective unaryConnective : unaryConnectives) {
						Formula formulaToAdd = new CompoundFormula(unaryConnective, immediateSubformula);
						result.add(formulaToAdd);
					}
				}
				for (Formula immediateSubformula : cThis.getImmediateSubformulas()) {
					immediateSubformula.getLocalFormulasRecursive(unaryConnectives, result);
				}
			}
		}

	}
	
	public boolean hasPrefix() {
		return this.getMaximalPrefix().getLength() != 0;
	}
	
	private Set<Formula> getLocalFormulasNoRecursion(Set<PropositionalConnective> us) {
		Set<Formula> result = new LinkedHashSet<Formula>();
		Stack<Formula> stack = new Stack<Formula>();
		stack.push(this);
		result.add(this);
		

		while (!stack.isEmpty()) {
			Formula curr = stack.pop();
			Prefix prefix = curr.getMaximalPrefix();
			Formula body = curr.getBody();
			for (Formula immediateSubformula : body.getImmediateSubformulas()) {
				Formula immediateLocalFormula = immediateSubformula.prefixBy(prefix);
				stack.push(immediateLocalFormula);
				result.add(immediateLocalFormula);
				for (PropositionalConnective u :us) {
					Formula immediateULocalFormulaBody = new CompoundFormula(u, immediateSubformula);
					Formula immediateULocalFormula = immediateULocalFormulaBody.prefixBy(prefix); 
					result.add(immediateULocalFormula);
				}
			}
		}
		return result;
	}
	
	public Formula getBody() {
		return this.trim(this.getMaximalPrefix());
	}
	
	public abstract List<Formula> getImmediateSubformulas();




	public static Set<Formula> getSubformulasOfASet(Set<Formula> formulas, Set<PropositionalConnective> unaryConnectives) {
		Set<Formula> result = new LinkedHashSet<Formula>();
		for (Formula formula: formulas) {
			result.addAll(formula.getSubformulas(unaryConnectives));
		}
		return result;
	}





	public Set<Formula> getSubformulas(Set<PropositionalConnective> unaryConnectives) {
		Set<Formula> result = new LinkedHashSet<Formula>();
		result.add(this);
		if (! (this instanceof AtomicFormula)) {
			CompoundFormula compoundFormula = (CompoundFormula)this;
			for (Formula formula : compoundFormula.getImmediateSubformulas()) {
				for (Formula subformula: formula.getSubformulas(unaryConnectives)) { 
					result.add(subformula);
					for (PropositionalConnective unaryConnective : unaryConnectives) {
						Formula unariedFormula = new CompoundFormula(unaryConnective, subformula);
						result.add(unariedFormula);
						result.addAll(formula.getSubformulas(unaryConnectives));
					}
				}
			}
		}  
		return result;
	}





	@Override
	public int compareTo(Formula o) {
		return this.toString().compareTo(o.toString());
	}





	public Formula prefixBy(Prefix prefix) {
		if (prefix.isTheEmptyPrefix()) {
			return this;
		} else {			
			Formula result = copyFormula(this);
			for (int i=prefix.getLength()-1; i>=0; i--) {
				result = new CompoundFormula(prefix.getOperator(i), result);
			}
			return result;
		}
	}



	// if prefix is not a prefix of this formula, then we return this formula
	public Formula trim(Prefix prefix) {
			if (this instanceof AtomicFormula) {
				return this;
			} else {
				CompoundFormula thisCompound = (CompoundFormula)this;
				if (thisCompound.hasPrefix(prefix)) {
					if (prefix.isTheEmptyPrefix()) {
						return this;
					} else {
						return thisCompound.getImmediateSubformulas().get(0).trim(prefix.getSubPrefix(1));
					}
				} else {
					return this;
				}
			}
		}





	public Prefix getMaximalPrefix() {
		if (this instanceof AtomicFormula) {
			return new Prefix(); 
		} else {
			return ((CompoundFormula)this).getMaximalPrefix();
		}
	}





	public boolean hasPrefix(Prefix prefix) {
		if (prefix.isTheEmptyPrefix()) {
			return true;
		} else {
			if (this instanceof AtomicFormula) {
				return false;
			} else {				
				return ((CompoundFormula)this).hasPrefix(prefix);
			}
		}
	}
	
	public static void main(String[] args) {
		if (args == null || args.length ==0) {
			System.out.println("Input - a formula in the language &|->~. Output: the same formula, with good parenthesis.");
		}
		Language L = Language.makeInstance("", "&:2,|:2,->:2,!:1,@:1", "");
		String frmStr = args[0];
		Formula formula = Formula.makeInstance(frmStr, L);
		System.out.println(formula);
		
		
		
		//TODO remove following
		formula = Formula.makeInstance("(!A1&(!A2&(!A3&(!A4&(!A5&(!A6&(!A7&(!A8&(!A9&!A10)))))))))", L);
		System.out.println(formula);
	}
	
	

	
}
