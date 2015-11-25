package com.type.entity;

import java.util.ArrayList;
import java.util.List;

import com.type.generalized.ObjectType;

public class Entity extends ObjectType {
	/* 标识类名  */
	private String name;
	
	private boolean isAbstract;
	
	private List<Attribute> attrs;
	
	private List<Entity> entities;
	
	public Entity(int id) {
		super(id);
		this.name = null;
		this.isAbstract = false;
		this.attrs = new ArrayList<Attribute>();
		this.entities = new ArrayList<Entity>();
	}
	
	public Entity(int id,String name,boolean isAbstract,List<Attribute> attrs,
			List<Entity> entities) {
		super(id);
		this.name = name;
		this.isAbstract = isAbstract;
		this.attrs = attrs;
		this.entities = entities;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public List<Attribute> getAttrs() {
		return attrs;
	}

	public void setAttrs(List<Attribute> attrs) {
		this.attrs = attrs;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

}
