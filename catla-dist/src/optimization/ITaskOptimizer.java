package cn.edu.bjtu.cdh.catla.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;

public interface ITaskOptimizer {

	public double[] optimize(MultivariateFunction func,int maxInteration,double[] initValues,double[] lowerBounds,double[] upperBounds);
	
	public double getCost();

}
