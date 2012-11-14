import java.io.*;
import java.util.*;


public class FileOperation {
	/** 
     * Initial file
     * @param fileName   
     * @param content  
     */
	public static void iniLogFile(String fileName) {
        try {
        	//status=true: add the content to the end of the file
        	//status=false: rewrite the whole file, clear the history data 
            FileWriter writer = new FileWriter(fileName, false);  
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
		
	/** 
     * Write document TreeMap to the Map files 
     */
	public synchronized static void writeDocumentTreeMapToFileImprove(TreeMap<Integer,DocumentNode> tTreeMap,
			String docMapName,boolean status) {
        try {
        	//status=true: add the content to the end of the file
        	//status=false: rewrite the whole file, clear the history data 
            FileWriter docMapWriter = new FileWriter(docMapName, status);
            //StringBuffer can provide the much more efficiency for disk write operation
            //use for write map file
    		StringBuffer currentLineSB = new StringBuffer();
    		
            Iterator<Integer> iterator = tTreeMap.keySet().iterator();
    		while (iterator.hasNext()) {    			
    			Integer key = iterator.next();
    			//put the document map information into currentLineSB buffer
    			//the format like: assign_document_number real_document (example: 1 FT911-1)
    			currentLineSB.append(String.valueOf(key)).append(" ").append(tTreeMap.get(key).docOriginalNo+" ")
    			             .append(String.valueOf(tTreeMap.get(key).docLength)+" ")
    			             .append(String.valueOf(tTreeMap.get(key).startPosition)+" ")
    						 .append(String.valueOf(tTreeMap.get(key).endPosition)+"\n");
    		}
    		//write files
			docMapWriter.write(currentLineSB.toString());
            //close the files
    		docMapWriter.close();
    		//release the StringBuffer
    		currentLineSB = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/** 
     * Write HashMap to the dictionary and inverted index files
     */
	public synchronized static void writeHashMapToFileImprove(HashMap<String,InvertIndexNode> tHashMap,
			String dictName,String indexName,boolean status) {
        try {
        	//status=true: add the content to the end of the file
        	//status=false: rewrite the whole file, clear the history data 
        	//create dictionary file Writer
            FileWriter dictWriter = new FileWriter(dictName, status);
            //create invert list file Writer
            FileWriter indexWriter = new FileWriter(indexName, status);
            
            //record the current line number
            int currentLineNumber = 0;
            //StringBuffer can provide the much more efficiency for disk write operation
            //use for write invert_list file
            StringBuffer currentIndexLineSB = new StringBuffer();
            //use for write dictionary file
            StringBuffer currentDictLineSB = new StringBuffer();
    		
            Iterator<String> iterator = tHashMap.keySet().iterator();
            System.out.println("File Write Buffer Operateion Start!");
            long starttime1 = System.currentTimeMillis();
    		while (iterator.hasNext()) {
    			//????????????????
//    			if(currentLineNumber == 100000){
    		   if(currentLineNumber == 80000 ||currentLineNumber == 160000){
    				//write the content of these two StringBuffer to files
    				System.out.println("The current line number is: "+currentLineNumber);
    				dictWriter.write(currentDictLineSB.toString());
    				indexWriter.write(currentIndexLineSB.toString());
    				currentIndexLineSB = new StringBuffer();
    				currentDictLineSB = new StringBuffer();
    			}
    			currentLineNumber = currentLineNumber + 1;
    			//get each term from HashMap
    			String key = iterator.next();
    			//put the dictionary information into currentDictLineSB buffer
    			//the format like: word ft line-number (example: government 6 368)
    			currentDictLineSB.append(key+" ").append(String.valueOf(tHashMap.get(key).ft)+" ").append(String.valueOf(currentLineNumber)+"\n");
    			//loop deal with the each document node for each term
    			Iterator<Integer> iterator1 = tHashMap.get(key).fDt.keySet().iterator();
    			while (iterator1.hasNext()) {
    				Integer key1 = iterator1.next();
    				//put the document term occurrence information into currentIndexLineSB buffer
        			//the format like: document_number term frequency in this document (example: 6 128)
    				currentIndexLineSB.append(String.valueOf(key1)+" ").append(String.valueOf(tHashMap.get(key).fDt.get(key1))+" ");
    			}
    			currentIndexLineSB.append("\n");
    		}
    		long endtime1 = System.currentTimeMillis();
			System.out.println("File Write buffer Time: " + (endtime1 - starttime1)/1000 + "s");
    		System.out.println("File Write Operateion Start!");
    		long starttime = System.currentTimeMillis();
    		//write the content of these two StringBuffer to files
			dictWriter.write(currentDictLineSB.toString());
			indexWriter.write(currentIndexLineSB.toString());
			long endtime = System.currentTimeMillis();
			System.out.println("File Write Time: " + (endtime - starttime)/1000 + "s");
            
            //close the files
            dictWriter.close();
            indexWriter.close();
            //release the StringBuffer
            currentDictLineSB = null;
            currentIndexLineSB = null;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/** 
     * Write document TreeMap to the Map files 
     */
	public synchronized static void writeREScoreToFile(ArrayList<DocREScore> tREScoreArrayList,
			String docMapName,boolean status) {
        try {
        	//status=true: add the content to the end of the file
        	//status=false: rewrite the whole file, clear the history data 
            FileWriter docMapWriter = new FileWriter(docMapName, status);
            //StringBuffer can provide the much more efficiency for disk write operation
            //use for write map file
    		StringBuffer currentLineSB = new StringBuffer();
    		
    		for (int i = 0; i < tREScoreArrayList.size(); i++) {
				currentLineSB.append(tREScoreArrayList.get(i).queryNo
						+" R:"+String.valueOf(tREScoreArrayList.get(i).R) 
						+ " E:" + String.valueOf(tREScoreArrayList.get(i).E) 
						+" Retrieved:" +String.valueOf(tREScoreArrayList.get(i).findDocNum)
						+" Score:"+String.valueOf(tREScoreArrayList.get(i).score)+"\n");
			}
    		//write files
			docMapWriter.write(currentLineSB.toString());
            //close the files
    		docMapWriter.close();
    		//release the StringBuffer
    		currentLineSB = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
