## Requirement of programming MapReduce jobs for self-tuning in Calta
There are some strict requirements that must be employed when designing a MapReduce algorithm for tuning in Calta.

### List of requirements
1) Use of <b>GenericOptionsParser</b> for args[] in the main() is a must. 

Sample code:
```java
	public static void main(String[]){
		Configuration conf = new Configuration();	
		args = new GenericOptionsParser(conf, args).getRemainingArgs();
		...
	}	
```

2) You can use Injecting Variable (InjectVar) to specify advance parameters in MapReduce algorithm rather general parmaters like '/input'

Definition: @property_name=proper_value

Sample Code like: 
```java
	args = new String[] { 
		"hdfs://192.168.xx.xx:9000/data/cdh/research/join-test/input-smalldata", 
		"hdfs://192.168.xx.xx2:9000/data/cdh/research/join-test/input-data",
		"hdfs://192.168.xx.xx:9000/data/cdh/research/join-test/output-reducejoin",
		"@jointype=inner",
					"@traceId=" + System.currentTimeMillis(),
		"@jobName=" + jobName
			};
```
@jobName is a user-level parameter used in design of MapReduce algorithm, rather the algorithm itself. 

@traceId is a user-level parameter used for tracking multiple jobs submitted at the same time. Usually you do not need to specify it because the system will automatically assign one when submitting and tuning. 

Note: you can also define any parameter starting with '@' to meet users' need.

3) When multiple jobs are used in ONE single MapReduce job, the name of last job must end with '-END'
Supposing that a similarity job has two phrases, namely indexing and similarity, then the last job is 'similarity' whose job name must end with  '-END'. 

See sample: 
```java
		...
		//this is last job
	Job job = Job.getInstance(conf, "Similarity-END]");
	job.setJarByClass(PartitionJoinWithCount.class);
	job.setReducerClass(JoinReducer.class);
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(Text.class);
	MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, CodeJoinMapper.class);
	MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, DocJoinMapper.class);
	FileOutputFormat.setOutputPath(job, new Path(args[2]));
	job.getConfiguration().set("join.type", appArgs.get("jointype"));

		job.waitForCompletion(true);

```
See! The name of the last job ends with '-Join-END', the string '-END' must be specified when you want to tune the job performance since the system will determine all jobs have been finished when last job's name ends with END. If the job is not finished, there exists no job with name ending '-END'.

Node: If you just want to submit the job, rather trying to monitor their performance in your client, then all these requirements are not necessary. 

### A simple WordCount program that meets the above requirements in Catla is here:

```java

package cn.edu.bjtu.cdh.examples.tuning;
import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCount {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);

		private Text word = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			StringTokenizer itr = new StringTokenizer(value.toString());

			while (itr.hasMoreTokens()) {

				word.set(itr.nextToken());

				context.write(word, one);

			}

		}

	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {

			int sum = 0;

			for (IntWritable val : values) {

				sum += val.get();

			}

			result.set(sum);

			context.write(key, result);

		}

	}
	

	public static String getIP(String hdfs_url) {
		String[] fs = hdfs_url.split(":");
		return fs[1].replace("/", "");
	}

	
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		
		if(otherArgs.length==0) {
			otherArgs=new String[]{"hdfs://192.168.xxx.xxx:9000/data/cdh/examples/wordcount/input",
				"hdfs://192.168.xxx.xxx:9000/data/cdh/examples/wordcount/output"};
		}

		Map<String,String> app_args=InjectVars.getVars(otherArgs);
		otherArgs=InjectVars.getArgs(otherArgs);
		
		conf.set("mapred.jop.tracker", "hdfs://" + getIP(otherArgs[0]) + ":9001");
		conf.set("fs.defaultFS", "hdfs://" + getIP(otherArgs[0]) + ":9000");

		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(otherArgs[1]), true);
		
		String job_name="WordCount";
		if(app_args.containsKey("traceId")) {
			job_name+="["+app_args.get("traceId")+"]";
		}
		
		Job job = Job.getInstance(conf, job_name);
		
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));  
	    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1])); 

		job.setJarByClass(WordCount.class);

		job.setMapperClass(TokenizerMapper.class);

		job.setCombinerClass(IntSumReducer.class);
		
		job.setReducerClass(IntSumReducer.class);

		job.setOutputKeyClass(Text.class);

		job.setOutputValueClass(IntWritable.class);
		
		job.waitForCompletion(true);
 
	}

}

```

Another important tool helper for Injecting Variables is defined as : 

```java
package cn.edu.bjtu.cdh.bigdata.research.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InjectVars {
	
	public static String traceId="traceId";
	public static String jobName="jobName";
 
	public static Map<String,String> getVars(String[] args){
		Map<String,String> vars=new HashMap<String,String>();
		if(args.length>0)
		for(int i=0;i<args.length;i++) {
			//@a=1
			if(args[i].startsWith("@")) {
				String[] fs=args[i].split("=");
				String key=fs[0].replace("@", "");
				String value=fs[1];
				vars.put(key, value);
			}
		}
		return vars;
	}
	
	public static String[] getArgs(String[] args){
		List<String> new_args=new ArrayList<String>();

		if(args.length>0)
		for(int i=0;i<args.length;i++) {
			if(!args[i].startsWith("@")) {
				new_args.add(args[i]);
			}
		}
		
		String[] as=new String[new_args.size()];
		for(int i=0;i<as.length;i++)
			as[i]=new_args.get(i);
		return as;
	}
	
	 
	
}


```

