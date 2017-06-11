package my.app;

import java.io.*;
import java.net.*;


public class ChatServerThread extends Thread {
	private ChatServer server = null;
	private Socket socket = null;
	private int ID = -1;
	private volatile boolean chatting;
	private volatile boolean lookingForChat;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	
	public ChatServerThread(ChatServer serv, Socket soc){
		super();
		server = serv;
		socket = soc;
		ID = socket.getPort();
		chatting = true;
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
				server.handle(streamIn.readUTF());
				if(!chatting){
					//System.out.println("find talker");
					findTalker();
					//System.out.println("Talker found ");
				}
				//System.out.println("running:"+this.ID);
				
				
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
	
	public boolean isLookingForChat() {
		return lookingForChat;
	}

	public void setLookingForChat(boolean lFC) {
		this.lookingForChat = lFC;
	}
}
