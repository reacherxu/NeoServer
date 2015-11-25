package com.type.generalized;

public class Real extends GenaralizedType {

	//正整数指明小数位数
	private int precimsion_spec;

	public Real(int id,int precimsion_spec) {
		super(id);
		this.precimsion_spec = precimsion_spec;
	}
	
	public int getBit() {
		return precimsion_spec;
	}

	public void setBit(int precimsion_spec) {
		this.precimsion_spec = precimsion_spec;
	}
	
	
}
