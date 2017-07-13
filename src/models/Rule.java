package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.CombinationIterator;
import utils.StringUtils;

public class Rule {
	private Set<Sequent> premises;
	private Sequent conclusion;
	
	public Rule() {
		this(new LinkedHashSet<Sequent>(), new Sequent());
	}
	
	public Rule(Set<Sequent> premises, Sequent conclusion) {
		this.premises = premises;
		this.conclusion = conclusion;
	}


	public List<Formula> getClosingFormulas(Set<PropositionalConnective> us) {
		for (int i=1; i<=conclusion.getFormulas().size(); i++) {
			Set<Formula> closingFormulas = getClosingFormulas(i, us);
			if (closingFormulas != null) {
				List<Formula> result = new ArrayList<Formula>(closingFormulas);
				Collections.sort(result);
				return result;
			}
		}
		return null;
	}

	private Set<Formula> getClosingFormulas(int i, Set<PropositionalConnective> us) {
		for (CombinationIterator<Formula> c = new CombinationIterator<Formula>(conclusion.getFormulas(), i);c.hasNext();) {
			Set<Formula> closingFormulas = c.next();
			if (isClosing(closingFormulas, us)) {
				return closingFormulas;
			}
		}
		return null;
	}

	private boolean isClosing(Set<Formula> closingFormulas, Set<PropositionalConnective> us) {
		Set<Formula> subFormulasOfClosing = Formula.getSubformulasOfASet(closingFormulas, us);
		for (Formula formula : getFormulas()) {
			if (!subFormulasOfClosing.contains(formula)) {
				return false;
			}
		}
		return true;
	}

	
	private Set<Formula> getFormulas() {
		Set<Formula> allFormulasInRule = new LinkedHashSet<Formula>();
		for (Sequent s : premises) {
			allFormulasInRule.addAll(s.getFormulas());
		}
		allFormulasInRule.addAll(conclusion.getFormulas());
		return allFormulasInRule;
	}

	public Set<Sequent> getPremises() {
		return premises;
	}

	public void setPremises(Set<Sequent> premises) {
		this.premises = premises;
	}

	public Sequent getConclusion() {
		return conclusion;
	}

	public void setConclusion(Sequent conclusion) {
		this.conclusion = conclusion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conclusion == null) ? 0 : conclusion.hashCode());
		result = prime * result
				+ ((premises == null) ? 0 : premises.hashCode());
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
		Rule other = (Rule) obj;
		if (conclusion == null) {
			if (other.conclusion != null)
				return false;
		} else if (!conclusion.equals(other.conclusion))
			return false;
		if (premises == null) {
			if (other.premises != null)
				return false;
		} else if (!premises.equals(other.premises))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String result = "";
		for (Sequent premise : premises) {
			result += premise + "; ";
		}
		if (result.endsWith("; ")) {
			result = result.substring(0, result.length()-2);
		}
		result = result + " / " + conclusion;
		return result;
	}

	public static Rule makeInstance(String ruleStr, Language L) {
				
		String[] premsAndConc = ruleStr.split("/");
		String premisesStr = "";
		String concStr = "";
		if (premsAndConc.length >=1) {			
			premisesStr = premsAndConc[0];
		}
		if (premsAndConc.length>=2) {			
			concStr = premsAndConc[1];
		}
		Set<String> premisesStrings = StringUtils.splitAndTrim(premisesStr, ";");
		Set<Sequent> prems = new LinkedHashSet<Sequent>();
		for (String premStr : premisesStrings) {
			prems.add(Sequent.makeInstance(premStr, L));
		}
		Sequent conc = Sequent.makeInstance(concStr, L);
		
		Rule result = new Rule(prems,conc);
		return result;
	}

	public Set<Rule> axiomize() {
		Set<Rule> result = new LinkedHashSet<Rule>();
		if (this.premises.isEmpty()) {
			result.add(this);
		} else {			
			Map<Sequent, Set<Sequent>> fromPremisesToTheirComponents = new HashMap<Sequent, Set<Sequent>>();
			for (Sequent prem : this.premises) {
				Set<Sequent> components = new LinkedHashSet<Sequent>();
				components.addAll(getComponents(prem));
				fromPremisesToTheirComponents.put(prem, components);
			}
			List<Sequent> premisesList = new ArrayList<Sequent>();
			premisesList.addAll(this.premises);
			result = makeSequentsIntoAxiomaticRules(buildCombinations(fromPremisesToTheirComponents, premisesList));
		}
		return result;
	}

	private Set<Rule> makeSequentsIntoAxiomaticRules(
			Set<Sequent> sequents) {
		Set<Rule> result = new LinkedHashSet<Rule>();
		for (Sequent s : sequents) {
			result.add(makeSequentIntoAxiomaticRule(s));
		}
		return result;
		
	}

	private Rule makeSequentIntoAxiomaticRule(Sequent s) {
		return new Rule(new LinkedHashSet<Sequent>(), s);
	}

	private Set<Sequent> buildCombinations(Map<Sequent, Set<Sequent>> fromPremisesToTheirComponents, List<Sequent> prems) {
		Set<Sequent> resultConclusions = new LinkedHashSet<Sequent>();
		if (prems.isEmpty()) {
			resultConclusions.add(this.conclusion);
		} else {
			Sequent prem = prems.get(0);
			prems.remove(0);
			Set<Sequent> smallerSet = buildCombinations(fromPremisesToTheirComponents, prems);
			for (Sequent s : smallerSet) {
				for (Sequent component : fromPremisesToTheirComponents.get(prem)) {
					resultConclusions.add(s.union(component.reverse()));
				}
			}
		}
		return resultConclusions;
	}

	private Collection<? extends Sequent> getComponents(Sequent s) {
		Set<Sequent> result = new LinkedHashSet<Sequent>();
		for (Formula f : s.getLeftSide()) {
			Set<Formula> left = new LinkedHashSet<Formula>();
			Set<Formula> right = new LinkedHashSet<Formula>();
			left.add(f);
			result.add(new Sequent(left, right));
		}
		
		for (Formula f : s.getRightSide()) {
			Set<Formula> left = new LinkedHashSet<Formula>();
			Set<Formula> right = new LinkedHashSet<Formula>();
			right.add(f);
			result.add(new Sequent(left, right));
		}
		return result;
	}

	public boolean isAxiomatic() {
		return this.getPremises().isEmpty();
	}

	public boolean hasLonelyAtoms() {
		boolean result = false;
		for (Formula f : this.getFormulas()) {
			if (isLonelyAtom(f)) {
				result = true;
				break;
			}
		}
		return result;
	}

	private boolean isLonelyAtom(Formula f) {
		boolean result = true;
		if (f instanceof AtomicFormula) {			
			for (Formula other : this.getFormulas()) {
				if (!f.equals(other) && other.getSubformulas(new LinkedHashSet<PropositionalConnective>()).contains(f)) {
					result = false;
					break;
				}
			}
		} else {
			result = false;
		}
		return result;
	}

	
	
}
