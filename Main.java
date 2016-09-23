import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * TCSS 342 - Data Structure
 * Assignment 3 - Compressed Literature. 
 */

/**
 * This class is capable of compressing a number of files 
 * and include methods used to test components of the program. 
 * @author Hsin-Jung Wang and Travis Arriola
 * @version 1.0
 */
public class Main {
	/** The name of the input file. */
	private static final String INPUT_FILE = "WarAndPeace.txt";
	
	/** The name of the output code file. */
	private static final String CODE_FILE = "WAPCodes.txt";
	
	/** The name of the output code file.*/ 
	private static final String COMPRESSED_FILE = "WAPCompressed.txt";
	
	/** The name of the output code file. */
	private static final String DECODE_FILE = "WAPDecode.txt";
    
	
	/** 
	 * This is the main method that will carry out 
	 * compression of a file usiing the CodingTree class.  
	 */
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException{
		
		// Import the text from the file. 
		File file = new File(INPUT_FILE);
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		
		// Turn letters to binary code. 
		File fout = new File(CODE_FILE);
		FileWriter fw = new FileWriter(fout);
		BufferedWriter bw = new BufferedWriter(fw);
		
		int ascii;
		while((ascii = br.read()) != -1) {
			sb.append((char)ascii);
		}
		String sourceMessage = sb.toString();
		String another = sb.toString();
		
		CodingTree ct = new CodingTree(sourceMessage);
		Iterator<?> it = ct.myCodes.entrySet().iterator();
		
		bw.write("{");
		if(it.hasNext()) {
			Map.Entry element = (Map.Entry) it.next();
			Object key = element.getKey();
			Object value = element.getValue();
			bw.write(key + "=" + value);
		}
		
		while(it.hasNext()){
			bw.write(", ");
			Map.Entry element = (Map.Entry) it.next();
			Object key = element.getKey();
			Object value = element.getValue();
			bw.write(key + "=" + value);
		}
		
		bw.write("}");
		bw.close();
		fw.close();
		
		// To compress file 
		File fout1 = new File(COMPRESSED_FILE);
		FileWriter fw1 = new FileWriter(fout1);
		BufferedWriter bw1 = new BufferedWriter(fw1);
		long runTime = System.currentTimeMillis();
		ct.compress(another);
		long runTime2 = System.currentTimeMillis();
		encodeToFile(ct.bits);

		// To decode file
		decodeToFile();
		
		double compressedPercentage = ((double) ct.bits.length()/8) / ((double) sourceMessage.length()) * 100;
		System.out.println("**************** Statistics ****************");
		System.out.println("The size of the compressed message is: " + (double) (ct.bits.length()/8) / 1000 + " Kilobyte(s)");
		System.out.println("The size of the message is: " + (double) sourceMessage.length() / 1000 + " Kilobyte(s)");
		System.out.println("The compression ratio is: " + String.format("%.02f", compressedPercentage) + " Percent");
		System.out.println("The elapsed time for the compression is: " + (runTime2 - runTime) + " ms");
		
		bw1.close();
		br.close();
		
//		// This is the test case
//		testCodingTree();
	}
	
	public static void encodeToFile(String binaryString) throws IOException{
		int part = binaryString.length() / 7;
		int remainder = binaryString.length() % 7;
		
		File f = new File(COMPRESSED_FILE);
		FileWriter writer;
		writer = new FileWriter(f);
		BufferedWriter bwriter = new BufferedWriter(writer);
		for(int i = 0; i < part; i++) {
			bwriter.write((char)(Byte.parseByte(binaryString.substring(i*7, i* 7 + 7), 2)));
		}
		
		if(remainder != 0) {
			String tmp = binaryString.substring(binaryString.length() - remainder, binaryString.length());
			while(tmp.length() < 7){
				tmp = tmp + "0";
			}
			bwriter.write((char) Byte.parseByte(tmp, 2));
		}
		bwriter.close();
	}
	
	
	/** This method serves as the test case for my CodingTree class */ 
	@SuppressWarnings({ "rawtypes", "unused" })
	private static void testCodingTree() {
//		String testCase = "ANNA HAS A BANANA IN A BANDANA";
		String testCase = "\u0000";

		CodingTree ct = new CodingTree(testCase);
		
		Iterator<?> it = ct.myCodes.entrySet().iterator();
		
		System.out.println("The codes book: ");
		System.out.println("******Codes******");
		while(it.hasNext()){
			Map.Entry element = (Map.Entry) it.next();
			Object key = element.getKey();
			Object value = element.getValue();
			System.out.println(key + "=" + value);
		}
		long runTime = System.currentTimeMillis();
		ct.compress(testCase);
		long runTime2 = System.currentTimeMillis();
		
		System.out.println("****************");
		System.out.println("The size of the message is: " + testCase.length() + " Byte(s)");
		System.out.println("The size of the compressed message is: " + ct.bits.length()/8 + " Byte(s)");
		System.out.println("The time cost for the compression is: " + (runTime2 - runTime) + " ms");
	}
	
	/** 
	 * To decode file	 
	 * @throws IOException 
	 * */
	private static void decodeToFile() throws IOException {	
		CodingTree tree = new CodingTree(); 
		Map<Character, String> myMap = new HashMap<Character, String>();
		
		// Import the code from the file. 
		File codefile = new File(CODE_FILE);
		StringBuilder codesb = new StringBuilder();
		BufferedReader codebr = null;
		
		codebr = new BufferedReader(new FileReader(codefile));
		int binary;
		while((binary = codebr.read()) != -1) {
			codesb.append((char)binary);
		}
		codebr.close();
		String codeMessage = codesb.toString();
		
		codeMessage = codeMessage.substring(1, codeMessage.length() -1);
		
		String[] pairs = codeMessage.split(", ");
		for (int i=0;i<pairs.length;i++) {
		    String pair = pairs[i];
		    myMap.put(pairs[i].charAt(0), pairs[i].substring(2));
		}
		
		String tmp = tree.decode(tree.decodeHelper(COMPRESSED_FILE), myMap);
		
		// Open the decode file to be written to. 
		File decodefout = new File(DECODE_FILE);
		FileWriter decodefw = null;
		decodefw = new FileWriter(decodefout);			

		BufferedWriter decodebw = new BufferedWriter(decodefw);

		decodebw.write(tmp);
		decodebw.close();
		
		
	}

}
