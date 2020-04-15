package edu.columbia.lrl.LWSim.traffic;


import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.epfl.general_libraries.traffic.Rate;
import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.traffic.deadlines.AbstractTTLGenerator;
import edu.columbia.lrl.LWSim.traffic.deadlines.NoTTLRequirement;
import edu.columbia.lrl.LWSim.traffic.sizes.AbstractPacketSizeGenerator;
import edu.columbia.lrl.LWSim.traffic.sizes.ConstantSizeGenerator;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public abstract class AbstractRandomTrafficGenerator extends AbstractTrafficGenerator {

	/**
	 * 
	 */
	protected Rate r;
	private static final long serialVersionUID = 1L;
	protected int messageIndex = 0;
	protected AbstractTTLGenerator ttlGen;
	protected AbstractPacketSizeGenerator sizeGen;
	protected AbstractLoadScheme loadScheme;
	protected double loadFactor = -1;

	
	public AbstractTTLGenerator getTTLGen() {
		return ttlGen;
	}
	
	protected int getDestination() {
		if (possibleDestinationIndexes == null)
			throw new NullPointerException("Possible destinations has not been set for this traffic generator");
		return randomStream.pickIn(possibleDestinationIndexes);		
	}
	
	protected abstract double getInterTime();
	protected abstract void updateRate();
	
	public AbstractRandomTrafficGenerator(AbstractLoadScheme scheme, int size) {
		this(scheme, null, new ConstantSizeGenerator(size));
	}
	
	public AbstractRandomTrafficGenerator(AbstractLoadScheme scheme, AbstractPacketSizeGenerator sizeGen) {
		this(scheme, null, sizeGen);
	}
	
	public AbstractRandomTrafficGenerator(AbstractLoadScheme scheme, AbstractTTLGenerator ttlGen, AbstractPacketSizeGenerator sizeGen) {
		if (ttlGen == null) {
			this.ttlGen = new NoTTLRequirement();
		} else {
			this.ttlGen = ttlGen;
		}
		this.sizeGen = sizeGen;
		this.loadScheme = scheme;
	}
	
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = super.initComponent(lwSimExperiment);
		if (failure != null) return failure;
		if (r == null) {
			r = loadScheme.getLoadPerClient(lwSimExperiment).multiply(loadFactor);
			updateRate();
	
			Evt traf = new Evt(addInterTime(0d, 0), this, this);
			lwSimExperiment.manager.queueEvent(traf);
		}
		return null;
	}	
	
	protected void setDefaultInCopy(double loadCoeff, AbstractRandomTrafficGenerator copy) {
		super.setDefaultInCopy(copy);
		copy.loadFactor = loadCoeff;
	}	
	
	public void processEvent(Evt e) {
		// getting a new size of the packet
		int size = sizeGen.getSize(randomStream);
		
		
		// creating an event for the next packet
		Evt traf = new Evt(addInterTime(e.getTimeNS(), size), this, this);
		lwSimExperiment.manager.queueEvent(traf);
		// Creating an event for the next element
		Evt next = new Evt(e.getTimeNS(), this, nextDest);
		Message m = getNewMessage(messageIndex++, index, getDestination(), e.getTimeNS(), size);
		
		// add Time to live information
		// if initTTL >= 0, deadline feature enabled
		// else, deadline feature disabled
		m.setInitTimeToLive(ttlGen.nextTTL());		
				
	/*	if (m.index == 0) {
			System.out.println("Generator: " + index + " pkt" + m.index + " sent at " + e.getTimeNS() + " (dest:" + m.dest + ")");
		}*/
		next.setMessage(m);
		lwSimExperiment.packetEmitted(m);
		lwSimExperiment.manager.queueEvent(next);

	}
	
	public String toString() {
		return "G"+index;
	}
	
	@Override
	public int getAveragePacketSize() {
		return sizeGen.getAverageSize();
	}
	
	protected double addInterTime(double ns, int size) {
		return ns + getInterTime();	
	}
	
	public boolean hasContextualMenu() {return true;}
	
	public JMenuItem getElementMenu() {
		JMenu menu = new JMenu("Traffic intensity");
		
		JMenu load = new JMenu("Load");
		final JLabel loadValue = new JLabel("Load : " + r);
		final JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, 320, (int)r.getInGbitSeconds());
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				r = Rate.ONE_GBIT_S.multiply(slider.getValue());
				updateRate();
				loadValue.setText("Load : " + r);
			}	
		});
		load.add(loadValue);
		load.add(slider);		
		menu.add(load);		
		return menu;		
	}
	
	public String toShortString() {
		return "traffic gen";
	}
	
	@Override
	public Map<String, String> getAllParameters(LWSIMExperiment lwSimExp) {
		Map<String, String> map = super.getAllParameters(lwSimExp);
		map.putAll(this.ttlGen.getAllParameters());
		map.putAll(this.sizeGen.getAllParameters());
		map.putAll(this.loadScheme.getAllParameters(lwSimExp));
		return map;
	}	
}
