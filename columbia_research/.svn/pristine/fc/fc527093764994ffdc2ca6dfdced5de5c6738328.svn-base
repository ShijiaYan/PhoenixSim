package edu.columbia.lrl.experiments.skeletons.HPCCG;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.simulation.Time;
import ch.epfl.general_libraries.utils.SimpleMap;
import edu.columbia.lrl.LWSim.InitFeedback;
import edu.columbia.lrl.LWSim.LWSIMExperiment;
import edu.columbia.lrl.LWSim.application.AbstractApplication;
import edu.columbia.lrl.LWSim.application.ActionManager;
import edu.columbia.lrl.LWSim.application.ActionManager.Transmission;

public class HPCCGmain extends AbstractApplication {
	
	private boolean debug;
	
	private int max_iter = 150;		// default parameter from sstmacro, main.cpp
	private double tolerance = 0;	// default parameter from sstmacro, main.cpp
	private double normr;
	private int niters;
	
	private int nx;
	private int ny;
	private int nz;
	

	private int numberOfRanks;
	
	private double opPerNS;
	
	public HPCCGmain(
			@ParamName(name="operation per ns") double opPerNS,
			@ParamName(name="nx") int nx, 
			@ParamName(name="ny") int ny,
			@ParamName(name="nz") int nz,
			@ParamName(name="maxIter") int maxIter, 
			@ParamName(name="tolerance") double tolerance,  
			@ParamName(name="debug") boolean debug) {
		this.opPerNS = opPerNS;
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
		
		this.debug = debug;
		
		this.max_iter = maxIter;
		this.tolerance = tolerance;
	}

	@Override
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap(//"Original size", overall+"",
								"Problem size", (nx*ny*nz*numberOfRanks)+"",
								//"Max iter", max_iter+"",
								//"Tolerance", tolerance+"",
								//"Norm r", normr+"",
								"opsNS", opPerNS+"");
	}

	@Override
	public void runImpl(ActionManager c, int rank, Time ref) throws InterruptedException {
		
		double t6 = 0;	
		

		
		HPCCGstruct struct = HPCCGstruct.generateMatrix(nx, ny, nz, c.getParticipantNumber(), rank, debug);

		HPCCGstruct.make_local_matrix(c, ref, struct.matrix, rank, c.getParticipantNumber(), debug);	
		
		double[] times = new double[7];
		
		int ierr = run(c, rank, ref, struct, times);
		
	}

	@Override
	public InitFeedback init(LWSIMExperiment exp) {
		InitFeedback fb = super.init(exp.getNumberOfClients());
		
	//	int cubeTemp = MoreMaths.ceilDiv(overall, exp.getNumberOfClients());
	//	int[] fact = MoreMaths.factorize(cubeTemp, 3);
		
	//	nx = fact[0];
	//	ny = fact[1];
	//	nz = fact[2];
		
		//nx = 2;
		//ny = 2;
		//nz = cubeTemp/4;
		
		numberOfRanks = exp.getNumberOfClients();
		return fb;
	}

	@Override
	public void addApplicationInfoImpl(LWSIMExperiment lwSimExp, double ref, boolean analyseNodes) {
		// TODO Auto-generated method stub
		
	}
	
	public static void TICK() {
		
	}
	
	public static void TOCK(double d){
		
	}

	public int run(ActionManager c, int rank, Time ref, HPCCGstruct struct, double[] times) throws InterruptedException {
		
		HPCSparseMatrix A = struct.matrix;
		double[] x = struct.x;
		double[] b = struct.b;
		
		double normr = this.normr;
		
	//	double t_begin = mytimer();  // Start timing right away

		double t0 = 0.0, t1 = 0.0, t2 = 0.0, t3 = 0.0, t4 = 0.0;
	//	#ifdef USING_MPI
		double t5 = 0.0;
	//	#endif
		int nrow = A.local_nrow;
		int ncol = A.local_ncol;

		double[] r = new double [nrow];
		double[] p = new double [ncol]; // In parallel case, A is rectangular
		double[] Ap = new double [nrow];

		normr = 0.0;
		double rtrans = 0.0;
		double oldrtrans = 0.0;

		int print_freq = max_iter/10; 
		if (print_freq>50) print_freq=50;
		if (print_freq<1)  print_freq=1;

		// p is of length ncols, copy x to p for sparse MV operation
		TICK(); 
		waxpby(c, ref, nrow, 1.0, x, 0.0, x, p); 
		TOCK(t2);
	//	#ifdef USING_MPI
		TICK(); 
		exchange_externals(c, ref, A,p, rank, c.getParticipantNumber()); 
		TOCK(t5); 
		//#endif
		TICK(); 
		HPC_sparsemv(c, ref, A, p, Ap); 
		TOCK(t3);
		TICK(); 
		waxpby(c, ref, nrow, 1.0, b, -1.0, Ap, r); 
		TOCK(t2);
		TICK(); 
		rtrans = ddot(c, ref, nrow, r, r); 
		TOCK(t1);
		
		normr = Math.sqrt(rtrans);

		if (rank==0) {
			System.out.println("Initial Residual = " + normr);
		}

		for(int k=1; k<max_iter && normr > tolerance; k++ ) {
			if (k == 1) {
				TICK(); 
				waxpby(c, ref, nrow, 1.0, r, 0.0, r, p); 
				TOCK(t2);
			} else {
				oldrtrans = rtrans;
				TICK(); 
				rtrans = ddot (c, ref, nrow, r, r); 
				TOCK(t1);// 2*nrow ops
				double beta = rtrans/oldrtrans;
				TICK(); 
				waxpby (c, ref, nrow, 1.0, r, beta, p, p);  
				TOCK(t2);// 2*nrow ops
			}
			normr = Math.sqrt(rtrans);
			if (rank==0 && (k%print_freq == 0 || k+1 == max_iter)) {
				System.out.println("Iteration = " + k + "   Residual = "+ normr);

			}

		//	#ifdef USING_MPI
			TICK(); 
			exchange_externals(c, ref, A,p, rank, c.getParticipantNumber()); 
			TOCK(t5); 
		//	#endif
			TICK(); 
			HPC_sparsemv(c, ref, A, p, Ap); 
			TOCK(t3); // 2*nnz ops
			double alpha = 0.0;
			TICK(); 
			alpha = ddot(c, ref, nrow, p, Ap); 
			TOCK(t1); // 2*nrow ops
			alpha = rtrans/alpha;
			TICK(); 
			waxpby(c, ref, nrow, 1.0, x, alpha, p, x);// 2*nrow ops
			waxpby(c, ref, nrow, 1.0, r, -alpha, Ap, r);  
			TOCK(t2);// 2*nrow ops
			niters = k;
		}

		// Store times
		times[1] = t1; // ddot time
		times[2] = t2; // waxpby time
		times[3] = t3; // sparsemv time
		times[4] = t4; // AllReduce time
	//	#ifdef USING_MPI
		times[5] = t5; // exchange boundary time
	//	#endif
	//	times[0] = mytimer() - t_begin;  // Total time. All done...
		
		// record final residual value before exit
		this.normr = normr;
		
		return 0;
	}
	
	public int HPC_sparsemv(ActionManager c, Time ref, HPCSparseMatrix A, double[] x, double[] y) {

		int nrow = A.local_nrow;
		
		int counter = 0;

	//	#ifdef USING_OMP
	//	#pragma omp parallel for
	//	#endif
		for (int i=0; i< nrow; i++) {
			double sum = 0.0;
			double[] cur_vals = A.getRowOfVals(i); // A.ptr_to_vals_in_row[i];

			int[] cur_inds = A.getRowOfInds(i); // A.ptr_to_inds_in_row[i];

			int cur_nnz = A.nnz_in_row[i];

			for (int j=0; j< cur_nnz; j++) {
				sum += cur_vals[j]*x[cur_inds[j]];
				counter++;
			}
			y[i] = sum;
		}
		c.doSomeJob(ref, (double)counter/this.opPerNS, "sparsemv");
		return(0);
	}
	
	public int waxpby(ActionManager c, Time ref, int n, double alpha, double[] x, double beta, double[] y, double[] w) {
		if (alpha==1.0) {
		//	#ifdef USING_OMP
		//	#pragma omp parallel for
		//	#endif
			for (int i=0; i<n; i++) {
				w[i] = x[i] + beta * y[i];
			}
		}
		else if(beta==1.0) {
		//	#ifdef USING_OMP
		//	#pragma omp parallel for
		//	#endif
			for (int i=0; i<n; i++) {
				w[i] = alpha * x[i] + y[i];
			}
		}
		else {
		//	#ifdef USING_OMP
		//	#pragma omp parallel for
		//	#endif
			for (int i=0; i<n; i++) {
				w[i] = alpha * x[i] + beta * y[i];
			}
		}
		
		c.doSomeJob(ref, (double)n/this.opPerNS, "waxpby");

		return(0);
	}
	
	public double ddot (ActionManager c, Time ref, int n, double[] x, double[] y) throws InterruptedException {  
		double local_result = 0.0;
		if (y==x) {
		//	#ifdef USING_OMP
		//	#pragma omp parallel for reduction (+:local_result)
		//	#endif
			for (int i=0; i<n; i++) {
				local_result += x[i]*x[i];
			}
		} else {
			//#ifdef USING_OMP
			//#pragma omp parallel for reduction (+:local_result)
			//#endif
			for (int i=0; i<n; i++) {
				local_result += x[i]*y[i];
			}
		}
		c.doSomeJob(ref, (double)n/this.opPerNS, "ddot");

		//#ifdef USING_MPI
		// Use MPI's reduce function to collect all partial sums
		//double t0 = mytimer();
		double global_result = 0.0;
		
		global_result = c.allReduce(ref, local_result, "sum");
		
		return global_result;
		
	//	MPI_Allreduce(&local_result, &global_result, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
	//	result = new double[]{global_result};
	//	*result = global_result;
	//	time_allreduce += mytimer() - t0;
	//	#else
	//		*result = local_result;
	//	#endif

	//	return(0);
	}
	
	public static void exchange_externals(ActionManager c, Time ref, HPCSparseMatrix A, double[] x, int rank, int size) throws InterruptedException {
		
		int i, j, k;
		int num_external = 0;

		// Extract Matrix pieces

		int local_nrow = A.local_nrow;
		int num_neighbors = A.num_send_neighbors;
		int[] recv_length = A.recv_length;
		int[] send_length = A.send_length;
		int[] neighbors = A.neighbors;
		double[] send_buffer = A.send_buffer;
		int total_to_be_sent = A.total_to_be_sent;
		int[] elements_to_send = A.elements_to_send;

		//
		//  first post receives, these are immediate receives
		//  Do not wait for result to come, will do that at the
		//  wait call below.
		//

		int MPI_MY_TAG = 99;  

	//	MPI_Request * request = new MPI_Request[num_neighbors];


		//
		// Fill up send buffer
		//

		for (i=0; i<total_to_be_sent; i++) {
			send_buffer[i] = x[elements_to_send[i]];
		}

		//
		// Send to each neighbor
		//
		int sendBufferIndex = 0;
		for (i = 0; i < num_neighbors; i++) {
			int n_send = send_length[i];
			
			double[] tosend = new double[n_send];
			System.arraycopy(send_buffer, sendBufferIndex, tosend, 0, n_send);
			
	//		System.out.println("Processor " + rank + " of " + size + " send to "+ neighbors[i] + " " + Arrays.toString(tosend));
			
			c.send(ref, tosend, tosend.length*4, neighbors[i],null, 0, MPI_MY_TAG, false);
			
		//	MPI_Send(send_buffer, n_send, MPI_DOUBLE, neighbors[i], MPI_MY_TAG, 
		//			MPI_COMM_WORLD);
		//	send_buffer += n_send;
			sendBufferIndex += n_send;
		}
		
		//
		// Externals are at end of locals
		//
	//	double *x_external = (double *) x + local_nrow;
		int x_external_index = local_nrow;

		// Post receives first 
		for (i = 0; i < num_neighbors; i++) {
			int n_recv = recv_length[i];
			Transmission r = c.blockingRead(ref, neighbors[i], true, MPI_MY_TAG);
			
			if (r == null || r.getObject() == null) {
				System.out.println("");
			}
			
			System.arraycopy((double[])r.getObject(), 0, x, x_external_index, n_recv);
			
		//	MPI_Irecv(x_external, n_recv, MPI_DOUBLE, neighbors[i], MPI_MY_TAG, 
		//			MPI_COMM_WORLD, request+i);
		//	x_external += n_recv;
			x_external_index += n_recv;
		}

		//
		// Complete the reads issued above
		//

	//	MPI_Status status;
	//	for (i = 0; i < num_neighbors; i++) {
		//	if ( MPI_Wait(request+i, &status) )
		//	{
		//		cerr << "MPI_Wait error\n"<<endl;
		//		exit(-1);
		//	}
		//}


		return;
	}

	


}
