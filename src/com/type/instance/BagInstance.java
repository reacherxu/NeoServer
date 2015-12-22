package com.type.instance;

import java.util.ArrayList;
import java.util.List;

import com.type.datatype.ExpressBag;
import com.type.datatype.ExpressGeneralizedDataType;

public class BagInstance<T extends GeneralizedInstance, E extends ExpressGeneralizedDataType> extends NoIndexedCollectionInstance<T, E> {

	@SuppressWarnings("unchecked")
	private final ExpressBag<E> dataType = (ExpressBag<E>) super.dataType;

	public BagInstance(Integer id, String name, ExpressBag<E> dataType) {
		super(id, name, dataType);
		value = new ArrayList<T>();
	}

	@Override
	public Boolean add(T element) {
		if (!isFull()) {

			getList().add(element);
			size++;
			return true;
		}

		return false;
	}

	@Override
	public Boolean remove(T object) {

		if (getList().remove(object)) {
			size--;
			return true;
		}
		return false;
	}

	@Override
	public Boolean isFull() {

		if (size >= dataType.getBound2()) {
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
		/*ExpressString string = new ExpressString();
		ExpressBag<ExpressString> dataType = new ExpressBag<ExpressString>(0, 10, string);
		BagInstance<StringInstance, ExpressString> bag = new BagInstance<StringInstance, ExpressString>(null, "array", dataType);
		bag.add(new StringInstance(null, "a", (ExpressString) bag.getElementDataType(), "a"));
		bag.add(new StringInstance(null, "b", (ExpressString) bag.getElementDataType(), "b"));
		bag.add(new StringInstance(null, "c", (ExpressString) bag.getElementDataType(), "c"));
		bag.add(new StringInstance(null, "d", (ExpressString) bag.getElementDataType(), "d"));

		System.out.println(bag);*/
	}

	@Override
	public String toString() {
		String result = this.getClass().getSimpleName() + " " + getName() + ":\n";
		for (T t : getList()) {
			result += "[" + t.toString() + "],\n";
		}

		return result.endsWith(",\n") ? result.substring(0, result.length() - 2) + "\n" : result;
	}

	@Override
	protected ExpressGeneralizedDataType getElementDataType() {
		return dataType.getDataType();
	}

	@Override
	public ExpressBag<E> getDataType() {
		return dataType;
	}

	@Override
	public Boolean setCollection(List<T> list) {
		if (list.size() >= dataType.getBound1() && list.size() <= dataType.getBound2()) {
			value = list;

			return true;
		}
		return false;
	}

	@Override
	public Boolean setCollection(Integer bound1, Integer bound2, List<T> list) {
		dataType.setBound1(bound1);
		dataType.setBound2(bound2);

		return this.setCollection(list);
	}

}
