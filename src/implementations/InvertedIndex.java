package implementations;

import java.util.LinkedList;
import java.util.Map;

import CrawlerPackage.Node;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class InvertedIndex implements Serializable {

	private static final int FOUND_STRING_RETURN_INT_VALUE = 1;
	private static final int NOT_FOUND_RETURN_INT = -1;
	public static Integer wordCountNumber;
	public static Trie startNode;
	public static HashMap<Integer, HashMap<String, Integer>> arrayTableMapping;
	static int count = 0;

	public InvertedIndex() {
		arrayTableMapping = new HashMap<Integer, HashMap<java.lang.String, Integer>>();
		wordCountNumber = 1;
		startNode = new Trie(' ');
	}

	public void getAllInvertedIndexList() {
		for (Map.Entry<Integer, HashMap<String, Integer>> entry : arrayTableMapping.entrySet()) {
			System.out.println(entry);
		}
	}
	
	public int search(String word) {
		Trie curr = startNode;
		for (int i = 0; i < word.length(); i++) {
			char c= word.charAt(i);
			if (curr.getChild(c) == null) {
				return NOT_FOUND_RETURN_INT;
			} else {
				curr = curr.getChild(c);
			}
		}
		if (curr.isLast) {
			return curr.position;
		}
		return NOT_FOUND_RETURN_INT;
	}

	public void deleteFromTrie(String str, String websiteLink) {
		int wordNum = search(str);
		if (wordNum == NOT_FOUND_RETURN_INT) {
			System.out.println("the given string is not existing in the trie so cannot delete");
			return;
		}
		arrayTableMapping.get(wordNum).remove(websiteLink);
		Trie trie = startNode;
		for (int i = 0 ; i < str.length(); i++) {
			char c = str.charAt(i);
			Trie child = trie.getChild(c);
			if (child.count == FOUND_STRING_RETURN_INT_VALUE) {
				trie.childNode.remove();
				return;
			} else {
				child.count--;
				trie = child;
			}
		}
		trie.isLast = false;
	}
	
	public int findEditDistance(String String1, String String2) {
		Integer str1Length = String1.length();
		Integer str2Length = String2.length();
		Integer editDistanceMatrix[][] = new Integer[str1Length + 1][str2Length + 1];
		
		for (int i = 0; i <= Math.max(str1Length, str2Length); i++) {
			if(!(i > str1Length))
				editDistanceMatrix[i][0] = i;
			if(!(i > str2Length))
				editDistanceMatrix[0][i] = i;
		}
		
		for (int i = 1; i < str1Length; i++) {
			for (int j = 1; j < str2Length; j++) {
				if (String1.charAt(i) == String2.charAt(j)) {
					editDistanceMatrix[i][j] = editDistanceMatrix[i - 1][j - 1];
				} else {
					editDistanceMatrix[i][j] = Math.min(Math.min( (editDistanceMatrix[i][j - 1]) + 1, (editDistanceMatrix[i - 1][j]) + 1),(editDistanceMatrix[i-1][j-1]) + 1);
				}
			}
		}
		int editDistance = editDistanceMatrix[str1Length - 1][str2Length - 1];
		return editDistance;
	}

	public void loadData(Collection collection, String url) {
		Iterator<String> itr = collection.iterator();
		while (itr.hasNext()) {
//			count++;
			addNewString(itr.next(), url);
		}
//		System.out.println("Number of words: " + count);
	}

	public void dataUpdated(Collection<Node> e) {
			Iterator<Node> itr = e.iterator();
			Node webCrawledNodes;
			while (itr.hasNext()) {
				webCrawledNodes = itr.next();
				Collection<String> eachWord = webCrawledNodes.getTokens();
				Iterator<String> itr1 = eachWord.iterator();
				while(itr1.hasNext()){
//					count++;
					String input= itr1.next();
					addNewString(input,webCrawledNodes.getBaseURL());
				}
			}
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
	
	public String[] predictWord(String prefix) {
		Trie current = startNode;
		int lengthOfWord = 0;
		String predictedWords[] = null;
		int i=0;
		while(i<prefix.length()){
			if (current.getChild(prefix.charAt(i)) == null) {
				return null;
			} else if (i == (prefix.length() - 1)) {
				current = current.getChild(prefix.charAt(i));
				lengthOfWord = current.count;
			} else {
				current = current.getChild(prefix.charAt(i));
			}
			i++;
		}
		predictedWords = new String[lengthOfWord];
		int j=0;
		while(j<predictedWords.length){
			predictedWords[j] = prefix;
			j++;
		}
		ArrayList<Trie> currentChildBuffer = new java.util.ArrayList<Trie>();
		ArrayList<Trie> nextChildBuffer = new java.util.ArrayList<Trie>();
		HashMap<Integer, String> wordCompleted = new HashMap<Integer, String>();
		int counter = 0;
		if (current.childNode != null) {
			for (Trie e : current.childNode) {
				currentChildBuffer.add(e);
			}
		}

		
		while (currentChildBuffer.size() != 0) {
			for (Trie tr : currentChildBuffer) {
				while (wordCompleted.get(counter) != null) {
					counter++;
				}
				int k =0;
				while(k<tr.count) {
					if (tr.isLast && k == (tr.count-1)) {
						wordCompleted.put(counter, "done");
					}
					 System.out.println("counter " + counter);
					predictedWords[counter] = predictedWords[counter] + tr.strData;
					counter++;
					k++;
				}
				for (Trie t : tr.childNode) {
					nextChildBuffer.add(t);
				}
			}
			counter = 0;
			
			currentChildBuffer = new java.util.ArrayList<Trie>();
			if (nextChildBuffer.size() > 0) {
				currentChildBuffer = nextChildBuffer;
				nextChildBuffer = new java.util.ArrayList<Trie>();
			}
		}
		return predictedWords;
	}


	
	
	public void wordOccurenceUpdatr(int pos, String websiteLink) {
		HashMap<String, Integer> arrayTableEntry;
		if (arrayTableMapping.get(pos) != null) {
			if (arrayTableMapping.get(pos).get(websiteLink) != null) arrayTableMapping.get(pos).put(websiteLink, arrayTableMapping.get(pos).get(websiteLink) + 1);
			 else arrayTableMapping.get(pos).put(websiteLink, 1);
		} else {
			arrayTableEntry = new HashMap<>();
			arrayTableEntry.put(websiteLink, 1);
			arrayTableMapping.put(pos, arrayTableEntry);
		}
	}

	
	public static void main(String[] arr) {
		InvertedIndex t = new InvertedIndex();
	}
	

	public String[] getMostRelevantUrls(String word) {
		int docNum = search(word);
		if (docNum != NOT_FOUND_RETURN_INT) {
			int topk = 5;
			int i = 0;
			HashMap<String, Integer> foundUrl = arrayTableMapping.get(docNum);
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
			return null;
		}
	}
	
	public void addNewString(String str, String websiteLink) {
		int wordNum = search(str);
		if (wordNum > NOT_FOUND_RETURN_INT) {
			count++;
			wordOccurenceUpdatr(wordNum, websiteLink);
			return;
		}
		System.out.println("Number of words: " + count);
		Trie existing = startNode;
		for (int i = 0; i< str.length(); i++) {
			char c=  str.charAt(i);
			Trie child = existing.getChild(c);
			if (child != null) {
				existing = child;
				existing.count++;
			} else {
				existing.childNode.add(new Trie(c));
				existing = existing.getChild(c);
				existing.count++;
			}
		}
		existing.isLast = true;
		existing.position = wordCountNumber;
		wordOccurenceUpdatr(existing.position, websiteLink);
		wordCountNumber++;
	}
}
