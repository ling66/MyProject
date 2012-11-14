import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class index {
	//store the number of relevant and irrelevant document
	private int relNumDoc = 0;
	private int irrelNumDoc = 0;
	//using HashMap to store term frequency information
	//store term info in relevant collection
	private HashMap<String,TermNode> relTermHashMap = new HashMap<String,TermNode>();
	//store term info in irrelevant collection
	private HashMap<String,TermNode> irrelTermHashMap = new HashMap<String,TermNode>();
	//define sorted LinkedHashMap for relevant and irrelevant HashMap
	private LinkedHashMap<String, TermNode> relSortedLinkedHashMap = new LinkedHashMap<String, TermNode>();
	private LinkedHashMap<String, TermNode> irrelSortedLinkedHashMap = new LinkedHashMap<String, TermNode>();
	//define sorted LinkedHashMap to store difference between relevant and irrelevant Term score
	private LinkedHashMap<String, Double> sortedDiffScoreLinkedHashMap = new LinkedHashMap<String, Double>();
	//Using this ArrayList to store stop words
	private ArrayList<String> stopWordArrayList = new ArrayList<String>();
	//Using this ArrayList to store some special ignore words
	private ArrayList<String> ignoreWordArrayList = new ArrayList<String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		index tIndex = new index();
		tIndex.indexController(120,30);
	}
	/**
	 * @param args
	 */
	public void indexController(int relFeatureNum,int irrelFeatureNum) {
		// read stop list words
		this.readStopWordsFileToArrayList("stoplist_sorted");
		// generate term frequency HashMap
		this.readCollectionPagesInfo("./basic/pages");
		// calculate term score and sort terms by score by descending order
		this.calculateTermScoreAndSortMap();
		//compare relevant and irrelevant terms
		this.compareTermScore();
		// write results to files
		this.writeResultsToFiles();
		//generate weka arff file
		FeatureExtraction tFeatureExtration = new FeatureExtraction();
		tFeatureExtration.featureController(relFeatureNum, irrelFeatureNum, this.relTermHashMap, this.irrelTermHashMap);
		System.out.println("Finish");
	}
	
	/**
	 * @param args
	 */
	public void readStopWordsFileToArrayList(String fileName) {
		//read information from stop list file
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
			//??????????????
			//Stemming operation on stop words
			Stemmer stem = new Stemmer();
			while ((curLineString = reader.readLine()) != null) {
				//this.stopWordArrayList.add(curLineString);
				this.stopWordArrayList.add(stem.stemmingOperation(curLineString));
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
	 * read relevant and irrelevant info from pages file
	 */
	public void readCollectionPagesInfo(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			//current line content
            String curLineString = null;
            String[] splitArray = null;
            String fileContent = "";
            int lineNum = 1;
			while ((curLineString = reader.readLine()) != null) {
				//format: class,file_name,url
				//    eg: 1,6,http://www.rmit.edu.au/news.html
				System.out.println("The current article is"+lineNum+":"+curLineString);
				splitArray = curLineString.split(",");
				//read relevant file from rel directory
				if(splitArray[0].equals("1"))
				{
					fileContent = FileOperation.readLocalFile("./basic/rel/"+splitArray[1]);
					this.relNumDoc = this.relNumDoc + 1;
				}
				//read irrlevant file from irrel directory
				else if(splitArray[0].equals("0"))
				{
					fileContent = FileOperation.readLocalFile("./basic/irrel/"+splitArray[1]);
					this.irrelNumDoc = this.irrelNumDoc + 1;
				}
				//filter document content
				fileContent = PubFunction.StringFilter(fileContent);
				//???????????????
		    	//Stemming  operation on article
				Stemmer stem = new Stemmer();
				fileContent = stem.stemmingOperation(fileContent);
				this.generateTermHashMap(splitArray[0],splitArray[1], fileContent);
				lineNum = lineNum + 1;
			}
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
	 * This function extracts the data from specific file and generate the whole HashMap structure
	 */
	public void generateTermHashMap(String urlClass,String docNo,String fileContent) {
		//current word
        String curWord="";
        TermNode tTermNode = null;
        int tempfc = 0;
		//Using String StringTokenizer
    	StringTokenizer strTk=new StringTokenizer(fileContent); 
    	while(strTk.hasMoreTokens()) {
	    	//execute the lower case operation on each word
	    	curWord = strTk.nextToken().toLowerCase();
//	    	//filter special character eg.?
//	    	if(curWord.contains("�"))
//	    		//System.out.println(curWord);
//	    	if(curWord.contains(" "))
//    		//System.out.println(curWord);
	    	 
	    	//check stop words
	    	if(this.stopWordArrayList.contains(curWord))
	    		continue;
	    	//relevant document
	    	if(urlClass.equals("1"))
	    	{
	    		//word no exists in HashMap so create new node and add word into HashMap 
    	    	if(this.relTermHashMap.get(curWord)==null){
    	    		tTermNode = new TermNode();
    	    		tTermNode.ft=1;
    	    		tTermNode.fDt.put(docNo, new Integer(1));
    	    		this.relTermHashMap.put(curWord, tTermNode);
    	    	}
    	    	//word already exists in HashMap
    	    	else{
    	    		//check whether the current document number exist in word node
    	    		//if not in word node , add this new document number into word node, 
    	    		//and increase the word frequent(ft) by one
    	    		if(this.relTermHashMap.get(curWord).fDt.get(docNo)==null){    
    	    			this.relTermHashMap.get(curWord).ft = this.relTermHashMap.get(curWord).ft + 1;
    	    			this.relTermHashMap.get(curWord).fDt.put(docNo, new Integer(1));
    	    		}
    	    		//if word node already exists,just find this node and increase word document frequent by one
    	    		else{
    	    			int temp = this.relTermHashMap.get(curWord).fDt.get(docNo).intValue();
    	    			temp = temp + 1;
    	    			this.relTermHashMap.get(curWord).fDt.put(docNo, new Integer(temp));
    	    		}
    	    	}
    	    	//the term total number of frequency in collection
    	    	tempfc = this.relTermHashMap.get(curWord).fc + 1;
    	    	this.relTermHashMap.get(curWord).fc = tempfc;
	    	}
	    	//irrelevant document
	    	else if(urlClass.equals("0"))
	    	{
	    		//word no exists in HashMap so create new node and add word into HashMap 
    	    	if(this.irrelTermHashMap.get(curWord)==null){
    	    		tTermNode = new TermNode();
    	    		tTermNode.ft=1;
    	    		tTermNode.fDt.put(docNo, new Integer(1));
    	    		this.irrelTermHashMap.put(curWord, tTermNode);
    	    	}
    	    	//word already exists in HashMap
    	    	else{
    	    		//check whether the current document number exist in word node
    	    		//if not in word node , add this new document number into word node, 
    	    		//and increase the word frequent(ft) by one
    	    		if(this.irrelTermHashMap.get(curWord).fDt.get(docNo)==null){    
    	    			this.irrelTermHashMap.get(curWord).ft = this.irrelTermHashMap.get(curWord).ft + 1;
    	    			this.irrelTermHashMap.get(curWord).fDt.put(docNo, new Integer(1));
    	    		}
    	    		//if word node already exists,just find this node and increase word document frequent by one
    	    		else{
    	    			int temp = this.irrelTermHashMap.get(curWord).fDt.get(docNo).intValue();
    	    			temp = temp + 1;
    	    			this.irrelTermHashMap.get(curWord).fDt.put(docNo, new Integer(temp));
    	    		}
    	    	}
    	    	//the term total number of frequency in collection
    	    	tempfc = this.irrelTermHashMap.get(curWord).fc + 1;
    	    	this.irrelTermHashMap.get(curWord).fc = tempfc;
	    	}
    	}
	}
	
	/**
	 * calculate score for relevant and irrelevant collection
	 * formula: (ft / total number of documents in collection)*Math.log(fc)
	 */
	public void calculateTermScoreAndSortMap() {
		//build HashMap to store term-score pair
		//format: term,score
		HashMap<String,Double> tempRelTermHashMap = new HashMap<String,Double>();
		HashMap<String,Double> tempIrrelTermHashMap = new HashMap<String,Double>();
		//calculate score for relevant collection
		for (String key : this.relTermHashMap.keySet())
		{
			this.relTermHashMap.get(key).score = ((this.relTermHashMap.get(key).ft*1.0) / this.relNumDoc) 
					* (1.0 + Math.log(this.relTermHashMap.get(key).fc));
			tempRelTermHashMap.put(key, new Double(this.relTermHashMap.get(key).score));
		}
		//calculate score for irrelevant collection
		for (String key : this.irrelTermHashMap.keySet())
		{
			this.irrelTermHashMap.get(key).score = ((this.irrelTermHashMap.get(key).ft*1.0) / this.irrelNumDoc) 
					* (1.0 + Math.log(this.irrelTermHashMap.get(key).fc));
			tempIrrelTermHashMap.put(key, new Double(this.irrelTermHashMap.get(key).score));
		}
		
		//sort Map
		//sort relevant HashMap
		this.relSortedLinkedHashMap = PubFunction.sortHashMapByValues((HashMap<String, Double>)tempRelTermHashMap,this.relTermHashMap);
		this.irrelSortedLinkedHashMap = PubFunction.sortHashMapByValues((HashMap<String, Double>)tempIrrelTermHashMap,this.irrelTermHashMap);
	}
	
	/**
	 * compare relevant and irrelevant terms score
	 */
	public void compareTermScore() {
		//combine all terms in one collection
		HashMap<String,Double> tempDiffScoreHashMap = new HashMap<String,Double>();
		//add relevant term-score pair to collection
		for (String key : this.relTermHashMap.keySet()) {
			tempDiffScoreHashMap.put(key, new Double(this.relTermHashMap.get(key).score));
		}
		//doubleValue() 
		//add irrelevant term-score pair to collection 
		//also calculate score difference between two collection
		double tempValue = 0.0;
		for (String key : this.irrelTermHashMap.keySet()) {
			//term not exists in HashMap
	    	if(tempDiffScoreHashMap.get(key)==null)
	    		tempDiffScoreHashMap.put(key, new Double(this.irrelTermHashMap.get(key).score));
	    	//term exists in HashMap
	    	else
	    	{
	    		//d1 < d2
	    		if(Double.compare(tempDiffScoreHashMap.get(key), new Double(this.irrelTermHashMap.get(key).score)) < 0)
	    		{
	    			tempValue = this.irrelTermHashMap.get(key).score - tempDiffScoreHashMap.get(key).doubleValue();
	    			tempDiffScoreHashMap.put(key,new Double(tempValue));
	    		}
	    		//d1 > d2
	    		else if(Double.compare(tempDiffScoreHashMap.get(key), new Double(this.irrelTermHashMap.get(key).score)) > 0)
	    		{
	    			tempValue = tempDiffScoreHashMap.get(key).doubleValue() - this.irrelTermHashMap.get(key).score;
	    			tempDiffScoreHashMap.put(key,new Double(tempValue));
	    		}
	    		//d1 == d2 
	    		else{
	    			tempValue = tempDiffScoreHashMap.get(key).doubleValue() - this.irrelTermHashMap.get(key).score;
	    			tempDiffScoreHashMap.put(key,new Double(tempValue));
	    		}
	    	}
		}
		//sort result
		this.sortedDiffScoreLinkedHashMap = PubFunction.sortHashMapByValues(tempDiffScoreHashMap);
	}
	
	/**
	 * write results to files
	 * relTerms and irrelTerms files store non-sorted terms info
	 * relSortedTerms and irrelSortedTerms files store sorted terms info
	 */
	public void writeResultsToFiles() {
		// write non-sorted info into files
		System.out.println("--------Start to write non-sorted files-----------------------");
		FileOperation.writeHashMapToFileImprove(this.relTermHashMap,"./basic/relTerms", false);
		FileOperation.writeHashMapToFileImprove(this.irrelTermHashMap,"./basic/irrelTerms", false);
		System.out.println("------------Start to write sorted term files-----------------------");
		FileOperation.writeLinkedHashMapToFileImprove(this.relSortedLinkedHashMap,"./basic/relSortedTerms", false);
		FileOperation.writeLinkedHashMapToFileImprove(this.irrelSortedLinkedHashMap,"./basic/irrelSortedTerms", false);
		System.out.println("------------Start to write term difference sorted files-----------------------");
		FileOperation.writeLinkedHashMapToFileImprove(this.sortedDiffScoreLinkedHashMap,"./basic/TermDiff", false,this.relTermHashMap,this.irrelTermHashMap);
	}
}
