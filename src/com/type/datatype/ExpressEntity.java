package com.type.datatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.type.instance.GeneralizedInstance;

public class ExpressEntity extends ExpressGeneralizedDataType {

	private String name;
	private Boolean isAbstract;
	List<String> superTypes = new ArrayList<String>();
	
	private Map<GeneralizedInstance,List<String>> map = new HashMap<GeneralizedInstance,List<String>>();
	protected List<GeneralizedInstance> instanceList = new ArrayList<GeneralizedInstance>();
	

	/**
	 * store unique rule name,and instances' names
	 */
	private Map<String,List<String>> uniqueList = new HashMap<String,List<String>>();

	public ExpressEntity(Integer id, String name) {
		super(id);
		this.name = name;
	}


	public ExpressEntity(Integer id,String name, Map<GeneralizedInstance,List<String>> map) {
		super(id);
		this.name = name;
		if (map != null) {
			this.map = map;
		}
	}

	
	public List<GeneralizedInstance> getInstanceList() {
		return instanceList;
	}


	public void setInstanceList() {
		this.instanceList.addAll(map.keySet());
	}

	// if it is necessary
	public void addAttribute(GeneralizedInstance instance, List<String> info) {
		map.put(instance, info);
		instanceList.add(instance);
	}
	
	public void addAttribute(Map<GeneralizedInstance,List<String>> map) {
		this.map.putAll(map);
		instanceList.addAll(map.keySet());
	}
	
	public List<String> getSuperTypes() {
		return superTypes;
	}


	public void setSuperTypes(List<String> superTypes) {
		this.superTypes = superTypes;
	}


	public Boolean getIsAbstract() {
		return isAbstract;
	}


	public void setIsAbstract(Boolean isAbstract) {
		this.isAbstract = isAbstract;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Map<GeneralizedInstance, List<String>> getMap() {
		return map;
	}


	public void setMap(Map<GeneralizedInstance, List<String>> map) {
		this.map = map;
	}


	public Map<String, List<String>> getUniqueList() {
		return uniqueList;
	}


	public void setUniqueList(Map<String, List<String>> uniqueList) {
		this.uniqueList = uniqueList;
	}


	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("ExpressEntity [name=" + name + ", body=");
		for(Map.Entry<GeneralizedInstance,List<String>> entry : map.entrySet() ) 
			str.append(entry.getKey() + "=" + entry.getValue() + "\n");
		return str.append(",uniqueList= " + uniqueList + ",superTypes= " + superTypes + "]").toString() ;
	}


}
