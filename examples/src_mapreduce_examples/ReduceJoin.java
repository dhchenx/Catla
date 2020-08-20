package cn.edu.bjtu.cdh.bigdata.research.join;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.edu.bjtu.cdh.bigdata.research.utils.InjectVars;

import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ReduceJoin {

	// user map
	public static class DocJoinMapper extends Mapper<Object, Text, Text, Text> {
		private Text outKey = new Text();
		private Text outValue = new Text();

		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			String line = value.toString();
			String[] items = line.split("\t");

			if(line.isEmpty()|| items[0].contains("_ID"))
				return;
			
			if(items.length<2)
				return;
				
			outKey.set(items[1].trim());
			outValue.set("docs-" + items[0].trim());
			context.write(outKey, outValue);
		}
	}

	// city map
	public static class CodeJoinMapper extends Mapper<Object, Text, Text, Text> {
		// TODO Auto-generated constructor st
		private Text outKey = new Text();
		private Text outValue = new Text();

		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			String line = value.toString();

			String[] items = line.split("\t");

			if(line.isEmpty()|| items[0].contains("_ID"))
				return;
			
			if(items.length<2)
				return;
			
			outKey.set(items[1].trim());
			outValue.set("codes-" + items[0].trim());
			
			context.write(outKey, outValue);
		}

	}

	public static class JoinReducer extends Reducer<Text, Text, Text, Text> {
		// TODO Auto-generated constructor stub
		// Join type:{inner,leftOuter,rightOuter,fullOuter,anti}
		private String joinType = null;
		private static final Text EMPTY_VALUE = new Text("");
		private List<Text> listA = new ArrayList<Text>();
		private List<Text> listB = new ArrayList<Text>();

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			// 获取join的类型
			joinType = context.getConfiguration().get("join.type");
		}

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			listA.clear();
			listB.clear();

			Iterator<Text> iterator = values.iterator();
			while (iterator.hasNext()) {
				String value = iterator.next().toString();
				System.out.println(value);
				if (value.startsWith("docs-"))
					listA.add(new Text(value.replace("docs-", "")));
				if (value.startsWith("codes-"))
					listB.add(new Text(value.replace("codes-", "")));
			}
			joinAndWrite(key, context);
		}

		private void joinAndWrite(Text key, Context context) throws IOException, InterruptedException {
			// inner join
			if (joinType.equalsIgnoreCase("inner")) {
				if (!listA.isEmpty() && !listB.isEmpty()) {
					for (Text A : listA)
						for (Text B : listB) {
							context.write(key, new Text(A.toString() + "\t" + B.toString()));
						}
				}
			}
			// left outer join
			if (joinType.equalsIgnoreCase("leftouter")) {
				if (!listA.isEmpty()) {
					for (Text A : listA) {
						if (!listB.isEmpty()) {
							for (Text B : listB) {
								context.write(key, new Text(A.toString() + "\t" + B.toString()));
							}
						} else {
							context.write(key, new Text(A.toString() + "\t" + EMPTY_VALUE));
						}
					}
				}
			}
			// right outer join
			else if (joinType.equalsIgnoreCase("rightouter")) {
				if (!listB.isEmpty()) {
					for (Text B : listB) {
						if (!listA.isEmpty()) {
							for (Text A : listA)
								context.write(key, new Text(A.toString() + "\t" + B.toString()));
						} else {

							context.write(key, new Text(EMPTY_VALUE + "\t" + B.toString()));
						}
					}
				}
			}
			// full outer join
			else if (joinType.equalsIgnoreCase("fullouter")) {
				if (!listA.isEmpty()) {
					for (Text A : listA) {
						if (!listB.isEmpty()) {
							for (Text B : listB) {
								context.write(key, new Text(A.toString() + "\t" + B.toString()));
							}
						} else {
							context.write(key, new Text(A.toString() + "\t" + EMPTY_VALUE));
						}
					}
				} else {
					for (Text B : listB)
						context.write(key, new Text(EMPTY_VALUE + "\t" + B.toString()));
				}
			}
			// anti join
			else if (joinType.equalsIgnoreCase("anti")) {
				if (listA.isEmpty() ^ listB.isEmpty()) {
					for (Text A : listA)
						context.write(key, new Text(A.toString() + "\t" + EMPTY_VALUE));
					for (Text B : listB)
						context.write(key, new Text(EMPTY_VALUE + "\t" + B.toString()));
				}
			}
		}
	}
	
	public static String getIP(String hdfs_url) {
		String[] fs=hdfs_url.split(":");
		return fs[1].replace("/","");
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		String jobName = "ReduceJoin";

		Configuration conf = new Configuration();
		
		args = new GenericOptionsParser(conf, args).getRemainingArgs();
	
		// default parameter
		if (args.length == 0) {
			
			args = new String[] { 
					"hdfs://192.168.xx.xx:9000/data/cdh/research/join-test/input-smalldata", // allow multiple input
					"hdfs://192.168.xx.xx:9000/data/cdh/research/join-test/input-data",
					"hdfs://192.168.xx.xx:9000/data/cdh/research/join-test/output-reducejoin", "@jointype=inner",
					"@traceId=" + System.currentTimeMillis(),
					"@jobName=" + jobName
			};
		}	

		//obtain application args
		Map<String, String> appArgs = InjectVars.getVars(args);
		args = InjectVars.getArgs(args);
		
		// set default if not exists @vars
		if (!appArgs.containsKey(InjectVars.traceId))
			appArgs.put(InjectVars.traceId, "0");

		if (!appArgs.containsKey(InjectVars.jobName))
			appArgs.put(InjectVars.jobName, jobName);

		//delete output
		
		conf.set("mapred.jop.tracker", "hdfs://"+getIP(args[0])+":9001");
		conf.set("fs.defaultFS", "hdfs://"+getIP(args[0])+":9000");
		
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(args[2]), true);

		//set job
		Job job = Job.getInstance(conf, appArgs.get(InjectVars.jobName) + "-" + "[" + appArgs.get(InjectVars.traceId) + "]");
		job.setJarByClass(ReduceJoin.class);
		job.setReducerClass(JoinReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, CodeJoinMapper.class);
		MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, DocJoinMapper.class);
		FileOutputFormat.setOutputPath(job, new Path(args[2]));
		job.getConfiguration().set("join.type", appArgs.get("jointype"));

		//time cost
		long startTime = System.currentTimeMillis(); 

		job.waitForCompletion(true);
		long endTime = System.currentTimeMillis(); 
		System.out.println("Runing Time: " + (endTime - startTime) + "ms");
	}

}
