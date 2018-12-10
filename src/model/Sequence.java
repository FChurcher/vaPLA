package model;

/**
 * the Sequence Class is to store and handle char-Sequences
 * @author falco
 */
public class Sequence {
  // object definition
	/** the name of this sequence */
	private String name;
	/** the sequence */
	private char[] sequence;
	/** true if this sequence is left global */
	private boolean globalLeft;
	/** true if this sequence is right global */
	private boolean globalRight;
	
	
	/**
	 * @param sequence - the sequence
	 * @param globalLeft - true if this sequence is left global
	 * @param globalRight - true if this sequence is right global
	 * @param name - the name of this sequence
	 */
	public Sequence(char[] sequence, boolean globalLeft, boolean globalRight, String name) {
		this.sequence = sequence;
		this.globalLeft = globalLeft;
		this.globalRight = globalRight;
		this.name = name;
	}
	
	/**
	 * @param sequence - the sequence
	 * @param globalLeft - true if this sequence is left global
	 * @param globalRight - true if this sequence is right global
	 */
	public Sequence(char[] sequence, boolean globalLeft, boolean globalRight) {
		this(sequence, globalLeft, globalRight, "not set");
	}
	
	/** @return a String representation of this object */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(name);
		stringBuilder.append(":\t");
		if (globalLeft) {
			stringBuilder.append("[");
		} else {
			stringBuilder.append("(");
		}
		stringBuilder.append(toCharArray());
		if (globalRight) {
			stringBuilder.append("]");
		} else {
			stringBuilder.append(")");
		}
		return stringBuilder.toString();
	}
	
	public char[] toCharArray() {
		return sequence;
	}

	/** @return the char at the given position */
	public char get(int i){
		return sequence[i];
	}
	
	/** sets the char at position i to c*/
	public void set(char c, int i){
		sequence[i] = c;
	}
	
	public int getLength(){
		return sequence.length;
	}

	public void setSequence(char[] sequence) {
		this.sequence = sequence;
	}

	public boolean isGlobalLeft() {
		return globalLeft;
	}

	public void setGlobalLeft(boolean globalLeft) {
		this.globalLeft = globalLeft;
	}

	public boolean isGlobalRight() {
		return globalRight;
	}

	public void setGlobalRight(boolean globalRight) {
		this.globalRight = globalRight;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
