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

import constants.constants;

public class Crawler {
	// Linked Lists
	private LinkedList<String> linksNotVisited = new LinkedList<String>();
	private HashSet<String> linksVisited = new HashSet<String>();
	private HashSet<String> linksNotVisitedSet = new HashSet<String>();
	
	private Collection<Node> nodes = new LinkedList<Node>();
	
	private int linksInQueue = 0;

	public Collection<Node> getNodes() {
		return nodes;
	}

    public void buildCrawler(String url)  {
    	Node node = new Node(url);
    	try {
		    Document doc = Jsoup.connect(url).get();
		    Elements links = doc.select("a[href]");
		    
	    	node = new Node(url);
	    	String htmlToText = doc.text();
	    	
	    	StringTokenizer tokenizer = new StringTokenizer(htmlToText);
	    	
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken().replaceAll("[+.^:,?;!�()\n\r\"]","");
				node.addToken(token);
			}

			int count = 0;
			while(count < links.size()) {
				Element link = links.get(count);
				node.addLink(link.attr("abs:href"));
				
				if (linksInQueue <= constants.URL_VISITS) {
					String hrefURL = link.attr("abs:href");
					if (validateURL(hrefURL) && !linksNotVisitedSet.contains(hrefURL)) {
						linksNotVisited.add(hrefURL);
						linksNotVisitedSet.add(hrefURL);
						linksInQueue++;
					}
				}
				count++;
			}
    	} catch (IOException ex) {
    		node.isInvalid(true);
    	}
    	
    	nodes.add(node);
    	
	    if (!linksNotVisited.isEmpty()) {
	    	String crawlWebURL = linksNotVisited.removeFirst();
	    	while (!linksNotVisited.isEmpty() && linksVisited.contains(crawlWebURL)) {
	    		crawlWebURL = linksNotVisited.removeFirst();
	    	}
	    	
			linksVisited.add(crawlWebURL);
			buildCrawler(crawlWebURL);
	    }
    }
    
    public boolean validateURL(String url) {
    	boolean urlMatch = false;
    	if (url != null) {
    		urlMatch = urlMatch || url.matches(constants.URL_REGEX);
    	}
		return urlMatch;
    }
    
}