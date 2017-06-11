package my.app;

import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;

public class ChatServer implements Runnable {
	private ArrayList<ChatServerThread> clients;
	private ServerSocket server = null;
	private Thread thread = null;
	private ServerWindow serverWindow;
	private ChatServerThread client = null;
	private String[] receivedMessage;
	private String send, next, disconnect,ask, allow,wait,connect;
	private ArrayList<Talk> talks;
	private Lock talkLock;
	//private Lock lookLock;
	//iniciajcja portu i gniazda
	public ChatServer(int port) {
		clients = new ArrayList<>();
		serverWindow = new ServerWindow(this,port);
		new Thread(serverWindow).start();
		serverWindow.setVisible(true);
		send = new String("Send");
		next = new String("Next");
		ask = new String("Ask");
		allow = new String("Allow");
		wait = new String("Wait");
		connect = new String("Connect");
		disconnect = new String("Disconnect");
		next = new String("Next");
		talks = new ArrayList<>();
		
		talkLock = new ReentrantLock();
		//lookLock = new ReentrantLock();
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
			
			//client = new ChatServerThread(this, server);
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
	public synchronized void handle(String input){
		
		serverWindow.setNotificationArea(input);
		receivedMessage = input.split(":", 3);

		if(receivedMessage[1].equals(ask)){
			serverWindow.setNotificationArea(receivedMessage[0]+":Wait:");
			clients.get(findClient(Integer.valueOf(receivedMessage[0]))).send(":"+wait+":Looking for a companion ...");
			//rozpoczêcie poczukiwania towarzysza do rozmowy
			clients.get(findClient(Integer.valueOf(receivedMessage[0]))).setChatting(false);
		}else if(receivedMessage[1].equals(send)){
			
			serverWindow.setNotificationArea("determining receiver");
			determineReceiver(Integer.valueOf(receivedMessage[0])).send(input);
			
		}else if(receivedMessage[1].equals(next)){
			Talk talkToDelete = talkToDelete(Integer.valueOf(receivedMessage[0]));
			if(talkToDelete == null){
				clients.get(findClient(Integer.valueOf(receivedMessage[0]))).send(":"+connect+":Connect command");
			}else{
				ChatServerThread receiver = determineReceiver(Integer.valueOf(receivedMessage[0]));
				//determineReceiver(Integer.valueOf(receivedMessage[0])).send(":"+next+":Stranger has disconnected");
				removeTalk(talkToDelete);
				receiver.send(":"+next+":Stranger has disconnected\n Press next to Look for Stranger");
				clients.get(findClient(Integer.valueOf(receivedMessage[0]))).send(":"+next+":You have disconnected");
				clients.get(findClient(Integer.valueOf(receivedMessage[0]))).send(":"+wait+":Waiting ...");
				clients.get(findClient(Integer.valueOf(receivedMessage[0]))).send(":"+connect+":Connect command");
			}
			//clients.get(findClient(Integer.valueOf(receivedMessage[0]))).send(":"+connect+":");
			//clients.get(findClient(Integer.valueOf(receivedMessage[0]))).setChatting(false);
			
			//determineReceiver(Integer.valueOf(receivedMessage[0])).send(":"+next+":Stranger has disconnected");
			//determineReceiver(Integer.valueOf(receivedMessage[0])).send(":"+connect+":Stranger has disconnected");
			//determineReceiver(Integer.valueOf(receivedMessage[0])).setChatting(false);
			
			
			
			
			
		}else if(receivedMessage[1].equals(disconnect)){
			
		}
	}
	
	public Talk talkToDelete(int id){
		Talk talkToDelete = null;
		for(Talk talk : talks){
			if(talk.getClient2().getID() == id){
				serverWindow.setNotificationArea("Deleting talk between: "+String.valueOf(talk.getClient1().getID())
						+":"+String.valueOf(talk.getClient2().getID()));
				talkToDelete = talk;
			}
			else if(talk.getClient1().getID() == id){
				talkToDelete = talk;
			}
		}	
		return  talkToDelete;
	
	}

	public ChatServerThread determineReceiver(int id){

		for(Talk talk : talks){
				if(talk.getClient2().getID() == id){
					serverWindow.setNotificationArea(talk.getTalkID()+":Sending to: "+String.valueOf(talk.getClient1().getID()));
					return talk.getClient1();
				}
				else if(talk.getClient1().getID() == id){
					serverWindow.setNotificationArea(talk.getTalkID()+":Sending to: "+String.valueOf(talk.getClient2().getID()));
					return talk.getClient2();
				}
			
		}
		return null;
	}
	
	//dodaje do tablicy rozmow nowa rozmowe
	public void  findSecondTalker(ChatServerThread firstTalker){
		if(clients.size()>1){
		serverWindow.setNotificationArea("Looking for second Talker");
		Random myRandom = new Random();
		int talkID = -1;
		boolean unique = false;
		ChatServerThread otherTalker =null;

		otherTalker = clients.get(myRandom.nextInt(clients.size()));
		
		while(!firstTalker.isChatting()&&(clients.size()<=1 || otherTalker.getID() == firstTalker.getID() || otherTalker.isChatting())){
			otherTalker = clients.get(myRandom.nextInt(clients.size()));
			System.out.println("Stuck");
		}
		serverWindow.setNotificationArea("Second Talker Found: "+otherTalker.getID());
		while(!unique){
			unique = true;
			talkID = myRandom.nextInt(10000);
			for(Talk talk : talks){
				if(talkID == talk.getTalkID())
					unique = false;
			}
		}

		serverWindow.setNotificationArea("Talk ID: "+talkID);
		
		talkLock.lock();
		try{
			if(!otherTalker.isChatting()){
				addTalk(new Talk(firstTalker, otherTalker, talkID));
				
				serverWindow.setNotificationArea("Talk created: " + talkID);
				clients.get(findClient(otherTalker.getID())).send(":"+allow+":You are now chatting");
				clients.get(findClient(firstTalker.getID())).send(":"+allow+":You are now chatting");
			}else{
				;
			}
		}finally{
			talkLock.unlock();
		}
		
		}
		
	}
	
	public void removeTalk(Talk t){
		Talk talk = t;
		
			
			synchronized(talks){
				talks.remove(t);
			}
			/*talk.getClient1().setChatting(false);
			talk.getClient2().setChatting(false);*/
			serverWindow.setNotificationArea("Talk removed: "+talk.getTalkID());
			
			for(ChatServerThread client : clients){
				System.out.println(client.isChatting());
			}
	}
	
	public void addTalk(Talk t){

		//t.getClient1().send(t.getClient1().getID()+":"+allow+":"+"");
		//t.getClient2().send(t.getClient1().getID()+":"+allow+":"+"");
		
		t.getClient1().setChatting(true);
		t.getClient2().setChatting(true);
		synchronized(talks){	
			talks.add(t);
		}
		serverWindow.setNotificationArea("No."+t.getTalkID() +" Talk between :"+
		t.getClient1().getID()+" : "+ t.getClient2().getID());
		
	}

	//dodanie klienta do tablicy klientów 
	private synchronized void addThread(Socket socket) {

		serverWindow.setNotificationArea("Client accepted: "+socket);
		clients.add(new ChatServerThread(this, socket));
		try{
			//otwarcie komunikacji i rozpoczêcie w¹tku odpowiedzialnego za komunikacjê
			clients.get(clients.size()-1).open();
			clients.get(clients.size()-1).start();
			clients.get(clients.size()-1).send(":"+connect+":You have been connected to the server");
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

