package com.xzt;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class NeoGraphDB {

	private static final String SERVER_ROOT_URI = "http://172.26.13.122:7474/db/data/";

	public static void main(String[] args) {
		NeoGraphDB neo = new NeoGraphDB();
		neo.launchDB();
		
		/*neo.checkDatabaseIsRunning();
		
		URI firstNode = neo.getNodesURI(String.valueOf(0));
		
		URI thirdNode = neo.getNodesURI(String.valueOf(2));
		neo.addProperty(thirdNode, "band", "Twins");
		
		try {
			URI relationshipUri = neo.addRelationship(firstNode, thirdNode, "singer",
					"{ \"from\" : \"1946\", \"until\" : \"1966\" }");
			neo.addMetadataToProperty(relationshipUri, "gender", "female");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}*/
		
	}
	
	public void launchDB() {
		checkDatabaseIsRunning();

		// add nodes and properties
		URI firstNode = createNode();
		addProperty(firstNode, "name", "Kobe Bryant");
		URI secondNode = createNode();
		addProperty(secondNode, "band", "LeBron James");

		// add relationships
		URI relationshipUri;
		try {
			relationshipUri = addRelationship(firstNode, secondNode, "feat",
					"{ \"in\" : \"2008\" }");

			// add metadata to relationships
			addMetadataToProperty(relationshipUri, "NBA Final", "MVP");

			// query for singers
			findSingersInBands(firstNode);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}
	
	//TODO   cypher查询待看
	public String findByParam(String param) {
		String cypherUri = SERVER_ROOT_URI + "cypher"; 

	    String queryStr = "start n = node(*) return n." + param ;	//查询所有的source_id属性。

	    JSONObject jsObject = new JSONObject();
	    jsObject.put("query", queryStr);
	    WebResource resource = Client.create().resource( cypherUri );
	    ClientResponse response = resource.accept( MediaType.APPLICATION_JSON_TYPE )
					.type( MediaType.APPLICATION_JSON_TYPE ) .entity( jsObject.toString() ).post( ClientResponse.class );
	    String returnStr=String.format( "POST [%s] to [%s], status code [%d], RETURNED DATA: "+ System.getProperty( "line.separator" ) + "%s", queryStr, cypherUri, response.getStatus(), response.getEntity( String.class ) ) ;
	    String resultData=returnStr.split("RETURNED DATA:")[1];
	    JSONObject resultObj = JSONObject.fromObject(resultData);
	    String resultStr = resultObj.getString("data");
	    if(null != resultStr && !"".equals(resultStr)){
	    	resultStr = resultStr.replace("[", "").replace("]", "").replace("\"", "");
	    }else{
	    	resultStr = "";
	    }
	    response.close();
	    return resultStr; 
	}
	
	//TODO　
	public void findSingersInBands(URI startNode)
			throws URISyntaxException {
		// TraversalDescription turns into JSON to send to the Server
		TraversalDescription t = new TraversalDescription();
		t.setOrder(TraversalDescription.DEPTH_FIRST);
		t.setUniqueness(TraversalDescription.NODE);
		t.setMaxDepth(10);
		t.setReturnFilter(TraversalDescription.ALL);
		t.setRelationships(new Relationship("singer", Relationship.OUT));

		URI traverserUri = new URI(startNode.toString() + "/traverse/node");
		WebResource resource = Client.create().resource(traverserUri);
		String jsonTraverserPayload = t.toJson();
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(jsonTraverserPayload)
				.post(ClientResponse.class);

		System.out.println(String.format(
				"POST [%s] to [%s], status code [%d], returned data: "
						+ System.getProperty("line.separator") + "%s",
				jsonTraverserPayload, traverserUri, response.getStatus(),
				response.getEntity(String.class)));
		response.close();
	}

	/**
	 * 为关系添加属性
	 * @param relationshipUri   关系
	 * @param name				key
	 * @param value				value
	 * @throws URISyntaxException
	 */
	public void addMetadataToProperty(URI relationshipUri, String name,
			String value) throws URISyntaxException {
		URI propertyUri = new URI(relationshipUri.toString() + "/properties");
		String entity = toJsonNameValuePairCollection(name, value);
		WebResource resource = Client.create().resource(propertyUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(entity)
				.put(ClientResponse.class);

		System.out.println(String.format("PUT [%s] to [%s], status code [%d]",
				entity, propertyUri, response.getStatus()));
		response.close();
	}


	public String toJsonNameValuePairCollection(String name,
			String value) {
		return String.format("{ \"%s\" : \"%s\" }", name, value);
	}

	public URI createNode() {
		final String nodeEntryPointUri = SERVER_ROOT_URI + "node";
		// http://localhost:7474/db/data/node

		WebResource resource = Client.create().resource(nodeEntryPointUri);
		// POST {} to the node entry point URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity("{}")
				.post(ClientResponse.class);

		final URI location = response.getLocation();
		System.out.println(String.format(
				"POST to [%s], status code [%d], location header [%s]",
				nodeEntryPointUri, response.getStatus(), location.toString()));
		response.close();

		return location;
	}
	
	/**
	 * 根据已知节点ID获取节点
	 * @param exist
	 * @return
	 */
	public URI getNodesURI(String exist) {	
		String nodeEntryPointUri = SERVER_ROOT_URI + "node/" + exist.replace("\"", "");
		URI location = URI.create(nodeEntryPointUri);
		return location;
	}

	/**
	 * 增加neo4j的两个节点之间的关系
	 * @param startNode  起始节点的URI
	 * @param endNode    指向节点的URI
	 * @param relationshipType
	 * @param jsonAttributes
	 * @return
	 * @throws URISyntaxException
	 */
	public URI addRelationship(URI startNode, URI endNode,
			String relationshipType, String jsonAttributes)
			throws URISyntaxException {
		URI fromUri = new URI(startNode.toString() + "/relationships");
		String relationshipJson = generateJsonRelationship(endNode,
				relationshipType, jsonAttributes);

		WebResource resource = Client.create().resource(fromUri);
		// POST JSON to the relationships URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(relationshipJson)
				.post(ClientResponse.class);

		final URI location = response.getLocation();
		System.out.println(String.format(
				"POST to [%s], status code [%d], location header [%s]",
				fromUri, response.getStatus(), location.toString()));

		response.close();
		return location;
	}

	public String generateJsonRelationship(URI endNode,
			String relationshipType, String... jsonAttributes) {
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"to\" : \"");
		sb.append(endNode.toString());
		sb.append("\", ");

		sb.append("\"type\" : \"");
		sb.append(relationshipType);
		if (jsonAttributes == null || jsonAttributes.length < 1) {
			sb.append("\"");
		} else {
			sb.append("\", \"data\" : ");
			for (int i = 0; i < jsonAttributes.length; i++) {
				sb.append(jsonAttributes[i]);
				if (i < jsonAttributes.length - 1) { // Miss off the final comma
					sb.append(", ");
				}
			}
		}

		sb.append(" }");
		return sb.toString();
	}

	/**
	 * 添加节点关系
	 * @param nodeUri    节点
	 * @param propertyName    key
	 * @param propertyValue   value
	 */
	public void addProperty(URI nodeUri, String propertyName,
			String propertyValue) {
		String propertyUri = nodeUri.toString() + "/properties/" + propertyName;
		// http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

		WebResource resource = Client.create().resource(propertyUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.entity("\"" + propertyValue + "\"").put(ClientResponse.class);

		System.out.println(String.format("PUT to [%s], status code [%d]",
				propertyUri, response.getStatus()));
		response.close();
	}

	/**
	 * 连接是否正常
	 * 2XX  正常
	 * 4XX 5XX  不正常
	 */
	public void checkDatabaseIsRunning() {
		WebResource resource = Client.create().resource(SERVER_ROOT_URI);
		ClientResponse response = resource.get(ClientResponse.class);

		System.out.println(String.format("GET on [%s], status code [%d]",
				SERVER_ROOT_URI, response.getStatus()));
		response.close();
	}

}
