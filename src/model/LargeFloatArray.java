package model;

import java.lang.reflect.Field;

public class LargeFloatArray {
	private final static int FLOAT_BYTE_SIZE = 4;
	
	private long address;
	private long length;
	
	
	public static sun.misc.Unsafe getUnsafe() {
	    try {
	        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
	        f.setAccessible(true);
	        return (sun.misc.Unsafe) f.get(null);
	    } catch (Exception e) { /* ... */ }
	    return null;
	}
	
	public LargeFloatArray(long length) {
		this.length = length;
		address = getUnsafe().allocateMemory(length * FLOAT_BYTE_SIZE);
	}
	
	public void set(float value, long index) {
	    getUnsafe().putFloat(address + index * FLOAT_BYTE_SIZE, value);
	  }
	    
	  public float get(long index) {
	    return getUnsafe().getFloat(address + index * FLOAT_BYTE_SIZE);   
	  }
	    
	  public void free() {
	    getUnsafe().freeMemory(address);
	}

	public long getLength() {
		return length;
	}
}
