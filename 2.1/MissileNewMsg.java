import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileNewMsg implements Msg {
	Missile m;
	private int msg_type = Msg.Missile_NEW_MSG;
	TankClient tc;

	public MissileNewMsg(Missile m) {
		this.m = m;
	}
	public MissileNewMsg(TankClient tc) {
		this.tc = tc;
	}
    
	@Override
	public void send(DatagramSocket ds, String ip, int udpport) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(msg_type);
			dos.writeInt(m.tankId);
			dos.writeInt(m.id);
			dos.writeInt(m.getMis_x());
			dos.writeInt(m.getMis_y());
			dos.writeInt(m.dir.ordinal());
			dos.writeBoolean(m.isGood());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] buf = baos.toByteArray();
		
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(ip, udpport));
			ds.send(dp);
//			System.out.println("Sent a DatagramPacket to server");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void parse(DataInputStream dis) {
		try{
			int tankId = dis.readInt();
			if(tankId == tc.myTank.id){
				return;
			}
			int id = dis.readInt();
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			boolean exist = false;
			//for(int i = 0; i < tc.missiles.size(); i++){
			Missile m = new Missile(tankId, x, y, good, dir, tc);
			m.id = id;
			tc.missiles.add(m);
			//}
			
//			if(!exist){
//				TankNewMsg msg = new TankNewMsg(tc.myTank);
//				tc.nc.send(msg);
//				Tank t = new Tank(x, y , good, dir, tc);
//				t.id = id;
//				tc.enemyTanks.add(t);
//			}
			
//System.out.println("id:"+id+"-x:"+x+"-y:"+y+"-dir:"+dir+"-good:"+good);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
