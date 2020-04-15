package edu.columbia.lrl.LWSim.traffic.deadlines;

public class NoTTLRequirement extends AbstractTTLGenerator {

	public NoTTLRequirement() {
		this.mean = -1;
		this.min = -1;
	}

	@Override
	public int nextTTL() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public AbstractTTLGenerator getCopy() {
		// TODO Auto-generated method stub
		return new NoTTLRequirement();
	}

}
