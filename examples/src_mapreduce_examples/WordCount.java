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
			otherArgs=new String[]{"hdfs://192.168.159.132:9000/data/cdh/examples/wordcount/input",
				"hdfs://192.168.159.132:9000/data/cdh/examples/wordcount/output"};
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