import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetConnect {
	private TankClient tc;
	private int udpPort;
	private String ip;
	DatagramSocket ds = null;
	
	//�������ݰ�Socket����ds��ָ�����Ͷ˿�
	public NetConnect(TankClient tc){
		this.tc = tc;
		
	}
	
	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	//����TCP���ӣ���̹��ID�����������
	public void connect(String ip, int port){
		this.ip =ip;
		Socket s = null;
		try{
			ds = new DatagramSocket(udpPort);
		}catch(SocketException e){
			e.printStackTrace();
		}
		try {
			s = new Socket(ip, port);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort);
			DataInputStream dis  = new DataInputStream(s.getInputStream());
			int id = dis.readInt();
			tc.myTank.id = id;
			if(id % 2 == 0) tc.myTank.setGood(true);
			else tc.myTank.setGood(false);
//			System.out.println("Connected to Server, server gave me an ID:" + id);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(s != null){
				try{
					s.close();
					s = null;
				}
				catch(IOException ex){
					ex.printStackTrace();
				}
			}
		}
		
		TankNewMsg msg = new TankNewMsg(tc.myTank);//�½�̹�˵���Ϣ��
		send(msg);//���½�̹�˵���Ϣ��ͨ��UDP��ʽ���͵������
		
		new Thread(new UDPRecvThread()).start();
	}
	
	//����ָ����Ϣ����ָ��IP��ָ���˿ڵķ���ˣ�����TankNewMsg���send()��������
	public void send(Msg msg){
		msg.send(ds, ip, TankServer.UDP_PORT);
	}
	
	private class UDPRecvThread implements Runnable{
		byte[] buf = new byte[1024];

		@Override
		public void run() {
			while(ds != null){//ֻҪds��Ϊ�գ���ָ���˷���/���ն˿ڣ�ÿ��̹��ʹ��һ��ͬһ���˿ڽ��з��͡��������ݰ������ͽ��մ������Է���˵���Ϣ
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
//					System.out.println("received DatagramPackets from server");
					parse(dp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
		}
		
		//����TankNewMsg�ķ����������Է���˵����ݰ���Ϣ
		private void parse(DatagramPacket dp){
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
			DataInputStream dis = new DataInputStream(bais);
			int msg_type = 0;
			try {
			    msg_type = dis.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Msg msg = null;
			switch(msg_type){
			case Msg.TANK_NEW_MSG:
				msg = new TankNewMsg(NetConnect.this.tc);
				msg.parse(dis);
				break;
			case Msg.TANK_MOVE_MSG:
				msg = new TankMoveMsg(NetConnect.this.tc);
				msg.parse(dis);
				break;
			case Msg.Missile_NEW_MSG:
				msg = new MissileNewMsg(NetConnect.this.tc);
				msg.parse(dis);
				break;
			case Msg.TANK_DEAD_MSG:
				msg = new TankDeadMsg(NetConnect.this.tc);
				msg.parse(dis);
				break;
			case Msg.MISSILE_DEAD_MSG:
				msg = new MissileDeadMsg(NetConnect.this.tc);
				msg.parse(dis);
				break;
			}
			
		}
		
	}

}
