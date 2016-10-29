import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tank {
	private int tank_x;
	private int tank_y;
	public static final int TANK_W = 30;
	public static final int TANK_H = 30;
	public static final int XSPEED = 20;
	public static final int YSPEED = 20;
	private static boolean isU = false, isD =false, isL =false, isR =false;
	
	private boolean live = true; 
	private Dir eject_dir = Dir.D; 
	TankClient tc;
	
	
	
	private boolean good;
	private Random r = new Random();
	
	enum Dir{U, D, L, R, LU, RU, LD, RD, STOP};
	private static final String dir_des[] = {"U", "D", "L", "R", "LU", "RU", "LD", "RD"};
	private Dir dir = Dir.STOP;
	
	public Tank(int x, int y, boolean good){
		this.tank_x = x;
		this.tank_y = y;
		this.good = good;
		if(this.good == false){
			this.eject_dir = Dir.valueOf(dir_des[r.nextInt(8)]);
		}
	}
	public Tank(int x, int y, boolean good, TankClient tc){
		this(x, y, good);
		this.tc = tc;
	}
	
	
	//根据炮筒方向画出炮筒
	void ejectDir(Graphics g){
		switch(eject_dir){
		case U:
			g.drawLine(tank_x + TANK_W/2, tank_y + TANK_H/2, tank_x + TANK_W/2, tank_y - TANK_H/4);
			break;
		case D:
			g.drawLine(tank_x + TANK_W/2, tank_y + TANK_H/2, tank_x + TANK_W/2, tank_y + TANK_H +TANK_H/4);
			break;
		case L:
			g.drawLine(tank_x + TANK_W/2, tank_y + TANK_H/2, tank_x - TANK_H/4, tank_y + TANK_H/2);
			break;
		case R:
			g.drawLine(tank_x + TANK_W/2, tank_y + TANK_H/2, tank_x + TANK_W + TANK_H/4, tank_y + TANK_H/2);
			break;
		case LU:
			g.drawLine(tank_x + TANK_W/2, tank_y + TANK_H/2, tank_x, tank_y);
			break;
		case RU:
			g.drawLine(tank_x + TANK_W/2, tank_y + TANK_H/2, tank_x + TANK_W, tank_y);
			break;
		case LD:
			g.drawLine(tank_x + TANK_W/2, tank_y + TANK_H/2, tank_x, tank_y + TANK_H);
			break;
		case RD:
			g.drawLine(tank_x + TANK_W/2, tank_y + TANK_H/2, tank_x + TANK_W, tank_y + TANK_H);
			break;
		}
	}
	
	//画出Tank
	public void draw(Graphics g){
		if(!live) return;
		Color c = g.getColor();
		if(good) {
			g.setColor(Color.ORANGE);
			g.fillRect(tank_x, tank_y, TANK_W, TANK_H);
		}
		else {
			g.setColor(Color.cyan);
			g.fillOval(tank_x, tank_y, TANK_W, TANK_H);
		}
		g.setColor(Color.BLACK);
		ejectDir(g);
		g.setColor(c);
		
		move();
	}
	
	//根据按键方向移动
	void move(){
		if(!good){
			this.dir = this.eject_dir;
			switch(dir){
			case U:
				tank_y -= YSPEED;
				break;
			case D:
				tank_y += YSPEED;
				break;
			case L:
				tank_x -= XSPEED;
				break;
			case R:
				tank_x += XSPEED;
				break;
			case LU:
				tank_x -= XSPEED;
				tank_y -= YSPEED;
				break;
			case RU:
				tank_x += XSPEED;
				tank_y -= YSPEED;
				break;
			case LD:
				tank_x -= XSPEED;
				tank_y += YSPEED;
				break;
			case RD:
				tank_x += XSPEED;
				tank_y += YSPEED;
				break;
			case STOP:
				break;
			}
			outOfRange();
		}
		else{
			switch(dir){
			case U:
				tank_y -= YSPEED;
				break;
			case D:
				tank_y += YSPEED;
				break;
			case L:
				tank_x -= XSPEED;
				break;
			case R:
				tank_x += XSPEED;
				break;
			case LU:
				tank_x -= XSPEED;
				tank_y -= YSPEED;
				break;
			case RU:
				tank_x += XSPEED;
				tank_y -= YSPEED;
				break;
			case LD:
				tank_x -= XSPEED;
				tank_y += YSPEED;
				break;
			case RD:
				tank_x += XSPEED;
				tank_y += YSPEED;
				break;
			case STOP:
				break;
			}
			outOfRange();
		}
		
		if(dir != Dir.STOP) eject_dir = dir;
	}
	
	//不让坦克出界
	void outOfRange(){
		if(!good){
			if(tank_x < 0) this.dir = Dir.R;
			else if(tank_y < tc.getTitleLen()) this.dir = Dir.D;
			else if(tank_x > TankClient.GAME_WIDTH - TANK_W ) this.dir = Dir.L ;
			else if(tank_y > TankClient.GAME_HEIGHT - TANK_H) this.dir = Dir.U;
			//else this.dir = Dir.valueOf(dir_des[r.nextInt(8)]);
		}
		else{
			if(tank_x < 0) tank_x = 0;
			if(tank_y < tc.getTitleLen()) tank_y = tc.getTitleLen();
			if(tank_x > TankClient.GAME_WIDTH - TANK_W ) tank_x = TankClient.GAME_WIDTH - TANK_W ;
			if(tank_y > TankClient.GAME_HEIGHT - TANK_H) tank_y = TankClient.GAME_HEIGHT - TANK_H;		
		}
	}

	//返回子弹实例
	Missile fire(){
		return new Missile(this.tank_x + TANK_W/2 - Missile.MIS_W/2,this.tank_y + TANK_H/2 - Missile.MIS_H/2,
				eject_dir, this.tc);
	}
	
	//返回判断是否相撞的方块
	public Rectangle getRect(){
		return new Rectangle(this.tank_x, this.tank_y, TANK_W, TANK_H);
	}
	
	//处理TankClient传来的按下键盘事件
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key){
		case KeyEvent.VK_SPACE:
			tc.missiles.add(fire());
			break;
		case KeyEvent.VK_UP:
			isU = true;
			break;
		case KeyEvent.VK_DOWN:
			isD = true;
			break;
		case KeyEvent.VK_LEFT:
			isL = true;
			break;
		case KeyEvent.VK_RIGHT:
			isR = true;
			break;
		}
		locateDir();
	}
	//处理TankClient传来的释放键盘事件
		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
			switch(key){	
			case KeyEvent.VK_UP:
				isU = false;
				break;
			case KeyEvent.VK_DOWN:
				isD = false;
				break;
			case KeyEvent.VK_LEFT:
				isL = false;
				break;
			case KeyEvent.VK_RIGHT:
				isR = false;
				break;
			}
			locateDir();
		}
	
	//根据KeyPressed()和KeyReleased()的处理结果设置Tank方向
	void locateDir(){
		if(isU && !isD && !isL && !isR) { dir = Dir.U;}
		else if(!isU && isD && !isL && !isR) {dir = Dir.D;}
		else if(!isU && !isD && isL && !isR) {dir = Dir.L;}
		else if(!isU && !isD && !isL && isR) {dir = Dir.R;}
		else if(isU && !isD && isL && !isR) {dir = Dir.LU;}
		else if(isU && !isD && !isL && isR) {dir = Dir.RU;}
		else if(!isU && isD && isL && !isR) {dir = Dir.LD;}
		else if(!isU && isD && !isL && isR) {dir = Dir.RD;}
		else if(!isU && !isD && !isL && !isR) { dir = Dir.STOP;}
	}
	
	//设置坦克是否死亡
	public void setLive(boolean live) {
		this.live = live;
	}
	public boolean getLive(){
		return this.live;
	}
	
	public int getTank_x() {
		return tank_x;
	}
	public void setTank_x(int tank_x) {
		this.tank_x = tank_x;
	}
	public int getTank_y() {
		return tank_y;
	}
	public void setTank_y(int tank_y) {
		this.tank_y = tank_y;
	}

}
