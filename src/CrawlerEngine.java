import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.HashSet;

import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;
import org.apache.hadoop.util.hash.Hash;

public class CrawlerEngine implements Runnable{
/*
 * Field member for storing the path
 * */
	private static String queueFilePath;
	private static String crawledFilePath;
	private static String crawledLinkPath;
	private static String keyWordsPath;
/*
 * Field member for storing set of links in memory
 * */
	private static HashSet<String> queueSet;
/*
 * Field member for storing queuedlinks in disk
 * */
	private static RandomAccessFile queueFile;
	private static BufferedWriter buff;
	private volatile boolean flag = false;
/*
 * Field memeber for storing configuration setting 
 * */
	private final int SET_SIZE = 100000;//10000
	private final int NO_THREADS = 2000;//1000
	private static Thread[] threadPool;
/*
 * Field member for storing bloom filter 
 * */
	private static BloomFilter  stopWordBF;
	
/*
 * for changing channel position instead of shifting data from queue file.
 */
	private static long position = 0;
	private static BigInteger Counter_Size = new BigInteger("4");
	private static BigInteger Counter_Current = new BigInteger("0");
	private static final int BUFFER_SIZE =  16384;//8192
	
	@SuppressWarnings("static-access")
	public CrawlerEngine(String queueFilePath,String crawledFilePath,String crawledLinks,String keyWordsPath) throws IOException{
		this.queueFilePath = queueFilePath;
		this.crawledFilePath = crawledFilePath;
		this.crawledLinkPath = crawledLinks;
		this.keyWordsPath = keyWordsPath;
		queueSet = new HashSet<String>();
		
		buff = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(crawledLinkPath)));
		
		trainBloomFilter();
		
		Parser.initBloomFilter(stopWordBF);
		
		fill2QueueSet_ChangeWritePos();
		
//Run first Thread to crawl in parallel
		threadPool = new Thread[NO_THREADS];
		threadPool[0] = new Thread(this);
		threadPool[0].start();
		threadPool[0].setName("Thread 0");
//Wait till first Thread has done some work
		while(!flag){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//Launch More Thread
		for(int i=1;i<NO_THREADS;++i){
			threadPool[i] = new Thread(this);
			threadPool[i].start();
			threadPool[i].setName("Thread "+i);
		}
//Test for thread to alive or not 
		while(new File(queueFilePath).length() != 0){
			for(int i=0;i<NO_THREADS;++i){
				if(!threadPool[i].isAlive()){
					threadPool[i] = new Thread(this);
					threadPool[i].setName("Thread "+i);
					threadPool[i].start();
				}
			}
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
//Work for each thread 
	public void run() {
		System.out.println("Thread Started...");
		while(true){
			while(!queueSet.isEmpty()){
				//get a link from set and give it to parser that will return the scanned Link.
				String link = "";
				synchronized (this){
					link = queueSet.iterator().next();
					queueSet.remove(link);
					System.out.println("|||||||QueueSet Size +"+queueSet.size());
				}
				String[] generatedData = Parser.parser(link);
				//code to store generated links
				putLinksToQueueFile(generatedData[1]);
				putGeneratedLinksToCrawledFile(generatedData[0]);
				putKeyWordsToKeyWordsFile(generatedData[2]);
				System.out.println("Link Added");
				flag = true;
				try {
					buff.write(link+"\n");
					buff.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			synchronized (this){
				if(new File(queueFilePath).length() == 0){	
					break;
				}
				else{
					try {
						if(Counter_Current.compareTo(Counter_Size) == -1){
							fill2QueueSet_ChangeWritePos();
							Counter_Current = Counter_Current.add(new BigInteger("1"));
						}
						else{
							Counter_Size = Counter_Size.multiply(new BigInteger("2"));
							Counter_Current = new BigInteger("0");
							QueueSet_ShiftData();
							fill2QueueSet_ChangeWritePos();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("Thread Finished...");
	}
//Training bloomfilter
	public void trainBloomFilter() throws IOException{
		BufferedReader buff = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("StopWordList.txt")));
		stopWordBF = new BloomFilter(47923,33,Hash.MURMUR_HASH);
		String line = "";
		while((line = buff.readLine())!= null){
			stopWordBF.add(new Key(line.getBytes()));
		}
		buff.close();
	}
	public synchronized void putLinksToQueueFile(String links){
		try {
			BufferedWriter queueFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(queueFilePath,true)));
			queueFileWriter.write(links);
			queueFileWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public synchronized void putGeneratedLinksToCrawledFile(String data){
		try {
			BufferedWriter crawledFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(crawledFilePath,true)));
			crawledFileWriter.write(data);
			crawledFileWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public synchronized void putKeyWordsToKeyWordsFile(String keyWords){
		try {
			BufferedWriter keyWordFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(keyWordsPath,true)));
			keyWordFileWriter.write(keyWords);
			keyWordFileWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public synchronized void fill2QueueSet_ChangeWritePos() throws IOException{
		queueFile = new RandomAccessFile(queueFilePath,"rw");
		queueFile.seek(position);
		String line = "";
		int count = 0;
		while((count < SET_SIZE) && ((line = queueFile.readLine()) != null)){
			queueSet.add(line.replace("\n", ""));
			position = queueFile.getFilePointer();
			count++;
		}
		if(count < SET_SIZE){
			queueFile.setLength(0);
			position = 0;
			System.out.println("Comes Here");
			return ;
		}
		queueFile.close();
	}
	public void QueueSet_ShiftData() throws IOException{
		long writePos = 0;
		long readPos = position;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead;
		while(-1 != (bytesRead = queueFile.read(buffer))){
			queueFile.seek(writePos);
			queueFile.write(buffer,0,bytesRead);
			writePos += bytesRead;
			readPos += bytesRead;
			queueFile.seek(readPos);
		}
		queueFile.setLength(writePos);
		queueFile.close();
		position = 0;
	}
}
