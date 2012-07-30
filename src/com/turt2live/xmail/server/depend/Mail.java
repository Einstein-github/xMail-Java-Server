package com.turt2live.xmail.server.depend;

import java.io.Serializable;

public class Mail implements Serializable {

	public static enum MailType{
		COMPLEX, SIMPLE;
	}

	private static final long serialVersionUID = -3832504963142301634L;
	private String to, from, message, apiKey, attachments, UID, PID;
	private MailType type;
	private boolean read = false;

	public Mail(String to, String from, String message, String apiKey, String UID, String PID, String attachments){
		this.to = to;
		this.from = from;
		this.message = message;
		this.apiKey = apiKey;
		this.attachments = attachments;
		this.type = MailType.COMPLEX;
		this.UID = UID;
		this.PID = PID;
	}

	public Mail(String to, String from, String message, String apiKey, String UID, String PID){
		this.to = to;
		this.from = from;
		this.message = message;
		this.apiKey = apiKey;
		this.type = MailType.SIMPLE;
		this.UID = UID;
		this.PID = PID;
	}

	public String getUID(){
		return UID;
	}

	public String getPID(){
		return PID;
	}

	public MailType getType(){
		return type;
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

	public void setPID(int pid){
		this.PID = String.valueOf(pid);
	}

	public void setUID(String uid){
		this.UID = uid;
	}

	public void mark(boolean isRead){
		read = isRead;
	}

	public boolean isRead(){
		return read;
	}

}
