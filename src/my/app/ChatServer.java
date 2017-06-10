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
	private String send, next, disconnect,ask, allow;
	private ArrayList<Talk> talks;
	private Lock talkLock;
	
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
		disconnect = new String("Disconnect");
		talks = new ArrayList<>();
		
		talkLock = new ReentrantLock();
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
		//String msg = new String(input);
		//serverWindow.setNotificationArea(msg);
		receivedMessage = input.split(":", 3);
		/*if(clients.size()==2)
				clients.get(1).send(ID + ": "+input);*/
		if(receivedMessage[1].equals(ask)){
			//System.out.println("chuj");
			//client.send(server+":"+allow+":"+msg);
			//clients.get(0).send(server+":"+allow+":"+msg);
			//findTalker();
		}else if(receivedMessage[1].equals(send)){
			//System.out.println("dupa");
			serverWindow.setNotificationArea("determining receiver");
			//erverWindow.setNotificationArea("input");
			/*if(clients.size()==2)
				clients.get(1).send(ID + ": "+input);*/
			//serverWindow.setNotificationArea(input);
			determineReceiver(Integer.valueOf(receivedMessage[0])).send(input);
		}else if(receivedMessage[1].equals(next)){
			
		}else if(receivedMessage[1].equals(disconnect)){
			
		}
	}
	
	public synchronized ChatServerThread determineReceiver(int id){
		//serverWindow.setNotificationArea("determining receiver");
		serverWindow.setNotificationArea("ID: "+String.valueOf(id));
		for(Talk talk : talks){
			System.out.println("talk.getClient1(): "+talk.getClient1().getID());
			System.out.println("talk.getClient2(): "+talk.getClient2().getID());
				if(talk.getClient2().getID() == id){
					serverWindow.setNotificationArea("Sending to: "+String.valueOf(talk.getClient1().getID()));
					return talk.getClient1();
				}
				else if(talk.getClient1().getID() == id){
					serverWindow.setNotificationArea("Sending to: "+String.valueOf(talk.getClient2().getID()));
					return talk.getClient2();
				}
			
		}
		return null;
	}
	
	//dodaje do tablicy rozmow nowa rozmowe
	public void findSecondTalker(ChatServerThread firstTalker){
		if(clients.size()>1){
		serverWindow.setNotificationArea("Looking for second Talker");
		Random myRandom = new Random();
		int talkID = -1;
		boolean unique = false;
		ChatServerThread otherTalker =null;
		otherTalker = clients.get(myRandom.nextInt(clients.size()));
		
		while(clients.size()<=1 || otherTalker.getID() == firstTalker.getID() || otherTalker.isChatting()){
			otherTalker = clients.get(myRandom.nextInt(clients.size()));
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
		addTalk(new Talk(firstTalker, otherTalker, talkID));
		
		serverWindow.setNotificationArea("Talk created: " + talkID);
		}
		
	}
	
	public void removeTalk(Talk t){
		
		
			serverWindow.setNotificationArea("Talk removed");
			t.getClient1().setChatting(false);
			t.getClient2().setChatting(false);
			synchronized(talks){
				talks.remove(t);
			}
		
	}
	
	public void addTalk(Talk t){

		t.getClient1().send(t.getClient1().getID()+":"+allow+":"+"");
		t.getClient2().send(t.getClient1().getID()+":"+allow+":"+"");
	
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
			//clients.get(clients.size()-1).findTalker();

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

