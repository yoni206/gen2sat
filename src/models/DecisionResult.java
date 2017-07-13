package models;

import java.util.LinkedHashSet; 
import java.util.Set;

import utils.MultiMap;

public class DecisionResult {
	
	private boolean provable;
	private Set<Rule> minimalSetOfRules;
	private PartialBiValuation counterModel;
	private boolean timeoutExceeded;
	private int numberOfVariables;
	private int numberOfClauses;

	/**
	 * to be called when the sequent is provable
	 * @param satInstance
	 * @param semanticConstraintsHoldToRules 
	 */
	private DecisionResult(SatInstance<Formula> explanation, MultiMap<SatClause<Formula>, Rule> semanticConstraintsHoldToRules, int numberOfVariables, int numberOfClauses) {
		this.provable = true;
		minimalSetOfRules = getSetOfRules(explanation, semanticConstraintsHoldToRules);
		this.counterModel = null;
		this.timeoutExceeded = false;
		this.numberOfVariables = numberOfVariables;
		this.numberOfClauses = numberOfClauses;
	}
	
	private Set<Rule> getSetOfRules(SatInstance<Formula> explanation,
			MultiMap<SatClause<Formula>, Rule> semanticConstraintsHoldToRules) {
		Set<Rule> result = new LinkedHashSet<Rule>();
		if (explanation != null && explanation.getSetOfClauses() != null) {
			for (SatClause<Formula> clause : explanation.getSetOfClauses()) {
				if (semanticConstraintsHoldToRules.containsKey(clause)) {
					result.addAll(semanticConstraintsHoldToRules.getAll(clause));
				}
			}
		}
		return result;
	}

	/**
	 * to be called when the sequent is unprovable
	 * @param counterModel
	 */
	private DecisionResult(PartialBiValuation counterModel, int numberOfVariables, int numberOfClauses) {
		this.provable = false;
		this.minimalSetOfRules = null;
		this.counterModel = counterModel;
		this.timeoutExceeded = false;
		this.numberOfVariables = numberOfVariables;
		this.numberOfClauses = numberOfClauses;
	}
	
	/**
	 * to be called when there was a timeout
	 */
	private DecisionResult(int numberOfVariables, int numberOfClauses) {
		this.provable = true;
		minimalSetOfRules = null;
		this.counterModel = null;
		this.timeoutExceeded = true;
		this.numberOfVariables = numberOfVariables;
		this.numberOfClauses = numberOfClauses;
	}

	public boolean isProvable() {
		return provable;
	}


	public PartialBiValuation getCounterModel() {
		return counterModel;
	}
	
	public static DecisionResult createProvableDecisionResult(SatInstance<Formula> satInstance, MultiMap<SatClause<Formula>, Rule> semanticConstraintsHoldToRules, int numberOfVariables, int numberOfClauses) {
		return new DecisionResult(satInstance, semanticConstraintsHoldToRules, numberOfVariables, numberOfClauses);
	}
	
	public static DecisionResult createUnprovableDecisionResult(PartialBiValuation counterModel, int numberOfVariables, int numberOfClauses) {
		return new DecisionResult(counterModel, numberOfVariables, numberOfClauses);
	}
	
	public static DecisionResult createTimeoutDecisionResult(int numberOfVariables, int numberOfClauses) {
		return new DecisionResult(numberOfVariables, numberOfClauses);
	}

	public boolean isTimeoutExceeded() {
		return timeoutExceeded;
	}

	public Set<Rule> getMinimalSetOfRules() {
		return minimalSetOfRules;
	}

	@Override
	public String toString() {
		return "DecisionResult [provable=" + provable + ", minimalSetOfRules="
				+ minimalSetOfRules + ", counterModel=" + counterModel
				+ ", timeoutExceeded=" + timeoutExceeded + "]";
	}
	public int getNumberOfVariables() {
		return numberOfVariables;
	}

	
	public int getNumberOfClauses() {
		return numberOfClauses;
	}


}
