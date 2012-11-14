import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.io.*;
import java.util.Comparator;

/** 
 * Running command for this program:
 * ssh -X s3300154@yallara.cs.rmit.edu.au
 *  All the testing queries are:
    BM25 format: java search -BM25 -q 401 -n 20 lexicon invlists map foreign minorities germany
	BM25 format: java search -BM25 -q 402 -n 20 lexicon invlists map behavioral genetics
	BM25 format: java search -BM25 -q 403 -n 20 lexicon invlists map osteoporosis
	BM25 format: java search -BM25 -q 405 -n 20 lexicon invlists map cosmic events
	BM25 format: java search -BM25 -q 408 -n 20 lexicon invlists map tropical storms
			
	PRF  format: java search -PRF -q 401 -n 20 -R 10 -E 20 lexicon invlists map foreign minorities germany
	PRF  format: java search -PRF -q 402 -n 20 -R 10 -E 20 lexicon invlists map behavioral genetics
	PRF  format: java search -PRF -q 403 -n 20 -R 10 -E 20 lexicon invlists map osteoporosis
	PRF  format: java search -PRF -q 405 -n 20 -R 10 -E 20 lexicon invlists map cosmic events
	PRF  format: java search -PRF -q 408 -n 20 -R 10 -E 20 lexicon invlists map tropical storms
 */

class DecreaseCompare implements Comparator
{
    public int compare(Object o1,Object o2)
    {
    	Double i1=(Double)o1;
    	Double i2=(Double)o2;
        return -i1.compareTo(i2);
    }
}

class ArrayListComparator implements Comparator {
	public int compare(Object o1, Object o2) {
		DocREScore p1 = (DocREScore) o1;
		DocREScore p2 = (DocREScore) o2;
		if (p1.score < p2.score)
			return 1;
		else if (p1.score > p2.score)
			return -1;
		else
			return 0;
	}
}

public class search {
	// Using this HashMap to store lexicon dictionary
	private HashMap<String, LexiconNode> lexiconHahsMap = new HashMap<String, LexiconNode>();
	// Using this HashMap to store line number and word relation
	private HashMap<String, String> lineNumToWordHahsMap = new HashMap<String, String>();
	// Using this HashMap to store assign document number to real document number
	private HashMap<String, DocumentNode> docNumHahsMap = new HashMap<String, DocumentNode>();
	// Using this HashMap to store document score
	private HashMap<String, Double> docScoreHashMap = new HashMap<String, Double>();
	// Document Average length
	private double docAveLength = 0.0;
	// Using this TreeMap to store top-n-order document-score
	private TreeMap<Double,String> nTopOrderDocTreeMap = new TreeMap<Double,String>(new DecreaseCompare());
	// Using for store terms in query with its docNum and fdt (gain from invlists file on disk)
	private HashMap<String,InvertIndexNode> queryTermHashMap;
	// Using this ArrayList to store R-E retrieved document number 
	private ArrayList<DocREScore> tREScoreArrayList = new ArrayList<DocREScore>();

	/**
	 * The main function control the whole process for searching operation
	 */
	public static void main(String[] args) {
		//user command line input
		search tSearch = new search();
		ArrayList<String> inputArrayList = new ArrayList<String>();

		//User input format:
		//BM25 format: search -BM25 -q 401 -n 20 lexicon invlists map foreign minorities germany
		//PRF  format: search -PRF -q 401 -n 20 -R 10 -E 20 lexicon invlists map foreign minorities germany    
		for (String s : args) {
//			System.out.println(s);
			inputArrayList.add(s);
		}
		
		//get query type  
		String searchType = inputArrayList.get(0).substring(1);
		//query number
		String queryNo = inputArrayList.get(2);
		//number of document returned
		int n = Integer.parseInt(inputArrayList.get(4));
		// number of document using for query expansion
		int R = 0;
		// number of words for query expansion
		int E = 0;
		//set values for PRF operation
		if(searchType.equals("PRF")){
			R = Integer.parseInt(inputArrayList.get(6));
			E = Integer.parseInt(inputArrayList.get(8));
		}
		// using for precision
		int precision = 10;
		
		//setting query position for different query type
		int queryPos =0;
		if(searchType.equals("BM25")){
			queryPos = 8;
		}
		else if(searchType.equals("PRF")){
			queryPos = 12;
		}
		//query string
		String queryStr = "";
		for (int i = queryPos; i < inputArrayList.size(); i++) {
			queryStr = queryStr + inputArrayList.get(i)+" ";
		}
		//Testing input values
//		System.out.println("searchType: "+ searchType);
//		System.out.println("queryNo: "+ queryNo);
//		System.out.println("n: "+ n);
//		System.out.println("R: "+ R);
//		System.out.println("E: "+ E);
//		System.out.println("precision: "+ precision);
//		System.out.println("queryStr: "+ queryStr);
		
		//Running time testing
		long starttime;
		long endtime;

		// time testing
		starttime = System.currentTimeMillis();
		// 1. Generate term dictionary HashMap
		tSearch.readLexiconFileToHashMap("lexicon");

		// 2. Generate document map relationship
		tSearch.docNumMapFileToHashMap("map");
		// starttime = System.currentTimeMillis();

		// 3 terms searching and calculate document score process(according to
		// the query terms)
		// filter the input query
		// ????????? be careful the order of these two operation and also have
		// to change stop_list program:
		// SearchQueryExpansion-readStopWordsFileToHashMap function
		String filterQueryStr = tSearch.StringFilter(queryStr);
		// perform the stemming operation on query string
		// Stemmer stem = new Stemmer();
		// filterQueryStr = stem.stemmingOperation(filterQueryStr);

		// call generate query result function
		// note: last parameter is the number of the relevant documents, I will use this parameter for
		// R and E testing experiments, so here just give 1, it is meaningless value for this calling
		tSearch.generateQueryResult(searchType, queryNo, filterQueryStr, n, R,
				E, precision, 1);
		// display the total execution time
		endtime = System.currentTimeMillis();
		System.out.println("generate N-Top-Ordered-Document time is: "
				+ (endtime - starttime) / 1000.00 + "s");

		// investigating the query expansion parameters for R and E
		// saving the result in REScoreFile
		if (searchType.equals("PRF")) {
			Comparator comp = new ArrayListComparator();
			Collections.sort(tSearch.tREScoreArrayList, comp);
			for (int i = 0; i < tSearch.tREScoreArrayList.size(); i++) {
				System.out.println("R:" + tSearch.tREScoreArrayList.get(i).R
						+ " E:" + tSearch.tREScoreArrayList.get(i).E
						+ " Retrieved:"
						+ tSearch.tREScoreArrayList.get(i).findDocNum);
			}
//			FileOperation.writeREScoreToFile(tSearch.tREScoreArrayList,
//					"REScoreFile", false);
		}
	}
	
	/**
	 * @param args
	 */
	public void generateQueryResult(String searchType,String queryNo,String filterQueryStr,int n
			,int R,int E,int precision,int queryRelDocNum){
		int para_1 = 0;
		if(searchType.equals("BM25"))
			para_1 = n;
		else if (searchType.equals("PRF"))
			para_1 = R;
		
		//generate n top dorder documents
		this.generateNTopOrderedDocument(para_1,filterQueryStr, "invlists");
		
		QueryTermsDetailInDocument tQueryTermsDetailInDocument = new QueryTermsDetailInDocument();
		if(searchType.equals("BM25")){
			//4. display ordered top R document
			System.out.println("Query String is: "+filterQueryStr);
			System.out.println("Document Average Length is: "+this.docAveLength);
			this.displayTopDocument(queryNo);
			
//			//display terms detail in each retrieval document
//			System.out.println("************************************");
//			tQueryTermsDetailInDocument.printQueryTermsDetailInDocuments(queryNo, filterQueryStr
//					, this.docNumHahsMap, this.queryTermHashMap, this.nTopOrderDocTreeMap);
//			System.out.println("************************************");
			
			//display common documents compared with human judgment
			tQueryTermsDetailInDocument.matchDocumentCollection(searchType,queryNo,R,E,precision
					,this.docNumHahsMap, this.nTopOrderDocTreeMap,0);
		}
		else if (searchType.equals("PRF")){
			//For Query Expansion
			SearchQueryExpansion tSearchQueryExpansion = new SearchQueryExpansion();
			String tQuery1 = filterQueryStr;
			DocScoreNode[] expTermSet = tSearchQueryExpansion.generateWordsExpansionCollection(this.nTopOrderDocTreeMap
					,this.lineNumToWordHahsMap , R, E,tQuery1);
			
			System.out.println("----------------------------------------");
			//add the expansion terms to generate new query
			String newQuery = filterQueryStr;
			for(int i=0; i<expTermSet.length ; i++){
				newQuery = newQuery + " "+expTermSet[i].docDescription;
			}
			System.out.println("The Expansion Query is: "+ newQuery);
			
			//reset variables for new query
			this.docScoreHashMap = new HashMap<String, Double>();
			this.nTopOrderDocTreeMap = new TreeMap<Double,String>(new DecreaseCompare());
			//
			//terms searching and calculate document score process(according to the query terms)
			this.generateNTopOrderedDocument(n, newQuery, "invlists");

			//display ordered top R document
			this.displayTopDocument(queryNo);
			
//			//display terms detail in each retrieval document
//			System.out.println("************************************");
//			tQueryTermsDetailInDocument.printQueryTermsDetailInDocuments(queryNo, newQuery
//					, this.docNumHahsMap, this.queryTermHashMap, this.nTopOrderDocTreeMap);
//			System.out.println("************************************");
			
			//display common documents compared with human judgment
			DocREScore tDocREScore = 
					tQueryTermsDetailInDocument.matchDocumentCollection(searchType,queryNo,R,E,precision
					,this.docNumHahsMap, this.nTopOrderDocTreeMap,queryRelDocNum);
			//
			this.tREScoreArrayList.add(tDocREScore);
		}
	}
	
	/**
	 * @param args
	 */
	public boolean chechFileExist(String filePathName) {
		 java.io.File file = new java.io.File(filePathName);   
	      if(file.exists()){   
	          return true;   
	      }else{      
	          System.out.println("Sorry the file:"+filePathName+" not exists,please input the right path and name again!");  
	          return false;
	      }   
	}

	/**
	 * @param args
	 */
	public void readLexiconFileToHashMap(String fileName) {
		//read information from lexicon file
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
			// 1. String split method
			String[] strArray;
			LexiconNode tLexiconNode;
			// int linerNumber = 0;
			while ((curLineString = reader.readLine()) != null) {
				// String split method
				strArray = curLineString.split(" " + "|\n");
				//put term frequency within document collection 
				//and term position in invert list file into LexiconNode
				tLexiconNode = new LexiconNode();
				tLexiconNode.ft = Integer.valueOf(strArray[1]).intValue();
				tLexiconNode.lexiconPosition = Integer.valueOf(strArray[2]).intValue();
				//put term and LexiconNode into lexiconHashMap
				lexiconHahsMap.put(strArray[0], tLexiconNode);
				//store line number and word relation using for query expansion operation
				this.lineNumToWordHahsMap.put(strArray[2], strArray[0]);
			}
			// System.out.println("Total line number are: "+linerNumber);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public void docNumMapFileToHashMap(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
			// 1. String split method
			String[] strArray;
			//
			DocumentNode tDocumentNode;
			int accDocLength = 0;
			while ((curLineString = reader.readLine()) != null) {
				// String split method
				strArray = curLineString.split(" " + "|\n");
				//map file format: assign docNum-docNum-docLength-startPos-endPos
				//        example: 1 LA010189-0001 7071 4 107
				tDocumentNode = new DocumentNode();
				tDocumentNode.docOriginalNo = strArray[1];
				tDocumentNode.docLength = Integer.valueOf(strArray[2]).intValue();
				accDocLength = accDocLength + tDocumentNode.docLength;
				tDocumentNode.startPosition = Integer.valueOf(strArray[3]).intValue();
				tDocumentNode.endPosition = Integer.valueOf(strArray[4]).intValue();
				docNumHahsMap.put(strArray[0], tDocumentNode);
			}
			//calculate document average length
			this.docAveLength = accDocLength / 35472;
			
			// System.out.println("Total line number are: "+linerNumber);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	
	/**
	 * generate top-n-ranked document form collection
	 */
	public String generateNTopOrderedDocument(int num, String inputQuery,String indexFileName){
		//1. terms searching and calculate document score process(according to the query terms)
		this.displayQueryInfoImprove(inputQuery, "invlists");
				
		//2. generate top-ranked document by minHeapOrder Algorithm
		MinHeapOrderAlgorithm tMinHeapOrderAlgorithm = new MinHeapOrderAlgorithm();
		//get the size of the document Set which will be ordered
		int docSetSize = 0;
		if(num <= this.docScoreHashMap.size()){
			docSetSize = num;
		}
		else{
			docSetSize = this.docScoreHashMap.size();
		}
		//
		DocScoreNode[] curDoc = new DocScoreNode[docSetSize];
		//
		Iterator<String> iterator = this.docScoreHashMap.keySet().iterator();
		int curPos = 1;
		DocScoreNode tDocScoreNode;
		while (iterator.hasNext()) {
			//get current document number
			String key = iterator.next();
			if(curPos <= docSetSize){
				tDocScoreNode = new DocScoreNode();
				tDocScoreNode.docNumber = Integer.parseInt(key);
				tDocScoreNode.docScore = this.docScoreHashMap.get(key).doubleValue();
				curDoc[curPos-1] = tDocScoreNode;
				//when the size of DocScoreNode array equals docSetSize.
				//initial Min-Heap Order Algorithm
				if(curPos == docSetSize)
					tMinHeapOrderAlgorithm.orderingDocCollection(curDoc);
			}
			else{
				//compare the score value of current document with the first element of curDoc array
				//Because the value of first element is the smallest 
				if(this.docScoreHashMap.get(key).doubleValue() > curDoc[0].docScore){
					//replace the value of the first element
					curDoc[0].docNumber = Integer.parseInt(key);
					curDoc[0].docScore = this.docScoreHashMap.get(key).doubleValue();
					//reorder the curDoc array
					tMinHeapOrderAlgorithm.orderingDocCollection(curDoc);
				}
			}
			curPos = curPos + 1; 
		}
				
		//3. order the top-ranked document by their document score
		for(int i=0; i<curDoc.length ; i++){
			nTopOrderDocTreeMap.put(new Double(curDoc[i].docScore), String.valueOf(curDoc[i].docNumber));
		}
		
		return "1";
	}
	
	/**
	 * @param args
	 */
	public HashMap<String,InvertIndexNode> searchTermInfoImprove(String inputQuery, String indexFileName) {
		File indexFile = new File(indexFileName);
		BufferedReader indexReader = null;
		try {
			int termDictLine = 0;
			
			HashMap<String,InvertIndexNode> queryHashMap = new HashMap<String,InvertIndexNode>();
			//format: line number - term
			TreeMap<Integer,String> tTreeMap = new TreeMap<Integer,String>();
			String curWord = "";
			//Using String StringTokenizer
			//normalize the input query and check whether the repeated term exists in query
			String tQuery = inputQuery;
//			String tQuery = this.StringFilter(inputQuery);
//			String tQuery = this.checkQueryTermsRepeat(tQuery1);
//			System.out.println("After checking Query is: "+ tQuery);
			//deal with each input term one by one
	    	StringTokenizer strTk=new StringTokenizer(tQuery);
	    	while(strTk.hasMoreTokens()) {
				curWord = strTk.nextToken().toLowerCase();
				//look up each term in lexiconHahsMap (dictionary)
				if (lexiconHahsMap.get(curWord) != null) {
					termDictLine = lexiconHahsMap.get(curWord).lexiconPosition;
					tTreeMap.put(new Integer(termDictLine), curWord);
				}
				else
					System.out.println("word: " + curWord + " is not found! ");
	    	 }
	    	//dealing with the terms which we find in term dictionary
	    	if(tTreeMap.size() > 0){
	    		InvertIndexNode termInvertIndexNode;
	    		
	    		//open the invert index file
				indexReader = new BufferedReader(new FileReader(indexFile));
				//record the current line number pointer
				int currentLineNum = 0;
				//record the current line
				String curLineString = "";
				String curDocNum = "";
				//get the first element(first line number) from TreeMap
				Iterator<Integer> iterator = tTreeMap.keySet().iterator();
				termDictLine = iterator.next().intValue();
				
				while ((curLineString = indexReader.readLine()) != null) {
					currentLineNum++;
					
					if((currentLineNum - termDictLine) == 0){
						termInvertIndexNode = new InvertIndexNode();
						termInvertIndexNode.ft = lexiconHahsMap.get(tTreeMap.get(new Integer(termDictLine))).ft;
						// format: 10 2 23 3
						// Using String StringTokenizer
						StringTokenizer strTk1 = new StringTokenizer(curLineString);
						while (strTk1.hasMoreTokens()) {
							curDocNum = strTk1.nextToken();
							termInvertIndexNode.fDt.put(new Integer(curDocNum),new Integer(strTk1.nextToken()));
						}
						queryHashMap.put(tTreeMap.get(new Integer(termDictLine)), termInvertIndexNode);
						
						//check whether has more element left
						if(iterator.hasNext())
							termDictLine = iterator.next().intValue();
						else
							break;
					}
				}
	    	}
	    	//close BufferedReader
			indexReader.close();
			//return result set
			return queryHashMap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public void displayQueryInfoImprove(String inputQuery,String indexFileName) {
		//
//		HashMap<String,InvertIndexNode> queryTermHashMap;
		queryTermHashMap = searchTermInfoImprove(inputQuery,indexFileName);
		
    	//if the size of the query result set larger than 0, 
    	//that means we find some words in our lexicon dictionary
    	if(queryTermHashMap.size() > 0){
    		StringBuffer displayStrBuf = new StringBuffer();
    		Iterator<String> iterator = queryTermHashMap.keySet().iterator();
    		Iterator<Integer> iterator11 ;
    		Integer key11;
    		double para_1=0;
    		double para_2=0;
    		double docCurScore= 0;
    		//
    		while (iterator.hasNext()) {
    			//get current word
    			String key = iterator.next();
//    			displayStrBuf.append(key+"\n").append(queryHashMap.get(key).ft).append("\n");
    			//loop current word for accumulating document score
    			iterator11 = queryTermHashMap.get(key).fDt.keySet().iterator();
    			//calculate first parameter in BM25 formula for current word
    			//double log2 = Math.log(x) / Math.log(2);
    			para_1 = Math.log((35472 - queryTermHashMap.get(key).ft + 0.5)/(queryTermHashMap.get(key).ft + 0.5))/Math.log(2);
    			while (iterator11.hasNext()) {
    				key11 = iterator11.next();
    				//calculate second parameter in BM25 formula for current word
    				para_2 = (2.2 * queryTermHashMap.get(key).fDt.get(key11))
							/((1.2 * (0.25 + ((0.75 * docNumHahsMap.get(String.valueOf(key11.intValue())).docLength)/this.docAveLength) )) 
									+ queryTermHashMap.get(key).fDt.get(key11));
//    				displayStrBuf.append(docNumHahsMap.get(String.valueOf(key11))).append(" ").append(queryHashMap.get(key).fDt.get(key11)+"\n");
    				//whether the current document exists in docScoreHashMap
    				//if document not exist , add it
    				if(this.docScoreHashMap.get(String.valueOf(key11.intValue()))==null){
    					this.docScoreHashMap.put(String.valueOf(key11.intValue()), new Double(para_1*para_2));
//    					if(key11.intValue() == 787)
//    						System.out.println("***1 "+this.docScoreHashMap.get(String.valueOf(key11.intValue())));
    				}
    				//if document already exist in docScoreHashMap
    				else{
    					docCurScore = this.docScoreHashMap.get(String.valueOf(key11.intValue()));
    					this.docScoreHashMap.put(String.valueOf(key11.intValue()), new Double(docCurScore+para_1*para_2));
//    					if(key11.intValue() == 787)
//    						System.out.println("***2 "+key11.intValue()+" docCurScore="+docCurScore + "--"+para_1*para_2);
    				}
    			}
    		}
//    		System.out.println(displayStrBuf.toString());
    	}
    	//Print current docScoreHashMap content
//    	System.out.println("------------- Document Score -----------");
//    	Iterator<String> iterator = this.docScoreHashMap.keySet().iterator();
//    	int i=1;
//		while (iterator.hasNext()) {
//			//get current document number
//			String key = iterator.next();
//			System.out.println(key+" "+this.docNumHahsMap.get(key).docOriginalNo+" "+i+" "+this.docScoreHashMap.get(key));
//			i++;
//		}
//    	System.out.println("----------------------------------------");
	}
	
	/**
	 * @param args
	 */
	public String StringFilter(String str) throws PatternSyntaxException {
		//example: "6.6 a. .b 5/4 patent. . . .and .a b....c 1/1 2 /2 3/ 3 d..d xx+*-~yy zz.'"
		//1.  filter other characters
//		String regEx = "[?',\\-~#$@%^&*()_+=|{};:\"'<>`\\[\\]\\\\]";
		String regEx = "[!?',\\-~#$@%^&*()_+=|{};:\"'>`\\[\\]\\\\]|(</|<)";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		String str1 = m.replaceAll("").trim();
		//2. filter "." and "/" first time 
		regEx = "(\\.\\s)|(\\s\\.)|(\\.+\\.)|(\\.$)|(/\\s)|(\\s/)";
		p = Pattern.compile(regEx);
		m = p.matcher(str1);
		String str2 = m.replaceAll(" ").trim();
		//3. filter "." second time
		regEx = "(\\s\\.)";
		p = Pattern.compile(regEx);
		m = p.matcher(str2);
		return m.replaceAll(" ").trim();
	}
	/**
	 * @param args
	 */
	public void displayTopDocument(String queryNo)  {
		StringBuffer strBuff = new StringBuffer();
		Iterator<Double> iterator = this.nTopOrderDocTreeMap.keySet().iterator();
		int i = 1;
		while (iterator.hasNext()) {
			Double key = iterator.next();
//			strBuff.append(this.nTopOrderDocTreeMap.get(key)+" "
//			  +this.docNumHahsMap.get(this.nTopOrderDocTreeMap.get(key)).docOriginalNo+" "+i+" "+key+"\n");
			strBuff.append(queryNo+" "
					  +this.docNumHahsMap.get(this.nTopOrderDocTreeMap.get(key)).docOriginalNo+" "+i+" "+key+"\n");
			i++;
		}
		System.out.println(strBuff);
	}
	
	/**
	 * Test 4.1 : loop R E
	 * */
	/*
	public static void main(String[] args) {
		//user command line input
		search tSearch = new search();
		//using for precision
		int n = 30;
		int precision = 10;
		String searchType = "PRF";
		//Running time testing
		long starttime;
		long endtime;
		
		//time testing
		starttime = System.currentTimeMillis();
		// 1. Generate term dictionary HashMap
		tSearch.readLexiconFileToHashMap("lexicon");

		// 2. Generate document map relationship
		tSearch.docNumMapFileToHashMap("map");
//		starttime = System.currentTimeMillis();
		
//		//call generate query result function
//		tSearch.generateQueryResult(searchType, queryNo, filterQueryStr, n, R, E,precision);
		
		String[] queryArray = new String[5];
		queryArray[0]="foreign minorities germany";
		queryArray[1]="behavioral genetics";
		queryArray[2]="osteoporosis";
		queryArray[3]="cosmic events";
		queryArray[4]="tropical storms";
		
		String[] queryNum = new String[5];
		queryNum[0]="401";
		queryNum[1]="402";
		queryNum[2]="403";
		queryNum[3]="405";
		queryNum[4]="408";
		
		int[] queryRelDocNum = new int[5];
		queryRelDocNum[0]=8;
		queryRelDocNum[1]=5;
		queryRelDocNum[2]=7;
		queryRelDocNum[3]=3;
		queryRelDocNum[4]=6;
		
		String filterQueryStr ="";
		
		//loop testing R E
		for(int i=0; i<5;i++){
			for(int j=1;j<=20;j++){
				for(int k=1;k<=25;k++){
					//call generate query result function
					filterQueryStr = tSearch.StringFilter(queryArray[i]);
					tSearch.generateQueryResult(searchType, queryNum[i], filterQueryStr, n, j, k,precision,queryRelDocNum[i]);
					System.out.println("The current position is:"+queryNum[i]+" R="+j+" E="+k);
					//reset variables
					tSearch.docScoreHashMap = new HashMap<String, Double>();
					tSearch.nTopOrderDocTreeMap = new TreeMap<Double,String>(new DecreaseCompare());
				}
			}
			//write results to the files
			//investigating the query expansion parameters for R and E 
			//saving the result in REScoreFile
			Comparator comp = new ArrayListComparator();
			Collections.sort(tSearch.tREScoreArrayList, comp);
			FileOperation.writeREScoreToFile(tSearch.tREScoreArrayList, "REScoreFile", true);
			tSearch.tREScoreArrayList = new ArrayList<DocREScore>();
		}
		
		//display the total execution time
		endtime = System.currentTimeMillis();
		System.out.println("generate N-Top-Ordered-Document time is: " + (endtime - starttime)/1000.00 + "s");
	}
	*/
}
