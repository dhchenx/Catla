package cn.edu.bjtu.cdh.catla.optimization;

import java.util.Map;

public class HadoopTaskOptimizerFactory {
	public static ITaskOptimizer getInstance(String jarLib,String mainClass,Map<String,String> opt_params) {
		ITaskOptimizer optimizer=null;
		if(jarLib==null) {
			if(mainClass.equals("BOBYQA")) {
				
				optimizer= new HT_BOBYQA();
				
				((HT_BOBYQA)optimizer).setInitialTrustRegionRadius(Double.parseDouble(opt_params.get("-BOBYQA-initTRR")));
				((HT_BOBYQA)optimizer).setStoppingTrustRegionRadius(Double.parseDouble(opt_params.get("-BOBYQA-stopTRR")));
				
				
			}else if (mainClass.equals("Powell")) {
				double rel=1e-4;
				double abs=1e-4;
				double lineRel=1e-4;
				double lineAbs=1e-4;
				
				if(opt_params.containsKey("-powell-rel")) 
					rel=Double.parseDouble(opt_params.get("-powell-rel"));
				if(opt_params.containsKey("-powell-abs")) 
					abs=Double.parseDouble(opt_params.get("-powell-abs"));
				
				if(opt_params.containsKey("-powell-lineRel")) 
					lineRel=Double.parseDouble(opt_params.get("-powell-lineRel"));
				if(opt_params.containsKey("-powell-lineAbs")) 
					lineAbs=Double.parseDouble(opt_params.get("-powell-lineAbs"));
				
				optimizer = new HT_Powell(rel,abs,lineRel,lineAbs);
				
			}else if (mainClass.equals("CMAES")) {
				
				optimizer=new HT_CMAES();
				if(opt_params.containsKey("-cmaes-sigma")) {
					String[] sigma=opt_params.get("-cmaes-sigma").split(",");
					double[] sv=new double[sigma.length];
					for(int i=0;i<sv.length;i++) {
						sv[i]=Double.parseDouble(sigma[i]);
					}
					
					((HT_CMAES)optimizer).setInsigma(sv);;
				}
				
				if(opt_params.containsKey("-cmaes-maxeval")) {
					((HT_CMAES)optimizer).setMaxEval(Integer.parseInt(opt_params.get("-cmaes-maxeval")));
				}
				
				if(opt_params.containsKey("-cmaes-checkfeasiblepoint")) {
					
					((HT_CMAES)optimizer).setfTol(Integer.parseInt(opt_params.get("-cmaes-checkfeasiblepoint")));
				}
				
				if(opt_params.containsKey("-cmaes-ftol")) {
					
					((HT_CMAES)optimizer).setfTol(Double.parseDouble(opt_params.get("-cmaes-ftol")));
				}
				
				if(opt_params.containsKey("-cmaes-pointtol")) {
					
					((HT_CMAES)optimizer).setPointTol(Double.parseDouble(opt_params.get("-cmaes-pointtol")));
				}
				
				if(opt_params.containsKey("-cmaes-expectedvalue")) {
					
					((HT_CMAES)optimizer).setExpectedValue(Double.parseDouble(opt_params.get("-cmaes-expectedvalue")));
				}
				
				if(opt_params.containsKey("-cmaes-diagonalonly")) {
					
					((HT_CMAES)optimizer).setDiagonalOnly(Integer.parseInt(opt_params.get("-cmaes-diagonalonly")));
				}
					
				if(opt_params.containsKey("-cmaes-stopvalue")) {
					
					((HT_CMAES)optimizer).setStopValue(Double.parseDouble(opt_params.get("-cmaes-stopvalue")));
				}
				
			}else if(mainClass.equals("Simplex")) {
				optimizer=new HT_Simplex();

				if(opt_params.containsKey("-simplex-rel")) 
					((HT_Simplex)optimizer).setRel(Double.parseDouble(opt_params.get("-simplex-rel")));
				if(opt_params.containsKey("-simplex-abs")) 
					((HT_Simplex)optimizer).setRel(Double.parseDouble(opt_params.get("-simplex-abs")));
				
			}else if (mainClass.equals("Random")) {
				optimizer=new HT_RandomSearch();
				if(opt_params.containsKey("-random-maxiter")) 
					((HT_RandomSearch)optimizer).setMaxInters(Integer.parseInt(opt_params.get("-random-maxiter")));
			}else if(mainClass.equals("Grid")) {
				optimizer=new HT_GridSearch(opt_params);
			 
			}
		}
		return optimizer;
	}
}
