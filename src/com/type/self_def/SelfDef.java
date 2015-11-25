package com.type.self_def;

import com.type.generalized.ObjectType;

public class SelfDef extends ObjectType {

	private String oldName;
	
	private String newName;
	
	public SelfDef(int id) {
		super(id);
	}

	public SelfDef(int id,String oldName,String newName) {
		super(id);
		this.oldName = oldName;
		this.newName = newName;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}
	
}
