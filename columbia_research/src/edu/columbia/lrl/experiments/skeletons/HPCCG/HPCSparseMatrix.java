package edu.columbia.lrl.experiments.skeletons.HPCCG;

import java.util.HashMap;

public class HPCSparseMatrix {
	
	String title;
	  int start_row;
	  int stop_row;
	  int total_nrow;
	  long total_nnz;
	  int local_nrow;
	  int local_ncol;  // Must be defined in make_local_matrix
	  int local_nnz;
	
	  int[] nnz_in_row;
//	  double[][]ptr_to_vals_in_row;
//	  int[][] ptr_to_inds_in_row;
//	  double[][] ptr_to_diags;
	  
	  private static class Row {
		  int[] indexes;
		  double[] vals;
	  }
	  
	  HashMap<Integer, Row> map = new HashMap<>();

	  int num_external;
	  int num_send_neighbors;
	  int[] external_index;
	  int[] external_local_index;
	  int total_to_be_sent;
	  int[] elements_to_send;
	  int[] neighbors;
	  int[] recv_length;
	  int[] send_length;
	  double[] send_buffer;	  
	
//	  double[] list_of_vals;   //needed for cleaning up memory
//	  int[] list_of_inds;

	public double[] getRowOfVals(int i) {
		return map.get(i).vals;
	}

	public int[] getRowOfInds(int i) {
		return map.get(i).indexes;
	}
	
	public void createRow(int rowIndex, int size) {
		Row row = new Row();
		row.indexes = new int[size];
		row.vals = new double[size];
		if (map.get(rowIndex) != null) throw new IllegalStateException();
		map.put(rowIndex, row);
	}

}
