package com.turt2live.xmail.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class UserFile {

	public static void prepare(){
		File path = new File("users");
		path.mkdirs();
	}

	public static File getUserFile(String username){
		return new File("users", username + ".txt");
	}

	public static boolean userFileExists(String username){
		return new File("users", username + ".txt").exists();
	}

	public static boolean login(String username, String password){
		if(!userFileExists(username)){
			return false;
		}else{
			File file = getUserFile(username);
			try{
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line;
				boolean valid = false;
				while ((line = in.readLine()) != null){
					if(line.startsWith("password")){
						line = line.replace("password", "").trim();
						if(line.equals(password)){
							valid = true;
						}else{
							valid = false;
						}
						break;
					}
				}
				in.close();
				return valid;
			}catch(Exception e){}
		}
		return false;
	}

	public static boolean register(String username, String password){
		if(userFileExists(username)){
			return false;
		}else{
			File file = getUserFile(username);
			try{
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
				out.write("password " + password);
				out.newLine();
				out.close();
				return true;
			}catch(Exception e){}
		}
		return false;
	}

	public static String extractUsername(File file){
		return file.getName().replace(".txt", "").trim();
	}

	public static String extractPassword(File file){
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while ((line = in.readLine()) != null){
				if(line.startsWith("password")){
					line = line.replace("password", "").trim();
					return line;
				}
			}
			in.close();
		}catch(Exception e){}
		return null;
	}

}
