package controllers;


import java.io.IOException; 
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import models.DecisionProcedure;
import models.DecisionProperties;
import models.DecisionResult;
import models.Language;
import models.Sequent;
import models.SequentCalculus;


import utils.FileUtilsInternal;
import utils.MultiMap;
import utils.StringUtils;

public class CmdApplication {


	public static void main(String[] args) throws IOException {
		//new StaticApplication(new File("")).application(); 
		if (args.length == 0) {
			printHelp();
			return;
		}
		String path = args[0];
		runFromFile(path);
		System.out.println();
	} 

	private static void printHelp() throws IOException {
		System.out.println("Welcome to gen2sat");
		System.out.println("Usage: java -jar gen2sat.jar <path to file>");
		System.out.println("path to file: the path to the file that contains the problem description");
		System.out.println("An example of such a file can be found here: http://delta-tomcat-vm.cs.tau.ac.il:40123/gen2sat/assets/LK.txt ");
	}

	private static void runFromFile(String path) throws IOException {
		String stringOfFile = FileUtilsInternal.getFileAsString(path);
		MultiMap<String, String> lines = StringUtils.getLinesAsMultiMap(stringOfFile, ":");
		DecisionProperties decisionProperties = DecisionProperties.makeInstance(stringOfFile);
		Language l = Language.makeInstance(lines.getSome(""), lines.getSome("connectives"), lines.getSome("nextOperators"));
		String rules = "";
		if (lines.containsKey("rule")) {
			for (String rule : lines.getAll("rule")) {
				rules += "\n" + rule;
			}
		}
		SequentCalculus G = SequentCalculus.makeInstance(rules, l, lines.getSome("analyticity"));
		DecisionProcedure d = new DecisionProcedure(G, decisionProperties);
		SortedMap<String, String> inputSequents = getSequentsStrings(l, stringOfFile);
		System.out.println(";result;time in ms;#vars;#clauses");
		for (String name : inputSequents.keySet()) {
			long startTime = System.currentTimeMillis();
			Sequent s = Sequent.makeInstance(inputSequents.get(name), l);
			DecisionResult decisionResult = decideOnSequent(l, G, d, s);
			long endTime = System.currentTimeMillis();
			String provableOrUnprovableOrTimeout = "timeout";
			if (!decisionResult.isTimeoutExceeded()) {
				if (decisionResult.isProvable()) {
					provableOrUnprovableOrTimeout = "provable";
				} else {
					provableOrUnprovableOrTimeout = "unprovable";
				}
			}
			String line = (!StringUtils.isEmpty(name)  ? name  + ";" : "") + provableOrUnprovableOrTimeout + ";" + (endTime - startTime)   + ";"  +decisionResult.getNumberOfVariables() + ";"  + decisionResult.getNumberOfClauses(); 
			System.out.println(line);
			if ((decisionResult.isProvable() && decisionProperties.includeDetails())) {
				System.out.println("There is a proof that uses only these rules: " + 
						decisionResult.getMinimalSetOfRules().toString());
			}
			if (!decisionResult.isProvable() && decisionProperties.includeDetails()) {
				System.out.println("Countermodel: " + 
						decisionResult.getCounterModel().toString());
			}
		}
	}



	private static SortedMap<String, String> getSequentsStrings(Language l, String stringOfSystem) {
		SortedMap<String,String> result = new TreeMap<String, String>();
		List<String> lines = StringUtils.splitAndTrimToList(stringOfSystem, "\n");
		for (String line: lines) {
			String type = line.substring(0, line.indexOf(':'));
			String value = line.substring(line.indexOf(':')+1);
			type = type.trim();
			value = value.trim();
			if (type.contains("inputSequent")) {
				String name = "";
				if (type.contains("-")) {
					name = type.substring(type.indexOf('-')+1, type.length());
				}
				name = name.trim();
				result.put(name, value);
			}
		}
		return result;
	}

	private static DecisionResult decideOnSequent(Language l, SequentCalculus G, DecisionProcedure d, Sequent s)  {
		DecisionResult decisionResult = null;
		decisionResult = d.decide(s);
		return decisionResult;
	}


}
