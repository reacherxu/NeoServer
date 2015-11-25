package com.type.entity;

import com.type.generalized.ObjectType;

public class Attribute extends ObjectType {

	private ObjectType attr;
	/*创建实例时必须给出值的属性*/
	private boolean explicit;
	/*通过某种计算而得到值的属性*/
	private String derived;
	
	
	public Attribute(int id) {
		super(id);
		this.attr = null;
		this.explicit = false;
		this.derived = null;
	}
	
	public Attribute(int id,ObjectType attr,boolean explicit,
				String derived) {
		super(id);
		this.attr = attr;
		this.explicit = explicit;
		this.derived = derived;
	}

	public ObjectType getAttr() {
		return attr;
	}

	public void setAttr(ObjectType attr) {
		this.attr = attr;
	}

	public boolean isExplicit() {
		return explicit;
	}

	public void setExplicit(boolean explicit) {
		this.explicit = explicit;
	}

	public String getDerived() {
		return derived;
	}

	public void setDerived(String derived) {
		this.derived = derived;
	}
	
}
