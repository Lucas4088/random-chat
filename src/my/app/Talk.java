package my.app;

public class Talk {
	private ChatServerThread client1;
	private ChatServerThread client2;
	
	public Talk(ChatServerThread c1, ChatServerThread c2){
		client1 = c1;
		client2 = c2;
	}

	public ChatServerThread getClient1() {
		return client1;
	}

	public void setClient1(ChatServerThread client1) {
		this.client1 = client1;
	}

	public ChatServerThread getClient2() {
		return client2;
	}

	public void setClient2(ChatServerThread client2) {
		this.client2 = client2;
	}
}
