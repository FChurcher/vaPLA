package model.hasse;

import java.util.ArrayList;

import io.Writer;
import model.Sequence;

/**
 * The HasseGraph Class serves the functionality of storing HasseGraph-States with a reference to the initial State.
 * @author falco
 */
public class HasseGraph {
  // object definition
	/** stores all the States of this HasseGraph */
	private ArrayList<State> states;
	/** stores the initial State of this HasseGraph */
	private State initialState;
	/** stores ALL the sequences of the Alignment in an ordered Array */
	private Sequence[] sequences;
	
	/** genereates a new HasseGraph with the given parameters*/
	public HasseGraph(ArrayList<State> states, State initialState, Sequence[] sequences) {
		this.states = states;
		this.initialState = initialState;
		this.sequences = sequences;
	}
	
	/** @return a String representation of this object */
	@Override
	public String toString() {
		String s = "";
		s += "==========================================================\n";
		s += "HASSE \"GRAPH\":\n";
		s += "----------------------------------------------------------\n";
		for (State state : states) {
			s += state + "\n";
		}
		s += "----------------------------------------------------------\n";
		s += "==========================================================\n";
		return s;
	}
	
	public String toLongString() {
		String s = "";
		s += "==========================================================\n";
		s += "HASSE \"GRAPH\" (verbouse):\n";
		s += "----------------------------------------------------------\n";
		for (State state : states) {
			s += state + "\n";
			s += state.getScoreMatrix();
		}
		s += "----------------------------------------------------------\n";
		s += "==========================================================\n";
		return s;
	}
	
	/** @return the final State with the best (maximal) score (could be used for backtracking) */
	public State getMaxFinalState() {
		if (states.size() == 0) { return null; }
		State maxState = states.get(0);
		for (State state : states) {
			if (state.isFinalState() && (state.getScore(this.sequences) > maxState.getScore(sequences) 
				|| (state.getScore(this.sequences) == maxState.getScore(sequences) && state.getActive().size() > maxState.getActive().size())
				)) {
				maxState = state;
			}
		}
		return maxState;
	}
	
	/**
	 * looks for a state with the active sequences of the given state and creates a new state if none is found
	 * @param fullState - the given state
	 * @param indicesToCount - the indices to take into account
	 * @return a state with the active sequences of the given state
	 */
	public State getOrCreateStateByActiveSequences(State fullState, int[] indicesToCount) {
		int[] sequenceIndices = new int[indicesToCount.length];
		for (int i = 0; i < sequenceIndices.length; i++) {
			sequenceIndices[i] = fullState.getActive().get(indicesToCount[i]);				// mapping indicesToUse to SequenceIndex
		}
		
		for (State state : states) {
			if (state.getActive().size() != sequenceIndices.length) { continue; } 			// same indices ==> same lengths
			boolean containsEveryIndex = true;
			for (int index : sequenceIndices) {												// same legths ==> one way check is enough
				if (!state.getActive().contains(index)) {
					containsEveryIndex = false;
					break;
				}
			}
			if (containsEveryIndex && state.getReady().size() >= fullState.getReady().size()) {	// planestate ready can be bigger than fullstate ready since indieces not used are in planestate.ready
				for (int i = 0; i < fullState.getReady().size(); i++) {							// check for same ready set
					if ((int) state.getReady().get(i) != (int) fullState.getReady().get(i)) {
						containsEveryIndex = false;
						break;
					}
				}
				if (containsEveryIndex && state.getDone().size() == fullState.getDone().size()) {
					for (int i = 0; i < fullState.getDone().size(); i++) {						// check for same done set
						if ((int) state.getDone().get(i) != (int) fullState.getDone().get(i)) {
							containsEveryIndex = false;
							break;
						}
					}																			// no need to check if non-used active sequences are in planestate.ready, because they cant be anywhere else
				} else {
					containsEveryIndex = false;
				}
			} else {
				containsEveryIndex = false;
			}
			
			if (containsEveryIndex) {
				//System.out.println("OLD");
				return state;
			}
		}
		State state = new State(new ArrayList<Integer>(), new ArrayList<Integer>(), new ArrayList<Integer>(), false, false);
		for (int i = 0; i < sequenceIndices.length; i++) {
			state.addToActive(sequenceIndices[i]);
		}
		//System.out.println("NEW");
		return state;
	}

	public ArrayList<State> getStates() {
		return states;
	}

	public void setStates(ArrayList<State> states) {
		this.states = states;
	}

	public State getInitialState() {
		return initialState;
	}

	public void setInitialState(State initialState) {
		this.initialState = initialState;
	}

	public Sequence[] getSequences() {
		return sequences;
	}
}
