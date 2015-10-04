package System;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import System.Atoms.Atom;
import System.State.LinkedState;
import System.Main;

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
	
	// generates text output in the expected format.
		public static void generateOutput() throws IOException {
			Writer writer;
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("machine.txt"), "utf-8"));
				writeStates(writer);
				writer.write("y\n");
				writeAtoms(writer);
				writeTransactions(writer);
				
				writer.close();
		}
		
		private static void writeStates(Writer writer) throws IOException {
			String states = "";
			for (State currentSt = first; currentSt != null; currentSt = currentSt.getNext())
				states = currentSt.getSubMachine() + "," + currentSt.getNumber() + " " + states;
			
			writer.write(states + "\n");
		}
		
		private static void writeAtoms(Writer writer) throws IOException {
			String atoms = "";
			for (Atom current = Main.vocabulary.first; current != null; current = current.next)
				atoms += current.atom + " ";
			
			writer.write(atoms + "\n");
		}
		
		private static void writeTransactions(Writer writer) throws IOException {
			String transaction = "";
			boolean writeReturnState = true;
			for (State currentSt = first; currentSt != null; currentSt = currentSt.getNext()) {
				System.out.println(currentSt.getSubMachine() +","+ currentSt.getNumber() + " " + String.valueOf(currentSt.finalState && writeReturnState));
				writeReturnState = true;
				for (LinkedState ls = currentSt.first; ls != null; ls = ls.nextLinkedState) 
					if (Main.reservedWords.getSubMachine(ls.lsTerm) != -1) { //check if the atom for transaction is a non-terminal
						transaction =  currentSt.getSubMachine() + "," + currentSt.getNumber() + " " + 
								Main.reservedWords.getSubMachine(ls.lsTerm) + " STACK " + 
								ls.lsSubmachine + "," + ls.lsNumber + "\n";
						writer.write(transaction);
					} else {
						transaction = currentSt.getSubMachine() + "," + currentSt.getNumber() + " " + 
								ls.lsTerm + " " + 
								ls.lsSubmachine + "," + ls.lsNumber + "\n";
						writer.write(transaction);
					}
				
				if (currentSt.getNumber() == 1 && writeReturnState) {
					transaction = currentSt.getSubMachine() + "," + currentSt.getNumber() + " RETURN\n";
					writer.write(transaction);
					writeReturnState = false;
				}
			}
		}
}
