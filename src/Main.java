import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import System.Automaton;
import System.Stack;
import System.Stack.Node;
import System.State;
import System.ReservedWords;

public abstract class Main {
	
	public static String expression; //whole expression in Wirth's notation.
	public static char term; 	//each term, including terminal, non-terminal and signals.
	public static int counter;	//increments every terminal, non-terminal and opening brackets.
	public static int stateNumber;	//keeps the current state number.
	public static int subMachineNumber; //keeps the current sub-machine number.
	public static Stack stack;
	public static Automaton automaton;
	public static ReservedWords reservedWords;
	
	public static void main(String[] args) throws IOException {
		//initialize variables. 
		stack = new Stack();
		automaton = new Automaton();
		counter = 0;
		stateNumber = 0;
		subMachineNumber = 1;
		reservedWords = new ReservedWords();
		
		//first state with number = 0 and sub-machine = 1 and status -1 (initial state).
		automaton.addState(new State(counter, subMachineNumber));
		
		//read expression.
		BufferedReader bf = new BufferedReader(new FileReader(new File("grammar.txt")));
		expression = bf.readLine();
		
		while (expression != null) {
			analyze();
			expression = bf.readLine();
		}
		
		bf.close();
		
		System.out.println("____________________________________________");
		automaton.print();
		
		automaton.removeIndeterminacies();
		
		automaton.print();
	}
	
	private static void analyze() {
		// main loop to analyze each term of the expression.
			stack.print();
			
			String[] atoms = expression.split("\\s+");
			
			// set numbers
			subMachineNumber = reservedWords.getSubMachine(atoms[0]);
			stateNumber = counter = 0;
			
			
			for (String atom : atoms ) {
			
				System.out.println("cont = " + counter + "; nState = " + 
				subMachineNumber + "," + stateNumber + "; atom = \"" + atom + "\"");
				
				switch (atom) {
				case "=":
					stack.push(new Stack.Node(stateNumber, counter));
					counter++;
					break;
				case "(":
					stack.push(new Stack.Node(stateNumber, counter));
					counter++;
					break;
				case ")":
					closingBrackets();
					break;
						
				case "[":
					// adds new return state to stack
					stack.push(new Stack.Node(stateNumber, counter));
					
					// [] accepts empty transaction, so add it
					
					// creates an State with stateNumber, if doesn't exist
					if(Automaton.getState(stateNumber, subMachineNumber) == null) {
						// creates new State and adds to Automaton.
						automaton.addState(new State(stateNumber, subMachineNumber));						
					}
					
					// now, add it
					Automaton.getState(stateNumber, subMachineNumber).
					addLinkedState(counter, "_");
					
					counter++;
					break;
					
				case "]":
					closingBrackets();
					break;
				
				case "{":
					// add to stack, ending and starting number are the same in this case.
					stack.push(new Node(counter, counter));
					
					// {} accepts empty transaction, so add it				
					// creates an State with stateNumber, if doesn't exist
					if(Automaton.getState(stateNumber, subMachineNumber) == null) {
						// creates new State and adds to Automaton.
						automaton.addState(new State(stateNumber, subMachineNumber));						
					}
					
					// now, add it
					Automaton.getState(stateNumber, subMachineNumber).
						addLinkedState(counter, "_");
					
					stateNumber = counter;
					counter++;
					break;
				
				case "}":
					closingBrackets();
					break;
					
				case ".":
					// get return state number.
					Node last_node = stack.pop();
					// search this state and 
					// add empty connection pointing to last state.
					Automaton.getState(stateNumber, subMachineNumber)
						.addLinkedState(last_node
							.getEnd(), "_");
					
					//create final state.
					automaton.addState(new State(last_node.getEnd(), subMachineNumber));
					break;
					
				case "|":
					int endingStateNumber = stack.peek().getEnd();
					
					// gets state from current state number
					State endingState = Automaton.getState(endingStateNumber, subMachineNumber);
					if(endingState == null) {
						// creates new State and adds to Automaton.
						endingState = automaton.addState(new State(endingStateNumber, 
								subMachineNumber));						
					}
					
					// adds an empty transaction from this state
					Automaton.getState(stateNumber, subMachineNumber)
						.addLinkedState(endingStateNumber, "_");
					
					stateNumber = stack.peek().getStart(); 
					break;
				
				default:
					// check if state already exists
					State currentState = Automaton.getState(stateNumber, subMachineNumber);
					if (currentState == null) {
						// creates new State and adds to Automaton.
						if (counter == 0)
							currentState = automaton.addState(
									new State(stateNumber, subMachineNumber));
						else 
							currentState = automaton.addState(
									new State(stateNumber, subMachineNumber));
					}
					
					// sets that states's next = new inserted state.
					if (counter != 0) {
						// creates State that this is linked to.
						if(Automaton.getState(counter, subMachineNumber) == null)
							automaton.addState(new State(counter, subMachineNumber));
						currentState.addLinkedState(counter, atom);
						stateNumber = counter;
					}
					counter++;
					break;
				}
			}
	}
	
	private static void closingBrackets() {
		// get return state number.
		Node top = stack.pop();
		// creates State with ending number.
		if(Automaton.getState(top.getEnd(), subMachineNumber) == null)
			automaton.addState(new State(top.getEnd(), subMachineNumber));
		// link this state with the ending state above. 
		Automaton.getState(stateNumber, subMachineNumber).addLinkedState(top.getEnd(), "_");
		// sets new state number.
		stateNumber = top.getEnd();
	}
}
