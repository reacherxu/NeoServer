package com.type.instance;

import java.util.ArrayList;
import java.util.List;

import com.type.datatype.ExpressAggregation;
import com.type.datatype.ExpressGeneralizedDataType;

public class SetInstance extends NoIndexedCollectionInstance {

	private final ExpressAggregation dataType =  (ExpressAggregation)super.dataType;

	public SetInstance(Integer id, String name, ExpressAggregation dataType) {
		super(id, name, dataType);
		value = new ArrayList<GeneralizedInstance>();
	}

	@Override
	public Boolean add(GeneralizedInstance element) {
		if (!isFull() && !isExist(element)) {

			getList().add(element);
			size++;
			return true;
		}

		return false;
	}

	@Override
	public Boolean remove(GeneralizedInstance object) {

		if (getList().remove(object)) {
			size--;
			return true;
		}
		return false;
	}

	@Override
	public Boolean isFull() {

		if (dataType.getBound2() == null) {
			return false;
		}
		else if (size >= dataType.getBound2()) {
			return true;
		}

		return false;
	}

	@Deprecated
	@Override
	public void setValue(Object value) {
		System.out.println("Array无此方法");
	}

	@Deprecated
	@Override
	public Object getValue() {
		System.out.println("Array无此方法");
		return null;
	}

	public static void main(String args[]) {
	}

	/*@Override
	public String toString() {
		String result = this.getClass().getSimpleName() + " " + getName() + ":\n";
		for (T t : getList()) {
			result += "[" + t.toString() + "],\n";
		}

		return result.endsWith(",\n") ? result.substring(0, result.length() - 2) + "\n" : result;
	}*/

	@Override
	protected ExpressGeneralizedDataType getElementDataType() {
		return dataType.getDataType();
	}

	@Override
	public ExpressAggregation getDataType() {
		return dataType;
	}

	@Override
	public Boolean setCollection(List<GeneralizedInstance> list) {
		if (list.size() >= dataType.getBound1() && list.size() <= dataType.getBound2()) {
			for (GeneralizedInstance element : list) {
				this.add(element);
			}
			return true;
		}
		return false;
	}

	@Override
	public Boolean setCollection(Integer bound1, Integer bound2, List<GeneralizedInstance> list) {
		dataType.setBound1(bound1);
		dataType.setBound2(bound2);

		return this.setCollection(list);
	}

}
