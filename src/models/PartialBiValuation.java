package models;

import java.util.Map;
import java.util.Set;

public class PartialBiValuation {
	Map<Formula, Boolean> map;

	public PartialBiValuation(Map<Formula, Boolean> map) {
		super();
		this.map = map;
	}

	public Map<Formula, Boolean> getMap() {
		return map;
	}

	public void setMap(Map<Formula, Boolean> map) {
		this.map = map;
	}
	
	public Set<Formula> getDomain() {
		return map.keySet();
	}
	
	public Boolean getValueFor(Formula f) {
		return map.get(f);
	}
	
	@Override
	public String toString() {
		String result = map.toString();
		result = result.substring(1, result.length()-1);
		return result;
	}
	
	
	
}
