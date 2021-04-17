package client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class Client{
	
	// IP and port
	private static String ip;
	private static int port;
	
	public static void main(String[] args) {
		
		// Read from command line arguments
		if (args.length != 2) {
			System.out.println("Please enter a valid server address and port number");
		} else {
			ip = args[0];
			port = Integer.parseInt(args[1]);
			
			// Render GUI
			// All features are implemented within the function (guiImplement())
			System.out.println("DictionaryClient is running..");
			guiImplement();
		}
	}
	
	private static String createSocket(JSONObject newCommand) {
		
		String result = new String();
		
		try(Socket socket = new Socket(ip, port);)
		{
			// JSON Parser, Output and Input Stream
			JSONParser parser = new JSONParser();
			DataInputStream input = new DataInputStream(socket.getInputStream());
		    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
	    	
	    	//	Send message to Server
	    	output.writeUTF(newCommand.toJSONString());
	    	output.flush();
	    	
	    	//	Print out results received from server..
	    	String receive = input.readUTF();
	    	result = parseReply((JSONObject) parser.parse(receive));
		    
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
		return result;
	}

	// Read and parse reply from server
	private static String parseReply(JSONObject command) {
		
		String status = command.get("status").toString();
		String word = command.get("word").toString();
		
		// Extract result string
		switch ((String) command.get("command")) {
		case "search":
			if (status.equals("succeeded")) {
				return (word+": "+command.get("meaning").toString());
			} else {
				return ("The word ["+word+"] is not in the dictionary, you can try to add it.");
			}
			
		case "add":
			if (status.equals("succeeded")) {
				return ("The word ["+word+"] has been successfully added to the dictionary");
			} else {
				return ("The word ["+word+"] already exists in the dictionary");
			}
			
		case "delete":
			if (status.equals("succeeded")) {
				return ("The word ["+word+"] has been successfully deleted from the dictionary");
			} else {
				return ("The word ["+word+"] does not exist in the dictionary");
			}
			
		case "modify":
			if (status.equals("succeeded")) {
				return ("The word ["+word+"] has been successfully updated");
			} else {
				return ("The word ["+word+"] does not exist in the dictionary");
			}
		}
		
		return "You have not enter a valid word to search :)";
	}
	
	// GUI
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void guiImplement() {
		
		JFrame frame = new JFrame("Dictionary-Client");
        JPanel top = new JPanel();
        JPanel mid = new JPanel();
        
        JTextField txtfield = new JTextField(11);
        JTextArea txtArea = new JTextArea(4,26);
        
        JButton proceedButton = new JButton("Proceed");
        
        JComboBox actions=new JComboBox();
        actions.addItem("Search");
        actions.addItem("Add");
        actions.addItem("Delete");
        actions.addItem("Modify");
        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
        actions.setRenderer(listRenderer);
        
        // Send JSONObject message
        proceedButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            	String action = actions.getSelectedItem().toString().toLowerCase();
            	String word = txtfield.getText().toString();
            	String meaning = txtArea.getText().toString();
            	JSONObject newCommand = new JSONObject();
            	
            	//	command type:
            	//	search / add / delete / modify
            	newCommand.put("command", action);
            	newCommand.put("word", word);
            	newCommand.put("meaning", meaning);
            	//	Send message and receive reply
            	System.out.println("Sending message to DictionaryServer");
            	JOptionPane.showMessageDialog(frame, createSocket(newCommand));
            	System.out.println("Received message from DictionaryServer");
            }
        });
        
        top.add(actions);
        top.add(txtfield);
        top.add(proceedButton);
        
        mid.add(txtArea);
        
        frame.add(top,BorderLayout.NORTH);
        frame.add(mid,BorderLayout.CENTER);
        
        frame.setBounds(300,200,350,170);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
