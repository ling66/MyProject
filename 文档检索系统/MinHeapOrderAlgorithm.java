
public class MinHeapOrderAlgorithm {
	//
	public DocScoreNode[] docSet;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MinHeapOrderAlgorithm tMinHeapOrderAlgorithm = new MinHeapOrderAlgorithm();
		// TODO Auto-generated method stub
		DocScoreNode[] tDocSet = new DocScoreNode[2];
		DocScoreNode tDocScoreNode = new DocScoreNode();
		tDocScoreNode.docNumber = 1;
		tDocScoreNode.docScore = 11.840166;
		tDocSet[0] = tDocScoreNode;
		
		tDocScoreNode = new DocScoreNode();
		tDocScoreNode.docNumber = 2;
		tDocScoreNode.docScore = 11.096296;
		tDocSet[1] = tDocScoreNode;
		
//		tDocScoreNode = new DocScoreNode();
//		tDocScoreNode.docNumber = 3;
//		tDocScoreNode.docScore = 15.091325;
//		tDocSet[2] = tDocScoreNode;
		
//		tDocScoreNode = new DocScoreNode();
//		tDocScoreNode.docNumber = 4;
//		tDocScoreNode.docScore = 14.612198;
//		tDocSet[3] = tDocScoreNode;
		
//		tDocScoreNode = new DocScoreNode();
//		tDocScoreNode.docNumber = 5;
//		tDocScoreNode.docScore = 11;
//		tDocSet[4] = tDocScoreNode;
//		
//		tDocScoreNode = new DocScoreNode();
//		tDocScoreNode.docNumber = 6;
//		tDocScoreNode.docScore = 7;
//		tDocSet[5] = tDocScoreNode;
//		
//		tDocScoreNode = new DocScoreNode();
//		tDocScoreNode.docNumber = 7;
//		tDocScoreNode.docScore = 1;
//		tDocSet[6] = tDocScoreNode;
		
		for(int i = 0; i < tDocSet.length ; i++){
			System.out.println(tDocSet[i].docScore);
		}
		System.out.println("---------------");
		tMinHeapOrderAlgorithm.orderingDocCollection(tDocSet);
		for(int i = 0; i < tDocSet.length ; i++){
			System.out.println(tDocSet[i].docScore);
		}
		
		//Add another node into collection
		tDocSet[0].docNumber = 4;
		tDocSet[0].docScore = 14.612198;
		System.out.println("---------------");
		tMinHeapOrderAlgorithm.orderingDocCollection(tDocSet);
		for(int i = 0; i < tDocSet.length ; i++){
			System.out.println(tDocSet[i].docScore);
		}
		
		//Add another node into collection
		tDocSet[0].docNumber = 3;
		tDocSet[0].docScore = 15.091325;
		System.out.println("---------------");
		tMinHeapOrderAlgorithm.orderingDocCollection(tDocSet);
		for(int i = 0; i < tDocSet.length ; i++){
			System.out.println(tDocSet[i].docScore);
		}

	}
	
//	//initial the size of collection for ordering
//	public MinHeapOrderAlgorithm(DocScoreNode[] tDocSet){
//		//initial variables 
//		this.docSet = tDocSet;
//	}
	
	/*
	 * Function: Perform collection ordering operation
	 * Return 1 - successful ; 
	 *        0 - fail;
	 */
	public String orderingDocCollection(DocScoreNode[] curDoc){
		//assign current document collection to ordering document collection
		this.docSet = curDoc;
		//calculate ordering start position
		int curPos = this.docSet.length/2;
		
		//only 1 or 2 document in document collection, just return 
		if (this.docSet.length <= 2) {
			if (this.docSet.length == 2) {
				if (this.docSet[0].docScore > this.docSet[1].docScore)
					this.swapElementInSet(0, 1);
			}
			return "1";
		}
		// more than 2 document in collection
		else{
			while(curPos > 0){
				this.orderingCurDocSet(curPos);
				curPos = curPos - 1; 
			}
		}
		//return ordered document collection
		return "1";
	}
	
	/*
	 * 
	 * */
	public String orderingCurDocSet(int curPos) {
		//check whether there is any element(left or right child) exists at current Node
		if( (2*curPos <= this.docSet.length) || ( (2*curPos+1) <= this.docSet.length)){
			double sValue = 0;
			int sPos = 0;
			//for example: docNum=6; 6/2=3;2*3=6;2*3+1=7(position not exist)
			//             docNum=7; 7/2=3;2*3=6;2*3+1=7(position exist)
			//Note: array start from 0
			if( (2*curPos+1) <= this.docSet.length){
				//compare value between (2*i) and (2*i+1) and set record the smaller value
				if(this.docSet[2*curPos-1].docScore > this.docSet[2*curPos].docScore){
					sValue = this.docSet[2*curPos].docScore;
					sPos = 2*curPos;
				}
				else{
					sValue = this.docSet[2*curPos-1].docScore;
					sPos = 2*curPos-1;
				}
			}
			else{
				sValue = this.docSet[2*curPos-1].docScore;
				sPos = 2*curPos-1;
			}
			//compare the value of curPos with sPos value
			if(this.docSet[curPos-1].docScore > sValue ){
				this.swapElementInSet(curPos-1, sPos);
				this.orderingCurDocSet(sPos+1);
			}
		}
		//
		return "1";
	}
	/**
	 * swap position between two element in collection
	 * @param pos1: current position;
	 * @param pos2: lower position
	 */
	private void swapElementInSet(int pos1 , int pos2){
		//swap the value of these two position
		DocScoreNode tDocScoreNode = new DocScoreNode();
		tDocScoreNode.docNumber = this.docSet[pos1].docNumber;
		tDocScoreNode.docScore = this.docSet[pos1].docScore;
		tDocScoreNode.docDescription = this.docSet[pos1].docDescription;
		
		this.docSet[pos1].docNumber = this.docSet[pos2].docNumber;
		this.docSet[pos1].docScore = this.docSet[pos2].docScore;
		this.docSet[pos1].docDescription = this.docSet[pos2].docDescription;
		
		this.docSet[pos2].docNumber = tDocScoreNode.docNumber;
		this.docSet[pos2].docScore = tDocScoreNode.docScore;
		this.docSet[pos2].docDescription = tDocScoreNode.docDescription;
		
	}
	
}
