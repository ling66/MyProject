import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class SearchQueryExpansion {
	// Using this TreeMap to store top-ranked R document using by generate query expansion words
	private TreeMap<Integer,String> expansionDocsTreeMap = new TreeMap<Integer,String>();
	// Using this HashMap to store term dictionary
	private HashMap<String, InvertIndexNode> termIndexHahsMap = new HashMap<String, InvertIndexNode>();
	// Using this HashMap to store term score
	private HashMap<String, Double> termScoreHashMap = new HashMap<String, Double>();
	// Using this HashMap to store stop words
	private HashMap<String, String> stopWordHashMap = new HashMap<String, String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SearchQueryExpansion tSearchQueryExpansion = new SearchQueryExpansion();
		tSearchQueryExpansion.readStopWordsFileToHashMap("stoplist_sorted");
		
		Iterator<String> iterator = tSearchQueryExpansion.stopWordHashMap.keySet().iterator();
		int i = 1;
		while (iterator.hasNext()) {
			String key = iterator.next();
			System.out.println(key+"--"+tSearchQueryExpansion.stopWordHashMap.get(key));
		}
	}
	/**
	 * @param args
	 */
	public DocScoreNode[] generateWordsExpansionCollection(TreeMap<Double,String> topDocSet
			,HashMap<String, String> lineNumToWordHahsMap,int numExpDoc,int numOfWords,String queryStr) {
		//print topDocSet to check collection
		
		//Check input parameter for expension document size
		int docSetSize = 0;	
		if(numExpDoc <= topDocSet.size()){
			docSetSize = numExpDoc;
		}
		else{
			docSetSize = topDocSet.size();
		}
		
		//1. generate expansionDocsTreeMap collection , order by document number asc
		Iterator<Double> iterator = topDocSet.keySet().iterator();
		int curDocNum = 0;
		while (iterator.hasNext()) {
			Double key = iterator.next();
			if(curDocNum < docSetSize)
				this.expansionDocsTreeMap.put(new Integer(topDocSet.get(key)), "1");
			else
				break;
			curDocNum = curDocNum + 1;
		}
		
		// generate stop words HashMap
		this.readStopWordsFileToHashMap("stoplist_sorted");
		
		
		//2. generate words indexing by reading invlists file
		//   at the same time generate term score
		this.generateTermIndexingAndScore(lineNumToWordHahsMap);
		
//		//3. generate term score for each term in collection
//		this.generateTermScore();
		
		//3. fetch the top E term from collection according to term score
		MinHeapOrderAlgorithm tMinHeapOrderAlgorithm = new MinHeapOrderAlgorithm();
		//get the size of the document Set which will be ordered
		int termSetSize = 0;
		if(numOfWords <= this.termScoreHashMap.size()){
			termSetSize = numOfWords;
		}
		else{
			termSetSize = this.termScoreHashMap.size();
		}
		//
		DocScoreNode[] curTermSet = new DocScoreNode[termSetSize];
//		System.out.println("termSetSize is : "+ termSetSize);
		//
		Iterator<String> iterator1 = this.termScoreHashMap.keySet().iterator();
		int curPos = 1;
		DocScoreNode tDocScoreNode;
		//checking whether Query Terms Repeat
		Set<String> set = new HashSet<String>();
		StringTokenizer strTk=new StringTokenizer(queryStr);
		String curWord="";
    	while(strTk.hasMoreTokens()) {
			curWord = strTk.nextToken().toLowerCase();
			set.add(curWord);
    	}
		while (iterator1.hasNext()) {
			//get current document number
			String key = iterator1.next();
			//if key already exists in set, jump over current term
			if(set.contains(key))
				continue;
			
			if(curPos <= termSetSize){
				tDocScoreNode = new DocScoreNode();
				tDocScoreNode.docDescription= key;
				tDocScoreNode.docScore = this.termScoreHashMap.get(key).doubleValue();
				curTermSet[curPos-1] = tDocScoreNode;
				//when the size of DocScoreNode array equals docSetSize.
				//initial Min-Heap Order Algorithm
				if(curPos == termSetSize)
					tMinHeapOrderAlgorithm.orderingDocCollection(curTermSet);
			}
			else{
				//compare the score value of current document with the first element of curDoc array
				//Because the value of first element is the smallest 
				if(this.termScoreHashMap.get(key).doubleValue() > curTermSet[0].docScore){
					//replace the value of the first element
					curTermSet[0].docDescription= key;
					curTermSet[0].docScore = this.termScoreHashMap.get(key).doubleValue();
					//reorder the curDoc array
					tMinHeapOrderAlgorithm.orderingDocCollection(curTermSet);
				}
			}
			curPos = curPos + 1; 
		}
		
		//5. display terms with highest score 
		System.out.println("----------------------------------------");
		for(int i=0; i<curTermSet.length ; i++){
			System.out.println(curTermSet[i].docDescription+"--"+curTermSet[i].docScore);
		}
		
		//
		return curTermSet;
	}
	
	/**
	 * @param args
	 */
	public void readStopWordsFileToHashMap(String fileName) {
		//read information from lexicon file
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
//			Stemmer stem = new Stemmer();
			while ((curLineString = reader.readLine()) != null) {
				//?????????
				this.stopWordHashMap.put(curLineString, "1");
//				this.stopWordHashMap.put(stem.stemmingOperation(curLineString), "1");
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
	public void generateTermIndexingAndScore(HashMap<String, String> tLineNumToWordHahsMap) {
		File file = new File("invlists");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
			// 1. String split method
			String[] strArray;
			//
			int curMaxDocNum = this.expansionDocsTreeMap.lastKey().intValue();
			int curLineNum = 1;
			String curTerm = "";
			int curTermFt = 0;
//			int curTermFDt = 0;
			double curTermPartWeight = 0.0;
			double termWeight = 0.0;
			while ((curLineString = reader.readLine()) != null) {
				//get current line word
				curTerm = tLineNumToWordHahsMap.get(String.valueOf(curLineNum));
				//check whether the current term is stop word, if yes continue loop
				if(this.stopWordHashMap.get(curTerm) != null){
					//current line number increase by 1
					curLineNum = curLineNum + 1;
					continue;
				}
				// String split method
				strArray = curLineString.split(" " + "|\n");
				//invlists file format: document number - term frequent in this document
				//        example: 5356 1 10738 1 10888 2
				for(int i=1; i<=strArray.length ; i=i+2){
					//if current document number larger than MaxDocNum, break loop
					if(Integer.valueOf(strArray[i-1]).intValue() > curMaxDocNum)
						break;
					//current word included in this current document
					if(this.expansionDocsTreeMap.get(Integer.valueOf(strArray[i-1])) == "1"){
						curTermFt = curTermFt + 1;
						curTermPartWeight = curTermPartWeight + (1 + Math.log(Integer.parseInt(strArray[i])) );
//						curTermFDt = curTermFDt + Integer.parseInt(strArray[i]);
					}
				}
				//calculate term score
				if(curTermFt > 0){
					termWeight = Math.log(1 + 35472/curTermFt) * curTermPartWeight;
					this.termScoreHashMap.put(curTerm, new Double(termWeight));
				}
				//current line number increase by 1
				curLineNum = curLineNum + 1;
				//reset ft and fdt for next term
				curTermFt = 0;
				curTermPartWeight = 0;
//				curTermFDt = 0;
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
	 * back up generateTermIndexingAndScore function
	 */
	/*
	public void generateTermIndexingAndScore(HashMap<String, String> tLineNumToWordHahsMap) {
		File file = new File("invlists");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
			// 1. String split method
			String[] strArray;
			//
			DocumentNode tDocumentNode;
			int accDocLength = 0;
			//
			int curMaxDocNum = this.expansionDocsTreeMap.lastKey().intValue();
			int curLineNum = 1;
			String curWord = "";
			InvertIndexNode tInvertIndexNode;
			while ((curLineString = reader.readLine()) != null) {
				//get current line word
				curWord = tLineNumToWordHahsMap.get(String.valueOf(curLineNum));
				// String split method
				strArray = curLineString.split(" " + "|\n");
				//invlists file format: document number - term frequent in this document
				//        example: 5356 1 10738 1
				for(int i=1; i<=strArray.length ; i=i+2){
					//if current document number larger than MaxDocNum, break loop
					if(Integer.valueOf(strArray[i-1]).intValue() > curMaxDocNum)
						break;
					//current word included in this current document
					if(this.expansionDocsTreeMap.get(Integer.valueOf(strArray[i-1])) == "1"){
						//word no exists in HashMap so create new node and add word into HashMap 
            	    	if(this.termIndexHahsMap.get(curWord)==null){
            	    		tInvertIndexNode = new InvertIndexNode();
            	    		tInvertIndexNode.ft=1;
            	    		tInvertIndexNode.fDt.put(new Integer(strArray[i-1]), new Integer(strArray[i]));
            	    		this.termIndexHahsMap.put(curWord, tInvertIndexNode);
            	    	}
            	    	//word already exists in HashMap
            	    	else{
            	    		this.termIndexHahsMap.get(curWord).ft = this.termIndexHahsMap.get(curWord).ft + 1;
            	    		this.termIndexHahsMap.get(curWord).fDt.put(new Integer(strArray[i-1]), new Integer(strArray[i]));
            	    	}
					 }
				  }
				  //current line number increase by 1
				  curLineNum = curLineNum + 1;
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
	}*/
	
	/**
	 * @param args
	 */
//	public void generateTermScore() {
//		Iterator<String> iterator = this.termIndexHahsMap.keySet().iterator();
//		Iterator<Integer> iterator11 ;
//		Integer key11;
//		while (iterator.hasNext()) {
//			//get current word
//			String key = iterator.next();
//			//loop current word for accumulating document score
//			iterator11 = this.termIndexHahsMap.get(key).fDt.keySet().iterator();
//			//
//			while (iterator11.hasNext()) {
//				key11 = iterator11.next();
//			}
//		}
//	}

}
