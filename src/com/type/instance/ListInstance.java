package com.type.instance;

import java.util.ArrayList;

import com.type.datatype.ExpressAggregation;
import com.type.datatype.ExpressGeneralizedDataType;


public class ListInstance extends IndexedCollectionInstance {

	private final ExpressAggregation dataType = (ExpressAggregation) super.dataType;

	public ListInstance(Integer id, String name, ExpressGeneralizedDataType dataType) {
		super(id, name, dataType);
		value = new ArrayList<GeneralizedInstance>();
		index = this.dataType.getBound1() - 1;
	}

	@Override
	public GeneralizedInstance get(Integer i) {

		if (isIndexLegal(i)) {
			return getList().get(this.indexModify(i));
		}
		return null;
	}


	@Override
	public Boolean remove(GeneralizedInstance object) {

		if (getList().remove(object)) {
			index--;
			return true;
		}

		return false;
	}

	@Override
	public Boolean remove(int i) {
		i = this.indexModify(i);
		if (getList().remove(i) != null) {
			index--;
			return true;
		}
		return false;
	}

	@Override
	protected Boolean isIndexLegal(Integer index) {

		if (dataType.getBound2() == null && index >= dataType.getBound1()) {
			return true;
		}
		else if (index <= dataType.getBound2() && index >= dataType.getBound1()) {
			return true;
		}

		return false;
	}

	@Override
	protected Integer indexModify(Integer i) {
		return i - dataType.getBound1();
	}

	@Override
	public Boolean isFull() {

		if (dataType.getBound2() == null) {
			return false;
		}
		else if (index >= dataType.getBound2()) {
			return true;
		}

		return false;
	}


	@Deprecated
	@Override
	public void setValue(Object value) {
		System.out.println(unknownFunction());
	}

	@Deprecated
	@Override
	public Object getValue() {
		System.out.println(unknownFunction());
		return null;
	}

	public static void main(String args[]) {
		/*ExpressString string = new ExpressString();
		ExpressList<ExpressString> dataType = new ExpressList<ExpressString>(string);
		dataType.getDataType().getClass();
		ListInstance<StringInstance, ExpressString> list = new ListInstance<StringInstance, ExpressString>(null, "array", dataType);
		list.add(new StringInstance(null, "a", (ExpressString) list.getElementDataType(), "a"));
		list.add(new StringInstance(null, "b", (ExpressString) list.getElementDataType(), "b"));
		list.add(new StringInstance(null, "c", (ExpressString) list.getElementDataType(), "c"));
		list.add(new StringInstance(null, "d", (ExpressString) list.getElementDataType(), "d"));

		System.out.println(list);

		list.remove(1);
		list.add(new StringInstance(null, "d", (ExpressString) list.getElementDataType(), "d"));
		System.out.println(list);*/
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
	public Integer getSize() {
		return index - dataType.getBound1() + 1;
	}

	@Override
	public Boolean add(GeneralizedInstance element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean add(Integer index, GeneralizedInstance element) {
		// TODO Auto-generated method stub
		return null;
	}
}
