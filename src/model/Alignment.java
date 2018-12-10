package model;

import model.hasse.HasseGraph;

/**
 * Objects of this class are representing alignments containing the aligned sequences and the score.
 * @author Falco
 */
public class Alignment {
  // object definition
	/** the initial sequences */
	private Sequence[] sequences;
	/** the aligned sequences */
	private Sequence[] alignedSequences;
	/** the score of this Alignment */
	private float score;
	/** the generated hasseGraph */
	private HasseGraph hasseGraph;
	
	/**
	 * generates a new Alignment with a given Array of chars representing the aligned sequences
	 * use this if the alignment is already computed
	 * @param alignedSequences - the aligned sequences
	 * @param aligne - set it true if the sequences have to be aligned; false if the sequences are already aligned
	 */
	public Alignment(Sequence[] sequences, Sequence[] alignedSequences, Matrix scoreMatrix) {
		this.sequences = sequences;
		this.alignedSequences = alignedSequences;
		this.score = scoreMatrix.getScore();
		this.hasseGraph = null;
	}
	
	/**
	 * generates a new Alignment with a given Array of chars representing the aligned sequences
	 * use this if the alignment is already computed
	 * @param alignedSequences - the aligned sequences
	 * @param aligne - set it true if the sequences have to be aligned; false if the sequences are already aligned
	 */
	public Alignment(Sequence[] alignedSequences, HasseGraph hasseGraph) {
		this.sequences = hasseGraph.getSequences();
		this.alignedSequences = alignedSequences;
		this.score = hasseGraph.getMaxFinalState().getScore(sequences);
		this.hasseGraph = hasseGraph;
	}
	
	/** @return a String representation of this object */
	@Override
	public String toString() {
		String s = "";
		s += "==========================================================\n";
		s += "Sequences:\n"; 
		s += "----------------------------------------------------------\n";
		for (int i = 0; i < sequences.length; i++) {
			s += "(" + i + ") " + sequences[i].toString() + "\n";
		}
		s += "----------------------------------------------------------\n";
		s += "==========================================================\n\n";
		
		s += this.hasseGraph.toString() + "\n";
		
		s += "==========================================================\n";
		s += "ALIGNMENT:\n";
		s += "----------------------------------------------------------\n";
		for (int i = 0; i < alignedSequences.length; i++) {
			if (alignedSequences[i].isGlobalLeft()) {
				s += "[ ";
			} else {
				s += "( ";
			}
			for (int j = 0; j < alignedSequences[i].getLength(); j++) {
				s += alignedSequences[i].get(j);
			}
			if (alignedSequences[i].isGlobalRight()) {
				s += " ]";
			} else {
				s += " )";
			}
			s += "\n";
		}
		s += "----------------------------------------------------------\n";
		s += "score:\t" + score + "\n";
		s += "==========================================================\n";
		return s;
	}

	public Sequence[] getAlignedSequences() {
		return alignedSequences;
	}
	
	public void setAlignedSequences(Sequence[] alignedSequences) {
		this.alignedSequences = alignedSequences;
	}

	public Sequence[] getSequences() {
		return sequences;
	}

	public void setSequences(Sequence[] sequences) {
		this.sequences = sequences;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public HasseGraph getHasseGraph() {
		return hasseGraph;
	}

	public void setHasseGraph(HasseGraph hasseGraph) {
		this.hasseGraph = hasseGraph;
	}

}
