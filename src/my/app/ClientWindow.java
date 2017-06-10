package my.app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import java.awt.GridLayout;
import javax.swing.JScrollPane;

public class ClientWindow extends JFrame implements Runnable {

	private JFrame frame;
	private CardLayout cardLayout;
	private JTextArea messageDisplayArea = new JTextArea();
	private JTextArea messageSendArea = new JTextArea();
	private CardLayout cards;
	private ChatClient chatClient = null;
	private JButton btnSend;
	/**
	 * Launch the application.
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//ClientWindow window = new ClientWindow();
					/*window.frame.setVisible(true);*/
					//window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientWindow(ChatClient cC) {
		chatClient = cC;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		/*frame = new JFrame();*/
		/*frame.*/setBounds(100, 100, 632, 427);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Client");
		cardLayout = new CardLayout();
		
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		
		
		JPanel connectionCard = new JPanel();
		//cardLayout.addLayoutComponent(connectionCard, "name_779615792272941");
		connectionCard.setLayout(null);
		
		JPanel chatCard = new JPanel();
		//cardLayout.addLayoutComponent(chatCard, "name_779615801882191");
		chatCard.setLayout(null);
		
		cardPanel.add(connectionCard, "Conenction Card");
		cardPanel.add(chatCard, "Chat Card");
		//cardLayout.show(chatCard, "Chat Card");
		
		JButton btnNewButton = new JButton("Connect");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatClient.establishConnection();
				cardLayout.show(cardPanel, "Chat Card");
			}
		});
		/*frame.*/getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		btnNewButton.setBounds(226, 174, 101, 34);
		connectionCard.add(btnNewButton);
		
		
		messageSendArea.setBounds(10, 325, 323, 52);
		chatCard.add(messageSendArea);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//1getUserMessage();
				messageDisplayArea.append("You: "+messageSendArea.getText()+"\n");
				chatClient.getUserMessage(getUserTextMessage("Send"));
				
			}
		});
		
		btnSend.setEnabled(false);
		messageSendArea.setEditable(false);
		
		btnSend.setBounds(343, 325, 70, 52);
		chatCard.add(btnSend);
		
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatClient.getUserMessage(getUserTextMessage("Next"));
			}
		});
		btnNext.setBounds(423, 325, 70, 52);
		chatCard.add(btnNext);
		
		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatClient.disconnect();
				cardLayout.show(cardPanel, "Conenction Card");
				
			}
		});
		btnDisconnect.setBounds(503, 325, 98, 52);
		chatCard.add(btnDisconnect);
		messageDisplayArea.setEditable(false);
		messageDisplayArea.setBounds(10, 11, 591, 294);
		
		DefaultCaret caret = (DefaultCaret) messageDisplayArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		//chatCard.add(messageDisplayArea);
		
		JScrollPane scrollPane = new JScrollPane(messageDisplayArea);
		scrollPane.setBounds(10, 11, 596, 294);
		chatCard.add(scrollPane);
		

		/*frame.*/getContentPane().add(cardPanel);
	}
	
	public String getUserTextMessage(String command){
		String msg = new String(command);
		msg  = msg.concat(":" +messageSendArea.getText()) ;
		messageSendArea.setText("");
		//chatClient.setSend(true);
		messageSendArea.requestFocus();
		System.out.println(msg);
		return msg;
	}
	
	public void setMessageDisplayArea(String str){
		messageDisplayArea.append(str);
		messageDisplayArea.append("\n");
	}

	public void allowClientToWrite(){
		btnSend.setEnabled(true);
		messageSendArea.setEditable(true);
	}
	
}
