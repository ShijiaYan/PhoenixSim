package ch.epfl.general_libraries.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class MoreString {
	
	public static Collection<Character> toCharCollection(String source) {
		ArrayList<Character> al = new ArrayList<>(source.length());
		for (int i = 0; i < source.length() ; i++) {
			al.add(source.charAt(i));
		}
		return al;
	}
	
	public static Set<Character> toCharSet(String source) {
		TreeSet<Character> al = new TreeSet<>();
		for (int i = 0; i < source.length() ; i++) {
			al.add(source.charAt(i));
		}
		return al;
	}	

	public static String toString(ArrayList<Character> alc) {
		StringBuilder sb = new StringBuilder();
		int size = alc.size();
        for (Character character : alc) {
            sb.append(character);
        }
		return sb.toString();
	}



}
