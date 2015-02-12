package com;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

















import com.amazonaws.regions.Regions;






import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.geo.GeoDataManager;
import com.amazonaws.geo.GeoDataManagerConfiguration;
import com.amazonaws.geo.model.*;
import com.amazonaws.geo.util.GeoTableUtil;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.Tables;

/*
 * This class is mapped to url : "/fillupgeotable"
 */
public class FillupGeoTable extends HttpServlet
{
	private GeoDataManager geoDataManager = null;
	AmazonDynamoDBClient ddb = null;
	/*
	 * Method to create a table geo_coordinate_table if one does not exist.
	 * After creating the table the table is populated with the values in the school_list_wa.txt.
	 * 
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		setupGeoDataManager();
		
	}
	private AWSCredentials getCredentials()
	{
		String accessKey = "AKIAIDLQO354GNV72AGQ";
		String secretKey = "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";
		return new BasicAWSCredentials(accessKey, secretKey);
		
	}

	
	String userCoorTableName = "usercoor";
	private synchronized void setupGeoDataManager() throws FileNotFoundException 
	{
			
			ClientConfiguration clientConfiguration = new ClientConfiguration().withMaxErrorRetry(20);
				
			clientConfiguration.setProxyHost("172.31.16.10");
			clientConfiguration.setProxyPort(8080);
				
			
			AWSCredentials credentials = getCredentials();
			
			ddb = new AmazonDynamoDBClient(credentials , clientConfiguration);
			ddb.setRegion(Region.getRegion(Regions.US_EAST_1));
	
			GeoDataManagerConfiguration config = new GeoDataManagerConfiguration(ddb, userCoorTableName);
										
			geoDataManager = new GeoDataManager(config);
			
			
			
			if(Tables.doesTableExist(ddb, userCoorTableName))
			{
				System.out.println("table already exist");
			}
			else
			{
				CreateTableRequest createTableRequest = GeoTableUtil.getCreateTableRequest(config);
				CreateTableResult createTableResult = ddb.createTable(createTableRequest);
				
				
				 System.out.println("Waiting for " + userCoorTableName + " to become ACTIVE...");
				 
		         Tables.waitForTableToBecomeActive(ddb, userCoorTableName);
				
			}
			insertData();
			/*
			double latitude = 47.61121;
			double longitude = -122.31846;
			 
			GeoPoint geoPoint = new GeoPoint(latitude, longitude);
			AttributeValue rangeKeyValue = new AttributeValue().withS("1");//get the user number fomr the request object
			PutPointRequest putPointRequest = new PutPointRequest(geoPoint, rangeKeyValue);
			PutPointResult putPointResult = geoDataManager.putPoint(putPointRequest);
			*/
	}
	private static GeoDataManagerConfiguration config;
	 private static Map<String, AttributeValue> newItem(String userno, String lat, String lon, Map<String, AttributeValue> map) 
	 {
	        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
	        
	        item.put("lat", new AttributeValue().withS(lat));
	        item.put("userno", new AttributeValue().withS(userno));
	        item.put("lon", new AttributeValue().withS(lon));
	        
	        item.put(config.getGeohashAttributeName(),map.get(config.getGeohashAttributeName()));
	        item.put(config.getGeoJsonAttributeName(), map.get(config.getGeoJsonAttributeName()));
	        item.put(config.getRangeKeyAttributeName(), map.get(config.getRangeKeyAttributeName()));
	        item.put(config.getHashKeyAttributeName(), map.get(config.getHashKeyAttributeName()));
	        
	        
	        return item;
	    }
	private void insertData() throws FileNotFoundException {
		
		System.out.println("current working directory : " + System.getProperty("user.dir"));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\kshitij\\workspace\\Firstd\\WebContent\\school_list_wa.txt")));

		String line;

		try 
		{
			while ((line = br.readLine()) != null) 
			{
				String[] columns = line.split("\t");
				
				String userno = columns[0];
				
				double latitude = Double.parseDouble(columns[2]);
				double longitude = Double.parseDouble(columns[3]);

				GeoPoint geoPoint = new GeoPoint(latitude, longitude);
				
				AttributeValue rangeKeyAttributeValue = new AttributeValue().withS(UUID.randomUUID().toString());
				
				AttributeValue usernoAttribute = new AttributeValue().withS(userno);
				
				PutPointRequest putPointRequest = new PutPointRequest(geoPoint, rangeKeyAttributeValue);
				
				putPointRequest.getPutItemRequest().addItemEntry("userno",usernoAttribute);
				PutPointResult putPointResult = geoDataManager.putPoint(putPointRequest);
				
				System.out.println(putPointResult);
				
		
			}
		} 
		catch (IOException e) 
		{
			throw new RuntimeException(e);
		} 
		finally 
		{
			try 
			{
				br.close();
				
			} 
			catch (IOException e) 
			{
				throw new RuntimeException(e);
			}
		}
	}
}
