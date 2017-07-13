package models;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.StringUtils;

public class SequentCalculus {
	private Set<Rule> rules;
	private Set<PropositionalConnective> analyticity;
	private Map<Rule, List<Formula>> rulesToClosingFormulas;
	
	//IMPORTANT: DO NOT MAKE EMPTY CONSTRUCTORS NOR SETTERS! YOU WILL FORGET TO CALL makemap();
	public SequentCalculus(Set<Rule> rules, Set<PropositionalConnective> analyticity) {
		this.rules = rules;
		this.analyticity = analyticity;
		makeMap();
	}

	private void makeMap() {
		rulesToClosingFormulas = new HashMap<Rule, List<Formula>>();
		for (Rule rule : rules) {
			rulesToClosingFormulas.put(rule, rule.getClosingFormulas(analyticity));
		}
	}


	public Set<Rule> getRules() {
		return rules;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rules == null) ? 0 : rules.hashCode());
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
		SequentCalculus other = (SequentCalculus) obj;
		if (rules == null) {
			if (other.rules != null)
				return false;
		} else if (!rules.equals(other.rules))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String result = "";
		for (Rule rule : rules) {
			result += "[" + rule + "], ";
		}
		if (result.endsWith(", ")) {
			result = result.substring(0, result.length() -2);
		}
		return result;
	}

	public static SequentCalculus makeInstance(String rules, Language L, String analyticityString) {
		Set<String> rulesStrings = StringUtils.splitAndTrim(rules, "\n");
		Set<Rule> rulesOfCalculus = new LinkedHashSet<Rule>();
		for (String ruleStr : rulesStrings) {
			rulesOfCalculus.add(Rule.makeInstance(ruleStr, L));
		}
		Set<String> analyticityWrt = StringUtils.splitAndTrim(analyticityString, ",");
		Set<PropositionalConnective> analyticityWrtConns = new LinkedHashSet<PropositionalConnective>();
		for (String str : analyticityWrt) {
			analyticityWrtConns.add(new PropositionalConnective(str, 1));
		}
		SequentCalculus result = new SequentCalculus(rulesOfCalculus, analyticityWrtConns);
		return result;
	}

	public Set<PropositionalConnective> getAnalyticity() {
		return analyticity;
	}
	

	public int getClosure() {
		int result = -1;
		for (Rule r: rules) {
			if (result < this.rulesToClosingFormulas.get(r).size()) {
				result = this.rulesToClosingFormulas.get(r).size();
			}
		}
		return result;
	}




	



	private boolean isAxiomatic() {
		boolean result = true;
		for (Rule r : rules) {
			if (!r.isAxiomatic()) {
				result = false;
				break;
			}
		}
		return result;
	}

	public boolean hasLonelyAtoms() {
		boolean result = false;
		for (Rule rule : rules) {
			if (rule.hasLonelyAtoms()) {
				result = true;
				break;
			}
		}
		return result;
	}

	public Map<Rule, List<Formula>> getRulesToClosingFormulas() {
		return rulesToClosingFormulas;
	}
	










	
	
}
