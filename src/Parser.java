import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
	private static BloomFilter stopWordBF;
	private static Pattern pattern = Pattern.compile("[a-zA-Z]*");
	
	public static void  initBloomFilter(BloomFilter bloomFilter){
		stopWordBF = bloomFilter;
	}
	public static String[] collectLinkAndFormat(Elements links,String url,String heading){
		StringBuilder crawled = new StringBuilder(heading+"\t"+url+"\t\t[{");//crawled is for crawled data
		StringBuilder queued = new StringBuilder();
		int count = 0;
		for(Element link : links){
			String link_string = link.absUrl("href");
			String curr_heading = link.text();
			try {
				if(new URI(link_string).getHost().startsWith("en.wikipedia.org")){
					if(link_string.contains("#")){
						continue;
					}
					else if(link_string.endsWith(".png")||
							link_string.endsWith(".PNG")||
							link_string.endsWith(".jpg")||
							link_string.endsWith(".JPG")||
							link_string.endsWith(".exe")||
							link_string.endsWith(".EXE")||
							link_string.endsWith(".gif")||
							link_string.endsWith(".GIF")||
							link_string.endsWith(".pdf")||
							link_string.endsWith(".PDF")){
						continue;
					}
					crawled.append(curr_heading+":::"+link_string+" , ");
					queued.append(link_string+"\n");
					count++;
				}
			}catch (URISyntaxException e) {
				e.printStackTrace();
			}catch(Exception e){
				System.out.println("Exception");
				e.printStackTrace();
			}
		}
		crawled.delete(crawled.length()-2, crawled.length()-1);
		crawled.append("},count:"+count+"]\n");
		return new String[]{crawled.toString(),queued.toString()};
	}
	public static String[] parser(String url){
		try {
			System.out.println("Inside Parser with "+url);
			Document doc = Jsoup.connect(url).timeout(20*1000).get();
			String[] links = Parser.parseLink(url, doc);
			String words = Parser.parseText2keyWords(url,doc);
			return new String[]{links[0],links[1],words};
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		catch(SocketTimeoutException e){
			Parser.parser(url);
		}
		catch(UnknownHostException e){
			e.printStackTrace();
		}
		catch(HttpStatusException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			Parser.parser(url);
		}
		return new String[]{"","",""};
	}
	public static String[] parseLink(String url,Document doc){
		Elements links = doc.getElementById("bodyContent").select("a[href]");
		String heading = doc.getElementById("firstHeading").text();
		return collectLinkAndFormat(links,url,heading);
	}
	public static String parseText2keyWords(String url,Document doc){
		String body = doc.getElementById("bodyContent").text();
		return collectKeyWordsAndFormat(url,body);	
	}
	public static String collectKeyWordsAndFormat(String url,String body){
		HashMap<String,Integer> keyWords = new HashMap<String,Integer>();
		Matcher matcher = pattern.matcher(body);
		while(matcher.find()){
			String key = body.substring(matcher.start(),matcher.end()).toLowerCase();
				if (key.length() > 0) {
					if (!stopWordBF.membershipTest(new Key(key.getBytes()))) {
						if (keyWords.containsKey(key)) {
							keyWords.put(key, keyWords.get(key) + 1);
						} else {
							keyWords.put(key, 1);
						}
					}
				}
		}
		StringBuilder keyWordBuilder = new StringBuilder(url+"\t\t[{");
		for(String keyWord : keyWords.keySet()){
			keyWordBuilder.append(keyWord+":"+(int)keyWords.get(keyWord)+" , ");
		}
		keyWordBuilder.delete(keyWordBuilder.length()-2,keyWordBuilder.length()-1);
		return keyWordBuilder.append("}]\n").toString();
	}
}
