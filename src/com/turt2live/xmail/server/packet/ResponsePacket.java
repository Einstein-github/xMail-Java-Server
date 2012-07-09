package com.turt2live.xmail.server.packet;

import java.util.Map;

import com.turt2live.xmail.server.depend.ServerVariable;

public class ResponsePacket implements Packet {

	private static final long serialVersionUID = -3114454319268037940L;

	public static enum Status{
		OK, ERROR;
	}

	private Status status;
	private String message;
	private Map<String, String> keys;
	private Map<String, Map<String, String>> mail;

	public ResponsePacket(Status status, String message, Map<String, Map<String, String>> mail, ServerVariable... variables){
		this.status = status;
		this.message = message;
		if(variables != null){
			for(ServerVariable v : variables){
				keys.put(v.key, v.value);
			}
		}
	}

	public ResponsePacket(Status status, String message){
		this.status = status;
		this.message = message;
	}

	public Map<String, Map<String, String>> getMail(){
		return mail;
	}

	public Map<String, String> getKeys(){
		return keys;
	}

	public String getMessage(){
		return message;
	}

	@Override
	public Status getResult(){
		return status;
	}

}
