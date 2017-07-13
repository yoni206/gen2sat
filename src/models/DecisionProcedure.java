package models;

import java.util.ArrayList; 
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import utils.MultiMap;
import utils.TupleIterator;


public class DecisionProcedure {
	//natural members
	private SequentCalculus sequentCalculus;
	private DecisionProperties decisionProperties;
	
	//implementation internal members 
	private SequentCalculus axiomaticVersion;
	private MultiMap<Rule, Rule> axiomaticRuleToOriginalRules;
	
	
	
	
	public DecisionProcedure(SequentCalculus g, DecisionProperties decisionProperties) {
		sequentCalculus = g;
		this.decisionProperties = decisionProperties;
		createAxiomaticVersionAndMapping();
	}


	private void createAxiomaticVersionAndMapping() {
		Set<Rule> axiomaticRules = new LinkedHashSet<Rule>();
		axiomaticRuleToOriginalRules = new MultiMap<Rule, Rule>();
		
		for (Rule rule : sequentCalculus.getRules()) {
			Set<Rule> axiomaticVersionOfRule = rule.axiomize();
			axiomaticRules.addAll(axiomaticVersionOfRule);
			for (Rule axiomaticRule : axiomaticVersionOfRule) {
				axiomaticRuleToOriginalRules.put(axiomaticRule, rule);
			}
		}
		axiomaticVersion = new SequentCalculus(axiomaticRules, sequentCalculus.getAnalyticity());
	}


	public DecisionResult decide(Sequent s) {
		
		Set<Formula> locs = s.getLocalFormulas(axiomaticVersion.getAnalyticity());
		MultiMap<Rule, Sequent> constraintsByRules = getSemanticConstraints(locs);
		SatInstance<Formula> sIsFalse = satMinus(s);
		MultiMap<SatClause<Formula>, Rule> semanticConstraintsHoldToRules = generateSatOfConstraints(constraintsByRules);
		SatInstance<Formula> satInstance = new SatInstance<Formula>();
		satInstance.addClauses(semanticConstraintsHoldToRules.keySet()); 
		satInstance.addClauses(sIsFalse.getSetOfClauses());
		PartialBiValuation counterModel = null;
		SatInstance<Formula> explanation = null;
		DecisionResult result;
		try {
			satInstance.solve(decisionProperties);
			if (satInstance.isSatisfiable()) {
				if (decisionProperties.includeDetails()) {
					counterModel = getRefutingVal(satInstance);
				}
				result = DecisionResult.createUnprovableDecisionResult(counterModel, satInstance.getNumberOfVariables(), satInstance.getNumberOfClauses());
			} else {
				if (decisionProperties.includeDetails()) {
					explanation = satInstance.getExplanation();
				}
				result = DecisionResult.createProvableDecisionResult(explanation, semanticConstraintsHoldToRules, satInstance.getNumberOfVariables(), satInstance.getNumberOfClauses());
			}
		} catch (MyTimeoutException e) {
			result = DecisionResult.createTimeoutDecisionResult(satInstance.getNumberOfVariables(), satInstance.getNumberOfClauses()); 
		} catch (MyContradictionException e) {
			result = DecisionResult.createProvableDecisionResult(satInstance, semanticConstraintsHoldToRules, satInstance.getNumberOfVariables(), satInstance.getNumberOfClauses());
		}
		return result;
		
	}

	private PartialBiValuation getRefutingVal(SatInstance<Formula> satInstance) throws MyContradictionException, MyTimeoutException{
		Map<Formula, Boolean> counterModel = satInstance.getCounterModel(); 
		return new PartialBiValuation(counterModel);
	}
	



	private MultiMap<SatClause<Formula>, Rule> generateSatOfConstraints(MultiMap<Rule, Sequent> constraints) {
		MultiMap<SatClause<Formula>, Rule> result = new MultiMap<SatClause<Formula>, Rule>();
		for (Rule r : constraints.keySet()) {
			for (Sequent constraint : constraints.getAll(r)) {
					SatClause<Formula> clause = satPlus(constraint);
					result.put(clause, r);
			}
		}
		return result;
		
	}



	private SatClause<Formula> satPlus(Sequent s) {
		SatClause<Formula> clause = new SatClause<Formula>();
		for (Formula f : s.getLeftSide()) {
			clause.addNegativeLiteral(f);
		}
		for (Formula f: s.getRightSide()) {
			clause.addPositiveLiteral(f);
		}
		return clause;
	}



	private SatInstance<Formula> satMinus(Sequent s) {
		SatInstance<Formula> result = new SatInstance<Formula>();
		for (Formula f : s.getLeftSide()) {
			SatClause<Formula> clause = new SatClause<Formula>();
			clause.addPositiveLiteral(f);
			result.addClause(clause);
		}
		for (Formula f: s.getRightSide()) {
			SatClause<Formula> clause = new SatClause<Formula>();
			clause.addNegativeLiteral(f);
			result.addClause(clause);
		}
		return result;
	}
	

	private MultiMap<Rule, Sequent> getSemanticConstraints(Set<Formula> formulas) {
		MultiMap<Rule, Sequent> result = new MultiMap<Rule, Sequent>();
		//here this is an axiomatic calculus
		for (Rule r: axiomaticVersion.getRules()) {
			Set<Sequent> constraints = getConstraints(r, formulas);
			for (Rule originalRule : axiomaticRuleToOriginalRules.getAll(r)) {
				result.putAll(originalRule, constraints);
			}
		}
		return result;
	}
	
	private Set<Sequent> getConstraints(Rule r, Set<Formula> formulas) {
		Set<Sequent> result = new LinkedHashSet<Sequent>();
		int closure = axiomaticVersion.getRulesToClosingFormulas().get(r).size();
		if (formulas.size() >= closure) {					
			for (TupleIterator<Formula> iter = new TupleIterator<Formula>(formulas, closure); iter.hasNext();) {
				List<Formula> tuple = iter.next();
				//here i am relying on the fact that the rule don't have lonely atoms:
				Prefix tupleMaximalCommonPrefix= getMaximalCommonPrefix(tuple);
				List<Formula> trimmedTuple = trimPrefixFromTuple(tuple, tupleMaximalCommonPrefix);
				Substitution sigma = matchRuleToTuple(r, trimmedTuple, axiomaticVersion);
				if (sigma != null) {
					Sequent sigmadSequent = sigma.applyOn(r.getConclusion());
					Sequent prefixedSigmadSequent = sigmadSequent.addPrefix(tupleMaximalCommonPrefix); 
					result.add(prefixedSigmadSequent);
				}
			}
		}
		return result;
	}


	private Substitution matchRuleToTuple(Rule r, List<Formula> tuple, SequentCalculus calculus) {
		List<Formula> closingFormulas = axiomaticVersion.getRulesToClosingFormulas().get(r);
		Substitution sigma = Substitution.make(closingFormulas, tuple);
		return sigma;
	}

	private List<Formula> trimPrefixFromTuple(List<Formula> tuple,
			Prefix prefix) {
		List<Formula> result = new ArrayList<Formula>();
		for (Formula f: tuple) {
			result.add(f.trim(prefix));
		}
		return result;
	}

	private Prefix getMaximalCommonPrefix(List<Formula> tuple) {
		Prefix maximalPrefOfFirstFormula = tuple.get(0).getMaximalPrefix();
		Prefix maximalCommonPrefix = new Prefix();
		for (int i=0; i<maximalPrefOfFirstFormula.getLength(); i++) {
			Prefix backup = new Prefix(maximalCommonPrefix.getPref());
			maximalCommonPrefix.append(maximalPrefOfFirstFormula.getOperator(i));
			if (!isCommon(tuple, maximalCommonPrefix)) {
				maximalCommonPrefix = backup;
				break;
			}
		}
		return maximalCommonPrefix;
		
	}
	
	private boolean isCommon(List<Formula> tuple, Prefix prefix) {
		boolean result = true;
		for (Formula f: tuple) {
			if (!f.hasPrefix(prefix)) {
				result = false;
				break;
			}
		}
		return result;
	}

}



/* TODO Validations:
 * -------------
 * next operators are not in the rules.
 * the conclusion cannot be empty.
 * Add all validations to command line utility
 * Add to the command line utility the possibility of multiple systems with ----- seperator.
 * 
 */


/*
 * TODO ideas
 * -------------
 * - Find out how to get a minimal set of rules. maybe change the order of the input to the solver.
 * - Split the input sequent to two text boxes
 * - Make a developer mode and visual mode, with better visualisation of input of rules.
 * */
