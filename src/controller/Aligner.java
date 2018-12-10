package controller;

import java.util.ArrayList;
import java.util.Collections;

import io.Writer;
import model.Alignment;
import model.IndexVector;
import model.Matrix;
import model.Sequence;
import model.hasse.HasseGraph;
import model.hasse.State;
import ui.TimeStampMaganer;

/**
 * The Aligner Class is a singleton class containing methods to compute Alignments
 * @author Falco, inspired by Alignments/src/algorithms/GotohBigram.java written by Nancy
 */
public class Aligner {
  // singleton intrinsic
	/** the singleton instance */
	protected static Aligner instance;
	
	/**
	 * singleton get instance method
	 * @return an instance of this class
	 */
	public static Aligner getInstance(){
		if (instance == null) {
			instance = new Aligner();
		}
		return instance;
	}
	
  // object definition	
	/**
	 * aligns the given Sequences
	 * @return the Alignment of the given Sequences
	 */
	public Alignment align(Sequence... sequences){
		TimeStampMaganer.getInstance().printTimeStamp("generating Hassegraph... ");
		HasseGraph hasseGraph = generateHasseGraph(sequences);
		TimeStampMaganer.getInstance().printTimeStamp("computing " + hasseGraph.getStates().size() + " matrices... ");
		compute(hasseGraph);
		TimeStampMaganer.getInstance().printTimeStamp("backtracking... ");
		return new Alignment(backtrack(hasseGraph), hasseGraph);
	}
	
	/** generates a HasseDiagram from a given set of Sequences */
	public HasseGraph generateHasseGraph(Sequence... sequences) {
		// to save all the states
		ArrayList<State> states = new ArrayList<>();
		// to save the indices of global left and right sequences
		    // every sequence which is global left but not global right
		ArrayList<Integer> finishable = new ArrayList<>();
		    // every sequence which is flobal right (it does not matter if its globalLeft since it wont be activated when it is active allready)
		ArrayList<Integer> activatable = new ArrayList<>();
		// if one or more Sequences is neither globalLeft nor globalRight, it is calles local and totallyLocal is then set to true
		boolean totallyLocal = false;
		
		State startState = new State();
		startState.setInitialState(true);
	
		// generating startState and filling globalLeft and globalRight index-Lists
		for (int i = 0; i < sequences.length; i++) {
			if (sequences[i].isGlobalLeft()) { startState.addToActive(i); } 
			else { startState.addToReady(i); }
			
			// globalRight States have to active in terminal State; globalLeft States have to be active in the initial State
			if (!sequences[i].isGlobalRight()) {
				finishable.add(new Integer(i));
			}
			
			if (!sequences[i].isGlobalLeft()) {
				activatable.add(new Integer(i));
				// if one or more Sequences i neither globalLeft nor globalRight, it is calles local and totallyLocal is then set to true
				if (!sequences[i].isGlobalRight()) {
					totallyLocal = true;
				}
			}
		}
		states.add(startState);
		
		// generate States and connections
		ArrayList<State> oldStates = new ArrayList<>();
		
		//System.out.println(activatable.size());
		oldStates.add(startState);
		System.out.println("initial:");
		for (State state : oldStates) {
			System.out.println(state);
		}
		
		if (!totallyLocal) {										// if there is a totally local Sequence test finishing last
			finish(oldStates, states, finishable);	
		}
		states = new ArrayList<>(oldStates);
		activate(oldStates, states, activatable);
	
		if (totallyLocal) {											// if there is a totally local Sequence test finishing last
			states = new ArrayList<>(oldStates);					// look at all the previously gerneratet states to finish them
			finish(oldStates, states, finishable);
		}
		
		markFinalStates(oldStates, sequences);
		
		return new HasseGraph(oldStates, startState, sequences);
	}

	/**
	 * activating all the finishable sequences pseudorecursively (used by generateHasseDiagram)
	 * @param oldStates - list that shall hold all the obtained states
	 * @param states - the States list for the actual iteration
	 * @param newStates - the States list for the obtained States from the action iteration
	 * @param finishable - a list of indices of activable Sequences
	 */
	private void activate(ArrayList<State> oldStates, ArrayList<State> states, ArrayList<Integer> activatable) {
		ArrayList<State> newStates = new ArrayList<>();
		while (true) {
			for (Integer i : activatable) {
				for (int j = 0; j < states.size(); j++) {					// activate all the left local sequences in each state
					State newState = (State) states.get(j).clone();
					if (newState.activate(i)) {
						newState.order();									// put the indexlists in ascending order
						if (!newStates.contains(newState)) { 
							newStates.add(newState);
						}
					}
				}
			}
			if (newStates.size() == 0) {
				break;
			}
			System.out.println("activating:");
			for (State state : newStates) {
				System.out.println(state);
			}
			for (State newState : newStates) {								// connecting all old states to the new states
				for (State oldstate : oldStates) {							// connect old to new (if valid)
					State.connect(oldstate, newState);
				}
				for (State newState2 : newStates) {							// connect new to new (if valid)
					State.connect(newState, newState2);
				}
			}	
			oldStates.addAll(newStates);
			states = newStates;
			newStates = new ArrayList<>();
		}
	}

	/**
	 * finishing all the finishable sequences pseudorecursively (used by generateHasseDiagram)
	 * @param oldStates - list that shall hold all the obtained states
	 * @param states - the States list for the actual iteration
	 * @param newStates - the States list for the obtained States from the action iteration
	 * @param finishable - a list of indices of finishable Sequences
	 */
	private void finish(ArrayList<State> oldStates, ArrayList<State> states, ArrayList<Integer> finishable) {
		ArrayList<State> newStates = new ArrayList<>();
		while (true) {
			for (Integer i : finishable) {
				for (int j = 0; j < states.size(); j++) {					// finish all the right global sequences in each state
					State newState = (State) states.get(j).clone();
					if (newState.finish(i)) {
						if (newState.isOrdered()) {
							newStates.add(newState);
						}
					}
				}
			}
			if (newStates.size() == 0) {
				break;
			}
			System.out.println("finishing:");
			for (State state : newStates) {
				System.out.println(state);
			}
			for (State newstate : newStates) {								// connecting all old states to the new states
				for (State oldstate : oldStates) {							// connect old to new (if valid)
					State.connect(oldstate, newstate);
				}
				for (State newState2 : newStates) {							// connect new to new (if valid)
					State.connect(newstate, newState2);
				}
			}
			oldStates.addAll(newStates);
			states = newStates;
			newStates = new ArrayList<>();
		}
	}
		
	/**
	 * identifies THE final state
	 * @param states
	 * @param sequences
	 */
	private void markFinalStates(ArrayList<State> states, Sequence[] sequences) {
		boolean thereIsAtLeastOneGlobalRightSequence = false;
		for (int i = 0; i < sequences.length; i++) {									// check if there is any rightGlobal sequence
			if (sequences[i].isGlobalRight()) {
				thereIsAtLeastOneGlobalRightSequence = true;
			}
		}
		
		for (State state : states) {
			
			if (!thereIsAtLeastOneGlobalRightSequence) {
				state.setFinalState(true);
				continue;
			}
			
			boolean isFinal = true;
			for (int i = 0; i < sequences.length; i++) {
				if (sequences[i].isGlobalRight() && !state.getActive().contains(i)) {
					isFinal = false;
					break;																// if one globalRight sequence is not contained in state.getActive() => jump to next state
				}
				if (!sequences[i].isGlobalRight() && state.getActive().contains(i)) {
					isFinal = false;
					break;																// if one non-globalRight sequence is contained in state.getActive() => jump to next state
				}
				if (state.getReady().size() > 0) {
					isFinal = false;													// only consider "last" states as final
					break;
				}
			} 
			state.setFinalState(isFinal);
		}
	}

	/** computes all States of the given HasseGraph starting by the initial state */
	public void compute (HasseGraph hasseGraph) {
		ArrayList<State> statesToCompute = new ArrayList<>(hasseGraph.getStates());
		compute(hasseGraph.getInitialState(), hasseGraph);
		System.out.print("#");
		statesToCompute.remove(hasseGraph.getInitialState());
		while (true) {
			if (statesToCompute.size() == 0) { break; }
			for (int i = 0; i < statesToCompute.size(); i++) {
				State state = statesToCompute.get(i);
				
				boolean computable = true;
				for (State previousState : state.getPrevious()) {
					if (statesToCompute.contains(previousState)) {			// if one of the previous states is not computed yet => cant compute this state
						computable = false;
						break;
					}
				}
				if (computable) {
					compute(state, hasseGraph);
					statesToCompute.remove(state);
					System.out.print("#");
					i--;
				}
			}
		}
		System.out.println();
	}

	/** computes a states scoreMatrix */
	private void compute (State state, HasseGraph hasseGraph) {
		state.setScoreMatrix(computeMatrix(state, hasseGraph));
	}

	/**
	 * aligns n sequences
	 * ueses the Matrix class which may cause more effort (O(n) with n = sequences.length per Matrix lookup)
	 * @param state - the actual state of the alignment specifying active and inactive sequences
	 * @param sequences - the sequences to align given in char arrays
	 */
	private Matrix computeMatrix(State state, HasseGraph hasseGraph){
		Sequence[] allSequences = hasseGraph.getSequences();
		Sequence[] sequences = allSequences;									// use all sequecnes if no state is given
		if (state != null) {
			if (state.getActive().size() < 1) {
				return new Matrix(0);											// return a void Matrix (spacial case in backtrack?)
			}
			sequences = new Sequence[state.getActive().size()];
			for (int i = 0; i < sequences.length; i++) {
				sequences[i] = allSequences[state.getActive().get(i)];			// since state.active is storing the INDICES of avtive sequences
			}
		}
		
		IndexVector iPattern = new IndexVector(new int[sequences.length]);		// the index Vector used for iteration (I-Pattern)
		Matrix scoreMatrix = new Matrix(sequences);								// the score Matrix
		
		//initialization (initial rows/columns)
		for (int i = 0; i < iPattern.length(); i++) {
			for (iPattern.set(1, i); iPattern.get(i) < scoreMatrix.getLength(i); iPattern.addTo(1, i)) {
				scoreMatrix.set(scoreMatrix.get(
						iPattern.getChangedArray(-1, i)) + 															// predeceeding matrix etnry
						(Scorer.getInstance().getScore(sequences[i].get(iPattern.get(i)-1), Scorer.GAP_CHAR) * 		// + gap score
						(iPattern.length() -1)), iPattern.toArray());												// * sequences (times to count the gap score)
			}
			iPattern.set(0, i);	
		}
		
		// initialization (initial planes)
		for (int i = 2; i < sequences.length; i++) {						// i = 2 since we need this for (at least two-dimensional) planes
			int[] indicesToCount = new int[i];										// specifies the planes sequences
			for (int j = 0; j < indicesToCount.length; j++) {						// initialization of initialization
				indicesToCount[j] = j;												// skip cases with same indices							
			}
			
			while (true) {															// iteration over every possible i-dimensional (hyper) plane starts here
				
				computeMatrixPlane(state, scoreMatrix, sequences, allSequences, indicesToCount, hasseGraph);// compute Matrix plane using the chosen Sequences (Dimensions)
				
				indicesToCount[indicesToCount.length-1]++;										// iteration logic (binomial (n over i))
				for (int j = indicesToCount.length -1 ; j > 0 ; j--) {							// just go down to 1 ( j > 0 )
					if (indicesToCount[j] == sequences.length-(indicesToCount.length-1-j)) {
						indicesToCount[j-1]++;
						for (int k = j; k < indicesToCount.length; k++) {						// reset following indices to lowest possible value
							indicesToCount[k] = indicesToCount[k-1] +1; 
						}
					}
				}
				if (indicesToCount[indicesToCount.length-1] == sequences.length) {							// overflow on end
					break;
				}
			}																// iteration over every possible i-dimensional (hyper) plane ends here
		}
		
		// compute complete Matrix using every Sequence
		computeMatrixPlane(state, scoreMatrix, sequences, allSequences, null, hasseGraph);
		return scoreMatrix;
	}
	
	/**
	 * computes a plane of t he scoreMatrix
	 * @param scoreMatrix - Matrix with scores
	 * @param sequences - the sequences to align
	 * @param allSequences - all the Sequences of the whole Alignment
	 * @param indicesToCount -  indices of sequences to use here (if null -> use all)
	 */
	private void computeMatrixPlane(State state, Matrix scoreMatrix, Sequence[] sequences, Sequence[] allSequences, int[] indicesToCount, HasseGraph hasseGraph){
		boolean fullState = false;													// true if all sequences are used
		if (indicesToCount == null) {												// if null use all
			indicesToCount = new int[sequences.length];
			for (int i = 0; i < indicesToCount.length; i++) {
				indicesToCount[i] = i;
			}
			fullState = true;
		}
		
		IndexVector iPattern = new IndexVector(new int[indicesToCount.length]);		// the index Vector used for iteration (I-Pattern)
		for (int i = 0; i < iPattern.length(); i++) {								// start with I-Pattern (1,...,1)
			iPattern.set(0, i);
		}
		
		boolean b = false;
		while (true) {																// iteration over I-Pattern starts here
			ArrayList<Float> scores = new ArrayList<Float>();								// stores the scores, used to find max score
			if (!iPattern.contains(0)) {
				// calculation
				IndexVector piPattern = new IndexVector(new int[indicesToCount.length]);		// the PI-Pattern vector used in case distinctions (PI-Pattern)
				boolean c = false;
				
				// compute scores in the same matrix
				while (true) {																	// iteration over (negative!) PI-Pattern starts here. (Pi-Pattern {0,-1}^n)
					if (!piPattern.isNullVector()) {											// ignore case where PI-Pattern = (0,...,0) (total gap is not allowed)
						// case distinctions
						int[] predecessorIPattern = iPattern.addToArray(piPattern);				// (miss)match/gaps in different sequences
						// add scores using all sequences (null stands for all)
						scores.add(scoreMatrix.get(predecessorIPattern, indicesToCount) + Scorer.getInstance().getScoreSumOfPairs(getSequencesCharAraryByIndices(iPattern.toArray(), piPattern.toArray(), sequences, indicesToCount)));
					}
					
					// move "/// if a state is given, we need to consider previous states"-Block here
					// update getMaxScoreCandidateIndices
					
					// iteration logic (PI-Pattern)	
					piPattern.addTo(-1, piPattern.length()-1);									// iteration logic (PI-Pattern)
					for (int i = piPattern.length()-1; i >= 0; i--) {
						if (piPattern.get(i) == -2) {										
							if (i == 0) {														// check iteration completeness
								c = true;
								break;
							}
							piPattern.set(0, i);
							piPattern.addTo(-1, i-1);
						}
					}
					if (c) { break; }															// iteration complete?
				}																		// iteration over PI-Pattern ends here
			}
			
			// if a state is given, we need to consider previous states
			if (state != null) {
				State imaginaryPlaneState;
				if (fullState) {
					imaginaryPlaneState = state;
				} else {
					imaginaryPlaneState = hasseGraph.getOrCreateStateByActiveSequences(state, indicesToCount);	// the State with indicesToUse as active states (can allways be found in previous states)
				}
				
				//System.out.println("imaginaryPlaneState: " + imaginaryPlaneState);
				for (State previousState : imaginaryPlaneState.getDirectlyPrevious()) {								// look at previous states
					if (! state.getPrevious().contains(previousState)) {									// look at intersection state.getPrevious() and imaginaryPlaneState.getPrevious()
						break;
					}
					// for every entry of the matrix (bc u were wondering last time)
					IndexVector maxScoreCandidateIndices = getMaxScoreCandidateIndices(state, previousState, sequences, allSequences, iPattern, indicesToCount);
					if (maxScoreCandidateIndices != null) {
						//IndexVector computedPiPattern = getPiPattern(state, previousState, iPattern, maxScoreCandidateIndices, indicesToCount);
						//System.out.println("computed piPattern:\t" + computedPiPattern);
						scores.add(previousState.getScoreMatrix().get(maxScoreCandidateIndices.toArray()));
						//System.out.println(iPattern + " :: " + maxScoreCandidateIndices + " :: " + computedPiPattern);
					}
				}
			}
			
			if (scores.size() > 0) {
				float maxScore = Collections.max(scores);								// find max Score
				scoreMatrix.set(maxScore, iPattern.toArray(), indicesToCount);			// set max score
			}
			
			// iteration logic (I-Pattern)
			iPattern.addTo(1, iPattern.length()-1);									// iteration logic (I-Pattern)
			for (int i = iPattern.length()-1; i >= 0; i--) {
				if (iPattern.get(i) == scoreMatrix.getLength(indicesToCount[i])) {
					if (i == 0) {													// check iteration completeness
						b = true;														
						break;
					}
					iPattern.set(0, i);
					iPattern.addTo(1, i-1);
				}
			}
			if (b) { break; }														// iteration complete?
		}																		// iteration over I-Pattern (I-Pattern) ends here
	}
	
	/**
	 * 
	 * @param actualState - the actual State
	 * @param previousState - the state to find the max score in
	 * @param sequences - the sequences used
	 * @param allSequences - all the sequences
	 * @param actualStateIPattern - the actual iPattern
	 * @return
	 */
	private IndexVector getMaxScoreCandidateIndices(State actualState, State previousState, Sequence[] sequences, Sequence[] allSequences, IndexVector actualStateIPattern, int[] indicesToCount){
		if (previousState.getActive().size() == 0) { return null; }								// nothing to find here
		
		ArrayList<Integer> actualActiveIndices;
		if (indicesToCount == null) {
			actualActiveIndices = actualState.getActive();	
		} else {
			// take only indicesToCount
			actualActiveIndices = new ArrayList<>();
			for (Integer index : indicesToCount) {
				actualActiveIndices.add(actualState.getActive().get(index));
			}
		}

		ArrayList<Integer> matchingIndices = new ArrayList<>();
		ArrayList<Integer> missingIndices = new ArrayList<>();									// missing in actualState
		IndexVector maxScoreCandidateIPattern = null; 											// collecting the score candidates in here
		float maxScore = Float.NEGATIVE_INFINITY;
		
		for (Integer index : previousState.getActive()) {										// find matching and missing indices
			if (actualActiveIndices.contains(index)) {
				matchingIndices.add(index);
			} else {
				missingIndices.add(index);
			}
		}
		
		IndexVector matchingIPattern = new IndexVector(new int[matchingIndices.size()]);		// the index Vector used for iteration (I-Pattern)s
		
//		//DEBUGGING
//		System.out.println("actualState:             " + actualState);
//		if (indicesToCount != null) { System.out.print("indicesToCount:          ");
//			for (int index : indicesToCount) {
//				System.out.print(index + ",");	
//			}
//		} System.out.println();
//		System.out.println("actual indices:          " + actualActiveIndices);
//		System.out.println("previous indices:        " + previousState.getActive());
		
		int matchingIndex = 0;
		for (int i = 0; i < actualStateIPattern.length(); i++) {								// reduce actualStateIPattern to matchingIPattern
			if (previousState.getActive().contains(actualActiveIndices.get(i))) {				// previousState.contains(indexOfActualState) === actualState.contains(indexOfPreviousState) since indices are ordered
				matchingIPattern.set(actualStateIPattern.get(i), matchingIndex);
				matchingIndex++;
			}
		}
		
		//System.out.println("matchingIndices:         " + matchingIndices);
		//System.out.println("missingIndices(in act.): " + missingIndices);
//		System.out.println("actualStateIPattern:    " + actualStateIPattern);
//		System.out.println("matchingIPattern:       " + matchingIPattern);
		
		if (matchingIndices.size() == 0) {
			return previousState.getMaxScoreIndices();
		}
		
		IndexVector previousSpaceKandidate = new IndexVector(new int[previousState.getActive().size()]);// the save the previous space candidate
				// case distinctions - find candidates in reduced (matching) space
				int[] matchingPredecessorIPattern = matchingIPattern.toArray();							// (miss)match/gaps in the matching sequences - candidates in reduced (matching) space
				// multiply matching candidates to previous space cantidates
				ArrayList<Integer> previousSpaceRunnungIndices = new ArrayList<>();						// the indices to iterate over to get multiplied candidates in previous state space
				int j = 0;
				for (int i = 0; i < previousState.getActive().size(); i++) {
					if (j < matchingIndices.size() && previousState.getActive().get(i) == matchingIndices.get(j)) {
						previousSpaceKandidate.set(matchingPredecessorIPattern[j], i);
						j++;
					} else {
						previousSpaceKandidate.set(1, i);												// since i pattern starts at 1
						previousSpaceRunnungIndices.add(i);
					}
				}
				// run over previousSpaceRunnungIndices here
				boolean d = false;
				IndexVector runningIndices = new IndexVector(previousSpaceRunnungIndices);
//				System.out.println("runningIndices: \t" + runningIndices);// DEBUGGING
				while (true) {																						// iteration over previousSpaceRunnungIndices starts here. (previousSpaceRunnungIndices {0,n}^n)
					if (previousSpaceKandidate.hasNegetiveEntry()) { break; }
					
					float score = previousState.getScoreMatrix().get(previousSpaceKandidate.toArray());
					//System.out.println("max candidate: " + maxScoreCandidateIPattern + " : " + maxScore);
//					System.out.println("candidate: \t\t" + previousSpaceKandidate + " : " + previousState.getScoreMatrix().get(previousSpaceKandidate.toArray()) + " = " + score);
					if (score > maxScore) {
						maxScoreCandidateIPattern = previousSpaceKandidate.clone();
						maxScore = score;
//						System.out.println("new max candidate:\t" + maxScoreCandidateIPattern + " : " + score);
					}
					
					if (runningIndices.length() == 0) { break; }
//					if (previousSpaceKandidate.contains(0)) {
//						//System.out.println(previousSpaceKandidate + " is not a viable candidate"); 
//						break; 
//					}
					
					// DEBUGGING
					//System.out.println("candidate: " + previousSpaceKandidate);
					//for (int i = 0; i < previousSpaceKandidate.length(); i++) {
						//System.out.print(allSequences[previousState.getActive().get(i)] + "[");
						//System.out.print(previousSpaceKandidate.get(i) + "] = ");
						//System.out.println(allSequences[previousState.getActive().get(i)].toCharArray()[previousSpaceKandidate.get(i)-1]);
					//}
					//System.out.println(" Score: " + previousState.getScoreMatrix().get(previousSpaceKandidate.toArray()));
					
					// iteration logic (previousSpaceRunningIndices)
					previousSpaceKandidate.addTo(1, runningIndices.get(runningIndices.length()-1));					// iteration logic (previousSpaceRunnungIndices)	
					for (int i = runningIndices.length()-1; i >= 0; i--) {
						//System.out.println("runningSequenceIndex:  " + runningIndices.get(i));
						//System.out.println("runningSequence:      " + allSequences[previousState.getActive().get(runningIndices.get(i))]);
						if (previousSpaceKandidate.get(runningIndices.get(i)) == allSequences[previousState.getActive().get(runningIndices.get(i))].getLength() + 1) {										
							if (i == 0) {																			// check iteration completeness
								d = true;
								break;
							}
							previousSpaceKandidate.set(1, runningIndices.get(i));
							previousSpaceKandidate.addTo(1, runningIndices.get(i-1));
						}
					}
					if (d) { break; }
				}
				
		
		if (maxScoreCandidateIPattern != null) {
			//System.out.println("maxscoreCandidateIPattern: " + maxScoreCandidateIPattern + " Score: " + maxScore);	// DEBUGGING
			return maxScoreCandidateIPattern;
		} else {
			//System.out.println("NO CANDIDATES");
			return null;																				// without iPattern no piPattern
		}
	}
	
	/**
	 * @param actualState - the actual State
	 * @param previousState - the state to find the max score in
	 * @param actualStateIPattern - the actual iPattern
	 * @param previousStateIPattern - the previus iPattern
	 * @return the pi pattern between these two iPatterns
	 */
	private IndexVector getPiPattern(State actualState, State previousState, IndexVector actualStateIPattern, IndexVector previousStateIPattern, int[] indicesToCount) {
		ArrayList<Integer> actualActiveIndices;
		if (indicesToCount == null) {
			actualActiveIndices = actualState.getActive();	
		} else {
			// take only indicesToCount
			actualActiveIndices = new ArrayList<>();
			for (Integer index : indicesToCount) {
				actualActiveIndices.add(actualState.getActive().get(index));
			}
		}
		
		IndexVector piPattern = new IndexVector(new int[actualActiveIndices.size()]);
		for (int i = 0; i < actualActiveIndices.size(); i++) {
			if (previousState.getActive().contains(actualActiveIndices.get(i))) {
				piPattern.set(previousStateIPattern.get(previousState.getActive().indexOf(actualActiveIndices.get(i))) - actualStateIPattern.get(i), i);	// subtraction on match
			} else {
				piPattern.set(-1, i);																	// read one if new sequence is opened, so pipattern is 1 (-1 for negative pipattern)
			}
		}
		return piPattern;
	}
	
	/**
	 * returns the actual vertical vector of the Alignment (letters), the actual character of each sequence or Scorer.GAP_CHAR if pi at this column and for this sequence is 0 (piPattern[i] == 0)
	 * @param iPattern the actual I-Pattern
	 * @param piPattern the Pi-Pattern
	 * @param sequences all the considered sequences
	 * @param indicesToUse indices of sequences to use here (if null use all)
	 * @return the actual vertical vector of the Alignment
	 */
	private char[] getSequencesCharAraryByIndices(int[] iPattern, int[] piPattern, Sequence[] sequences, int[]indicesToUse){
		// here we got the reduced i- and pi- patterns but the full sequences array
		// if indicesToUse == null use all
		if (indicesToUse == null) {
			indicesToUse = new int[sequences.length];
			for (int i = 0; i < indicesToUse.length; i++) {
				indicesToUse[i] = i;
			}
		}
		
		// initialize the SequencesCharArray
		char[] chars = new char[sequences.length];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = Scorer.GAP_CHAR;
		}
		
		// get chars
		for (int i = 0; i < indicesToUse.length; i++) {
			if (piPattern[i] != 0) {
				// since we got the reduced i- and pi- patterns but the full sequences array we have to select the sequence by using the inidicesToUse vector
				chars[indicesToUse[i]] = sequences[indicesToUse[i]].get(iPattern[i] + piPattern[i]); 
			}
		}
		return chars;
	}

	/** the backtracking */
	private Sequence[] backtrack(HasseGraph hasseGraph){
		Sequence[] allSequences = hasseGraph.getSequences();
		String[] alignedSequences = new String[allSequences.length]; 			// the aligned Sequences (output)
		
		for (int i = 0; i < alignedSequences.length; i++) {						// set the aligned sequences to "" at start
			alignedSequences[i] = "";
		}
		
		State actualState = hasseGraph.getMaxFinalState();						// initialize with maxFinalState of the HasseGraph
		System.out.println("maxfinalstate: " + hasseGraph.getMaxFinalState());
		//System.out.println(hasseGraph.getInitialState().getScoreMatrix());
		//System.out.println(hasseGraph.getInitialState().getFollowing().get(0).getScoreMatrix());
		
		Sequence[] sequences = new Sequence[actualState.getActive().size()];
		for (int i = 0; i < sequences.length; i++) {
			sequences[i] = allSequences[actualState.getActive().get(i)];		// select the active sequences for the actual state (maxFinalState = initial State for backtracking)
		}
		
		IndexVector piPattern = new IndexVector(new int[sequences.length]);		// the PI-Pattern vector used in case distinctions (PI-Pattern)
		IndexVector iPattern = new IndexVector(new int[sequences.length]);		// to save the actual matrix indices (I-Pattern)
		char[] chars = new char[sequences.length];								// temporary char array used for calculation
		
		if (actualState.isFullyRightLocal()) {
			iPattern = actualState.getMaxScoreIndices();
		} else {
			for (int i = 0; i < sequences.length; i++) {							// start on the bottom right of the matrix (max indices)
				iPattern.set(sequences[i].getLength(), i);
			}
		}
		
		while(true) {
			boolean c = false;
			Matrix scoreMatrix = actualState.getScoreMatrix();						// get the score matrix of the final state with maximal "end"-score
//			System.out.println("ACTUAL STATE: " + actualState);
//			System.out.println("ACTUAL PI:    " + piPattern);
//			System.out.println("ACTUAL I:     " + iPattern);
//			System.out.println("CHARS LEN:    " + chars.length);
			// Look for a match in the actual Matrix
			while (true) {															// iteration over (negative!) PI-Pattern starts here. (Pi-Pattern {0,-1}^n)
//				System.out.println("ACTUAL INNER PI:    " + piPattern);
				if (!piPattern.isNullVector() && !iPattern.addToVector(piPattern.toArray()).hasNegetiveEntry()) {	// ignore case where PI-Pattern = (0,...,0) (total gap is not allowed) or where one or more indices get negative
					// case distinctions for SAME MATRIX (to compute the actual columns score)
					for (int i = 0; i < chars.length; i++) {
						if (piPattern.get(i) == 0) { 
							chars[i] = Scorer.GAP_CHAR;
						} else {
							chars[i] = sequences[i].get(iPattern.get(i)-1);	
						}
					}
					
					// DEBUGGING
//					for (int i = 0; i < iPattern.length(); i++) {
//						System.out.print(iPattern.get(i) + ",");
//					}
//					System.out.print(chars);
//					System.out.println(" : " + scoreMatrix.get(iPattern.addToArray(piPattern)) + " ==? " + scoreMatrix.get(iPattern.toArray()) + "-" + Scorer.getInstance().getScoreSumOfPairs(chars));
					
					// find matching successor matrix entry ("looking at"-score = actual score - score for actual pi-pattern (column))
					if (scoreMatrix.get(iPattern.addToArray(piPattern.toArray())) == scoreMatrix.get(iPattern.toArray()) - Scorer.getInstance().getScoreSumOfPairs(chars)) {
						int j = 0;
						for (int i = 0; i < alignedSequences.length; i++) {					// mapping from sequences to allSequences
							if (actualState.getActive().contains(i)) {
								alignedSequences[i] = chars[j] + alignedSequences[i];		// writing the aligned sequences
								j++;
							} else {
								alignedSequences[i] = ' ' + alignedSequences[i];
							}
						}
						
						iPattern = iPattern.addToVector(piPattern.toArray());				// make the step (pi pattern)
						if (iPattern.isNullVector()) {
							break;			
						}
						piPattern.setToNullVector();										// reset pi pattern
						continue;
					}
				}
				
				// iteration logic (PI-Pattern)
				piPattern.addTo(-1, piPattern.length()-1);								// iteration logic
				for (int i = piPattern.length()-1; i >= 0; i--) {
					if (piPattern.get(i) == -2) {										// check iteration completeness
						if (i == 0) {			
							c = true;
							break;
						}
						piPattern.set(0, i);
						piPattern.addTo(-1, i-1);
					}
				}
				if (c) { break; }														// breaks if all pipatterns where used and no match was found
			}
			
			// additional case distictions for previous ADJACENT MATRICES (you come here if no match in same matrix was found or alignment is complete) // Change to next Matrix
			if (iPattern.isNullVector()) { break; }																								// if alignment is allready complete, skip this
//			System.out.println("no way found in actual matrix...");
			for (State state : actualState.getPrevious()) {
				if (state.getScoreMatrix().getMaxScore() == Float.NEGATIVE_INFINITY) { continue; }													// skip void states
				IndexVector candiadateIPattern = getMaxScoreCandidateIndices(actualState, state, sequences, allSequences, iPattern, null);		// the "previous" iPattern (of the previous state)
				IndexVector computedPiPattern = getPiPattern(actualState, state, iPattern, candiadateIPattern, null);							// the piPattern betweed previous and actual iPattern
				
				// case distinctions for SAME MATRIX (to compute the actual columns score)
				for (int i = 0; i < chars.length; i++) {
					if (computedPiPattern.get(i) == 0 || iPattern.get(i) == 0) { 
						chars[i] = Scorer.GAP_CHAR;
					} else {
						chars[i] = sequences[i].get(iPattern.get(i)-1);	
					}
				}
				
				// DEBUGGING
//				for (int i = 0; i < iPattern.length(); i++) {
//					System.out.print(iPattern.get(i) + ",");
//				}
//				System.out.print(chars);
//				System.out.println(" : " + state.getScoreMatrix().get(candiadateIPattern.toArray()) + " ==? " + scoreMatrix.get(iPattern.toArray()) + "-" + Scorer.getInstance().getScoreSumOfPairs(chars));
				if (state.getScoreMatrix().get(candiadateIPattern.toArray()) == scoreMatrix.get(iPattern.toArray())) {	// find matching successor matrix entry ("looking at"-score = actual score - score for actual pi-pattern (column))
					// reconfiguration to process the new State
					actualState = state;
					sequences = new Sequence[actualState.getActive().size()];
					for (int i = 0; i < sequences.length; i++) {
						sequences[i] = allSequences[actualState.getActive().get(i)];	// select the active sequences for the actual state (maxFinalState = initial State for backtracking)
					}
					chars = new char[sequences.length];
					piPattern = new IndexVector(new int[sequences.length]);				// reset the PI-Pattern vector with respect to the dimensions of the new matrix
					iPattern = candiadateIPattern;
					break;
				}
			}
		}
		
		Sequence[] alignedSequencesCharArray = new Sequence[allSequences.length];		// char[] to sequence; alignedSequences to alignedSequencesCharArray
		for (int i = 0; i < alignedSequencesCharArray.length; i++) {
			//System.out.println(alignedSequencesCharArray.length);
			//System.out.println(sequences.length);
			alignedSequencesCharArray[i] = new Sequence(alignedSequences[i].toCharArray(), allSequences[i].isGlobalLeft(), allSequences[i].isGlobalRight());
		}		
		return alignedSequencesCharArray;
	}

	
	
//	/**
//	 * aligns the given Sequences
//	 * @return the Alignment of the given Sequences
//	 */
//	public Alignment align(Sequence... sequences){
//		Matrix scoreMatrix = computeMatrix(null, sequences);
//		return new Alignment(sequences, backtrack(scoreMatrix, sequences), scoreMatrix);
//	}
	
	
	
//	/** the backtracking */
//	private Sequence[] backtrack(Matrix scoreMatrix, Sequence... sequences){
//		IndexVector piPattern = new IndexVector(new int[sequences.length]);		// the PI-Pattern vector used in case distinctions (PI-Pattern)
//		IndexVector indices = new IndexVector(new int[sequences.length]);		// to save the actual matrix indices
//		char[] chars = new char[sequences.length];								// temporary char array used for calculation
//		String[] alignedSequences = new String[sequences.length]; 				// the aligned Sequences (output)
//		
//		for (int i = 0; i < alignedSequences.length; i++) {						// set them to ""
//			alignedSequences[i] = "";
//		}
//		
//		for (int i = 0; i < sequences.length; i++) {							// start on the bottom right of the matrix (max indices)
//			indices.set(sequences[i].getLength(), i);
//		}
//		
//		// calculation
//		boolean c = false;
//		while (true) {															// iteration over (negative!) PI-Pattern starts here. (Pi-Pattern {0,-1}^n)
//			if (!piPattern.isNullVector() && !indices.addToVector(piPattern.toArray()).hasNegetiveEntry()) {	// ignore case where PI-Pattern = (0,...,0) (total gap is not allowed) or where one or more indices get negative
//				// case distinctions for SAME MATRIX (to compute the actual columns score)
//				for (int i = 0; i < chars.length; i++) {
//					if (piPattern.get(i) == 0) { 
//						chars[i] = Scorer.GAP_CHAR;
//					} else {
//						chars[i] = sequences[i].get(indices.get(i)-1);	
//					}
//				}
//				
//				// DEBUGGING
//				for (int i = 0; i < indices.length(); i++) {
//					System.out.print(indices.get(i) + ",");
//				}
//				System.out.print(chars);
//				System.out.println(" : " + scoreMatrix.get(indices.addToArray(piPattern)) + " =? " + scoreMatrix.get(indices.toArray()) + "-" + Scorer.getInstance().getScoreSumOfPairs(chars));
//				
//				// find matching successor matrix entry ("looking at"-score = actual score - score for actual pi-pattern (column))
//				if (scoreMatrix.get(indices.addToArray(piPattern.toArray())) == scoreMatrix.get(indices.toArray()) - Scorer.getInstance().getScoreSumOfPairs(chars)) {
//					for (int i = 0; i < alignedSequences.length; i++) {
//						alignedSequences[i] = chars[i] + alignedSequences[i];
//					}
//					indices = indices.addToVector(piPattern.toArray());				// make the step (pi pattern)
//					if (indices.isNullVector()) {
//						break;			
//					}
//					piPattern.setToNullVector();									// reset pi pattern
//					continue;
//				}
//			}
//			
//			// iteration logic (PI-Pattern)
//			piPattern.addTo(-1, piPattern.length()-1);								// iteration logic
//			for (int i = piPattern.length()-1; i >= 0; i--) {
//				if (piPattern.get(i) == -2) {										// check iteration completeness
//					if (i == 0) {			
//						c = true;
//						break;
//					}
//					piPattern.set(0, i);
//					piPattern.addTo(-1, i-1);
//				}
//			}
//			if (c) { break; }
//		}
//		
//		Sequence[] alignedSequencesCharArray = new Sequence[sequences.length];
//		for (int i = 0; i < alignedSequencesCharArray.length; i++) {
//			alignedSequencesCharArray[i] = new Sequence(alignedSequences[i].toCharArray(), sequences[i].isGlobalLeft(), sequences[i].isGlobalRight());
//		}		
//		return alignedSequencesCharArray;
//	}

}
