package com.turt2live.xmail.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.turt2live.xmail.server.depend.Mail;
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
		case MARK:
		case INBOX:
		case CHECK_LOGIN:
		case LOGIN:
		case REGISTER:
		case LOGOUT:
		case SEND:
			String apiKey = packet.get("apikey");
			String pid = packet.get("pid");
			String uid = packet.get("uid");
			String ident = packet.get("ident");
			String to = packet.get("to");
			String from = packet.get("from");
			String message = packet.get("message"); // Contains the attachments (if "C")
			String attachments = "";
			Mail mail;
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
				mail = new Mail(to, from, message, apiKey, uid, pid, attachments);
			}else if(ident.equalsIgnoreCase("S")){
				mail = new Mail(to, from, message, apiKey, uid, pid);
			}else{
				return new ResponsePacket(Status.ERROR, "Unknown ident");
			}
			server.saveMail(mail);
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
