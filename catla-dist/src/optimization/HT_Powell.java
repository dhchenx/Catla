package cn.edu.bjtu.cdh.catla.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;

public class HT_Powell implements ITaskOptimizer {

	private double rel=1e-4;
	private double abs=1e-4;
	private double lineRel=1e-4;
	private double lineAbs=1e-4;
	
	private double timeCost=24*3600*1000;
	
	public HT_Powell(double rel,double abs,double lrel,double labs) {
		this.rel=rel;
		this.abs=abs;
		this.lineRel=lrel;
		this.lineAbs=labs;
	}
	
	@Override
	public double[] optimize(MultivariateFunction func, int maxInteration, double[] initValues, double[] lowerBounds,
			double[] upperBounds) {
		// TODO Auto-generated method stub

		 
		InitialGuess start_values = new InitialGuess(initValues);
		PowellOptimizer optimizer = new PowellOptimizer(this.rel,this.abs
				,this.lineRel,this.lineAbs
				);

		ObjectiveFunction obj = new ObjectiveFunction(func);
		
		try {
			PointValuePair result = optimizer.optimize(obj, GoalType.MINIMIZE, start_values, new MaxEval(maxInteration),
					  new SimpleBounds(lowerBounds,upperBounds) 
					);
			
			double[] point = result.getPoint();
			
			System.out.print("point= ");
			for(int i=0; i< point.length; i++) 
				System.out.print("  "+ point[i]);
			System.out.println(" ");
			System.out.println("value = "+ result.getValue());
			this.timeCost=result.getValue();
			
			
			
			return point;

		} catch (TooManyEvaluationsException e) {
			e.printStackTrace();

		}
		
		return null;
		 
	}

	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return timeCost;
	}

}
