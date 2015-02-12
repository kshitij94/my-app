package com;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "UserProfile")
public class UserProfileModel 
{

	String userno;
	String firstname;
	String lastname;
	
	@DynamoDBHashKey(attributeName="userno")
	public String getUserno() 
	{
		return userno;
	}
	public void setUserno(String userno) {
		this.userno = userno;
	}
	@DynamoDBAttribute(attributeName="firstname")
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	@DynamoDBAttribute(attributeName = "lastname")
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
}
