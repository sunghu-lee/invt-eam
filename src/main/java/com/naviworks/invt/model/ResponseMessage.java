package com.naviworks.invt.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ResponseMessage
{
	private String status;
	private String message;
	private String errorMessage;
	private String errorCode;
	
	public ResponseMessage() {}
	public ResponseMessage(String status, String message, String errorCode, String errorMessage)
	{
		this.status = status;
		this.message = message;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
