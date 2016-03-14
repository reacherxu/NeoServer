package com.type.instance;

import com.type.datatype.ExpressGeneralizedDataType;

public abstract class IndexedCollectionInstance extends AggregationInstance {

	protected Integer index = 0;

	public IndexedCollectionInstance(Integer id, String name, ExpressGeneralizedDataType dataType) {
		super(id, name, dataType);
	}

	public abstract GeneralizedInstance get(Integer index);

	public abstract Boolean add(GeneralizedInstance element);

	public abstract Boolean add(Integer index, GeneralizedInstance element);

	public abstract Boolean remove(int index);

	protected abstract Boolean isIndexLegal(Integer index);

	protected abstract Integer indexModify(Integer i);

}
