package com.xzt;

import java.net.URI;
import java.net.URISyntaxException;

import com.xzt.NeoGraphDB;

public class TestCreateGraph {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws URISyntaxException {
		NeoGraphDB neo = new NeoGraphDB();
		
		neo.checkDatabaseIsRunning();

		// add nodes And Props
		URI firstNode = neo.createNode();
		neo.addProperty(firstNode, "name", "Joe Strummer");
		URI secondNode = neo.createNode();
		neo.addProperty(secondNode, "band", "The Clash");

		// add Rels
		URI relationshipUri = neo.addRelationship(firstNode, secondNode, "singer",
				"{ \"from\" : \"1976\", \"until\" : \"1986\" }");

		// add Meta To Rel
		neo.addMetadataToProperty(relationshipUri, "stars", "5");

		// query For Singers
		neo.findSingersInBands(firstNode);
	}
}
