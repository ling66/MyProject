import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

//weka command: java weka.classifiers.trees.J48 -t mobile.arff -i > ./smart/weka_result_J48

public class classify_smart_J48 {
	//Using ArrayList to store weka arff features
	private ArrayList<String> arffFeaturesArrayList = new ArrayList<String>();
	//using HashMap to store doc-url information
	private HashMap<String,DocNode> docInfoHashMap = new HashMap<String,DocNode>();
	//using HashMap to store term frequency information
	//store term info into collection
	private HashMap<String,TermNode> termHashMap = new HashMap<String,TermNode>();
	//Using ArrayList to store stop words
	private ArrayList<String> stopWordArrayList = new ArrayList<String>();
	//Using ArrayList to store some special ignore words
	private ArrayList<String> ignoreWordArrayList = new ArrayList<String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		classify_smart_J48 tClassify_smart_J48 = new classify_smart_J48();
		tClassify_smart_J48.indexController("mobile.arff","./smart/pages_smart","./smart/mobile_test.arff"
				,"./smart/mobile_test_labeled.arff","./smart/classified_doc_J48","1");
	}
	/**
	 * @param args
	 */
	public void indexController(String trainingFile,String pagesFile
			,String testingFile,String testingLabeledFile,String finalFile,String tFlag) {
		//read stop list words
		this.readStopWordsFileToArrayList("stoplist_sorted");
		//read features from arff file
		this.readTrainingWekaFeaturesToArrayList(trainingFile);
		//generate term frequency HashMap
		this.readCollectionPagesInfo(pagesFile);
		//generate testing arff file(./smart/mobile_test.arff)
		this.generateWekaFile(testingFile);
		//use weka J48 classifier model to generate final class for document
		classifier_J48 tclassifier_J48 = new classifier_J48();
		tclassifier_J48.classifierController(this.docInfoHashMap,trainingFile,testingFile
				,testingLabeledFile,finalFile);
		// write term inverted list to files
		// also copy original labeled file(from ./smart/classified_doc_J48) to retrain directory(./retrain/classified_doc_J48)
		this.writeResultsToFiles(tFlag);
		
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
	 * @param args
	 */
	public void readTrainingWekaFeaturesToArrayList(String fileName) {
		//read information from stop list file
		File file = new File(fileName);
		BufferedReader reader = null;
		boolean startFlag = false;
		String[] splitArray = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
			while ((curLineString = reader.readLine()) != null) {
				//check whether to read current line
				if(curLineString.contains("@attribute") || startFlag )
				{
					startFlag = true;
					//check space
					if(curLineString.equals(""))
						break;
					//split current line and add the term into features ArrayList
					splitArray = curLineString.split(" ");
					this.arffFeaturesArrayList.add(splitArray[1]);
				}
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
            DocNode tDocNode = null;
			while ((curLineString = reader.readLine()) != null) {
				//format: class,file_name,url
				//    eg: 1,6,http://www.rmit.edu.au/news.html
				System.out.println("The current article is"+lineNum+":"+curLineString);
				splitArray = curLineString.split(",");
				
				//read file
				fileContent = FileOperation.readLocalFile("./smart/downloads/"+splitArray[1]);
				//filter document content
				fileContent = PubFunction.StringFilter(fileContent);
				//???????????????
		    	//Stemming  operation on article
				Stemmer stem = new Stemmer();
				fileContent = stem.stemmingOperation(fileContent);
				//generate term inverted list
				this.generateTermHashMap(splitArray[0],splitArray[1], fileContent);
				
				//generate document information
				tDocNode = new DocNode();
				tDocNode.url = splitArray[2];
				this.docInfoHashMap.put(splitArray[1],tDocNode);
				
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
	    	//???????????????
	    	//Stemming 
	    	//check stop words
	    	if(this.stopWordArrayList.contains(curWord))
	    		continue;
	    	//relevant document(in this case, always 1)
	    	if(urlClass.equals("1"))
	    	{
	    		//word no exists in HashMap so create new node and add word into HashMap 
    	    	if(this.termHashMap.get(curWord)==null){
    	    		tTermNode = new TermNode();
    	    		tTermNode.ft=1;
    	    		tTermNode.fDt.put(docNo, new Integer(1));
    	    		this.termHashMap.put(curWord, tTermNode);
    	    	}
    	    	//word already exists in HashMap
    	    	else{
    	    		//check whether the current document number exist in word node
    	    		//if not in word node , add this new document number into word node, 
    	    		//and increase the word frequent(ft) by one
    	    		if(this.termHashMap.get(curWord).fDt.get(docNo)==null){    
    	    			this.termHashMap.get(curWord).ft = this.termHashMap.get(curWord).ft + 1;
    	    			this.termHashMap.get(curWord).fDt.put(docNo, new Integer(1));
    	    		}
    	    		//if word node already exists,just find this node and increase word document frequent by one
    	    		else{
    	    			int temp = this.termHashMap.get(curWord).fDt.get(docNo).intValue();
    	    			temp = temp + 1;
    	    			this.termHashMap.get(curWord).fDt.put(docNo, new Integer(temp));
    	    		}
    	    	}
    	    	//the term total number of frequency in collection
    	    	tempfc = this.termHashMap.get(curWord).fc + 1;
    	    	this.termHashMap.get(curWord).fc = tempfc;
	    	}
    	}
	}
	
	/**
	 * @param args
	 */
	public void generateWekaFile(String fileName) {
		//store the content of the arff file
		StringBuffer currentLineSB = new StringBuffer();
		//generate title part
		currentLineSB.append("@relation mobile\n\n");
		//generate attribute part
		for(int i=0;i<this.arffFeaturesArrayList.size();i++)
		{
			currentLineSB.append("@attribute ").append(this.arffFeaturesArrayList.get(i)+" {yes, no}\n");
		}
		currentLineSB.append("\n");
		
		//generate data part
		currentLineSB.append("@data\n");
		Iterator<String> iterator = this.docInfoHashMap.keySet().iterator();
		while (iterator.hasNext()) {
			//get document number from HashMap
			//ignore last column, it is a class label
			String docNo = iterator.next();
			for(int i=0;i<(this.arffFeaturesArrayList.size()-1);i++)
			{
				if(this.termHashMap.get(this.arffFeaturesArrayList.get(i))!=null)
				{
					if(this.termHashMap.get(this.arffFeaturesArrayList.get(i)).fDt.get(docNo)!=null)
					{
						currentLineSB.append("yes,");
					}
					else
						currentLineSB.append("no,");
				}
				else
					currentLineSB.append("no,");
			}
			//the class label is not sure
			currentLineSB.append("?\n");
		}
		
		//write file
		FileOperation.writeFile(fileName, currentLineSB.toString(), false);
	}
	
	/**
	 * write results to files
	 */
	public void writeResultsToFiles(String tFlag) {
		//write inverted index term into files
		System.out.println("--------Start to write non-sorted files-----------------------");
		FileOperation.writeHashMapToFileImprove(this.termHashMap,"./smart/terms", false);
		//copy original labeled file(from ./smart/classified_doc_J48) to retrain directory(./retrain/classified_doc_J48)
		//this operation not perform at retrain stage
		if(tFlag.equals("1"))
		{
			System.out.println("--------Start to copy file-----------------------");
			FileOperation.copyFile("./smart/classified_doc_J48","./retrain/classified_doc_J48");
		}
	}
}
