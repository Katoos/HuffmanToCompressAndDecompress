package com.company;

import java.util.Comparator;

@SuppressWarnings({ "unused", "rawtypes" })
public class Node implements Comparable {
	Node left;
	Node right;
	int freq;
	String string;
	
	public Node(){
		this.right = null;
		this.left = null;
	}
	
	public Node(String string, int freq) {
		this.string = string;
		this.freq = freq;
		this.right = null;
		this.left = null;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
	
	@Override
	public int compareTo(Object o) {
		return this.freq - ((Node)(o)).freq;
	}
}
