package zombie;

import java.awt.Color;
import java.util.Random;

public class Tree {
	private ZombieModel model;
	private int lat;
	private int lon;
	private boolean drawSuccess;
	
	public Tree(ZombieModel mModel) {
		model = mModel;
		Random random = new Random();
		lat = random.nextInt(model.getWidth());
		lon = random.nextInt(model.getHeight());
		
		if (checkLocation(lat, lon)) {
				model.setColor(lat, lon, Color.GREEN);
				model.setColor(lat + 1, lon, Color.GREEN);
				model.setColor(lat - 1, lon, Color.GREEN);
				model.setColor(lat, lon + 1, Color.GREEN);
				model.setColor(lat, lon - 1, Color.GREEN);
				drawSuccess = true;
		} else {
			drawSuccess = false;
		}
	}
	
	public boolean checkLocation(int lat, int lon) {
		// Check width and height of canvas
		if (lat < 2 || lon < 2) return false;
		
		if (lat > (model.getWidth() - 2) || lon > (model.getHeight() - 2)) return false;
		
		// Check if too close to other entities
		if (!(model.getColor(lat, lon) == Color.BLACK)) return false;
		
		if (!(model.getColor(lat + 1, lon) == Color.BLACK)) return false;
		
		if (!(model.getColor(lat - 1, lon) == Color.BLACK)) return false;
		
		if (!(model.getColor(lat, lon + 1) == Color.BLACK)) return false;
		
		if (!(model.getColor(lat, lon - 1) == Color.BLACK)) return false;
		
		return true;
	}

	public int getLat() {
		return lat;
	}

	public int getLon() {
		return lon;
	}
	
	public boolean isSuccess() {
		return drawSuccess;
	}
}
