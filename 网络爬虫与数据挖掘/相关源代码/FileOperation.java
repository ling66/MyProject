import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class FileOperation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//FileOperation.writeFile("./rel/a.html", "<html></html>", false);
		String str = FileOperation.readLocalFile("./basic/irrel/76");
		System.out.println(str);
		System.out.println(PubFunction.StringFilter(str));
	}
	
	/** 
     * Write document TreeMap to the Map files 
     */
	public synchronized static boolean writeFile(String fileName,String fileContent,boolean status) {
        try {
        	//status=true: add the content to the end of the file
        	//status=false: rewrite the whole file, clear the history data 
            FileWriter docMapWriter = new FileWriter(fileName, status);
    		//write files
			docMapWriter.write(fileContent);
            //close the files
    		docMapWriter.close();
    		return true;
        } catch (Exception e) {
        	System.out.println("Exception at FileOperation.writeFile");
            e.printStackTrace();
            return false;
        }
	}
	
	/**
	 * copy a file from source to destination
	 */
	public static void copyFile(String srFile, String dtFile) {
		InputStream inStream = null;
		OutputStream outStream = null;
		try {
			File afile = new File(srFile);
			File bfile = new File(dtFile);

			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);

			byte[] buffer = new byte[1024];
			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}
			inStream.close();
			outStream.close();
			System.out.println("File is copied successful!");
		} catch (Exception e) {
			System.out.println("Exception at FileOperation.copyFile");
			e.printStackTrace();
		}
	}
	
	/** 
     * Write document TreeMap to the Map files 
     */
	public synchronized static String readLocalFile(String fileName) {
        try {
        	String textOnly = "";
        	File file = new File(fileName);
        	Document doc = Jsoup.parse(file,"UTF-8");
        	textOnly = Jsoup.parse(doc.toString()).text();
        	//System.out.println(textOnly);
        	return textOnly;
        } catch (IOException e) {
        	System.out.println("Exception at FileOperation.readLocalFile");
            e.printStackTrace();
            return null;
        }
	}
	
	/** 
     * Write HashMap to the dictionary and inverted index files
     */
	public synchronized static void writeHashMapToFileImprove(HashMap<String,TermNode> tHashMap,
			String indexName,boolean status) {
        try {
        	//status=true: add the content to the end of the file
        	//status=false: rewrite the whole file, clear the history data 
            //create invert list file Writer
            FileWriter indexWriter = new FileWriter(indexName, status);
            
            //record the current line number
            int currentLineNumber = 0;
            //StringBuffer can provide the much more efficiency for disk write operation
            //use for write invert_list file
            StringBuffer currentIndexLineSB = new StringBuffer();
    		
            Iterator<String> iterator = tHashMap.keySet().iterator();
            System.out.println("File Write Buffer Operateion Start!");
            long starttime1 = System.currentTimeMillis();
    		while (iterator.hasNext()) {
    			//????????????????
//    			if(currentLineNumber == 100000){
    		   if(currentLineNumber == 80000 ||currentLineNumber == 160000){
    				//write the content of these two StringBuffer to files
    				System.out.println("The current line number is: "+currentLineNumber);
    				indexWriter.write(currentIndexLineSB.toString());
    				currentIndexLineSB = new StringBuffer();
    			}
    			currentLineNumber = currentLineNumber + 1;
    			//get each term from HashMap
    			String key = iterator.next();
    			//format: term ft fc <docno,ftd>
    			currentIndexLineSB.append(key+" ").append(tHashMap.get(key).ft+" ").append(tHashMap.get(key).fc);
    			//loop deal with the each document node for each term
    			Iterator<String> iterator1 = tHashMap.get(key).fDt.keySet().iterator();
    			while (iterator1.hasNext()) {
    				String key1 = iterator1.next();
    				//put the document term occurrence information into currentIndexLineSB buffer
        			//the format like: document_number term frequency in this document (example: 6 128)
    				currentIndexLineSB.append(" <"+key1+",").append(String.valueOf(tHashMap.get(key).fDt.get(key1))+">");
    			}
    			currentIndexLineSB.append("\n");
    		}
    		long endtime1 = System.currentTimeMillis();
			System.out.println("File Write buffer Time: " + (endtime1 - starttime1)/1000 + "s");
    		System.out.println("File Write Operateion Start!");
    		long starttime = System.currentTimeMillis();
    		//write the content of StringBuffer to files
			indexWriter.write(currentIndexLineSB.toString());
			long endtime = System.currentTimeMillis();
			System.out.println("File Write Time: " + (endtime - starttime)/1000 + "s");
            
            //close the files
            indexWriter.close();
            //release the StringBuffer
            currentIndexLineSB = null;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/** 
     * Write LinkedHashMap to the Sorted inverted index files
     */
	public synchronized static void writeLinkedHashMapToFileImprove(LinkedHashMap<String,TermNode> tLinkedHashMap,
			String indexName,boolean status) {
        try {
        	//status=true: add the content to the end of the file
        	//status=false: rewrite the whole file, clear the history data 
            //create invert list file Writer
            FileWriter indexWriter = new FileWriter(indexName, status);
            
            //record the current line number
            int currentLineNumber = 0;
            //StringBuffer can provide the much more efficiency for disk write operation
            //use for write invert_list file
            StringBuffer currentIndexLineSB = new StringBuffer();
    		
            Iterator<String> iterator = tLinkedHashMap.keySet().iterator();
            System.out.println("File Write Buffer Operateion Start!");
            long starttime1 = System.currentTimeMillis();
    		while (iterator.hasNext()) {
    			//????????????????
//    			if(currentLineNumber == 100000){
    		   if(currentLineNumber == 80000 ||currentLineNumber == 160000){
    				//write the content of these two StringBuffer to files
    				System.out.println("The current line number is: "+currentLineNumber);
    				indexWriter.write(currentIndexLineSB.toString());
    				currentIndexLineSB = new StringBuffer();
    			}
    			currentLineNumber = currentLineNumber + 1;
    			//get each term from LinkedHashMap
    			String key = iterator.next();
    			//format: term ft fc <docno,ftd>
    			currentIndexLineSB.append(key+" ").append(tLinkedHashMap.get(key).score+" ").append(tLinkedHashMap.get(key).ft+" ")
    					.append(tLinkedHashMap.get(key).fc);
    			//loop deal with the each document node for each term
    			Iterator<String> iterator1 = tLinkedHashMap.get(key).fDt.keySet().iterator();
    			while (iterator1.hasNext()) {
    				String key1 = iterator1.next();
    				//put the document term occurrence information into currentIndexLineSB buffer
        			//the format like: document_number term frequency in this document (example: 6 128)
    				currentIndexLineSB.append(" <"+key1+",").append(String.valueOf(tLinkedHashMap.get(key).fDt.get(key1))+">");
    			}
    			currentIndexLineSB.append("\n");
    		}
    		long endtime1 = System.currentTimeMillis();
			System.out.println("File Write buffer Time: " + (endtime1 - starttime1)/1000 + "s");
    		System.out.println("File Write Operateion Start!");
    		long starttime = System.currentTimeMillis();
    		//write the content of StringBuffer to files
			indexWriter.write(currentIndexLineSB.toString());
			long endtime = System.currentTimeMillis();
			System.out.println("File Write Time: " + (endtime - starttime)/1000 + "s");
            
            //close the files
            indexWriter.close();
            //release the StringBuffer
            currentIndexLineSB = null;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/** 
     * Write LinkedHashMap to the Sorted difference score file
     */
	public synchronized static void writeLinkedHashMapToFileImprove(LinkedHashMap<String,Double> tLinkedHashMap,
			String indexName,boolean status,HashMap<String,TermNode> relTermHashMap,HashMap<String,TermNode> irrelTermHashMap) {
        try {
        	//status=true: add the content to the end of the file
        	//status=false: rewrite the whole file, clear the history data 
            //create invert list file Writer
            FileWriter indexWriter = new FileWriter(indexName, status);
            
            //record the current line number
            int currentLineNumber = 0;
            //StringBuffer can provide the much more efficiency for disk write operation
            //use for write invert_list file
            StringBuffer currentIndexLineSB = new StringBuffer();
    		
            Iterator<String> iterator = tLinkedHashMap.keySet().iterator();
            System.out.println("File Write Buffer Operateion Start!");
            long starttime1 = System.currentTimeMillis();
            //temporary store term score for relevant and irrelevant collection 
            double score1 = 0.0;
            double score2 = 0.0;
            String tFlag = "";
    		while (iterator.hasNext()) {
    			//????????????????
//    			if(currentLineNumber == 100000){
    		   if(currentLineNumber == 80000 ||currentLineNumber == 160000){
    				//write the content of these two StringBuffer to files
    				System.out.println("The current line number is: "+currentLineNumber);
    				indexWriter.write(currentIndexLineSB.toString());
    				currentIndexLineSB = new StringBuffer();
    			}
    			currentLineNumber = currentLineNumber + 1;
    			//get each term from LinkedHashMap
    			String key = iterator.next();
    			//format: term diff-score (1-rel or 2-irrel) rel-score irrel-score
    			currentIndexLineSB.append(key+" ").append(tLinkedHashMap.get(key).toString()+" ");
    			//check whether term exists in relevant collection
    	    	if(relTermHashMap.get(key)!=null)
    	    	{
    	    		score1 = relTermHashMap.get(key).score;
    	    	}
    	    	else 
    	    		score1 = 0.0;
    	    	//check whether term exists in irrelevant collection
    	    	if(irrelTermHashMap.get(key)!=null)
    	    	{
    	    		score2 = irrelTermHashMap.get(key).score;
    	    	}
    	    	else 
    	    		score2 = 0.0;
    	    	//compare score and set flag
    	    	//relevant score >= irrelevant score
    	    	if(score1 > score2)
    	    		tFlag = "rel";
    	    	else if(score1 < score2)
    	    		tFlag = "irrel";
    	    	else if(score1 == score2)
    	    		tFlag = "equal";
    	    	//append the results to stringbuffer
    	    	currentIndexLineSB.append(tFlag+" ").append((new Double(score1)).toString()+" ")
    	    		.append((new Double(score2)).toString()+"\n");
    		}
    		long endtime1 = System.currentTimeMillis();
			System.out.println("File Write buffer Time: " + (endtime1 - starttime1)/1000 + "s");
    		System.out.println("File Write Operateion Start!");
    		long starttime = System.currentTimeMillis();
    		//write the content of StringBuffer to files
			indexWriter.write(currentIndexLineSB.toString());
			long endtime = System.currentTimeMillis();
			System.out.println("File Write Time: " + (endtime - starttime)/1000 + "s");
            
            //close the files
            indexWriter.close();
            //release the StringBuffer
            currentIndexLineSB = null;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
