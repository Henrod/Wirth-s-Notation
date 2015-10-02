package System;

// This class is one State of the Automaton
public class State {
	
	//FIXED: 	state number = 1 -> state is final
	//			state number = 0 -> state is initial
	//			state number = 0 and finalState == true -> state is initial and final 
	
	private State next;		//for the list of state in the Automaton.
	private int number;		//number of this state.
	private int subMachine;	//number of the sub-machine
	protected LinkedState first;	//first connected states with this State
								//to build the linked list.
	protected boolean finalState;	//tells if this state is final
	
	// constructor.
	public State(int number, int subMachine) {
		this.number = number;
		this.subMachine = subMachine;
		this.first = null;
		
		if (number == 1) finalState = true;
		else finalState = false;
	}
	
	// state which this State has connections.
	protected class LinkedState {
		protected int lsNumber;	//number of the connected state.
		protected int lsSubmachine;	//number of state's sub-machine
		protected String lsTerm;	//vocabulary that triggers the transaction.
		protected LinkedState nextLinkedState; //next state of the linked list.
		
		public LinkedState(int number, String term, int submachine) {
			this.lsNumber = number;
			this.lsSubmachine = submachine;
			this.lsTerm = term;
			nextLinkedState = null;
		}
	}
	
	//add an state which this has transaction
	public void addLinkedState(int number, String term) {
		LinkedState ls = new LinkedState(number, term, subMachine);
		ls.nextLinkedState = first;
		first = ls;
	}
	
	public State getNext() {
		return this.next;
	}
	
	public void setNext(State state) {
		this.next = state;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getSubMachine() {
		return this.subMachine;
	}
	
	//print all transactions from this state
	public void print() {
		for (LinkedState current = first;
				current != null; 
				current = current.nextLinkedState) {
			System.out.print("Current: " + subMachine + "," + number +
					" gets \"" + current.lsTerm + "\" goes to " 
					+ current.lsSubmachine + "," + current.lsNumber);
			
			switch (current.lsNumber) {
			case 0:
				if (Automaton.getState(current.lsNumber, current.lsSubmachine).finalState)
					System.out.println(" Status: final");
				else
					System.out.println(" Status: initial");
				break;
			case 1: 
				System.out.println(" Status: final");
				break;
			default:
				System.out.println(" Status: normal");
				break;
			}
		}
	}
}
