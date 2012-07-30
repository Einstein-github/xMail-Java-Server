package com.turt2live.xmail.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.turt2live.xmail.server.depend.Mail;
import com.turt2live.xmail.server.depend.ServerVariable;
import com.turt2live.xmail.server.keys.APIKey;
import com.turt2live.xmail.server.packet.InfoPacket;
import com.turt2live.xmail.server.packet.Packet;
import com.turt2live.xmail.server.packet.ResponsePacket;
import com.turt2live.xmail.server.packet.ResponsePacket.Status;

public class ThreadedClient implements Runnable {

	private Socket socket;
	private XMailServer server = XMailServer.getInstance();
	private ObjectOutputStream out;
	private ObjectInputStream in;

	public ThreadedClient(Socket socket){
		this.socket = socket;
		try{
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		try{
			while (socket.isConnected()){
				Object object;
				while ((object = in.readObject()) != null){
					if(object instanceof Packet){
						if(object instanceof InfoPacket){
							ResponsePacket response = handleInfoPacket((InfoPacket) object);
							send(response);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private ResponsePacket handleInfoPacket(InfoPacket packet){
		switch (packet.getResult()){
		case IMPORT_KEY:
			try{
				String hash = APIKey.hash(String.valueOf(System.currentTimeMillis()));
				while (XMailServer.getInstance().keyExists(hash)){
					hash = APIKey.hash(String.valueOf(System.currentTimeMillis()));
				}
				return new ResponsePacket(Status.OK, hash);
			}catch(Exception e){
				return new ResponsePacket(Status.ERROR, "Encoding Issue");
			}
		case MARK:
			String apiKey = packet.get("apikey");
			//String pid = packet.get("pid");
			String uid = packet.get("uid");
			String username = packet.get("username");
			String read = packet.get("read");
			boolean isRead = false;
			Mail mail = server.getMail(uid);
			if(mail == null){
				return new ResponsePacket(Status.ERROR, "Mail does not exist");
			}else{
				if(server.isKeyValid(apiKey, username)){
					if(mail.getTo().equalsIgnoreCase(username)){
						if(read.equalsIgnoreCase("0")){
							isRead = false;
						}else if(read.equalsIgnoreCase("1")){
							isRead = true;
						}else{
							return new ResponsePacket(Status.ERROR, "Unknown read state");
						}
						server.markMail(mail, isRead);
						return new ResponsePacket(Status.OK, "Mail saved");
					}else{
						return new ResponsePacket(Status.ERROR, "Mail is not owned by user");
					}
				}else{
					return new ResponsePacket(Status.ERROR, "Invalid API Key");
				}
			}
		case INBOX:
			String xUsername = packet.get("username");
			User user = server.getUser(xUsername);
			if(user != null){
				return new ResponsePacket(Status.OK, "Inbox found", user.getMail(), new ServerVariable("unread", String.valueOf(user.getMail().size())), new ServerVariable("username", xUsername));
			}else{
				return new ResponsePacket(Status.ERROR, "User not found");
			}
		case CHECK_LOGIN:
			String username3 = packet.get("username");
			String apiKey3 = packet.get("apikey");
			if(server.isKeyValid(apiKey3, username3)){
				if(username3.contains("CONSOLE@")){
					return new ResponsePacket(Status.OK, "Logged in", null, new ServerVariable("username", username3), new ServerVariable("loggedin", "true"), new ServerVariable("date", XMailServer.now()), new ServerVariable("lastlogin", XMailServer.now()), new ServerVariable("apikey", "null"));
				}else{
					Boolean check = server.checkLogin(username3);
					User xUser = server.getUser(username3);
					if(check == null){
						return new ResponsePacket(Status.ERROR, "Unknown user");
					}else{
						return new ResponsePacket(Status.OK, "User logged in", null, new ServerVariable("date", XMailServer.now()), new ServerVariable("loggedin", "true"), new ServerVariable("lastlogin", xUser.lastLogin()), new ServerVariable("apikey", xUser.apikey()));
					}
					/* TODO
					============================= CRITICAL =============================
					  xMail ignores the REAL logged in value of the user here!! FIX!!
					============================= CRITICAL =============================				
					*/
				}
			}else{
				return new ResponsePacket(Status.ERROR, "Invalid API Key");
			}
		case LOGIN:
			String un = packet.get("username");
			String pw = packet.get("password"); // Encoded
			boolean loggedIn = server.loginUser(un, pw);
			User user1 = server.getUser(un);
			if(loggedIn){
				return new ResponsePacket(Status.OK, "User logged in", null, new ServerVariable("date", XMailServer.now()), new ServerVariable("loggedin", "true"), new ServerVariable("lastlogin", user1.lastLogin()), new ServerVariable("apikey", user1.apikey()));
			}else{
				return new ResponsePacket(Status.ERROR, "Username in use");
			}
		case REGISTER:
			String un1 = packet.get("username");
			String pw1 = packet.get("password"); // Encoded
			boolean registered = server.registerUser(un1, pw1);
			User user11 = server.getUser(un1);
			if(registered){
				return new ResponsePacket(Status.OK, "User logged in", null, new ServerVariable("date", XMailServer.now()), new ServerVariable("loggedin", "true"), new ServerVariable("lastlogin", user11.lastLogin()), new ServerVariable("apikey", user11.apikey()));
			}else{
				return new ResponsePacket(Status.ERROR, "Username in use");
			}
		case LOGOUT:
			String un2 = packet.get("username");
			String apiKey2 = packet.get("apikey");
			if(server.isKeyValid(apiKey2, un2)){
				server.logoutUser(un2);
				return new ResponsePacket(Status.OK, "Logged out", null, new ServerVariable("username", un2));
			}else{
				return new ResponsePacket(Status.ERROR, "Invalid API Key");
			}
		case SEND:
			String apiKey1 = packet.get("apikey");
			String pid1 = packet.get("pid");
			String uid1 = packet.get("uid");
			String ident = packet.get("ident");
			String to = packet.get("to");
			String from = packet.get("from");
			String message = packet.get("message"); // Contains the attachments (if "C")
			String attachments = "";
			Mail mail1;
			if(ident.equalsIgnoreCase("C")){
				String[] parts = message.split(";");
				message = parts[0];
				StringBuilder att = new StringBuilder();
				for(int i = 1; i < parts.length; i++){
					att.append(parts[i]).append(";");
				}
				String attStr = att.toString().trim();
				attStr = attStr.substring(0, attStr.length() - 1);
				attachments = attStr;
				mail1 = new Mail(to, from, message, apiKey1, uid1, pid1, attachments);
			}else if(ident.equalsIgnoreCase("S")){
				mail1 = new Mail(to, from, message, apiKey1, uid1, pid1);
			}else{
				return new ResponsePacket(Status.ERROR, "Unknown ident");
			}
			if(server.isKeyValid(from, apiKey1)){
				server.saveMail(mail1);
			}else{
				return new ResponsePacket(Status.ERROR, "Invalid API Key");
			}
			return new ResponsePacket(Status.OK, "Message sent!");
		}
		return new ResponsePacket(Status.ERROR, "Invalid mode");
	}

	public void send(Packet packet){
		try{
			out.writeObject(packet);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
