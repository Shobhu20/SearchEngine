package WebCrawler;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
	
	private LinkedList<String> linksToVisit = new LinkedList<String>();
	private HashSet<String> visitedLinks = new HashSet<String>();
	private HashSet<String>  linksToVisitCheckSet = new HashSet<String>();
	private Collection<WebCrawlerNode> webCrawledNodes = new LinkedList<WebCrawlerNode>();
	private final String STANDARD_URL_REGEX = "\\b(https?)://[-a-zA-Z0-9|.]+[-a-zA-Z0-9#/_|.:]*(asp|aspx|asx|asmx|cfm|html|htm|xhtml|jhtml|jsp|jspx|wss|do|php|php4|php3|phtml|py|shtml){1}$";
	private final String STANDARD_URLFOLDER_REGEX = "\\b(https?)://[-a-zA-Z0-9|.]+[-a-zA-Z0-9#/_|.:]*(/){1}$";
	private final String STANDARD_URLOTHER_REGEX = "\\b(https?)://[-a-zA-Z0-9|.]+[-a-zA-Z0-9/_:]*";
	private int MAX_URL_VISITS = 500;
	private int QtyQueuedLinks = 0;
	private boolean isPrintDebug = true;

	public WebCrawler(int maxURLVisits) {
		this.MAX_URL_VISITS = maxURLVisits;
	}

	public Collection<WebCrawlerNode> getWebCrawledNodes() {
		return webCrawledNodes;
	}

    public void buildWebCrawl(String url)  {
    	WebCrawlerNode crawlerNode = new WebCrawlerNode(url);
    	try {
	    	Connection connectionToURL = Jsoup.connect(url);

		    Document jSoupDoc = connectionToURL.get();
		    Elements hreflinks = jSoupDoc.select("a[href]");
		    
	    	crawlerNode = new WebCrawlerNode(url);
	    	String fullHtmlTextFormat = jSoupDoc.text();
	    	StringTokenizer tokenizer = new StringTokenizer(fullHtmlTextFormat);
	    	
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				String cleanToken = token.replaceAll("[+.^:,?;!�()\n\r\"]","");
				crawlerNode.addTextContentToken(cleanToken);
			}

		    for (int i = 0; i < hreflinks.size(); i++) {
	    		Element link = hreflinks.get(i);
	    		crawlerNode.addNodeUrlLink(link.attr("abs:href"));
		    	if (QtyQueuedLinks <= this.MAX_URL_VISITS) {
		    		String hrefURL = link.attr("abs:href");
		    		if (validadeURLWebCrawler(hrefURL)) {
		    			if (!linksToVisitCheckSet.contains(hrefURL)) {
					    	linksToVisit.add(hrefURL);
					    	linksToVisitCheckSet.add(hrefURL);
					    	QtyQueuedLinks++;
		    			} else {
		    			}
		    		} else {
		    		}
		    	}
		    }
    	} catch (IOException ex) {
    		crawlerNode.setBadURL(true);
    	}
    	
    	webCrawledNodes.add(crawlerNode);
    	
	    if (!linksToVisit.isEmpty()) {
	    	String linkToVisit = null;
	    	do {
	    		linkToVisit = linksToVisit.removeFirst();
	    		if (visitedLinks.contains(linkToVisit)) {
	    		}
	    	} while (!linksToVisit.isEmpty() && visitedLinks.contains(linkToVisit));
	    	
			visitedLinks.add(linkToVisit);
			buildWebCrawl(linkToVisit);
	    }
    }
    
    public boolean validadeURLWebCrawler(String URLToValidate) {
    	boolean URLMatches = false;
    	if (URLToValidate != null) {
    		URLMatches = URLMatches || URLToValidate.matches(STANDARD_URL_REGEX);
    		URLMatches = URLMatches || URLToValidate.matches(STANDARD_URLFOLDER_REGEX);
    		URLMatches = URLMatches || URLToValidate.matches(STANDARD_URLOTHER_REGEX);
    	}
		return URLMatches;
    }
    
}