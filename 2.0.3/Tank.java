import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tank {
	private int tank_x;
	private int tank_y;
	private int old_x, old_y;
	public static final int TANK_W = 25;
	public static final int TANK_H = 25;
	public static final int XSPEED = 10;
	public static final int YSPEED = 10;
	private static final int MOVE_STEP = 10;
	private static boolean isU = false, isD = false, isL = false, isR = false;

	private boolean live = true;
	private Dir eject_dir = Dir.D;
	TankClient tc;

	private boolean good;
	public boolean isGood() {
		return good;
	}

	private static Random r = new Random();
	private int move_step = MOVE_STEP;

//	enum Dir {
//		U, D, L, R, LU, RU, LD, RD, STOP
//	};

	private Dir dir = Dir.D;

	public Tank(int x, int y, boolean good) {
		this.tank_x = x;
		this.tank_y = y;
		this.good = good;
		if(good) this.dir = Dir.STOP;
	}

	public Tank(int x, int y, boolean good, TankClient tc) {
		this(x, y, good);
		this.tc = tc;
	}

	// 根据炮筒方向画出炮筒
	void ejectDir(Graphics g) {
		switch (eject_dir) {
		case U:
			g.drawLine(tank_x + TANK_W / 2, tank_y + TANK_H / 2, tank_x + TANK_W / 2, tank_y - TANK_H / 4);
			break;
		case D:
			g.drawLine(tank_x + TANK_W / 2, tank_y + TANK_H / 2, tank_x + TANK_W / 2, tank_y + TANK_H + TANK_H / 4);
			break;
		case L:
			g.drawLine(tank_x + TANK_W / 2, tank_y + TANK_H / 2, tank_x - TANK_H / 4, tank_y + TANK_H / 2);
			break;
		case R:
			g.drawLine(tank_x + TANK_W / 2, tank_y + TANK_H / 2, tank_x + TANK_W + TANK_H / 4, tank_y + TANK_H / 2);
			break;
		case LU:
			g.drawLine(tank_x + TANK_W / 2, tank_y + TANK_H / 2, tank_x, tank_y);
			break;
		case RU:
			g.drawLine(tank_x + TANK_W / 2, tank_y + TANK_H / 2, tank_x + TANK_W, tank_y);
			break;
		case LD:
			g.drawLine(tank_x + TANK_W / 2, tank_y + TANK_H / 2, tank_x, tank_y + TANK_H);
			break;
		case RD:
			g.drawLine(tank_x + TANK_W / 2, tank_y + TANK_H / 2, tank_x + TANK_W, tank_y + TANK_H);
			break;
		}
	}

	// 画出Tank
	public void draw(Graphics g) {
		if (!live){
			if(!good){
				tc.enemyTanks.remove(this);
			}
			return;
		}
			
		Color c = g.getColor();
		if (good) {
			g.setColor(Color.ORANGE);
			g.fillRect(tank_x, tank_y, TANK_W, TANK_H);
		} else {
			g.setColor(Color.CYAN);
			g.fillRect(tank_x, tank_y, TANK_W, TANK_H);
		}
		g.setColor(Color.BLACK);
		ejectDir(g);
		g.setColor(c);

		move();
	}

	// 根据按键方向移动
	void move() {
		this.old_x = this.tank_x;
		this.old_y = this.tank_y;
		switch (dir) {
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
			tank_x -= XSPEED*Math.sqrt(2)/2;
			tank_y -= YSPEED*Math.sqrt(2)/2;
			break;
		case RU:
			tank_x += XSPEED*Math.sqrt(2)/2;
			tank_y -= YSPEED*Math.sqrt(2)/2;
			break;
		case LD:
			tank_x -= XSPEED*Math.sqrt(2)/2;
			tank_y += YSPEED*Math.sqrt(2)/2;
			break;
		case RD:
			tank_x += XSPEED*Math.sqrt(2)/2;
			tank_y += YSPEED*Math.sqrt(2)/2;
			break;
		case STOP:
			break;
		}
		outOfRange();

		if (dir != Dir.STOP)
			eject_dir = dir;
		
		if(!good){
			move_step--;
			if(move_step == 0){
				Dir[] dirs = Dir.values();
				int rn = r.nextInt(dirs.length);
				this.dir = dirs[rn];
				move_step = MOVE_STEP;
				
				if(r.nextInt(40) > 20) tc.missiles.add(this.fire());
			}	
		}
	}

	// 不让坦克出界
	void outOfRange() {
		if (tank_x < 0)
			tank_x = 0;
		if (tank_y < tc.getTitleLen())
			tank_y = tc.getTitleLen();
		if (tank_x > TankClient.GAME_WIDTH - TANK_W)
			tank_x = TankClient.GAME_WIDTH - TANK_W;
		if (tank_y > TankClient.GAME_HEIGHT - TANK_H)
			tank_y = TankClient.GAME_HEIGHT - TANK_H;
	}
	
	//坦克回到上个位置
	void stay(){
		this.tank_x = this.old_x;
		this.tank_y = this.old_y;
	}

	// 返回子弹实例
	Missile fire() {
		if(!live) return null;
		return new Missile(this.tank_x + TANK_W / 2 - Missile.MIS_W / 2, this.tank_y + TANK_H / 2 - Missile.MIS_H / 2,
				this.good, eject_dir, this.tc);
	}
	Missile fire(Dir dir){
		if(!live) return null;
		return new Missile(this.tank_x + TANK_W / 2 - Missile.MIS_W / 2, this.tank_y + TANK_H / 2 - Missile.MIS_H / 2,
				this.good, dir, this.tc);
	}
	
	//超级炮弹
	private void superFire(){
		Dir[] dirs = Dir.values();
		for(int i = 0; i < 8; i++){
			if(this.live){
				tc.missiles.add(fire(dirs[i]));
			}
		}
	}

	// 返回判断是否相撞的方块
	public Rectangle getRect() {
		return new Rectangle(this.tank_x, this.tank_y, TANK_W, TANK_H);
	}
	
	//判断坦克与一墙是否相撞
	public boolean collidesWall(Wall w){
		if(this.live && this.getRect().intersects(w.getRect())){
			this.stay();
			return true;
		}
		return false;
	}
	//判断坦克与任一墙是否相撞
	public boolean collidesWalls(){
		for(int i = 0; i < tc.walls.size(); i++){
			if(collidesWall(tc.walls.get(i)))
				return true;
		}
		return false;
	}
	
	//
	public boolean collidesTanks(java.util.List<Tank> tanks){
		for(int i = 0; i < tanks.size(); i++){
			Tank t = tanks.get(i);
			if(this != t && this.live && t.getLive() && this.getRect().intersects(t.getRect())){
				this.stay();
				return true;
			}
		}
		if(this != tc.myTank && this.live && tc.myTank.getLive() && this.getRect().intersects(tc.myTank.getRect())){
			this.stay();
			return true;
		}
		
		
		return false;
	}

	// 处理TankClient传来的按下键盘事件
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			switch (key) {
			case KeyEvent.VK_SPACE:
				if(live) tc.missiles.add(fire());
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
			case KeyEvent.VK_ENTER:
				this.live = true;
				break;
			case KeyEvent.VK_A:
				superFire();
				break;
			}
			locateDir();
		}
		
	// 处理TankClient传来的释放键盘事件
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
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

	// 根据KeyPressed()和KeyReleased()的处理结果设置Tank方向
	void locateDir() {
		if (isU && !isD && !isL && !isR) {
			dir = Dir.U;
		} else if (!isU && isD && !isL && !isR) {
			dir = Dir.D;
		} else if (!isU && !isD && isL && !isR) {
			dir = Dir.L;
		} else if (!isU && !isD && !isL && isR) {
			dir = Dir.R;
		} else if (isU && !isD && isL && !isR) {
			dir = Dir.LU;
		} else if (isU && !isD && !isL && isR) {
			dir = Dir.RU;
		} else if (!isU && isD && isL && !isR) {
			dir = Dir.LD;
		} else if (!isU && isD && !isL && isR) {
			dir = Dir.RD;
		} else if (!isU && !isD && !isL && !isR) {
			dir = Dir.STOP;
		}
	}

	// 设置坦克是否死亡
	public void setLive(boolean live) {
		this.live = live;
	}

	public boolean getLive() {
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
