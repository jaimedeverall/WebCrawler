import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

/*When the crawl method is called on a WebCrawler object, 
 * it visits all pages reachable from originalUrl under
 * the domain of originalUrl and prints out the static assets (images, 
 * javascript, stylesheets) for each page in JSON format listing the URLs of 
 * every static asset on that page.*/

public class WebCrawler {

	private String originalUrl;
	private HashSet<String> explored;
	private LinkedList<String> queue;

	WebCrawler(Document document) {
		setOriginalUrl(endWithSlash(document.location().toLowerCase()));
		explored = new HashSet<String>();
		queue = new LinkedList<String>();
	}

	/*
	 * crawl goes to all pages reachable from originalUrl under the domain of
	 * originalUrl and prints out the static assets (images, javascript,
	 * stylesheets) for each page in JSON format listing the URLs of every
	 * static asset on that page.
	 */
	public void crawl() {
		queue.addLast(originalUrl);
		explored.add(originalUrl);
		System.out.println("[");
		while (!queue.isEmpty()) {
			String currentUrl = queue.removeFirst();
			try {
				Document currentDocument = Jsoup.connect(currentUrl).get();
				ArrayList<String> staticAssets = getUrlsOfStaticAssets(currentDocument);
				System.out.println(formatAssetsForPage(staticAssets,
						currentDocument));
				enqueueAllReachableLinks(currentDocument);
			} catch (IOException e) {
				// System.out.println(currentUrl + " cannot be accessed");
			}
		}
		System.out.println("]");
	}

	/*
	 * getUrlsOfStaticAssets finds and returns the urls of all the static assets
	 * (images, javascript, stylesheets) in currentDocument.
	 */
	ArrayList<String> getUrlsOfStaticAssets(Document currentDocument) {
		ArrayList<String> staticAssets = new ArrayList<String>();
		Elements images = currentDocument.select("img[src]");
		for (Element image : images) {
			String absHref = image.attr("abs:src");
			staticAssets.add(absHref);
		}
		Elements scripts = currentDocument.select("script[src]");
		for (Element script : scripts) {
			String absHref = script.attr("abs:src");
			staticAssets.add(absHref);
		}
		Elements stylesheets = currentDocument.select("link[href]");
		for (Element stylesheet : stylesheets) {
			String absHref = stylesheet.attr("abs:href");
			staticAssets.add(absHref);
		}
		return staticAssets;
	}

	/*
	 * enqueueAllReachableLinks finds the href attribute of all the hyperlinks
	 * (<a> tag) in currentDocument. Then it checks that the url belongs to the
	 * same domain as originalUrl. Then we check to make sure that we have not
	 * been to this url before. If we have not, then we enqueue this url to the
	 * queue of pages to visit and add the url to our set of explored urls.
	 */
	void enqueueAllReachableLinks(Document currentDocument) {
		Elements links = currentDocument.select("a[href]");
		for (Element link : links) {
			String absHref = endWithSlash(link.attr("abs:href").toLowerCase());
			if (absHref.contains(originalUrl)) {
				if (!isExplored(absHref)) {
					queue.addLast(absHref);
					explored.add(absHref);
				}
			}
		}
	}

	/*
	 * Returns true if url is present in the queue and therefore the url is also
	 * present in the set of explored urls.
	 */
	boolean isExplored(String url) {
		return explored.contains(url);
	}

	/*
	 * Formats a string of all the static assets found in currentDocument so
	 * that this string can be easily printed out by the calling method.
	 */
	String formatAssetsForPage(ArrayList<String> staticAssets,
			Document currentDocument) {
		String assets = "\t{\n";
		assets += "\t\t\"url\" : \"" + currentDocument.location() + "\",\n";
		assets += "\t\t\"assets\" : [\n";
		for (String url : staticAssets) {
			assets += "\t\t\t\t" + "\"" + url + "\",\n";
		}
		assets += "\t\t]\n";
		assets += "\t},\n";
		return assets;
	}

	/*
	 * endWithSlash ensures that url ends with a slash. We use this methodto
	 * make sure that all the urls that we store in the set, explored, have the
	 * same format (i.e. all end with a slash).
	 */
	String endWithSlash(String url) {
		if (url.length() > 0 && url.charAt(url.length() - 1) == '/') {
			return url;
		}
		return url + "/";
	}

	/*
	 * Sets the originalUrl, which we store need to store in order toensure that
	 * the links that we find are under the domain of theoriginalUrl.
	 */
	void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

}
