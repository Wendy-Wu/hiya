	import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration; 
	   
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream; 
	   
	import org.apache.hadoop.fs.FileSystem; 
	   
import org.apache.hadoop.fs.Path; 
	
public class HdfsTool {
	

	public static FSDataInputStream readFile(URI URI, String filePath) throws IOException{
	   
			Configuration conf=new Configuration(); 
	   
			FileSystem hdfs=FileSystem.get(URI, conf); 
	   
			Path dfs=new Path(filePath); 

			FSDataInputStream inputStream=hdfs.open(dfs); 
			
			return inputStream;
			//read 

		} 
	
	public static void deleteFile(URI URI, String filePath) throws IOException{ 
		
		Configuration conf=new Configuration(); 
		   
		FileSystem hdfs=FileSystem.get(URI, conf); 
		
		Path delef=new Path(filePath); 
	}
}