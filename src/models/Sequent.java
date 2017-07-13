package models;

import java.util.LinkedHashSet;
import java.util.Set;

import utils.StringUtils;

public class Sequent {
	private Set<Formula> leftSide;
	private Set<Formula> rightSide;
	
	public Sequent(Set<Formula> leftSide, Set<Formula> rightSide) {
		leftSide.remove(null);
		rightSide.remove(null);
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}

	public Sequent() {
		this(new LinkedHashSet<Formula>(), new LinkedHashSet<Formula>());
	}

	public Set<Formula> getLeftSide() {
		return leftSide;
	}

	public void setLeftSide(Set<Formula> leftSide) {
		leftSide.remove(null);
		this.leftSide = leftSide;
	}

	public Set<Formula> getRightSide() {
		return rightSide;
	}

	public void setRightSide(Set<Formula> rightSide) {
		rightSide.remove(null);
		this.rightSide = rightSide;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((leftSide == null) ? 0 : leftSide.hashCode());
		result = prime * result
				+ ((rightSide == null) ? 0 : rightSide.hashCode());
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
		Sequent other = (Sequent) obj;
		if (leftSide == null) {
			if (other.leftSide != null)
				return false;
		} else if (!leftSide.equals(other.leftSide))
			return false;
		if (rightSide == null) {
			if (other.rightSide != null)
				return false;
		} else if (!rightSide.equals(other.rightSide))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return leftSide.toString().replaceAll("[\\[\\]]","") + "=>"+rightSide.toString().replaceAll("[\\[\\]]","");
	}

	public static Sequent makeInstance(String sequentStr, Language L) {
		String leftAndRight[];
		if (sequentStr.contains("=>")) {			
			leftAndRight = sequentStr.split("=>");
		} else {
			leftAndRight = sequentStr.split("\\\\Rightarrow");
		}
		String leftString = "";
		String rightString = "";
		
		if (leftAndRight.length >= 1) {			
			leftString = leftAndRight[0];
		}
		if (leftAndRight.length >=2) {
			rightString = leftAndRight[1];
		}
		
		Set<String> leftStrings = StringUtils.splitAndTrimBalanced(leftString, ",");
		Set<String> rightStrings = StringUtils.splitAndTrimBalanced(rightString, ",");
		
		Set<Formula> left = new LinkedHashSet<Formula>();
		for (String str : leftStrings) {
			left.add(Formula.makeInstance(str, L));
		}
		
		Set<Formula> right = new LinkedHashSet<Formula>();
		for (String str : rightStrings) {
			right.add(Formula.makeInstance(str, L));
		}
		
		Sequent result = new Sequent(left, right);
		return result;
	}

	public Set<Formula> getLocalFormulas(Set<PropositionalConnective> unaryConnectives) {
		Set<Formula> result = new LinkedHashSet<Formula>();
		for (Formula f : this.getFormulas()) {
			result.addAll(f.getLocalFormulas(unaryConnectives));
		}
		return result;
	}

	public Set<Formula> getFormulas() {
		Set<Formula> result = new LinkedHashSet<Formula>();
		for (Formula f: this.getLeftSide()) {
			result.add(f);
		}
		for (Formula f: this.getRightSide()) {
			result.add(f);
		}
		return result;
	}
	
	public Sequent reverse() {
		return new Sequent(this.getRightSide(), this.getLeftSide());
	}
	
	public Sequent union(Sequent component) {
		Set<Formula> left = new LinkedHashSet<Formula>();
		Set<Formula> right = new LinkedHashSet<Formula>();
		left.addAll(component.getLeftSide());
		left.addAll(this.getLeftSide());
		right.addAll(component.getRightSide());
		right.addAll(this.getRightSide());
		return new Sequent(left, right);
	}

	public Sequent addPrefix(Prefix prefix) {
		Set<Formula> left = new LinkedHashSet<Formula>();
		Set<Formula> right = new LinkedHashSet<Formula>();
		for (Formula f : this.leftSide) {
			left.add(f.prefixBy(prefix));
		}
		for (Formula f : this.rightSide) {
			right.add(f.prefixBy(prefix));
		}
		return new Sequent(left, right);
	}
	
	public static void main(String[] args) {
		if (args == null || args.length ==0) {
			System.out.println("Input - a sequent in the language &|->~=>. Output: the same sequent, with good parenthesis.");
		} else {
			Language L = Language.makeInstance("", "&:2, |:2, ->:2,!:1", "");
			String sequentStr = args[0];
			Sequent sequent = Sequent.makeInstance(sequentStr, L);
			System.out.println(sequent);	
		}
	}
	
	
	
}
