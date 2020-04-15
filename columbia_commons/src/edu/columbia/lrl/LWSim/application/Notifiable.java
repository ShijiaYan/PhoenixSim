package edu.columbia.lrl.LWSim.application;

import edu.columbia.lrl.general.Message;

public interface Notifiable {

	public void objectReceived(Message m, Object o, int origin, double timeNS);

}
