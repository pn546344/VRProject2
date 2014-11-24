package com.mustcsie.vrproject2;

public class TagDetail {

	private int id;
	private float x0,x1,y0,y1;
	private boolean isSurvival = true;
	public TagDetail(int id,float x0 ,float x1,float y0,float y1){
		this.id = id;
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
	}
	
	public void setX0(float x0) {
		this.x0 = x0;
	}
	
	public void setX1(float x1) {
		this.x1 = x1;
	}

	public void setY0(float y0) {
		this.y0 = y0;
	}

	public void setY1(float y1) {
		this.y1 = y1;
	}
	
	public int getId() {
		return id;
	}
	
	public float getX0() {
		return x0;	
	}
	
	public float getX1() {
		return x1;
	}
	
	public float getY0() {
		return y0;
	}
	
	public float getY1() {
		return y1;
	}
	
	public void setIsSurvival(boolean change) {
		isSurvival = change;
	}
	
	public boolean getIsSurvival() {
		return	isSurvival;
	}
	
}
