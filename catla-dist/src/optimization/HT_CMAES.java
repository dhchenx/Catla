package cn.edu.bjtu.cdh.catla.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

public class HT_CMAES implements ITaskOptimizer{

	private double timeCost=24*3600*1000;
	
	private double expectedValue=0.0;
	private int diagonalOnly=0;
	private double stopValue=1e-13;
	private double fTol=1e-13;
	private double pointTol=1e-6;
	private int maxEval=1000;
	
	public double getTimeCost() {
		return timeCost;
	}

	public void setTimeCost(double timeCost) {
		this.timeCost = timeCost;
	}

	public double getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(double expectedValue) {
		this.expectedValue = expectedValue;
	}

	public double getDiagonalOnly() {
		return diagonalOnly;
	}

	public void setDiagonalOnly(int diagonalOnly) {
		this.diagonalOnly = diagonalOnly;
	}

	public double getStopValue() {
		return stopValue;
	}

	public void setStopValue(double stopValue) {
		this.stopValue = stopValue;
	}

	public double getfTol() {
		return fTol;
	}

	public void setfTol(double fTol) {
		this.fTol = fTol;
	}

	public double getPointTol() {
		return pointTol;
	}

	public void setPointTol(double pointTol) {
		this.pointTol = pointTol;
	}
	
	private int checkFeasiblePoint=0;
	
	private double[] insigma=new double[] {10,0.1};

	@Override
	public double[] optimize(MultivariateFunction func,int maxInteration,double[] initValues,double[] lowerBounds,double[] upperBounds) {
		// TODO Auto-generated method stub
		 int DIM=initValues.length;
		
		 int LAMBDA = 4 + (int) (3. * FastMath.log(DIM));

		 
		double[][] boundaries = new double[][] { lowerBounds, upperBounds };
		
		
		PointValuePair expected = new PointValuePair(initValues, this.expectedValue);

		double[] result=doTest(func, initValues, this.insigma, boundaries, GoalType.MINIMIZE, LAMBDA, true, this.diagonalOnly, this.stopValue, 
				this.fTol, this.pointTol,
				this.maxEval, expected,maxInteration);
	 
		return result;
	}
	
	/**
	 * @param func           Function to optimize.
	 * @param startPoint     Starting point.
	 * @param inSigma        Individual input sigma.
	 * @param boundaries     Upper / lower point limit.
	 * @param goal           Minimization or maximization.
	 * @param lambda         Population size used for offspring.
	 * @param isActive       Covariance update mechanism.
	 * @param diagonalOnly   Simplified covariance update.
	 * @param stopValue      Termination criteria for optimization.
	 * @param fTol           Tolerance relative error on the objective function.
	 * @param pointTol       Tolerance for checking that the optimum is correct.
	 * @param maxEvaluations Maximum number of evaluations.
	 * @param expected       Expected point / value.
	 */
	private double[] doTest(MultivariateFunction func, double[] startPoint, double[] inSigma, double[][] boundaries,
			GoalType goal, int lambda, boolean isActive, int diagonalOnly, double stopValue, double fTol,
			double pointTol, int maxEvaluations, PointValuePair expected, int maxInteractions) {
		int dim = startPoint.length;

		RandomGenerator generator = new JDKRandomGenerator();

		// test diagonalOnly = 0 - slow but normally fewer feval#
		CMAESOptimizer optim = new CMAESOptimizer(maxInteractions, stopValue, isActive, diagonalOnly, this.checkFeasiblePoint, generator, true, null);

		PointValuePair result = boundaries == null
				? optim.optimize(new MaxEval(maxEvaluations), new ObjectiveFunction(func), goal,
						new InitialGuess(startPoint), SimpleBounds.unbounded(dim), new CMAESOptimizer.Sigma(inSigma),
						new CMAESOptimizer.PopulationSize(lambda))
				: optim.optimize(new MaxEval(maxEvaluations), new ObjectiveFunction(func), goal,
						new SimpleBounds(boundaries[0], boundaries[1]), new InitialGuess(startPoint),
						new CMAESOptimizer.Sigma(inSigma), new CMAESOptimizer.PopulationSize(lambda));

		// System.out.println("sol=" + Arrays.toString(result.getPoint()));
		System.out.println();
		System.out.println("Expected Value:" + expected.getValue());
		System.out.println("Objective Value:" + result.getValue());
		System.out.println("Ftol Value:" + fTol);

		System.out.println();
		System.out.println("ExptectedValue\tActualValue\tTol");
		for (int i = 0; i < dim; i++) {
			System.out.println(expected.getPoint()[i] + "\t" + result.getPoint()[i] + "\t" + pointTol);
		}
		
		timeCost=result.getValue();
		
		return result.getPoint();

	}

	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return timeCost;
	}

	public double[] getInsigma() {
		return insigma;
	}

	public void setInsigma(double[] insigma) {
		this.insigma = insigma;
	}

	public int getCheckFeasiblePoint() {
		return checkFeasiblePoint;
	}

	public void setCheckFeasiblePoint(int checkFeasiblePoint) {
		this.checkFeasiblePoint = checkFeasiblePoint;
	}

	public int getMaxEval() {
		return maxEval;
	}

	public void setMaxEval(int maxEval) {
		this.maxEval = maxEval;
	}

 
}
