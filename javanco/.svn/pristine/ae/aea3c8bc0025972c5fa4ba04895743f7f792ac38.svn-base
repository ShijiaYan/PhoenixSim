package test.javancox.inputs;

import org.junit.Test;

import ch.epfl.JavancoGUI;
import ch.epfl.general_libraries.gui.NewObjectPanel;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javancox.inputs.topology.PositionnerAndLinkerBasedGenerator;

public class TestGUIInstanciation {
	public static void main(String[] args) throws Exception {
		TestGUIInstanciation test = new TestGUIInstanciation();
		
		test.test1();
	}
	
	@Test
	@SuppressWarnings("all")
	public void test1() throws Exception {

		NewObjectPanel<PositionnerAndLinkerBasedGenerator> p =
			new NewObjectPanel<PositionnerAndLinkerBasedGenerator>(PositionnerAndLinkerBasedGenerator.class);
		
		synchronized(p.getNotifiable()) {
			try {
				p.getNotifiable().wait(4000);
			}
			catch(Exception e) {}
		}
		
		if (p.getObject() != null) {
			PositionnerAndLinkerBasedGenerator gen = p.getObject();
			AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
			gen.generate(agh);
			
			JavancoGUI.displayGraph(agh);
		}
		Thread.sleep(4000);
		

	}
	
	
}

