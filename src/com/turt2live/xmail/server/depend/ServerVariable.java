package com.turt2live.xmail.server.depend;

import java.io.Serializable;

public class ServerVariable implements Serializable {

	private static final long serialVersionUID = 5987044196591477667L;
	public final String key;
	public final String value;

	public ServerVariable(String key, String value){
		this.key = key;
		this.value = value;
	}

}
