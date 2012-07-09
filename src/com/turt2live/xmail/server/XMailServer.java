package com.turt2live.xmail.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.turt2live.xmail.server.depend.Mail;

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

	public XMailServer(int port){
		instance = this;
		try{
			ServerSocket server = new ServerSocket(port);
			while (!dead){
				Socket client = server.accept();
				new Thread(new ThreadedClient(client)).start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void die(){
		dead = true;
	}

	public static XMailServer getInstance(){
		return instance;
	}

	public void saveMail(Mail mail){

	}

}
