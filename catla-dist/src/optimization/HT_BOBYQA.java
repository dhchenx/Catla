package cn.edu.bjtu.cdh.catla.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;

public class HT_BOBYQA implements ITaskOptimizer{

	private double timeCost=24*3600*1000;
	
	private double initialTrustRegionRadius=10;
	private double stoppingTrustRegionRadius=1.0E-8;
	
	@Override
	public double[] optimize(MultivariateFunction func,int maxInteration,double[] initValues,double[] lowerBounds,double[] upperBounds) {
		// TODO Auto-generated method stub
		
		int dimension=initValues.length;
		
		BOBYQAOptimizer optimizer = new BOBYQAOptimizer(2*dimension+1,this.getInitialTrustRegionRadius(),this.getStoppingTrustRegionRadius()); // 2*point.length + 1+additional
		final PointValuePair optimum =
				optimizer.optimize(
					new MaxEval(maxInteration), 
	                new ObjectiveFunction(func), 
	                GoalType.MINIMIZE,
	                new InitialGuess(initValues),
	                new SimpleBounds(lowerBounds,
	                          upperBounds) 
						);

		double[] point = optimum.getPoint();
		
		System.out.print("point= ");
		for(int i=0; i< point.length; i++) 
			System.out.print("  "+ point[i]);
		System.out.println(" ");
		System.out.println("value = "+ optimum.getValue());
		this.timeCost=optimum.getValue();
		return point;
	}

	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return timeCost;
	}

	public double getInitialTrustRegionRadius() {
		return initialTrustRegionRadius;
	}

	public void setInitialTrustRegionRadius(double initialTrustRegionRadius) {
		this.initialTrustRegionRadius = initialTrustRegionRadius;
	}

	public double getStoppingTrustRegionRadius() {
		return stoppingTrustRegionRadius;
	}

	public void setStoppingTrustRegionRadius(double stoppingTrustRegionRadius) {
		this.stoppingTrustRegionRadius = stoppingTrustRegionRadius;
	}

}
