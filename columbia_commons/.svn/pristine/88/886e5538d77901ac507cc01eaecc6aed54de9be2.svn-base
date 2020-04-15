package edu.columbia.lrl.LWSim.traffic.traces;

import ch.epfl.general_libraries.clazzes.ParamName;
 

public class UclRidwanTraceParser extends AbstractTraceLineParser {
	
	// magnify send time by 100 to avoid event queue overflow
	private double magnifyRatio = 100;

	public UclRidwanTraceParser(
			@ParamName(name="Magnify Time by", default_="100") double magnifyRatio) {
		this.magnifyRatio = magnifyRatio;
	}

	@Override
	public TraceEvent parse(String line) {
		String[] elements = line.split(" ");
		
		TraceEvent ev = new TraceEvent(Integer.parseInt(elements[1]),					//source
										Integer.parseInt(elements[2]),					//dest
										72,												//fixed msg size
										magnifyRatio* Double.parseDouble(elements[0])	//time
										);
		
		return ev;
	}

}
