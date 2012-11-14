import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class re_train_classifier {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		re_train_classifier tRe_train_classifier = new re_train_classifier();
		tRe_train_classifier.controller("./retrain/mobile.arff", "./basic/pages","./retrain/classified_doc_J48"
				, "./retrain/pages","./retrain/pages_smart", "./retrain/mobile_test.arff", "./retrain/mobile_test_labeled.arff","./retrain/classified_doc_J48");

	}
	
	/**
	 * @param args
	 */
	public void controller(String trainingFile,String oriPagesFile,String newLabeledFile
			,String newPagesFile,String newPagesSmartFile,String testingFile,String testingLabeledFile,String finalFile) {
		//generate combined Pages List and new pages_smart file(use for build test arff data set)
		this.generateCombinedPagesList(oriPagesFile,newLabeledFile,newPagesFile,newPagesSmartFile);
		//generate term inverted list and training arff file
		re_train_index tRe_train_index = new re_train_index();
		tRe_train_index.indexController(120,30);
		//use J48 algorithm to classify document
		classify_smart_J48 tClassify_smart_J48 = new classify_smart_J48();
		tClassify_smart_J48.indexController("./retrain/mobile.arff","./retrain/pages_smart","./retrain/mobile_test.arff"
				,"./retrain/mobile_test_labeled.arff","./retrain/classified_doc_J48_improve","0");
		
		System.out.println("Finish");
	}
	
	/**
	 * Combine original pages and user label pages list
	 * type:
	 *  original pages:   1-relevant; 0-irrelevant;
	 *  user label pages: 2-relevant; 3-irrelevant;
	 * Also generate new pages_smart file(use for build test arff data set)
	 */
	public void generateCombinedPagesList(String oriPagesFile,String newLabeledFile
			,String newPagesFile,String newPagesSmartFile)
	{
		//copy original pages file(from ./base/pages) to new pages file(./retrain/pages)
		FileOperation.copyFile(oriPagesFile, newPagesFile);
		//read the content of user labeled file, add it to new pages file
		File file = new File(newLabeledFile);
		BufferedReader reader = null;
		//store the new content of pages file
		StringBuffer pagesSB = new StringBuffer();
		//store the new content of pages_smart file
		StringBuffer pagesSmartSB = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
			String[] curArray = null;
			int newDocNo = 1;
			//original format: 1,http://www.news.com.au/technology,Not Mobile
			//new format     : 1,http://www.news.com.au/technology,Not Mobile,Mobile,10
			while ((curLineString = reader.readLine()) != null) {
				curArray = curLineString.split(",");
				//the element length equals 5 means user has labeled this record
				if(curArray.length==5)
				{
					for (int i = 0; i < Integer.parseInt(curArray[4]); i++) {
						//here we add a letter N at the front of the document number to avoid name repeated problem
						if (curArray[3].equals("Mobile"))
							pagesSB.append("2," + curArray[0] + "-"+Integer.toString(newDocNo)+","+ curArray[1] + "\n");
						else if (curArray[3].equals("Not Mobile"))
							pagesSB.append("3," + curArray[0] + "-"+Integer.toString(newDocNo)+","+ curArray[1] + "\n");
						//increase new document No by one
						newDocNo = newDocNo + 1;
					}
				}
				//the record has not been labeled add to new page_smart file to build testing arff file
				else 
				{
					pagesSmartSB.append("1,"+curArray[0]+","+curArray[1]+"\n");
				}
					
			}
			// System.out.println("Total line number are: "+linerNumber);
			reader.close();
			//write file
			FileOperation.writeFile(newPagesFile, pagesSB.toString(), true);
			FileOperation.writeFile(newPagesSmartFile, pagesSmartSB.toString(), false);
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
	
}
