package com.type.constructed;

import java.util.ArrayList;
import java.util.List;

import com.type.generalized.ObjectType;

public class Enumeration extends ObjectType {

	private List<String> elems;

	public Enumeration(int id) {
		super(id);
		this.elems = new ArrayList<String>();
	}
	
	public Enumeration(int id, List<String> elems) {
		super(id);
		this.elems = elems;
	}

	public List<String> getElems() {
		return elems;
	}

	public void setElems(List<String> elems) {
		this.elems = elems;
	}

	public int compareTo(String obj1,String obj2) {
		int idx1 = this.elems.indexOf(obj1);
		int idx2 = this.elems.indexOf(obj2);
		return idx1-idx2;
	}
}
