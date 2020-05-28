package edu.columbia.lrl.experiments.skeletons.HPCCG;

import java.util.TreeMap;

import ch.epfl.general_libraries.simulation.Time;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.LWSim.application.ActionManager.Transmission;

public class HPCCGstruct {
	
	// CONST:
	final static int max_external = 10000;
	final static int max_num_messages = 500;
	final static int max_num_neighbors = max_num_messages;

	HPCSparseMatrix matrix;
	double[] x;
	double[] b;
	double[] xexact;	
	
	public HPCCGstruct(HPCSparseMatrix mat, double[] x, double[] b, double[] xexact) {
		this.matrix = mat;
		this.x = x;
		this.b = b;
		this.xexact = xexact;
	}	
	
	public static HPCCGstruct generateMatrix(int nx, int ny, int nz, int size, int rank, boolean debug) {
		HPCSparseMatrix A = new HPCSparseMatrix();
		
		int local_nrow = nx*ny*nz; // This is the size of our subblock
		int local_nnz = 27*local_nrow; // Approximately 27 nonzeros per row (except for boundary nodes)

		int total_nrow = local_nrow*size; // Total number of grid points in mesh
		long total_nnz = 27* total_nrow; // Approximately 27 nonzeros per row (except for boundary nodes)

		int start_row = local_nrow*rank; // Each processor gets a section of a chimney stack domain
		int stop_row = start_row+local_nrow-1;


		// Allocate arrays that are of length local_nrow
		A.nnz_in_row = new int[local_nrow];

		double[] x = new double[local_nrow];
		double[] b = new double[local_nrow];
		double[] xexact = new double[local_nrow];


		// Allocate arrays that are of length local_nnz
	//	A.list_of_vals = new double[local_nnz];
	//	A.list_of_inds = new int   [local_nnz];

	//	double[] curvalptr = A.list_of_vals;
	//	int[] curindptr = A.list_of_inds;
		


		long nnzglobal = 0;
		for (int iz = 0; iz < nz; iz++) {
			for (int iy = 0; iy < ny; iy++) {
				for (int ix = 0; ix < nx; ix++) {

					int curlocalrow = iz * nx * ny + iy * nx + ix;
					int currow = start_row + iz * nx * ny + iy * nx + ix;
					int nnzrow = 0;
					A.createRow(curlocalrow, 27);
					int[] indsInRow = A.getRowOfInds(curlocalrow);
					double[] valsInRow = A.getRowOfVals(curlocalrow);
					
					int index = 0;
		//			A.ptr_to_vals_in_row[curlocalrow] = curvalptr;
		//			A.ptr_to_inds_in_row[curlocalrow] = curindptr;
					for (int sz = -1; sz <= 1; sz++) {
						for (int sy = -1; sy <= 1; sy++) {
							for (int sx = -1; sx <= 1; sx++) {
								int curcol = currow + sz * nx * ny + sy * nx + sx;

								if (curcol >= 0 && curcol < total_nrow) {
									if (curcol == currow) {
			//							A.ptr_to_diags[curlocalrow] = curValIndex;
									//	curValIndex++;
									//	A.list_of_vals[curValIndex] = 27;
										valsInRow[index] = 27;
									}
									else {
										valsInRow[index] = -1;
									//	curValIndex++;
									//	A.list_of_vals[curValIndex] = -1;
									//	*curvalptr++ = -1.0;
									}
									indsInRow[index] = curcol;
									index++;
									//curIndIndex++;
									//A.list_of_inds[curIndIndex] = curcol;
									//*curindptr++ = curcol;
									nnzrow++;

								}
							}
						}
					}
					A.nnz_in_row[curlocalrow] = nnzrow;
					nnzglobal += nnzrow;
					x[curlocalrow] = 0.0;
					b[curlocalrow] = 27.0 - (double) (nnzrow-1);
					xexact[curlocalrow] = 1.0;
				} // end ix loop
			}
		}	
		
		if (debug) System.out.print("Process " + rank + " of " + size + " has " + local_nrow);

		if (debug) System.out.println(" rows. Global rows " + start_row  + " through " +  stop_row);

		if (debug) System.out.println("Process " + rank + " of " + size + " has " + local_nnz + " nonzeros.");

		A.start_row = start_row ; 
		A.stop_row = stop_row;
		A.total_nrow = total_nrow;
		A.total_nnz = total_nnz;
		A.local_nrow = local_nrow;
		A.local_ncol = local_nrow;
		A.local_nnz = local_nnz;

		return new HPCCGstruct(A, x, b, xexact);

	}	
	
	public static void make_local_matrix(ActionManager c, Time ref, HPCSparseMatrix A, int rank, int size, boolean debug) throws InterruptedException {
		TreeMap<Integer, Integer> externals = new TreeMap<>();
		int i, j, k;
		int num_external = 0;
		double t0;

		int debug_details = 0; // Set to 1 for voluminous output
		


		// Extract Matrix pieces

		int start_row = A.start_row;
		int stop_row = A.stop_row;
	//	int total_nrow = A.total_nrow;
	//	long total_nnz = A.total_nnz;
	//	int local_nrow = A.local_nrow;
	//	int local_nnz = A.local_nnz;
	//	int[] nnz_in_row = A.nnz_in_row;
	//	double[][] ptr_to_vals_in_row = A.ptr_to_vals_in_row;
	//	int[][] ptr_to_inds_in_row = A.ptr_to_inds_in_row;



		// We need to convert the index values for the rows on this processor
		// to a local index space. We need to:
		// - Determine if each index reaches to a local value or external value
		// - If local, subtract start_row from index value to get local index
		// - If external, find out if it is already accounted for.  
		//   - If so, then do nothing, 
		//   - otherwise 
		//     - add it to the list of external indices,  
		//     - find out which processor owns the value. 
		//     - Set up communication for sparse MV operation.


		///////////////////////////////////////////
		// Scan the indices and transform to local
		///////////////////////////////////////////

	//	if (debug) t0 = mytimer();

		int[] external_index = new int[max_external];
		int[] external_local_index = new int[max_external];
		A.external_index = external_index;
		A.external_local_index = external_local_index;

		for (i=0; i< A.local_nrow; i++) {
			int[] indsInRow = A.getRowOfInds(i);
			for (j=0; j<A.nnz_in_row[i]; j++) {
				int cur_ind = indsInRow[j];
				if (debug) {
				//	System.out.println("Process " + rank + " of " + size + " getting index " + cur_ind  + " in local row " + i);
				}
				if (start_row <= cur_ind && cur_ind <= stop_row) {
					indsInRow[j] -= start_row;
				} else { // Must find out if we have already set up this point {
				//	Integer cur = externals.get(cur_ind);
				//	Map.Entry<Integer,Integer> entry = externals.lastEntry();
				//	if (cur != null && entry != null && cur ==entry.getValue()) {
					if (!externals.containsKey(cur_ind)) {
						externals.put(cur_ind, num_external); 	// instead of	externals[cur_ind] = num_external++;
						num_external++;

						if (num_external<=max_external) {
							external_index[num_external-1] = cur_ind;
							// Mark index as external by negating it
							indsInRow[j] = - (indsInRow[j] + 1);
						} else {
							System.err.println("Must increase max_external in HPC_Sparse_Matrix.hpp");
							throw new IllegalStateException();
						}
					} else {
						// Mark index as external by adding 1 and negating it
						indsInRow[j] = - (indsInRow[j] + 1);
					}
				}
			}
		}

		if (debug) {
		//	t0 = mytimer() - t0;
			System.out.println("Processor " + rank + " of " + size + ": Number of external equations = " + num_external);
		//	cout << "            Time in transform to local phase = " << t0 << endl;

		}

		////////////////////////////////////////////////////////////////////////////
		// Go through list of externals to find out which processors must be accessed.
		////////////////////////////////////////////////////////////////////////////

		//if (debug) t0 = mytimer();

		A.num_external = num_external;
		int[] tmp_buffer  = new int[size];  // Temp buffer space needed below

		// Build list of global index offset

		//int[] global_index_offsets = new int[size];
		for (i=0;i<size; i++) {
			tmp_buffer[i] = 0;  // First zero out
		}

		tmp_buffer[rank] = start_row; // This is my start row

		// This call sends the start_row of each ith processor to the ith 
		// entry of global_index_offset on all processors.
		// Thus, each processor know the range of indices owned by all
		// other processors.
		// Note:  There might be a better algorithm for doing this, but this
		//        will work...

		int[] global_index_offsets = c.allReduce(ref, tmp_buffer, "sum");

		// Go through list of externals and find the processor that owns each
		int[] external_processor = new int[num_external];
		int[] new_external_processor = new int[num_external];

		for (i=0; i< num_external; i++) {
			int cur_ind = external_index[i];
			for (j=size-1; j>=0; j--) {
				if (global_index_offsets[j] <= cur_ind) {
					external_processor[i] = j;
					break;
				}
			}
		}
		if (debug) {
		//	t0 = mytimer() - t0;
			System.out.println("          Time in finding processors phase = ");
		}


		////////////////////////////////////////////////////////////////////////////
		// Sift through the external elements. For each newly encountered external
		// point assign it the next index in the sequence. Then look for other
		// external elements who are update by the same node and assign them the next
		// set of index numbers in the sequence (ie. elements updated by the same node
		// have consecutive indices).
		////////////////////////////////////////////////////////////////////////////

	//	if (debug) t0 = mytimer();

		int count = A.local_nrow;
		for (i = 0; i < num_external; i++) {
			external_local_index[i] = -1;
		}

		for (i = 0; i < num_external; i++) {
			if (external_local_index[i] == -1) {
				external_local_index[i] = count++;

				for (j = i + 1; j < num_external; j++) {
					if (external_processor[j] == external_processor[i]) {
						external_local_index[j] = count++;
					}
				}
			}
		}

		if (debug) {
		//	t0 = mytimer() - t0;
			System.out.println("           Time in scanning external indices phase = ");
		}
		//if (debug) t0 = mytimer();


		for (i=0; i< A.local_nrow; i++) {
			int[] indsInRow = A.getRowOfInds(i);
			for (j=0; j<A.nnz_in_row[i]; j++) {
				if (indsInRow[j]<0) { // Change index values of externals
					int cur_ind = - indsInRow[j] - 1;
					Integer extGet = externals.get(cur_ind);
					indsInRow[j] = external_local_index[extGet];
				}
			}
		}

		for (i = 0 ; i < num_external; i++) {
			new_external_processor[i] = 0;
		}

		for (i = 0; i < num_external; i++) {
			new_external_processor[external_local_index[i] - A.local_nrow] =  external_processor[i];
		}

		if (debug) {
		//	t0 = mytimer() - t0;
			System.out.println("           Time in assigning external indices phase = ");
		}

		if (debug) {
			for (i = 0; i < num_external; i++) {
		//		System.out.println("Processor " + rank + " of " + size + ": external processor[" + i + "] = " + external_processor[i]);

		//		System.out.println("Processor " + rank + " of " + size + ": new external processor[" + i + "] = " + new_external_processor[i]);
			}
		}

		////////////////////////////////////////////////////////////////////////////
		///
		// Count the number of neighbors from which we receive information to update
		// our external elements. Additionally, fill the array tmp_neighbors in the
		// following way:
		//      tmp_neighbors[i] = 0   ==>  No external elements are updated by
		//                              processor i.
		//      tmp_neighbors[i] = x   ==>  (x-1)/size elements are updated from
		//                              processor i.
		///
		////////////////////////////////////////////////////////////////////////////

	//	t0 = mytimer();
		int[] tmp_neighbors = new int[size];
		for (i = 0 ; i < size ; i++) {
			tmp_neighbors[i] = 0;
		}

		int num_recv_neighbors = 0;
		int length             = 1;

		for (i = 0; i < num_external; i++) {
			if (tmp_neighbors[new_external_processor[i]] == 0) {
				num_recv_neighbors++;
				tmp_neighbors[new_external_processor[i]] = 1;
			}
			tmp_neighbors[new_external_processor[i]] += size;
		}

		/// sum over all processors all the tmp_neighbors arrays ///
		
		tmp_buffer = c.allReduce(ref, tmp_neighbors, "sum");

		/// decode the combined 'tmp_neighbors' (stored in tmp_buffer) 
		//  array from all the processors

		int num_send_neighbors = tmp_buffer[rank] % size;

		/// decode 'tmp_buffer[rank] to deduce total number of elements 
		//  we must send

		A.total_to_be_sent = (tmp_buffer[rank] - num_send_neighbors) / size;

		//
		// Check to see if we have enough workspace allocated.  This could be 
		// dynamically modified, but let's keep it simple for now...
		//

		if (num_send_neighbors > max_num_messages) {
			System.out.println("Must increase max_num_messages in HPC_Sparse_Matrix.hpp");
			System.out.println("Must be at least " +  num_send_neighbors);
			throw new IllegalStateException();
		}

		if (A.total_to_be_sent > max_external ) {
			System.out.println("Must increase max_external in HPC_Sparse_Matrix.hpp");
			System.out.println("Must be at least " + A.total_to_be_sent);
			throw new IllegalStateException();
		}
		tmp_neighbors = null;

		if (debug) {
		//	t0 = mytimer() - t0;
			System.out.println("           Time in finding neighbors phase = ");
			
			System.out.println("Processor " + rank + " of " + size + ": Number of send neighbors = " + num_send_neighbors);
			System.out.println("Processor " + rank + " of " + size + ": Number of receive neighbors = " + num_recv_neighbors);
			System.out.println("Processor " + rank + " of " + size + ": Total number of elements to send = " + A.total_to_be_sent);
			c.barrier(ref);
		}


		/////////////////////////////////////////////////////////////////////////
		///
		// Make a list of the neighbors that will send information to update our
		// external elements (in the order that we will receive this information).
		///
		/////////////////////////////////////////////////////////////////////////

		int[] recv_list = new int[max_external];

		j = 0;
		recv_list[j++] = new_external_processor[0];
		for (i = 1; i < num_external; i++) {
			if (new_external_processor[i - 1] != new_external_processor[i]) {
				recv_list[j++] = new_external_processor[i];
			}
		}

		//
		// Send a 0 length message to each of our recv neighbors
		//

		int [] send_list = new int[num_send_neighbors];
		for (i = 0 ; i < num_send_neighbors; i++ ) {
			send_list[i] = 0;
		}

		//
		//  first post receives, these are immediate receives
		//  Do not wait for result to come, will do that at the
		//  wait call below.
		//
		int MPI_MY_TAG = 99;
		
		for (i = 0; i < num_recv_neighbors; i++) {
			c.send(ref, tmp_buffer[i], 4, recv_list[i], null, 0, MPI_MY_TAG, true);
			//MPI_Send(tmp_buffer+i, 1, MPI_INT, recv_list[i], MPI_MY_TAG, 
			//		MPI_COMM_WORLD);
		}		

	//	MPI_Request * request = new MPI_Request[max_num_messages];
		for (i = 0; i < num_send_neighbors; i++) {
			Transmission r = c.blockingReadFromAny(ref, true, MPI_MY_TAG);
			if (r == null) {
				System.out.println("Rank : "  + rank);
			}
			tmp_buffer[i] = (int)(Integer) r.getObject();
			///
			// Receive message from each send neighbor to construct 'send_list'.
			///
			send_list[i] = r.getOrigin();
		//	MPI_Irecv(tmp_buffer+i, 1, MPI_INT, MPI_ANY_SOURCE, MPI_MY_TAG, 
		//			MPI_COMM_WORLD, request+i);
		}

		// send messages 


		/////////////////////////////////////////////////////////////////////////
		///
		//  Compare the two lists. In most cases they should be the same.  
		//  However, if they are not then add new entries to the recv list
		//  that are in the send list (but not already in the recv list).
		///
		/////////////////////////////////////////////////////////////////////////

		for (j = 0; j < num_send_neighbors; j++) {
			int found = 0;
			for (i = 0; i < num_recv_neighbors; i++) {
				if (recv_list[i] == send_list[j])
					found = 1;
			}

			if (found == 0) {
				if (debug) {
					System.out.println("Processor " + rank + " of " + size + ": recv_list[" + num_recv_neighbors + "] = "+  send_list[j] );
				}
				recv_list[num_recv_neighbors] = send_list[j];
				num_recv_neighbors++;
			}
		}

		send_list = null;
		
		num_send_neighbors = num_recv_neighbors;

		if (num_send_neighbors > max_num_messages) {
			System.err.println("Must increase max_external in HPC_Sparse_Matrix.hpp");
			throw new IllegalStateException();
		}

		/////////////////////////////////////////////////////////////////////////
		/// Start filling HPC_Sparse_Matrix struct
		/////////////////////////////////////////////////////////////////////////

	//	A.total_to_be_sent = total_to_be_sent;
		A.elements_to_send = new int[A.total_to_be_sent];

		for (i = 0 ; i < A.total_to_be_sent; i++ ) {
			A.elements_to_send[i] = 0;
		}

		//
		// Create 'new_external' which explicitly put the external elements in the
		// order given by 'external_local_index'
		//

		int[] new_external = new int[num_external];
		for (i = 0; i < num_external; i++) {
			new_external[external_local_index[i] - A.local_nrow] = external_index[i];
		}

		/////////////////////////////////////////////////////////////////////////
		//
		// Send each processor the global index list of the external elements in the
		// order that I will want to receive them when updating my external elements
		//
		/////////////////////////////////////////////////////////////////////////

		int[] lengths = new int[num_recv_neighbors];

		MPI_MY_TAG++;

		c.barrier(ref);


		A.neighbors = new int[max_num_neighbors];
		A.recv_length = new int[max_num_neighbors];		
		A.send_length = new int[max_num_neighbors];

		j = 0;
		for (i = 0; i < num_recv_neighbors; i++) {
			int start  = j;
			int newlength = 0;

			// go through list of external elements until updating 
			// processor changes

			while (j < num_external && new_external_processor[j] == recv_list[i]) {
				newlength++;
				j++;
				if (j == num_external) break;
			}

			A.recv_length[i] = newlength;
			A.neighbors[i]  = recv_list[i];

			length = j - start;
		//	MPI_Send(&length, 1, MPI_INT, recv_list[i], MPI_MY_TAG, MPI_COMM_WORLD);
			c.send(ref, length, 4, recv_list[i], null, 0, MPI_MY_TAG, true);
		}
		
		for (i = 0; i < num_recv_neighbors; i++) {
			int partner    = recv_list[i];
			Transmission r = c.blockingRead(ref, partner, true, MPI_MY_TAG);
			lengths[i] = (int)(Integer) r.getObject();
		//	MPI_Irecv(lengths+i, 1, MPI_INT, partner, MPI_MY_TAG, MPI_COMM_WORLD, 
		//			request+i);
		}

		// Complete the receives of the number of externals

		for (i = 0; i < num_recv_neighbors; i++) {
			A.send_length[i] = lengths[i];
		}


		///////////////////////////////////////////////////////////////////
		// Build "elements_to_send" list.  These are the x elements I own
		// that need to be sent to other processors.
		///////////////////////////////////////////////////////////////////

		MPI_MY_TAG++;

		j = 0;
		for (i = 0; i < num_recv_neighbors; i++) {
			int start  = j;
			int newlength = 0;

			// Go through list of external elements 
			// until updating processor changes.  This is redundant, but 
			// saves us from recording this information.

			while (j < num_external && new_external_processor[j] == recv_list[i]) {

				newlength++;
				j++;
				if (j == num_external) break;
			}
			int[] subarray = new int[j-start];
			System.arraycopy(new_external, start, subarray, 0, j-start);
			c.send(ref, subarray , subarray.length*4, recv_list[i], null, 0, MPI_MY_TAG, true);
		//	MPI_Send(new_external+start, j-start, MPI_INT, recv_list[i], 
		//			MPI_MY_TAG, MPI_COMM_WORLD);
		}
		
		j = 0;
		for (i = 0; i < num_recv_neighbors; i++) {
			Transmission r = c.blockingRead(ref, A.neighbors[i], true, MPI_MY_TAG);
			System.arraycopy(r.getObject(), 0, A.elements_to_send, j, A.send_length[i]);
		//	MPI_Irecv(elements_to_send+j, send_length[i], MPI_INT, neighbors[i], 
		//			MPI_MY_TAG, MPI_COMM_WORLD, request+i);
			j += A.send_length[i];
		}

		// receive from each neighbor the global index list of external elements

	/*	for (i = 0; i < num_recv_neighbors; i++) {
			if ( MPI_Wait(request+i, &status) ) {
				cerr << "MPI_Wait error\n"<<endl;
				exit(-1);
			}
		}*/

		/// replace global indices by local indices ///

		for (i = 0; i < A.total_to_be_sent; i++) {
			A.elements_to_send[i] -= start_row;
		}


		////////////////
		// Finish up !!
		////////////////

		A.num_send_neighbors = num_send_neighbors;
		A.local_ncol = A.local_nrow + num_external;

		//Used in exchange_externals
		double[] send_buffer = new double[A.total_to_be_sent];
		A.send_buffer = send_buffer;

		return;		
	}
	

}
