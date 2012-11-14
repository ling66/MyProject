import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/** 
 * Running command for this program:
 * format: crawl [-politeness <seconds>] [-maxpages <pages>] seed_url
 * example:
 * 1. crawl -politeness 0 -maxpages 200 http://www.news.com.au/technology
 * 2. crawl -maxpages 20 http://www.news.com.au/technology
 * 3. crawl -politeness 2 http://www.news.com.au/technology
 * 4. crawl http://www.news.com.au/technology
 */

public class crawl_smart {
	// store the number of max download pages
	private int maxpages = 20;
	// store the number of the current download pages
	private int downloadPages = 0;
	// store input url
	private String inputURL = "";
	// store the host url
	private String hostURL = "";
	//time control variable
	private long delayTime = 10;
	private long lastEndTime = 0;
		
	// Define collections to store page link
	// store unvisit page links
	private LinkedList<String> unVisitLinkedList = new LinkedList<String>();
	// store visited page links
	private LinkedList<String> visitedLinkedList = new LinkedList<String>();
	// store (page link-document number) relation
	private HashMap<String, String> urlDocNumHahsMap = new HashMap<String, String>();
	// store download page links
	private ArrayList<String> downloadedPagesArrayList = new ArrayList<String>();
	// store ignore page links(not text/html type). eg. image, video
	private ArrayList<String> ignorePagesArrayList = new ArrayList<String>();

	/**
	 * @param args
	 */
	/*
	 * public static void main(String[] args) { //Get User input parameter
	 * 
	 * //crawl the web to fetch pages
	 * 
	 * }
	 */
	// /*
	public static void main(String[] args) {
		//user command line input
		ArrayList<String> inputArrayList = new ArrayList<String>();
		// User input format:
		// crawl -politeness 0 -maxpages 200 http://www.news.com.au/technology
		for (String s : args) {
			inputArrayList.add(s);
		}
		
		crawl_smart tCral_smart = new crawl_smart();
//		tCral_smart.maxpages = 200;
//		tCral_smart.delayTime = 0;
//		tCral_smart.inputURL = "http://www.news.com.au/technology";
		//get the input information
		for (int i = 0; i < inputArrayList.size(); i++) {
			if (inputArrayList.get(i).contains("-politeness"))
				tCral_smart.delayTime = Integer.parseInt(inputArrayList.get(i + 1));
			else if (inputArrayList.get(i).contains("-maxpages"))
				tCral_smart.maxpages = Integer.parseInt(inputArrayList.get(i + 1));
			else if (inputArrayList.get(i).contains("http:"))
				tCral_smart.inputURL = inputArrayList.get(i);
		}

		System.out.println("User Input Information:");
		System.out.println("Politeness: " + tCral_smart.delayTime);
		System.out.println("Maxpages: " + tCral_smart.maxpages);
		System.out.println("URL: " + tCral_smart.inputURL);
		System.out.println("----------------------------Crawler Start------------------------------------");
		
//		if(1==1)
//			return;
		
		// check input link type
		if (!tCral_smart.checkPageContentType(tCral_smart.inputURL)) {
			System.out.println("Please check you input link, has to be text/html file!");
			return;
		}

		tCral_smart.webCrawler();

		// write result file
		// format: class document number,address eg.
		// 1,6,http://www.rmit.edu.au/login.html
		StringBuffer currentLineSB = new StringBuffer();
		for (int i = 0; i < tCral_smart.downloadedPagesArrayList.size(); i++)
			currentLineSB.append("1").append(",")
				.append(tCral_smart.urlDocNumHahsMap.get(tCral_smart.downloadedPagesArrayList.get(i)))
				.append(",").append(tCral_smart.downloadedPagesArrayList.get(i)).append("\n");
		FileOperation.writeFile("./smart/pages_smart",currentLineSB.toString(), false);

		System.out.println("Run finished!");
	}

	// */
	/**
	 * @param args
	 */
	public void webCrawler() {
		// check whether url is legal
		if (!this.inputURL.contains("http://")) {
			System.out.println("Sorry, the input link should use http protocol! ");
			return;
		}
		this.hostURL = this.getHostString(this.inputURL);

		this.visitPageRecursion(this.inputURL);

		// time politeness check?????
	}

	/**
	 * get host string
	 */
	public String getHostString(String tURL) {
		int pos = tURL.indexOf("/", 7);
		return tURL.substring(0, pos);
	}

	/**
	 * @param args
	 */
	public void visitPageRecursion(String tURL) {
		// stop condition : if find enough pages or this page has been visited
		// before
		if (this.maxpages <= this.downloadPages|| this.visitedLinkedList.contains(tURL)
				|| this.ignorePagesArrayList.contains(tURL))
			return;
		// check whether the current tURL is exist in unvisit page collection
		// if exist, move this page from unvisit to visit page collection
		if (this.unVisitLinkedList.contains(tURL)) {
			this.unVisitLinkedList.remove(tURL);
			this.visitedLinkedList.add(tURL);
		}
		// if not exist, add to visit page collection
		else
			this.visitedLinkedList.add(tURL);

		// check whether need download this page
		if (this.checkWhetherDownloadPage(tURL))
			this.downloadPage(this.getPageContent(tURL, "1"), tURL);

		ArrayList<String> urlArrayList = this.getPageLinks(tURL);
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println("Finish get links: " + tURL);
		System.out.println("----------------------------------------------------------------------------------------");
		// stop condition : no page find under this url
		// if urlArrayList is null, that means some error happened in
		// getPageLinks() method
		if (urlArrayList == null || (urlArrayList != null && urlArrayList.size() == 0))
			return;
		//
		String curURL = "";
		for (int i = 0; i < urlArrayList.size(); i++) {
			// get current URl
			curURL = urlArrayList.get(i);
			// add link to unvisit collection
			this.unVisitLinkedList.add(curURL);

			// check whether download current page
			if (this.checkWhetherDownloadPage(curURL))
				// get whole page content
				this.downloadPage(this.getPageContent(curURL, "1"), curURL);

			// ?????????????????????
			// download pages reach maxpages
			if (this.maxpages <= this.downloadPages)
				return;
		}
		// clone one object
		LinkedList<String> tempUnVisitLinkedList = (LinkedList<String>) this.unVisitLinkedList.clone();
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println("Current Download files:");
		for (int i = 0; i < this.downloadedPagesArrayList.size(); i++)
			System.out.println((i + 1)+ "-"+ this.downloadedPagesArrayList.get(i)+ "-"
					+ this.urlDocNumHahsMap.get(this.downloadedPagesArrayList.get(i)));
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println("Current unvisited files-Size-"+ tempUnVisitLinkedList.size());
		for (int j = 0; j < tempUnVisitLinkedList.size(); j++) {
			System.out.println(tempUnVisitLinkedList.get(j));
		}
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

		for (int i = 0; i < tempUnVisitLinkedList.size(); i++) {
			this.visitPageRecursion(tempUnVisitLinkedList.get(i));
		}
	}

	/**
	 * give one url, fetch all the legal links from this url
	 */
	public ArrayList<String> getPageLinks(String tURL) {
		try {
			//politeness checking
        	PubFunction.delayTimer(System.currentTimeMillis(),this.lastEndTime,this.delayTime);
        	this.lastEndTime = System.currentTimeMillis();
        	//start download page
			Document doc = Jsoup.connect(tURL).timeout(5000).get();
			//
			Elements links = doc.select("a[href]");
			String link = "";
			// store return url ArrayList
						ArrayList<String> urlArrayList = new ArrayList<String>();
			System.out.println("****************************************************************************************");
			System.out.println("The parent: " + tURL);
			System.out.println("****************************************************************************************");
			for (Iterator<Element> it = links.iterator(); it.hasNext();) {
				// Element name = (Element) it.next();
				Element name = it.next();
				// System.out.println(name.attr("href"));
				// check whether url is legal
				if (!this.checkURL(name.attr("href")))
					continue;

				// deal with relative path: eg. /lifestyle/life/blog/citykat add
				// host url
				if (this.urlRegularExpressionChecker("1", "/",
						name.attr("href")))
					link = this.hostURL + name.attr("href");
				else
					link = name.attr("href");
				// if link contains '#' then remove '#'
				// eg:
				// http://www.theage.com.au/digital-life/hometech/five-technologies-to-look-for-in-your-next-smartphone-20120410-1wlxq.html#comments
				if (link.contains("#"))
					link = link.substring(0, link.lastIndexOf("#"));
				// check whether this link exist in collection
				if (this.unVisitLinkedList.contains(link)|| this.visitedLinkedList.contains(link)|| urlArrayList.contains(link))
					continue;
				urlArrayList.add(link);
			}
			// return result
			return urlArrayList;
		} catch (Exception e) {
			System.out.println("Exciption in crawl_smart-getPageLinks!!!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * check whether the file type is text/html html,htm,php,asp,aspx ignore:
	 * 0.start with the same host 1.# eg.#nav
	 * http://www.smh.com.au/entertainment
	 * /restaurants-and-bars/a-suburban-kitchen
	 * -for-tony-bilson-20120424-1xi4n.html#comments 2.javascript: 3.https eg:
	 * https://membercentre.fairfax.com.au/NewsletterSubscription.aspx
	 * 4.relative path: eg. /lifestyle/life/blog/citykat
	 */
	public boolean checkURL(String tURL) {
		// 1. check url type
		if (!this.urlRegularExpressionChecker("1", "http:", tURL)&& !this.urlRegularExpressionChecker("1", "/", tURL))
			return false;

		// check the url is whether on the same host
		if (this.urlRegularExpressionChecker("1", "http:", tURL)&& !this.urlRegularExpressionChecker("1", this.hostURL, tURL))
			return false;
		
//		//deal with relative path: eg. /lifestyle/life/blog/citykat add host url 
//		if (this.urlRegularExpressionChecker("1", "/", tURL))
//			tURL = this.hostURL + tURL;
//		//if link contains '#' then remove '#'
//		// eg: http://www.theage.com.au/digital-life/hometech/five-technologies-to-look-for-in-your-next-smartphone-20120410-1wlxq.html#comments
//		if (tURL.contains("#"))
//			tURL = tURL.substring(0, tURL.lastIndexOf("#"));
//		// check whether this link exist in collection
//		if (this.unVisitLinkedList.contains(tURL)|| this.visitedLinkedList.contains(tURL)|| urlArrayList.contains(tURL))
//			return false;
//		// check url type: text/html
//		if (!checkPageContentType(tURL))
//			return false;

		return true;
	}

	/**
	 * check whether file is text/html
	 */
	public boolean checkPageContentType(String tURL) {
		try {
			System.out.println("Checking Content-Type:"+tURL);
			// check content-type must be text/html
			URL url = new URL(tURL);
			URLConnection connection = url.openConnection();
			String ct = connection.getContentType();
			// System.out.println("content-type: " + ct);
			// check whether the return string contains text/html
			if (ct != null && ct.contains("text/html"))
				return true;
			else
				return false;
		} catch (IOException e) {
			System.out.println("Exception in crawl.checkPageContentType!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * check whether downlad this page
	 */
	public boolean checkWhetherDownloadPage(String tURL) {
		if (this.maxpages <= this.downloadPages)
			return false;

		if (this.downloadedPagesArrayList.contains(tURL))
			return false;
		
		//check url type: text/html
		if (!checkPageContentType(tURL)) {
			this.ignorePagesArrayList.add(tURL);
			return false;
		}

		return true;
	}

	/**
	 * download page content
	 */
	public void downloadPage(String pageContent, String tURL) {
		try {
			// check whether the pageContent is null
			if (pageContent == null)
				return;

			this.downloadPages = this.downloadPages + 1;

			// download the current URL
			String filePath = "";
			filePath = "./smart/downloads/"+ Integer.toString(this.downloadPages);
			FileOperation.writeFile(filePath, pageContent, false);
			this.downloadedPagesArrayList.add(tURL);

			// store url and document number into hashmap
			this.urlDocNumHahsMap.put(tURL,Integer.toString(this.downloadPages));
		} catch (Exception e) {
			System.out.println("Exception in crawl_smart.downloadPage URL: "
					+ tURL);
			e.printStackTrace();
		}
	}

	/**
	 * ref: http://www.regexlab.com/zh/regref.htm
	 */
	public boolean urlRegularExpressionChecker(String type, String rule,
			String tURL) {
		String regEx = "";
		// type-1: prefix check
		if (type.equals("1"))
			regEx = "^" + rule + ".*";
		// type-2: suffix check
		else if (type.equals("2"))
			regEx = "^.*" + rule + "$";
		if (tURL.matches(regEx))
			return true;
		else
			return false;
	}

	/**
	 * return the content of page type: 1-whole page content; 2-only the text of
	 * page
	 */
	public String getPageContent(String tURL, String type) {
		try {
			//politeness checking
        	PubFunction.delayTimer(System.currentTimeMillis(),this.lastEndTime,this.delayTime);
        	this.lastEndTime = System.currentTimeMillis();
        	System.out.println("Download Page:"+tURL);
        	//start download page
			String tReturnResult = "";
			Document doc = Jsoup.connect(tURL).timeout(5000).get();
			// get whole document
			if (type.equals("1"))
				tReturnResult = doc.toString();
			// get only the text of page
			else if (type.equals("2"))
				tReturnResult = Jsoup.parse(doc.toString()).text();
			// return result
			return tReturnResult;
		} catch (Exception e) {
			System.out.println("Exception in crawl_smart.getPageContent!!!");
			e.printStackTrace();
			return null;
		}
	}
}
