import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/*RunWebCrawler is used to instantiate a WebCrawler object
 * and call its crawl method. RunWebCrawler does some error-checking
 * to ensure the client supplied url is valid.*/

public class RunWebCrawler {

	public static void main(String[] args) {
		if(args.length != 1){
			System.out.println("Arguments are incorrect.");
		}else{
			String originalUrl = args[0];
			try{
				Document document = Jsoup.connect(originalUrl).get();
				WebCrawler crawler = new WebCrawler(document);
				crawler.crawl();
			}catch(Exception e){
				System.out.println("The url you supplied was incorrect. Please try again");
			}
		}
	}

}
