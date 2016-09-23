import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * TCSS 342 - Data Structure
 * Assignment 3 - Compressed Literature. 
 */

/**
 * This class builds a Huffman tree and includes 
 * methods to compress, decode, and encode 
 * @author Hsin-Jung Wang and Travis Arriola
 * @version 1.0
 */
public class CodingTree  {
	
	/** The name of the output code file.*/ 
	private static final String COMPRESSED_FILE = "CompressedWarAndPeace.txt";
	
	/** Alphabet size of extended ASCII */
    private static final int R = 256;
	
	/** A map of characters in the message to binary codes 
	    (Strings of ‘1’s and ‘0’s) created by your tree */
	public Map<Character, String> myCodes;
	
	/** The message encoded using the Huffman codes */
	private int[] myBits; 
	
	/** This is the Huffman node. */
	private HuffmanNode myNode; 
	
	/** This is the text file in binary. */
	public String bits;
	
	/** This is an empty constructor. */
	public CodingTree() {
		
	}
	
	/** This is an overloaded constructor that takes 
	 * the text of a message to be compressed. 
	 * The constructor is responsible for calling 
	 * all private methods that carry out the 
	 * Huffman coding algorithm. 
	 * @param message is the content from the original text file. 
	 */
	public CodingTree(String message) {
		// calculate the frequency of the message first
		calculateFrequency(message);
		
		// Build the Huffman Tree.
		buildTree(); 

		// Build the map
		myCodes = new HashMap<Character, String>();
		buildMap("", myNode); 
		
		// convert the message to new character array
		// then appends the characters together using string builder.
		StringBuilder sb = new StringBuilder(); 
		char[] charArray = message.toCharArray();
		for(char c: charArray) {
			sb.append(myCodes.get(c));
		}	
	}
	
	
	/**
	 * If the node is a leaf, the map would store the ASCII value 
	 * and the binary code of the leaf node. Else, it recurses pre-orderly
	 * by adding 0 when going down the left sub-tree and adding 1 
	 * when going down the right sub-tree. 
	 * @param binaryString is the binary string. 
	 * @param theNode is the Huffman node. 
	 */
	public void buildMap(String binaryString, HuffmanNode theNode) {
		if(theNode != null) {
			if(theNode.left == null && theNode.right == null) {
				myCodes.put((char) theNode.key, binaryString);
			} else {
				buildMap(binaryString + "0", theNode.left);
				buildMap(binaryString + "1", theNode.right);
			}
		}
	}
	
	/**
	 * constructs a HuffmanTree using an array of frequency (myBit[])
	 * where myBits is the count of the character with an ASCII value
	 */
	private void buildTree() {
//		Queue<HuffmanNode> myHeap = new PriorityQueue<HuffmanNode>(); 
		MyPriorityQueue<HuffmanNode> myHeap = new MyPriorityQueue<HuffmanNode>(); 

		for (int i = 0; i < myBits.length; i++) {
			if(myBits[i] > 0) {
				myHeap.add(new HuffmanNode(i, myBits[i]));
			}
		}
		myHeap.add(new HuffmanNode(myBits.length,1));
		while(myHeap.size() > 1) {
			HuffmanNode left = myHeap.remove();
			HuffmanNode right = myHeap.remove();
			HuffmanNode combine = new HuffmanNode(left.freq + right.freq, left, right);
			myHeap.add(combine);
		}
		myNode = myHeap.remove(); 
	}
	
	/**
	 * This method calculate the frequency of 
	 * each appeared letter. 
	 * @param message is the inputed text. 
	 */
	private void calculateFrequency(String message) {
		// Initialize an array to the size of 256 
		myBits = new int[R];
		Arrays.fill(myBits, 0);
		
		for(int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);
			myBits[c & 0xff]++; 
		}
	}
	
	/** 
	 * This method compressed the file into
	 *  binary codes.
	 * @param message is the text from the file. 
	 */
	public void compress(String message){
		char[] msg = message.toCharArray();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < msg.length; i++){
	      if(myCodes.get(msg[i]) == null)
	    	  continue;
			sb.append(myCodes.get(msg[i]));
		}
		bits = sb.toString(); 
	}
		
	//decode helper
	public String decodeHelper(String compressedText) throws IOException{

		File fin = new File(compressedText);
		BufferedReader br = new BufferedReader(new FileReader(fin));
		StringBuilder converter = new StringBuilder();
		int n;
		while((n = br.read()) != -1){
			
			String s = Integer.toBinaryString(n);
			//check whether s.length == 7
			while(s.length() < 7){
				s = "0" + s;
			}
			converter.append(s);
			
		}
		br.close();
		return converter.toString();
	}
	
	//(optional) decoder
	public String decode(String bits, Map<Character, String> codes){
		Map<String, Character> decoder = new HashMap<String, Character>();
		Iterator it = codes.keySet().iterator();
		while(it.hasNext()){
			Character letter = (Character)it.next();
			String val = codes.get(letter);
			decoder.put(val, letter);
		}
		StringBuilder sb = new StringBuilder();
		int start = 0, end = 1;
		while(start < bits.length() && end < bits.length()){
			String s = bits.substring(start,end);
			if(decoder.containsKey(s)){
				sb.append(decoder.get(s));
				start = end; 
				end = start + 1;
			}else{
				end++;
			}
		}
		if(start < bits.length()){
			if(decoder.containsKey(bits.substring(start))){
				sb.append(decoder.get(bits.substring(start)));
			}
		}
		return sb.toString();
	}
	
	/**
	 * This program stores a single HuffmanNode that will be used
	 * to construct a binary tree. The HuffmanNode will contain
	 * an integer frequency and a character's ASCII value. 
	 * @author Hsin-Jung Wang and Travis Arriola
	 * @version 1.0
	 */
	class HuffmanNode implements Comparable<HuffmanNode> {
		public int key;       		// ASCII value
		public int freq;           	// Frequency
		public HuffmanNode left;  	// Left sub-tree HuffmanNode
		public HuffmanNode right; 	// Right sub-tree HuffmanNode
		
		/**
		 * Constructs a leaf HuffmanNode that has an ASCII value of -1 and 
		 * a frequency of 0.
		 */
		public HuffmanNode() {
			this(-1, 0, null, null);
		}
		
		/**
		 * Constructs a leaf HuffmanNode that contains an ASCII value and a integer 
		 * @param key is the character to be encoded.
		 * @param freq is the frequency of the key.
		 */
		public HuffmanNode(int key, int freq) {
			this(key, freq, null, null);
		}

		/**
		 * Constructs a branch HuffmanNode that contains an ASCII value of -1 and a
		 * given integer frequency and allows users to access left and right sub-trees. 
		 * @param freq is the frequency of the key.
		 * @param left is the left leaf node. 
		 * @param right is the right leaf node. 
		 */
		public HuffmanNode(int freq, HuffmanNode left, HuffmanNode right) {
			this(-1, freq, left, right);
		}

		/**
		 * Constructs a branch HuffmanNode that contains a given ASCII value, integer
		 * frequency and allows users to access the left and right sub-trees. 
		 * @param key is the character to be encoded.
		 * @param freq is the frequency of the key.
		 * @param left is the left leaf node. 
		 * @param right is the right leaf node. 
		 */
		public HuffmanNode(int key, int freq, HuffmanNode left, HuffmanNode right) {
			this.key = key;
			this.freq = freq;
			this.left = left;
			this.right = right;
		}
		
		/**
		 * Compares the frequency stored in the two HuffmanNode.
		 * The HuffmanNode that is greater has larger integer frequency.
		 * @param other is the other HuffmanNode. 
		 */
		public int compareTo(HuffmanNode other) {
			return freq - other.freq;
		}
	}
	
}
