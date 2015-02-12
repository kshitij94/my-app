package com;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "unseenposts")
public class UnseenPostModel 
{
	String userno;
	String unseenpostIds;
	
	@DynamoDBHashKey(attributeName = "userno")
	public String getUserno() {
		return userno;
	}
	public void setUserno(Integer userno) {
		this.userno = String.valueOf(userno);
	}
	
	@DynamoDBAttribute(attributeName = "unseenpostIds")
	public String getUnseenpostIds() {
		return unseenpostIds;
	}
	public void setUnseenpostIds(String unseenpostIds) {
		this.unseenpostIds = unseenpostIds;
	}
	
	
}
