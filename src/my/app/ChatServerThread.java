package my.app;

import java.io.*;
import java.net.*;


public class ChatServerThread extends Thread {
	private ChatServer server = null;
	private Socket socket = null;
	private int ID = -1;
	private volatile boolean chatting;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	
	public ChatServerThread(ChatServer serv, Socket soc){
		super();
		server = serv;
		socket = soc;
		ID = socket.getPort();
		chatting = false;
		try {
			open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run(){
		System.out.println("Server Thread " + ID + " runninig");
		while(true){
			try{
				//System.out.println(streamIn.readUTF());
				if(!chatting)
				findTalker();
				
				server.handle(streamIn.readUTF());
			}catch(IOException ioe){
				System.out.println(ID + " ERROREN reading: "+ ioe.getMessage());
				server.remove(ID);
				stop();
			}
		}
	}
	
	//wyslanie wiadomosci przez sserver
	@SuppressWarnings("deprecation")
	public void send(String msg){
		try{
			streamOut.writeUTF(msg);
			streamOut.flush();
		}catch(IOException ioe){
			System.out.println(ID + " Error sending: "+ ioe.getMessage());
			server.remove(ID);
			stop();
			
		}
	}
	//zwrocene id klienta 
	public int getID(){
		return ID;
	}
	
	//otwarcie 
	public void open() throws IOException{
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		
	}
	
	public void findTalker(){
		server.findSecondTalker(this);
	}
	
	public void close() throws IOException {
		if(socket != null) socket.close();
		if(streamIn != null) streamIn.close();
		if(streamOut != null) streamOut.close();
	}
	
	public void endTalk(){
		
	}
	
	public boolean isChatting() {
		return chatting;
	}

	public void setChatting(boolean chatting) {
		this.chatting = chatting;
	}
}
