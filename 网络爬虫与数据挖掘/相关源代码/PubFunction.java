import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PubFunction {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Test 1: url type
		//http://content.mycareer.com.au/advice-research/career/pave-the-path-to-power.aspx
		//http://resources.theage.com.au/common/media-common-1.0/css/output/common.skin.news_min.css
		//http://jobs.com.au/jobs/melbourne/-/-/?s=500?s_rid=smh:rainbowstrip:bullet3:MoreJobsMelb
		//not exits page: http://www.bxxyyzz.com/login.html
//		boolean result = PubFunction.checkURL("#nav");
//		System.out.println("The return result is: " + result);
		
		//Test 2: time delay
//		System.out.println(PubFunction.getCurrentTime());
//		System.out.println(System.currentTimeMillis());
//		PubFunction.delayTimer(3000,1000,4);
//		System.out.println(PubFunction.getCurrentTime());
//		System.out.println(System.currentTimeMillis());
		
		//Test 3: get Host name
//		String str = "http://www.theage.com.au/digital-life/mobiles/Mobiles/iPhone";
//		System.out.println(PubFunction.getHostString(str));
		
		//Test 4: urlRegularExpressionChecker
//		String str = "http://www.theage.com.au/digital-life/mobiles/Mobiles/iPhone";
//		System.out.println(PubFunction.urlRegularExpressionChecker("/",str));
		
		//Test 5: decision regular expression
//		String curLineString = "|   |   |   |   hous = no: no (2.0)";
//		curLineString =	PubFunction.decisionTreeFilter(curLineString);
//		System.out.println(curLineString.substring(0, curLineString.indexOf("=")));
		//Test 6: select attribute regular expression
		String curLineString = "           0(  0 %)     186 campaign";
		curLineString = PubFunction.selectAttFilter("1",curLineString);
		System.out.println(curLineString);
//		System.out.println(curLineString.substring(0,curLineString.indexOf("(")));
		curLineString = PubFunction.selectAttFilter("2",curLineString);
		System.out.println(curLineString);
	}
	
	/** 
     * get current DateTime
     */
	public static String getCurrentTime() {
		Date currentTime = new Date();    
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
		String dateString = formatter.format(currentTime);   
		return dateString;
	}
	
	/** 
     * get current time
     */
	public static void delayTimer(long curStartTime,long lastEndTime,long delayTime) {
		try {
			if(lastEndTime == 0)
				return;
			//calculate interval between start time to end time
			long interval = (curStartTime - lastEndTime)/1000;
			
			if(interval < delayTime)
			{
				Thread.sleep((delayTime - interval)*1000);
			}
		} catch (InterruptedException ie) {
			System.out.println(ie.getMessage());
		}
	}
	
	/**
	 * String Filter function using regular expression technology
	 * example1: this implementation "first-second" will be converted into "firstsecond"
	 * example2: the string"6.6 a. .b 5/4 patent. . . .and .a b...c 1/1 2 /2 3/ 3  d..d xx+*-~yy zz.'"
	 *  will be changed to "6.6 a b 5/4 patent   and a b c 1/1 2 2 3 3  d d xxyy"
	 */
	public static String StringFilter(String str) {
		//example: "6.6 a. .b 5/4 patent. . . .and .a b....c 1/1 2 /2 3/ 3 d..d xx+*-~yy zz.'"
		//1.  filter other characters
		String regEx = "[\\-]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		String str1 = m.replaceAll("").trim();
		
		//in this part,we find some very strange lable, should be very careful
		//regEx = "[ ?.©»«�!',\\-/~#$@%^&*()_+=|{};:\"'>`\\[\\]\\\\]|(</|<)|[0-9]";
		regEx = "[ ?.°©»«�!',/~#$@%^&*()_+=|\\n\\t{};:\"'>`\\[\\]\\\\]|(</|<)|[0-9]";
		p = Pattern.compile(regEx);
		m = p.matcher(str1);
		String str2 = m.replaceAll(" ").trim();
		return str2;
//		//2. filter multiple space
//		regEx = "\\s{2,}";
//		p = Pattern.compile(regEx);
//		m = p.matcher(str1);
//		String str3 = m.replaceAll(" ").trim();
//		return str3.trim();
	}
	
	/**
	 * weka decision tree Filter
	 * http://www.regexlab.com/zh/regref.htm
	 */
	public static String decisionTreeFilter(String str) {
		String regEx = "[|\\s]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		String str1 = m.replaceAll("").trim();
		return str1;
	}
	
	/**
	 * weka select attribute filter
	 * http://www.regexlab.com/zh/regref.htm
	 */
	public static String selectAttFilter(String type,String str) {
		String regEx = " ";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		String str1 = m.replaceAll("").trim();
		if(type.equals("1"))
			return str1;
		else if(type.equals("2"))
		{
			regEx = "[0-9\\(%\\)]";
			p = Pattern.compile(regEx);
			m = p.matcher(str1);
			str1 = m.replaceAll("").trim();
			return str1;
		}
		return str1;
	}
	
	/**
	 * sort HashMap by values and return a LinkedHashMap<String,TermNode>
	 * http://www.lampos.net/sort-hashmap
	 */
	public static LinkedHashMap<String,TermNode> sortHashMapByValues(HashMap<String,Double> passedMap,HashMap<String,TermNode> tempHashMap) {
		ArrayList<String> mapKeys = new ArrayList<String>(passedMap.keySet());
		ArrayList<Double> mapValues = new ArrayList<Double>(passedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);
	        
	    LinkedHashMap<String,Double> sortedMap = new LinkedHashMap<String,Double>();
	    
	    Iterator<Double> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Double val = valueIt.next();
	        Iterator<String> keyIt = mapKeys.iterator();
	        
	        while (keyIt.hasNext()) {
	            String key = keyIt.next();
	            Double comp1 = passedMap.get(key);
	            Double comp2 = val;
	            
	            if (Double.compare(comp1, comp2)==0){
	                passedMap.remove(key);
	                mapKeys.remove(key);
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    
	    LinkedHashMap<String,TermNode> sortedMap1 = new LinkedHashMap<String,TermNode>();
	    ArrayList<String> sortedMapKeys = new ArrayList<String>(sortedMap.keySet());
		Collections.reverse(sortedMapKeys);
		Iterator<String> keys = sortedMapKeys.iterator();
	    while (keys.hasNext()) {
	    	String val = keys.next();
	    	sortedMap1.put(val, tempHashMap.get(val));
	    }
		
	    return sortedMap1;
	}
	
	/**
	 * sort HashMap by values and return a LinkedHashMap<String,Double>
	 * http://www.lampos.net/sort-hashmap
	 */
	public static LinkedHashMap<String,Double> sortHashMapByValues(HashMap<String,Double> passedMap) {
		ArrayList<String> mapKeys = new ArrayList<String>(passedMap.keySet());
		ArrayList<Double> mapValues = new ArrayList<Double>(passedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);
	        
	    LinkedHashMap<String,Double> sortedMap = new LinkedHashMap<String,Double>();
	    
	    Iterator<Double> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Double val = valueIt.next();
	        Iterator<String> keyIt = mapKeys.iterator();
	        
	        while (keyIt.hasNext()) {
	            String key = keyIt.next();
	            Double comp1 = passedMap.get(key);
	            Double comp2 = val;
	            
	            if (Double.compare(comp1, comp2)==0){
	                passedMap.remove(key);
	                mapKeys.remove(key);
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    
	    LinkedHashMap<String,Double> sortedMap1 = new LinkedHashMap<String,Double>();
	    ArrayList<String> sortedMapKeys = new ArrayList<String>(sortedMap.keySet());
		Collections.reverse(sortedMapKeys);
		Iterator<String> keys = sortedMapKeys.iterator();
	    while (keys.hasNext()) {
	    	String val = keys.next();
	    	sortedMap1.put(val, sortedMap.get(val));
	    }
		
	    return sortedMap1;
	}
}
