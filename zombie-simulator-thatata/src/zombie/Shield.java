package zombie;

import java.awt.Color;
import java.util.Random;

public class Shield {
	private ZombieModel model;
	private int lat;
	private int lon;
	private boolean drawSuccess;
	
	public Shield(ZombieModel mModel) {
		model = mModel;
		Random random = new Random();
		lat = random.nextInt(model.getWidth());
		lon = random.nextInt(model.getHeight());
		
		if (checkLocation(lat, lon)) {
				model.setColor(lat, lon, Color.CYAN);
				model.setColor(lat + 1, lon, Color.CYAN);
				model.setColor(lat, lon + 1, Color.CYAN);
				model.setColor(lat + 1, lon + 1, Color.CYAN);
				drawSuccess = true;
		} else {
			drawSuccess = false;
		}
	}
	
	public int getLat() {
		return this.lat;
	}

	public int getLon() {
		return this.lon;
	}
	
	public boolean isSuccess() {
		return drawSuccess;
	}
	
	public boolean checkLocation(int lat, int lon) {
		// Check width and height of canvas
		if (lat < 0 || lon < 0) return false;
		
		if (lat >= (model.getWidth() - 2) || lon >= (model.getHeight() - 3)) return false;
		
		// Check if too close to other entities
		if (!(model.getColor(lat, lon) == Color.BLACK)) return false;
		
		if (!(model.getColor(lat + 1, lon) == Color.BLACK)) return false;
		
		if (!(model.getColor(lat, lon + 1) == Color.BLACK)) return false;
		
		if (!(model.getColor(lat + 1, lon + 1) == Color.BLACK)) return false;
		
		return true;
	}
}
