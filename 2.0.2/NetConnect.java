import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetConnect {
	public static int UDP_PORT_START = 2333;
	private TankClient tc;
	private int udpPort;
	
	public NetConnect(){
		this.udpPort = UDP_PORT_START++;
	}
	
	public void connect(String ip, int port){
		Socket s = null;
		try {
			s = new Socket(ip, port);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort);
			System.out.println("Connect to Server");
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
	}

}
