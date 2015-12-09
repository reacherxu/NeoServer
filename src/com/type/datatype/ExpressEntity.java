package com.type.datatype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.type.instance.GeneralizedInstance;

public class ExpressEntity extends ExpressGeneralizedDataType {

	private String name;
	
	private Map<GeneralizedInstance,List<String>> map = new HashMap<GeneralizedInstance,List<String>>();

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


	@Override
	public String toString() {
		return "ExpressEntity [name=" + name + ", body=" + map.toString() + "]";
	}


}
