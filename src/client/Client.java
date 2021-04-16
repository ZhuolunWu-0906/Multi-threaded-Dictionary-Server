package client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import server.JsonUtil;


public class Client {
	
	// IP and port
	private static String ip = "localhost";
	private static int port = 3005;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) 
	{
	
		try(Socket socket = new Socket(ip, port);)
		{
			// JSON Parser, Output and Input Stream
			JSONParser parser = new JSONParser();
			DataInputStream input = new DataInputStream(socket.getInputStream());
		    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		    
	    	output.writeUTF("I want to connect");
	    	output.flush();
	    	
	    	JSONObject newCommand = new JSONObject();
	    	
	    	//	command type:
	    	//	search / add / delete / modify
	    	newCommand.put("command", "modify");
	    	newCommand.put("word", "apple");
	    	newCommand.put("meaning", "0");
	    	System.out.println(newCommand.toJSONString());
	    	
	    	//	 Read hello from server..
	    	String message = input.readUTF();
	    	System.out.println(message);
	    	
	    	//	Send message to Server
	    	output.writeUTF(newCommand.toJSONString());
	    	output.flush();
	    	
	    	//	Print out results received from server..
	    	String result = input.readUTF();
	    	parseCommand((JSONObject) parser.parse(result));
		    
		} 
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private static void parseCommand(JSONObject command) {
		
		String status = command.get("status").toString();
		String word = command.get("word").toString();
		
		switch ((String) command.get("command")) {
		case "search":
			if (status.equals("succeeded")) {
				System.out.println(word+": "+command.get("meaning").toString());
			} else {
				System.out.println("The word ["+word+"] is not in the dictionary, you can try to add it.");
			}
			break;
			
		case "add":
			if (status.equals("succeeded")) {
				System.out.println("The word ["+word+"] has been successfully added to the dictionary");
			} else {
				System.out.println("The word ["+word+"] already exists in the dictionary");
			}
			break;
			
		case "delete":
			if (status.equals("succeeded")) {
				System.out.println("The word ["+word+"] has been successfully deleted from the dictionary");
			} else {
				System.out.println("The word ["+word+"] does not exist in the dictionary");
			}
			break;
			
		case "modify":
			if (status.equals("succeeded")) {
				System.out.println("The word ["+word+"] has been successfully updated");
			} else {
				System.out.println("The word ["+word+"] does not exist in the dictionary");
			}
			break;
		}
		
	}
	
}
