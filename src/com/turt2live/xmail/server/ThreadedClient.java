package com.turt2live.xmail.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.turt2live.xmail.server.packet.MailPacket;
import com.turt2live.xmail.server.packet.Packet;

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
						if(object instanceof MailPacket){
							server.incomingMail(((MailPacket) object).getResult(), this);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void send(Packet packet){
		try{
			out.writeObject(packet);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
