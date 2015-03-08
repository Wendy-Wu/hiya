
import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;


public class MyFileInputFormat extends FileInputFormat<Text,Image> {

	@Override
	public RecordReader<Text, Image> createRecordReader(InputSplit arg0,
			TaskAttemptContext arg1) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return new MyRecordReader(); 
	}

    protected boolean isSplitable(FileSystem fs, Path file) {  
    //  CompressionCodec codec = compressionCodecs.getCode(file);  
        return false;//以文件为单位，每个单位作为一个split，即使单个文件的大小超过了64M，也就是Hadoop一个块得大小，也不进行分片  
    }  
  
   /* public RecordReader<Text,Image> getRecordReader(InputSplit genericSplit,  
                            JobConf job, Reporter reporter) throws IOException{  
        reporter.setStatus(genericSplit.toString());  
        return new MyRecordReader(job,(FileSplit)genericSplit);  
    }*/

	

}