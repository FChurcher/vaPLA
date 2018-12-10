package model;

/**
 * Objects of this class are representing represent lengths.length-dimensional Matrices stored in 1-dimensional matrices. lengths.length-dimensional indices are mapped to 1-dimensional indices.
 * @author Falco
 */
public class Matrix {
  // object definition
	/** * the 1-dimensional (flat) matrix */
	private float[] flatMatrix;
	/** the length of each dimension */
	private int[] lengths;
	/** the DimensionsCount = SequencesCount */
	private int dimensions;
	
	/**
	 * generates a new 1-dimensional (flat) score-Matrix with length max(lengths)^lengths(size)
	 * to store the Alignment scores at each position of the Alignment
	 * @param legth -	the length of each dimension
	 */
	public Matrix(int... lengths) {
		this.dimensions = lengths.length;
		this.lengths = lengths;
		int length = lengths[0];
		for (int i = 1; i < lengths.length; i++) {
			length *= lengths[i];
		}
		flatMatrix = new float[length];
	}
	
	/**
	 * generates a new 1-dimensional (flat) score-Matrix with length max(lengths)^sequences(size)
	 * to store the Alignment scores at each position of the Alignment
	 * @param sequences - the sequences for which the score-Matrix is needed
	 */
	public Matrix(char[]... sequences) {
		this.dimensions = sequences.length;
		int[] lengths = new int[sequences.length];
		for (int i = 0; i < lengths.length; i++) {
			lengths[i] = sequences[i].length+1;
		}
		this.lengths = lengths;
		int length = lengths[0];
		for (int i = 1; i < lengths.length; i++) {
			length *= lengths[i];
		}
		flatMatrix = new float[length];
	}
	
	/**
	 * generates a new 1-dimensional (flat) score-Matrix with length max(lengths)^sequences(size)
	 * to store the Alignment scores at each position of the Alignment
	 * @param sequences - the sequences for which the score-Matrix is needed
	 */
	public Matrix(Sequence... sequences) {
		this.dimensions = sequences.length;
		int[] lengths = new int[sequences.length];
		for (int i = 0; i < lengths.length; i++) {
			lengths[i] = sequences[i].getLength()+1;
		}
		this.lengths = lengths;
		int length = lengths[0];
		for (int i = 1; i < lengths.length; i++) {
			length *= lengths[i];
		}
		flatMatrix = new float[length];
	}
	
	/** @return a String representation of this object */
	@Override
	public String toString() {
		String s = "";
		s += "==========================================================\n";
		s += "SCORE MATRIX:\n";
		s += "----------------------------------------------------------\n";
		if (lengths[0] == 0) {
			s += "[VOID MATRIX]";
		}
		if (this.dimensions == 1) {
			for (int i = 0; i < lengths[0]; i++) {
				s += get(new int[] {i}) + " | ";
			}
			s += "\n";
		}
		if (this.dimensions == 2) {
			for (int i = 0; i < lengths[0]; i++) {
				for (int j = 0; j < lengths[1]; j++) {
					s += get(new int[] {i,j}) + " | ";
				}
				s += "\n";
			}
		} 
		if (this.dimensions == 3) {
			s += "Plane: 0,1:\n";
			s += "-----------------------------------------\n";
			for (int i = 0; i < lengths[0]; i++) {
				for (int j = 0; j < lengths[1]; j++) {
					s += get(new int[] {i,j,0}) + " | ";
				}
				s += "\n";
			}
			s += "-----------------------------------------\n";
			s += "Plane: 0,2:\n";
			s += "-----------------------------------------\n";
			for (int i = 0; i < lengths[0]; i++) {
				for (int j = 0; j < lengths[2]; j++) {
					s += get(new int[] {i,0,j}) + " | ";
				}
				s += "\n";
			}
			s += "-----------------------------------------\n";
			s += "Plane: 1,2:\n";
			s += "-----------------------------------------\n";
			for (int i = 0; i < lengths[1]; i++) {
				for (int j = 0; j < lengths[2]; j++) {
					s += get(new int[] {0,i,j}) + " | ";
				}
				s += "\n";
			}
			s += "-----------------------------------------\n";
			s += "Slices:\n";
			s += "-----------------------------------------\n";
			for (int k = 0; k < lengths[2]; k++) {
				s += "Slice 0,1: " + k + "\n";
				for (int i = 0; i < lengths[0]; i++) {
					for (int j = 0; j < lengths[1]; j++) {
						s += get(new int[] {i,j,k}) + " | ";
					}
					s += "\n";
				}
			}
		}
		if (this.dimensions > 3) {
			s += "| H/GHER\n| / DIMENSION\n|/____ MATRIX\n";
		}
		s += "----------------------------------------------------------\n";
		s += "score: " + this.getScore() + "\tmax score: " + this.getMaxScore() + " at " + this.getMaxScoreIndices() + "\n";
		s += "==========================================================\n";
		return s;
	}

	/** @return the score of this Matrix (right bottom entry) */
	public float getScore(){
		if (lengths[0] == 0) {
			return 0f;
		}
		int[] indices = lengths.clone();
		for (int i = 0; i < indices.length; i++) {
			indices[i] = indices[i] - 1;
		}
		return get(indices);
	}

	/**
	 * itereates through the flat matrix and returns the maximal score of it
	 * @return the maximal score of this Matrix
	 */
	public float getMaxScore() {
		float max = Float.NEGATIVE_INFINITY;
		for (float f : flatMatrix) {
			if (f >= max) {
				max = f;
			}
		}
		return max;
	}
	
	/**
	 * itereates through the flat matrix and returns the indices of the maximal score of it
	 * @return the indices of the maximal score of this Matrix
	 */
	public IndexVector getMaxScoreIndices() {
		if (this.flatMatrix.length == 0) { return null; }
		int[] data = new int[dimensions];
		IndexVector indices = new IndexVector(data);
		boolean b = false;
		IndexVector maxScoreIPattern = indices.clone();
		while (true) {
			
			if (this.get(indices.toArray()) > this.get(maxScoreIPattern.toArray())) {
				maxScoreIPattern = indices.clone();									// finding maximum
			}
			
			// iteration logic (I-Pattern)
			indices.addTo(1, indices.length()-1);									// iteration logic (I-Pattern)
			for (int i = indices.length()-1; i >= 0; i--) {
				if (indices.get(i) == lengths[i]) {
					if (i == 0) {													// check iteration completeness
						b = true;														
						break;
					}
					indices.set(1, i);
					indices.addTo(1, i-1);
				}
			}
			if (b) { break; }	
		}
		return maxScoreIPattern;
	}
	
	/** maps the DimensionCount-dimensional indices to 1-dimensional indices */
	private int getFlatIndex(int[] indices){
		if (lengths.length == indices.length) {
			int flatIndex = 0;
			int multiplier = 1;
			
			for (int i = 0; i < indices.length; i++) {
				int index = indices[i]+1;
				if (index <= lengths[i]) {
					flatIndex += multiplier * (index-1);
					multiplier *= lengths[i];
				} else {
					throw new java.lang.Error("Matrix.get: ArrayIndexOutOfBoundsException: " + (index-1));
				}
			}
			return flatIndex;
		} else {
			throw new java.lang.Error("Matrix.get: called with wrong dimensions count " + indices.length + "(expected " + this.dimensions + ")");
		}
	}
	
	/**
	 * returns the value at the position of the Matrix by using reduced indexlists (indices*indicesToUse)
	 * this function is used to initiate the Matrix edges, planes and hyperplanes 
	 * @param indices - the position in whole Matrix
	 * @param indicesToUse - the indices of the indices to use
	 * @return the value of the position induced by indices and indicesToUse (projection of position in wole matrix to position in lower dimensional matrix)
	 */
	public float get(int[] indices, int[] indicesToUse){
		// if null use all; if all use tradition function
		if (indicesToUse == null || indicesToUse.length == this.dimensions) {
			return get(indices);
		}
		
		// if not all use only indices to use
		int[] newIndices = new int[this.dimensions];
		// set everything to 0 
		for (int i = 0; i < newIndices.length; i++) {
			newIndices[i] = 0;
		}
		// take only indicesToUse
		for (int i = 0; i < indicesToUse.length; i++) {
			newIndices[indicesToUse[i]] = indices[i];
		}
		return flatMatrix[getFlatIndex(newIndices)];
	}
	
	/**
	 * returns the value at this position of the Matrix
	 * @param indices - the position
	 * @return the value of the position indices
	 */
	public float get(int[] indices){
		return flatMatrix[getFlatIndex(indices)];
	}
	
	 /**
	  * sets the value at the position of the Matrix by using reduced indexlists (indices*indicesToUse)
	  * @param value - the value to set
	  * @param indicesToUse - the indices of the indices to use
	  */
	public void set(float value, int[] indices, int[] indicesToUse){
		// if null use all; if all use tradition function
		if (indicesToUse == null || indicesToUse.length == this.dimensions) {
			set(value, indices);
		}
		
		// if not all use only indices to use
		else {
			int[] newIndices = new int[this.dimensions];
			// set everything to 0 
			for (int i = 0; i < newIndices.length; i++) {
				newIndices[i] = 0;
			}
			// take only indicesToUse
			for (int i = 0; i < indicesToUse.length; i++) {
				newIndices[indicesToUse[i]] = indices[i];
			}
			flatMatrix[getFlatIndex(newIndices)] = value;
		}
	}
	
	 /**
	  * sets the value at the position of the Matrix
	  * @param value - the value to set
	  * @param indices - the position
	  */
	public void set(float value, int[] indices){
		flatMatrix[getFlatIndex(indices)] = value;
	}
	
	/**
	 * returns the lengths of the sequence at the given position
	 * @param index - the index
	 * @return the lengths
	 */
	public int getLength(int index){
		return this.lengths[index];
	}
	
}
