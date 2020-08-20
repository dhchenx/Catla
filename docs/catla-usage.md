## Key usage
### (1) Submit a MapReduce job
Submit a MapReduce job within a Java library (jar file) to Hadoop cluster and obtain Hadoop log files and output results after job completion. <br/>

Example:
<code>java -jar Catla.jar -tool project -dir /your-example-folder/project_wordcount -task pipeline -download true -sequence true</code>

Help of arguments:<br/>
<b>-dir</b> (required)	The path of project folder that contains '_hproj.txt' file<br/>
<b>-download</b> (optional)	Download the results when the task is finished! value: true or false (default)<br/>
<b>-sequence</b> (optional)	Determine if submitting at the same time or in a sequence for multiple jobs. value: true (in Sequence) or false (default)<br/>
<b>-task</b> (required)	Some options: uploadjar, submit, uploadhdfs, pipeline<br/>
<b>-master</b> (optional=the first env_ file)	Specify the target Hadoop environment file when there are multiple env_* files in the folder<br/>

### (2) Exhaustive Search for tuning
Tuning performance of MapReduce jobs using exhaustive search, which means the system tries all combinations of parameter values to test the job and obtain a summary of time cost vs. parameter values after the tuning process is finished. 

Example: 
<code>java -jar Catla.jar -tool tuning -dir /your-example-folder/tuning_wordcount -clean true -group wordcount -upload true -uploadjar true</code>

Help of arguments:<br/>
<b>-dir</b> (required)	The path of project folder that contains '_hproj.txt' file<br/>
<b>-clean</b> (optional) indicate whether delete history, logs, outputs, and progress folder before running, value: true or false (default). <br/>
<b>-group</b> (optional) specify the prefix of name of summary files after job completion when the tuning process is finished. The files will be located in /history folder after job completion. <br/>
<b>-upload</b> (optional) indicate whether upload data from /inputs folder, value: true or false (default)<br/>
<b>-uploadjar</b> (optional) indicate whether upload data from /jars folder, value: true or false (default)<br/>
<b>-continue</b> (optional) indicate whether the tuning process continues the unfinished process last time by loading the process in the past. value: true or false (default)<br/>
<b>-params</b> (optional) specify the list of used parameters from the parameter configuration files from /tuning folder during the tuning process. If not specified, the system will use the file located in the path of /tuning/current.txt. <br/>

### (3) DFO-based methods for tuning
Tuning performance of MapReduce jobs using derivative-free optimization methods, currently supporting BOBYQA, Powell’s method, CMA-ES and the Simplex methods. <br/>
#### 1) Example of BOBYQA-based tuning process
<code>java -jar Catla.jar -tool optimizer -dir /your-example-folder/tuning_wordcount -clean true -group wordcount -upload true -uploadjar true -maxinter 100 -optimizer BOBYQA -BOBYQA-initTRR 50 -BOBYQA-stopTRR 1.0E-4</code>

Details of optimizer arguments can see: https://commons.apache.org/proper/commons-math/javadocs/api-3.3/org/apache/commons/math3/optim/nonlinear/scalar/noderiv/BOBYQAOptimizer.html

#### 2) Example of Powell-based tuning process
<code>java -jar Catla.jar -tool optimizer -dir /your-example-folder/tuning_wordcount -clean true -group wordcount -upload true -uploadjar true -maxinter 100 -optimizer Powell -powell-rel 1e-4 -powell-abs 1e-4</code>

Details of optimizer arguments can see: https://commons.apache.org/proper/commons-math/javadocs/api-3.6/org/apache/commons/math3/optimization/direct/PowellOptimizer.html 

#### 3) Example of CMAES-based tuning process
<code>java -jar Catla.jar -tool optimizer -dir /your-example-folder/tuning_wordcount -clean true -group wordcount -upload true -uploadjar true -maxinter 100 -optimizer CMAES -cmaes-sigma 20,0.4 -cmaes-ftol 10 -cmaes-pointtol 1e-1 -cmaes-expectedvalue 23 -cmaes-diagnoalonly 0 -cmaes-stopvalue 23 -cmaes-checkfeasiblepoint 0 -cmaes-maxeval 3000</code>

Details of optimizer arguments can see: http://commons.apache.org/proper/commons-math/javadocs/api-3.4/org/apache/commons/math3/optim/nonlinear/scalar/noderiv/CMAESOptimizer.html 

#### 4) Example of Simplex-based tuning process
<code>java -jar Catla.jar -tool optimizer -dir /your-example-folder/tuning_wordcount -clean true -group wordcount -upload true -uploadjar true -maxinter 100 -optimizer Simplex -simplex-rel 1e-4 -simplex-abs 1e-4</code>

Details of optimizer arguments can see: https://commons.apache.org/proper/commons-math/javadocs/api-3.1/org/apache/commons/math3/optim/nonlinear/scalar/noderiv/NelderMeadSimplex.html 

### (4) Aggregate logs from the tuning results
When the tuning process is stopped in the middle, the log aggregation is not finished. Therefore, you can start this command to re-aggregate existing logs from /history folder. 

<code>
<code>java -jar Catla.jar -tool log -dir /your-example-folder/tuning_wordcount</code>
</code>
 
### (5) Analyzing Results
After job completion, the summaries of job metrics are located in the sub folder “/history” of the project root folder that you run. Then, you can visualize the results from the information of *.csv files in the history folder by using statistics software such as Minitab and MATLAB.<br/> 