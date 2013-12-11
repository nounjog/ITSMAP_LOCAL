package com.example.itsmap;

public class Friend {
	
	private long id;
	private String name;
	//TODO: Instead of timestamp there would be actual Date object
	// So that calculation could made of x seconds ago, x minutes ago to ListView
	private String timestamp;
	
	 // Will be used by the ArrayAdapter in the ListView
	 @Override
	 public String toString() {
	     return name;
	 }
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


}
