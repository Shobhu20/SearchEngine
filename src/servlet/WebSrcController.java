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
import resources.InvertedIndex;

/**
 * Servlet implementation class FirstEntry
 */
@WebServlet("/WebSrcController")
public class WebSrcController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final String CRAWLER_NODES_FILE = "luis";
	
	// Inverted Index for Web Search
	InvertedIndex invertedIndexObj;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WebSrcController() {
		super();
	}

	
    /**
     *  simply creates or loads some data that will be used throughout the life of the servlet
     * 
     */
    public void init() throws ServletException {
    	
		Collection<WebCrawlerNode> savedNode = null;
		try {
			
			savedNode = (Collection<WebCrawlerNode>)WebCrawlerManager.loadSerializedObject(getServletContext().getRealPath("/WEB-INF/LinkedList-luis.ser"), "LinkedList");
		
			invertedIndexObj = new InvertedIndex();
			System.out.println("INVERTED INDEX Started");
			invertedIndexObj.dataUpdated(savedNode);
		} catch (ClassNotFoundException | IOException e) {
			System.out.println(e);
		}
    }	
	
	
	
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		
		// TO check if InvertedIndex was instantiated 
//		if (invertedIndexObj != null) {
//			System.out.println("### DEBUG => Inverted Index loaded and instantianted");
//		} else {
//			System.out.println("### DEBUG => Inverted Index IS NULL");
//		}

		if (request.getParameter("act") != null) {
			String actValue = request.getParameter("act");
			System.out.println("### DEBUG => ACT = " + actValue);
			if (actValue.equals("prefix")) {
				if (request.getParameter("prefix") != null) {
					String prefixValue = request.getParameter("prefix");
					System.out.println("### DEBUG Prefix = " + prefixValue.length());
					if (prefixValue.length() != 0) {
						//org.json.JSONArray obj = new JSONArray();
						StringBuffer buffer = new StringBuffer();
						buffer.append("[");
						if (invertedIndexObj.predictWord(prefixValue) != null) {
							for (String s : invertedIndexObj.predictWord(prefixValue)) {
								//obj.put(s);
								buffer.append("\"" + s + "\",");
							}
							System.out.println("Returnung " + invertedIndexObj.predictWord(prefixValue));
							buffer.append("\" \"]");
						}
						response.getWriter().print(buffer.toString());
					}
				}
			} else if (actValue.equals("getTopUrl")) {
				System.out.println("### DEBUG => ACT = " + actValue);
				String prefixValue = request.getParameter("prefix");
				System.out.println("### DEBUG prefix = " + prefixValue);
				if (prefixValue != null) {
					System.out.println("### DEBUG prefix = " + prefixValue.length());
					if (prefixValue.length() != 0) {
						ArrayList<String> e = new ArrayList<String>();
						JSONArray obj = new JSONArray();
						//StringBuffer buffer = new StringBuffer();
						if (invertedIndexObj.getMostRelevantUrls(prefixValue) != null) {
							int i = 0;
							for (String s : invertedIndexObj.getMostRelevantUrls(prefixValue)) {
								if (s != null) {
									System.out.println("### DEBUG => getTopUrl = " + i + " => " + s);
									i++;
									//buffer.append(s);
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
	

	
	public static void main (String[] args) {
		Collection<WebCrawlerNode> nodesSaved = null;
		try {
			System.out.println("### DEBUG - Servlet Initialization ###");
			System.out.println("### DEBUG - Will try to load WebCrawler Serialized file.");
			nodesSaved = (Collection<WebCrawlerNode>)WebCrawlerManager.loadSerializedObject("LinkedList-luis", "LinkedList");
			System.out.println("### DEBUG - WebCrawler Serialized file loaded Successfully");			
			System.out.println("### DEBUG - Will instantiate INVERTED INDEX Search Structure");
			InvertedIndex invertedIndexEngine = new InvertedIndex();
			System.out.println("### DEBUG - INVERTED INDEX Search Structure Instantiated");
			invertedIndexEngine.dataUpdated(nodesSaved);
			System.out.println("### DEBUG - INVERTED INDEX Will be saved to file");
			WebCrawlerManager.saveSerializableObject("InvertedIdxIluisRueda", invertedIndexEngine);			
			System.out.println("### DEBUG - INVERTED INDEX saved to file");			
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}		
	}
	

}
