package edu.columbia.ke.component;

import java.util.ArrayList;
import java.util.Collections;
import ch.epfl.general_libraries.utils.Pair;

// temp valid for 2D Torus
public class TorusSwitch extends HighRadixSwitch {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dx;
	private int dy;
	private int dz;
	
	private int x;
	private int y;
	private int z;
	
	private int ewnsud[];
	private IndexTable iTable;
	private TorusSwitch[] swSet; 
	
	public TorusSwitch(int id, int nbNode, double swi,
			double linkLatency, int dx, int dy, int dz, IndexTable table) {
		super(id, nbNode, swi, linkLatency);
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		
		this.iTable = table;
		
		x  = iTable.get(id, 0);
		y  = iTable.get(id, 1);
		z  = iTable.get(id, 2);
		
		ewnsud = new int [6];
	}
	
	public void selfBuildTopology(TorusSwitch[] sw) {
		int tx, ty, tz, tid;
		tx = x;
		ty = y;
		tz = z;
		
		tx++;
		tx = tx % dx;
		tid = this.iTable.getID(tx, ty, tz);
		addSuccessor(tid, sw[tid]);
		ewnsud[0] = tid;
		tx = x;
		
		ty++;
		ty = ty % dy;
		tid = this.iTable.getID(tx, ty, tz);
		addSuccessor(tid, sw[tid]);
		ewnsud[2] = tid;
		ty = y;
		
		tz++;
		tz = tz % dz;
		tid = this.iTable.getID(tx, ty, tz);
		addSuccessor(tid, sw[tid]);
		ewnsud[4] = tid;
		tz = z;
		
		tx--;
		tx = (tx + dx) % dx;
		tid = this.iTable.getID(tx, ty, tz);
		addSuccessor(tid, sw[tid]);
		ewnsud[1] = tid;
		tx = x;
		
		ty--;
		ty = (ty + dy) % dy;
		tid = this.iTable.getID(tx, ty, tz);
		addSuccessor(tid, sw[tid]);
		ewnsud[3] = tid;
		ty = y;
		
		tz--;
		tz = (tz + dz) % dz;
		tid = this.iTable.getID(tx, ty, tz);
		addSuccessor(tid, sw[tid]);
		ewnsud[5] = tid;
		tz = z;
		
	}
	
	private void addXHops(int difx, ArrayList<Integer> nextHops) {
		if (difx == 0)
			return;
		if (difx == dx / 2 || difx == -dx / 2) {
			nextHops.add(ewnsud[0]);
			nextHops.add(ewnsud[1]);	
		}
		else if (difx > dx / 2 || (difx < 0 && difx > -dx/2) )
			nextHops.add(ewnsud[1]);	
		else if (difx < -dx / 2 || (difx > 0 && difx < dx / 2))
			nextHops.add(ewnsud[0]);
		else {
			int a = 0;
			a++;
		}
	}
	
	private void addYHops(int dify, ArrayList<Integer> nextHops) {
		if (dify == 0)
			return;
		if (dify == dy / 2 || dify == -dy / 2) {
			nextHops.add(ewnsud[2]);
			nextHops.add(ewnsud[3]);	
		}
		else if (dify > dy / 2 || (dify < 0 && dify > -dy/2) )
			nextHops.add(ewnsud[3]);	
		else if (dify < -dy / 2 || (dify > 0 && dify < dy / 2))
			nextHops.add(ewnsud[2]);
		else {
			int a = 0;
			a++;
		}
	}
	
	private void addZHops(int difz, ArrayList<Integer> nextHops) {
		if (difz == 0)
			return;
		if (difz == dz / 2 || difz == -dz / 2) {
			nextHops.add(ewnsud[4]);
			nextHops.add(ewnsud[5]);	
		}
		else if (difz > dz / 2 || (difz < 0 && difz > -dz/2) )
			nextHops.add(ewnsud[5]);	
		else if (difz < -dz / 2 || (difz > 0 && difz < dz / 2))
			nextHops.add(ewnsud[4]);
		else {
			int a = 0;
			a++;
		}
	}

	@Override
	protected Pair<Integer, Double> getNextHop(int dest, double currentTime) {
		ArrayList<Integer> nextHops = new ArrayList<Integer>();
		
		if (dest == this.id)
			nextHops.add(dest);
		else {
			// return new Pair<Integer, Double> (dest, 0d);

			int tx = iTable.get(dest, 0);
			int ty = iTable.get(dest, 1);
			int tz = iTable.get(dest, 2);

			int difx = tx - this.x;
			int dify = ty - this.y;
			int difz = tz - this.z;

			this.addXHops(difx, nextHops);
			this.addYHops(dify, nextHops);
			this.addZHops(difz, nextHops);
		}
		
		Collections.shuffle(nextHops);
		
		int nextHop = -1;
		double smallestLWT = this.packetTransTime;
		for (int tmpNextHop: nextHops) {
			double tmpWaitTime = (double) busyUntil.get(tmpNextHop) - currentTime;
			if (tmpWaitTime <= 0) {
				nextHop = tmpNextHop;
				break;
			} else {
				if (tmpWaitTime < smallestLWT)
					smallestLWT = tmpWaitTime;
			}
		}
		return new Pair<Integer, Double> (nextHop, smallestLWT);
	}
}
