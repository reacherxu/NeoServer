package com.type.instance;

import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressEntity;

public class EntityInstance extends GeneralizedInstance {

	public EntityInstance(Integer id, String name, ExpressEntity dataType) {
		super(id, name, dataType);
	}
	
	public Map<GeneralizedInstance,List<String>> getAttributes() {
		return ((ExpressEntity) dataType).getMap();
	}



}
