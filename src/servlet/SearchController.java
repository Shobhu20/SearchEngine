package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import WebCrawler.WebCrawlerManager;
import WebCrawler.WebCrawlerNode;
import implementations.InvertedIndex;


@WebServlet("/searchController")
public class SearchController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;	
	private InvertedIndex invertedIndexObj;

    public void init() throws ServletException {
    	
		Collection<WebCrawlerNode> savedNode = null;
		try {
			savedNode = (Collection<WebCrawlerNode>)WebCrawlerManager.loadSerializedObject(getServletContext().getRealPath("/WEB-INF/LinkedList-Raywanderlich2000URLs.ser"), "LinkedList");
			invertedIndexObj = new InvertedIndex();
			invertedIndexObj.dataUpdated(savedNode);
		} catch (ClassNotFoundException | IOException e) {
			System.out.println(e);
		}
    }	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");


		if (request.getParameter("action") != null) {
			String actValue = request.getParameter("action");
			if (actValue.equals("prefix")) {
				if (request.getParameter("prefix") != null) {
					String prefixValue = request.getParameter("prefix");
					if (prefixValue.length() != 0) {
						StringBuffer buffer = new StringBuffer();
						buffer.append("[");
						if (invertedIndexObj.predictWord(prefixValue) != null) {
							for (String s : invertedIndexObj.predictWord(prefixValue)) {
								buffer.append("\"" + s + "\",");
							}
							System.out.println("Return value is " + invertedIndexObj.predictWord(prefixValue));
							buffer.append("\" \"]");
						}
						response.getWriter().print(buffer.toString());
					}
				}
			} else if (actValue.equals("getTopUrl")) {
				String prefixValue = request.getParameter("prefix");
				if (prefixValue != null) {
					if (prefixValue.length() != 0) {
						ArrayList<String> e = new ArrayList<String>();
						JSONArray obj = new JSONArray();
						if (invertedIndexObj.getMostRelevantUrls(prefixValue) != null) {
							int i = 0;
							for (String s : invertedIndexObj.getMostRelevantUrls(prefixValue)) {
								if (s != null) {
									i++;
									obj.put(s);
								}
							}
						}
						response.getWriter().print(obj);
					}
				}
			} else if (actValue.equals("getWordSuggestion")) {
				String prefixValue = request.getParameter("prefix");
				if (prefixValue != null && !prefixValue.equals("")) {
						JSONArray jsonArrayOfSuggestedWords = new JSONArray();
						ArrayList<String> suggestedWordList = invertedIndexObj.findSuggestedWord(prefixValue);
						if (suggestedWordList != null && suggestedWordList.size() !=0 ) {
							for (String suggestedWord : suggestedWordList) {
								if (suggestedWord != null && !suggestedWord.equals("")) {
									jsonArrayOfSuggestedWords.put(suggestedWord);
								}
							}
						}
						response.getWriter().print(jsonArrayOfSuggestedWords);
				}
			}
		}
		response.getWriter().print("");
	}

}
