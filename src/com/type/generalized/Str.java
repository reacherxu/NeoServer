package com.type.generalized;

/**
 * generalized data types:String
 * @author Reacher
 *
 */
public class Str extends GenaralizedType {
	private int width;
	private boolean fixed;
	
	public Str(){
		super(-1);
		this.fixed = false;
		this.width = 0;
	}
	
	public Str(int id,int width, boolean fixed) {
		super(id);
		this.width = width;
		this.fixed = fixed;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	@Override
	public String toString() {
		return "[id:" + this.getId() + ",width:" + this.width + ",fixed:" + this.fixed + "]";
	}
	
}
