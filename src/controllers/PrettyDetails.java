package controllers;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import models.DecisionProcedure;
import models.DecisionResult;
import models.Formula;
import models.Language;
import models.NextOperator;
import models.PartialBiValuation;
import models.PropositionalConnective;
import models.Rule;
import models.Sequent;
import models.SequentCalculus;
import utils.StringUtils;

public class PrettyDetails {
	public static String getCmdDetails(Language L, SequentCalculus G, Sequent s, 
			DecisionResult decisionResult, DecisionProcedure decisionProcedure, long elapsedTime) {
		String bivaluationPart = getBivaluationWithoutExplanation(decisionResult, ":");
		String timePart = getTimePart(elapsedTime, false);
		String content = 
						bivaluationPart + ": " + 
						timePart;
		return content;
		
	}
	
	
	public static String getDetails(Language L, SequentCalculus G, Sequent s, 
			DecisionResult decisionResult, long elapsedTime) {
		String languagePart = getLanguagePartString(L);
		String CalculusPart = getCalculusPartString(G, L);
		String sequentPart = getSequentPartString(s);
		String bivaluationPart = getBiValuationPartString(decisionResult, "<br>");
		String explanationPart = getExplanationPart(decisionResult, "<br>", G.getAnalyticity(), L);
		String timePart = getTimePart(elapsedTime, true);
		String content =
				"<h2>Detailed Result</h2><br><br>" +		
						languagePart + "<br><br>" +
						CalculusPart + "<br><br>" +
						sequentPart + "<br><br>" +
						bivaluationPart + "<br><br>" + 
						explanationPart + "<br><br>" +
						timePart;
		return content;
	}

	private static String getTimePart(long elapsedTime, boolean seconds) {
		if (seconds) {			
			return "Running time in seconds is: " + String.format("%s", (float)((float)elapsedTime / 1000));
		} else {
			return "Running time in miliseconds is: " + elapsedTime;
		}
	}

	private static String getExplanationPart(DecisionResult decisionResult, String delimiter, Set<PropositionalConnective> analyticity, Language l) {
			String result = "";
			Set<Rule> exp = decisionResult.getMinimalSetOfRules();		
//			//if there are  next operators, include just one generic next operator.
//			NextOperator genericNextOperator = new NextOperator("NEXT");
//			Set<NextOperator> genericNextOperators = new LinkedHashSet<NextOperator>();
//			genericNextOperators.add(genericNextOperator);
//			Set<NextOperator> nextOperatorsToPrint = l.hasNextOperators() ?  genericNextOperators : new LinkedHashSet<NextOperator>(); 
			Language lTag = new Language(l.getPropositionalVariables(), l.getPropositionalConnectives(), new LinkedHashSet<NextOperator>());
			String minimalSetOfRules = "";
			if (exp != null && !exp.isEmpty()) {
				minimalSetOfRules = getRulesString(new SequentCalculus(decisionResult.getMinimalSetOfRules(), analyticity), lTag);
				result = "There is a proof that uses a subset of the following rules (not including next operators): " + delimiter + 
						minimalSetOfRules; 
			}
			return result;

	}

	private static String getBiValuationPartString(DecisionResult  decisionResult, String delimiter) {
		String result = "<h4>Result: </h4>";
		result += getExplainedBivaluation(decisionResult, delimiter);
		return result;
	}

	private static String getExplainedBivaluation(
			DecisionResult decisionResult,
			String delimiter) {
		String result = "";
		if (decisionResult.isTimeoutExceeded()) {
			result += "The SAT Solver's time limit was reached";
		} else {			
			if (decisionResult.getCounterModel() == null) {
				result += "The sequent is provable. " ;
			} else {
				String counterModelString = getBivaluationString(decisionResult.getCounterModel(), delimiter);
				result += "The sequent is not provable." + delimiter +  " The following bivaluation is a countermodel:" +delimiter + counterModelString;
			}
		}
		return result;

	}
	
	private static String getBivaluationWithoutExplanation(DecisionResult decisionResult, String delimiter) {
		String result = "";
		if (decisionResult.isTimeoutExceeded()) {
			result += "time limit";
		} else {
			result += decisionResult.isProvable() ? "provable" : "unprovable";
		}
		return result;
	}


	private static String getBivaluationString(PartialBiValuation counterModel, String delimiter) {
		List<String> table = new ArrayList<String>();
		for (Formula f : counterModel.getDomain()) {
			table.add(StringUtils.mathMode(f.toString()) + " : " + counterModel.getValueFor(f));
		}
		return StringUtils.getSeperatedList(table, delimiter);

	}

	private static String getCalculusPartString(SequentCalculus G, Language l) {
		String rulesPart = getRulesString(G, l);
		String analyticityPart = getAnalyticityString(G);
		String result = 
				"<h4>Sequent Calculus:</h4><br>" + 
						rulesPart + "<br>" +
						analyticityPart;
		return result;
	}

	private static String getAnalyticityString(SequentCalculus G) {
		String result = "<b>Analyticity assumption: </b> The calculus is analytic ";
		if (G.getAnalyticity().size() == 0) {
			result += "(w.r.t. the empty set)";
		} else {
			result += "w.r.t. " +
					StringUtils.mathMode(
							"\\\\left\\\\{" +  
									StringUtils.getCommaSeperatedList(G.getAnalyticity()) +
							"\\\\right\\\\}");
		}
		return result;
	}

	private static String getRulesString(SequentCalculus G, Language l) {
		List<String> rulesStrings = getRulesStrings(G);
		List<String> nextOperatorsRulesStrings = getNextOpRulesStrings(l);
		//		List<List<String>> lines = CollectionUtils.divideToListsOfSize(rulesStrings, 4);
		//		List<List<String>> nextOpsLines = CollectionUtils.divideToListsOfSize(nextOperatorsRulesStrings, 4);
		//		lines.addAll(nextOpsLines);
		//		String linesInOneString = "";
		//		for (List<String> line : lines) {
		//			String lineInOneString = 
		//					StringUtils.displayMode(
		//									StringUtils.getSeperatedList(
		//													StringUtils.removeDollars(line), 
		//													"      "
		//												)
		//								);
		//			linesInOneString += lineInOneString + " ";
		//		}
		List<String> allRules = new ArrayList<String>();
		allRules.addAll(rulesStrings);
		allRules.addAll(nextOperatorsRulesStrings);
		List<String> allRulesWithDollars = new ArrayList<String>();
		for (String str : allRules) {
			String newStr = StringUtils.mathMode(str);
			allRulesWithDollars.add(newStr);
		}
		String linesInOneString = StringUtils.getSeperatedList(allRulesWithDollars, " ");
		String result = 
				"<b>Rules: </b>" +
						"<br>" + 
						"<center>" +
						"<p id=\"paragraphOfCalculus\">" + 
						linesInOneString + 
						"</p>" +
						"</center>"
						;

		return result;
	}

	private static List<String> getNextOpRulesStrings(Language l) {
		List <String> result = new ArrayList<String>();
		String gammaDelta = addGammaDelta("\\\\Rightarrow");
		String premise = gammaDelta;
		for (NextOperator n : l.getNextOperators()) {
			String conclusion = n.getSymbol() + " " + gammaDelta.replace("\\Rightarrow", "\\Rightarrow " + n.getSymbol());
			result.add(StringUtils.getDfrac(premise, conclusion));
		}
		return result;
	}

	private static List<String> getRulesStrings(SequentCalculus G) {
		List<String> result = new ArrayList<String>();
		for (Rule r : G.getRules()) {
			result.add(getRuleString(r));
		}
		return result;
	}

	private static String getRuleString(Rule r) {
		List<String> premisesList = getPremisesStrings(r);
		String premisesString = StringUtils.getSeperatedList(premisesList, "  ;  ");
		String conclusionString = addGammaDelta(getSequentString(r.getConclusion()));
		String result = StringUtils.getDfrac(premisesString, conclusionString);
		return result;
	}

	private static String getSequentString(Sequent s) {
		String left = StringUtils.getCommaSeperatedList(s.getLeftSide());
		String right = StringUtils.getCommaSeperatedList(s.getRightSide());
		return (left + " \\\\Rightarrow " + right);
	}

	private static String getSequentPartString(Sequent s) {
		String result = "<h4>The Sequent is: </h4>" + StringUtils.displayMode(getSequentString(s));
		return result;
	}

	private static List<String> getPremisesStrings(Rule r) {
		List<String> result = new ArrayList<String>();
		for (Sequent s : r.getPremises()) {
			result.add(addGammaDelta(getSequentString(s)));
		}
		return result;
	}

	private static String addGammaDelta(String sequentString) {
		String result = sequentString.trim();
		if (result.endsWith("=>") || result.endsWith("\\\\Rightarrow")) {
			result = result + "\\\\Delta";
		} else {
			result =  result +",\\\\Delta";
		}
		if (result.startsWith("=>") || result.startsWith("\\\\Rightarrow")) {
			result = "\\\\Gamma" + result;
		} else {
			result = "\\\\Gamma," + result;
		}
		return result;
	}

	private static String getLanguagePartString(Language L) {
		String propositionalConnectivesPart = getPropositionalConnectivesString(L);
		String nextOperatorsPart = getNextOperatorsString(L);
		String result = 
				"<h4>Language:</h4><br>" +
						//propositionalVariablesPart + "<br>" +
						propositionalConnectivesPart + "<br>" +
						nextOperatorsPart;
		return result;
	}

	private static String getNextOperatorsString(Language L) {
		String result = "";
		if (!L.getNextOperators().isEmpty()) {			
			String listOfNextOperators = StringUtils.mathMode(StringUtils.getCommaSeperatedList(L.getNextOperators()));
			result = "<b>Next Operators:</b> " + listOfNextOperators;
		}
		return result;
	}

	private static String getPropositionalConnectivesString(Language L) {
		List<String> iaryConnectives = new ArrayList<String>();
		for (int i=0; i<=L.getMaxArity(); i++) {
			Set<PropositionalConnective> csi = L.getConnectivesWithArity(i);
			if (!csi.isEmpty()) {
				iaryConnectives.add("<b>" + i + "-ary connectives: </b>" + StringUtils.mathMode(StringUtils.getCommaSeperatedList(csi)));
			}
		}
		return StringUtils.getSeperatedList(iaryConnectives, "<br>");

	}

	@SuppressWarnings("unused")
	private static String getPropositionalVariablesString(Language L) {
		String listOfVariables = StringUtils.getCommaSeperatedList(L.getPropositionalVariables());
		String result = "<b>Propositional Variables: </b>" + listOfVariables;
		return result;
	}


}
