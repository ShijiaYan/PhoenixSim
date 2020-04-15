package edu.columbia.lrl.LWSim.traffic.traces;

import ch.epfl.general_libraries.clazzes.ParamName;
 

public class UclAnoukTraceParser extends AbstractTraceLineParser {
	
	// magnify send time by 100 to avoid event queue overflow
	private double magnifyRatio = 100;
	private int nNode;

	public UclAnoukTraceParser(
			@ParamName(name="Magnify Time by", default_="100") double magnifyRatio,
			@ParamName(name="nNode", default_="16") int nNode) {
		this.magnifyRatio = magnifyRatio;
		this.nNode = nNode;
	}

	@Override
	public TraceEvent parse(String line) {
		String[] elements = line.split(";");
		
		TraceEvent ev = new TraceEvent(Integer.parseInt(elements[1]) % nNode,			//source
										Integer.parseInt(elements[2]) % nNode,			//dest
										parseSize(elements[3]),							//size
										magnifyRatio* Double.parseDouble(elements[0])	//time
										);
		
		return ev;
	}

	private int parseSize(String type) {
		if (type.equals("REQ_D"))
			return 72;
		else 
			return 8;
	}

}
