package edu.columbia.sebastien.data_center_consumption;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class OperationPerSecondModel {
	
	private double millionsOfServer;
	private double serverBaseGOPS;
	
	public OperationPerSecondModel(
			@ParamName(name="Millions of servers", default_="7.3") double millionsOfServer,
			@ParamName(name="Server strength in GOPS", default_="50") double serverBaseGOPS) {
		
		this.millionsOfServer = millionsOfServer;
		this.serverBaseGOPS = serverBaseGOPS;
	}
	
	public double getBaseTeraOPS() {
		return millionsOfServer*1e6*serverBaseGOPS/1e3d;
	}

	public double getConsumptionTW(double gFlopJoule) {
		double ops = getBaseTeraOPS()*1e12d;
		double flopPerJ = gFlopJoule*1e9d;
		double watts = ops/flopPerJ;
		return watts/1e12d;
	}

	public Map<String, String> getProperties() {
		return SimpleMap.getMap("Millions of servers", millionsOfServer, "Server strenght in GOPS", serverBaseGOPS);
	}

}
