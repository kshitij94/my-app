package com;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "usercoor")
public class UserCoorModel
{
	private String userno;
	private String lat;
	private String lon;
	private String rangeKey;
	private String haskKey;
	
	@DynamoDBHashKey(attributeName = "userno")
	public String getUserno() {
		return userno;
	}
	public void setUserno(String userno) {
		this.userno = userno;
	}
	@DynamoDBAttribute(attributeName = "lat")
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	
	@DynamoDBAttribute(attributeName = "lon")
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	
	@DynamoDBAttribute(attributeName = "rangekey")
	public String getRangeKey() {
		
		return rangeKey;
	}
	public void setRangeKey(String rangeKey) {
		this.rangeKey = rangeKey;
	}
	
	@DynamoDBAttribute(attributeName = "hashKey")
	public String getHaskKey() {
		return haskKey;
	}
	public void setHaskKey(String haskKey) {
		this.haskKey = haskKey;
	}
	
	
	
}