package System;

import System.State.LinkedState;

// This class has all states of the automaton in a linked list of States
public class Automaton {
	private static State first;
	
	public Automaton() {
		first = null;
	}
	 
	public State addState(State state) {
		state.setNext(first);
		first = state;
		
		return first;
	}
	
	public static State getState(int number, int subMachine) {
		for(State current = first; 
				current != null; 
				current = current.getNext())
			if (current.getNumber() == number && 
				current.getSubMachine() == subMachine) 
				return current;
		
		return null;
	}
	
	// print states and transitions
	public void print() {
		for (State current = first; current != null; current = current.getNext())
			current.print();
	}
	
	// remove all empty transactions. 
	public void removeIndeterminacies() {
		State currentSt = first;
		while (currentSt != null) {
			State nextState = currentSt.getNext();
			
			for (LinkedState currentLs = currentSt.first; 
					currentLs != null; currentLs = currentLs.nextLinkedState) {
				if (currentLs.lsTerm.equals("_")) {
					// gets the state pointed by this, in the same sub-machine
					int number = currentLs.lsNumber;
					int submachine = currentSt.getSubMachine();
					State pointedState = getState(number, submachine);
					
					int removedStateNumber = currentSt.getNumber();
					// removes current state from the automaton
					excludeState(removedStateNumber, submachine, number);
					
					// if previous state was final, the merged one is also final
					if (removedStateNumber == 1){
						pointedState.setNumber(1);
						pointedState.finalState = true;
					}
					
					// if previous one was initial, the merged one is also initial
					// if previous was new one must be final and initial, number==0 and finalState==true
					if (removedStateNumber == 0) {
						removedStateNumber = pointedState.getNumber();
						number = 0;
						pointedState.setNumber(0);
					}
					
					// gets all stated pointing to removed state and points to pointedState
					changeAllPointers(removedStateNumber, number, submachine);
					
					System.out.println("____________________________________________");
					print();
				}
					
			}
			currentSt = nextState;
		}
	}

	// gets all states that points to removedStateNumber and points is to number 
	private void changeAllPointers(int removedStateNumber, int number, int subMachine) {
		for (State currentSt = first; currentSt != null; currentSt = currentSt.getNext()) {
			for (LinkedState currentLs = currentSt.first; 
					currentLs != null; currentLs = currentLs.nextLinkedState) {
				if (currentLs.lsNumber == removedStateNumber && currentLs.lsSubmachine == subMachine)
					currentLs.lsNumber = number;
			}
		}
	}
	
	// remove removedStateNumber from automaton
	private void excludeState(int removedStateNumber, int subMachine, int number) {
		// before removing, all the connections need to be transfered to stated merged
		State removedState = getState(removedStateNumber, subMachine);
		State targetState = getState(number, subMachine);
		
		for (LinkedState ls = removedState.first; ls != null; ls = ls.nextLinkedState) {
			if (ls.lsTerm != "_")
				targetState.addLinkedState(ls.lsNumber, ls.lsTerm);
		}
		
		if (first.getNumber() == removedStateNumber && 
				first.getSubMachine() == subMachine) {
			first = first.getNext();
			return;
		}
		
		for (State currentSt = first; currentSt != null; currentSt = currentSt.getNext()) {
			if (currentSt.getNext() == null) return;
			if (currentSt.getNext().getNumber() == removedStateNumber
					&& currentSt.getNext().getSubMachine() == subMachine){
				State nextState = currentSt.getNext().getNext();
				currentSt.setNext(nextState);
			}
		}
	}
}
