package zombie;

import java.awt.Color;
import java.util.Random;

public class River {
	private ZombieModel model;
	private int lat;
	private boolean drawSuccess;
	
	public River(ZombieModel mModel) {
		model = mModel;
		Random random = new Random();
		lat = random.nextInt(model.getWidth() - 5);
		
		while (!drawSuccess) {
			if (checkBounds(lat)) {
				for (int i = 0; i < model.getHeight(); i++) {
					for (int j = 0; j < 5; j++) {
						model.setColor(lat + j, i, Color.BLUE);
					}
				}
				drawSuccess = true;
			}
			else {
				drawSuccess = false;
			}
		}
	}
	
	private boolean checkBounds(int lat) {
		if (lat <= 0) return false;
		
		if (lat >= (model.getWidth() - 5)) return false;
		
		return true;
	}
	
	public boolean isSuccess() {
		return drawSuccess;
	}
}
