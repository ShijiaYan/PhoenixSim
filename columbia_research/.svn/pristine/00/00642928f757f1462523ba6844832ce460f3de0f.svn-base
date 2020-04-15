package edu.columbia.lrl.experiments.AMR2;

import java.io.Serializable;

import org.apache.xerces.dom.AttributeMap;

import ch.epfl.general_libraries.utils.SimpleMap;

public abstract class AbstractAMRXMLElement implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String[] deps;
	public String id;
	boolean sat = false; // means not equal to ""
//	String sat;
	public int at = -1;
	
	private static final String[] empty = new String[0];
	
	public AbstractAMRXMLElement(SimpleMap<String, String> map) {
		for (int i = 0 ; i < map.size() ; i++) {
			String key = map.getKey(i);
			String value = map.getValue(i);
			if (key.equals("dep")) {
				if (value.equals("")) {
					deps = empty;
				} else {
					deps = value.split(",");
				}
			} else if (key.equals("id")) {
				id = value;
			} else if (key.equals("sat")) {
				sat = value.equals("");
//				sat = value;
			}
		}
	}
	
	public AbstractAMRXMLElement(AttributeMap map, int at) {
		deps = map.getNamedItem("dep").getFirstChild().getTextContent().split(",");
		id = map.getNamedItem("id").getFirstChild().getTextContent();
		String satS = map.getNamedItem("sat").getFirstChild().getTextContent();
		sat = satS.equals("");
		this.at = at;
	}
}
