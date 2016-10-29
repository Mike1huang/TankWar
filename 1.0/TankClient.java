import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * 坦克大战，实现透明背景、敌我坦克、我方炮弹
 * @author Cin
 *
 */
public class TankClient extends JFrame implements ActionListener{
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	
	private JButton but_close, but_min;
	private JLabel jl_name;
	private JPanel jp_show, jp_win;
	
	Tank myTank = new Tank(300, 200, true, this);
	List<Missile> missiles = new ArrayList();
	List<Tank> tanks = new ArrayList(); 
	private Random r = new Random();
	

	public TankClient(){
		
	}
	
//由launchFrame()方法构建界面，从构造方法分离		
	public void lauchFrame(){
		this.setTitle("TankWar");
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setLocationRelativeTo(null);    //默认居中
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setResizable(false);    //界面大小不变
		this.getContentPane().setBackground(Color.BLACK);
		
		//设置透明背景
		this.setUndecorated(true);
		this.setBackground(new Color(100,100,100,100));
		
		//为透明JFrame添加名称标签，最小化及关闭按钮
		this.but_close = new JButton("X");
		this.but_close.addActionListener(this);
		this.but_close.setFocusable(false);
		this.but_min = new JButton("O");
		this.but_min.addActionListener(this);
		this.but_min.setFocusable(false);
		this.jl_name = new JLabel("TankWar");
		this.jp_show = new JPanel(new BorderLayout());
		this.jp_win = new JPanel(new GridLayout(1,2));
		this.jp_show.add(this.jl_name, "West");
		this.jp_show.add(this.jp_win, "East");
		this.jp_win.add(this.but_min, 0);
		this.jp_win.add(this.but_close, 1);
		
		this.jp_show.addMouseMotionListener(new MouseMonitor());
		this.getContentPane().add(this.jp_show, "North");
		
		this.addKeyListener(new KeyMonitor());
		
		initial();
		
		new Thread(new PaintThread()).start();
		
		this.setVisible(true);
	}
	
	//返回标题面板的宽度
	public int getTitleLen(){
		return jp_show.getHeight();
	}
	
	//覆盖paint()方法
	public void paint(Graphics g){
		super.paint(g);    //继承父类的paint()方法
		if(tanks.size() == 0) initial();
		for(int i = 0; i < missiles.size(); i++){
			Missile m = missiles.get(i);			
			m.draw(g);
			for(int j = 0; j < tanks.size(); j++){
				m.hitTank(tanks.get(j));
				if(!tanks.get(j).getLive()) tanks.remove(j);
			}
		}
		g.setColor(Color.RED);
		g.drawString(""+missiles.size(), 20, 120);
		myTank.draw(g);
		for(int i = 0; i < tanks.size(); i++){
			tanks.get(i).draw(g);
		}
	}
	
	//敌方坦克初始化
	private void initial(){
		for(int i = 0; i < 3; i++){
			Tank t = new Tank(r.nextInt(GAME_WIDTH), r.nextInt(GAME_HEIGHT), false, this);
			for(int j = 0; j < tanks.size() && tanks.size() != 0; j++){
				while(t.getTank_x() == tanks.get(j).getTank_x() && t.getTank_x() == tanks.get(j).getTank_x())
					t = new Tank(r.nextInt(GAME_WIDTH), r.nextInt(GAME_HEIGHT), false);
			}
			tanks.add(t);
		}
	}

	public static void main(String[] args) {
		new TankClient().lauchFrame();;
	}
	
	//线程类，重画
	private class PaintThread implements Runnable{
		public void run(){
			while(true){
				repaint();
				try{
					Thread.sleep(100);
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	//鼠标监听，实现拖动JP_show就拖动窗格
	private class MouseMonitor extends MouseAdapter{

		@Override
		public void mouseDragged(MouseEvent e) {
			TankClient.this.setLocation(e.getLocationOnScreen());
		}	
	}
	
	//键盘监听，控制移动方向
	private class KeyMonitor extends KeyAdapter{

		@Override
		public void keyPressed(KeyEvent e) {
			//获得键盘事件，传给Tank处理
			myTank.keyPressed(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == but_close){
			System.exit(0);
		}
		if(e.getSource() == but_min){
			this.setExtendedState(JFrame.ICONIFIED);//点击后最小化
		}
	}
	

}
