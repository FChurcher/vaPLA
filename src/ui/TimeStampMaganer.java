package ui;

public class TimeStampMaganer {
  // singleton intrinsic
	/** the singleton instance */
	protected static TimeStampMaganer instance;
	
	/**
	 * singleton get instance method
	 * @return an instance of this class
	 */
	public static TimeStampMaganer getInstance(){
		if (instance == null) {
			instance = new TimeStampMaganer();
		}
		return instance;
	}
	
  // object defenition
	/** the very first time stamp of this run */
	long firstStamp;
	/** the last time stamp */
	long lastStamp;
	
	public TimeStampMaganer() {
		this.firstStamp = getSystemSeconds();
		this.lastStamp = getSystemSeconds();
	}
	
	public void printGuide() {
		System.out.println("how to read the TimeStamps:");
		System.out.println("[whole run time|difference to last stamp] messege");
	}
	
	public void printTimeStamp(String messege) {
		long actualStamp = getSystemSeconds();
		long differenceToFirst = actualStamp - firstStamp;
		long differenceToLast = actualStamp - lastStamp;
		this.lastStamp = actualStamp;
		
		System.out.println("[" + differenceToFirst + "|" + differenceToLast + "]\t" + messege);
	}
	
	public long getActualRunTime() {
		long actualStamp = getSystemSeconds();
		return actualStamp - firstStamp;
	}
	
	public long getSystemSeconds() {
		return System.currentTimeMillis();
	}
}
