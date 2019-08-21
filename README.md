<img src="https://github.com/dhchenx/Catla/blob/master/images/catla-logo.jpg?raw=true" alt="Kitten"
	title="Catla's logo" width="200" height="90" /><br/>
# Catla
<b>Catla</b> is a self-tuning system for Hadoop parameters to improve performance of MapReduce jobs on Hadoop clusters. It is template-driven, making it very flexible to perform complicated job execution, monitering and self-tuning. 

## Core functions
1) <b>Task Runner:</b> To submit a single MapReduce job to a Hadoop cluster and obtain its analyzing results and logs after the job is completed. 
2) <b>Project Runner:</b> To submit a group of MapReduce jobs in an organized project folder and moniter the status of its running until completion; eventually, all analyzing results and their logs that contain informaiton of time cost in all phrases are downloaded to specified location in its project folder. 
3) <b>Optimizer Runner:</b> To create a series of MapReduece jobs with different combinations of parameter values according to parameter configuraion files and obtain the optimal parmater value sets with least time cost after the tuning process is finished. Two tuning processes, namely exhaustive search and derivative-free optimization (DFO) techniques, are supported.

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
3) Simply Run the Java command as bellows: '<b>java -jar Catla.jar -tool task -dir task_wordcount</b>'. 
4) After finished, the 'task_wordcount' folder should create a new folder 'downloaded_results' which stores the analzying result of WordCount MapReduce job. 

## Visualization based on aggregation of time cost during the tuning process

![Surface Plots of tuning MapReduce job performance](https://github.com/dhchenx/Catla/blob/master/images/catla-surfaceplot.jpg?raw=true)

## Cite this project
<p>
D. Chen. Catla: A self-tuning system for Hadoop parameters to improve MapReduce job performance, 2019, GitHub repository, https://github.com/dhchenx/Catla
</p>



