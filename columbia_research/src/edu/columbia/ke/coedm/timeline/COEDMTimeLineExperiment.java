package edu.columbia.ke.coedm.timeline;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.graphics.timeline.TimeLineGUI;
import ch.epfl.general_libraries.graphics.timeline.TimeLineSet;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;

public class COEDMTimeLineExperiment implements Experiment {
	
	private String fileName;
	
	public COEDMTimeLineExperiment(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		TimeLine tl = new TimeLine();
		
		try {
			readReads(tl);
			readWrites(tl);
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		
		TimeLineSet tset = new TimeLineSet();
		tset.add(tl);
		TimeLineGUI gui = new TimeLineGUI(tset);
	}
	
	private void readReads(TimeLine tl) throws IOException {
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		long lastStart = 0;
		boolean isLastStart = false;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("DMAC: read")) {
				String[] parts = line.split("@");
				long time = 0;
				if (parts[1].endsWith("ps")) {
					String number = parts[1].replaceAll(" ps", "");
					time = Long.parseLong(number);
				} else if (parts[1].endsWith("ns")) {
					String number = parts[1].replaceAll(" ns", "");
					time = (long) (1e3*Long.parseLong(number));
				} else if (parts[1].endsWith("us")) {
					String number = parts[1].replaceAll(" us", "");
					time = (long) (1e6*Long.parseLong(number));
				} else {
					throw new IllegalStateException("unknown time unit" + parts[1]);
				}
					
				if (parts[0].endsWith("start ")) {
					lastStart = time;
					isLastStart = true;
				} else {
					if (isLastStart != true) {
						throw new IllegalStateException("seems to be a problem");
					}
					isLastStart = false;
					System.out.println(lastStart/1000 + " - " + time/1000);
					tl.addJobPhase(lastStart, time, "read", Color.GREEN);
				}
			}
		}
		br.close();
	}
	
	private void readWrites(TimeLine tl) throws IOException {
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		long lastStart = 0;
		boolean isLastStart = false;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("DMAC: write")) {
				String[] parts = line.split("@");
				long time = 0;
				if (parts[1].endsWith("ps")) {
					String number = parts[1].replaceAll(" ps", "");
					time = Long.parseLong(number);
				} else if (parts[1].endsWith("ns")) {
					String number = parts[1].replaceAll(" ns", "");
					time = (long) (1e3*Long.parseLong(number));
				} else if (parts[1].endsWith("us")) {
					String number = parts[1].replaceAll(" us", "");
					time = (long) (1e6*Long.parseLong(number));
				} else {
					throw new IllegalStateException("unknown time unit" + parts[1]);
				}
				
				if (parts[0].endsWith("start ")) {
					lastStart = time;
					isLastStart = true;
				} else {
					if (isLastStart != true) {
						throw new IllegalStateException("seems to be a problem");
					}
					isLastStart = false;
					System.out.println(lastStart/1000 + " - " + time/1000);
					tl.addJobPhase(lastStart, time, "read", Color.RED);
				}
			}
		}
		br.close();
	}	

}
