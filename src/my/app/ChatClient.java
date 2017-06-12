package my.app;

import java.net.*;
import java.nio.channels.ClosedByInterruptException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class ChatClient implements Runnable  {

	private Socket socket = null;
	private Thread thread = null;
	//private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client = null;
	private ClientWindow  clientWindow = null;
	private String serverName = null;
	private int serverPort;
	private volatile boolean sent;
	private volatile boolean allowed;
	private String userMessage;
	private String[] serverMessage;
	private String except;
	private String allow, disconnect, send, next,wait,ask,connect,unavailable;
	
	//ustanawianie po³¹czenia z serwerem w konstruktorze 
	public ChatClient(String sN, int sP) {
		sent = false;
		serverName = sN;
		serverPort = sP;
		ask = new String("Ask");
		allow = new String("Allow");
		send = new String("Send");
		disconnect = new String("Disconnect");
		next = new String("Next");
		wait = new String("Wait");
		connect = new String("Connect");
		unavailable = new String("Unavailable");
		clientWindow = new ClientWindow(this);
		allowed = false;
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
			except =ioe.getMessage();
		}
		
		client = new ChatClientThread(this, socket);
		new Thread(client).start();
		
	}
	
	public String getException(){
		return except;
	}
	public void disconnect(){
		
		if(streamOut!=null){
			try {
				streamOut.writeUTF(socket.getLocalPort()+":"+disconnect);
				streamOut.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Tutaj zmiana pewnie do cofniêcia
		client.stopClient();
		
		//thread = null;
	}
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		while(thread != null){
				if(allowed){
					 
					 allowed = false;
					 askForOtherTalk();
				}
			try{
				if(sent){
				streamOut.writeUTF(userMessage);
				
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
		
		serverMessage = msg.split(":", 3);
		System.out.println("Received Message :"+msg);
		
		if(serverMessage[1].equals(connect)){
			clientWindow.setMessageDisplayArea(serverMessage[2]);
			allowed =true;
			
		}else if(serverMessage[1].equals(wait)){
			
			clientWindow.setMessageDisplayArea(serverMessage[2]);
			clientWindow.disableNext();
			disallowToSend();
			allowed = false;
			//askForOtherTalk();
		}else if(serverMessage[1].equals(allow)){
			
			allowed = false;
			clientWindow.enableNext();
			clientWindow.allowClientToWrite();
			clientWindow.resetMessageDisplayArea();
			clientWindow.setMessageDisplayArea(serverMessage[2]);
			
		}else if(serverMessage[1].equals(send)){
			
			clientWindow.setMessageDisplayArea("Stranger: "+serverMessage[2]);
			
		}else if(serverMessage[1].equals(next)){
			
			clientWindow.setMessageDisplayArea(serverMessage[2]);
			disallowToSend();
			allowed = false;
			
		}else if(serverMessage[1].equals(unavailable)){
			
			disallowToSend();
			clientWindow.resetMessageDisplayArea();
			clientWindow.disableNext();
			clientWindow.setMessageDisplayArea(serverMessage[2]);
			try {
				if(streamOut != null){
					streamOut.close();
					streamOut = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}/*else if(serverMessage[1].equals(disconnect)){
			
		}*/
		
	}
	//rozpoczêcie klienta chatu
	public void startThread() throws IOException {
		streamOut = new DataOutputStream(socket.getOutputStream());
		
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
		System.out.println("Thread started");
	}
	
	public void getUserMessage(String msg){
		userMessage = socket.getLocalPort()+":" + msg;
		//System.out.println(userMessage);
		sent = true;
		System.out.println(sent);
	}
	
	private void askForOtherTalk(){
		
		try {
			streamOut.writeUTF(socket.getLocalPort()+":"+ask+":");
			streamOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//allowed =false;
	}
	
	private void allowToSend(){
		clientWindow.allowClientToWrite();
	}
	
	private void disallowToSend(){
		clientWindow.disallowClientToWrite();
	}
	
	//zatrzymanie w¹tka klienta chatu
	@SuppressWarnings("deprecation")
	public void stop() {
		if(thread != null){
			thread.stop();
			thread = null;
		}
		try{
	         if (streamOut != null)  streamOut.close();
	         
		}catch(IOException ioe ){
			System.out.println("Error closing ... "+ ioe.getMessage());
		}
		client.close();
		if (socket    != null)
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		client.stopClient();
	}

	public static void main(String[] args) {
		ChatClient client = null;
		client = new ChatClient("localhost",666);
		
	}
	public void setSend(boolean s){
		sent =s;
	}
	

}
