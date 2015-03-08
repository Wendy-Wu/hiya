import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.util.LineReader;


public class MyRecordReader extends RecordReader<Text, Image>{
	private FSDataInputStream fin = null;
	private Text key = null;
	private Image value = null;
	private LineReader reader = null;
	@Override
	public void close() throws IOException {
	// TODO Auto-generated method stub
	fin.close();
	}

	@Override
	public Text getCurrentKey() throws IOException,
	InterruptedException {
	// TODO Auto-generated method stub
	return key;
	}

	@Override
	public Image getCurrentValue() throws IOException, InterruptedException {
	// TODO Auto-generated method stub
	return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
	// TODO Auto-generated method stub
	return 0;
	}

	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext context)
	throws IOException, InterruptedException {
	// TODO Auto-generated method stub
	FileSplit fileSplit = (FileSplit)inputSplit;
	Configuration conf = context.getConfiguration();
	Path path = fileSplit.getPath();
	FileSystem fs = path.getFileSystem(conf);
	fin = fs.open(path);
	reader = new LineReader(fin);
	}

	public boolean isLegal(String s){
		if(s.indexOf("//00")!=-1)
			return false;//不合法有哪几种？？？？？
		return true;
	}
	
	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		
		if (key == null) {  
		      key = new Text();  
		    }  
		value=new Image();
		Text s=null;
		reader.readLine(s);
		Image image=new Image();
		if(s.toString().indexOf(".jpg")!=-1){
			key.set(s);;
			for(int i=0;i<4;i++){
				Text t=null;
				reader.readLine(t);
				String l=t.toString();
				if(isLegal(l)){
					image.setvector(i, l);;
				}else{
					return false;
				}
			}
			value=image;
			return true;	
		}
		else
			 return false;

	}

	
}
