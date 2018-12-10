package model.hasse;

import java.util.ArrayList;
import java.util.Collections;

import model.IndexVector;
import model.Matrix;
import model.Sequence;

/**
 * The State Class serves of states containing lists of ready, active and done sequences.
 * It also contains a static method to connect two states.
 * @author falco
 */
public class State {
  // static definition
	/** connects the previousState to the followingState and vice versa, if its a valid connection in terms of State changes of the Sequences */
	public static void connect(State previousState, State followingState) {
		// fast check (necessary)
		if (previousState.equals(followingState) || 
			previousState.ready.size() < followingState.ready.size() || 
			previousState.done.size() > followingState.done.size()) {
			return;
		}
		// check (sufficient)
		// check for invalid done -> active/ready changes
		for (Integer prevDoneIndex : previousState.done) {
			if (followingState.active.contains(prevDoneIndex) || followingState.ready.contains(prevDoneIndex)) {
				return;
			}
		}
		// check for invalid active -> ready changes 
		for (Integer prevActiveIndex : previousState.active) {
			if (followingState.ready.contains(prevActiveIndex)) {
				return;
			}
		}
		// ==> valid connection
		previousState.addFollowingState(followingState);
		followingState.addPreviousState(previousState);
		
		// check immediate connection
		int changesCount = 0;
		for (Integer prevReadyIndex : previousState.ready) {
			if (!followingState.ready.contains(prevReadyIndex)) {
				if (followingState.done.contains(prevReadyIndex)) {
					return;		// change from ready to done => 2 changes
				}
				changesCount++;
			}
		}
		
		for (Integer prevActiveIndex : previousState.active) {
			if (!followingState.active.contains(prevActiveIndex)) {
				changesCount++;
			}
		}
		
		if (changesCount == 1) {
			previousState.addDirectlyFollowingState(followingState);
			followingState.addDirectlyPreviousState(previousState);
		}
		
	}
	
	
  // object definition
	private ArrayList<Integer> ready, active, done;
	private ArrayList<State> previous, following, directlyPrevios, directlyFollowing;
	private boolean initialState, finalState;
	private Matrix scoreMatrix;
	private IndexVector maxScoreIndices;
	private Boolean fullyRightLocal;
	
	
	/** genereates a new empty state */
	public State() {
		this.ready = new ArrayList<>();
		this.active = new ArrayList<>();
		this.done = new ArrayList<>();
		this.previous = new ArrayList<>();
		this.following = new ArrayList<>();
		this.directlyPrevios = new ArrayList<>();
		this.directlyFollowing = new ArrayList<>();
		this.initialState = false;
		this.finalState = false;
	}
	
	/** genereates a new state with the given parameters */
	public State(ArrayList<Integer> ready, ArrayList<Integer> active, ArrayList<Integer> done, boolean initialState, boolean finalState) {
		this();
		this.ready = ready;
		this.active = active;
		this.done = done;
		this.initialState = initialState;
		this.finalState = finalState;
	}
	
	/** genereates a new state with the given parameters */
	public State(ArrayList<Integer> ready, ArrayList<Integer> active, ArrayList<Integer> done, ArrayList<State> previous, ArrayList<State> following, ArrayList<State> directlyPrevios, ArrayList<State> directlyFollowing, boolean initialState, boolean finalState) {
		this();
		this.ready = ready;
		this.active = active;
		this.done = done;
		this.previous = previous;
		this.following = following;
		this.directlyPrevios = directlyPrevios;
		this.directlyFollowing = directlyFollowing;
		this.initialState = initialState;
		this.finalState = finalState;
	}
	
	/** @return a String representation of this object */
	@Override
	public String toString() {
		String s = toShortString() + "  \t directly previous: [";
		for (int i = 0; i < directlyPrevios.size(); i++) {
			s += directlyPrevios.get(i).toShortString();
			if (i <= directlyPrevios.size()-2) { s += "; "; }
		}
		s += "]";
		
		s += "  \t trans previous: [";
		for (int i = 0; i < previous.size(); i++) {
			s += previous.get(i).toShortString();
			if (i <= previous.size()-2) { s += "; "; }
		}
		s += "]";
		return s;
	}

	/** @return a short String representation of this object */
	public String toShortString() {
		String s = "(";
		if (initialState) { s += "I "; }
		if (finalState) { s += "F "; }
		s += "{";
		for (int i = 0; i < ready.size(); i++) {
			s += ready.get(i);
			if (i < ready.size()-1) { s += ","; }
		}
		s += "}, {";
		for (int i = 0; i < active.size(); i++) {
			s += active.get(i);
			if (i < active.size()-1) { s += ","; }
		}
		s += "}, {";
		for (int i = 0; i < done.size(); i++) {
			s += done.get(i);
			if (i < done.size()-1) { s += ","; }
		}
		s += "})";
		return s;
	}
	
	/** @return a short String representation of this object */
	public String toGraphString() {
		String s = "";
		for (int i = 0; i < ready.size(); i++) {
			s += ready.get(i);
			if (i < ready.size()-1) { s += ""; }
		}
		s += "_";
		for (int i = 0; i < active.size(); i++) {
			s += active.get(i);
			if (i < active.size()-1) { s += ""; }
		}
		s += "_";
		for (int i = 0; i < done.size(); i++) {
			s += done.get(i);
			if (i < done.size()-1) { s += ""; }
		}
		return s;
	}

	/** @return a new State with the same ready, active and done lists, but empty previous and following lists and initialState = false and finalState = false */
	@Override
	public State clone() {
		State s = new State(
				new ArrayList<Integer>(this.ready), new ArrayList<Integer>(this.active), new ArrayList<Integer>(this.done), // === (?) (ArrayList<Integer>) this.ready.clone(), (ArrayList<Integer>) this.active.clone(), (ArrayList<Integer>) this.done.clone()
				new ArrayList<State>(), new ArrayList<State>(), new ArrayList<State>(), new ArrayList<State>(),
				false, false
		);
		return s;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		} 
		State state = (State) obj;
		if (!(state.getReady().size() == this.getReady().size()) || !(state.getActive().size() == this.getActive().size()) || !(state.getDone().size() == this.getDone().size())) {
			return false;
		}
		for (int i = 0; i < state.getReady().size(); i++) {
			if ((int) state.getReady().get(i) != (int) this.getReady().get(i)) {
				return false;
			}
		}
		for (int i = 0; i < state.getActive().size(); i++) {
			if ((int) state.getActive().get(i) != (int) this.getActive().get(i)) {
				return false;
			}
		}
		for (int i = 0; i < state.getDone().size(); i++) {
			if ((int) state.getDone().get(i) != (int) this.getDone().get(i)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * returns right bottom score, if at least one sequence is right global, return maxScore else 
	 * @return the percise score of this Matrix
	 */
	public float getScore(Sequence[] sequences) {
		if (fullyRightLocal == null) {
			fullyRightLocal = true;									// does this state contain right local sequences only?
			for (int sequenceIndex : this.getActive()) {
				if (sequences[sequenceIndex].isGlobalRight()) {					// if one is right global => no
					fullyRightLocal = false;
					break;
				}
			}	
		}
		float score;
		if (fullyRightLocal) {
			score = this.getScoreMatrix().getMaxScore();					// if all the sequences are right local => take max score over whole matrix
		} else {
			score = this.getScoreMatrix().getScore();						// if one sequence is right global => take the score as usual
		}
		return score;
	}

	/**
	 * return a previous state with the given active sequences
	 * @param indices - the active sequence indices
	 * @return th e corresponding previous state
	 */
	public State getPreviousStateWithActiveSequences(int[] indices) {
		for (State previousState : previous) {
			if (previousState.getActive().size() != indices.length) { continue; } 		// same indices ==> same lengths
			boolean containsEveryIndex = true;
			for (int index : indices) {													// same legths ==> one way check is enough
				if (!previousState.getActive().contains(index)) {
					containsEveryIndex = false;
					break;
				}
			}
			if (containsEveryIndex) {
				return previousState;
			}
		}
		return this;
	}
	
	/**
	 * orders the indices from low to high
	 */
	public void order() {
		Collections.sort(ready);
		Collections.sort(active);
		Collections.sort(done);
	}

	/**
	 * to check if this state is a doubled state
	 * (we only need the set of all ordered states)
	 * @return true if all stets of this state are ordered
	 */
	public boolean isOrdered() {
		if (this.ready.size() > 1) {
			for (int i = 1; i < this.ready.size(); i++) {
				if (this.ready.get(i-1) > this.ready.get(i)) {
					return false;
				}
			}
		}
		if (this.active.size() > 1) {
			for (int i = 1; i < this.active.size(); i++) {
				if (this.active.get(i-1) > this.active.get(i)) {
					return false;
				}
			}
		}
		if (this.done.size() > 1) {
			for (int i = 1; i < this.done.size(); i++) {
				if (this.done.get(i-1) > this.done.get(i)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isInitialState() {
		return initialState;
	}

	public boolean isFinalState() {
		return finalState;
	}

	/**
	 * pushes the index i to done and removes it from active
	 * return true if i was found, false if i was not found
	 * if i was not found, this state stays unchanged
	 * @return true if the Sequence was finished; false otherwise
	 */
	public boolean finish(Integer i) {
		boolean finished = this.active.remove(new Integer(i));
		if (finished) {
			this.done.add(new Integer(i));
		}
		return finished;
	}
	
	/**
	 * pushes the index i to active and removes it from ready
	 * return true if i was found, false if i was not found
	 * if i was not found, this state stays unchanged
	 * @return true if the Sequence was activated; false otherwise
	 */
	public boolean activate(Integer i) {
		boolean activeted = this.ready.remove(new Integer(i));
		if (activeted) {
			this.active.add(new Integer(i));
		}
		return activeted;
	}
	
	/** adds the given state to the list of previous states of this state */
	public void addPreviousState(State previousState) {
		if (!previous.contains(previousState)) {
			this.previous.add(previousState);	
		}
	}
	
	/** adds the given state to the list of following states of this state */
	public void addFollowingState(State followingState) {
		if (!following.contains(followingState)) {
			this.following.add(followingState);	
		}
	}
	
	/** adds the given state to the list of previous states of this state */
	public void addDirectlyPreviousState(State previousState) {
		if (!directlyPrevios.contains(previousState)) {
			this.directlyPrevios.add(previousState);	
		}
	}
	
	/** adds the given state to the list of following states of this state */
	public void addDirectlyFollowingState(State followingState) {
		if (!directlyFollowing.contains(followingState)) {
			this.directlyFollowing.add(followingState);	
		}
	}
	
	/** adds the given index of a sequence to the ready set of this state */
	public void addToReady(Integer i) {
		this.ready.add(i);
	}
	
	/** adds the given index of a sequence to the active set of this state */
	public void addToActive(Integer i) {
		this.active.add(i);
	}
	
	/** adds the given index of a sequence to the done set of this state */
	public void addToDone(Integer i) {
		this.done.add(i);
	}
	
	/**
	 * computes the max score of the matrix if it wasnt computet allready and can just be reused
	 * @return the maxScore
	 */
	public IndexVector getMaxScoreIndices() {
		if (maxScoreIndices == null) {
			maxScoreIndices = scoreMatrix.getMaxScoreIndices();
		}
		return maxScoreIndices;
	}

	public ArrayList<Integer> getReady() {
		return ready;
	}

	public ArrayList<Integer> getActive() {
		return active;
	}

	public ArrayList<Integer> getDone() {
		return done;
	}

	public ArrayList<State> getPrevious() {
		return previous;
	}

	public ArrayList<State> getFollowing() {
		return following;
	}
	
	public ArrayList<State> getDirectlyPrevious() {
		return directlyPrevios;
	}

	public ArrayList<State> getDirectlyFollowing() {
		return directlyFollowing;
	}

	/**
	 * @return the scoreMatrix
	 */
	public Matrix getScoreMatrix() {
		return scoreMatrix;
	}

	/**
	 * @param scoreMatrix the scoreMatrix to set
	 */
	public void setScoreMatrix(Matrix scoreMatrix) {
		this.scoreMatrix = scoreMatrix;
	}

	public void setInitialState(boolean initialState) {
		this.initialState = initialState;
	}


	public void setFinalState(boolean finalState) {
		this.finalState = finalState;
	}

	/**
	 * @return false if at least one sequence is global right, true otherwise
	 */
	public boolean isFullyRightLocal() {
		return fullyRightLocal;
	}
	
}
