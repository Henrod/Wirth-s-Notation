import java.util.Scanner;

import System.Automaton;
import System.Stack;
import System.Stack.Node;
import System.State;

public abstract class Main {
	
	public static String expression; //whole expression in Wirth's notation.
	public static char term; 	//each term, including terminal, non-terminal and signals.
	public static int counter;	//increments every terminal, non-terminal and opening brackets.
	public static int stateNumber;	//keeps the current state number.
	public static Stack stack;
	public static Automaton automaton;
	
	public static void main(String[] args) {
		//initialize variables. 
		stack = new Stack();
		automaton = new Automaton();
		counter = 0;
		stateNumber = 0;
		
		//first state with number = 0.
		automaton.addState(new State(counter));
		counter++;
		
		//read expression.
		Scanner scanner = new Scanner(System.in);
		System.out.print("Insert expression: ");
		
		expression = scanner.nextLine();
		scanner.close();
		
		analyze();
		automaton.print();
	}
	
	private static void analyze() {
		// main loop to analyze each term of the expression.
		for (int index = 0; index < expression.length(); index++) {
			stack.print();
			term = expression.charAt(index);
			
			System.out.println("cont = " + counter + "; nState = " + stateNumber + "; term = " + term);
			
			switch (term) {
			case '=':
				stack.push(new Stack.Node(stateNumber, counter));
				counter++;
				break;
			case '(':
				stack.push(new Stack.Node(stateNumber, counter));
				counter++;
				break;
			case ')':
				// get return state number.
				Node top = stack.pop();
				// creates State with ending number.
				automaton.addState(new State(top.getEnd()));
				// link this state with the ending state above. 
				automaton.getState(stateNumber).addLinkedState(top.getEnd(), '_');
				// sets new state number.
				stateNumber = top.getEnd();
				break;
				
			case '.':
				// get return state number.
				Node last_node = stack.pop();
				// search this state and 
				// add empty connection pointing to last state.
				automaton.getState(stateNumber)
					.addLinkedState(last_node
						.getEnd(), '_');
				break;
			
			default:
				// Non-terminal
				if (term >= 'A' && term <= 'Z') {
					// creates new State and adds to Automaton.
					automaton.addState(new State(counter));
				} //terminal.
				else if (term >= 'a' && term <= 'z') {
					// check if state already exists
					State currentState = automaton.getState(stateNumber);
					if(currentState == null) {
						// creates new State and adds to Automaton.
						currentState = automaton.addState(new State(stateNumber));						
					}
					
					// creates State that this is linked to.
					automaton.addState(new State(counter));
					
					// sets that states's next = new inserted state.
					currentState.addLinkedState(counter, term);
					stateNumber = counter;
					counter++;
				}
				break;
			}
		}
		
		
	}

}
