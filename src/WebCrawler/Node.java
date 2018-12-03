package WebCrawler;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class Node implements java.io.Serializable {
	
	//private static final long serialVersionUID = 1L;
	
	private String baseURL;
	private Collection<String> tokens;
	private Collection<String> urls;
	private boolean isInvalidURL = false;

	public Node(String url) {
		this.baseURL = url;
		this.tokens = new ArrayList<String>();
		this.urls = new ArrayList<String>();		
	}
	
	public Collection<String> getTokens() {
		return this.tokens;
	}
	
	public Collection<String> getLinks() {
		return this.urls;
	}
	
	public void addToken(String stringToken) {
		this.tokens.add(stringToken);
	}
	
	public void addLink(String stringUrlLink) {
		this.urls.add(stringUrlLink);
	}	
	
	public String getBaseURL() {
		return this.baseURL;
	}
	
	public void isInvalid(boolean isBad) {
		this.isInvalidURL = isBad;
	}

	public boolean isInvalidURL() {
		return this.isInvalidURL;
	}

}