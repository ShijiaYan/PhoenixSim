package edu.columbia.lrl.experiments.topology_radix.routing_study;

import java.util.ArrayList;
import java.util.Iterator;

import ch.epfl.general_libraries.utils.MoreString;
import ch.epfl.general_libraries.utils.SetEnumerator;

public class TypeEnumerator implements Iterable<String> {
	
	private int[] dimensions;
	private String possiblechars;
	
	public TypeEnumerator(int dimension, String possiblechars) {
		this(new int[]{dimension}, possiblechars);
	}
	
	public TypeEnumerator(int[] dimensions, String possiblechars) {
		this.dimensions = dimensions;
		this.possiblechars = possiblechars;
	}
	
//	SetEnumerator<Character> setEnum;
//	private int index = 0;
	
	@Override
	public Iterator<String> iterator() {
//		index = 0;
//		setEnum = new SetEnumerator<Character>(MoreString.toCharSet(possiblechars), dimensions[index], true);
		return new Iterator<String>() {
			
			int index = 0;
			SetEnumerator<Character> setEnum = new SetEnumerator<>(MoreString.toCharSet(possiblechars), dimensions[index], true);
			Iterator<ArrayList<Character>> protoIt = setEnum.iterator();

			@Override
			public boolean hasNext() {
				while (protoIt.hasNext() == false && index+1 < dimensions.length) {
					index++;
					setEnum = new SetEnumerator<>(MoreString.toCharSet(possiblechars), dimensions[index], true);
					protoIt = setEnum.iterator();
				}
				return protoIt.hasNext();
			}

			@Override
			public String next() {
				ArrayList<Character> alc = protoIt.next();
				System.out.println(MoreString.toString(alc));
				return MoreString.toString(alc);
			}

			@Override
			public void remove() {
				protoIt.remove();
				
			}
			
		};
	} 
	

}
