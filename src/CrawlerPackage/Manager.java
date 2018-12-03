package CrawlerPackage;


import java.io.*;
import java.util.Collection;
import java.util.Iterator;

import implementations.InvertedIndex;

public class Manager {

	private static final String prefix = "Nodes";
	private static final String type = ".ser";

	public static boolean saveNode(String group, Collection<Node> nodes) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(prefix + group + type);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(nodes);
		objectOutputStream.close();
		fileOutputStream.close();
		return true;
	}

	public static boolean saveObject(String suffix, Object object) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(object.getClass().getSimpleName() + "-" + suffix + type);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.close();
		fileOutputStream.close();
		return true;
	}

	public static Collection<Node> loadNode(String group) throws IOException, ClassNotFoundException {
		Collection<Node> nodes = null;
		FileInputStream fileInputStream = new FileInputStream(prefix + group + type);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		nodes = (Collection<Node>) objectInputStream.readObject();
		objectInputStream.close();
		fileInputStream.close();    	  
		return nodes;
	}   

	public static Object loadSerialObject(String suffix) throws IOException, ClassNotFoundException {
		Object object = null;
		FileInputStream fileInputStream = new FileInputStream(suffix);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		object = objectInputStream.readObject();
		objectInputStream.close();
		fileInputStream.close();    	  
		return object;
	}   
	
	// Used to create the crawled data file and/or load data
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Crawler webCrawler = new Crawler(2000);
		webCrawler.buildCrawlerFromURL("https://www.raywenderlich.com/");
		System.out.println("List size : " + webCrawler.getNodes().size());
		Manager.saveObject("Raywanderlich2000URLs", webCrawler.getNodes());	
		System.out.println("File saved!");
		
		Collection<Node> nodesSaved = (Collection<Node>)Manager.loadSerialObject("Raywanderlich2000URLs");
		InvertedIndex invertedIndexObject = new InvertedIndex();
		invertedIndexObject.dataUpdated(nodesSaved);
		Manager.saveObject("Raywanderlich2000URLs", invertedIndexObject);				
   }


}

