package System;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

// This class keeps a list of reserved words, including the compiler and name of variables and labels.
public class ReservedWords {
	Word first;
	SubMachine firstSM; 
	
	public ReservedWords() {
		first = new Word("END");
		addWord("READ");
		addWord("PRINT");
		addWord("IF");
		addWord("THEN");
		addWord("ELSE");
		addWord("AND");
		addWord("OR");
		
		BufferedReader non_terminals;
		String line = "";
		try {
			non_terminals = new BufferedReader(new FileReader(new File("non_terminals.txt")));
			line = non_terminals.readLine();
			
			for (int i = 1; line != null; i++) {
				if (i == 1)
					firstSM = new SubMachine(line, i);
				else {
					SubMachine SM = new SubMachine(line, i);
					SM.next = firstSM;
					firstSM = SM;
				}
				
				addWord(line);
				line = non_terminals.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addWord(String strWord) {
		Word new_word = new Word(strWord);
		new_word.next = first;
		first = new_word;
	}
	
	// search if the words exists as a reserved word in the language or code.
	public boolean exists(String word) {
		for (Word current = first; current != null; current = current.next) 
			if (current.word.equals(word))
				return true;
		return false;
	}
	
	private class Word {
		String word;
		Word next;
		
		private Word(String word) {
			this.word = word;
			next = null;
		}
	}
	
	// this class has all sub-machines associated with each non-terminal of the grammar
	private class SubMachine {
		int subMachineNumber;
		String nonTerminal;
		SubMachine next;
		
		private SubMachine(String nonTerminal, int subMachine) {
			this.subMachineNumber = subMachine;
			this.nonTerminal = nonTerminal;
			this.next = null;
		}
	}
	
	// returns sub-machine number from its non-terminal name
	public int getSubMachine(String non_terminal) {
		for (SubMachine SM = firstSM; SM != null; SM = SM.next)
			if (SM.nonTerminal.equals(non_terminal))
				return SM.subMachineNumber;
		// error: no sub-machine with this non-terminal
		return -1;
	}
}
