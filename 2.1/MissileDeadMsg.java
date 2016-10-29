import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MissileDeadMsg implements Msg {
	private TankClient tc;
	int msgType = Msg.MISSILE_DEAD_MSG; 
	int tankId;
	int id;
	

	public MissileDeadMsg(int tankId, int id) {
		this.tankId = tankId;
		this.id = id;
	}

	public MissileDeadMsg(TankClient tc) {
		this.tc = tc;
	}

	@Override
	public void send(DatagramSocket ds, String ip, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tankId);
			dos.writeInt(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(ip, udpPort));
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void parse(DataInputStream dis) {
		try {
			int tankId = dis.readInt();
			int id = dis.readInt();
			
			for(int i = 0; i < tc.missiles.size(); i++) {
				Missile m = tc.missiles.get(i);
				if(m.tankId == tankId && m.id == id) {
					m.setLive(false);
					tc.explodes.add(new Explode(m.getMis_x(),m.getMis_y(),tc));
					
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
