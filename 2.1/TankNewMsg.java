import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
public class TankNewMsg implements Msg{
	private Tank tank;
	private TankClient tc;
	int msg_type = Msg.TANK_NEW_MSG; 
	
	public TankNewMsg(){
		
	}
	
	//ͨ��������������Tank���Ա㷢��̹��id����Ϣ
	public TankNewMsg(Tank tank){
		this.tank = tank;
	}
	
	//TankClient���ã��Ա㴦�����Է���˵���Ϣ
	public TankNewMsg(TankClient tc){
		this.tc = tc;
	}
	
	//��Tank��������Ϣ�����ͨ��UDP��ʽ���͸�ָ��IP���˿ڵķ���ˣ�dsָ�����Ͷ˶˿�
	public void send(DatagramSocket ds, String ip, int udpport){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(msg_type);
			dos.writeInt(tank.id);
			dos.writeInt(tank.getTank_x());
			dos.writeInt(tank.getTank_y());
			dos.writeInt(tank.getDir().ordinal());
			dos.writeBoolean(tank.isGood());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] buf = baos.toByteArray();
		
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(ip, udpport));
			ds.send(dp);
			System.out.println("Sent a DatagramPacket to server");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//�������Է���˵���Ϣ
	public void parse(DataInputStream dis){
		try{
			int id = dis.readInt();
			if(id == tc.myTank.id){
				System.out.println("that is mine");
				return;
			}
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			boolean exist = false;
			for(int i = 0; i < tc.enemyTanks.size(); i++){
				Tank t = tc.enemyTanks.get(i);
				if(t.id == id){
					exist = true;
					break;
				}
			}
			if(!exist){
				TankNewMsg msg = new TankNewMsg(tc.myTank);
				tc.nc.send(msg);
				Tank t = new Tank(x, y , good, dir, tc);
				t.id = id;
				tc.enemyTanks.add(t);
			}
			
			//System.out.println("id:"+id+"-x:"+x+"-y:"+y+"-dir:"+dir+"-good:"+good);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
