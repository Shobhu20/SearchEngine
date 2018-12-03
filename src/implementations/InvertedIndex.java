package implementations;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import WebCrawler.Node;
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
	
	public void dataUpdated(Collection<Node> webCrawlerNodeCollection) {
			Iterator<Node> itr = webCrawlerNodeCollection.iterator();
			Node node;
			while (itr.hasNext()) {
				node = itr.next();
				ArrayList<String> eachWord = (ArrayList<String>) node.getTokens();
				for(String word : eachWord) {
					addNewString(word, node.getBaseURL());
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
	
	public String[] predictWord(String startWithAffix) {
		int wordlen = 0;
		String wordList[];
		int affixLength = startWithAffix.length();
		HashMap<Integer, String> completedWords = new HashMap<>();
		ArrayList<Trie> childCurr = new java.util.ArrayList<>();
		ArrayList<Trie> childNext = new java.util.ArrayList<>();
		
		Trie trie = startNode;
		for (int i = 0; i < affixLength; i ++ ) {
			if ((i+1) == (affixLength)) {
				trie = trie.getImmediateChild(startWithAffix.charAt(i));
				wordlen = trie.count;
			} 	else if (trie.getImmediateChild(startWithAffix.charAt(i)) == null) {
				return null;
			} else {
				trie = trie.getImmediateChild(startWithAffix.charAt(i));
			}
		}
		wordList = new String[wordlen];
		for(int j=0 ; j < wordList.length; j ++)
			wordList[j] = startWithAffix;
		int ctr = 0;
		if (trie.childNode != null) {
			for (Trie temp : trie.childNode) {
				childCurr.add(temp);
			}
		}
		
		while (childCurr.size() != 0) {
			for (Trie tr : childCurr) {
				while (completedWords.get(ctr) != null) {
					ctr++;
				}
				for(int k = 0 ; k < tr.count; k++) {
					if (tr.isLeafNode && k == (tr.count-1)) {
						completedWords.put(ctr, "finish");
					}
					wordList[ctr] = wordList[ctr] + tr.strData;
					ctr++;
				}
				for (Trie t : tr.childNode) {
					childNext.add(t);
				}
			}
			ctr = 0;
			
			childCurr = new java.util.ArrayList<>();
			if (childNext.size() > 0) {
				childCurr = childNext;
				childNext = new ArrayList<>();
			}
		}
		return wordList;
	}

	
	
	public void wordOccurenceUpdatr(int pos, String websiteLink) {
		HashMap<String, Integer> mappingTableEnty;
		HashMap<String, Integer> mapOfURlToOccurance = arrayTableMapping.get(pos);
		if (mapOfURlToOccurance != null) {
			if (mapOfURlToOccurance.get(websiteLink) != null) {
				int newValue= mapOfURlToOccurance.get(websiteLink) + 1;
				arrayTableMapping.get(pos).put(websiteLink, newValue); 
				}
			 else 
				 arrayTableMapping.get(pos).put(websiteLink, 1);
		} else {
			mappingTableEnty = new HashMap<>();
			mappingTableEnty.put(websiteLink, 1);
			arrayTableMapping.put(pos, mappingTableEnty);
		}
	}

	public String[] getMostRelevantUrls(String word) {
		int positionInTrie = findWord(word);
		if (positionInTrie == constants.NOT_FOUND_RETURN_INT)
			return null;
		HashMap<String, Integer> urlMap = arrayTableMapping.get(positionInTrie);
		int[] occuranceCount = new int[urlMap.size()];
		int i = 0;
		for (final int value : urlMap.values()) {
			occuranceCount[i++] = value;
		}
		QuickSelectAlgo quickSelct = new QuickSelectAlgo();
		int kthLargestFreq = quickSelct.findKthLargest(occuranceCount, constants.NUMBER_OF_PAGES_IN_QUICKSELECT);
		String[] topKElements = new String[constants.NUMBER_OF_PAGES_IN_QUICKSELECT];
		int j = 0;
		for (final Entry<String, Integer> entry : urlMap.entrySet()) {
			if (entry.getValue().intValue() >= kthLargestFreq) {
				topKElements[j++] = entry.getKey();
				if (j == constants.NUMBER_OF_PAGES_IN_QUICKSELECT) {
					break;
				}
			}
		}
		return topKElements;
	}
	
	public void addNewString(String str, String websiteLink) {
		int position = findWord(str);
		if (position > constants.NOT_FOUND_RETURN_INT) {
			wordOccurenceUpdatr(position, websiteLink);
		}
		else {
		Trie trie = startNode;
		for (int i = 0; i< str.length(); i++) {
			char c=  str.charAt(i);
			Trie child = trie.getImmediateChild(c);
			if (child == null) {
				trie.childNode.add(new Trie(c));
				trie = trie.getImmediateChild(c);
				trie.count++;
			} else {
				trie = child;
				trie.count++;
			}
		}
		trie.position = wordCountNumber;
		trie.isLeafNode = true;
		wordOccurenceUpdatr(trie.position, websiteLink);
		wordCountNumber++;}
	}
}
