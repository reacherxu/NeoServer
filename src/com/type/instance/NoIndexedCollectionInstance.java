package com.type.instance;

import java.util.List;

import com.type.datatype.ExpressGeneralizedDataType;

public abstract class NoIndexedCollectionInstance<T extends GeneralizedInstance, E extends ExpressGeneralizedDataType> extends AggregationInstance<T, E> {

	protected Integer size = 0;

	public NoIndexedCollectionInstance(Integer id, String name, ExpressGeneralizedDataType dataType) {
		super(id, name, dataType);
	}

	@Override
	public Integer getSize() {
		return size;
	}

	public abstract Boolean add(T element);

	public abstract Boolean setCollection(List<T> list);

	public abstract Boolean setCollection(Integer bound1, Integer bound2, List<T> list);
}
