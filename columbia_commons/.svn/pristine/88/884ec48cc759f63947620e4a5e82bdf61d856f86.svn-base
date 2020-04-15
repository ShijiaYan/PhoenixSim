package edu.columbia.lrl.LWSim.builders;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.traffic.Rate;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.PerDestinationReceivedPacketEndCriterium;
import edu.columbia.lrl.LWSim.analysers.AbstractLWSimAnalyser;
import edu.columbia.lrl.LWSim.analysers.LatencyDistributionAnalyser;
import edu.columbia.lrl.LWSim.components.Buffer;
import edu.columbia.lrl.LWSim.components.Receiver;
import edu.columbia.lrl.LWSim.components.TransmissionLink;
import edu.columbia.lrl.LWSim.traffic.DefaultPoissonTrafficGenerator;
import edu.columbia.lrl.LWSim.traffic.RelativeToReferenceBWLoad;

public class SingleLinkBuilder extends AbstractBandwidthSpecifiedNBClientBuilder {
	
	public static void main(String[] args) {
		SingleLinkBuilder builder = new SingleLinkBuilder(10, true);
		AbstractTrafficGenerator generator = new DefaultPoissonTrafficGenerator(new RelativeToReferenceBWLoad(0.3), 10000);
		NumberOfClientBasedBuilder nbClientsBasedBuilder = new NumberOfClientBasedBuilder(builder, 1, Rate.ONE_GBIT_S.multiply(10));
		
		SmartDataPointCollector db = new SmartDataPointCollector();	
		
		LWSIMExperiment exp = new LWSIMExperiment(nbClientsBasedBuilder, 
												  generator,
												  0, //seed
												  new PerDestinationReceivedPacketEndCriterium(10000),
												  new AbstractLWSimAnalyser[]{new LatencyDistributionAnalyser(true)},
												  false /*timeline*/);
		
		exp.run(db, null);
		
		DefaultResultDisplayingGUI.displayDefault(db);
		
	}
	
	@Override
	public int getMaxPacketSizeInBits() {
		return Integer.MAX_VALUE;
	}	
	
	
	// this variable is used to store the parameter "time" received from the constructor
	private double time;
	private boolean usePriorities;
	private double buffBoot = 0;

	public SingleLinkBuilder(@ParamName(name="Propagation delay of the link in ns") double time, 
							 @ParamName(name="Use priorities ?") boolean usePriorities) {
		// making the received parameter attached to this object
		this.time = time;
		this.usePriorities = usePriorities;
	}
	
	public SingleLinkBuilder(@ParamName(name="Propagation delay of the link in ns") double time, 
			 @ParamName(name="Use priorities ?") boolean usePriorities,
			 @ParamName(name="Buffer bootstrap time (ns)") double buffBoot) {
		// making the received parameter attached to this object
		this.time = time;
		this.usePriorities = usePriorities;
		this.buffBoot = buffBoot;
}	
	
	@Override
	public InitFeedback buildSubBuilder(ArrayList<LWSimComponent> dests, int nbClients) {
		
		if (nbClients > 1)
			return new InitFeedback("Link cannot have more than one client");
		// in this case we just ignore the number of clients
		
		// lwSimExperiment.getLoad() returns the nominal rate multiplied by the load used in the simulation
		AbstractTrafficGenerator generator = lwSimExperiment.getTrafficGenerator().getCopy(1, 0);
		Buffer b = new Buffer(100000, buffBoot, 0, 1, usePriorities); // last parameter is the output rate
		TransmissionLink l = new TransmissionLink(time); // here we use the parameter received
		Receiver r = this.getReceiver(0);
		
		generator.setPossibleDestinationIndexes(new int[]{0});
		
		dests.add(generator);
		dests.add(b);
		dests.add(l);
		dests.add(r);
		
		generator.setTrafficDestination(b);
		b.setTrafficDestination(l);
		l.setTrafficDestination(r);	
		
		return null;
	}
	
	@Override
	public int[][] getNeighborhood(int fromAnode) {
		throw new IllegalStateException("Makes no sense");
	}	
	
	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Propagation delay", time +"",
								"Buffer bootstrap time", buffBoot+"");
	}
	

	@Override
	public double getTotalInjectionBandwidthRatio() {
		return 1;
	}	
	
	@Override	
	public void notifyEnd(double clock, int status) {
	}
}
