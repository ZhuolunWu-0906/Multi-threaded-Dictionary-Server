package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.lang.String;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ServerSocketFactory;

public class Server {
	
	// Declare the port number
	private static int port = 3005;
	
	// Identifies the user number connected
	private static int counter = 0;

	public static void main(String[] args)
	{	
		
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		
		try(ServerSocket server = factory.createServerSocket(port))
		{
			System.out.println("Waiting for client connection-");
			
			// Wait for connections.
			while(true)
			{
				Socket client = server.accept();
				counter++;
				System.out.println("Client "+counter+": Applying for connection!");
							
				// Start a new thread for a connection
				Thread t = new Thread(() -> serveClient(client));
				t.start();
			}
			
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private static void serveClient(Socket client)
	{
		try(Socket clientSocket = client)
		{
			// JSON Parser, Output and Input Stream
			JSONParser parser = new JSONParser();
			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
		    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
		    
		    // Receive more data..
		    while(true) {
		    	if(input.available() > 0) {
		    		// Attempt to convert read data to JSON
		    		JSONObject command = (JSONObject) parser.parse(input.readUTF());
		    		JSONObject reply = parseCommand(command);
		    		output.writeUTF(reply.toJSONString());
		    	}
		    }
		    
		} 
		catch (IOException | ParseException e) 
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject parseCommand(JSONObject command) {
		
		JSONObject reply = new JSONObject();
		JSONArray dict = JsonUtil.readJsonFile();
		String word = command.get("word").toString();
		String meaning = command.get("meaning").toString();
		
		switch ((String) command.get("command")) {
		
			case "search":
				reply.put("command", "search");
				reply.put("word", word);
				if (JsonUtil.inObject(dict, word)) {
					reply.put("status", "succeeded");
					reply.put("meaning", JsonUtil.getMeaning(dict, word));
				} else {
					reply.put("status", "failed");
				}
				break;
				
			case "add":
				reply.put("command", "add");
				reply.put("word", word);
				if (!JsonUtil.inObject(dict, word)) {
					JsonUtil.addWord(dict, word, meaning);
					reply.put("status", "succeeded");
				} else {
					reply.put("status", "failed");
				}
				break;
				
			case "delete":
				reply.put("command", "delete");
				reply.put("word", word);
				if (JsonUtil.inObject(dict, word)) {
					JsonUtil.deleteWord(dict, word);
					reply.put("status", "succeeded");
				} else {
					reply.put("status", "failed");
				}
				break;
				
			case "modify":
				reply.put("command", "modify");
				reply.put("word", word);
				if (JsonUtil.inObject(dict, word)) {
					JsonUtil.modifyWord(dict, word, meaning);
					reply.put("status", "succeeded");
					reply.put("meaning", meaning);
				} else {
					reply.put("status", "failed");
				}
				break;
		}
		
		try (FileWriter file = new FileWriter("server-dict.json")) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(dict.toJSONString()); 
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return reply;
	}

}
