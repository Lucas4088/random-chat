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
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client = null;
	private ClientWindow  clientWindow = null;
	private String serverName = null;
	private int serverPort;
	private volatile boolean sent;
	private volatile boolean allowed;
	private String userMessage;
	private String[] serverMessage;
	private String allow, disconnect, send;
	//ustanawianie po³¹czenia z serwerem w konstruktorze 
	public ChatClient(String sN, int sP) {
		sent = false;
		serverName = sN;
		serverPort = sP;
		allow = new String("Allow");
		send = new String("Send");
		disconnect = new String("Disconnect");
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
		/*try {
			streamOut.writeUTF(socket.getLocalPort()+":"+"Ask"+":"+"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		while(thread != null){
			//System.out.println("woda");
			try {
				if(allowed){
					streamOut.writeUTF(socket.getLocalPort()+":"+"Ask"+":");
					streamOut.flush();
					allowed =false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				//handle(streamIn.readUTF());
				//System.out.println(userMessage);
				if(sent){
				//if(userMessage != null)
				//userMessage = ":Send:";
					System.out.println(userMessage);
				streamOut.writeUTF(userMessage);
				
				//System.out.println(userMessage);
				streamOut.flush();
				sent = false;
				}
				
				//while()
			}catch(IOException ioe){
				System.out.println("Sending error: "+ ioe.getMessage());
				stop();
			}
		}
	}
	
	//zakonczenie chatu
	public void handle(String msg){
		//clientWindow.setMessageDisplayArea("Stranger: "+msg);
		serverMessage = msg.split(":", 3);
		System.out.println(msg);
		if(serverMessage[1].equals(allow)){
			clientWindow.allowClientToWrite();
			allowed = true;
			clientWindow.setMessageDisplayArea("You've been connected now you can chat");
		}else if(serverMessage[1].equals(send)){
			clientWindow.setMessageDisplayArea("Stranger: "+serverMessage[2]);
		}
		
	}
	//rozpoczêcie klienta chatu
	public void startThread() throws IOException {
		//console = new DataInputStream(System.in);
		streamOut = new DataOutputStream(socket.getOutputStream());
		/*try{
			streamIn = new DataInputStream(socket.getInputStream());
		}catch(IOException ioe){
			System.out.println("Error getting input stream: "+ ioe);
			stop();
		}*/
		if(thread == null){
			//client = new ChatClientThread(this,socket);
			//new Thread(client).start();
			thread = new Thread(this);
			thread.start();
		}
		System.out.println("Thread started");
	}
	
	public void getUserMessage(String msg){
		userMessage = socket.getLocalPort()+":" + msg;
		System.out.println(userMessage);
		sent = true;
		System.out.println(sent);
	}
	
	public void allowToSend(){
		clientWindow.allowClientToWrite();
	}
	
	//zatrzymanie w¹tka klienta chatu
	@SuppressWarnings("deprecation")
	public void stop() {
		if(thread != null){
			thread.stop();
			thread = null;
		}
		try{
			//if (client   != null)  client.close();
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
