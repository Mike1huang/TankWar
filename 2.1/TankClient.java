import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TankClient extends JFrame implements ActionListener, ChangeListener {
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 592;
	public static final int R = 100, G = 100, B = 100, A = 100;
	
	public static int  bg_r = R, bg_g = G, bg_b = B, bg_a = A;

	private JButton buts[];
	private JLabel jl_name;
	private JPanel jp_show, jp_win;
	private Random ran = new Random();
	private static int wall_num = 1;
	private JScrollPane js;
	private static String but_des[]={"S","I","O","X"};
	private static final String msg = ""
			+ "������˵����\r\n"
			+ "��������Ʒ���\r\n"
			+ "�ո�������ڵ�\r\n"
//			+ "Enter������\r\n"
			+ "��N��������������\r\n\r\n"
			
			+ "�汾 V2.1\r\n"
			+ "�����¡�\r\n"
			+ "1. ����S��ť��BUG\r\n"
			+ "2. ������������\r\n\r\n"
			
			+ "�����ڡ�\r\n"
			+ "���ߣ�Cin\r\n"
			+ "�ο�����ʿ��̹�˴�ս��Ŀ\r\n"
			+ "2016.10.28\r\n";

	private TankWarSetting ts;
	Tank myTank = new Tank(300, 200, true, this);
	
	NetConnect nc = new NetConnect(TankClient.this);
	
	NetDialog dialog = new NetDialog();
	
	
	List<Missile> missiles = new ArrayList<Missile>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Tank> enemyTanks = new ArrayList<Tank>();
	List<Wall> walls = new ArrayList<Wall>();

	public TankClient() {
		super();
	}
	public TankClient(int r, int g, int b, int a){
		bg_r = r;
		bg_g = g;
		bg_b = b;
		bg_a = a;
	}
	

	// ��launchFrame()�����������棬�ӹ��췽������
	public void lauchFrame() {
		this.setTitle("TankWar");
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setLocationRelativeTo(null); // Ĭ�Ͼ���
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		 

		this.setResizable(false); // �����С����
		this.getContentPane().setBackground(Color.BLACK);

		// ����͸������
		this.setUndecorated(true);
		this.setBackground(new Color(bg_r, bg_g, bg_b, bg_a));
		//this.setOpacity(0.5f);
		
		
		this.ts = new TankWarSetting(this);
		
		JTextArea ja = new JTextArea(msg);
		ja.setOpaque(false);
		ja.setLineWrap(true);
		ja.setEditable(false);
		ja.setFocusable(false);
		this.js = new JScrollPane(ja);
		js.setOpaque(false);//���ù�������͸��
		js.getViewport().setOpaque(false);

		// Ϊ͸��JFrame������Ʊ�ǩ�����ڡ���С�����رհ�ť
		this.buts = new JButton[but_des.length];
		for(int i = 0; i < this.buts.length; i++){
			this.buts[i] = new JButton(but_des[i]);
			this.buts[i].addActionListener(this);
			this.buts[i].setFocusable(false);
			this.buts[i].setContentAreaFilled(false);//��ť͸�� 
		}	
		
		this.jl_name = new JLabel("TankWar");
		this.jp_show = new JPanel(new BorderLayout());
		this.jp_win = new JPanel(new GridLayout(1, 3));
		this.jp_win.setOpaque(false);
		this.jp_show.add(this.jl_name, "West");
		this.jp_show.add(this.jp_win, "East");
		
		for(int i = 0; i < this.buts.length; i++){
			this.jp_win.add(this.buts[i]);
		}
		//this.buts[0].setVisible(false);

		this.jp_show.addMouseMotionListener(new MouseMonitor());
		this.getContentPane().add(this.jp_show, "North");
		this.jp_show.setOpaque(false);

		this.addKeyListener(new KeyMonitor());

		new Thread(new PaintThread()).start();
		//nc.connect("127.0.0.1", TankServer.TCP_PORT);

		this.setVisible(true);
	}

	// ���ر������Ŀ��
	public int getTitleLen() {
		return jp_show.getHeight();
	}

	// ����paint()����
	public void paint(Graphics g) {
		super.paint(g); // �̳и����paint()����
				
		g.setColor(Color.BLACK);
		g.drawString("�ڵ� : " + missiles.size(), 10, 40);
		g.drawString("��̹ : " + enemyTanks.size(), 10, 55);
		g.drawString("��ը : " + explodes.size(), 10, 70);
		g.drawString("RGBA: " + bg_r+","+ bg_g+","+ bg_b+","+ bg_a, 480, 20);
		
		while(myTank == null){
			myTank = new Tank(ran.nextInt(GAME_WIDTH), ran.nextInt(GAME_HEIGHT - jp_show.getHeight()) + jp_show.getHeight(), true,this);
			
				myTank.collidesWalls();
				myTank.collidesTanks(enemyTanks);
		}
		
		if(walls.size() == 0) ini_Wall();//ûǽ���ʼ��ǽ
		for(int i = 0; i < walls.size(); i++){
		Wall w = walls.get(i);
			w.draw(g);
		}
		
//		if(enemyTanks.size() == 0){//û�з�̹�����ʼ���з�̹��
//			ini_Tank();
//		}
		
		for (int i = 0; i < missiles.size(); i++) {//���ڵ�
			Missile m = missiles.get(i);
			if(m.hitTank(myTank)) {
				TankDeadMsg msg = new TankDeadMsg(myTank.id);
				nc.send(msg);
				MissileDeadMsg mdmMsg = new MissileDeadMsg(m.tankId, m.id);
				nc.send(mdmMsg);
			}
			m.draw(g);
			m.hitWalls();
			
		}
		
		for(int i = 0; i < explodes.size(); i++){//����ը
			Explode e = explodes.get(i);
			e.draw(g);
		}
		
		for(int i = 0; i < enemyTanks.size(); i++){//���з�̹��
			Tank t = enemyTanks.get(i);
			t.collidesTanks(enemyTanks);
			t.collidesWalls();
			t.draw(g);
		}
		
		myTank.draw(g);
		myTank.collidesWalls();
		myTank.collidesTanks(enemyTanks);
	}
	
	//��ʼ���з�̹��
	private void ini_Tank(){
		for(int i = 0; i < 7; i++){
			Tank t = new Tank(ran.nextInt(GAME_WIDTH), ran.nextInt(GAME_HEIGHT - jp_show.getHeight()) + jp_show.getHeight(), false, this);
		    if(!t.collidesWalls()) enemyTanks.add(t);//�����ײǽ�������
		    else --i;
		}
	}
	//��ʼ��ǽ
	private void ini_Wall(){
		switch(wall_num){
		case 1:
			walls.add(new Wall(GAME_WIDTH*2/16, GAME_HEIGHT*2/16, GAME_WIDTH*2/16, GAME_HEIGHT*10/16, this));
			walls.add(new Wall(GAME_WIDTH*1/16, GAME_HEIGHT*6/16, GAME_WIDTH*4/16, GAME_HEIGHT*2/16, this));
			walls.add(new Wall(GAME_WIDTH*1/16, GAME_HEIGHT*10/16, GAME_WIDTH*5/16, GAME_HEIGHT*2/16, this));
			walls.add(new Wall(GAME_WIDTH*8/16, GAME_HEIGHT*2/16, GAME_WIDTH*6/16, GAME_HEIGHT*2/16, this));
			walls.add(new Wall(GAME_WIDTH*8/16, GAME_HEIGHT*5/16, GAME_WIDTH*2/16, GAME_HEIGHT*3/16, this));
			walls.add(new Wall(GAME_WIDTH*12/16, GAME_HEIGHT*5/16, GAME_WIDTH*2/16, GAME_HEIGHT*3/16, this));
			walls.add(new Wall(GAME_WIDTH*8/16, GAME_HEIGHT*6/16, GAME_WIDTH*6/16, GAME_HEIGHT*1/16, this));
			walls.add(new Wall(GAME_WIDTH*8/16, GAME_HEIGHT*9/16, GAME_WIDTH*6/16, GAME_HEIGHT*2/16, this));
			walls.add(new Wall(GAME_WIDTH*7/16, GAME_HEIGHT*12/16, GAME_WIDTH*8/16, GAME_HEIGHT*2/16, this));
		}
	}
	

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.lauchFrame();
		;
	}
	
	class NetDialog extends JDialog{
		JButton but_conf = new JButton("ȷ��");
		String str[][] = {{"IP:","Port:","My UDP Port:"},{"127.0.0.1",""+TankServer.TCP_PORT,"2333"},{"12","4","4"}};
		JLabel jl[];
		JTextField jt[];
		public NetDialog(){
			super(TankClient.this, true);
			this.setSize(500, 80);
			this.setLocationRelativeTo(null);
			this.setLayout(new FlowLayout());
			
			jl = new JLabel[str[0].length];
			jt = new JTextField[str[1].length];
			for(int i = 0; i < jl.length; i++){
				jl[i] = new JLabel(str[0][i]);
				jt[i] = new JTextField(str[1][i],Integer.parseInt(str[2][i]));
				this.getContentPane().add(jl[i]);
				this.getContentPane().add(jt[i]);
			}
			this.getContentPane().add(but_conf);
			but_conf.addActionListener(new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String IP = jt[0].getText().trim();
					int port = TankServer.TCP_PORT;
					int myUDPPort = 2333;
					try{
						port = Integer.parseInt(jt[1].getText().trim());
						myUDPPort = Integer.parseInt(jt[2].getText().trim());
					}catch(NumberFormatException ex){	
					}
					nc.setUdpPort(myUDPPort);
					nc.connect(IP, port);
					setVisible(false);
				}
			});
			
		}
	}

	// �߳��࣬�ػ�
	private class PaintThread implements Runnable {
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// ��������ʵ���϶�JP_show���϶�����
	private class MouseMonitor extends MouseAdapter {

		@Override
		public void mouseDragged(MouseEvent e) {
			TankClient.this.setLocation(e.getLocationOnScreen());
		}
	}

	// ���̼����������ƶ�����
	private class KeyMonitor extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			// ��ü����¼�������Tank����
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_N){
				dialog.setVisible(true);	
			}
			
			myTank.keyPressed(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("X")) {
			System.exit(0);
		}
		if (e.getActionCommand().equals("O")) {
			this.setExtendedState(JFrame.ICONIFIED);// �������С��
		}
		if(e.getActionCommand().equals("I")){
			JOptionPane.showMessageDialog(jp_show, js);
		}
		if(e.getActionCommand().equals("S")){
			int confirm = JOptionPane.showConfirmDialog(jp_show, ts, "��������", JOptionPane.YES_NO_OPTION);
			if(confirm == 0){
				try{
					int i = Integer.parseInt(ts.jt.getText());
					if(i > 0 && i < 255){
						TankWarSetting.new_a = i;
					}
				}
				catch(NumberFormatException ex){
					
				}
				
				TankWarSetting.old_r = bg_r = TankWarSetting.new_r;
				TankWarSetting.old_g = bg_g = TankWarSetting.new_g;
				TankWarSetting.old_b = bg_b = TankWarSetting.new_b;
				TankWarSetting.old_a = bg_a = TankWarSetting.new_a;
		
			}
			else{
				bg_r = TankWarSetting.old_r;
				bg_g = TankWarSetting.old_g;
				bg_b = TankWarSetting.old_b;
				bg_a = TankWarSetting.old_a;
			}
			
			ts.setSlider(bg_a);
			this.setBackground(new Color(bg_r, bg_g, bg_b, bg_a));
		}
		if(e.getActionCommand().equals("Ĭ��")){
			TankWarSetting.new_r = R; 
			TankWarSetting.new_g = G;
			TankWarSetting.new_b = B;
			TankWarSetting.new_a = A;
			
			this.setBackground(new Color(R, G, B, A));
		}
		if(e.getActionCommand().equals("�Զ���")){
			Color c = JColorChooser.showDialog(jp_show, "Background Color", new Color(bg_r,bg_g, bg_b));
			if(c != null){
				TankWarSetting.new_r = c.getRed(); 
				TankWarSetting.new_g = c.getGreen();
				TankWarSetting.new_b = c.getBlue();
				
				this.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue(), bg_a));
			}
		}	
	}
	
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == this.ts.slider){
			this.ts.jt.setText(this.ts.slider.getValue()+"");
			this.setBackground(new Color(TankWarSetting.new_r, TankWarSetting.new_g, TankWarSetting.new_b, this.ts.slider.getValue()));
		}
	}

}
