package servlet;


import java.util.Collection;
import WebCrawler.WebCrawlerManager;
import WebCrawler.WebCrawlerNode;
import implementations.InvertedIndex;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;




@WebServlet("/searchController")
public class SearchController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;	
	private InvertedIndex searchEngOperation = new InvertedIndex();

    public void init() throws ServletException {
    	
		Collection<WebCrawlerNode> savedNode = null;
		try {
			savedNode = (Collection<WebCrawlerNode>)WebCrawlerManager.loadSerializedObject(getServletContext().getRealPath("/WEB-INF/LinkedList-Raywanderlich2000URLs.ser"), "LinkedList");
			searchEngOperation.dataUpdated(savedNode);
		} catch (ClassNotFoundException | IOException e) {
		}
    }	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONArray jsonArrayResul = new JSONArray();
		response.setContentType("text/html");
		String stringValue = request.getParameter("searchStr");
		if (request.getParameter("action") != null) {
			String requestAction = request.getParameter("action");
			if (requestAction.equals("autocomplete") && stringValue != null && stringValue.length() != 0) {
						StringBuffer buffer = new StringBuffer();
						buffer.append("[");
						if (searchEngOperation.predictWord(stringValue) != null) {
							for (String s : searchEngOperation.predictWord(stringValue)) {
								buffer.append("\"" + s + "\",");
							}
							System.out.println("Return value is " + searchEngOperation.predictWord(stringValue));
							buffer.append("\" \"]");
						}
						response.getWriter().print(buffer.toString());
			} else if (requestAction.equals("getTopUrl")) {
				if (stringValue != null && stringValue.length() != 0) {
						if (searchEngOperation.getMostRelevantUrls(stringValue) != null) {
							for (String s : searchEngOperation.getMostRelevantUrls(stringValue)) {
								if (s != null){jsonArrayResul.put(s);}}}
						response.getWriter().print(jsonArrayResul);
				}
			} else if (requestAction.equals("getWordSuggestion") && stringValue != null && !stringValue.equals("")) {
						ArrayList<String> suggestedWordList = searchEngOperation.findSuggestedWord(stringValue);
						if (suggestedWordList != null && suggestedWordList.size() !=0 ) {
							for (String suggestedWord : suggestedWordList) {
								if (suggestedWord != null && !suggestedWord.equals("")) {
									jsonArrayResul.put(suggestedWord);
								}
							}
						}
						response.getWriter().print(jsonArrayResul);
				}
		}
		response.getWriter().print("");
	}
}
