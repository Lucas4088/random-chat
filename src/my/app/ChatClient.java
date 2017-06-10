package my.app;

import java.net.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class ChatClient implements Runnable  {

	private Socket socket = null;
	private Thread thread = null;
	private DataInputStream console = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client = null;
	private ClientWindow  clientWindow = null;
	private String serverName = null;
	private int serverPort;
	private volatile boolean sent;
	private String userMessage;
	//ustanawianie po³¹czenia z serwerem w konstruktorze 
	public ChatClient(String sN, int sP) {
		sent = false;
		serverName = sN;
		serverPort = sP;
		
		clientWindow = new ClientWindow(this);
		new Thread(clientWindow).start();
		clientWindow.setVisible(true);
		
	}
	
	public void establishConnection(){
		
		System.out.println("Establishing connection. Please wait ... ");
		try{
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket);
			startThread();
		}catch(UnknownHostException uhe){
			System.out.println("Host unknown: "+uhe.getMessage());
		}catch(IOException ioe){
			System.out.println("Unexpected exception: "+ ioe.getMessage());
		}
		
		client = new ChatClientThread(this, socket);
		new Thread(client).start();
		
	}
	public void disconnect(){
		stop();
	}
	@SuppressWarnings("deprecation")
	@Override
	public void run() {

		while(thread != null){
			try{
				
				if(sent){
				streamOut.writeUTF(userMessage);
				//System.out.println(userMessage);
				streamOut.flush();
				sent = false;
				}
			}catch(IOException ioe){
				System.out.println("Sending error: "+ ioe.getMessage());
				stop();
			}
		}
	}
	
	//zakonczenie chatu
	public void handle(String msg){
		clientWindow.setMessageDisplayArea("Stranger: "+msg);
		 
		if(msg.equals(".bye")){
			System.out.println("Good bye. Press RETURN to exit");
			stop();
		}else{
			System.out.println(msg);
		}
	}
	//rozpoczêcie klienta chatu
	public void startThread() throws IOException {
		console = new DataInputStream(System.in);
		streamOut = new DataOutputStream(socket.getOutputStream());
		if(thread == null){
			//client = new ChatClientThread(this,socket);
			//new Thread(client).start();
			thread = new Thread(this);
			thread.start();
		}
		System.out.println("Thread started");
	}
	
	public void getUserMessage(String msg){
		userMessage = msg;
		sent = true;
		
	}
	
	//zatrzymanie w¹tka klienta chatu
	@SuppressWarnings("deprecation")
	public void stop() {
		if(thread != null){
			thread.stop();
			thread = null;
		}
		try{
			if (console   != null)  console.close();
	         if (streamOut != null)  streamOut.close();
	         if (socket    != null)  socket.close();
		}catch(IOException ioe ){
			System.out.println("Error closing ... "+ ioe.getMessage());
		}
		client.close();
		//client.stop();
	}

	public static void main(String[] args) {
		
		
		ChatClient client = null;
		client = new ChatClient("localhost",666);
		
	}

	public void setSend(boolean s){
		sent =s;
	}
	
	


}
