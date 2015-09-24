package System;

// keeps the starting state and ending state when an ( or [ or { is read
public class Stack {
	
	private Node first;
	
	public Stack() {
		this.first = null;
	}
	
	public void push(Node next) {
		next.next = this.first;
		this.first = next;
	}
	
	public Node pop() {
		Node return_node = first;
		first = first.next;
		
		return return_node;
	}
	
	public void print() {
		for(Node current = first; current != null; current = current.next)
			System.out.println("(" + current.startingState + ", " + current.endingState + ")");
	}
	
	public static class Node {
		private int startingState;
		private int endingState;
		private Node next;
		
		public Node(int start, int end) {
			this.startingState = start;
			this.endingState = end;
			this.next = null;
		}
		
		public int getStart() {
			return this.startingState;
		}
		
		public int getEnd() {
			return this.endingState;
		}
	}
}