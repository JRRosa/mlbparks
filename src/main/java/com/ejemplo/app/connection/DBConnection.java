package com.ejemplo.app.connection;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.ejemplo.app.connection.consts.ParameterConnection;
import com.mongodb.DB;
import com.mongodb.Mongo;

@Named
@ApplicationScoped
public class DBConnection {
	
	private DB mongoDB;
	
	public DBConnection() {
		super();
	}
	
	@PostConstruct
	public void afterCreate() {

		Mongo mongo = null;
		try {
			mongo = new Mongo(ParameterConnection.MONGO_DB_HOST, ParameterConnection.MONGO_DB_PORT);
		} catch (UnknownHostException e) {
			System.out.println("Couldn't connect to MongoDB: " + e.getMessage() + " :: " + e.getClass());
		}
		
		mongoDB = mongo.getDB(ParameterConnection.APP_NAME);
		
		if (!mongoDB.authenticate(ParameterConnection.MONGO_DB_USERNAME, ParameterConnection.MONGO_DB_PASSWORD.toCharArray())) {
			System.out.println("Failed to authenticate DB ");
		}
	}
	
	public DB getDB() {
		return mongoDB;
	}
	
	
}
