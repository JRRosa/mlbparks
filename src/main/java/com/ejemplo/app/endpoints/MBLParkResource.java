package com.ejemplo.app.endpoints;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.ejemplo.app.connection.DBConnection;
import com.ejemplo.app.endpoints.consts.ParameterEndPoint;
import com.ejemplo.app.entity.MLBPark;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


@Path("/parks")
@Produces("application/json")
public class MBLParkResource {
	
	@Inject
	private DBConnection dbConnection;
	
	private DBCollection getMLBParksCollection() {
		DB db = dbConnection.getDB();
		DBCollection parkListCollection = db.getCollection(ParameterEndPoint.TEAMS);
		return parkListCollection;
	}
	
	private MLBPark populateParkInformation(DBObject dataValue) {
		MLBPark park = new MLBPark();
		park.setId(dataValue.get(ParameterEndPoint.ID));
		park.setName(dataValue.get(ParameterEndPoint.NAME));
		park.setPosition(dataValue.get(ParameterEndPoint.COORDINATES));
		park.setBallpark(dataValue.get(ParameterEndPoint.BALLPARK));
		park.setLeague(dataValue.get(ParameterEndPoint.LEAGUE));
		park.setPayroll(dataValue.get(ParameterEndPoint.PAYROLL));
		return park;
	}
	
	@GET()

	public List<MLBPark> getAllParks(){
		ArrayList<MLBPark> allParks = new ArrayList<MLBPark>();
		
		DBCollection mblPark = this.getMLBParksCollection();
		DBCursor cursor =  mblPark.find();
		try {
			for (DBObject dbObject : cursor) {
				allParks.add(this.populateParkInformation(dbObject));
			}
		} catch (Exception ex) {
			System.out.println("Error : " + this.getClass() + " getAllParks "+ ex.getMessage());
		}finally {
			cursor.close();
		}
		return allParks;
	}
	
	@GET()
	@Path("within")
	public List<MLBPark> findParkWithin (@QueryParam("lat1") float lat1, @QueryParam("lat2") float lat2, @QueryParam("lon1") float lon1, @QueryParam("lon2") float lon2){
		ArrayList<MLBPark> allParks = new ArrayList<MLBPark>();
		
		DBCollection mlbPark = this.getMLBParksCollection();
		
		BasicDBObject spatialQuery = new BasicDBObject();
		ArrayList<double []> boxList = new ArrayList<double []>();
		boxList.add(new double [] {new Float(lon2), new Float(lat2)});
		boxList.add(new double [] {new Float(lon1), new Float(lat1)});
		
		BasicDBObject boxQuery = new BasicDBObject();
		boxQuery.put("$box", boxList);
		
		spatialQuery.put("coordinates", new BasicDBObject("$within", boxQuery));
		System.out.println("Using spatial query : "+ spatialQuery.toString());
		
		DBCursor cursor = mlbPark.find(spatialQuery);
		
		try {
			for (DBObject dbObject : cursor) {
				allParks.add(this.populateParkInformation(dbObject));
			}
		} catch (Exception ex) {
			System.out.println("Error : " + this.getClass() + " findParkWithin "+ ex.getMessage());
		}finally {
			cursor.close();
		}
		
		
		return allParks;
	}
	
}
