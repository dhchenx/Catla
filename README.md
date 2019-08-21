<img src="https://github.com/dhchenx/Catla/blob/master/images/catla-logo.jpg?raw=true" alt="Kitten"
	title="Catla's logo" width="100" height="40" /><br/>
# Catla
<b>Catla</b> is a self-tuning system for Hadoop parameters to improve performance of MapReduce jobs on Hadoop clusters. It is template-driven, making it very flexible to perform complicated job execution, monitering and self-tuning. 

## Core functions
1) <b>Task Runner:</b> To submit a single MapReduce job to a Hadoop cluster and obtain its analyzing results and logs after the job is completed. 
2) <b>Project Runner:</b> To submit a group of MapReduce jobs in an organized project folder and moniter the status of its running until completion; eventually, all analyzing results and their logs that contain informaiton of time cost in all phrases are downloaded to specified location in its project folder. 
3) <b>Optimizer Runner:</b> To create a series of MapReduece jobs with different combinations of parameter values according to parameter configuraion files and obtain the optimal parmater value sets with least time cost after the tuning process is finished. Two tuning process, namely exhaustive search and derivative-free optimization (DFO) techniques, are supported.

## Prerequisites
1) You should run Catla in a Windows computer located in <b>the same network</b> as Hadoop clusters. It means Catla is able to access the IP of master host.
2) Standard <b>Java environment</b> on the computer should be properly installed. 
3) Hadoop must enable <b>Yarn Log Aggregation</b> by setting value of 'yarn.log-aggregation-enable' to true.https://mapr.com/docs/51/AdministratorGuide/YARNLogAggregation-Enabli_28214137-d3e129.html 
4) Critical information of master host, like <b>username, userpassword, SSH port, etc.</b> must be known because Catala needs the information to run MapReduce jobs. 
5) You must <b>change the configuration of master host's information</b> in the env_* files in the example folder before you try to run any examples here. 
6) In your master host, please use 'sudo mkdir' command to create a new folder <b>/usr/hadoop_apps</b> in Ubuntu and change the folder's permission to every-one access. 

## Simple steps
1) Copy Catla.jar from 'catla-dist' to 'examples' folder, thus, the example folders and Catla.jar are in the same folder. 
3) Change master host's information within 'HadoopEnv.txt' according to your actual Hadoop cluster, such as master's IP, master's username, password, master port, Hadoo bin path, and root folder of App (the same as set in 6 of Prerequisties). 
2) Open a Windows CMD program, change current folder into the '/examples' folder by using 'CD' command
3) Simply Run the Java command as bellows: 
<code>
java -jar Catla.jar -tool task -dir task_wordcount
</code><br/>
4) After finished, the 'task_wordcount' folder should create a new folder 'downloaded_results' which stores the analzying result of WordCount MapReduce job. 

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

![Surface Plots of tuning MapReduce job performance](https://github.com/dhchenx/Catla/blob/master/images/catla-surfaceplot.jpg?raw=true)



