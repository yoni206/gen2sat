package utils;


import java.util.Collection;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.xplain.Xplain;


public class tmp {
	public static void main(String[] args) throws ContradictionException, TimeoutException {
//		ISolver solver = SolverFactory.newDefault();
//		int[] c1 = {1,2,3};
//		int[] c2 = {-1};
//		int[] c3 = {-2};
//		int[] c4 = {-3};
//		solver.addClause(new VecInt(c1));
//		solver.addClause(new VecInt(c2));
//		solver.addClause(new VecInt(c3));
//		solver.addClause(new VecInt(c4));
//		if (!solver.isSatisfiable()) {
//			System.out.println(solver.unsatExplanation());
//		}
		
	    Xplain<ISolver> solver = new Xplain<ISolver>(SolverFactory.newDefault());
        solver.newVar(3);
        IVecInt clause = new VecInt();
        clause.push(1).push(2);
        solver.addClause(clause);
        clause.clear();
        clause.push(1).push(-2);
        solver.addClause(clause);
        clause.clear();
        clause.push(-1).push(2);
        solver.addClause(clause);
        clause.clear();
        clause.push(-1).push(-2);
        solver.addClause(clause);
        clause.clear();
        clause.push(3);
        solver.addClause(clause);
        if (solver.isSatisfiable()) {
        	System.out.println("satisfiable");
        } else {        	
        	Collection<IConstr> explanation = solver.explain();
        	for (IConstr con : explanation) {
        		for (int i=0; i<con.size(); i++) {
        			int l = (con.get(i) % 2) == 0 ? con.get(i)/2 : (-1)*(con.get(i)-1)/2;
        			System.out.print(l + " ");
        		}
        		System.out.println();
        	}
        }
      
	}

}
