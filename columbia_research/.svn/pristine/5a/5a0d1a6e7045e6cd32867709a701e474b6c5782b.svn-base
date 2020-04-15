package edu.columbia.ke.trace.analyser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.columbia.ke.DataStructure.SrcDstReuseMap;
import edu.columbia.ke.DataStructure.SrcDstStackDistanceRecord;


public class StackReuseDistanceAnalyser {
	
	private String prefix = "mpiSend";
	private String splitSymbol = "\t";
	
	protected SrcDstReuseMap sdMap = new SrcDstReuseMap();
	protected int srcCount[];
	protected int dstCount[];
	private int nRank;
	private String fileName;
	
	// private String inBatchPath = "C:/UCL/3rd_batch_Bidirectional_transaction_based/";
	// private String outBatchPath = "../circuit_reuse_matlab/3rd_UCL_batch_direction_insensitive/";
	
	// private String inBatchPath = "../circuit_reuse/DesignForward/";
	// private String outBatchPath = "../circuit_reuse/DesignForward/profile-results/";
	
	//private String inBatchPath = "./data/traces/";
	//private String outBatchPath = "../circuit_reuse/";
	
	private String inBatchPath = "K:/trace-hub/ExaCT/";
	private String outBatchPath = "../circuit_reuse/ExaCT/profile-results/";
	
	private boolean bidirectional = false;
	
	private BufferedReader br = null;
	private String mode;
	private int thresholdSize = -1;
	private boolean thresholdMode = false;

	public StackReuseDistanceAnalyser(String fileName, int nRank, String mode) {
		this.nRank = nRank;
		this.fileName = fileName;
		this.mode = mode;
		this.srcCount = new int[nRank];
		this.dstCount = new int[nRank];
		
		if (mode.equals("THRESHOLD")) {
			thresholdSize = 72;		//default for UCL traces (1 cache line about 64 Byte)
			thresholdMode = true;
		}
		
		String filePath = inBatchPath + fileName /*+ ".txt"*/;
		
		try {
 			br = new BufferedReader(new FileReader(filePath)); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public StackReuseDistanceAnalyser(String fileName, int nRank, String mode, int thresholdSize) {
		this( fileName,  nRank,  mode);
		this.thresholdSize = thresholdSize;
	}
	
	public void analyse() throws IOException{
		String sCurrentLine;
		while ((sCurrentLine = br.readLine()) != null) {
			//System.out.println(sCurrentLine);
			getSplits(sCurrentLine);
		}
		br.close();
	}
	
	int lastSrc=-1;
	int lastDst=-1;
	
	private void getSplits(String s) {
	    ArrayList<Integer> intArr = new ArrayList<Integer>();
	    int count = 0;
	    String stime = "";
	    for(String str : s.split(splitSymbol)){
	    	if (str.equals(prefix))
	    		continue;
	    	if (count == 3) {		// count = 0 for UCL traces
	    		stime = str;
	    		count++;
	    		continue;
	    	}
	        intArr.add(Integer.parseInt(str));
	        count++;
	    }
	    int src = intArr.get(0);
	    int dst = intArr.get(1);
	    int size = intArr.get(2);			// size = 72 for UCL traces
	    double time = Double.parseDouble(stime);
	    
	    if (src >= nRank || dst >= nRank || src < 0 || dst <0)
	    	return;
	    
	    if (thresholdMode && size < thresholdSize)
	    	return;
	    
	    //if (mode.equals("STACK") && !(lastSrc == src && lastDst == dst)){		//depreciated
	    	srcCount[src]++;
	    	dstCount[dst]++;
	    //}
	    lastSrc = src;
	    lastDst = dst;
	    
	    String key;
	    // if using bidirectional circuits, set bidirectional to true
	    if (bidirectional && src > dst) 
	    	key = dst + "\t" + src;
	    else
	    	key = src + "\t" + dst;
	    
	    SrcDstStackDistanceRecord rc;
	    
	    if(!sdMap.containsKey(key)) {
	    	rc = new SrcDstStackDistanceRecord();
	    	this.sdMap.put(key, rc);
	    } else {
	    	rc = this.sdMap.get(key);
	    }
	    
	    /*
	     * use one of the two methods below
	     */
	    
	    // 1st: dump out all reuse distance samples to files
	    rc.addUseInstance(size, time, srcCount[src], dstCount[dst]);
	    
	    // 2nd: dump out <RD, count> maps
	    // rc.addUseInstanceHashed(size, time, srcCount[src], dstCount[dst]);

	}
	
	public boolean dumpReuseDistance(int src, int dst, String type, int reuseThreshold) throws FileNotFoundException, UnsupportedEncodingException{
		String key = src + "\t" + dst;
		if(!sdMap.containsKey(key)) {
	    	return false;
	    } 
		
		PrintWriter writer = null;
		String path1 = this.outBatchPath + this.fileName + "/" + mode + "/";
		String path = path1 +type+"/";
		String writeTo = path+src+"_"+dst+".txt";
		SrcDstStackDistanceRecord rc = sdMap.get(key);
		DumpType dt = DumpType.valueOf(type); // surround with try/catch

		switch(dt) {
		    case SRC_RD:
		    	if (rc.srcRDList.size() > reuseThreshold) {
		    		printArray(rc.srcRDList, writeTo);
		    	}
		        break;
		    case DST_RD:
		    	if (rc.dstRDList.size() > reuseThreshold){
		    		printArray(rc.dstRDList, writeTo);
		    	}
		        break;
		    case TIME_RD:
		    	if (rc.RDinTimeList.size() > reuseThreshold){
		    		printArray(rc.RDinTimeList, writeTo);
		    	}
		    	break;
		    case ALL:
				writeTo = path1 + "SRC_RD" +"/" +src+"_"+dst+".txt";
				if (rc.srcRDList.size() > reuseThreshold) {
		    		printArray(rc.srcRDList, writeTo);
		    	}
				
				writeTo = path1 + "DST_RD" +"/" +src+"_"+dst+".txt";
				if (rc.dstRDList.size() > reuseThreshold){
		    		printArray(rc.dstRDList, writeTo);
		    	}
				
				writeTo = path1 + "TIME_RD" +"/" +src+"_"+dst+".txt";
				if (rc.RDinTimeList.size() > reuseThreshold){
		    		printArray(rc.RDinTimeList, writeTo);
		    	}
				break;
		    default:
		    	throw new IllegalStateException("Wrong dump type!");
		}			
		return true;
	}
	
	public boolean dumpReuseDistanceHashed(int src, int dst, String type, int reuseThreshold) throws FileNotFoundException, UnsupportedEncodingException{
		String key = src + "\t" + dst;
		if(!sdMap.containsKey(key)) {
	    	return false;
	    } 
		
		PrintWriter writer = null;
		String path1 = this.outBatchPath + this.fileName + "/" + mode + "/";
		String path = path1 +type+"/";
		String writeTo = path+src+"_"+dst+".txt";
		SrcDstStackDistanceRecord rc = sdMap.get(key);
		DumpType dt = DumpType.valueOf(type); // surround with try/catch

		switch (dt) {
		case SRC_RD:
			printTable(rc.srcMap, rc.maxConsideredKey, writeTo);
			break;
		case DST_RD:
			printTable(rc.dstMap, rc.maxConsideredKey, writeTo);
			break;
		case TIME_RD:
			printTable(rc.timeMap, rc.maxConsideredKey, writeTo);
			break;
		case ALL:
			writeTo = path1 + "SRC_RD" +"/" +src+"_"+dst+".txt";
			printTable(rc.srcMap, rc.maxConsideredKey, writeTo);
			
			writeTo = path1 + "DST_RD" +"/" +src+"_"+dst+".txt";
			printTable(rc.dstMap, rc.maxConsideredKey, writeTo);
			
			writeTo = path1 + "TIME_RD" +"/" +src+"_"+dst+".txt";
			printTable(rc.timeMap, rc.maxConsideredKey, writeTo);
			break;
		default:
	    	throw new IllegalStateException("Wrong dump type!");
			
		}
		return true;
	}
	
	private void printArray(ArrayList l, String writeTo) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(writeTo, "UTF-8");
		String formatedString = l.toString()
                .replace(",", "")  //remove the commas
                .replace("[", "")   //remove the right bracket
                .replace("]", ""); //remove the left bracket
		writer.println(formatedString);
		writer.close();		
	}
	
	private void printTable(HashMap<Integer, Integer> m, int maxKey, String writeTo) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(writeTo, "UTF-8");
		int value;
		for (int i = 0; i <= maxKey; i++ ){
			if (m.containsKey(i))
				value = m.get(i);
			else 
				value = 0;
			writer.println(value);
		}
		writer.close();		
	}
	
	private enum DumpType {
	    SRC_RD, DST_RD, TIME_RD, ALL;
	}

}
