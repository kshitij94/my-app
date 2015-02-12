package com;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.http.*;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.google.common.primitives.Bytes;

public class GetUnseenPostServlet extends HttpServlet
{
	/**
	 * 
	 * GetUnseenPostServlet :
	 * 	This sevlet handles the request from the client to read next 10 posts.
	 * Parameters send by the client are :
	 * 
	 * 1) userno
	 * 
	 * Following are the actions that need to be taken:
	 * 1) retrieve the unseenpost from the unseenpost table.
	 * 2) retrieve m number of posts. 
	 * 3) From the postids split on '-' and get the userno who has posted the post.
	 * 4) From the userno, query the UserProfile to get the user name.
	 * 5) create a json object comprising of all the post info and the user name of the post owner. 
	 * 6) update the unseen table.
	 */
	
	static AmazonDynamoDBClient ddb = null;
	static String posttablename = "userposttable";
	static int numUnseenRetrieve = 1;
	
	private static AWSCredentials getCredentials()
	{
		String accessKey = "AKIAJALYAP3LBXFUKMOA";
		String secretKey = "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";
		return new BasicAWSCredentials(accessKey, secretKey);
		
	}

	public  void doGet(HttpServletRequest req,HttpServletResponse res ) throws IOException
	{
		String userno = req.getParameter("userno");
						 
		AWSCredentials credentials = getCredentials();
		
		ClientConfiguration clientConfig = new ClientConfiguration();
		
		//uncomment for setting proxy
        //clientConfig.setProxyHost("172.31.16.10");
        //clientConfig.setProxyPort(8080);
        

		ddb = new AmazonDynamoDBClient(credentials , clientConfig);
		
		ddb.setRegion(Region.getRegion(Regions.US_EAST_1));
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);
		
		//retrieving unseenpost for the userno.
		 DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
	       scanExpression.addFilterCondition("userno", 
	                new Condition()
	       
	                    .withComparisonOperator(ComparisonOperator.EQ)
	                    .withAttributeValueList(new AttributeValue().withS(userno)));
	       
	       List<UnseenPostModel> scanResult = mapper.scan(UnseenPostModel.class, scanExpression);
	       
	      	       
	       if(scanResult.size() ==1 )
	       {
	    	   UnseenPostModel unseenPosts = scanResult.get(0);
	    	   String [] postIds  = unseenPosts.getUnseenpostIds().split(",");
	    	   
	    	   if(postIds.length <= numUnseenRetrieve)
	    	   {
	    		   mapper.delete(unseenPosts);
	    	   }
	    	   else
	    	   {
	    		   String newUnseenPostIds = "";
	    		   
	    		   for(int i = numUnseenRetrieve ; i < postIds.length ; i++)
	    		   {
	    			   newUnseenPostIds += postIds[i] +",";
	    		   }
	    		   newUnseenPostIds = newUnseenPostIds.substring(0, newUnseenPostIds.length()-1);
	    		   
	    		   unseenPosts.setUnseenpostIds(newUnseenPostIds);
	    		   mapper.save(unseenPosts);
	    		   
	    		   String [] temp = new String[numUnseenRetrieve];
	    		   for(int i = 0 ; i < numUnseenRetrieve ; i++)
	    		   {
	    			   temp[i] = postIds[i];
	    		   }
	    		   postIds = temp;
	    			   
	    		   
	    		   
	    	   }
	    	   
	    	   JSONObject obj = new JSONObject();
	    	   
	    	   
	    	   for(int i = 0 ; i < postIds.length; i++)
	    	   {
	    		   String postUserno = postIds[i].split("-")[0];
	    		   scanExpression = new DynamoDBScanExpression();
	    	       
	    		   scanExpression.addFilterCondition("userno", 
	    	                new Condition()
	    	       
	    	                    .withComparisonOperator(ComparisonOperator.EQ)
	    	                    .withAttributeValueList(new AttributeValue().withS(postUserno)));
	    		  
	    	       UserProfileModel creatorProfile = mapper.scan(UserProfileModel.class, scanExpression).get(0);
	    	       
	    	       JSONObject postJson = new JSONObject();
	    	       
	    	       try 
	    	       {
	    	    	   
					postJson.put("creator", creatorProfile.getFirstname() + " " + creatorProfile.getLastname());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	       
	    	       scanExpression = new DynamoDBScanExpression();
	    	       
	    		   scanExpression.addFilterCondition("postid", 
	    	                new Condition()
	    	       
	    	                    .withComparisonOperator(ComparisonOperator.EQ)
	    	                    .withAttributeValueList(new AttributeValue().withS(postIds[i])));
	    		   
	    		  
	    		   UserPostModel post = mapper.scan(UserPostModel.class, scanExpression).get(0);
	    		   
	    		   try {
					postJson.put("postcontext", post.getPostcontext());
					postJson.put("posttitle", post.getPosttitle());
		    		   postJson.put("postid", post.getPostid());
		    		   
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		   
	    		  
	    		   try {
					obj.put(""+i, postJson.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	     
	    	   }
	    	   
	    	   //output the obj to servlet output stream.
	    	  
	    	   res.getOutputStream().write(obj.toString().getBytes(Charset.forName("UTF-8")));
	       }
	       else if(scanResult.size() == 0)
	       {
	    	   //no post available for the userno.
	    	   res.getOutputStream().write("no post available".getBytes(Charset.forName("UTF-8")));
	    	  
	       }
	       else
	       {
	    	   res.getOutputStream().write("internal error1".getBytes(Charset.forName("UTF-8")));
	    	
	       }
	}
	
}
