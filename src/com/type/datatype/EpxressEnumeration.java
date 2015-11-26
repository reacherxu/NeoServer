package com.type.datatype;

import java.util.ArrayList;
import java.util.List;

public class EpxressEnumeration extends ExpressConstructedDataType {

	private Boolean isExtensible = false;
	private List<String> items = new ArrayList<String>();
	private EpxressEnumeration extension = null;

	public EpxressEnumeration(Integer id) {
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

	public EpxressEnumeration getExtension() {
		return extension;
	}

	public void setExtension(EpxressEnumeration extension) {
		this.extension = extension;
	}

	@Override
	public String toString() {
		return "EpxressEnumeration [isExtensible=" + isExtensible + ", items=" + items + ", extension=" + extension + "]";
	}

}
