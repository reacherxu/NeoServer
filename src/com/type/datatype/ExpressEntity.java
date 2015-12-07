package com.type.datatype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.type.instance.GeneralizedInstance;

public class ExpressEntity extends ExpressGeneralizedDataType {

	private String name;
	
	private List<Map<GeneralizedInstance,List<String>>> list = new ArrayList<Map<GeneralizedInstance,List<String>>>();

	public ExpressEntity(Integer id, String name) {
		super(id);
		this.name = name;
	}


	public ExpressEntity(Integer id,String name, List<Map<GeneralizedInstance,List<String>>> list) {
		super(id);
		this.name = name;
		if (list != null) {
			this.list = list;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Map<GeneralizedInstance,List<String>>> getList() {
		return list;
	}

	public void setList(List<Map<GeneralizedInstance,List<String>>> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "ExpressEntity [name=" + name + ", body=" + list.toString() + "]";
	}


}
