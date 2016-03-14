package com.type.instance;

import java.util.List;

import com.type.datatype.ExpressGeneralizedDataType;

public abstract class NoIndexedCollectionInstance extends AggregationInstance {

	protected Integer size = 0;

	public NoIndexedCollectionInstance(Integer id, String name, ExpressGeneralizedDataType dataType) {
		super(id, name, dataType);
	}

	@Override
	public Integer getSize() {
		return size;
	}

	public abstract Boolean add(GeneralizedInstance element);

	public abstract Boolean setCollection(List<GeneralizedInstance> list);

	public abstract Boolean setCollection(Integer bound1, Integer bound2, List<GeneralizedInstance> list);
}
