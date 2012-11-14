
import java.util.*;

public class InvertIndexNode {
	//example: governments: 2 <10,2> <23,3>
	//The number of documents in the collection in which term occurs 
	public int ft = 0; 
	//How often term occurs in document
	//public HashMap<String,Integer> fDt = new HashMap<String,Integer>();
	public TreeMap<Integer,Integer> fDt = new TreeMap<Integer,Integer>();
}
