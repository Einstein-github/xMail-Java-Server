package com.turt2live.xmail.server.depend;

import java.io.Serializable;

public class Mail implements Serializable {

	private static final long serialVersionUID = -3832504963142301634L;
	private String to, from, message, apiKey, attachments;

	public Mail(String to, String from, String message, String apiKey, String attachments){
		this.to = to;
		this.from = from;
		this.message = message;
		this.apiKey = apiKey;
		this.attachments = attachments;
	}

	public Mail(String to, String from, String message, String apiKey){
		this.to = to;
		this.from = from;
		this.message = message;
		this.apiKey = apiKey;
	}

	public String getTo(){
		return to;
	}

	public String getFrom(){
		return from;
	}

	public String getMessage(){
		return message;
	}

	public String getAPIKey(){
		return apiKey;
	}

	public String getAttachments(){
		return attachments;
	}

	public boolean hasAttachments(){
		return getAttachments() != null;
	}

}
