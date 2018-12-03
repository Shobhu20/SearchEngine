package implementations;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

class Trie implements Serializable  {

	private static final long serialVersionUID = 1L;
	char strData;
	Integer count;
	boolean isLast;
	Integer NumberOfWord;
	LinkedList<Trie> childNode;

	public Trie getChild(char c) {
		if (childNode != null) {
			Iterator<Trie> itr = childNode.iterator();
			while(itr.hasNext()) {
				Trie t = itr.next();
				if (t.strData == c) return t;
			}
		}
		return null;
	}
	
	public Trie(char charForTrie) {
		strData = charForTrie;
		count = 0;
		isLast = false;
		NumberOfWord = -1;
		childNode = new LinkedList<Trie>();
	}
}