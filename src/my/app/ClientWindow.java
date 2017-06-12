package my.app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import java.awt.GridLayout;
import javax.swing.JScrollPane;
import static javax.swing.JOptionPane.showMessageDialog;
public class ClientWindow extends JFrame implements Runnable {

	private JFrame frame;
	private CardLayout cardLayout;
	private JTextArea messageDisplayArea = new JTextArea();
	private JTextArea messageSendArea = new JTextArea();
	private CardLayout cards;
	private ChatClient chatClient = null;
	private JButton btnSend;
	private JButton btnNext;
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
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we){
			cC.disconnect();
			cC.stop();
			System.exit(0);
			}
		});
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setBounds(100, 100, 632, 427);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setTitle("Client");
		cardLayout = new CardLayout();
		
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		
		
		JPanel connectionCard = new JPanel();
		connectionCard.setLayout(null);
		
		JPanel chatCard = new JPanel();
		chatCard.setLayout(null);
		
		cardPanel.add(connectionCard, "Conenction Card");
		cardPanel.add(chatCard, "Chat Card");
		
		JButton btnNewButton = new JButton("Connect");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
				chatClient.establishConnection();
				cardLayout.show(cardPanel, "Chat Card");
				}catch (Exception e1) {
					
					showMessageDialog(null, chatClient.getException());
					// TODO: handle exception
				}
			}
		});
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		btnNewButton.setBounds(226, 174, 101, 34);
		connectionCard.add(btnNewButton);
		
		
		messageSendArea.setBounds(10, 325, 323, 52);
		chatCard.add(messageSendArea);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				messageDisplayArea.append("You: "+messageSendArea.getText()+"\n");
				chatClient.getUserMessage(getUserTextMessage("Send"));
				
			}
		});
		
		btnSend.setEnabled(false);
		messageSendArea.setEditable(false);
		
		btnSend.setBounds(343, 325, 70, 52);
		chatCard.add(btnSend);
		
		btnNext = new JButton("Next");
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
		
		JScrollPane scrollPane = new JScrollPane(messageDisplayArea);
		scrollPane.setBounds(10, 11, 596, 294);
		chatCard.add(scrollPane);
		
		getContentPane().add(cardPanel);
	}
	
	public String getUserTextMessage(String command){
		String msg = new String(command);
		msg  = msg.concat(":" +messageSendArea.getText()) ;
		messageSendArea.setText("");
		messageSendArea.requestFocus();
		System.out.println(msg);
		return msg;
	}
	
	public void resetMessageDisplayArea(){
		messageDisplayArea.setText("");
	}
	
	public void setMessageDisplayArea(String str){
		messageDisplayArea.append(str);
		messageDisplayArea.append("\n");
	}

	public void allowClientToWrite(){
		btnSend.setEnabled(true);
		messageSendArea.setEditable(true);
		//messageDisplayArea.setText("");
	}
	
	public void enableNext(){
		btnNext.setEnabled(true);
	}
	
	public void disableNext(){
		btnNext.setEnabled(false);
	}
	
	public void disallowClientToWrite(){
		btnSend.setEnabled(false);
		messageSendArea.setEditable(false);
	}
	
}
