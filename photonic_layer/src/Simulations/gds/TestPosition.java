package Simulations.gds;

import GDS.Elements.Positioning.Position;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.Map;

public class TestPosition {

	public static void main(String[] args) {
		double x = 1 ;
		double y = 2 ;
		Position pt = new Position(x,y) ;
		System.out.println(pt.getString()); 
		pt.setX(3);
		System.out.println(pt.getString()); 
		Position p1 = new Position(0,0) ;
		p1.Polar(5, Math.PI/6);
		System.out.println(p1.getString());
		
		Map<String, Position> ports = new SimpleMap<>() ;
		ports.put("wg1.port1", pt) ;
		ports.put("wg1.port2", p1) ;
		
		System.out.println(ports.get("wg1.port2").getString()) ;
		
		String a = "123.323" ;
		double b = Double.parseDouble(a) ;
		System.out.println(b) ;
		
	}

}
