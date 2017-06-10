package my.app;

public class Talk {
	private final ChatServerThread client1;
	private final ChatServerThread client2;
	private final int talkID;
	
	public Talk(ChatServerThread c1, ChatServerThread c2, int talkID){
		client1 = c1;
		client2 = c2;
		this.talkID = talkID;
	}

	public int getTalkID() {
		return talkID;
	}

/*	public void setTalkID(int talkID) {
		this.talkID = talkID;
	}*/

	public ChatServerThread getClient1() {
		return client1;
	}

/*	public void setClient1(ChatServerThread client1) {
		this.client1 = client1;
	}*/

	public ChatServerThread getClient2() {
		return client2;
	}

/*	public void setClient2(ChatServerThread client2) {
		this.client2 = client2;
	}*/
}
