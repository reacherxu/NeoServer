package com.type.datatype;

import java.util.ArrayList;
import java.util.List;

public class ExpressEnumeration extends ExpressConstructedDataType {
	
	private Boolean isExtensible = false;
	private List<String> items = new ArrayList<String>();
	private EpxressEnumeration extensions = null;

	public ExpressEnumeration(Integer id) {
		super(id);
	}

	public Boolean getIsExtensible() {
		return isExtensible;
	}

	public void setIsExtensible(Boolean isExtensible) {
		this.isExtensible = isExtensible;
	}

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public EpxressEnumeration getExtensions() {
		return extensions;
	}

	public void setExtensions(EpxressEnumeration extensions) {
		this.extensions = extensions;
	}
	
	
}
