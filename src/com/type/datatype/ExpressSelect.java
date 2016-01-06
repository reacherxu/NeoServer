package com.type.datatype;

import java.util.ArrayList;
import java.util.List;

public class ExpressSelect extends ExpressConstructedDataType {
	
	private Boolean isExtensible = false;
	private List<ExpressGeneralizedDataType> list = new ArrayList<ExpressGeneralizedDataType>();
	private ExpressSelect extension = null;

	public ExpressSelect(Integer id) {
		super(id);
	}

	public Boolean getIsExtensible() {
		return isExtensible;
	}

	public void setIsExtensible(Boolean isExtensible) {
		this.isExtensible = isExtensible;
	}

	public List<ExpressGeneralizedDataType> getList() {
		return list;
	}

	public void setList(List<ExpressGeneralizedDataType> list) {
		this.list = list;
	}

	public ExpressSelect getExtension() {
		return extension;
	}

	public void setExtension(ExpressSelect extension) {
		this.extension = extension;
	}
	
	
	
}
