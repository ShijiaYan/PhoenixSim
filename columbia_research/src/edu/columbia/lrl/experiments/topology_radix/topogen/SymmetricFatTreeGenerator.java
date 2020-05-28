package edu.columbia.lrl.experiments.topology_radix.topogen;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.experiments.topology_radix.HPCtopologyAnalysis.HPCDesignPoint;
import edu.columbia.lrl.experiments.topology_radix.locality.AbstractNormalisedTrafficVectorGenerator;

public class SymmetricFatTreeGenerator extends AnalysableTopologyGenerator {
	
	private int desiredClients;
	private int radix;

	@Override
	public void setTotalDesiredClientsAndRadix(int desiredClients, int radix) {
		this.desiredClients = desiredClients;
		this.radix = radix;

	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap();
	}

	@Override
	public ArrayList<HPCDesignPoint> getDesignPoints(
			AbstractNormalisedTrafficVectorGenerator localityVec) {

		
		
		if (radix >= desiredClients) {
			Map<String, String> map = SimpleMap.getMap("fat-tree levels", 1+"", "topology_generator_name", "fat tree");
			
			HPCDesignPoint point = new HPCDesignPoint(radix, 1, radix , 1, desiredClients, 1, desiredClients, 1, 1, 1+" levels tree", map);
			
			ArrayList<HPCDesignPoint> l = new ArrayList<>(1);
			
			l.add(point);
			
			return l;
			
		}
		
		
		
		double[] loads = localityVec.getVector(0, desiredClients);
		double load = Math.min(1, MoreArrays.sum(loads));
		
		int clientDown = 0;
		for (int i = radix/2 ; i < radix ; i++) {
			if (i + Math.ceil(load*(double)i) <= radix) {
				clientDown = i;
			}
		}
		int firstLevelSwitches = MoreMaths.ceilDiv(desiredClients, clientDown);
		int supportedClients = firstLevelSwitches*clientDown;
		int firstLevelPorts = firstLevelSwitches*(radix-clientDown);
		int switchesPerLevel = MoreMaths.ceilDiv(firstLevelPorts, radix/2);
		int levels = (int)Math.ceil(MoreMaths.logN(firstLevelPorts, radix/2));
		
		int nbLinks = supportedClients + firstLevelPorts*(levels-1);
		
		int lastLevelSwitches = MoreMaths.ceilDiv(firstLevelPorts, radix);
		
		int totalSwitches = (levels-2)*switchesPerLevel + lastLevelSwitches + firstLevelSwitches;
		
		int index = 0;
		double accum = 0;
		int hops = 0;
		int range = clientDown;
		for (int j = 0 ; j <= levels ; j++) {
			for (int i = index ; i < range ; i++) {
				if (i >= loads.length) break;
				accum += loads[i]* (double)(2*j +1);
				hops += 2*j +1;
			}
			index = range;			
			range = range * radix/2;
		}
		
		double averageTopologicalHops = (double)hops / (double)(loads.length - 1);
		double averageHops = accum / load;
		
		Map<String, String> map = SimpleMap.getMap("fat-tree levels", (levels+1)+"", "topology_generator_name", "fat tree");
		
		HPCDesignPoint point = new HPCDesignPoint(radix, totalSwitches, supportedClients , 1, nbLinks, 1, clientDown, averageHops, averageTopologicalHops, levels+" levels tree", map);
		
		ArrayList<HPCDesignPoint> l = new ArrayList<>(1);
		
		l.add(point);
		
		return l;
		
	}

}
