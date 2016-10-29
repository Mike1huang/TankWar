import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMoveMsg implements Msg{
	int id;
	int msg_type = Msg.TANK_MOVE_MSG;
	Dir dir, eject_dir;
	Tank tank;
	TankClient tc;
	public TankMoveMsg(int id, Dir dir, Dir eject_dir, Tank tank) {
		super();
		this.id = id;
		this.dir = dir;
		this.eject_dir = eject_dir;
		this.tank = tank;
	}
	public TankMoveMsg(TankClient tc){
		this.tc = tc;
	}
	@Override
	public void send(DatagramSocket ds, String ip, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try{
			dos.writeInt(msg_type);
			dos.writeInt(id);
			dos.writeInt(tank.getTank_x());
			dos.writeInt(tank.getTank_y());
			dos.writeInt(dir.ordinal());
			dos.writeInt(eject_dir.ordinal());
			
			//dos.writeBoolean(tank.isGood());
		}catch(IOException e){
			e.printStackTrace();
		}
		
		byte[] buts = baos.toByteArray();
		DatagramPacket dp = new DatagramPacket(buts, buts.length, new InetSocketAddress(ip, port));
		try {
			ds.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void parse(DataInputStream dis) {
		boolean exist = false;
		try {
			int id = dis.readInt();
			if(id == tc.myTank.id){
				return;
			}
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			Dir eject_dir = Dir.values()[dis.readInt()];
			for(int i = 0; i < tc.enemyTanks.size(); i++){
				Tank t = tc.enemyTanks.get(i);
				if(t.id == id){
					t.setTank_x(x);
					t.setTank_y(y);
					t.setDir(dir);
					t.setEject_dir(eject_dir);
					exist = true;
					break;
				}
			}
			
			//boolean good = dis.readBoolean();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
