package edu.columbia.lrl.LWSim.traffic.traces;
 

public class MacroTraceParser extends AbstractTraceLineParser {
	
	public MacroTraceParser() {
	}

	@Override
	public TraceEvent parse(String line) {
		String[] elements = line.split("\t");
		
		TraceEvent ev = new TraceEvent(Integer.parseInt(elements[1]),
										Integer.parseInt(elements[2]),
										Integer.parseInt(elements[3]),
										Double.parseDouble(elements[4]));
		
		return ev;
	}

}
