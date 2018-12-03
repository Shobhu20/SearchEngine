package WebCrawler;


import java.io.*;
import java.util.Collection;
import java.util.Iterator;

import implementations.InvertedIndex;

public class Manager {
	
	private static final String FILE_PREFIX = "Nodes";
	private static final String FILE_TYPE = ".ser";

	public static boolean saveNode(String group, Collection<Node> nodes) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(FILE_PREFIX + group + FILE_TYPE);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(nodes);
		objectOutputStream.close();
		fileOutputStream.close();
		return true;
	}
   
	public static Collection<Node> loadNodes(String group) throws IOException, ClassNotFoundException {
	   FileInputStream fileInputStream = new FileInputStream(FILE_PREFIX + group + FILE_TYPE);
	   ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
	   Collection<Node> nodes = (Collection<Node>) objectInputStream.readObject();
	   objectInputStream.close();
	   fileInputStream.close();    	  
       return nodes;
    }   
	
	public static boolean saveObject(String suffix, Object object) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(object.getClass().getSimpleName() + "-" + suffix + FILE_TYPE);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.close();
		fileOutputStream.close();
		return true;
	}

	public static Object loadObject (String suffix, String className) throws IOException, ClassNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(suffix);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		fileInputStream.close();    	  
	    return object;
	}   
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Crawler webCrawler = new Crawler();
		webCrawler.buildCrawler("https://www.raywenderlich.com/");
		System.out.println("List size : " + webCrawler.getNodes().size());
		Collection<Node> nodesMemory = webCrawler.getNodes();
		Iterator<Node> it = nodesMemory.iterator();
		while(it.hasNext()) {
			Node node = it.next();
		}
		Manager.saveObject("Raywanderlich2000URLs", webCrawler.getNodes());	
		System.out.println("File saved!");
		
//		Collection<WebCrawlerNode> nodesSaved = (Collection<WebCrawlerNode>)WebCrawlerManager.loadSerializedObject("raywanderlich", "LinkedList");
//		InvertedIndex obj = new InvertedIndex();
//		obj.dataUpdated(nodesSaved);
//		WebCrawlerManager.saveSerializableObject("luisDictonary", obj);				
   }
	
   
}