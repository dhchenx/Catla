package cn.edu.bjtu.cdh.catla.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;

public class HT_RandomSearch implements ITaskOptimizer {

	private double[] generateRandomVector(double[] lowerBounds, double[] upperBounds) {
		double[] v=new double[lowerBounds.length];
		for(int i=0;i<v.length;i++) {
			v[i]=  (Math.random()*(upperBounds[i]-lowerBounds[i])+lowerBounds[i]);
		}
		return v;
	}
	
	private int maxInters=1000;
	
	private String toArrayStr(double[] v) {
		String s="(";
		for(int i=0;i<v.length;i++) {
			if(i!=v.length-1)
			s+=v[i]+",";
			else
				s+=v[i];
		}
		s+=")";
		return s;
	}

	private double innerCost;
	
	@Override
	public double[] optimize(MultivariateFunction func, int maxInterations, double[] initValues, double[] lowerBounds,
			double[] upperBounds) {
		// TODO Auto-generated method stub
		
		double bestSolution=Double.MAX_VALUE;
		double[] bestV=null;
		
		for(int iter=1;iter<=this.getMaxInters();iter++) {
			double[] v=generateRandomVector(lowerBounds,upperBounds);
			double currentObjValue=func.value(v);
			if(iter==1) {
				bestV=v;
				bestSolution=currentObjValue;
			}else {
			if(currentObjValue<bestSolution) {
				bestV=v;
				bestSolution=currentObjValue;
			}
			
			}
			this.innerCost=bestSolution;
			System.out.println("Iteration: "+(iter)+" Best solution: "+bestSolution);
			
		}
		
		System.out.println("\n\nBest Solution: "+toArrayStr(bestV)+" Objective Value: "+bestSolution);
		
		return bestV;
	}

	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return innerCost;
	}
	
	public static void main(String[] args) {
		
		MultivariateFunction func=	new MultivariateFunction() {
            @Override
            public double value(double[] vs) {
            	double x0=vs[0];
            	double x1=vs[1];
            	
            	return 100*Math.pow((x1-Math.pow(x0,2)),2)+Math.pow((1-x0),2) ;
            }
		};
		
		HT_RandomSearch trs=new HT_RandomSearch();
		double[] bestV=trs.optimize(func, 30000, new double[] {-1.2,1.0}, 
				new double[] {-10.0,-10.0}, new double[] { 0.9,1.0 });
		double bestCost=trs.getCost();
		
		
	}

	public int getMaxInters() {
		return maxInters;
	}

	public void setMaxInters(int maxInters) {
		this.maxInters = maxInters;
	}

}
