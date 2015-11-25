package com.type.generalized;

public class Binary extends GenaralizedType {
	private int width;
	private boolean fixed;
	
	public Binary(){
		/*-1 表示数据库没有节点对应*/
		super(-1);
		this.fixed = false;
		this.width = 0;
	}
	
	public Binary(int id,int width, boolean fixed) {
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
