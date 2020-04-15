package edu.columbia.ke.generic.dataStucture;

/** Here is the AVL-Node class for Completenesse **/
public class CopyOfAvlNode {
	public CopyOfAvlNode left;
	public CopyOfAvlNode right;
	public CopyOfAvlNode parent;
	public int key;
	public int balance;

	public CopyOfAvlNode(int k) {
		left = right = parent = null;
		balance = 0;
		key = k;
	}

	public String toString() {
		return "" + key;
	}
}
