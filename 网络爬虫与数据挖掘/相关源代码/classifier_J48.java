import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;


public class classifier_J48 {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		classifier_J48 tclassifier_J48 = new classifier_J48();
		tclassifier_J48.classifierController(null,"mobile.arff","./smart/mobile_test.arff"
				,"./smart/mobile_test_labeled.arff","./smart/classified_doc_J48");
	}
	
	/**
	 * @param args
	 */
	public void classifierController(HashMap<String,DocNode> docInfoHashMap,String trainingFile,String testingFile
			,String testingLabeledFile,String finalFile) {
		//create classifier
		Classifier tClassifier = this.createClassifier(trainingFile);
		//classify new instances
		ArrayList<String> tDocClassArrayList = this.classifyNewInstances(tClassifier, testingFile, testingLabeledFile);
		//generate final feed back file
		this.generateFeedBackDocument(docInfoHashMap, tDocClassArrayList, finalFile);
	}
	
	/**
	 * @param args
	 */
	public Classifier createClassifier(String trainingFile) {
		Instances ins = null;
		Classifier cfs = null;
		try {
			 //read training data set
			File file = new File(trainingFile);
			ArffLoader loader = new ArffLoader();
			loader.setFile(file);
			ins = loader.getDataSet();
			//set class Index(last column in arff file)
			ins.setClassIndex(ins.numAttributes() - 1);
			//initial classifier
			String[] options = new String[1];
			// unpruned tree
			options[0] = "-U"; 
			cfs = new weka.classifiers.trees.J48();
			// set the options
			cfs.setOptions(options);    
			//build classifier
			cfs.buildClassifier(ins);
//			String xx = cfs.toString();
//			System.out.println(xx);
			return cfs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param args
	 */
	public ArrayList<String> classifyNewInstances(Classifier tClassifier,String testingFile,String testingLabeledFile) {
		try {
			// store document class type
			ArrayList<String> tDocClassArrayList = new ArrayList<String>();
			// load unlabeled data
			Instances unlabeled = new Instances(new BufferedReader(new FileReader(testingFile)));
			// set class attribute
			unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
			// create copy
			Instances labeled = new Instances(unlabeled);
			// label instances
			for (int i = 0; i < unlabeled.numInstances(); i++) {
				double clsLabel = tClassifier.classifyInstance(unlabeled.instance(i));
				labeled.instance(i).setClassValue(clsLabel);
				//store label result into arraylist
				if(clsLabel == 0.0)
					tDocClassArrayList.add("Mobile");
				else if(clsLabel == 1.0)
					tDocClassArrayList.add("Not Mobile");
			}
			// save labeled data
			BufferedWriter writer = new BufferedWriter(new FileWriter(testingLabeledFile));
			writer.write(labeled.toString());
			writer.newLine();
			writer.flush();
			writer.close();
			return tDocClassArrayList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param args
	 */
	public void generateFeedBackDocument(HashMap<String,DocNode> docInfoHashMap,ArrayList<String> tDocClassArrayList,String finalFile) {
		// store the content of file
		StringBuffer currentLineSB = new StringBuffer();
		Iterator<String> iterator = docInfoHashMap.keySet().iterator();
		int lineNum = 0;
		while (iterator.hasNext()) {
			// get document number from HashMap
			String docNo = iterator.next();
			currentLineSB.append(docNo+","+docInfoHashMap.get(docNo).url+","+tDocClassArrayList.get(lineNum)+"\n");
			lineNum = lineNum + 1;
		}
		// write file
		FileOperation.writeFile(finalFile, currentLineSB.toString(), false);
	}
}
