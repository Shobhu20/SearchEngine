package WebCrawler;


import java.io.*;
import java.util.Collection;
import java.util.Iterator;

import implementations.InvertedIndex;

public class WebCrawlerManager {
	
	private static final String FILE_PREFIX = "Nodes";
	private static final String FILE_TYPE = ".ser";

	public static boolean saveWebCrawlerNode (String groupName, Collection<WebCrawlerNode> nodes) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(FILE_PREFIX + groupName + FILE_TYPE);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(nodes);
		out.close();
		fileOut.close();
		return true;
	}
   
	public static Collection<WebCrawlerNode> loadWebCrawlerNodes (String groupName) throws IOException, ClassNotFoundException {
	   Collection<WebCrawlerNode> nodes = null;
	   FileInputStream fileIn = new FileInputStream(FILE_PREFIX + groupName + FILE_TYPE);
	   ObjectInputStream in = new ObjectInputStream(fileIn);
	   nodes = (Collection<WebCrawlerNode>) in.readObject();
	   in.close();
	   fileIn.close();    	  
       return nodes;
    }   
	
	public static boolean saveSerializableObject(String suffixName, Object objectToSave) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(objectToSave.getClass().getSimpleName() + "-" + suffixName + FILE_TYPE);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(objectToSave);
		out.close();
		fileOut.close();
		return true;
	}

	public static Object loadSerializedObject (String suffixName, String className) throws IOException, ClassNotFoundException {
		Object loadedObject = null;
		FileInputStream fileIn = new FileInputStream(suffixName);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		loadedObject = in.readObject();
		in.close();
		fileIn.close();    	  
	    return loadedObject;
	}   
	
   
}

