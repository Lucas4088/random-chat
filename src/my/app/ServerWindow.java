package my.app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class ServerWindow extends JFrame implements Runnable {
	private JTextArea notificationArea;
	private ChatServer chatServer;
	private int port;
	private JButton btnStartServer;
	private JButton btnStopServer;
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
					//ServerWindow window = new ServerWindow();
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
	public ServerWindow(ChatServer cS, int port) {
		chatServer = cS;
		this.port = port;
		this.setName("Server");
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		setBounds(100, 100, 450, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		this.setTitle("Server");
		notificationArea = new JTextArea();
		notificationArea.setEditable(false);
		notificationArea.setBounds(10, 11, 414, 209);
		//getContentPane().add(notificationArea);
		
		btnStartServer = new JButton("Start");
		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatServer.establishConnection(port);
				disableStartButton();
			}
		});
		btnStartServer.setBounds(94, 231, 89, 23);
		getContentPane().add(btnStartServer);
		
		btnStopServer = new JButton("Stop");
		btnStopServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disableStopButton();
				chatServer.endConnection();
			}
		});
		btnStopServer.setBounds(239, 231, 89, 23);
		getContentPane().add(btnStopServer);
		DefaultCaret caret = (DefaultCaret) notificationArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane(notificationArea);
		scrollPane.setBounds(10, 11, 414, 209);
		getContentPane().add(scrollPane);
	}
	
	public void setNotificationArea(String msg){
		notificationArea.append(msg);
		notificationArea.append("\n");
	}

	public void disableStartButton(){
		btnStartServer.setEnabled(false);
	}
	
	public void enableStartButton(){
		btnStartServer.setEnabled(true);
	}
	
	public void disableStopButton(){
		btnStopServer.setEnabled(false);
	}
	
	public void enableStopButton(){
		btnStopServer.setEnabled(true);
	}
}
