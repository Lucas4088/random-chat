package my.app;

import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class ChatServer implements Runnable {
	private ArrayList<ChatServerThread> clients;
	private ServerSocket server = null;
	private Thread thread = null;
	private ServerWindow serverWindow;
	private ChatServerThread client = null;
	private String[] receivedMessage;
	private String sent, next, disconnect;
	//iniciajcja portu i gniazda
	public ChatServer(int port) {
		clients = new ArrayList<>();
		serverWindow = new ServerWindow(this,port);
		new Thread(serverWindow).start();
		serverWindow.setVisible(true);
		sent = new String("Sent");
		next = new String("Next");
		disconnect = new String("Disconnect");
	}
	
	@Override
	public void run() {
		while(thread != null){
			try{
				//dodanie nowego klienta
				serverWindow.setNotificationArea("Waiting for a client ...");
				addThread(server.accept());
				
			}catch(IOException ioe){
				serverWindow.setNotificationArea("Server accept error: "+ ioe);
				stop();
			}
		}
	}
	
	public void establishConnection(int port){
		try{
			
			serverWindow.setNotificationArea("Binding to port "+ port +", please wait ..");
			
			server = new ServerSocket(port);
			server.setReuseAddress(true);
			serverWindow.setNotificationArea("Server started: "+server);
			start();
		}catch(IOException ioe){
			serverWindow.setNotificationArea("Cannot bind to port "+ port + ": "+ioe.getMessage());
		}
	}
	
	
	
	public void endConnection(){
		if(server != null)
			try {
				
				server.close();
				stop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	public static void main(String[] args) {
		ChatServer server = null;
		
			server = new ChatServer(666);
		
	}
	
	//PArsowanie otrzymanej wiadomoœci
	public synchronized void handle(int ID, String input){
		
		//serverWindow.setNotificationArea(ID+":"+input);
		String msg = new String(ID+":"+input);
		serverWindow.setNotificationArea(msg);
		receivedMessage = msg.split(":", 3);
		/*if(clients.size()==2)
				clients.get(1).send(ID + ": "+input);*/
		if(receivedMessage[1].equals(sent)){
			if(clients.size()==2)
				clients.get(1).send(ID + ": "+input);
		}else if(receivedMessage[1].equals(next)){
			
		}else if(receivedMessage[1].equals(disconnect)){
			
		}
	}
	
	private void findSecondTalker(ChatServerThread firstTalker){
		Random myRandom = new Random();
		ChatServerThread otherTalker =null;
		otherTalker = clients.get(myRandom.nextInt(clients.size()));
		while(clients.size()<=1 || otherTalker.getID() == firstTalker.getID() || otherTalker.isChatting()){
			otherTalker = clients.get(myRandom.nextInt(clients.size()));
		}
		
		
	}

	//dodanie klienta do tablicy klientów 
	private void addThread(Socket socket) {

		serverWindow.setNotificationArea("Client accepted: "+socket);
		clients.add(new ChatServerThread(this, socket));
		try{
			//otwarcie komunikacji i rozpoczêcie w¹tku odpowiedzialnego za komunikacjê
			clients.get(clients.size()-1).open();
			clients.get(clients.size()-1).start();

		}catch(IOException ioe){
			serverWindow.setNotificationArea("Error opening thread: " + ioe);
		}
	}
	
	
	//stworzenie watku servera odpowiadaj¹cego za usuniecie klienta
	public synchronized void remove(int ID){
		int pos = findClient(ID);
		
		if(pos >= 0){
			ChatServerThread toTerminate = clients.remove(pos);
			serverWindow.setNotificationArea("Removing client thread" + ID + " at "+ pos);
			try{
				toTerminate.close();
			}catch(IOException ioe){
				serverWindow.setNotificationArea("Error closing thread: "+ ioe);
			}
			toTerminate.stop();
		}
	}
	
	//znalezienie klienta
	private int findClient(int ID){
		for(int i = 0; i< clients.size(); i++){
			if(clients.get(i).getID() == ID)
				return i;
			
		}
		return -1;
	}
	
	private void start() {
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void stop() {
		if(thread != null){
			
			//thread.stop();
			thread = null;
		}
	}
}

















