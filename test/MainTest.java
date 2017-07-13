import java.io.IOException;
import java.util.List;

import models.DecisionProcedure;
import models.DecisionProperties;
import models.DecisionResult;
import models.Language;
import models.Sequent;
import models.SequentCalculus;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import utils.FileUtilsInternal;
import utils.MultiMap;
import utils.StringUtils;


public class MainTest {
	
	public static void decideFromFile() throws ContradictionException, TimeoutException, IOException {
		//TODO bring back "data.txt"! 
		String testsDataString = FileUtilsInternal.getFileAsString("temp.txt");
		List<String> requiredTestsStrings = StringUtils.splitAndTrimToList(testsDataString, "\n\n");
		for (String requiredTest : requiredTestsStrings) {
			boolean pass;
			pass = runTest(requiredTest);
			if (! pass) {
				System.out.println("fail (" + requiredTest + ")");
			} else {
				System.out.println("pass");
			}
		}
	}

	private static boolean runTest(String requiredTest) throws ContradictionException, TimeoutException {
		MultiMap<String, String> lines = StringUtils.getLinesAsMultiMap(requiredTest, ":");
		//String sequent = getSequent(requiredTest);
		String sequent = lines.getSome("sequent");
		//String isDerivable = getIsDerivable(requiredTest);
		String isDerivable = lines.getSome("provable");
		boolean shouldBeDerivable = isDerivable.contains("true") ? true : false;
		Language l = Language.makeInstance(lines.getSome(""), lines.getSome("connectives"), lines.getSome("nextOperators"));
		SequentCalculus G = 
				SequentCalculus.makeInstance(
						StringUtils.getSeperatedList(lines.getAll("rule"), "\n"), 
						l, 
						lines.getSome("analyticity"));
		Sequent s = Sequent.makeInstance(sequent, l);
		DecisionProcedure d = new DecisionProcedure(G, new DecisionProperties(10, true));
		DecisionResult decisionResult = d.decide(s);
		boolean result = (shouldBeDerivable == decisionResult.isProvable());
		
		return result;
	}
	

	private static String getSequent(String requiredTest) {
		List<String> details = StringUtils.splitAndTrimToList(requiredTest, "\n");
		String sequentPart = details.get(1);
		List<String> splitedSequentPart = StringUtils.splitAndTrimBalancedToList(sequentPart, ":");
		String result = splitedSequentPart.get(1);
		return result;
	}

	private static String getIsDerivable(String requiredTest) {
		List<String> details = StringUtils.splitAndTrimToList(requiredTest, "\n");
		String result = StringUtils.splitAndTrimToList(details.get(2), ":").get(1);
		return result;
	}

	public static void main(String[] args) throws ContradictionException, TimeoutException, IOException {
		decideFromFile();
	}

}
