package People.Meisam.GUI.Utilities.DataBaseTable;

import java.util.Arrays;
import java.util.List;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import People.Meisam.GUI.Utilities.SimulationVariable;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import flanagan.io.FileChooser;
import flanagan.io.FileInput;

public class TestDB {
	
	static SimulationDataBase dataBase = new SimulationDataBase() ;
	
	public static void main(String[] args){
		FileChooser fc = new FileChooser() ;
		fc.setExtension("dbsim");
		fc.selectFile() ;
		String filePath = fc.getPathName() ;
		fc.close();
		
		@SuppressWarnings("unused")
		String name, alias, size, type ;
		double[] values = {} ;
		// next reading the file
		FileInput fin = new FileInput(filePath) ;
		int M = fin.numberOfLines() ;
//		System.out.println("number of lines = " + M);
		int k=0 ;
		while(k < M/6){
			name = fin.readLine() ;

			alias = fin.readLine() ;

			size = fin.readLine() ;

			type = fin.readLine() ;
			values = getValuesFromLine(fin.readLine()) ;

			fin.readLine() ;
			dataBase.addNewVariable(new SimulationVariable(name, alias, values));
			k++ ;

		}
		
		fin.close();
		dataBase.show();
	}
	
    private static double[] getValuesFromLine(String valueLine){
    	double[] values = {} ;
    	List<String> allValues = Arrays.asList(valueLine.trim().split("\t")) ;
    	int M = allValues.size() ;
        for (String allValue : allValues) {
            values = MoreMath.Arrays.append(values, Double.parseDouble(allValue));
        }
//    	MoreMath.Arrays.show(values);
    	return values ;
    }
	
	
	

}
