package com.mustcsie.vrproject2;

public class ButtonStatus {
	private String name = "";
	private boolean status;
	public ButtonStatus(String name , boolean status){
		this.name = name	;
		this.status = status	;
	}
	public String getName() {
		return name;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus() {
		status = !status;
	}
}
