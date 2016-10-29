import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Missile {
	public static final int XSPEED = 15;
	public static final int YSPEED = 15;
	public static final int MIS_W = 10;
	public static final int MIS_H = 10;

	private boolean live = true;
	private boolean isGood = false;

	private TankClient tc;

	private int mis_x, mis_y;
	Dir dir;

	public Missile(int mis_x, int mis_y, Dir dir) {
		this.mis_x = mis_x;
		this.mis_y = mis_y;
		this.dir = dir;
	}

	public Missile(int mis_x, int mis_y, boolean good, Dir dir, TankClient tc) {
		this(mis_x, mis_y, dir);
		this.isGood = good;
		this.tc = tc;
	}

	// 画出子弹
	public void draw(Graphics g) {
		if (!live) {
			tc.missiles.remove(this);
			return;
		}
		Color c = g.getColor();
		if(isGood) g.setColor(Color.RED);
		else g.setColor(Color.BLACK);
		g.fillOval(mis_x, mis_y, MIS_W, MIS_H);
		g.setColor(c);

		move();
	}

	// 子弹运动
	private void move() {
		switch (dir) {
		case U:
			mis_y -= YSPEED;
			break;
		case D:
			mis_y += YSPEED;
			break;
		case L:
			mis_x -= XSPEED;
			break;
		case R:
			mis_x += XSPEED;
			break;
		case LU:
			mis_x -= XSPEED*Math.sqrt(2)/2;
			mis_y -= YSPEED*Math.sqrt(2)/2;
			break;
		case RU:
			mis_x += XSPEED*Math.sqrt(2)/2;
			mis_y -= YSPEED*Math.sqrt(2)/2;
			break;
		case LD:
			mis_x -= XSPEED*Math.sqrt(2)/2;
			mis_y += YSPEED*Math.sqrt(2)/2;
			break;
		case RD:
			mis_x += XSPEED*Math.sqrt(2)/2;
			mis_y += YSPEED*Math.sqrt(2)/2;
			break;
		}
		if (mis_x < 0 || mis_y < tc.getTitleLen() || mis_x > TankClient.GAME_WIDTH || mis_y > TankClient.GAME_HEIGHT) {
			live = false;

		}

	}

	// 返回判断是否相撞的方块
	public Rectangle getRect() {
		return new Rectangle(this.mis_x, this.mis_y, MIS_W, MIS_H);
	}

	// 判断子弹与一辆坦克是否相撞
	public boolean hitTank(Tank t) {
		if (this.live && this.getRect().intersects(t.getRect()) && t.getLive() == true && this.isGood != t.isGood()) {// 碰撞并且坦克活着
			t.setLive(false);
			this.live = false;
			tc.explodes.add(new Explode(this.mis_x, this.mis_y, tc));
			return true;
		}
		return false;
	}
	//判断子弹与任一辆坦克是否相撞
	public boolean hitTanks(){
		if(hitTank(tc.myTank))
			return true;
		for(int i = 0; i < tc.enemyTanks.size(); i++){
			if(hitTank(tc.enemyTanks.get(i)))
				return true;
		}
		return false;
	}
	
	//判断子弹与一墙是否相撞
	public boolean hitWall(Wall w){
		if(this.live && this.getRect().intersects(w.getRect())){
			this.live = false;
			return true;
		}
		return false;
	}
	//判断子弹与任一墙是否相撞
	public boolean hitWalls(){
		for(int i = 0; i < tc.walls.size(); i++){
			if(hitWall(tc.walls.get(i)))
				return true;
		}
		return false;
	}

	// 返回子弹是否存在
	public boolean isLive() {
		return live;
	}

}
