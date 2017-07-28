import java.io.IOException;
import java.math.BigInteger;

public class Test {

	public Test() throws IOException{
		/*System.out.println("Starting Test...");
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					"C:\\Users\\Aman\\Desktop\\WebCrawler\\WebCrawler_2017_03_24_09_25_00\\Queue.txt")));
			writer.write("SomeThing");
			writer.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ending Test...");*/
		//System.out.println(new File("C:\\Users\\Aman\\Desktop\\WebCrawler\\WebCrawler_2017_03_24_11_44_40\\Crawl.txt").is);
		
		/*Document doc = Jsoup.connect("http://www.javatpoint.com").get();  
		Elements links = doc.select("a[href]");  
		for (Element link : links) {  
		    System.out.println("\nlink : " + link.attr("href"));  
		    System.out.println("text : " + link.text()); 	
		}*/
		/*Document doc = Jsoup.connect("http://www.javatpoint.com").get();  
		String keywords = doc.select("meta[name=keywords]").first().attr("content");  
		System.out.println("Meta keyword : " + keywords);  
		String description = doc.select("meta[name=description]").get(0).attr("content");  
		System.out.println("Meta description : " + description); */
		/*Document doc = Jsoup.connect("https://www.google.co.in/search?q=taylor+swift&rlz=1C1CHZL_enIN707IN707&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjhy4-Gi_DSAhWHro8KHW8mCY4Q_AUICSgC#imgrc=3dvDCsjelDRDOM:").get();  
		Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");  
		for (Element image : images) {  
		    System.out.println("src : " + image.attr("src"));  
		    System.out.println("height : " + image.attr("height"));  
		    System.out.println("width : " + image.attr("width"));  
		    System.out.println("alt : " + image.attr("alt"));  
		}*/
		System.out.println("Aman");
		
		//String url = "https://en.wikipedia.org/wiki/Computer_science";
		/*try {
			System.out.println("Inside Parser");
			Document doc = Jsoup.connect(url).get();
			//Elements links = doc.select("a[href]");
			Elements links = doc.getElementById("bodyContent").select("a[href]");

			System.out.println("Li ks length : "+links.size());
			int count = 0;
			for(Element link : links){
				String link_string = link.absUrl("href");
				System.out.println(link.text()+"***"+link.absUrl("href"));
				count++;
				try {
					if(new URI(link_string).getHost().startsWith("en")){
						System.out.println(link.text()+"***"+link.absUrl("href"));
					}
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("*\n*\n*\nCount = "+ count);
		}
		catch(SocketTimeoutException e){
			//if net is working but slow
			System.out.println("SocketTimeoutException");
			Parser.parser(url);
		}
		catch(UnknownHostException e){
			//if net is off or net is not working 
			System.out.println(" from UnknownHostException");
			//Parser.parser(url);	
			}
		catch(HttpStatusException e){
			System.out.println("Status 404");
		}
		catch (IOException e) {
			e.printStackTrace();
			//Parser.parser(url);
		}
		System.out.println(" Gupta");
		*/
		/*Pattern pattern = Pattern.compile("[a-zA-Z]*");
		String url = "https://en.wikipedia.org/wiki/Computer_science";
		try{
			Document doc = Jsoup.connect(url).timeout(5*1000).get();
			String s = doc.text();
			//System.out.println(s);
			Matcher matcher = pattern.matcher(s);
			while(matcher.find()){
				System.out.println(s.substring(matcher.start(),matcher.end()));
			}
			for(String str : doc.text().split(" ")){
				
				System.out.println(str);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
		BigInteger big = new BigInteger("1");
		big = big.add(new BigInteger("1"));
		System.out.println(big);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
