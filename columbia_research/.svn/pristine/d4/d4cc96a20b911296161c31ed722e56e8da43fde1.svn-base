package edu.columbia.lrl.experiments.AMR2;

import java.io.IOException;

import org.apache.xerces.dom.AttributeMap;

import ch.epfl.general_libraries.utils.SimpleMap;

public class AMRXMLCompElement extends AbstractAMRXMLElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double time;
	
	public AMRXMLCompElement(SimpleMap<String, String> map) {
		super(map);
		for (int i = 0 ; i < map.size() ; i++) {
			String key = map.getKey(i);
			String value = map.getValue(i);
			if (key.equals("time")) {
				time = Double.parseDouble(value) *1e9;
			} else if (key.equals("at")) {
				at = Integer.parseInt(value.substring(1));
				
				/*if (at == 2599) {
					System.out.println("parse" + super.id);
				}*/
			}
		}			
	}
	
	public AMRXMLCompElement(AttributeMap map) {
		super(map, Integer.parseInt(map.getNamedItem("at").getFirstChild().getTextContent().substring(1)));
		time = Double.parseDouble(map.getNamedItem("time").getFirstChild().getTextContent());
	}
	
	public String toString() {
		return "at" + at + " EXE " + id;
	}
	

    @SuppressWarnings("unused")
	private void writeObject__(java.io.ObjectOutputStream stream) throws IOException {   	
    	stream.writeInt(id.length());
    	stream.writeChar('\r');    	
    	stream.writeChar('\n');     	
    	stream.writeBytes(id);
    	stream.writeChar('\t');
    	
    	stream.writeObject(id);
    	stream.writeInt(at);
    	stream.writeBoolean(sat);
    	stream.writeObject(deps);
    	stream.writeDouble(time);
    	

    	
    /*	stream.writeChar(' ');
    	stream.writeInt(at);
    	stream.writeChar(' ');
    	stream.writeInt(sat ? 1 : 0);
       	stream.writeChar(' ');
       	stream.writeInt(deps.length);
       	for (int i = 0 ; i < deps.length ; i++) {
       		stream.writeInt(deps[i].length());
       		stream.writeBytes(deps[i]);
       	}
       	stream.writeDouble(time);
       	stream.writeChar('\n');*/
    }

    @SuppressWarnings("unused")
	private void readObject__(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {	
    	int len = stream.readInt();
    	stream.readChar();
    	stream.readChar();      	
    	byte[] buf = new byte[len];
    	stream.read(buf);
    	stream.readChar();
    	
    	id = (String)stream.readObject();
    	at = stream.readInt();
    	sat = stream.readBoolean();
    	deps = (String[])stream.readObject();
    	time = stream.readDouble();

    /*	id = new String(buf);
    	stream.readChar();
    	at = stream.readInt();
    	stream.readChar();
    	sat = (stream.readInt() == 1);
    	stream.readChar();
    	int arrayLen = stream.readInt();
    	deps = new String[arrayLen];
    	for (int i = 0 ; i < arrayLen ; i++) {
    		len = stream.readInt();
    		buf = new byte[len];
    		stream.read(buf);
    		deps[i] = new String(buf);
    	}
    	time = stream.readDouble();
    	stream.readChar();*/
    }
}
