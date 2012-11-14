import java.util.HashMap;


public class TermNode {
	//The number of documents in the collection in which term occurs 
	public int ft = 0; 
	//How often term occurs in each document under different collection
	//format: <document number,term frequency>
	public HashMap<String,Integer> fDt = new HashMap<String,Integer>();
	//the total number of frequency in collection
	public int fc = 0;
	//store the score of this node
	//formula: (ft / total number of documents in collection)* (1+Math.log(fc))
	public double score = 0.0;
}
