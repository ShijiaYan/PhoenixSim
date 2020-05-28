package edu.columbia.ke.trace.analyser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import edu.columbia.ke.DataStructure.SrcDstReuseMap;
import edu.columbia.ke.DataStructure.SrcDstStackDistanceRecord;
import edu.columbia.ke.generic.dataStucture.AvlTreeRm;


public class UniqueReuseDistanceAnalyser {
	
	private String prefix = "mpiSend";
	
	protected SrcDstReuseMap sdMap = new SrcDstReuseMap();
	protected int[] srcCount;
	protected int[] dstCount;
	private int nRank;
	private String fileName;
	
	private BufferedReader br = null;
	private String mode;

	public UniqueReuseDistanceAnalyser(String fileName, int nRank, String mode) {
		this.nRank = nRank;
		this.fileName = fileName;
		this.mode = mode;
		this.srcCount = new int[nRank];
		this.dstCount = new int[nRank];
		
		try {
 			br = new BufferedReader(new FileReader(fileName)); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void analyse() throws IOException{
		String sCurrentLine;
		while ((sCurrentLine = br.readLine()) != null) {
			System.out.println(sCurrentLine);
			getSplits(sCurrentLine);
		}
		br.close();
	}
	
	int lastSrc=-1;
	int lastDst=-1;
	
	private void getSplits(String s) {
	    ArrayList<Integer> intArr = new ArrayList<>();
	    int count = 0;
	    String stime = "";
	    for(String str : s.split("\t")){
	    	if (str.equals(prefix))
	    		continue;
	    	if (count == 3) {
	    		stime = str;
	    		break;
	    	}
	        intArr.add(Integer.parseInt(str));
	        count++;
	    }
	    int src = intArr.get(0);
	    int dst = intArr.get(1);
	    int size = intArr.get(2);
	    double time = Double.parseDouble(stime);
	    
	   // if (mode.equals("UNIQUE") && !(lastSrc == src && lastDst == dst)){
	    	srcCount[src]++;
	    	dstCount[dst]++;
	    //}
	    lastSrc = src;
	    lastDst = dst;
	    
	    String key = src + "\t" + dst;
	    SrcDstStackDistanceRecord rc;
	    
	    if(!sdMap.containsKey(key)) {
	    	rc = new SrcDstStackDistanceRecord();
	    	this.sdMap.put(key, rc);
	    } else {
	    	rc = this.sdMap.get(key);
	    }
	    
	    /*
	     * added for obtaining unique distance
	     */
	    int lastSeen = rc.lastSeenIndexInSrc;
	    int d = findUniqueDistance( lastSeen);
	    
	    // insert this use into the tree
	 	avlTree.insert(srcCount[src]);
	 	
	    rc.addSrcUseInstance(size, time, srcCount[src], d);

	}
	
	private AvlTreeRm avlTree = new AvlTreeRm();
	
	
	/*
	 * return how many uses after lastSeen
	 * return -1 if first time seeing this dest
	 */
	private int findUniqueDistance(int lastSeen) {
		if (lastSeen < 0)
			return -1;		// no reuse distance yet

		int rv = avlTree.removeAndRank(lastSeen);
		if (rv < 0)
			throw new IllegalStateException("cannot find key!");
		return rv;
	}
	
	public boolean dumpReuseDistance(int src, int dst, String type, int reuseThreshold) throws FileNotFoundException, UnsupportedEncodingException{
		String key = src + "\t" + dst;
		if(!sdMap.containsKey(key)) {
	    	return false;
	    } 
		
		PrintWriter writer = null;
		String path = "./matlab/"+this.fileName+"/"+mode+"/"+type+"/";
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
	
	private enum DumpType {
	    SRC_RD, DST_RD, TIME_RD;
	}

}
