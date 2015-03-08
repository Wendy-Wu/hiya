import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Stamp {

	private static String URI = "hdfs://localhost:9000";
	private static String DATA_PATH = "/user/hadoop/input";
	private static String CENTER_PATH = "/user/hadoop/output";
	private static String NEW_CENTER_PATH = "/user/hadoop/output1";
	
	public static class StampMapper extends Mapper<LongWritable, Text, IntWritable, Text>{
	
	private double[][] centroids = null;	
	
	private String FILE_NAME_TAG = ".jpg";
	private String ILLEGAL_WORD = "\\00";
	
	//Setup: 设置聚类中心文件
	@Override 
	 protected void setup(Context context) throws IOException, 
	 InterruptedException { 
		String filepath = "/user/hadoop/input/centers.txt";
		int count=0;
		try {
			while(true){
				String value= HdfsTool.readFile(new URI(URI), filepath).readLine().toString();
				double[] vector = StampTool.toDouble(value.split(" "));
				centroids[count++] = vector;			
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 需要写个hdfs util
	}
	
	//Map: 读取特征向量，计算到类中心的欧式距离，取最小距离的类中心，输出<类编号，特征向量>
	@Override 
	 protected void map(LongWritable key, Text value, Context context) 
	 throws IOException, InterruptedException { 
		String line = value.toString();
		//To be filled 遇到图片名字及不合法的不读
		if(line.indexOf(FILE_NAME_TAG) != -1) return;
		if(line.indexOf(ILLEGAL_WORD) != -1) return;
		if(line.equals("")) return;
		
		String[] result = line.split(" ");
		double[] vector = StampTool.toDouble(result);
		
		double minDist = Double.MAX_VALUE;
		int classnumber = -1;
		for (int i = 0; i < 10000; i++){
			double tempDist = StampTool.getDistance(vector, centroids[i]);
			if(tempDist < minDist){
				minDist = tempDist;
				classnumber = i;
			}
		}
		
		context.write(new IntWritable(classnumber), value);
	}
	
	//Reduce:计算同一类中的特征向量的均值作为新的聚类中心
	public static class StampReducer extends Reducer<IntWritable, Text, Text, Text> {
		public void reduce(IntWritable key, Iterable<Text> values, Context context)throws IOException, InterruptedException {
			double[] sum = null;
			int count = 0;
			Iterator<Text> iterator = values.iterator();
			while(iterator.hasNext()){
				String value = iterator.next().toString();
				double[] vector = StampTool.toDouble(StampTool.splitString(value));
				for(int i=0; i<24; i++){
					sum[i] += vector[i];
				}
				count++;
			}
			
			double[] average = null;
			String newCenter="";
			for(int i=0; i<24; i++){
				average[i] = sum[i]/count;
				newCenter += average[i];
			}
			
			context.write(new Text(""), new Text(newCenter));
		}

	}
	

    public static class CountMapper extends Mapper<Text,Image,Text,Text>{

	public ArrayList centers;
	
	public CountMapper(){
		centers=new ArrayList<double[]>();
	}
			
	protected void setup(Context context) throws IOException,
	InterruptedException {//打开中心数据文件
		String filepath = "/user/hadoop/input/centers.txt";
		try {
			while(true){
				String value= HdfsTool.readFile(new URI(URI), filepath).readLine().toString();
				double[] vector = StampTool.toDouble(value.split(" "));
				centers.add(vector);
			}
		}
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

	protected void map(Text key, Image value, Context context)
			throws IOException, InterruptedException {//将每个24维向量与中心向量比较，找出属于哪一类，输出key 为图像名，value为string，里面写着属于哪一类以及数
		
		HashMap<Integer,Integer> map=new HashMap<Integer,Integer>();
		
		for(int i=0;i<4;i++){
			double mindif=Double.MAX_VALUE;///?????
			int minclass=0;
			for(int j=0;j<centers.size();j++){
				double newdif=compute(value.getvector(i),(double[])(centers.get(j)));
				if(compare(mindif,newdif)){
					mindif=newdif;
					minclass=j+1;//类别就是center行数
				}
			}
			if(map.containsKey(minclass)){
				map.put(minclass, map.get(minclass)+1);
			}else{
				map.put(minclass, 1);
			}
		}
		
		String s=null;
		Iterator iterator = map.keySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Integer classnum = (Integer)entry.getKey();
			Integer num = (Integer)entry.getValue();
		System.out.println(classnum+" "+num);
		s=s+" class:"+classnum+" num:"+num;		
		}
		
		context.write((Text) key, new Text(s) );
	}
	
	
	
	public double compute(double[] a,double[] b){//计算差
		return 0;
	}
	public boolean compare(double a,double b){//a>b返回true
		return false;
	}

}

	
	//Main:迭代进行map,reduce
	public static void main(String[] args)throws Exception{
		int count=5;
		while(count != 0){
			runKmeans();
			fileReset();
			count--;
		}
		runCount();
	}
	
	public static void fileReset(){
		
	}
	
	//runKmeans:设置job
	public static void runKmeans() throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException{
		Configuration conf = new Configuration();
	
		Job job = new Job(conf,"Stamp Kmeans");
		
		job.setJarByClass(Stamp.class);
		
		job.setMapperClass(StampMapper.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setReducerClass(StampReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		
		FileInputFormat.addInputPath(job, new Path(DATA_PATH));
		FileOutputFormat.setOutputPath(job, new Path(NEW_CENTER_PATH));
		
		job.waitForCompletion(true);
	}
	
	public static void runCount() throws IOException, ClassNotFoundException, InterruptedException{
		Configuration conf = new Configuration();
		Job job = new Job(conf,"Stamp Count");
		job.setJarByClass(Stamp.class);
		
		job.setMapperClass(CountMapper.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(MyFileInputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(DATA_PATH));
		FileOutputFormat.setOutputPath(job, new Path(NEW_CENTER_PATH));
		
		job.waitForCompletion(true);
		
	}
	
	
	}
	
}
