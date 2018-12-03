package CrawlerPackage;


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

public class Crawler {
	
	// Regex
	private final String URL_REGEX = "((https|https)://)?(www\\.)([-a-zA-Z0-9]+).([-a-zA-Z0-9]+)/([-a-zA-Z0-9_\\+?&//=\\/]+)";
	
	// Links to visit
	private LinkedList<String> linksToBeVisited = new LinkedList<String>();
	private HashSet<String> linksVisited = new HashSet<String>();
	private HashSet<String> linksToVisitSet = new HashSet<String>();
	
	// Crawler Nodes
	private Collection<Node> nodesCrawled = new LinkedList<Node>();
	
	// Default MAX visits
	private int MAX_VISITS_FOR_URLS = 500;
	
	private int queueListCount = 0;

	public Crawler(int maxVisits) {
		this.MAX_VISITS_FOR_URLS = maxVisits;
	}

	public Collection<Node> getNodes() {
		return nodesCrawled;
	}

    public void buildCrawlerFromURL(String url) {
    	Node node = new Node(url);
    	try {

		    Document doc = Jsoup.connect(url).get();
		    Elements links = doc.select("a[href]");
		    
	    	node = new Node(url);
	    	
	    	StringTokenizer tokenizer = new StringTokenizer(doc.text());

	    	while (tokenizer.hasMoreTokens()) {
	    		String token = tokenizer.nextToken();
	    		String cleanToken = token.replaceAll("[+.^:,?;!�()\n\r\"]","");
	    		node.addToken(cleanToken);
	    	}

	    	for (int i = 0; i < links.size(); i++) {
	    		Element link = links.get(i);
	    		node.addURL(link.attr("abs:href"));
		    	if (queueListCount <= this.MAX_VISITS_FOR_URLS) {
		    		String href = link.attr("abs:href");
		    		if (validadeURLWebCrawler(href)) {
		    			if (!linksToVisitSet.contains(href)) {
		    				linksToBeVisited.add(href);
					    	linksToVisitSet.add(href);
					    	queueListCount++;
		    			}
		    		}
		    	}
		    }
    	} catch (IOException ex) {
    		node.setURL(true);
    	}
    	
    	nodesCrawled.add(node);
    	
	    if (!linksToBeVisited.isEmpty()) {
	    	String linkToVisit = linksToBeVisited.removeFirst();
	    	while (!linksToBeVisited.isEmpty() && linksVisited.contains(linkToVisit)) {
	    		linkToVisit = linksToBeVisited.removeFirst();
	    	}
			linksVisited.add(linkToVisit);
			buildCrawlerFromURL(linkToVisit);
	    }
    }
    
    public boolean validadeURLWebCrawler(String url) {
    	boolean match = false;
    	if (url != null) {
    		match = match || url.matches(URL_REGEX);
    	}
		return match;
    }
    
}