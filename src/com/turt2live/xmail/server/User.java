package com.turt2live.xmail.server;

import java.util.Map;

import com.turt2live.xmail.server.keys.APIKey;

public class User {

	private String username, password, apiKey, lastLogin;
	private boolean loggedIn = false;
	private XMailServer xmail = XMailServer.getInstance();

	public User(String un, String pw){
		this.username = un;
		this.password = pw;
	}

	public String lastLogin(){
		return lastLogin == null ? XMailServer.now() : lastLogin;
	}

	public String apikey(){
		return apiKey;
	}

	public Map<String, Map<String, String>> getMail(){
		// TODO Auto-generated method stub
		return null;
	}

	public boolean register(){
		// TODO Auto-generated method stub
		apiKey = APIKey.calculate(username);
		// TODO: Validation of username not in use
		return false;
	}

	public String getName(){
		return username;
	}

	public void logout(){
		loggedIn = false;
	}

	public Boolean isLoggedIn(){
		return loggedIn;
	}

	public boolean login(){
		// TODO: Validation of username/password
		loggedIn = true;
		return true;
	}

}
