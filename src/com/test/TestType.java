package com.test;

import java.util.Arrays;
import java.util.List;

import com.type.constructed.Enumeration;

public class TestType {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] strArray = new String[] { "aa", "bb","cc","dd" };
		List<String> list = Arrays.asList(strArray);
		Enumeration e = new Enumeration(0,list);
		System.out.println(e.compareTo(strArray[3], strArray[2]));
	}

}
