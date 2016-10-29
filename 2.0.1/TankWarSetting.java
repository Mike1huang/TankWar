import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;


public class TankWarSetting extends JPanel{
	static int old_r = TankClient.bg_r, old_g = TankClient.bg_g, old_b =TankClient.bg_b, old_a =TankClient.bg_a;
	static int new_r =TankClient.bg_r, new_g = TankClient.bg_g, new_b =TankClient.bg_b, new_a =TankClient.bg_a;
	private JLabel jl[];
	JTextField jt;
	private JButton buts[];
	Color c = new Color(old_r, old_g, old_b);
	private static String str[][] = {{"背景颜色","透明度(1~254)"},{"默认","自定义"}};
	
	private TankClient t;
	
	public TankWarSetting(TankClient t){
		this.t = t;
		
		Dimension d = new Dimension(300,60);
		this.setPreferredSize(d);
		this.setLayout(new GridLayout(2, 3));
		
		this.jl = new JLabel[str[0].length];
		for(int i = 0; i < this.jl.length; i++){
			this.jl[i] = new JLabel(str[0][i]);
			this.jl[i].setFocusable(false);
		}
		
		this.add(jl[0]);
		
		this.buts = new JButton[str[1].length];
		for(int i = 0; i < this.buts.length; i++){
			this.buts[i] = new JButton(str[1][i]);
			this.buts[i].addActionListener(this.t);
			this.buts[i].setFocusable(false);
			this.add(this.buts[i]);
		}
		
		this.add(jl[1]);
		
		this.jt = new JTextField(TankClient.bg_a);
		this.jt.setText(""+old_a);
		this.add(jt);
		
		
		
		
	}

}
