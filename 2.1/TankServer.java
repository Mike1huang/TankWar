import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class TankServer extends JFrame{
	private static int id = 100;
	public static final int TCP_PORT = 8888;
	public static final int UDP_PORT = 2266;
	List<Client> clients = new ArrayList<Client>();
	JTextArea ja;
	
	public void launch(){
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		ja = new JTextArea();
		ja.setLineWrap(true);
		ja.setEditable(false);
		this.getContentPane().add(ja);
		
		
		this.setVisible(true);
	}
	
	public void start() {
		
		new Thread(new UDPThread()).start();
		
		//����TCP���ӣ�����client���󱣴����Կͻ��˵�IP��udp�˿���Ϣ��ÿһ̹�˶�Ӧһ��udp�˿ڣ�������ID���ԣ�
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(TCP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			Socket s = null;
			try {
				s = ss.accept();
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String IP = s.getInetAddress().getHostAddress();
				int udpPort = dis.readInt();
				Client c = new Client(IP, udpPort);
				clients.add(c);
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(id++);
//System.out.println("A Client Connects! Addr-" + s.getInetAddress() + ":" + s.getPort());
			    ja.append("A Client Connects! Addr-" + s.getInetAddress() + ":" + s.getPort()+"\r\n");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (s != null) {
					try {
						s.close();
						s = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	public static void main(String[] args) {
		TankServer ts = new TankServer();
		ts.launch();
		ts.start();
	}
	
	//Client�ڲ��࣬����ͻ��˵�IP��udp�˿���Ϣ
	class Client{
		String ip;
		int udpPort;
		
		public Client(String ip, int udpPort){
			this.ip = ip;
			this.udpPort = udpPort;
		}
	}
	
	//UDPThread�ڲ��࣬���տͻ���ͨ��UDP��ʽ��������Ϣ��������ת�����Ѽ�¼�Ŀͻ���
	private class UDPThread implements Runnable{
		byte[] buf = new byte[1024];

		@Override
		public void run() {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket(UDP_PORT);				
			} catch (SocketException e) {
				e.printStackTrace();
			}
			while(ds != null){
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try{
					ds.receive(dp);
					//System.out.println("Server got a DatagramPacket" );
					for(int i = 0; i < clients.size(); i++){
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.ip, c.udpPort));
						ds.send(dp);
					//System.out.println("Server sent DatagramPackets to all clients");
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		
	}

}
