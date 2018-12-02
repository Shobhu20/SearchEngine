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

import utilities.Util;



public class WebCrawler {
	
	// Creates a LIST (Queue) to maintain the list of links that will have to be visited
	private LinkedList<String> linksToVisit = new LinkedList<String>();
	
	// Creates a SET to maintain the list of links that were visited in order to guarantee no double visit
	private HashSet<String> visitedLinks = new HashSet<String>();

	// Creates a SET to maintain the list of links to be visited in order to guarantee no double visit
	private HashSet<String>  linksToVisitCheckSet = new HashSet<String>();
	
	
	// List of the Visited and Built Nodes
	private Collection<WebCrawlerNode> webCrawledNodes = new LinkedList<WebCrawlerNode>();
	
	// Constants for validating the HREF URL Retrieved by the JSOUP.
	//	A crawler may only want to seek out HTML pages and avoid all other MIME types. In order to request only HTML resources, 
	//	a crawler may make an HTTP HEAD request to determine a Web resource's MIME type before requesting the entire resource with a GET request. 
	//	To avoid making numerous HEAD requests, a crawler may examine the URL and only request a resource if the URL ends with certain characters 
	//	such as .html, .htm, .asp, .aspx, .php, .jsp, .jspx or a slash. This strategy may cause numerous HTML Web resources to be unintentionally skipped.
	//	Some crawlers may also avoid requesting any resources that have a "?" in them (are dynamically produced) in order to avoid spider traps that may 
	//	cause the crawler to download an infinite number of URLs from a Web site. This strategy is unreliable if the site uses URL rewriting to simplify its URLs.
	private final String STANDARD_URL_REGEX = "\\b(https?)://[-a-zA-Z0-9|.]+[-a-zA-Z0-9#/_|.:]*(asp|aspx|asx|asmx|cfm|html|htm|xhtml|jhtml|jsp|jspx|wss|do|php|php4|php3|phtml|py|shtml){1}$";
	private final String STANDARD_URLFOLDER_REGEX = "\\b(https?)://[-a-zA-Z0-9|.]+[-a-zA-Z0-9#/_|.:]*(/){1}$";
	private final String STANDARD_URLOTHER_REGEX = "\\b(https?)://[-a-zA-Z0-9|.]+[-a-zA-Z0-9/_:]*";

	// Standard number of URL to be visited
	private int MAX_URL_VISITS = 500;
	
	// Quantity of Link Queued to be visited while Crawling
	private int QtyQueuedLinks = 0;
	
	
	// To setup if debug messages should be shown
	private boolean isPrintDebug = true;
	
	
	/**
	 * 	Constructor of the WebCrawler. It will visit a Maximum of maxUrlVisits provided in the Constructor
	 * 
	 * @param maxURLVisits
	 */
	public WebCrawler(int maxURLVisits) {
		this.MAX_URL_VISITS = maxURLVisits;
	}

	
	/**
	 * 	Returns a Collections of WebCrawlerNodes 
	 * 
	 * @return Collections of WebCrawlerNodes
	 */
	public Collection<WebCrawlerNode> getWebCrawledNodes() {
		return webCrawledNodes;
	}

    
	/**
	 * 
	 * 	Rhew URL to start Crawlling and add to the maintained Collection
	 * 
	 * @param urlToVisit
	 */
    public void buildWebCrawl(String url)  {
    	WebCrawlerNode crawlerNode = new WebCrawlerNode(url);
    	try {
	   		// Connects to the Provided URL to Visit
	    	Connection connectionToURL = Jsoup.connect(url);

	    	// Obtains the Document that in this case represents the HTML File
		    Document jSoupDoc = connectionToURL.get();
		    // Obtains all Tags of HREF type
		    Elements hreflinks = jSoupDoc.select("a[href]");
		    

	    	crawlerNode = new WebCrawlerNode(url);
	    	String fullHtmlTextFormat = jSoupDoc.text();
	    	StringTokenizer tokenizer = new StringTokenizer(fullHtmlTextFormat);
	    	
	    	// Tokenizing the TEXT from parsed from the HTML file 
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				// removing all Unwanted characters from String
				String cleanToken = token.replaceAll("[+.^:,?;!�()\n\r\"]","");
				crawlerNode.addTextContentToken(cleanToken);
			}

			// Saving all found links from HREF Tags in the HTML file
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
					    	Util.printDebug(isPrintDebug, "####### ADDED link to visit: " + hrefURL + "  Link index: " + QtyQueuedLinks);
		    			} else {
		    				Util.printDebug(isPrintDebug, "####### NOT ADDED link to visit, already QUEUED: " + hrefURL);
		    			}
		    		} else {
		    			Util.printDebug(isPrintDebug, "####### NOT ADDED link to visit, not valid: " + hrefURL);
		    		}
		    	}
		    }
    	} catch (IOException ex) {
    		// A connection happened to the URL so it is considered to be a BROKEN URL
    		// In this case this NODE is marked as BAD it may not be considered while building the anything on this node.
    		crawlerNode.setBadURL(true);
    	}
    	
    	// Adding this NODE to the list of Crawled nodes
    	webCrawledNodes.add(crawlerNode);
    	
	    // Recursively visits the first Sub-URL that was Queued
	    if (!linksToVisit.isEmpty()) {
	    	String linkToVisit = null;
	    	do {
	    		linkToVisit = linksToVisit.removeFirst();
	    		if (visitedLinks.contains(linkToVisit)) {
	    			Util.printDebug(isPrintDebug, "###### Already visited : " + linkToVisit);
	    		}
	    	} while (!linksToVisit.isEmpty() && visitedLinks.contains(linkToVisit));
	    	
	    	Util.printDebug(isPrintDebug, "Removing link from set: " + linkToVisit);
	    	Util.printDebug(isPrintDebug, "Number of links still to visit: " + linksToVisit.size());
	    	Util.printDebug(isPrintDebug, "Not visited yet : " + linkToVisit + " Will add to the visited list");
			visitedLinks.add(linkToVisit);
			Util.printDebug(isPrintDebug, ">>>>>>>>> Visiting recursevelly now: " + linkToVisit);
			buildWebCrawl(linkToVisit);
	    }
    }
    
    /**
     *   Uses REGEX functionalities to test the URL retrieved from the HREF in the HTML files.
     * 
     * 
     * @param URLToValidate
     * @return
     */
    public boolean validadeURLWebCrawler(String URLToValidate) {
    	boolean URLMatches = false;
    	if (URLToValidate != null) {
    		URLMatches = URLMatches || URLToValidate.matches(STANDARD_URL_REGEX);
    		URLMatches = URLMatches || URLToValidate.matches(STANDARD_URLFOLDER_REGEX);
    		URLMatches = URLMatches || URLToValidate.matches(STANDARD_URLOTHER_REGEX);
    	}
		return URLMatches;
    }   

    /**
     * 
     * 
     * @param args
     */
    public static void main (String[] args) {
//		WebCrawler webCrawler = new WebCrawler(1000);
//		System.out.println("1 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/wiki/Franks"));
//		System.out.println("2 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/wiki/1872"));
//		System.out.println("3 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/wiki/2006"));
//		System.out.println("4 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/wiki/Led_Zeppelin"));
//		System.out.println("5 - " + webCrawler.validadeURLWebCrawler("https://lists.wikimedia.org/mailman/listinfo/daily-article-l"));
//		System.out.println("6 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/w/index.php?title=Main_Page&action=purge/"));
//		System.out.println("7 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/wiki/Early_Netherlandish_painting"));
//		System.out.println("8 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/wiki/Template:POTD/2016-12-03"));
//		System.out.println("9 - " + webCrawler.validadeURLWebCrawler("https://commons.wikimedia.org/wiki/"));
//		System.out.println("10 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/wiki/Special:Statistics"));
//		System.out.println("11 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/w/index.php"));
//		System.out.println("12 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/w/index.php3"));
//		System.out.println("13 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/w/index.asp"));
//		System.out.println("14 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/w/index.aspx"));
//		System.out.println("15 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/w/index.php?action=test"));
//		System.out.println("16 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/w/index.php3/"));
//		System.out.println("17 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/w/index.htm"));
//		System.out.println("18 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/w/index.htmlllll"));
//		System.out.println("19 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/wiki/Wikipedia#cite_note-KockJungSyn2016-14"));
//		System.out.println("20 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/wiki/File:RAAF_Boeing_C-17A_Globemaster_III_CBR_Gilbert.jpg"));
//		System.out.println("21 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/wiki/Gilbert.jpg"));
//		System.out.println("22 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/Gilbert.asx"));
//		System.out.println("23 - " + webCrawler.validadeURLWebCrawler("https://en.wikipedia.org/test"));
    }
    
}