package edu.columbia.lrl.LWSim.application;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.graphics.timeline.TimeLine;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.MoreCollections;
import edu.columbia.lrl.LWSim.EventOrigin;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.LWSimComponent;
import edu.columbia.lrl.LWSim.TrafficDestination;
import edu.columbia.lrl.general.Evt;
import edu.columbia.lrl.general.Message;

public class ActionManager implements Notifiable, LWSimComponent, EventOrigin  {
	
	private static final LWSimComponent dummyEventTarget = new LWSimComponent() {
		@Override public String toShortString() { return null;}
		@Override public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {return null;}
		@Override public void processEvent(Evt e) {}
		@Override public void notifyEnd(double  ref, double status) {}
	};
	private static final ArrayList<Integer> EMPTY_LIST = new ArrayList<Integer>(0);	
//	private static Logger logger = new Logger(ActionManager.class);
	// must be static to allow desambiguation on the timeline
	public static int msgId = 0;
	private int collectiveId = 0;
	
	private TrafficDestination[] dests;
	private int rank;
	private LWSIMExperiment sim;
	private ApplicationTrafficGenerator associatedGeneratorObject;

	private LinkedList<Transmission> receptionQueue = new LinkedList<Transmission>();
	
	private Time localTime = Time.ZERO_TIME.thisTime();
	
	private int waitingFrom = -1;
	private boolean waitingFromAny = false;
	private boolean waitingForTimeout = false;
	private TimeLine timeline;
	
	private int blockingReadCounter = 0;
	
	private boolean aboutToDie = false;
	private boolean dead = false;
	private String error = null;

//	private boolean readWithExpiry = false;
	
	public ActionManager(ApplicationTrafficGenerator associatedGeneratorObject, TrafficDestination[] dests, LWSIMExperiment sim,  int rank) {
		this.dests = dests;
		this.rank = rank;
		this.sim = sim;
		this.associatedGeneratorObject = associatedGeneratorObject;
		if (associatedGeneratorObject.popupTimeLine)
			this.timeline = new TimeLine(rank, "Rank " + rank);
	}
	
	/**
	 * This method is used by the GUI to retrieve the thread activity over time
	 * @return a timeline object
	 */
	TimeLine getTimeLine() {
		return timeline;
	}
	
	/**
	 * Returns the number of threads involved in the collective task
	 * @return returns the number of threads involved in the collective task
	 */
	public int getParticipantNumber() {
		return dests.length;
	}
	
	public PRNStream getExperimentStream() {
		return sim.getRandomStreamForEverythingButTraffic();
	}
	
	public LWSIMExperiment getExperiment() {
		return sim;
	}
	
	// do nothing, exp already given in the constructor
	@Override public InitFeedback initComponent(LWSIMExperiment lwSimExperiment) {return null;}
	
	/**
	 * Can be call by skeleton thread to notify that it is running a computational task for a given duration
	 * @param ref The "actual time" object
	 * @param nanoseconds The job duration in nanoseconds
	 */
	public void doSomeJob(Time ref, double nanoseconds) {
		this.doSomeJob(ref,  nanoseconds, "");
	}

	/**
	 * Can be call by skeleton thread to notify that it is running a computational task for a given duration
	 * @param ref The "actual time" object
	 * @param nanoseconds The job duration in nanoseconds
	 * @param desc A String describing the job
	 */	
	public void doSomeJob(Time ref, double nanoseconds, String desc) {
		this.doSomeJob(ref, nanoseconds, desc, TimeLine.EnumType.COMPUTE);
	}
	
	/**
	 * Can be call by skeleton thread to notify that it is running a computational task for a given duration
	 * @param ref The "actual time" object
	 * @param nanoseconds The job duration in nanoseconds
	 * @param desc A String describing the job
	 */	
	public void doSomeJob(Time ref, double nanoseconds, String desc, Color color) {
		this.doSomeJob(ref, nanoseconds, desc, new TimeLine.CustomType(color));
	}	
	
	/**
	 * Can be call by skeleton thread to notify that it is running a computational task for a given duration
	 * @param ref The "actual time" object
	 * @param nanoseconds The job duration in nanoseconds
	 * @param desc A String describing the job
	 */	
	public void doSomeJob(Time ref, double nanoseconds, String desc, TimeLine.Type type) {
		double start = ref.getNanoseconds();
		if (associatedGeneratorObject.popupTimeLine)
			timeline.addJobPhase(start, start+nanoseconds, type, desc);
		ref.setNanoseconds(start+nanoseconds);
		associatedGeneratorObject.getApplication().executeSomething(this.rank, nanoseconds);
	}
	
	public void idle(Time ref, double nanoseconds) {
		double start = ref.getNanoseconds();
		if (associatedGeneratorObject.popupTimeLine)
			timeline.addJobPhase(start, start+nanoseconds, TimeLine.EnumType.IDLE, "idle");
		ref.setNanoseconds(start+nanoseconds);
	}
	
	public int getRank() {
		return rank;
	}
	
	public Object getConcerningEvents() {
		return sim.manager.getEventsRelatedTo(this);
	}
	
	/**
	 * Can be call by skeleton thread to broadcast an object to all other threads excluding itself
	 * @param ref The "actual time" object
	 * @param object Any object to send
	 * @param sizeOfO The size of the object in bytes
	 */
	public void broadcastButMe(Time ref, Object object, int sizeOfO) {
		broadcastButMe(ref, object, sizeOfO, -1);
	}
	
	public void broadcastButMe(Time ref, Object object, int sizeOfO, int operationNumber) {
		boolean orgMult = associatedGeneratorObject.wantsOrganizedMulticast();
		if (aboutToDie == true) return;
		for (int i = 0 ; i < dests.length ; i++) {
			if (i != rank) {
				send(ref, object, sizeOfO, i,null,0,operationNumber, false);
				if (orgMult) {
					ref.plusSameNS(0.1);
				}
			}
		}
	}
	
	public void send(Time ref, int bytesToSend, int destIndex) {
		send(ref, null, bytesToSend, destIndex, null, 0, -1, false);
		
	}	
	
	public void send_selfok(Time ref, Object o, int bytesToSend, int destIndex) {
		send(ref, o, bytesToSend, destIndex, null, 0, -1, true);
			
	}	
	
	/**
	 * Can be call by skeleton thread to send an object to another thread
	 * @param ref The "actual time" object
	 * @param object Any object to send
	 * @param sizeOfO The size of the object in bytes
	 * @param destIndex The id of the destination thread
	 */	
	public void send(Time ref, Object object, int sizeOfO, int destIndex) {	
		send(ref, object, sizeOfO, destIndex, null, 0, -1, false);
	}
	
	/**
	 * Can be call by skeleton thread to send an object to another thread
	 * @param ref The "actual time" object
	 * @param object Any object to send
	 * @param sizeOfO The size of the object in bytes
	 * @param destIndex The id of the destination thread
	 */	
	public void send(Time ref, Object object, int sizeOfO, int destIndex, int priority) {	
		send(ref, object, sizeOfO, destIndex, null, priority, -1, false);
	}
	
	/**
	 * Can be call by skeleton thread to send an object to another thread
	 * @param ref The "actual time" object
	 * @param object Any object to send
	 * @param sizeOfO The size of the object in bytes
	 * @param destIndex The id of the destination thread
	 */	
	public void send(Time ref, Object object, int sizeOfO, int destIndex, String desc) {	
		send(ref, object, sizeOfO, destIndex, desc, 0, -1, false);
	}
	
	/**
	 * Can be call by skeleton thread to send an object to another thread
	 * @param ref The "actual time" object
	 * @param object Any object to send
	 * @param sizeOfO The size of the object in bytes
	 * @param destIndex The id of the destination thread
	 */	
	public void send(Time ref, 
			Object object, 
			int sizeOfO, 
			int destIndex, 
			String desc, 
			int applicationPriority, 
			int operationNumber, 
			boolean selfOk) {
		if (destIndex == rank) {
			if (!selfOk) {
				throw new IllegalStateException("Cannot send to itself");
			} else {
				Transmission r = new Transmission(object, rank, ref.getNanoseconds(), -1);
				receptionQueue.addLast(r);
			}
		}
		if (aboutToDie == true) return;
		int maxBits = this.sim.getTopologyBuilder().getMaxPacketSizeInBits();
		int remaining = sizeOfO;
		int msID = msgId++;
		int partialIndex = 0;
		synchronized(sim.manager) {
			do {
				
				int size = Math.min(maxBits, remaining);
				remaining -= size;
				Transmission trans = new Transmission(object, rank, ref.getNanoseconds(), operationNumber);
				Evt e = new Evt(ref.getNanoseconds(), associatedGeneratorObject, dests[destIndex]);
				Message m = associatedGeneratorObject.getNewMessage(msID, rank, destIndex, ref.getNanoseconds(), size);
				m.applicationPriority = applicationPriority;
				e.setMessage(m);
				m.partialIndex = partialIndex;
				if (remaining > 0 && sizeOfO != 0) {				
					m.partialData = true;
					m.lastPartial = false;
					partialIndex++;
				} else {				
					m.partialData = (partialIndex > 0);
					m.lastPartial = (partialIndex > 0);
					m.totalSize = sizeOfO;
					e.getMessage().carriedData = trans;
					if (associatedGeneratorObject.popupTimeLine)
						timeline.addSend(ref.getNanoseconds(), m.index, m.sizeInBits, desc);
					
					// added for dumping AMR trace
					//sim.logRankComm(rank, destIndex, sizeOfO, ref);
				}
			//	System.out.println("(" + rank + ")" + " sending to " + destIndex + " at time " + ref + "                index " + m.index);
	
				sim.manager.queueEvent(e);
				sim.packetEmitted(m);			
			}
			while (remaining > 0);
		}
		
	}	

	public void coalesceSend(Time ref, Object object, int sizeOfO, int destIndex, String desc) {	
		coalesceSend(ref, object, sizeOfO, destIndex, desc, 0);
	}
	
	private int lastDest = -1;
	private int accSize = 0;
	private double lastTime;
	private Object lastObj;
	private int lastAppPrior;
	
	/**
	 * Can be call by skeleton thread to send an object to another thread
	 * @param ref The "actual time" object
	 * @param object Any object to send
	 * @param sizeOfO The size of the object in bytes
	 * @param destIndex The id of the destination thread
	 */	
	public void coalesceSend(Time ref, Object object, int sizeOfO, int destIndex, String desc, int applicationPriority) {
		if (destIndex == rank) throw new IllegalStateException("Cannot send to itself");
		if (aboutToDie == true) return;
		double now = ref.getNanoseconds();
		if (lastDest < 0){
			lastDest = destIndex;
			accSize = sizeOfO;
			lastTime = now;
			lastObj = object;
			lastAppPrior = applicationPriority;
			return;
		}
		if (lastTime == now && destIndex == lastDest) {
			accSize += sizeOfO;
			lastObj = object;
			return;
		} 
		
			int msID = msgId++;
			int partialIndex = 0;
			Evt e = new Evt(lastTime, associatedGeneratorObject, dests[lastDest]);
			Message m = associatedGeneratorObject.getNewMessage(msID, rank, lastDest, lastTime, accSize);
			m.applicationPriority = lastAppPrior;
			e.setMessage(m);
			m.partialIndex = partialIndex;
			
			m.partialData = (partialIndex > 0);
			m.lastPartial = (partialIndex > 0);
			m.totalSize = accSize;
			e.getMessage().carriedData = lastObj;
			if (associatedGeneratorObject.popupTimeLine)
				timeline.addSend(ref.getNanoseconds(), m.index, m.sizeInBits, desc);
			
			// added for dumping AMR trace
			//sim.logRankComm(rank, destIndex, sizeOfO, ref);
			
			synchronized(sim.manager) {
				sim.manager.queueEvent(e);
				sim.packetEmitted(m);			
			}
	}	
	
	

	public Transmission blockingReadFromAny(Time ref) throws InterruptedException {
		return blockingReadFromAny(ref, true);
	}
	
	/**
	 * Can be called by a skeleton thread to obtain the object associated with a communication from any thread.
	 * If the reception queue is not empty, the latest arrived object is returned. If the queue is empty, this method
	 * will be blocking until any communication happens.
	 * @param ref The "actual time" object
	 * @param appearsAsWaiting If true, the node is represented waiting in the timeline, with no job otherwise
	 * @return The object sent by the other thread
	 * @throws InterruptedException This exception is thrown if the thread is interrupted while waiting, which should not be the case
	 */
	public Transmission blockingReadFromAny(Time ref, boolean appearsAsWaiting) throws InterruptedException {
		return blockingReadFromAnyButSome(ref, EMPTY_LIST, appearsAsWaiting, -1);
	}
	
	public Transmission blockingReadFromAny(Time ref, boolean appearsAsWaiting, int operationNumber) throws InterruptedException {
		return blockingReadFromAnyButSome(ref, EMPTY_LIST, appearsAsWaiting, operationNumber);
	}	
	
	
	/**
	 * Calling this method will cause the skeleton to try to receive something for a given time.
	 * This causes a timeout with id={number of times blockingReadFromAny has been called} to be scheduled.
	 * When the timeout expires, the id is checked. If the calls counter is the same, timeout make sense.
	 * @param ref
	 * @param delayNs
	 * @return
	 */
	public Transmission blockingReadFromAny(Time ref, double delayNs) throws InterruptedException {
		scheduleTimeout(ref, delayNs, blockingReadCounter++);
		return blockingReadFromAny(ref);
	}
	
	public Transmission blockingReadFromAnyButOne(Time ref, int exception) throws InterruptedException {
		return blockingReadFromAnyButOne(ref, exception, true, -1);
	}
	
	public Transmission blockingReadFromAnyButOne(Time ref, int exception, boolean appearsAsWaiting, int operationNumber) throws InterruptedException {
		ArrayList<Integer> exceptions = new ArrayList<Integer>(1);
		exceptions.add(exception);
		return blockingReadFromAnyButSome(ref, exceptions, appearsAsWaiting, operationNumber);
	}
	
	
	public Transmission blockingReadFromAnyButSome(Time ref, ArrayList<Integer> exceptions, boolean appearsAsWaiting, int operationNumber) throws InterruptedException {
		localTime.setNanoseconds(ref.getNanoseconds());
		
		// changed March 9th, 2016, while loop re-established, might interfere with timeout but required to
		// support operation number
		while(true) {
			synchronized(this) {
				for (Iterator<Transmission> i = receptionQueue.iterator() ; i.hasNext() ;) {
					Transmission r = i.next();
					Integer origin = r.origin;
					if (!exceptions.contains(origin)) {
						if (operationNumber < 0 || operationNumber == r.msgIndex) {
							i.remove();
							return doReceive(r, ref, appearsAsWaiting);
						}
					}
				}
				if (aboutToDie) {
					return null;
				}
				enterWaitFromAnyState();
			}
		}
		// code commented on March 9th, 2016
		/*	for (Iterator<Transmission> i = receptionQueue.iterator() ; i.hasNext() ;) {
				Transmission r = i.next();
				Integer origin = r.origin;
				if (!exceptions.contains(origin)) {
					if (operationNumber < 0 || operationNumber == r.msgIndex) {
						i.remove();
						return doReceive(r, ref, appearsAsWaiting);
					}
				}
			}
			if (appearsAsWaiting && associatedGeneratorObject.popupTimeLine )
				timeline.addJobPhase(ref.getNanoseconds(), localTime.getNanoseconds(), TimeLine.EnumType.TIME_OUT_WAIT, "timed out wait");			
			ref.setNanoseconds(Math.max(localTime.getNanoseconds(), ref.getNanoseconds()));			
			return null;*/			
			// if the packet just received is for "exception", run the loop again
	//	}
	}	
	
	public void scheduleTimeout(Time ref, double timeNS) {
		scheduleTimeout(ref, timeNS, -1);
	}
	
	private void scheduleTimeout(Time ref, double timeNS, int timeoutIndex) {
		double deadlineNS = ref.getNanoseconds()+timeNS;
		Evt e = new Evt(deadlineNS, this, this);
		Message m = new Message();
		m.index = timeoutIndex;
		e.setMessage(m);
		synchronized(sim.manager) {
			sim.manager.queueEvent(e);		
		}		
	}
	
	public void yield(Time ref) throws InterruptedException {
		scheduleTimeout(ref, 0, -1);
		localTime.setNanoseconds(ref.getNanoseconds());
		synchronized(this) {
			waitingForTimeout = true;			
			this.notify();
			this.wait();	
			waitingForTimeout = false;
		}
	}
 	

	
	public Transmission blockingRead(Time ref, int from) throws InterruptedException {	
		return blockingRead(ref, from, true, -1);
	}
	
	/**
	 * Can be called by a skeleton thread to obtain the object associated with a communication from a particular thread.
	 * If no messages have been received from the specified thread id, current thread will be waiting until such a 
	 * message arrive. If the reception queue contains more than one message from the specified thread id, the latest
	 * one is returned, keeping the FIFO order.
	 * @param ref The "actual time" object
	 * @param from The index of the thread from which the object is awaited
	 * @return The object sent by the other thread
	 * @throws InterruptedException This exception is thrown if the thread is interrupted while waiting, which should not be the case
	 */	
	public Transmission blockingRead(Time ref, int from, boolean appearsAsWaiting, int operationNumber) throws InterruptedException {
		localTime.setNanoseconds(ref.getNanoseconds());
		while(true) {
			synchronized(this) {
				for (Iterator<Transmission> i = receptionQueue.iterator() ; i.hasNext() ;) {
					Transmission r = i.next();
					if (r.origin == from) {
						if (operationNumber < 0 || operationNumber == r.msgIndex) {
							i.remove();
							return doReceive(r, ref, appearsAsWaiting);
						}
					}
				}
				if (aboutToDie) {
					return null;
				}		
				enterWaitFromOneState(from);
			}
		}
	/*	for (Iterator<Transmission> i = receptionQueue.iterator() ; i.hasNext() ;) {
			Transmission r = i.next();
			if (r.origin == from) {
				if (operationNumber < 0 || operationNumber == r.msgIndex) {
					i.remove();
					return doReceive(r, ref, appearsAsWaiting);
				}
			}
		}
		if (aboutToDie == false) {
			throw new IllegalStateException("No such element in reception queue - shouldn't be here");
		} else {
			ref.setNanoseconds(localTime.getNanoseconds());
			return null;
		}*/
	}		
	
	public Transmission blockingReadFromSome(Time ref, ArrayList<Integer> allocated) throws InterruptedException {
		return 	blockingReadFromSome(ref, allocated, true, -1);
	}
	
	public Transmission[] blockingReadFromAllButOne(Time ref, int myRank, boolean appearsAsWaiting, int operationNumber) throws InterruptedException {
		int toCollect = dests.length-1;
		Transmission[] col = new Transmission[dests.length];
		ArrayList<Integer> src = MoreCollections.subsetOfN(0, col.length);
		src.remove(myRank);
		while (toCollect > 0) {
			Transmission r = blockingReadFromSome(ref, src, appearsAsWaiting, operationNumber);
			if (r == null) {
				System.out.print("");
			}
			col[r.origin] = r;
			src.remove(((Integer)r.origin));
			toCollect--;
		}
		return col;
	}
	
	private void enterWaitFromOneState(int from) throws InterruptedException {
		waitingFrom = from;
		this.notify();
		this.wait();
	//	logger.debug("now going " + this.rank);
		waitingFrom = -1;		
	}
	
	private void enterWaitFromAnyState() throws InterruptedException {
		waitingFromAny = true;
		this.notify();
		this.wait();
	//	logger.debug("now going " + this.rank);
		waitingFromAny = false;		
	}
	
	public Transmission blockingReadFromSome(Time ref, ArrayList<Integer> allocated, boolean appearsAsWaiting, int operationNumber) throws InterruptedException {
		localTime.setNanoseconds(ref.getNanoseconds());
		while(true) {
			synchronized(this) {
				for (Iterator<Transmission> i = receptionQueue.iterator() ; i.hasNext() ;) {
					Transmission r = i.next();
					Integer origin = r.origin;
					if (allocated.contains(origin)) {
						if (operationNumber < 0 || r.msgIndex == operationNumber) {
							i.remove();
							return doReceive(r, ref, appearsAsWaiting);
						}
					}
				}
				if (aboutToDie) {
					return null;
				}
				enterWaitFromAnyState();
			}
			for (Iterator<Transmission> i = receptionQueue.iterator() ; i.hasNext() ;) {
				Transmission r = i.next();
				Integer origin = r.origin;
				if (allocated.contains(origin)) {
					if (operationNumber < 0 || r.msgIndex == operationNumber) {
						i.remove();
						return doReceive(r, ref, appearsAsWaiting);
					}
				}
			}
			// if the packet just received is for "exception", run the loop again
		}
	}	
	
	public Transmission nonBlockingReadFromAny(Time ref) {
		if (receptionQueue.size() > 0) {
			Transmission r = receptionQueue.removeFirst();
			return doReceive(r, ref, false);
		}
		return null;
	}
	
	public boolean hasExpiredReceptions(Time ref) {
		for (Transmission reception : receptionQueue) {
			if (reception.timeNS <= ref.getNanoseconds()) {
				return true;
			}
		}
		return false;
	}
	
	public int getReceptionQueueSize() {
		return receptionQueue.size();
	}
	
	private Transmission doReceive(Transmission r, Time ref, boolean appearsAsWaiting) {
		if (localTime.getNanoseconds() < r.timeNS) {
			if (appearsAsWaiting && associatedGeneratorObject.popupTimeLine )
				timeline.addJobPhase(localTime.getNanoseconds(), r.timeNS, TimeLine.EnumType.WAIT, "wait on network");
			localTime.setNanoseconds(r.timeNS);
		}
		ref.setNanoseconds(localTime.getNanoseconds());
	//	System.out.println("(" + rank + ") received from " + r.origin + " at time " + ref + " (index: " + r.msgIndex + ")");
		if (associatedGeneratorObject.popupTimeLine)
			timeline.addRead(ref.getNanoseconds());
		

	//	System.out.println(this.rank + " message " + r.msgIndex + "has been received " + r.o.getClass());
		return r;		
	}
	
	/**
	 * This method is called by the network/event simulator when an event with this element as targer expires.
	 * 
	 * This mechanisms is above all used for BlockingReads with deadline (if no read happened in a time interval, 
	 * the associated application is unblocked
	 */
	@Override
	public void processEvent(Evt e) {
		// idea to develop : add dedicated "code/hash" for timeout. When timeout expires, associated this code to the return object in blocking read
		synchronized(this) {
			if (waitingForTimeout == false && waitingFromAny == false) return;
			if (e.getTimeNS() < localTime.getNanoseconds()) return;
			int index = e.getMessage().index;
			// not sure this should really be placed here, check
			if ((index == blockingReadCounter - 1 || index < 0) && !dead) {
				if (dead) {
				}
				localTime.setNanoseconds(e.getTimeNS());
				this.notify();
				try {
					this.wait();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}				
			}
		}
	}
	
	/**
	 * This method is called by the network simulator upon message reception. It should not be
	 * used by skeleton threads.
	 */
	public void objectReceived(Message m, Object o, int origin, double timeNS) {
		if (dead) {
	//		throw new IllegalStateException("Rank " + rank + " is dead but is still receiving messages...");
		}
		
		Transmission r = (Transmission)o;
		r.timeNS = timeNS;
		
	//	Transmission r = new Transmission(o, origin, timeNS, m.index);
		if (associatedGeneratorObject.popupTimeLine)
			timeline.addReceive(timeNS, m.index);
		synchronized (this) {
			receptionQueue.addLast(r);
			if ((waitingFrom == origin || waitingFromAny) && !dead) {
				this.notify();
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (getError() != null) {
			throw new WrongExperimentException(getError());
		}
	//	logger.debug("Now going : sim");		
	}
	
	/**
	 * This method is called at the end of the simulation to ensure thread termination. 
	 * This method will make the thread skip all operations (especially waiting calls)
	 * in order to have the thread returning from its original start point as soon as possible.
	 * This may however take time has control structure (loops) as not skipped by default.
	 * To ensure fast termination, skeleton code can contains a statement that returns
	 * from the <code>runImpl</code> method if <code>isToBeTerminated()</code> returns true,
	 * i.e.
	 * <code> if (isToBeTerminated()) return;</code>
	 */
	public void terminate(double ref) {
		synchronized (this) {
			this.localTime.setNanoseconds(ref);
			aboutToDie = true;
			// next line was commented, removed comment to unblock with time limited sim
			
			 this.notifyAll();			
		}
	}
	
	public void barrier(Time ref) throws InterruptedException {
		int opNum = collectiveId++;
		if (this.getRank() != 0) {
			this.send(ref, "BARRIER", 1, 0, null, 0, opNum, false);
			Transmission r = this.blockingRead(ref, 0, true, opNum);
			if (!isToBeTerminated()) {
				if (!(r.o.equals("BARRIER"))) {
					System.out.println("Barrier problem at rank " + getRank());
				}
			}
		} else {
			Transmission[] r = blockingReadFromAllButOne(ref, 0, true, opNum);
			for (Transmission rr : r) {
				if (rr != null) {
					if (!rr.o.equals("BARRIER")) {
						System.out.println("Barrier problem at rank " + getRank());
					}
				}
			}
			broadcastButMe(ref, "BARRIER", 1, opNum);
		}
	}	
	

	public void barrierDone(Time ref) {
		setDead(ref);
		synchronized (this) {
			this.notify();
		}
		associatedGeneratorObject.barrierDone();
		
		
	}	
	
	/**
	 * Returns true if for any reason this thread should skip all operations and terminate
	 * as soon as possible
	 */
	public boolean isToBeTerminated() {
		return aboutToDie;
	}
	
	/** Returns true if thread associated to this rank has terminated
	 * 
	 * @author rumley
	 *
	 */
	public boolean isDead() {
		return dead;
	}
	
	public static class Transmission {
		Object o;
		int origin;
		double timeNS;
		int msgIndex;
		public Transmission(Object o, int origin, double timeNS, int msgIndex) {
			this.o = o;
			this.origin = origin;
			this.timeNS = timeNS;
			this.msgIndex = msgIndex;
		}
		public Object getObject() {
			return o;
		}
		
		public int getOrigin() {
			return origin;
		}
	}

	public void setDead(Time ref) {
		dead = true;
		// a dummy event is added at the end of the queue to oblige the simulator to be the last to act
		Evt e = new Evt(ref.getNanoseconds(), associatedGeneratorObject, dummyEventTarget);
		synchronized(sim.manager) {		
			sim.manager.queueEvent(e);
		}
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}


	///
	//
	// THESE METHODS are here for compatibility reason with LWSIMComponent but are unused

	// unused
	@Override
	public void notifyEnd(double ref, double status) {}

	@Override
	public String toShortString() {
		return "actionmanager " + rank;
	}
	
	public double allReduce(Time ref, double value, String string) throws InterruptedException {
		int opNum = collectiveId++;
		broadcastButMe(ref, value, 8, opNum);
		Transmission[] rs = blockingReadFromAllButOne(ref, this.getRank(), true, opNum);
		double result = 0;
		if (string.equals("sum")) {
			for (Transmission r : rs) {
				if (r != null) {
					result += (Double)r.o;
				} else {
					result += value;
				}
			}
		}
		return result;
	}

	public int allReduce(Time ref, int value, String string) throws InterruptedException {
		int opNum = collectiveId++;
		broadcastButMe(ref, value, 4, opNum);
		Transmission[] rs = blockingReadFromAllButOne(ref, this.getRank(), true, opNum);
		int result = 0;
		if (string.equals("sum")) {
			for (Transmission r : rs) {
				if (r != null) {
					result += (Integer)r.o;
				} else {
					result += value;
				}
			}
		}
		return result;
	}
	
	public int[] allReduce(Time ref, int[] values, String string) throws InterruptedException {
		int opNum = collectiveId++;
		broadcastButMe(ref, values, 4*values.length, opNum);
		Transmission[] rs = blockingReadFromAllButOne(ref, this.getRank(), true, opNum);
		int[] result = new int[values.length];
		if (string.equals("sum")) {
			for (int i = 0 ; i < result.length ; i++) {
				for (Transmission r : rs) {
					if (r != null) {
						result[i] += ((int[])r.o)[i];
					} else {
						result[i] += values[i];
					}
				}
			}
		}
		return result;
	}












/*	@Override
	public void init() {}*/



}
