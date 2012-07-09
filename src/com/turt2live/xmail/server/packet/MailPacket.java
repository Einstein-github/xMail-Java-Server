package com.turt2live.xmail.server.packet;

import com.turt2live.xmail.server.depend.Mail;

public class MailPacket implements Packet {

	private static final long serialVersionUID = -8372743517138279942L;
	private Mail mail;

	public MailPacket(Mail mail){
		this.mail = mail;
	}

	@Override
	public Mail getResult(){
		return mail;
	}

}
