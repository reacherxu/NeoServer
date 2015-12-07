package com.type.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressEntity;

public class EntityInstance extends GeneralizedInstance {

	public EntityInstance(Integer id, String name, ExpressEntity dataType) {
		super(id, name, dataType);
	}
	
	public List<Map<GeneralizedInstance,List<String>>> getAttributes() {
		return ((ExpressEntity) dataType).getList();
	}

	//TODO 
	public List<String> getAttributeNames() {
//		List<Map<GeneralizedInstance,List<String>>> list = ((ExpressEntity) dataType).getList();
		List<String> attributeNames = new ArrayList<String>();
		/*for (Map<GeneralizedInstance, List<String>> ins : list) {
			attributeNames.add(ins.ge);
		}*/

		return attributeNames;
	}


}
