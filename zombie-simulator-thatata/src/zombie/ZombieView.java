package zombie;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ZombieView extends JPanel {

	private final ZombieModel model;
	
	public ZombieView(ZombieModel modelArg) {
		model = modelArg;
	}
	
	public void draw() {
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, model.getWidth() * model.getDotSize(),
				model.getHeight() * model.getDotSize());
		for (int i = 0; i < model.getWidth(); i++) {
			for (int j = 0; j < model.getHeight(); j++) {
				g.setColor(model.getColor(i, j));
				g.fillRect(model.getDotSize() * i, model.getDotSize() * j,
						model.getDotSize(), model.getDotSize());
			}
		}
	}
	
}
