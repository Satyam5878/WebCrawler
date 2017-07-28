import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;
import org.apache.hadoop.util.hash.Hash;

public class TestBloomFilter {

	public TestBloomFilter() throws IOException{
		System.out.println("Testing Started...");
		BloomFilter bf = new BloomFilter(47923,33,Hash.MURMUR_HASH);
		BufferedReader buff = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("StopWordList.txt")));
	
		String line = "";
		while((line = buff.readLine()) != null){
			bf.add(new Key(line.getBytes()));
		}
		
		
		buff.close();
		buff = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("StopWordList.txt")));
		while((line = buff.readLine()) != null){
			if(bf.membershipTest(new Key(line.getBytes()))){
				System.out.println("true "+line);
			}
			else{
				System.out.println("false "+line);
			}
		}
		System.out.println(""+bf.membershipTest(new Key("aman".getBytes())));
		
		
		
		
		
		
		FileSystem fs = FileSystem.get(new Configuration());
		DataOutput ds = fs.create(new Path("BloomFilter"));
		bf.write(ds);
		System.out.println("Testing Done...");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
