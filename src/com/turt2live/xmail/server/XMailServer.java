package com.turt2live.xmail.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.turt2live.xmail.server.depend.Mail;
import com.turt2live.xmail.server.keys.APIKey;

public class XMailServer {

	public static void main(String[] args){
		if(args == null || args.length < 0){
			System.out.println("You need to start the server with a port.");
			System.out.println("Example: java -jar xMailServer.jar 2020");
			System.exit(0);
			return;
		}
		try{
			Integer.parseInt(args[0]);
		}catch(Exception e){
			System.out.println("You need to start the server with a valid port.");
			System.out.println("Example: java -jar xMailServer.jar 2020");
			System.exit(0);
			return;
		}
		new XMailServer(Integer.parseInt(args[0]));
	}

	private static XMailServer instance;
	private boolean dead = false;
	private Map<String, APIKey> apiKeys = new HashMap<String, APIKey>();
	private List<User> users = new ArrayList<User>();
	private Map<String, Mail> mail = new HashMap<String, Mail>();

	public XMailServer(int port){
		instance = this;
		UserFile.prepare();
		try{
			ServerSocket server = new ServerSocket(port);
			while (!dead){
				Socket client = server.accept();
				new Thread(new ThreadedClient(client)).start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		// TODO: Run a threaded apiKey cleanup!
	}

	public void die(){
		dead = true;
	}

	public static XMailServer getInstance(){
		return instance;
	}

	public void saveMail(Mail mail){
		mail.setPID(this.mail.size() + 1);
		mail.setUID(UUID.randomUUID().toString());
		this.mail.put(mail.getUID(), mail);
	}

	public Mail getMail(String uid){
		return mail.get(uid);
	}

	public boolean isKeyValid(String apiKey, String owner){
		if(owner.startsWith("CONSOLE@") && apiKey.equalsIgnoreCase("null")){
			return true; // Console 
		}
		for(String holder : apiKeys.keySet()){
			APIKey key = apiKeys.get(holder);
			if(key.getHolder().equalsIgnoreCase(owner) && key.getHash().equals(apiKey)){
				return true;
			}
		}
		return false;
	}

	public boolean keyExists(String hash){
		for(String holder : apiKeys.keySet()){
			APIKey key = apiKeys.get(holder);
			if(key.getHash().equals(hash)){
				return true;
			}
		}
		return false;
	}

	public void markMail(Mail mail, boolean isRead){
		Mail aMail = this.mail.get(mail.getUID());
		if(aMail != null){
			aMail.mark(isRead);
		}
	}

	public boolean registerUser(String un, String pw){
		User user = new User(un, pw);
		boolean done = user.register();
		users.add(user);
		return done;
	}

	public User getUser(String un){
		for(User u : users){
			if(u.getName().equalsIgnoreCase(un)){
				return u;
			}
		}
		if(un.startsWith("CONSOLE@")){
			ConsoleUser cu = new ConsoleUser(un);
			users.add(cu);
			return cu;
		}
		return null;
	}

	public static String now(){
		return String.valueOf(System.currentTimeMillis());
	}

	public void logoutUser(String un2){
		User user = getUser(un2);
		if(user != null){
			user.logout();
		}
	}

	public Boolean checkLogin(String username3){
		User user = getUser(username3);
		if(user != null){
			return user.isLoggedIn();
		}else{
			User u = null;
			File userFile = UserFile.getUserFile(username3);
			if(userFile != null){
				u = new User(UserFile.extractUsername(userFile), UserFile.extractPassword(userFile));
				users.add(u);
			}
			return u != null && u.isLoggedIn();
		}
	}

	public boolean loginUser(String un, String pw){
		User user = new User(un, pw);
		boolean done = user.login();
		users.add(user);
		return done;
	}

}
