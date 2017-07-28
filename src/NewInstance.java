import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewInstance {
	private String path;
	private String startLink;
	private String projectName;
	private String queueFile;
	private String crawlFile;
	private String crawledLinks;
	private String keyWordsFile;
	private String logFile;
	
	public NewInstance(String args[]) throws IOException{
		path = args[0]+"/WebCrawler";
		startLink = args[1];
		setupName();
		createDirectories();
		String queueFilePath = path+"/"+projectName+"/"+queueFile+".txt";
		String crawlFilePath = path+"/"+projectName+"/"+crawlFile+".txt";
		String crawledLinksPath = path+"/"+projectName+"/"+crawledLinks+".txt";
		String keyWordsPath = path+"/"+projectName+"/"+keyWordsFile+".txt";
		
		
		System.setOut(new PrintStream(new FileOutputStream(path+"/"+projectName+"/"+logFile+".txt")));
		System.out.println("Crawler Started at : "+new Date());
		new CrawlerEngine(queueFilePath,crawlFilePath,crawledLinksPath,keyWordsPath);
	}
	public void setupName(){
		DateFormat df = new SimpleDateFormat("YYYY_MM_dd_hh_mm_ss");
		projectName = "WebCrawler_"+df.format(new Date());
		queueFile = "Queue";
		crawlFile = "Crawl";
		crawledLinks = "CrawledLinks";
		keyWordsFile = "KeyWords";
		logFile = "log";
	}
	public void createDirectories() throws IOException{
		if(Files.isDirectory(Paths.get(path))){
			System.out.println(path+" : Already Exists.");
			createProjectDirectory();
			createQueueFile();
			createCrawlFile();
		}
		else{
			System.out.println(path +"Do Not Exists.");
			createProjectDirectory();
			createQueueFile();
			createCrawlFile();
		}
	}
	public void createQueueFile() throws IOException{
		try {
			Files.createFile(Paths.get(path+"/"+projectName+"/"+queueFile+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		//code to insert first link in queue file
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+"/"+projectName+"/"+queueFile+".txt")));
			writer.write(startLink+"\n");
			writer.close();
	}
	public void createCrawlFile(){
		try {
			Files.createFile(Paths.get(path+"/"+projectName+"/"+crawlFile+".txt"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void createProjectDirectory(){
		try {
			Files.createDirectories(Paths.get(path+"/"+projectName));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
