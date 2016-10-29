import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

public class Explode {
	int x, y;
	private boolean live = true;
	private TankClient tc;
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static boolean init = false;
	
	private static Image[] imgs = {
		tk.getImage(Explode.class.getClassLoader().getResource("images/0.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/1.gif")),	
		tk.getImage(Explode.class.getClassLoader().getResource("images/2.gif")),	
		tk.getImage(Explode.class.getClassLoader().getResource("images/3.gif")),	
		tk.getImage(Explode.class.getClassLoader().getResource("images/4.gif")),	
		tk.getImage(Explode.class.getClassLoader().getResource("images/5.gif")),	
		tk.getImage(Explode.class.getClassLoader().getResource("images/6.gif")),	
		tk.getImage(Explode.class.getClassLoader().getResource("images/7.gif")),	
		tk.getImage(Explode.class.getClassLoader().getResource("images/8.gif")),	
		tk.getImage(Explode.class.getClassLoader().getResource("images/9.gif")),	
		tk.getImage(Explode.class.getClassLoader().getResource("images/10.gif")),	
	};
	
	int step = 0;
	
	public Explode(int x, int y, TankClient tc){
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	
	public void draw(Graphics g){
		if(!live) {
			tc.explodes.remove(this);
			return;
		}
		
		if(!init){//先将图片加载到内存，防止异步输入输出导致图片未加载就执行draw()
			for (int i = 0; i < imgs.length; i++) {
				g.drawImage(imgs[i], -100, -100, null);
			}
		}
		
		if(step == imgs.length){
			live = false;
			step = 0;
			return;
		}
		
		g.drawImage(imgs[step], x, y, null);
		
		step++;
		
	}

}
