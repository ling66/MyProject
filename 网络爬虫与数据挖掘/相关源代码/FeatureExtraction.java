import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class FeatureExtraction {
	//store features for relevant and irrelevant collection
	private ArrayList<String> relFeaturesArrayList = new ArrayList<String>();
	private ArrayList<String> irrelFeaturesArrayList = new ArrayList<String>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FeatureExtraction tFeatureExtraction = new FeatureExtraction();
		
	}
	
	/**
	 * @param args
	 */
	public void featureController(int relFeatureNum,int irrelFeatureNum
			,HashMap<String,TermNode> relTermHashMap,HashMap<String,TermNode> irrelTermHashMap) {
		//read term info from TermDiff file
		this.generateFeatureCollection("./basic/TermDiff");
		//generate arff file
		this.generateWekaFile("mobile.arff",relFeatureNum,irrelFeatureNum,relTermHashMap,irrelTermHashMap);
		//generate mode file
		this.generateFeaturesModel("model_basic", relFeatureNum, irrelFeatureNum);
		
		System.out.println("Finish");
	}
	
	/**
	 * @param args
	 */
	public void generateFeatureCollection(String fileName) {
		// read term info from TermDiff file
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String curLineString = "";
			String[] curArray = null;
			while ((curLineString = reader.readLine()) != null) {
				curArray = curLineString.split(" ");
				//store term into different collection
				if(curArray[2].equals("rel"))
					this.relFeaturesArrayList.add(curArray[0]);
				else if(curArray[2].equals("irrel"))
					this.irrelFeaturesArrayList.add(curArray[0]);
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
	public void generateWekaFile(String fileName,int relFeatureNum,int irrelFeatureNum
			,HashMap<String,TermNode> relTermHashMap,HashMap<String,TermNode> irrelTermHashMap) {
		//store the content of the arff file
		StringBuffer currentLineSB = new StringBuffer();
		//generate title part
		currentLineSB.append("@relation mobile\n\n");
		//generate attribute part
//		currentLineSB.append("@attribute docNo real\n");
		//relevant part
		for(int i=0;i<relFeatureNum;i++)
			currentLineSB.append("@attribute ").append(this.relFeaturesArrayList.get(i)+" {yes, no}\n");
		//irrelevant part
		for(int i=0;i<irrelFeatureNum;i++)
			currentLineSB.append("@attribute ").append(this.irrelFeaturesArrayList.get(i)+" {yes, no}\n");
		//class part
		currentLineSB.append("@attribute mobile {yes, no}\n\n");
		
		//generate data part
		currentLineSB.append("@data\n");
		String dataPart = this.generateWekaDataPart(relFeatureNum, irrelFeatureNum, relTermHashMap, irrelTermHashMap);
		currentLineSB.append(dataPart);
		
		//write file
		FileOperation.writeFile(fileName, currentLineSB.toString(), false);
	}
	
	/**
	 * @param args
	 */
	public String generateWekaDataPart(int relFeatureNum,int irrelFeatureNum
			,HashMap<String,TermNode> relTermHashMap,HashMap<String,TermNode> irrelTermHashMap) {
		//store data part of the arff file
		StringBuffer currentLineSB = new StringBuffer();
		
		File file = new File("./basic/pages");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			//current line content
            String curLineString = null;
            String[] splitArray = null;
			while ((curLineString = reader.readLine()) != null) {
				//format: class,file_name,url
				//    eg: 1,6,http://www.rmit.edu.au/news.html
				splitArray = curLineString.split(",");
				//generate format: docNo,attribute1,attribute2...Mobile
				//             eg: 1,yes,yes,no,yes...,yes
//				//add doc No part
//				currentLineSB.append(splitArray[1]+",");
				//add attribute part
				//relevant part
				for(int i=0;i<relFeatureNum;i++)
				{
//					System.out.println(splitArray[0]+"-"+splitArray[1]+"-"+this.relFeaturesArrayList.get(i)
//							+"-"+relTermHashMap.get(this.relFeaturesArrayList.get(i)).fDt.get(splitArray[1]));
					//belong to relevant collection
					if(splitArray[0].equals("1"))
					{
						//term not exist in current collection
						if(relTermHashMap.get(this.relFeaturesArrayList.get(i))!=null)
						{
							//term not exist in current document
							if(relTermHashMap.get(this.relFeaturesArrayList.get(i)).fDt.get(splitArray[1])==null)
								currentLineSB.append("no,");
							else
								currentLineSB.append("yes,");
						}
						else 
							currentLineSB.append("no,");
					}
					//belong to irrelevant collection
					if(splitArray[0].equals("0"))
					{
						//term not exist in current collection
						if(irrelTermHashMap.get(this.relFeaturesArrayList.get(i))!=null)
						{
							//term not exist in current document
							if(irrelTermHashMap.get(this.relFeaturesArrayList.get(i)).fDt.get(splitArray[1])==null)
								currentLineSB.append("no,");
							else
								currentLineSB.append("yes,");
						}
						else 
							currentLineSB.append("no,");
					}
				}
				//irrelevant part
				for(int i=0;i<irrelFeatureNum;i++)
				{
					//belong to relevant collection
					if(splitArray[0].equals("1"))
					{
						//term not exist in current collection
						if(relTermHashMap.get(this.irrelFeaturesArrayList.get(i))!=null)
						{
							//term not exist in current document
							if(relTermHashMap.get(this.irrelFeaturesArrayList.get(i)).fDt.get(splitArray[1])==null)
								currentLineSB.append("no,");
							else
								currentLineSB.append("yes,");
						}
						else 
							currentLineSB.append("no,");
					}
					//belong to irrelevant collection
					if(splitArray[0].equals("0"))
					{
						//term not exist in current collection
						if(irrelTermHashMap.get(this.irrelFeaturesArrayList.get(i))!=null)
						{
							//term not exist in current document
							if(irrelTermHashMap.get(this.irrelFeaturesArrayList.get(i)).fDt.get(splitArray[1])==null)
								currentLineSB.append("no,");
							else
								currentLineSB.append("yes,");
						}
						else 
							currentLineSB.append("no,");
					}
				}
				//add class part
				if(splitArray[0].equals("1"))
					currentLineSB.append("yes\n");
				else if(splitArray[0].equals("0"))
					currentLineSB.append("no\n");
			}
			reader.close();
			return currentLineSB.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * generate model feature set and write to model file
	 * format: 
	 * rel 20
	 * irrel 20
	 * feature1 feature2 feature3 ...
	 */
	public void generateFeaturesModel(String fileName,int relFeatureNum,int irrelFeatureNum) {
		//store the content of the model file
		StringBuffer currentLineSB = new StringBuffer();
		currentLineSB.append("rel "+Integer.toString(relFeatureNum)+"\n");
		currentLineSB.append("irrel "+Integer.toString(irrelFeatureNum)+"\n");
		//add features to model file
		//add relevant features
		currentLineSB.append(this.relFeaturesArrayList.get(0));
		for(int i=1;i<relFeatureNum;i++)
			currentLineSB.append(" "+this.relFeaturesArrayList.get(i));
		//add irrelevant features
		for(int i=0;i<irrelFeatureNum;i++)
			currentLineSB.append(" "+this.irrelFeaturesArrayList.get(i));
		currentLineSB.append("\n");
		
		//write file
		FileOperation.writeFile(fileName, currentLineSB.toString(), false);
	}
	
}
