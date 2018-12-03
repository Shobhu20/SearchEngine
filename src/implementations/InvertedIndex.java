package implementations;

import java.util.LinkedList;
import java.util.Map;

import WebCrawler.WebCrawlerNode;
import constants.constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class InvertedIndex implements Serializable {


	public static Integer wordCountNumber;
	public static Trie startNode;
	public static HashMap<Integer, HashMap<String, Integer>> arrayTableMapping;

	public InvertedIndex() {
		arrayTableMapping = new HashMap<>();
		wordCountNumber = 1;
		startNode = new Trie(constants.EMPTY_CHARACTER);
	}
	
	public int findWord(String word) {
		Trie trie = startNode;
		for (int i = 0; i < word.length(); i++) {
			char c= word.charAt(i);
			Trie temp = trie.getImmediateChild(c);
			if (temp == null) {
				return constants.NOT_FOUND_RETURN_INT;
			} 
			trie = temp;
		}
		int returnVal = (trie.isLeafNode == true) ? trie.position : constants.NOT_FOUND_RETURN_INT;
		return returnVal;
	}

	public void deleteFromTrie(String str, String websiteLink) {
		int wordNum = findWord(str);
		if (wordNum != constants.NOT_FOUND_RETURN_INT) {
		arrayTableMapping.get(wordNum).remove(websiteLink);
		Trie trie = startNode;
		for (int i = 0 ; i < str.length(); i++) {
			char c = str.charAt(i);
			Trie child = trie.getImmediateChild(c);
			if (child.count == constants.FOUND_STRING_RETURN_INT_VALUE) {
				trie.childNode.remove();
				break;
			} else {
				child.count--;
				trie = child;
			}
		}
		}
	}
	
	public void dataUpdated(Collection<WebCrawlerNode> webCrawlerNodeCollection) {
			Iterator<WebCrawlerNode> itr = webCrawlerNodeCollection.iterator();
			WebCrawlerNode webCrawledNodes;
			while (itr.hasNext()) {
				webCrawledNodes = itr.next();
				Collection<String> eachWord = webCrawledNodes.getTextContentsTokens();
				Iterator<String> itr1 = eachWord.iterator();
				while(itr1.hasNext()){
					String input= itr1.next();
					addNewString(input,webCrawledNodes.getNodeBaseUrl());
				}}}


	public ArrayList<String> findSuggestedWord(String searchWord) {
		String wordsStartingWithSameLetterList[] = predictWord(searchWord.substring(0, 1));
		ArrayList<String> suggestedWordList = new ArrayList<String>();
		for (String wordsStartingWithSameLetter : wordsStartingWithSameLetterList) {
			if (EditDistanceAlgo.findEditDistance(searchWord, wordsStartingWithSameLetter) == 1) {
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
			if (current.getImmediateChild(prefix.charAt(i)) == null) {
				return null;
			} else if (i == (prefix.length() - 1)) {
				current = current.getImmediateChild(prefix.charAt(i));
				lengthOfWord = current.count;
			} else {
				current = current.getImmediateChild(prefix.charAt(i));
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
					if (tr.isLeafNode && k == (tr.count-1)) {
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
		int docNum = findWord(word);
		if (docNum != constants.NOT_FOUND_RETURN_INT) {
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
		int wordNum = findWord(str);
		if (wordNum > constants.NOT_FOUND_RETURN_INT) {
			wordOccurenceUpdatr(wordNum, websiteLink);
			return;
		}
		Trie existing = startNode;
		for (int i = 0; i< str.length(); i++) {
			char c=  str.charAt(i);
			Trie child = existing.getImmediateChild(c);
			if (child != null) {
				existing = child;
				existing.count++;
			} else {
				existing.childNode.add(new Trie(c));
				existing = existing.getImmediateChild(c);
				existing.count++;
			}
		}
		existing.isLeafNode = true;
		existing.position = wordCountNumber;
		wordOccurenceUpdatr(existing.position, websiteLink);
		wordCountNumber++;
	}
}
