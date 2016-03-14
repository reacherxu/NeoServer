package com.type.datatype;

import java.util.ArrayList;
import java.util.List;

public class ExpressEnumeration extends ExpressConstructedDataType {

	private String name = null;
	private Boolean isExtensible = false;
	private List<String> items = new ArrayList<String>();
	private ExpressEnumeration extension = null;  

	public ExpressEnumeration(Integer id) {
		super(id);
	}
	
	public ExpressEnumeration(Integer id,String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public ExpressEnumeration getExtension() {
		return extension;
	}

	public void setExtension(ExpressEnumeration extension) {
		this.extension = extension;
	}

	@Override
	public String toString() {
		return "ExpressEnumeration [name=" + name + ", isExtensible=" + isExtensible + ", items=" + items + ", extension=" + extension + "]";
	}

}
