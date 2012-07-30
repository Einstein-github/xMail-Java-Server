package com.turt2live.xmail.server;


public class ConsoleUser extends User {

	public ConsoleUser(String un){
		super(un, null);
	}

	@Override
	public String lastLogin(){
		return XMailServer.now();
	}

	@Override
	public String apikey(){
		return "null";
	}

	@Override
	public boolean register(){
		return true;
	}

	@Override
	public void logout(){

	}

	@Override
	public Boolean isLoggedIn(){
		return true;
	}

	@Override
	public boolean login(){
		return true;
	}

}
