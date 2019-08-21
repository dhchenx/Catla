# Document of templates used in Catla
This document illustate details of templates and rules to construt a tuning task or project for MapReduce jobs on Hadoop. Properly designing the configuration files of the runing project can improve efficiency of tuning on Hadoop. 
## Task-based template

The task-based template is the core of this self-tuning system since it abstracts important job operations from one simple job execution on Hadoop. Job operations includes uploading Java libraries of job, uploading necessary datasets to analyze in a job, starting job execution and setting Hadoop environments. HJM implementation is illustrated as the HadoopTaskRunner class in Figure 1 where HJM depends on HadoopEnv, HadoopJar and HadoopTask classes. The attributes and methods in these classes illustrate actual implementation in the system. 

Three main steps to perform a complete MapReduce job execution on Hadoop are required as follows. 
1)	Information of Hadoop environment, such as address of master host, user name, user password, Hadoop executable program and remote folder for Java libraries of MapReduce algorithms, is configured at the very beginning. 
2)	Information of an upload operation, such as remote path of Java libraries of MapReduce algorithms, an indicator on whether the operation should replace existing files, and local path of the libraries, is configured. 
3)	Information of MapReduce job, such Java libraries to execute, input arguments, HDFS folders of output results, local output folder if downloading is required and local folder if uploading is needed, are configured. 
4)	Steps (1)-(3) can be configured by text-based templates. Then, all the configuration files are put into the same folder which is passed to TaskRunner in Figure 1 to execute the sequence of job operations defined in Steps (1)-(3). After completion, the analyzing results are stored in the output folder defined in Step (3). 

## Project-based template
The project-based template is established on the task-based template, providing a novel organization of complicated job to execute and tune on Hadoop. The project-based template depends on HadoopTaskRunner and HadoopLog classes. This model provides a pipeline of MapReduce job to execute while maintaining the same tracking number which helps track and aggregate all metrics of job performance after completion of multiple jobs. The project-based template is also configured by a project folder-based template which contains all necessary job operations of HJM. In addition, new features based on this new organization in the project are also developed. Critical settings of the project is as follows. 
(1)	Inputs Folder
Input Folder organizes the same directory hierarchy of datasets to be uploaded as in the HDFS folders. Before job execution, the dataset files in this folder is automatically uploaded to HDFS on Hadoop, providing data sources for the job. 
(2)	Jars Folder
Jars Folder stores Java libraries (jar files) expected to upload to a master host before job execution. A main class to execute a job comes from the libraries that may contain more than one classes in general. A series of different jobs can be created for different analysis tasks. 
(3)	Outputs Folder
Outputs Folder stores the downloaded analyzing results of the submitted MapReduce job after job completion. The directory hierarchy of this folder remains the same as that in HDFS. 
(4)	Logs Folder
Logs Folder stores system log files of each finished job. It should be noted that the logs are not log files of job with metrics of performance but system-level logs. 
(5)	Project File
Project File stores basic information of a project including alias name of the project, descriptions of the project, the default value of aforementioned folders. 
(6)	Task File
Task File use a template to configure each job operation’s information, which currently supports three main job operations defined in HJM. 
(7)	Environment File
Environment File stores basic information of the target Hadoop cluster used by each Task File when executing jobs on a specific cluster when multiple clusters are available. Multiple environment files are naturally supported here. 

To sum up, the design of the project-based template enables the tuning system to execute multiple jobs on complicated Hadoop clusters and provided a way to self-manage all tuning parameters and configuration files, automatically download analyzing results and job logs in a very simple manner. 

## Tuning-based template
 Having the same directory hierarchy as the project-based template, the model has additional folder to support organization of tuning parameter’s information. 
1) Tuning Folder
Tuning Folder stores a group of configuration files of Hadoop parameters for tuning the MapReduce jobs where configured parameters are defined in a flexible way. Each configuration of parameter in the file has the name of parameter and its value sets as well as their type of value. Types of tuning parameters are summarized in Table 2. For example, the mapred.reduce.task parameter represents the number of reducers used in MapReduce, which can be set between 1 and 3 when the maximum number of data nodes in the cluster is 3. Therefore, a range with step length is configured as in [1,3]:1 where the system will produce 1, 2, and 3 for three different settings of this parameters in three jobs separately. For those parameters with finite number of non-numeric values, a symbol {}, which represents a set, is used to define such discrete values. During job execution, the tuning system automatically loads all configuration of parameters from these configuration files and create exhaustive pairs of all parameters in non-DFO tuning manner. However, determining what should be used to tune during the execution relies on another file named CURRENT.TXT, which store a part of parameters from the configuration files to use during the tuning process. 
More importantly, the system supports change of user-defined parameters of MapReduce job itself when job performance relies on user-defined parameters rather Hadoop parameters. For example, you can tune the job with the use of different sizes of data sets on Hadoop by simply configuring a set parameter which has a set of different value of input folders in a configuration file. 

2) History folder 
History Folder stores groups of log files that contains metrics of performance from each job execution with different pairs of tuning parameters. It is necessary to enable log properties on Yarn should be set before performing any log-based analysis. The metrics cover all fields from logs files of MapReduce jobs including total running time of the job and their details of mapper, reducer and attempts. The most important metric to estimate job performance is the total running time of job, also known as time cost, which are recorded in each job folder. 
 
After all possible jobs are finished, the system aggregates all information from all job logs to generate a table storing all extracted fields from the logs for comparison and modeling later in the Hadoop Optimizer. 
(3)	Progress Folder
Progress Folder stores run-time data during the tuning process. The running data includes exhaustive values of tuning parameter pairs in different jobs and the order of current tuning job, which enables to run at the last checkpoint of job order when the tuning process is interrupted before. 

To sum up, the tuning-based template fully utilizes the features of HPM when executing a job on Hadoop while using dynamic parameter strategies to revise current parameters of the job. Meanwhile, MapReduce logs of each job are fetched from Hadoop cluster and organized into a proper manner for future visualization and optimization. 



