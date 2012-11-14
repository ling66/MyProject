import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class QueryTermsDetailInDocument {	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str="[aa] bbb [cc] ]dd";
		System.out.println(str.lastIndexOf("]"));
		System.out.println(str.substring(14+1));
	}
	
	/**
	 * @param args
	 */
	public void printQueryTermsDetailInDocuments(String queryNo,String queryStr,HashMap<String, DocumentNode> docNumHahsMap
			,HashMap<String,InvertIndexNode> queryTermHashMap,TreeMap<Double,String> nTopOrderDocTreeMap){
		
		//1. generate document original number and assignment number relation
		HashMap<String, String> docNumRelHashMap = new HashMap<String, String>();
		Iterator<String> iterator = docNumHahsMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			docNumRelHashMap.put(docNumHahsMap.get(key).docOriginalNo, key);
		}
		
		//2. sperate the terms from queryStr
		ArrayList<String> queryList = new ArrayList<String>();
		StringTokenizer strTk=new StringTokenizer(queryStr);
		String curWord="";
    	while(strTk.hasMoreTokens()) {
			curWord = strTk.nextToken().toLowerCase();
			queryList.add(curWord);
    	}
		
		//3. generate document-terms details in qrels file
    	this.generateTermDetailsInQurelFile(queryNo, queryList, docNumRelHashMap, queryTermHashMap,docNumHahsMap);
    	System.out.println("----------------------------------------");
		//4. generate document-terms details in Top n document from nTopOrderDocTreeMap
    	Iterator<Double> iterator1 = nTopOrderDocTreeMap.keySet().iterator();
		while (iterator1.hasNext()) {
			Double key1 = iterator1.next();
			//deal with each term in query
			for(int i=0 ; i<queryList.size(); i++){
				//print document number
				if(i==0)
					System.out.println(queryNo+" "+docNumHahsMap.get(nTopOrderDocTreeMap.get(key1)).docOriginalNo
							+" "+docNumHahsMap.get(nTopOrderDocTreeMap.get(key1)).docLength);
				//
				System.out.print(queryList.get(i)+"-"
				  +queryTermHashMap.get(queryList.get(i)).fDt.get(new Integer(nTopOrderDocTreeMap.get(key1)))+" ");
			}
			System.out.println();
		}
	}
	/**
	 * @param args
	 */
	public void generateTermDetailsInQurelFile(String queryNo, ArrayList<String> queryList
			,HashMap<String, String> docNumRelHashMap,HashMap<String,InvertIndexNode> queryTermHashMap
			,HashMap<String, DocumentNode> docNumHahsMap){
		//read information from qrels file
		File file = new File("qrels");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
			String[] strArray;
			while ((curLineString = reader.readLine()) != null) {
				// String split method
				strArray = curLineString.split(" " + "|\n");
				//
				if(	strArray[0].equals(queryNo) && strArray[3].equals("1")){
					//find relevant document
					if(docNumRelHashMap.get(strArray[2]) != null){
						//deal with each term in query
						for(int i=0 ; i<queryList.size(); i++){
							//print document number
							if(i==0)
								System.out.println(queryNo+" "+ strArray[2]+" "+docNumHahsMap.get(docNumRelHashMap.get(strArray[2])).docLength);
							//
							System.out.print(queryList.get(i)+"-"
							  +queryTermHashMap.get(queryList.get(i)).fDt.get(new Integer(docNumRelHashMap.get(strArray[2])))+" ");
						}
						System.out.println();
					}
					else
						break;
				}
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
	public DocREScore matchDocumentCollection(String searchType, String queryNo,int R,int E,int precision
			,HashMap<String, DocumentNode> docNumHahsMap, TreeMap<Double,String> nTopOrderDocTreeMap,int queryRelDocNum){
		//read information from qrels file
//		File file = new File("qrels");
		File file = new File("/public/courses/MultimediaInfoRetrieval/2011/a2/qrels");
		BufferedReader reader = null;
		//
		DocREScore tDocREScore = null;
		try {
			HashMap<String,String> compareHashMap = new HashMap<String,String>();
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
			String[] strArray;
			while ((curLineString = reader.readLine()) != null) {
				// String split method
				strArray = curLineString.split(" " + "|\n");
				//find relevant document and add it into compareHashMap
				if(	strArray[0].equals(queryNo) && strArray[3].equals("1")){
					compareHashMap.put(strArray[2], "1");
				}
			}
			//store matched documents
			ArrayList<String> comDocsList = new ArrayList<String>();
			//compare two document collections
			Iterator<Double> iterator = nTopOrderDocTreeMap.keySet().iterator();
			int rankNum = 1;
			//
			double curScore = 0.0;
			int curFindDocNum = 0;
			while(iterator.hasNext()){
				Double key = iterator.next();
				if(compareHashMap.get(docNumHahsMap.get(nTopOrderDocTreeMap.get(key)).docOriginalNo) != null){
					//increase the number of find document by one
					curFindDocNum = curFindDocNum + 1;
					comDocsList.add(String.valueOf(rankNum)+"-"+docNumHahsMap.get(nTopOrderDocTreeMap.get(key)).docOriginalNo);
					//if search type equal PRF, calculate average precision for these R-E parameters
					if(searchType.equals("PRF")){
						curScore = curScore + curFindDocNum/((double)rankNum);
//						System.out.println("********curScore is :"+curScore
//								+" curFindDocNum:"+curFindDocNum
//								+" rankNum:"+rankNum);
					}
				}
				rankNum = rankNum + 1;
			}
			//print result
			if(comDocsList.size()==0)
				System.out.println("Sorry,no Match documents exists!!");
			else{
				System.out.println("Match documents include : ");
				for(int i=0 ; i<comDocsList.size(); i++){
					System.out.println(comDocsList.get(i));
				}
				System.out.println("----------------------------------------------------");
				System.out.println("The precision is : "+comDocsList.size()+"/"+precision);
			}
			//??????
			//store R E and precision score
			if (searchType.equals("PRF")){
				tDocREScore = new DocREScore();
				tDocREScore.queryNo = queryNo;
				tDocREScore.R = R;
				tDocREScore.E = E;
				tDocREScore.findDocNum = comDocsList.size();
				tDocREScore.score = curScore/queryRelDocNum;
			}
				
			reader.close();
			return tDocREScore;
		} catch (IOException e) {
			e.printStackTrace();
			return tDocREScore;
		} finally {
			if (reader != null) {
				try {
					reader.close();
					return tDocREScore;
				} catch (IOException e1) {
				}
			}
		}
	}

}
