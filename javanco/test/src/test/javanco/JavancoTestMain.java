package test.javanco;

import org.junit.Test;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.ui.AbstractGraphicalUI;

public class JavancoTestMain {
	
	public static void main(String[] args) throws Exception {
		JavancoTestMain test = new JavancoTestMain();
		test.testNewLink();
		test.testNewNodesPositions();
		test.testOutputImage();
	}
	
	@Test
	public void testNewLink() throws Exception {
		System.out.println(System.getProperty("user.dir"));		
		System.out.println("Starting test");
		AbstractGraphHandler agh = getAGH();
		agh.newLayer("test");
		agh.newNode();
		agh.newNode();
		agh.newLink(0,1);
	}
	@Test
	public void testNewNodesPositions() throws Exception {
		AbstractGraphHandler agh = getAGH();
		agh.activateMainDataHandler();
		agh.activateGraphicalDataHandler();
		agh.newLayer("test");
		agh.newNode(0,100);
		agh.newNode(0,200);
		agh.newLink(0,1);
		LinkContainer link = agh.getLinkContainer(0,1);
		link.setGeodesicLinkLength(null);
		if (link.attribute("length").intValue() != 100) throw new Exception("Length does not match");
	}
	@Test
	public void testOutputImage() throws Exception {
		AbstractGraphHandler agh = getAGH();
		agh.activateGraphicalDataHandler();
		agh.newLayer("test");
		
		AbstractGraphicalUI ui = new AbstractGraphicalUI(AbstractGraphicalUI.getDefaultNetworkPainter(), agh) {};

		java.awt.image.BufferedImage o = ui.getFullView(400, 400);
		javax.imageio.ImageIO.write(o,"png",new java.io.File("test.png"));
		
		agh.newNode(0,0);
		agh.newNode(0,100);
		agh.newNode(0,300);
		
		o = ui.getFullView(400, 400);
		javax.imageio.ImageIO.write(o,"png",new java.io.File("test.png"));		
	}	
	
	private AbstractGraphHandler getAGH() throws Exception {
		return Javanco.getDefaultGraphHandler(false);	
	}
	
}
