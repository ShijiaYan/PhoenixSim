package test.edu.columbia.lrl.experiments.spinet;

import org.junit.Test;

import edu.columbia.lrl.experiments.spinet.mains.Explicit44Configuration;

public class SpinetTest {
	
	public static void main(String[] args) throws Exception {
		SpinetTest test = new SpinetTest();
		test.testSpinet();
	}
	
	@Test
	public void testSpinet() throws Exception {	
		Explicit44Configuration.main(new String[]{});
	}

	public SpinetTest() {
	
	}

}
