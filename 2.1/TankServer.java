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
		
		//创建TCP连接，建立client对象保存来自客户端的IP、udp端口信息（每一坦克对应一个udp端口，自身有ID属性）
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
	
	//Client内部类，保存客户端的IP，udp端口信息
	class Client{
		String ip;
		int udpPort;
		
		public Client(String ip, int udpPort){
			this.ip = ip;
			this.udpPort = udpPort;
		}
	}
	
	//UDPThread内部类，接收客户端通过UDP方式传来的信息，并将其转发到已记录的客户端
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
