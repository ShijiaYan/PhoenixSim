package edu.columbia.lrl.LWSim;

import java.util.Collection;
import java.util.Map;

import javax.swing.JMenuItem;

import edu.columbia.lrl.general.Message;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.Node;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.ui.swing.InteractiveElement;

public abstract class AbstractTrafficGenerator extends AbstractTrafficOrigin implements LWSimComponent, Node, InteractiveElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Required by the node interface. Implementing it permits to include this generator into a javanco node
	public void indexChanged(int i) {}
	public NodeContainer getNodeContainer() {return null;}
	public int getIndex() {return -1;}
	public void setContainer(AbstractElementContainer cont){}
	public AbstractElementContainer getContainer() { return null;}
	// end
	
	// required by the interactiveElement interface. Correspond to null behaviour in the gui
	// sub-class can further override this method for richer gui interactions
	@Override
	public boolean hasContextualMenu() { return false; }
	@Override
	public JMenuItem getElementMenu() { return null;}
	// end
	
	protected int index;
//	
	protected LWSIMExperiment lwSimExperiment;
	protected PRNStream randomStream;
	protected Message messageToUseAsOriginal;
	protected Collection<Integer> possibleDestinationIndexes;
	
	public abstract int getAveragePacketSize();
	public abstract AbstractTrafficGenerator getCopy(double loadCoeff, int index);
	/**
	 * Implementing this method is optional. In some cases, it is interesting to have the number of clients
	 * in the network determined by the traffic generator.
	 * @return
	 */
	public abstract int getNumberOfClients();
	
	protected void setDefaultInCopy(AbstractTrafficGenerator copy) {
		copy.messageToUseAsOriginal = this.messageToUseAsOriginal;
	}
	
	public void setMessageOriginal(Message msg) {
		messageToUseAsOriginal = msg;
	}
	
	public Message getNewMessage(int i, int origin, int dest, double timeEmitted, int size) {
		return messageToUseAsOriginal.getInstance(i, origin, dest, timeEmitted, size);
	}
	
	public void setPossibleDestinationIndexes(int[] dests) {
		this.setPossibleDestinationIndexes__(MoreArrays.asList(dests));
	}

	public void setPossibleDestinationIndexes__(Collection<Integer> col) {
		possibleDestinationIndexes = col;
	}
	
	public void setPossibleDestinationIndexesExcludingOne(Collection<Integer> col, int indexToExclude) {
		col.remove(new Integer(indexToExclude));
		setPossibleDestinationIndexes__(col);
	}
		
	@Override
	public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;		
		this.randomStream = lwSimExperiment.getRandomStreamForTraffic();
		return null;
	}
	
	public InitFeedback initTrafficGeneratorTemplateFirstPass(LWSIMExperiment lwSimExperiment) {
		this.lwSimExperiment = lwSimExperiment;	
		return null;
	}	
	
	public InitFeedback initTrafficGeneratorTemplateSecondPass(LWSIMExperiment lwSimExperiment) {
		return null;
	}	
	
	public Map<String, String> getAllParameters(LWSIMExperiment lwSimExp) {
		return SimpleMap.getMap("traffic_gen", this.getClass().getSimpleName());
	}	
	
	public Collection<Integer> getPossibleDestinations() {
		return possibleDestinationIndexes;
	}
	

}
