package edu.columbia.lrl.LWSim.application;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.timeline.TimeLineGUI;
import ch.epfl.general_libraries.graphics.timeline.TimeLineSet;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;

import edu.columbia.lrl.LWSim.AbstractTrafficGenerator;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;

public class ApplicationTrafficGenerator extends AbstractTrafficGenerator {
	
	private static final Logger logger = new Logger(ApplicationTrafficGenerator.class);
	
	@SuppressWarnings("serial")
	public class InstanceGenerator extends AbstractTrafficGenerator {
		
		ApplicationTrafficGenerator father;
		ActionManager actionManager;

		@ConstructorDef(ignore=true)
		public InstanceGenerator(int index, ApplicationTrafficGenerator father) {
			this.index = index;
			this.father = father;
		}
		@Override
		public void processEvent(Evt e) {}	
		@Override
		public String toShortString() {
			return "default gen";
		}
		@Override
		public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {
			InitFeedback failure = super.initComponent(lwSimExperiment);
			if (failure != null) return failure;
			TrafficDestination[] dests = new TrafficDestination[generators.size()];
			
			for (int i = 0 ; i < dests.length ; i++) {
				dests[i] = generators.get(index).getTrafficDestination();
			}
			
			actionManager = new ActionManager(father, dests, lwSimExperiment, index);
			lwSimExperiment.getTopologyBuilder().getReceiver(index).setObjectToNotify(actionManager);
			//father.recs[index].setObjectToNotify(c);
			
			Runnable run = new Runnable() {
				public void run() {
					synchronized (father) {
						father.running++;
					}
					try {
						appl.run(actionManager, index, Time.ZERO_TIME.thisTime());
					}
					catch (Throwable e) {
						e.printStackTrace();
					}
					finally {
						synchronized(father) {
							father.running--;
							if (father.running == 0) {
								father.lwSimExperiment.manager.tryToTerminate();
							}
				//			father.notifyAll();
						}
					}
				}
			};
			
			Thread t = new Thread(null, run, "Application runner " + (index+1) + "/" + generators.size(), 30);;
			synchronized(actionManager) {
				try {
					t.start();
					actionManager.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}		
		
		@Override
		public void notifyEnd(double  ref, double status) {}
		@Override
		public int getAveragePacketSize() {
			// return 0 because something must be returned anyway
			return 0;
		}
		/**
		 * This method is to comply with the definition but should never be called
		 */
		@Override
		public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
			throw new IllegalStateException("should never be called");
		}	
		@Override
		public int getNumberOfClients() {
			return appl.getNumberOfClients();
		}	
		/** Application traffic has no need for that */
		@Override
		public void setPossibleDestinationIndexes(int[] dests) {}	
		@Override
		public void setPossibleDestinationIndexes__(Collection<Integer> col) {}
		@Override
		public void setPossibleDestinationIndexesExcludingOne(Collection<Integer> col, int indexToExclude) {}		
	}
	
	private static final long serialVersionUID = 1L;
	private AbstractApplication appl;
	boolean init = false;
	boolean popupTimeLine;
	int running = 0; // number of thread running i.e not having reached the exit statement
	int done = 0; // number of threads awaiting for synchronized coordinated end
	int msgId = 0;
	boolean detailedAnalysis = false;
	boolean orgMult = false;
	
	private HashMap<Integer, InstanceGenerator> generators = new HashMap<Integer, InstanceGenerator>();
	
	public ApplicationTrafficGenerator(@ParamName(name="The application to run") AbstractApplication appl, @ParamName(name="Timeline ?") boolean popupTimeLine) {
		this(appl, popupTimeLine, false);
	}
	
	public ApplicationTrafficGenerator(@ParamName(name="The application to run") AbstractApplication appl, 
					 				   @ParamName(name="Timeline ?") boolean popupTimeLine,
					 				  @ParamName(name="Detailed analysis ?") boolean det) {
		this(appl, popupTimeLine, det, true);
	}
	
	public ApplicationTrafficGenerator(@ParamName(name="The application to run") AbstractApplication appl, 
			   @ParamName(name="Timeline ?") boolean popupTimeLine,
			  @ParamName(name="Detailed analysis ?") boolean det,
			  @ParamName(name="Organized multicast") boolean orgMult) {
		this.appl = appl;
		this.popupTimeLine = popupTimeLine;
		this.detailedAnalysis = det;
		this.orgMult = orgMult;
}	
	
	@Override
	public AbstractTrafficGenerator getCopy(double loadCoeff, int index) {
		if (generators.get(index) != null) {
			throw new IllegalStateException("Cannot create generator : Index " + index + " already used");
		}
		
		InstanceGenerator inst = new InstanceGenerator(index, this);		
		generators.put(index, inst);

		return inst;
	}
	
	public boolean isInitialised() {
		return init;
	}
	
	public boolean wantsOrganizedMulticast() {
		return orgMult;
	}
	
	@Override
	public InitFeedback initTrafficGeneratorTemplateFirstPass(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = super.initComponent(lwSimExperiment);
		if (failure != null) return failure;
		// if problem occur in pass 0, just forgive
		generators.clear();
		appl.reset();
	/*	try {
			failure = internalInit();
		}
		catch (Exception e) {
			return null;
		}*/
		return failure;		
	}
	
	public InitFeedback initTrafficGeneratorTemplateSecondPass(LWSIMExperiment lwSimExperiment) {
		InitFeedback failure = appl.init(lwSimExperiment);	
		if (!appl.isInit()) {
			throw new IllegalStateException("Method init() in rootclass AbstractApplication hasn't been called, likely because implementing classes override init" +
					" without calling super.init(). Check Application object hierarchy");
		}
		return failure;
	}
	
	private Object doneSync = new Object();
	
	public void barrierDone() {
		synchronized (doneSync) {
			done++;
			if (done < running) {
				try {
					doneSync.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				doneSync.notifyAll();
			}
		}
	}
	
	@Override
	public void notifyEnd(double ref, double status) {
		logger.debug("No more events in simulation, setting application threads to terminate at time " + ref);
		for (InstanceGenerator subgen : generators.values()) {
			subgen.actionManager.terminate(ref);
		}
		while (running > 0) {
			synchronized(this) {
				try {
					this.wait(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// transmitting timeline to lwSimExp	
		if (popupTimeLine) {
			for (InstanceGenerator g : generators.values()) {
				lwSimExperiment.addTimeLine(g.actionManager.getTimeLine());
			}
			
			TimeLineSet set = new TimeLineSet();
			for (InstanceGenerator g : generators.values()) {
				set.add(g.actionManager.getTimeLine());
			}
			new TimeLineGUI(set);
		}
		if (status == 0)
		appl.addApplicationInfo(lwSimExperiment, ref, detailedAnalysis);
	}	
		
	public void processEvent(Evt e){}
	
	public String toShortString() {
		return "test app";
	}	
	
	@Override
	public Map<String, String> getAllParameters(LWSIMExperiment lwSimExp) {
		Map<String, String> m = SimpleMap.getMap("traffic_gen", this.getClass().getSimpleName());
		m.putAll(appl.getAllParameters());
		m.put("Application class", appl.getClass().getSimpleName());
		return m;
	}

	@Override
	public int getAveragePacketSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfClients() {
		return appl.getNumberOfClients();
	}
	
	public AbstractApplication getApplication() {
		return appl;
	}
	
	
}
