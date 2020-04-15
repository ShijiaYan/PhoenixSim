package archives.common.physical_layer.loss_models;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.simulation.SimulationException;
import ch.epfl.general_libraries.utils.SimpleMap;

public class Switch2x2FromTable extends AbstractSwitchingRingModel {

	private String fileName;

	public Switch2x2FromTable(
			@ParamName(name = "File name", default_ = "src/edu/columbia/lrl/physical/loss_models/PPStagebyStage.dat") String fileName) {
		this.fileName = fileName;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>();
		map.put("Switch loss file name", fileName);
		return map;
	}

	@Override
	public void getLossThroughILdropILandSwitchPP(PscanResult result, int numberDrop, int numberThrough) {

		// Open crosstalk file
		Scanner xTalkScanner;
		try {
			xTalkScanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			throw new SimulationException(fileName + " could not be opened.");
		}

		// Line index is number of wavelengths, column is number of cascaded switches
		
		double xTalkCoeff = readFromFile(xTalkScanner, numberThrough, gloco.getNumberOfWavelengths());
	
		//Get loss in dB from power penalty coefficient
		double loss_dB = xTalkCoeff;
		
		result.addPowerDissipatedDB(loss_dB, "switch", "Insertion loss and power penalty from switches");		
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
			lineIndex++;
			
			wordScanner.close();
		}
		
		if( !valueSet ) {
			throw new SimulationException("Value not set while reading table. Row:" + row + ", Column:" + column);
		}
		
		return value;
	}
}
