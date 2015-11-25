package com.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.neo4j.jdbc.Driver;
import org.neo4j.jdbc.Neo4jConnection;

public class TestNeo2 {

	public static void main(String[] args) {
		
//		BasicConfigurator.configure();
		
		Neo4jConnection conn = null;
		ResultSet rs = null;  
		try{
			final Driver driver = new Driver();    			
			final Properties props = new Properties();
			props.put("user", "neo4j");
			props.put("password", "reacher");   
			String url="jdbc:neo4j://172.26.13.122:7474";
			conn = driver.connect(url, props); 
			
			/*String query = "start n = node(*) where n.name='Reacher' return n.sex";   //query
			//String query = "Create ( n:Person {name:'Reacher',sex:'male'} )" ;  //create
//			String query = "start n = node(*) where n.name='Reacher' delete n";   //delete
//			String query = "match (n) where n.name='Reacher' set n.sex='male' ";  //update
			
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				System.out.println(rs.getString("n.name"));
			}        	*/		
			
			String query = "MATCH (:Movie {title:{1}})<-[:ACTED_IN]-(a:Person) RETURN a.name as actor";
			PreparedStatement stmt = conn.prepareStatement(query);
		    stmt.setString(1,"The Matrix");

		    try {
		    	rs = stmt.executeQuery();
		        while(rs.next()) {
		             System.out.println(rs.getString("actor"));
		        }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
