package System;

public class Atoms {
	Atom first;
	
	public Atoms() {
		first = null;
	}
	
	public void addAtom(String atom) {
		if (!atomAlreadyAdded(atom)){
			Atom next_atom = new Atom(atom);
			next_atom.next = first;
			first = next_atom;
		}
	}
	
	private boolean atomAlreadyAdded(String atom) {
		for (Atom current = first; current != null; current = current.next)
			if (current.atom.equals(atom))
				return true;
		return false;
	}
	
	public class Atom {
		public String atom;
		public Atom next;
		
		public Atom(String atom) {
			this.atom = atom;
			this.next = null;
		}
	}
}
