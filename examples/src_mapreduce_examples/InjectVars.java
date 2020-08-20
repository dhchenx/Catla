package cn.edu.bjtu.cdh.examples.tuning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InjectVars {
	
 
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
