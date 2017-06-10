package my.app;

import java.net.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class ChatClientThread implements Runnable {
	private Socket socket = null;
	private ChatClient client = null;
	private DataInputStream streamIn = null;
	
	
	public ChatClientThread(ChatClient cli, Socket soc){
		//new ClientWindow().setVisible(true);
		client = cli;
		socket = soc;
		open();
		
	}
	

	//Otwieranie input stream
	public void open() {
		try{
			streamIn = new DataInputStream(socket.getInputStream());
		}catch(IOException ioe){
			System.out.println("Error getting input stream: "+ ioe);
			client.stop();
		}
	}
	//zamykanie input stream
	public void close(){
		try{
			if(streamIn != null){
				streamIn.close();
			}
		}catch(IOException ioe){
			System.out.println("Error closing input stream: "+ioe);
		}
	}
	
	@Override
	public void run(){
		
		while(true){
			try{
				//System.out.println(" czytam");
				client.handle(streamIn.readUTF());
			}catch (IOException ioe) {
				System.out.println("Listening error: "+ ioe.getMessage());
				client.stop();
			}
		}
	}



}
