package com.type.datatype;

import java.util.ArrayList;
import java.util.List;

public class ExpressSelect extends ExpressConstructedDataType {

	List<ExpressGeneralizedDataType> list = new ArrayList<ExpressGeneralizedDataType>();

	public ExpressSelect(Integer id) {
		super(id);
	}

	public List<ExpressGeneralizedDataType> getList() {
		return list;
	}

	public void setList(List<ExpressGeneralizedDataType> list) {
		this.list = list;
	}

}
