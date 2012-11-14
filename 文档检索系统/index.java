import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;


/**
 * Running command for this program:
 * ssh -X s3300154@yallara.cs.rmit.edu.au
 * javac *.java
 * java -Xms128M -Xmx1000M index /public/courses/MultimediaInfoRetrieval/2011/a2/collection
 * java index /public/courses/MultimediaInfoRetrieval/2011/a2/collection 
 */
public class index {
	//Using this TreeMap to store document assigned No with original document No
	private TreeMap<Integer,DocumentNode> docNumMap = new TreeMap<Integer,DocumentNode>();
	//Using this HashMap to store inverted index
	private HashMap<String,InvertIndexNode> hsmp = new HashMap<String,InvertIndexNode>();
	//Using this flag to store whether to print the word on screen
	private boolean pflag = false;
	
	/**
	 * The main function control the whole process for index generating operation
	 */
	public static void main(String[] args) {
		//user command line input
		index tIndex = new index();
		
		ArrayList<String> inputArrayList = new ArrayList<String>();

		//User input format:
		//format : index collection  
		//format : index /public/courses/MultimediaInfoRetrieval/2011/a2/collection  
		for (String s : args) {
			inputArrayList.add(s);
		}
		//getting file name and the path of the file
		String fileName = inputArrayList.get(0);
		
		//Running time testing
		long starttime;
		long endtime;
		
		starttime = System.currentTimeMillis();
		tIndex.generateTermHashMap(fileName);
		endtime = System.currentTimeMillis();
		
		System.out.println("Generate TermHashMap time is: " + (endtime - starttime)/1000 + "s");
		
		FileOperation.writeDocumentTreeMapToFileImprove(tIndex.docNumMap, "map", false);
		//FileOperation.writeHashMapToFile(tIndex.hsmp, "t-dict.txt", "t-index.txt", false);
//		FileOperation.writeHashMapToFileImprove(tIndex.hsmp, "lexicon", "invlists", false);
		FileOperation.writeHashMapToFileImprove(tIndex.hsmp, "lexicon", "invlists", true);
		
		endtime = System.currentTimeMillis();
		System.out.println("Total time(write one disk time) is: " + (endtime - starttime)/1000 + "s");
	}
	
	/**
	 * Checking whether the specific file exists
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
	 * This function extracts the data from specific file and generate the whole HashMap structure
	 */
	public void generateTermHashMap(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
//            System.out.println("Read File by line: ");
            reader = new BufferedReader(new FileReader(file));
            //
            int curAssignDocNum=1;
    		//
    		int index1 = 0;
            int index2 = 0;
            //current line number in collection
            int curLineNumber = 1;
            //current line content
            String curLineString = null;
            //current word
            String curWord="";
            //current accumulate document length
            int curAccDocLength = 0;
            //Using for store printing words
            StringBuffer printWords = new StringBuffer();
            //
            DocumentNode tDocumentNode;
            InvertIndexNode tInvertIndexNode ;
            // read file by line until meet the null
            while ((curLineString = reader.readLine()) != null) {
            	//Deal with <DOC> or </DOC> or <TEXT> or </TEXT> Line
            	//when parser meets these word, do not need parse these word
            	if(curLineString.contains("<DOC>")||curLineString.contains("</DOC>")
            			|| curLineString.contains("<TEXT>")||curLineString.contains("</TEXT>")){
            		//when program meet the <TEXT> lable  
            		if(curLineString.contains("<TEXT>")){
            			//record the end line number of current document
            			docNumMap.get(new Integer(curAssignDocNum)).startPosition = curLineNumber+1;
            		}
            		//when program meet the </TEXT> lable  
            		if(curLineString.contains("</TEXT>")){
            			//record the end line number of current document
            			docNumMap.get(new Integer(curAssignDocNum)).endPosition = curLineNumber-1;
            			//recore the length of the current document and reset curAccDocLength to 0
            			docNumMap.get(new Integer(curAssignDocNum)).docLength = curAccDocLength;
            			curAccDocLength = 0;
            			//current document number increase 1
            			curAssignDocNum = curAssignDocNum + 1;
            		}
            	}
            	//Deal with <DOCNO> Line, it will create the whole map file in this part
            	else if(curLineString.contains("<DOCNO>")){
//            		index1 = curLineString.indexOf("FT");
            		//start position is 7
            		index1 = 7;
    	    		index2 = curLineString.indexOf("</DOCNO>");
    	    		tDocumentNode = new DocumentNode();
    	    		docNumMap.put(new Integer(curAssignDocNum),tDocumentNode);
    	    		docNumMap.get(new Integer(curAssignDocNum)).docOriginalNo = curLineString.substring(index1,index2).trim();
            	}
            	else{
            		//do word stemming operation for the whole line
            		curLineString = this.StringFilter(curLineString);
            		//accumulate the current line length to document length
            		curAccDocLength = curAccDocLength + curLineString.length();
            		//Using String StringTokenizer
                	StringTokenizer strTk=new StringTokenizer(curLineString); 
            	    while(strTk.hasMoreTokens()) {
            	    	//execute the lower case operation on each word
            	    	curWord = strTk.nextToken().toLowerCase();
            	    	//if user input with -p command,put this into print StringBuffer
            	    	if(this.pflag)
            	    		printWords.append(curWord+"\n");
//            	    	System.out.println(strTk.nextToken());
            	    	//word no exists in HashMap so create new node and add word into HashMap 
            	    	if(hsmp.get(curWord)==null){
            	    		tInvertIndexNode = new InvertIndexNode();
            	    		tInvertIndexNode.ft=1;
            	    		tInvertIndexNode.fDt.put(new Integer(curAssignDocNum), new Integer(1));
            	    		hsmp.put(curWord, tInvertIndexNode);
            	    	}
            	    	//word already exists in HashMap
            	    	else{
            	    		//check whether the current document number exist in word node
            	    		//if not in word node , add this new document number into word node, 
            	    		//and increase the word frequent(ft) by one
            	    		if(hsmp.get(curWord).fDt.get(new Integer(curAssignDocNum))==null){    
            	    			hsmp.get(curWord).ft = hsmp.get(curWord).ft + 1;
            	    			hsmp.get(curWord).fDt.put(new Integer(curAssignDocNum), new Integer(1));
            	    		}
            	    		//if word node already exists,just find this node and increase word document frequent by one
            	    		else{
            	    			int temp = hsmp.get(curWord).fDt.get(new Integer(curAssignDocNum)).intValue();
            	    			temp = temp + 1;
            	    			hsmp.get(curWord).fDt.put(new Integer(curAssignDocNum), new Integer(temp));
            	    		}
            	    	}
            	    }
              }
            	//current line number increase 1
            	curLineNumber = curLineNumber + 1;
            }
            reader.close();
            //if user input with -p command,then print all the words
            if(this.pflag)
            	System.out.println(printWords.toString());
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
	 * String Filter function using regular expression technology
	 * example1: this implementation "first-second" will be converted into "firstsecond"
	 * example2: the string"6.6 a. .b 5/4 patent. . . .and .a b...c 1/1 2 /2 3/ 3  d..d xx+*-~yy zz.'"
	 *  will be changed to "6.6 a b 5/4 patent   and a b c 1/1 2 2 3 3  d d xxyy"
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
	 * String Filter function using regular expression technology
	 * example: this implementation "first-second" will be converted into "first second"
	 */
	public String StringFilter1(String str) throws PatternSyntaxException {
		//1. replace all '-' to space
		String regEx1 = "-";
		Pattern p1 = Pattern.compile(regEx1);
		Matcher m1 = p1.matcher(str);
		String str1 = m1.replaceAll(" ").trim();
		//2. replace other character
		String regEx = "[?',.\\-/~#$@%^&*()_+=|{};:\"'<>`\\[\\]\\\\]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str1);
		return m.replaceAll("").trim();
	}
	
}
