package pods.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import util.Vec;

public class Drawer implements ActionListener {
	public static final double SCALE = 0.01;
	public static final Vec SHIFT = new Vec(20, 20);
	public static final Color[] POD_COLORS = new Color[] {Color.green, Color.blue, Color.orange};
	public static final Color BACKGROUND = Color.white;
	public static final Color DEFAULT_COLOR = Color.black;
	public static final Color DIR_COLOR = Color.magenta;
	public static final Color VEL_COLOR = Color.cyan;
	
	public static Vec adjust(Vec v) {
		return v.times(SCALE).plus(SHIFT);
	}
	
	public static void circle(Graphics g, Vec center, double rad, Color color) {
		center = adjust(center);
		rad *= SCALE;
		g.setColor(color);
		g.fillOval((int)(center.x - rad), (int)(center.y - rad), (int)rad*2, (int)rad*2);
	}
	
	public static void line(Graphics g, Vec a, Vec b, Color color) {
		g.setColor(color);
		a = adjust(a);
		b = adjust(b);
		g.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
	}
	
	public static void rect(Graphics g, Vec a, Vec b, Color color) {
		a = adjust(a);
		b = adjust(b);
		g.setColor(color);
		g.drawRect((int)a.x, (int)a.y, (int)b.x, (int)b.y);
	}
	
	public class MyPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(BACKGROUND);
			g.clearRect(0, 0, getWidth(), getHeight());
			rect(g, Vec.ORIGIN, new Vec(PodWorld.WORLD_X, PodWorld.WORLD_Y), DEFAULT_COLOR);
			
			for(int i=0; i<world.getCheckpoints().size(); i++) {
				Vec check = world.getCheckpoints().get(i);
				circle(g, check, PodWorld.CHECK_RADIUS, colorForCheck(world, i));
			}
			
			for(int i=0; i<world.getPods().size(); i++)
				drawPod(g, world.getPods().get(i), i);
		}
		
		private Color colorForCheck(PodWorld world, int check) {
			for(int i=0; i<world.getPods().size(); i++) {
				if(world.getPods().get(i).nextCheck == check)
					return POD_COLORS[i];
			}
			return Color.black;
		}

		private void drawPod(Graphics g, PodInfo pod, int idx) {
			Vec dir = Vec.UNIT.rotate(pod.angle).times(PodWorld.POD_RADIUS);
			circle(g, pod.pos, PodWorld.POD_RADIUS, POD_COLORS[idx]);
			
			// Draw facing vector
			dir = dir.times(5);
			line(g, pod.pos, pod.pos.plus(dir), DIR_COLOR);
			
			// Draw velocity
			line(g, pod.pos, pod.pos.plus(pod.vel.times(3)), VEL_COLOR);
		}
	}
	
	private final Timer timer;
	private final MyPanel panel = new MyPanel();
	private final PodWorld world;
	
	public Drawer(PodWorld world) {
		this.world = world;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createGUI();
			}
		});
		
		timer = new Timer(150, this);
		timer.setRepeats(true);
		timer.start();
	}
	
	private void createGUI() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize((int)(PodWorld.WORLD_X*SCALE + 2*SHIFT.x) + 20, (int)(PodWorld.WORLD_Y*SCALE + 2*SHIFT.y) + 40);
		frame.setVisible(true);
		
		frame.add(panel);
	}
	
	public void actionPerformed(ActionEvent e) {
		world.step();
		panel.repaint();
	}
}
