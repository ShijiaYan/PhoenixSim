package edu.columbia.ke.DataStructure;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapSort {

	public MapSort() {
		// TODO Auto-generated constructor stub
	}
	
	// large value first
	@SuppressWarnings("unchecked")
	public static List listSortByValue(Map unsortMap) {	 
		List list = new LinkedList(unsortMap.entrySet());
	 
		Collections.sort(list, (Comparator) (o1, o2) -> -((Comparable) ((Map.Entry) o1).getValue())
                    .compareTo(((Map.Entry) o2).getValue()));
		
		return list;
	}
	
	// large value first
	public static Map mapSortByValue(Map unsortMap) {
		List list = listSortByValue(unsortMap);
		Map sortedMap = new LinkedHashMap();
        for (Object o : list) {
            Map.Entry entry = (Map.Entry) o;
            sortedMap.put(entry.getKey(), entry.getValue());
        }
		return sortedMap;
	}

}
