package zombie;

import java.awt.Color;
import java.util.Random;

public class Rock {
    private ZombieModel model;
    private int lat;
    private int lon;
    private int radius;
    private boolean success;

    public Rock(ZombieModel mModel) {
        model = mModel;

        Random random = new Random();
        radius = random.nextInt(5) + 4;
        lat = random.nextInt(model.getWidth());
        lon = random.nextInt(model.getHeight());

        if(checkBounds(lat, lon, radius)) {
            for(int i = 0; i <= (radius * 2); i++) {
                for(int j = 0; j <= (radius * 2); j++) {
                    if(distance((i + lat), lat, (j + lon), lon, radius)) {
                        model.setColor(lat - i, lon - j, Color.GRAY);
                        model.setColor(lat - i, j + lon, Color.GRAY);
                        model.setColor(i + lat, j + lon, Color.GRAY);
                        model.setColor(i + lat, lon - j, Color.GRAY);
                    }
                }
            }
            success = true;
        } else {
            success = false;
        }
    }

    private boolean distance(int lata, int latb, int lona, int lonb, int radius) {
        double distance = Math.pow((latb - lata), 2) + Math.pow((lonb - lona), 2);
        return distance < (radius * 2);
    }
    private boolean checkBounds(int lat, int lon, int radius) {
        if (lat <= (radius * 2) || lon <= (radius * 2)) return false;
        
        if (lat >= (model.getWidth() - (radius * 2)) || lon >= (model.getHeight() - (radius * 2))) return false;
        
        for (int i = 0; i < (radius * 2); i++) {
        	for (int j = 0; j < (radius * 2); j++) {
        		if (distance((i + lat), lat, (j + lon), lon, radius)) {
        			if (model.getColor(lat - i, lon - j) != Color.BLACK) return false;
                    if (model.getColor(lat - i, j + lon) != Color.BLACK) return false;
                    if (model.getColor(i + lat, j + lon) != Color.BLACK) return false;
                    if (model.getColor(i + lat, lon - j) != Color.BLACK) return false;
        		}
        	}
        }
    	return true;
    }


    public boolean isSuccess() {
        return success;
    }
}
