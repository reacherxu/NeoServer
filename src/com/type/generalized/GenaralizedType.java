package com.type.generalized;

/**
 * 基础数据类型的父类
 * @author Reacher
 *
 */
public abstract class GenaralizedType extends ObjectType {
	public GenaralizedType(int id) {
		super(id);
	}

	@Override
	public String toString() {
		return "generalized types";
	}
}
