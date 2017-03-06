import static org.junit.Assert.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import java.util.ArrayList;

/*WebCrawlerTest is a test class for WebCrawler.*/

public class WebCrawlerTest {

	@Test
	public void testGetUrlsOfStaticAssets() {
		String html = "<html><head>"
				+ "<link href=\"https://www.stylesheets.com\"/>"
				+ "</head><body>"
				+ "<script src=\"https://www.javascript.com\"></script>"
				+ "<img src=\"https://www.image.com\"/>"
				+ "</body></html>";
		Document doc = Jsoup.parse(html);
		WebCrawler crawler = new WebCrawler(doc);
		ArrayList<String> staticAssets = crawler.getUrlsOfStaticAssets(doc);
		assertTrue("image url present", staticAssets.contains("https://www.image.com"));
		assertTrue("javascript url present", staticAssets.contains("https://www.javascript.com"));
		assertTrue("stylesheet url present", staticAssets.contains("https://www.stylesheets.com"));
	}
	
	@Test
	public void testEnqueueAllReachableLinks(){
		String html = "<html><head>"
				+ "<link href=\"https://www.stylesheets.com\"/>"
				+ "</head><body>"
				+ "<script src=\"https://www.javascript.com\"></script>"
				+ "<img src=\"https://www.image.com\"/>"
				+ "<a href=\"https://www.image.com\"/>"
				+ "<a href=\"https://www.facebook.com\"/>"
				+ "<a href=\"https://www.youtube.com/random\"/>"
				+ "</body></html>";
		Document doc = Jsoup.parse(html);
		WebCrawler crawler = new WebCrawler(doc);
		crawler.setOriginalUrl("https://www.youtube.com/");
		crawler.enqueueAllReachableLinks(doc);
		assertTrue("https://www.youtube.com/random enqueued",
				crawler.isExplored("https://www.youtube.com/random/"));
		assertFalse("https://www.facebook.com enqueued",
				crawler.isExplored("https://www.facebook.com"));
	}
	
	@Test
	public void testEndWithSlash(){
		String html = "<html><head></head><body></body></html>";
		Document doc = Jsoup.parse(html);
		WebCrawler crawler = new WebCrawler(doc);
		assertEquals(crawler.endWithSlash("hi"), "hi/");
		assertEquals(crawler.endWithSlash("random/"), "random/");
	}
}
