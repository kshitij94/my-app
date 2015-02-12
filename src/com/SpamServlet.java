package com;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;

public class SpamServlet extends HttpServlet 
{
	/*
	 * This servlet is called when a user marks a post as spam. 
	 * Following are the paramerters in the request object.
	 * 
	 * 
	 * 1) postid
	 * 
	 */
	static AmazonDynamoDBClient ddb = null;
	static String posttablename = "userposttable";
	private static AWSCredentials getCredentials()
	{
		String accessKey = "AKIAJALYAP3LBXFUKMOA";
		String secretKey = "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";
		return new BasicAWSCredentials(accessKey, secretKey);
		
	}
	public  void doGet(HttpServletRequest req, HttpServletResponse res)
	{
		
		String postid = req.getParameter("postid");

		AWSCredentials credentials = getCredentials();
		
		ClientConfiguration clientConfig = new ClientConfiguration();
		
		//uncomment for setting proxy
       // clientConfig.setProxyHost("172.31.16.10");
        //clientConfig.setProxyPort(8080);
        
        
		ddb = new AmazonDynamoDBClient(credentials , clientConfig);
		
		ddb.setRegion(Region.getRegion(Regions.US_EAST_1));
		 DynamoDB dynamoDB = new DynamoDB(ddb);
		    Table table = dynamoDB.getTable(posttablename);
		    
		    
		    List<AttributeUpdate> list = new ArrayList<AttributeUpdate>();
		    
		    list.add(new AttributeUpdate("numspams").addNumeric(1));
		    
		    UpdateItemSpec updateItemSpec = new UpdateItemSpec()
	        .withPrimaryKey("postid", postid)
	        .withAttributeUpdate(list);
	    
	        
	        
	        UpdateItemOutcome outcome =  table.updateItem(updateItemSpec);
	        try {
				res.getOutputStream().write("success".getBytes(Charset.forName("UTF-8")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
