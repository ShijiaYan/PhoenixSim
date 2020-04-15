package edu.columbia.ke.generic.dataStucture;

public class AVLTreeTestMain {

	public AVLTreeTestMain() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AvlTreeRm t = new AvlTreeRm();
		for (int i = 1; i <= 4; i++){
			t.insert(i);
			t.insert(10-i);
		}
		//t.debug(t.root);
		
		int k = 5;
		int r = t.removeAndRank(k);
		//t.debug(t.root);
		
		System.out
		.println(r + " elements > " + k);
	}

}
