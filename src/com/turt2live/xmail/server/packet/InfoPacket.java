package com.turt2live.xmail.server.packet;

import java.util.List;

import com.turt2live.xmail.server.depend.ServerVariable;

public class InfoPacket implements Packet {

	private static final long serialVersionUID = 5045978621191128126L;

	public static enum InfoType{
		IMPORT_KEY, MARK, INBOX, CHECK_LOGIN, LOGIN, REGISTER, SEND, LOGOUT, SENT, READ, INFO;
	}

	private InfoType type;
	private List<ServerVariable> variables;

	public InfoPacket(InfoType type, List<ServerVariable> variables){
		this.type = type;
		this.variables = variables;
	}

	public List<ServerVariable> getVariables(){
		return variables;
	}

	@Override
	public InfoType getResult(){
		return type;
	}

	public String get(String key){
		for(ServerVariable variable : variables){
			if(variable.key.equals(key)){
				return variable.value;
			}
		}
		return null;
	}

}
