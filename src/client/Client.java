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
import javax.swing.JLabel;
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
	private static String ip = "localhost";
	private static int port = 3005;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		guiImplement();

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
	    	result = parseCommand((JSONObject) parser.parse(receive));
		    
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

	private static String parseCommand(JSONObject command) {
		
		String status = command.get("status").toString();
		String word = command.get("word").toString();
		
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
            	System.out.println(newCommand.toJSONString());
            	JOptionPane.showMessageDialog(frame,
            			createSocket(newCommand));createSocket(newCommand);
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
