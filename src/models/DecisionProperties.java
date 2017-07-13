package models;

import utils.MultiMap;
import utils.StringUtils;

public class DecisionProperties {
	private int timeoutInSeconds;
	private boolean details;

	public DecisionProperties(int timeoutInSeconds, boolean details) {
		this.timeoutInSeconds = timeoutInSeconds;
		this.details = details;
	}
	public int getTimeoutInSeconds() {
		return timeoutInSeconds;
	}
	
	public boolean includeDetails() {
		return details;
	}
	
	public static DecisionProperties makeInstance(String stringOfFile) {
		MultiMap<String, String> multiMap = StringUtils.getLinesAsMultiMap(stringOfFile, ":");
		String timeoutInSecondsString = multiMap.containsKey("timeout") ? multiMap.getSome("timeout") : "";
		String detailsString = multiMap.containsKey("details") ? multiMap.getSome("details") : "";
		int timeout = timeoutInSecondsString != "" ? Integer.parseInt(timeoutInSecondsString) : 10;
		boolean details = detailsString.contains("false") ? false : true; 
		return new DecisionProperties(timeout, details);
	}
	
	
}
