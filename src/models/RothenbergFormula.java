package models;

public class RothenbergFormula {
	Formula formula;
	int rothenbergType;
	int rothenbergSize;
	boolean provable;
	
	public RothenbergFormula(Formula formula, int rothenbergType, int rothenbergSize, boolean provable) {
		super();
		this.formula = formula;
		this.rothenbergType = rothenbergType;
		this.rothenbergSize = rothenbergSize;
		this.provable = provable;
	}
	
}
