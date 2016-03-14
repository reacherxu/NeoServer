package com.type.instance;

import java.util.List;

import com.type.datatype.ExpressGeneralizedDataType;

public abstract class AggregationInstance extends GeneralizedInstance {

	public AggregationInstance(Integer id, String name, ExpressGeneralizedDataType dataType) {
		super(id, name, dataType);
	}

	// public abstract T get(Integer index);

	// public abstract Boolean add(T element);

	// public abstract Boolean add(Integer index, T element);

	// public abstract Boolean remove(int index);

	public abstract Boolean remove(GeneralizedInstance object);

	public abstract Integer getSize();

	public abstract Boolean isFull();

	protected String unknownFunction() {
		return this.getClass().getSimpleName() + "无此方法";
	}

	
	// protected abstract Boolean isIndexLegal(Integer index);

	protected abstract ExpressGeneralizedDataType getElementDataType();

	@SuppressWarnings("unchecked")
	protected List<GeneralizedInstance> getList() {
		return (List<GeneralizedInstance>) value;
	}

	// protected abstract Integer indexModify(Integer i);

	public Boolean isExist(GeneralizedInstance element) {
		List<GeneralizedInstance> list = this.getList();
		if (list == null) {
			return false;
		}

		for (GeneralizedInstance t : list) {
			if (element.equals(t)) {
				return true;
			}
		}

		return false;
	}

}
