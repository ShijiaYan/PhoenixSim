package edu.columbia.lrl.experiments.AMR2;

import java.io.IOException;

import org.apache.xerces.dom.AttributeMap;

import ch.epfl.general_libraries.utils.SimpleMap;

public class AMRXMLCommElement extends AbstractAMRXMLElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int from;
	int to;
	int size;
	
	public AMRXMLCommElement(SimpleMap<String, String> map) {
		super(map);
		for (int i = 0 ; i < map.size() ; i++) {
			String key = map.getKey(i);
			String value = map.getValue(i);
			if (key.equals("to")) {
				to = Integer.parseInt(value.substring(1));
			} else if (key.equals("size")) {
				size = Integer.parseInt(value);
			} else if (key.equals("from")) {
				from = Integer.parseInt(value.substring(1));
				at = from;
			}
		}			
	}
	
	public AMRXMLCommElement(AttributeMap map) {
		super(map, Integer.parseInt(map.getNamedItem("from").getFirstChild().getTextContent().substring(1)));
		from = at;
		to = Integer.parseInt(map.getNamedItem("to").getFirstChild().getTextContent().substring(1));
		size = Integer.parseInt(map.getNamedItem("size").getFirstChild().getTextContent());
	}
	
	public String toString() {
		return "at" + at + " COM " + id;
	}
	
    @SuppressWarnings("unused")
	private void writeObject___(java.io.ObjectOutputStream stream) throws IOException {
   	
    	stream.writeInt(id.length());
    	stream.writeChar('\r');    	
    	stream.writeChar('\n');    	
    	stream.writeBytes(id);
    	stream.writeChar('\t');
    	
    	stream.writeObject(id);
    	stream.writeInt(at);
    	stream.writeBoolean(sat);
    	stream.writeObject(deps);
    	stream.writeInt(from);
    	stream.writeInt(to);
    	stream.writeInt(size);
    	
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
	private void readObject___(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {  	
    	int len = stream.readInt();
    	stream.readChar();
    	stream.readChar();    	
    	byte[] buf = new byte[len];
    	stream.read(buf);
    	stream.readChar();
    	
    	id = (String)stream.readObject();
    	if (id.equals("AS2599D202T1.0")) {
    		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    	}
    	at = stream.readInt();
    	sat = stream.readBoolean();
    	deps = (String[])stream.readObject();
    	from = stream.readInt();
    	to = stream.readInt();
    	size = stream.readInt();
    	

    	
    	
    	
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
