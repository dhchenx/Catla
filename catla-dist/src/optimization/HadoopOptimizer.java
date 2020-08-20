package cn.edu.bjtu.cdh.catla.optimization;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.analysis.MultivariateFunction;

import cn.edu.bjtu.cdh.catla.task.HadoopLog;
import cn.edu.bjtu.cdh.catla.task.HadoopProject;
import cn.edu.bjtu.cdh.catla.task.HadoopTask;

import cn.edu.bjtu.cdh.catla.tuning.HadoopTuning;
import cn.edu.bjtu.cdh.catla.tuning.TuningLog;
import cn.edu.bjtu.cdh.catla.tuning.TuningParameter;

public class HadoopOptimizer {
	
	public static class HadoopTimeCostFunction implements MultivariateFunction {
		private List<TuningParameter> parameters=null;
		private String[] useParameters=null;
		private double[] lowerBounds=null;
		private double[] upperBounds=null;
		public HadoopTimeCostFunction(List<TuningParameter> parameters,String[] useParameters,double[] lowerBounds,double[] upperBounds) {
			this.useParameters=useParameters;
			this.parameters=parameters;
			this.lowerBounds=lowerBounds;
			this.upperBounds=upperBounds;
		}
		
		public String getArgStr(double[] points) {
			String str="";
			for(int i=0;i<points.length;i++) {
				double value=points[i];
				
				//check lower bounds and upper bounds
				if(this.lowerBounds!=null&&this.upperBounds!=null) {
					if(!(value>=this.lowerBounds[i]&&value<=this.upperBounds[i])) {
						if(value<this.lowerBounds[i])
							value=this.lowerBounds[i];
						if(value>this.upperBounds[i])
							value=this.upperBounds[i];
					}
					
				}
				
				String new_value="";
				for(int j=0;j<this.parameters.size();j++) {
					if(this.parameters.get(j).getName().equals(useParameters[i])) {
						if(this.parameters.get(j).getNumberType().equals(TuningParameter.FLOAT)) {
							new_value=new DecimalFormat("#.00").format(value);
						}else 
							if (this.parameters.get(j).getNumberType().equals(TuningParameter.INT)) {
								new_value=new DecimalFormat("#").format(value);
							}
							else {
								new_value=value+"";
							}
					}
				}
				
				str+="-D "+useParameters[i]+"="+new_value+" ";
			}
			return str.trim();
		}

		@Override
		public double value(double[] arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	 static int iter_counter=0;
	
	public static void main(String[] args) throws Exception {
		
		if(args.length==0) {
		args = new String[] { 
				"-dir", "C:/Users/douglaschan/Desktop/大数据平台/tuning/tuning_wordcount",
				"-clean", "true", 
				"-group", "wordcount", 
				"-upload","false",
				"-uploadjar","false",
				"-maxinter","1000",
				"-optimizer","CMAES",
				"-BOBYQA-initTRR","20",
				"-BOBYQA-stopTRR","1.0E-4",
			};
		}

		URL rootUrl = HadoopTask.class.getProtectionDomain().getCodeSource().getLocation();
		String jarFolder = URLDecoder.decode(rootUrl.getPath(), "utf-8");

		if (jarFolder.endsWith(".jar")) {
			jarFolder = jarFolder.substring(0, jarFolder.lastIndexOf("/"));
			if (jarFolder.contains(":"))
				jarFolder = jarFolder.substring(1);

		} else {
			jarFolder = "";
		}

		Map<String, String> options = HadoopTuning.getOptionMap(args);

		String dirFolder = options.get("-dir");

		if (!jarFolder.isEmpty()) {
			if (!options.get("-dir").contains(":") && !options.get("-dir").startsWith("/")) {
				dirFolder = jarFolder + "/" + options.get("-dir");
			}
		}
		
		if (!new File(dirFolder).exists()) {
			System.out.println("Folder: " + dirFolder + " does not exist.");
			return;
		}
		
		if(!new File(dirFolder+"/_hproj.txt").exists()) {
			System.out.println("It is not a valid project folder!");
			return;
		}

		boolean is_clean=false;
		
		if(options.containsKey("-clean")) {
			
			is_clean=Boolean.parseBoolean(options.get("-clean"));
			
		}
		
	    boolean is_upload=true;
		if(options.containsKey("-upload"))
			is_upload=Boolean.parseBoolean(options.get("-upload"));
		 
		 boolean is_uploadjar=true;
			if(options.containsKey("-uploadjar"))
				is_uploadjar=Boolean.parseBoolean(options.get("-uploadjar"));
		
		final boolean hadoop_is_upload=is_upload;
		final boolean hadoop_is_uploadjar=is_uploadjar;
		
		String groupId="default";
		if(options.containsKey("-group"))
			groupId=options.get("-group");
		
		if(is_clean) {
			if(new File(dirFolder+"/logs").exists())
			FileUtils.deleteDirectory(new File(dirFolder+"/logs"));
			if(new File(dirFolder+"/history").exists())
			FileUtils.deleteDirectory(new File(dirFolder+"/history"));
			if(new File(dirFolder+"/outputs").exists())
			FileUtils.deleteDirectory(new File(dirFolder+"/outputs"));
			if(new File(dirFolder+"/progress").exists())
				FileUtils.deleteDirectory(new File(dirFolder+"/progress"));
		}
		
		String progressFolder=dirFolder+"/progress";
		if(!new File(progressFolder).exists())
			new File(progressFolder).mkdirs();
		
	
		//加载参数
		HadoopTuning htuning = new HadoopTuning(dirFolder);
		
		htuning.loadParameters();
		
		if(htuning.getAliasMap()!=null) {
			System.out.println("Alias Map for input parameter value: ");
			 for (Map.Entry<String,String> entry : htuning.getAliasMap().entrySet())  
			 {
				 System.out.println(entry.getKey()+"="+entry.getValue());
			 }
		}
		
		
		String quickTuningCofigPath=dirFolder+"/tuning/current.txt";
		String[] useParameters=new String[] {};
		
		if(options.containsKey("-params")) {
			useParameters=options.get("-params").split(";");
		 }else {
			 System.out.println("using param settings from tuning/current.txt");
				if(new File(quickTuningCofigPath).exists()) {
					List<String> lines=HadoopTuning.readFileByLines(quickTuningCofigPath);
					useParameters=new String[lines.size()];
					for(int i=0;i<lines.size();i++)
						useParameters[i]=lines.get(i);
				}
		 }
		

			TuningLog tl=new TuningLog(dirFolder,htuning.getAliasMap());
			
			String optimizer_name="BOBYQA";
			if(options.containsKey("-optimizer")) {
				optimizer_name=options.get("-optimizer");
			}
			
		
			ITaskOptimizer optimizer=HadoopTaskOptimizerFactory.getInstance(null, optimizer_name,options);
			
			if(optimizer==null)
			{
				System.out.println("no optimizer being set is found!");
				return;
			}

			int OPT_MAX_ITERATION=150;
			
			if(options.containsKey("-maxinter")){
				OPT_MAX_ITERATION=Integer.parseInt(options.get("-maxinter"));
			}

			double[] initValues=new double[useParameters.length];
			double[] lowerBounds=new double[useParameters.length];
			double[] upperBounds=new double[useParameters.length];
			
			for(int i=0;i<useParameters.length;i++) {
				for(int j=0;j<htuning.getParams().size();j++) {
					if(useParameters[i].equals(htuning.getParams().get(j).getName())) {
						initValues[i]=Double.parseDouble(htuning.getParams().get(j).getDefaultValue());
						lowerBounds[i]=htuning.getParams().get(j).getMin();
						upperBounds[i]=htuning.getParams().get(j).getMax();
					}
				}
			}
		
			iter_counter=0;
			
			final String projectFolder=dirFolder;
			
		
			final HadoopTimeCostFunction taskFunc = new HadoopTimeCostFunction(htuning.getParams(), useParameters,lowerBounds,upperBounds) {
				         @Override
				         public double value(double[] point) {
				        	 
				         
				        	 iter_counter++;
				        	 
				        	 System.out.println(iter_counter+", Points: "+Arrays.toString(point));
							        	
				        	 
				        	 String currentArgStr=getArgStr(point);
				        	 
				        	 System.out.println();
				        	 System.out.println("Current Parameters: "+currentArgStr);
				        	 
				        	 Map<String,String> currentParameters=htuning.obtainJobArgs(currentArgStr);
				        	 
				        	 for (Map.Entry<String,String> entry : currentParameters.entrySet())  
							 {
								 System.out.println("Using "+entry.getKey()+"="+entry.getValue());
							 }
				        	 
				        	 System.out.println();
				        	 
				        	 HadoopProject hp = HadoopProject.createInstance(new
									  File(projectFolder).getAbsolutePath(),currentParameters);
							
				        	 
							if(hadoop_is_upload)
								hp.callUpload2HDFS();
							
							if(hadoop_is_uploadjar)	  
								hp.callUploads();
							
				        	 
							boolean flag=hp.callSubmit(false, true);
							
							//record the running parameters
							if(hp.getRunningTraceId()!=null) {
								String historyFolder=hp.getRootFolder()+"/history/log-"+hp.getRunningTraceId();
								
								if(!new File(historyFolder).exists())
									new File(historyFolder).mkdirs();
								
								String historyRunningParameterPath=historyFolder+"/running_parameters.txt";
								
								HadoopTuning.writeFile(historyRunningParameterPath,currentArgStr);
								
								String iterRunningParameterPath=historyFolder+"/iteration_"+iter_counter;
								
								HadoopTuning.writeFile(iterRunningParameterPath,"");
								
							}
					
							 
							
							HadoopTuning.writeFile(progressFolder+"/running_number.txt",iter_counter+"");
							
							/*
							 * refresh logs from history folder in case the mutiple log is not refresh
							 * 
							 */
							
							HadoopLog hl=new HadoopLog(projectFolder);
							hl.refreshSingleLogFolder(projectFolder,hp.getRunningTraceId());
							
							///END
							
							long timeCost=tl.getTimeCost(hp.getRunningTraceId());

							System.out.println("----------TIME COST="+timeCost+"--------------");
							
						    if(timeCost<0)
						    	return 24*3600*1000;

				        	return timeCost;
				        	
				   }
			 };

			 try {
			
				 System.out.println("lowerBounds:");
				 System.out.println(Arrays.toString(lowerBounds));
				 System.out.println("upperBounds:");
				 System.out.println(Arrays.toString(upperBounds));
				
			 double[] bestValues=optimizer.optimize(taskFunc, OPT_MAX_ITERATION, initValues, lowerBounds, upperBounds);
				 
			 double timeCost=optimizer.getCost();
			 
			 System.out.println("Optimizer: best value: ");
			 for(int i=0;i<bestValues.length;i++) {
				 System.out.println(useParameters[i]+"="+bestValues[i]);
			 }
			 
			 System.out.println("Optimizer: optimized time cost: "+timeCost);
			 
			 }catch(Exception ex) {
				 ex.printStackTrace();
			 }
			

			//summarize...
			System.out.println("Summarizing the results...");
			
			
			tl.exportToCSV(groupId);
			
 
		System.out.println("Tuning Finished!");

		System.exit(0);
	}
}
