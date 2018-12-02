package WebCrawler;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class WebCrawlerNode implements java.io.Serializable {
	
	private String baseNodeUrl;
	private Collection<String> textContentsTokens;
	private Collection<String> nodeUrlLinks;
	private boolean isBadURL = false;

	public WebCrawlerNode(String newBaseUrl) {
		this.baseNodeUrl = newBaseUrl;
		this.textContentsTokens = new ArrayList<String>();
		this.nodeUrlLinks = new ArrayList<String>();		
	}
	
	public Collection<String> getTextContentsTokens() {
		return this.textContentsTokens;
	}
	
	public Collection<String> getNodeUrlLinks() {
		return this.nodeUrlLinks;
	}
	
	public void addTextContentToken(String stringToken) {
		this.textContentsTokens.add(stringToken);
	}
	
	public void addNodeUrlLink(String stringUrlLink) {
		this.nodeUrlLinks.add(stringUrlLink);
	}	
	
	public String getNodeBaseUrl() {
		return this.baseNodeUrl;
	}
	
	public void setBadURL(boolean isBad) {
		this.isBadURL = isBad;
	}

	public boolean isBadURL() {
		return this.isBadURL;
	}

}

