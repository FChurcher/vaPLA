package model;

import java.util.ArrayList;

/**
 * The indexVector Class is to store and handle int Arrays 
 * @author falco
 */
public class IndexVector {
  // object definition
	/** the Vector data */
	private int[] data;
	
	
	/** generates a new Vector out of an Array */
	public IndexVector(int[] data) {
		this.data = data;
	}
	
	/** generates a new Vector out of an ArrayList<Integer> */
	public IndexVector(ArrayList<Integer> data) {
		this.data = new int[data.size()];
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = data.get(i);
		}
	}
	
	/** @return a String representation of this object */
	@Override
	public String toString() {
		String s = "[";
		for (int i = 0; i < data.length; i++) {
			s += data[i];
			if (i < data.length-1) { s += "|"; }
		}
		return s + "]";
	}
	
	@Override
	public IndexVector clone() {
		return new IndexVector(this.data.clone());
	}

	/** returns the length of the Vector data */
	public int length(){
		return data.length;
	}

	public boolean contains(int value) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == value) {
				return true;
			}
		}
		return false;
	}
	
	/** @return the value at the position data[index] */
	public int get(int index){
		return data[index];
	}
	
	/**
	 * sets the value at data[index] to value
	 * @param value - the value to set
	 * @param index - the index where the values shall be set
	 */
	public void set(int value, int index){
		data[index] = value;
	}
	
	/**
	 * adds the valueToAdd tho the value at data[index]
	 * @param valueToAdd - the value to add
	 * @param index - the index where the values shall be added
	 */
	public void addTo(int valueToAdd, int index){
		data[index] += valueToAdd;
	}
	
	/**
	 * adds the valueToAdd to the value at data[index] and returns a new changed Vector. The old Vector stays unchanged.
	 * @param valueToAdd - the value to add
	 * @param index - the index from where the values shall be returned
	 */
	public IndexVector getChangedVector(int valueToAdd, int index){
		int[] data = this.data.clone();
		data[index] += valueToAdd;
		return new IndexVector(data);
	}
	
	/**
	 * adds the valueToAdd to the value at data[index] and returns a new changed data Array. The old Vector stays unchanged.
	 * @param valueToAdd -  the value to add
	 * @param index - the index where the values shall be added
	 */
	public int[] getChangedArray(int valueToAdd, int index){
		int[] data = this.data.clone();
		data[index] += valueToAdd;
		return data;
	}
	
	/** adds the valuesToAdd to the values at data[index] and returns a new changed Vector. The old Vector stays unchanged. */
	public IndexVector addToVector(int[] valuesToAdd){
		int[] data = this.data.clone();
		for (int i = 0; i < data.length; i++) {
			data[i] += valuesToAdd[i];
		}
		return new IndexVector(data);
	}
	
	/** adds the vectorToAdds data to the values at data[index] and returns a new changed Vector. The old Vector stays unchanged. */
	public IndexVector addToVector(IndexVector vectorToAdd){
		int[] data = this.data.clone();
		int[] valuesToAdd = vectorToAdd.toArray();
		for (int i = 0; i < data.length; i++) {
			data[i] += valuesToAdd[i];
		}
		return new IndexVector(data);
	}
	
	/** adds the valuesToAdd to the values at data[index] and returns a new changed data Array. The old Vector stays unchanged. */
	public int[] addToArray(int[] valuesToAdd){
		int[] data = this.data.clone();
		for (int i = 0; i < data.length; i++) {
			data[i] += valuesToAdd[i];
		}
		return data;
	}
	
	/** adds the vectorToAdds data to the values at data[index] and returns a new changed data Array. The old Vector stays unchanged. */
	public int[] addToArray(IndexVector vectorToAdd){
		int[] data = this.data.clone();
		int[] valuesToAdd = vectorToAdd.toArray();
		for (int i = 0; i < data.length; i++) {
			data[i] += valuesToAdd[i];
		}
		return data;
	}

	/** sets every entry of this vector to 0 */
	public void setToNullVector() {
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
		
	}
	
	/** @return true if this vector only contains zeros, false otherwise */
	public boolean isNullVector(){
		for (int i = 0; i < data.length; i++) {
			if (data[i] != 0) {
				return false;
			}
		}
		return true;
	}
	
	/** returns true if one entry is negative, false otherwise */
	public boolean hasNegetiveEntry(){
		for (int i = 0; i < data.length; i++) {
			if (data[i] < 0) {
				return true;
			}
		}
		return false;
	}
	
	/** @return the data as Array */
	public int[] toArray(){
		return data;
	}
}
