package edu.columbia.ke.spinet.variant;

public enum PriorityMode {
	OBLIVIOUS, 
	EDF, 
	EDF_RETRAN_YIELD, 
	EDF_RETRAN_WAIT, 
	EDF_RETRAN_DEADLINE_AWARE_BACKOFF,
	EDF_RETRAN_WAIT_TRUNCATED, 
	EDF_RETRAN_WAIT_TRUNCATED_SMARTER, 
	EDF_MILD,
	EDF_DIDD,
	EDF_OPTIMAL_WAIT, 
	SMARTER_OPTIMAL_WAIT, 
	RANDOM_SMARTER_OPTIMAL_WAIT, 
	LEAST_WAIT_TIME,
	NQPrioritized, /* feasible EDF */
	NQPrioritized_variant1, /* conditional EDFI */
	NQPrioritized_variant2,
	NQPrioritized_variant3,
	NQPrioritized_variant4,
	NQPrioritized_ExpScoreAndProb,
	NQPrioritized_OCF,
	Synchronized,
	Slotted_ALOHA
}
