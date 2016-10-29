import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Missile {
	public static final int XSPEED = 25;
	public static final int YSPEED = 25;
	public static final int MIS_W = 10;
	public static final int MIS_H = 10;
	
	private boolean live = true; 
	
	private TankClient tc;
	
	private int mis_x, mis_y;
	Tank.Dir dir;
	
	
	public Missile(int mis_x, int mis_y, Tank.Dir dir) {
		this.mis_x = mis_x;
		this.mis_y = mis_y;
		this.dir = dir;
	}
	public Missile(int mis_x, int mis_y, Tank.Dir dir, TankClient tc){
		this(mis_x, mis_y, dir);
		this.tc = tc;  
	}
	
	//画出子弹
	public void draw(Graphics g){
		if(!live){
			tc.missiles.remove(this);
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.RED);
		g.fillOval(mis_x, mis_y, MIS_W, MIS_H);
		g.setColor(c);
		
		move();
	}

	//子弹运动
	private void move() {
		switch(dir){
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
			mis_x -= XSPEED;
			mis_y -= YSPEED;
			break;
		case RU:
			mis_x += XSPEED;
			mis_y -= YSPEED;
			break;
		case LD:
			mis_x -= XSPEED;
			mis_y += YSPEED;
			break;
		case RD:
			mis_x += XSPEED;
			mis_y += YSPEED;
			break;
	    }
		if(mis_x < 0 || mis_y < tc.getTitleLen() || mis_x > TankClient.GAME_WIDTH || mis_y > TankClient.GAME_HEIGHT ){
			live = false;
			
		}
		
	}
	
	//返回判断是否相撞的方块
	public Rectangle getRect(){
		return new Rectangle(this.mis_x, this.mis_y, MIS_W, MIS_H);
	}
	
	//判断子弹与坦克是否相撞
	public boolean hitTank(Tank t){
		if(this.getRect().intersects(t.getRect())&& t.getLive() == true){//碰撞并且坦克活着
			t.setLive(false);
			live = false;
			return true;
		}
		return false;
	}
	
	//返回子弹是否存在
	public boolean isLive() {
		return live;
	}
}
