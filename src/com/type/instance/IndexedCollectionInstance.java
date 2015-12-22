package com.type.instance;

import com.type.datatype.ExpressGeneralizedDataType;

public abstract class IndexedCollectionInstance<T extends GeneralizedInstance, E extends ExpressGeneralizedDataType> extends AggregationInstance<T, E> {

	protected Integer index = 0;

	public IndexedCollectionInstance(Integer id, String name, ExpressGeneralizedDataType dataType) {
		super(id, name, dataType);
	}

	public abstract T get(Integer index);

	public abstract Boolean add(T element);

	public abstract Boolean add(Integer index, T element);

	public abstract Boolean remove(int index);

	protected abstract Boolean isIndexLegal(Integer index);

	protected abstract Integer indexModify(Integer i);

}
