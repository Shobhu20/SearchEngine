package CrawlerPackage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Node implements Serializable {
	
	private String baseURL = "https://www.raywenderlich.com";
	private Collection<String> tokens;
	private Collection<String> urls;
	private boolean isInvalid = false;

	public Node(String baseURL) {
		this.baseURL = baseURL;
		this.tokens = new ArrayList<String>();
		this.urls = new ArrayList<String>();		
	}
	
	public Collection<String> getTokens() {
		return this.tokens;
	}
	
	public Collection<String> getLinks() {
		return this.urls;
	}
	
	public void addToken(String token) {
		this.tokens.add(token);
	}
	
	public void addURL(String url) {
		this.urls.add(url);
	}	
	
	public String getBaseURL() {
		return this.baseURL;
	}
	
	public void setURL(boolean invalid) {
		this.isInvalid = invalid;
	}

	public boolean isInvalid() {
		return this.isInvalid;
	}

}

