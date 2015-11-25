package com.test;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class TestNeo {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) {
		try {
			Properties properties = new Properties();
			properties.put("user", "neo4j");
			properties.put("password", "reacher");
			Connection con = DriverManager.getConnection("jdbc:neo4j://172.26.13.122:7474/",properties);

			
//			String query = "MATCH (:Movie {title:{1}})<-[:ACTED_IN]-(a:Person) RETURN a.name as actor";
			String query = "start n = node(*) where n.sex='male' return n";
			PreparedStatement stmt = con.prepareStatement(query);
//		    stmt.setString(1,"The Matrix");

		    try {
		    	ResultSet rs = stmt.executeQuery();
		        while(rs.next()) {
		             System.out.println(rs.getString("n"));
		        }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
