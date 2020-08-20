package cn.edu.bjtu.cdh.catla.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;

public class HT_Simplex implements ITaskOptimizer{

	private double timeCost=24*3600*1000;
	
	private double rel=1e-2;
	
	private double abs=1e-4;
	
	@Override
	public double[] optimize(MultivariateFunction func,int maxInteration,double[] initValues,double[] lowerBounds,double[] upperBounds) {
		// TODO Auto-generated method stub
		
		
		 SimplexOptimizer optimizer = new SimplexOptimizer(this.rel, this.abs);
	       
	        final PointValuePair optimum =
	            optimizer.optimize(
	                new MaxEval(maxInteration), 
	                new ObjectiveFunction(func), 
	                GoalType.MINIMIZE, 
	                new InitialGuess(initValues), 
	                new NelderMeadSimplex(initValues.length),
	                new SimpleBounds(lowerBounds,
	                        upperBounds)
	            		);
	        
	    timeCost=optimum.getValue();
	
		return optimum.getPoint();
	}

	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return timeCost;
	}

	public double getRel() {
		return rel;
	}

	public void setRel(double rel) {
		this.rel = rel;
	}

	public double getAbs() {
		return abs;
	}

	public void setAbs(double abs) {
		this.abs = abs;
	}

}
