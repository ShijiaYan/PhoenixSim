package edu.columbia.lrl.CrossLayer.physical_models.devices.switches;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.simulation.SimulationException;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.CrossLayer.physical_models.PowerPenalty;
import edu.columbia.lrl.CrossLayer.physical_models.util.AbstractLinkFormat;


public class Switch2x2FromTable extends Abstract2x2Switch {

	String filename;

	private String fileName;

	public Switch2x2FromTable(
			@ParamName(name = "Switch ring driving power (mW)", default_ = DEFAULT_DRIVING_POWER_MW) double switchRingDrivingPower,
			@ParamName(name = "File name", default_ = "src/edu/columbia/lrl/CrossLayer/physical_models/devices/PPStagebyStage.dat") String fileName) {
		super(switchRingDrivingPower);
		this.fileName = fileName;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>();
		map.put("Switch loss file name", fileName);
		return map;
	}
	
	@Override
	public double getSize() {
		return 0.000002;
	}	

	public PowerPenalty getPowerPenalty(AbstractLinkFormat linkFormat, int numStages) {

		// Open crosstalk file
		Scanner xTalkScanner = null;
		try {
		//	File file = JavancoFile.findFile(fileName);
			xTalkScanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			throw new SimulationException(fileName + " could not be opened.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Line index is number of wavelengths, column is number of cascaded switches
		
		double value = readFromFile(xTalkScanner, numStages, linkFormat.getNumberOfChannels());
	
		return new PowerPenalty(_2x2Switch,"switch", value);		
	}
	
	public double readFromFile(Scanner fileScanner, int row, int column) {
		int lineIndex = 1;
		double value = 0;
		boolean valueSet = false;
		
		while (fileScanner.hasNext() && lineIndex <= row ) {
			Scanner wordScanner = new Scanner(fileScanner.nextLine());
			
			if (lineIndex == row) {
				// Find correct entry
				int wordIndex = 1;
				while (wordScanner.hasNext() && wordIndex <= column ) {
					String entry = wordScanner.next();
					if( wordIndex == column ) {
						//remove any brackets from entry
						if( entry.contains("{") ) {
							int start = 0;
						
							for( ; entry.charAt(start) == '{' && start < entry.length(); start++);
							int end = entry.length()-1;
							for( ; entry.charAt(end) == '}' && end >= 0; end--);
						
							entry = entry.substring(start, end+1);						
						}
						try {
							value = Double.parseDouble(entry);
							valueSet = true;
						} catch (NumberFormatException e){
							wordScanner.close();
							throw new SimulationException("Error parsing value \"" + entry + "\" in switch file table");
						}
					}
					
					wordIndex++;
				}
			}
			wordScanner.close();
			lineIndex++;
		}
		
		if( !valueSet ) {
			throw new SimulationException("Value not set while reading table. Row:" + row + ", Column:" + column);
		}
		
		return value;
	}

}
