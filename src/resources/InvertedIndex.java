package resources;

import java.util.LinkedList;
import java.util.Map;

import WebCrawler.WebCrawlerNode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;


class Tries implements Serializable  {
	char data;
	int count;
	boolean isEnd;
	int wordNumber;
	LinkedList<Tries> childNode;

	
	public Tries(char n) {
		data = n;
		count = 0;
		isEnd = false;
		wordNumber = -1;
		childNode = new LinkedList<Tries>();
	}

	
	public Tries getChild(char c) {
		if (childNode != null) {
			for (Tries child : childNode) {
				if (child.data == c) {
					return child;
				}
			}
		}
		return null;
	}
}

public class InvertedIndex implements Serializable {

	private static final boolean String = false;
	public static int currWordNumber;
	public static Tries root;
	public static HashMap<Integer, HashMap<String, Integer>> invertedIdxArray;

	public InvertedIndex() {
		root = new Tries(' ');
		invertedIdxArray = new HashMap<Integer, HashMap<java.lang.String, Integer>>();
		currWordNumber = 1;
	}

	
	public void updateWordOccurrence(int num, String url) {

	
		if (invertedIdxArray.get(num) != null) {

			
			if (invertedIdxArray.get(num).get(url) != null) {

				
				invertedIdxArray.get(num).put(url, invertedIdxArray.get(num).get(url) + 1);
			} else {

				
				invertedIdxArray.get(num).put(url, 1);
			}
		} else {

			
			HashMap<String, Integer> urlMap = new HashMap<java.lang.String, Integer>();
			urlMap.put(url, 1);
			invertedIdxArray.put(num, urlMap);
		}
	}

	public void insertWord(String word, String url) {

		
		int wordNum = search(word);
		
		if (wordNum != -1) {
			
			updateWordOccurrence(wordNum, url);
			return;
		}

		
		Tries curr = root;
		for (char c : word.toCharArray()) {
			Tries child = curr.getChild(c);
			if (child != null) {
				curr = child;
			} else {
				curr.childNode.add(new Tries(c));
				curr = curr.getChild(c);
			}
			curr.count++;
		}

		
		curr.isEnd = true;
		curr.wordNumber = currWordNumber;
		updateWordOccurrence(curr.wordNumber, url);
		
		currWordNumber++;
	}

	
	public void getAllInvertedIndexList() {

		System.out.println("Printing InvertedIndex List");
		for (Map.Entry<Integer, HashMap<String, Integer>> e : invertedIdxArray.entrySet()) {
			System.out.println(e);
		}
	}

	
	public int search(String word) {
		Tries curr = root;
		for (char c : word.toCharArray()) {
			if (curr.getChild(c) == null) {
				return -1;
			} else {
				curr = curr.getChild(c);
			}
		}
		if (curr.isEnd) {
			return curr.wordNumber;
		}

		return -1;
	}


	public void remove(String word, String url) {

		// check if the word is present
		int wordNum = search(word);
		if (wordNum == -1) {
			System.out.println("word not found");
			return;
		}

		
		invertedIdxArray.get(wordNum).remove(url);

		
		Tries curr = root;
		for (char c : word.toCharArray()) {
			Tries child = curr.getChild(c);
			if (child.count == 1) {
				curr.childNode.remove();
				return;
			} else {
				child.count--;
				curr = child;
			}
		}
		curr.isEnd = false;
	}
	
	public int findEditDistance(String s1, String s2) {
		int str1Length = s1.length();
		int str2Length = s2.length();
		int editDistanceMatrix[][] = new int[str1Length + 1][str2Length + 1];
		for (int i = 0; i <= str1Length; i++) {
			editDistanceMatrix[i][0] = i;
		}
		for (int i = 0; i <= str2Length; i++) {
			editDistanceMatrix[0][i] = i;
		}
		for (int i = 1; i < str1Length; i++) {
			for (int j = 1; j < str2Length; j++) {
				if (s1.charAt(i) == s2.charAt(j)) {
					editDistanceMatrix[i][j] = editDistanceMatrix[i - 1][j - 1];
				} else {
					editDistanceMatrix[i][j] = Math.min(Math.min((editDistanceMatrix[i - 1][j]) + 1, (editDistanceMatrix[i][j - 1]) + 1),
							(editDistanceMatrix[i - 1][j - 1]) + 1);
				}
			}
		}
		int editDistance = editDistanceMatrix[str1Length - 1][str2Length - 1];
		return editDistance;
	}

	
	public void loadData(Collection e, String url) {

		
		Iterator<String> itr = e.iterator();
		while (itr.hasNext()) {
			insertWord(itr.next(), url);
		}
	}


	public void dataUpdated(Collection<WebCrawlerNode> e) {

			
			Iterator<WebCrawlerNode> itr = e.iterator();
			WebCrawlerNode webCrawledNodes= null;
			while (itr.hasNext()) {
				
				webCrawledNodes = itr.next();
				
				Collection<String> eachWord = webCrawledNodes.getTextContentsTokens();
				Iterator<String> itr1 = eachWord.iterator();
				while(itr1.hasNext()){
					
					String input= itr1.next();
					
					insertWord(input,webCrawledNodes.getNodeBaseUrl());
				}
			}
		}


	public String[] getMostRelevantUrls(String word) {
		int docNum = search(word);
		System.out.println("Word is present at " + docNum);
		if (docNum != -1) {

			
			int topk = 5;
			int i = 0;

		
			HashMap<String, Integer> foundUrl = invertedIdxArray.get(docNum);

		
			final int[] frequency = new int[foundUrl.size()];
			for (final int value : foundUrl.values()) {
				frequency[i++] = value;
			}

			
			QuickSelectAlgo obj = new QuickSelectAlgo();
			final int kthLargestFreq = obj.findKthLargest(frequency, topk);

			
			final String[] topKElements = new String[topk];
			i = 0;
			for (final java.util.Map.Entry<String, Integer> entry : foundUrl.entrySet()) {
				if (entry.getValue().intValue() >= kthLargestFreq) {
					topKElements[i++] = entry.getKey();
					if (i == topk) {
						break;
					}
				}
			}
			return topKElements;
		} else {
			System.out.println("No word found");
			return null;
		}
	}

	public String[] predictWord(String prefix) {
		Tries current = root;
		int lengthOfWord = 0;
		String predictedWords[] = null;
		
		
		int i=0;
		while(i<prefix.length()){
			if (current.getChild(prefix.charAt(i)) == null) {
				System.out.println("No suggestion");
				return null;
			} else if (i == (prefix.length() - 1)) {
				current = current.getChild(prefix.charAt(i));
				System.out.println("Reading Character = "+ prefix.charAt(i));
				System.out.println("Current value =" + current.data + "===Current count= " + current.count);
				lengthOfWord = current.count;
			} else {
				current = current.getChild(prefix.charAt(i));
			}
			i++;
		}
		System.out.println("Number of words to be returned =" + lengthOfWord);

	
		predictedWords = new String[lengthOfWord];
		int j=0;
		while(j<predictedWords.length){
			predictedWords[j] = prefix;
			j++;
		}

		
		java.util.ArrayList<Tries> currentChildBuffer = new java.util.ArrayList<Tries>();
		java.util.ArrayList<Tries> nextChildBuffer = new java.util.ArrayList<Tries>();
		HashMap<Integer, String> wordCompleted = new HashMap<Integer, String>();

		
		int counter = 0;
		if (current.childNode != null) {
			for (Tries e : current.childNode) {
				currentChildBuffer.add(e);
			}
		}

		
		while (currentChildBuffer.size() != 0) {
			for (Tries tr : currentChildBuffer) {

				
				while (wordCompleted.get(counter) != null) {
					counter++;
				}
				int k =0;
				while(k<tr.count) {
					 System.out.println(
		 			 "e.data " + tr.data + "========boolena" + tr.isEnd +
					 "=========e.counter " + tr.count);
					 
					 
					if (tr.isEnd && k == (tr.count-1)) {
						wordCompleted.put(counter, "done");
					}
					 System.out.println("counter " + counter);
					predictedWords[counter] = predictedWords[counter] + tr.data;
					counter++;
					k++;
				}

				
				for (Tries t : tr.childNode) {
					nextChildBuffer.add(t);
				}
			}

			
			counter = 0;

			
			currentChildBuffer = new java.util.ArrayList<Tries>();
			if (nextChildBuffer.size() > 0) {
				currentChildBuffer = nextChildBuffer;
				nextChildBuffer = new java.util.ArrayList<Tries>();
			}
		}

		
		for (String str : predictedWords) {
			System.out.println("Predicted Words =" + str);
		}

		return predictedWords;
	}

	public ArrayList<String> findSuggestedWord(String searchWord) {
		String wordsStartingWithSameLetterList[] = predictWord(searchWord.substring(0, 1));
		ArrayList<String> suggestedWordList = new ArrayList<String>();
		for (String wordsStartingWithSameLetter : wordsStartingWithSameLetterList) {
			if (findEditDistance(searchWord, wordsStartingWithSameLetter) == 1) {
				suggestedWordList.add(wordsStartingWithSameLetter);
			}
		}
		return suggestedWordList;
	}

	
	public static void main(String[] arr) {
		InvertedIndex t = new InvertedIndex();
	}

}
