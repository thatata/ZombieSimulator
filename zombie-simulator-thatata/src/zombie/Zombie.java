package zombie;

import java.awt.Color;
import java.util.Random;

public class Zombie extends Human {
		
	//private ZombieModel model;
	private Color color;
	private Color prevColor; // keep track of whether a zombie is in river/on land
	private boolean isAlpha;
	
	// Default constructor, assume normal zombie
	public Zombie(ZombieModel mModel, int lat, int lon) {
		super(mModel, false, lat, lon);
		color = Color.ORANGE;
		prevColor = Color.BLACK; // zombie has to be placed on land
		this.isAlpha = false;
	}
	
	// Alpha constructor, for the alpha zombie
	public Zombie(ZombieModel mModel, boolean isAlpha) {
		super(mModel, true, -1, -1);
		color = Color.RED;
		prevColor = Color.BLACK; // zombie has to be placed on land
		this.isAlpha = isAlpha;
	}
	
	@Override
	public void update() {
		//===========================================================================
		// CHECK MOUSE CLICKS FOR ALPHA ZOMBIE
		//
		// Check to see if the mouse has been clicked in a hot spot, if so
		// override the random direction and move alpha zombie in the direction
		// corresponding to the respective hot spot.
		//
		//===========================================================================
		int tempx = super.getModel().getTempx() / 10;
		int tempy = super.getModel().getTempy() / 10;
		
		Direction d = direction(tempx, tempy);
		if (d != null) {
			switch(d) {
				case NORTH:
					super.setDirection(Direction.NORTH);
					break;
				case EAST:
					super.setDirection(Direction.EAST);
					break;
				case SOUTH:
					super.setDirection(Direction.SOUTH);
					break;
				case WEST:
					super.setDirection(Direction.WEST);
					break;
			}
			if (walkValid()) walk();
			else super.turnAround();
			
			super.getModel().setTempx(0);
			super.getModel().setTempy(0);
			return;
		}
		//===========================================================================
		// NORMAL ZOMBIE MOVEMENT
		//
		// STEP ONE: check surroundings
		//
		// 1. Check surroundings, if zombie sees human in front, walks in that direction.
		//
		// 2. Otherwise, 50% chance of keeping same direction, 20% turn left, 20%
		//    chance turn right, or 10% chance of turning around.
		//
		// Note: the river is not an obstacle for a zombie
		//
    	//===========================================================================
		
		// Check direction (1) to see if human is in the distance
		if (seeHuman()) {
			if (walkValid()) {
				walk();
				return;
			}
		}
		
		// Implement probability of changing direction (2) and check to see if zombie can move there
		Random random = new Random();
		int probability = random.nextInt(10);
		
		// 0-4 - same direction (do nothing)
		
		// 5-6 - turn left
		if (probability == 5 || probability == 6) {
			super.turnLeft();
		}
		
		// 7-8 - turn right
		if (probability == 7 || probability == 8) {
			super.turnRight();
		}
		
		// 9 - turn around
		if (probability == 9) {
			super.turnAround();
		}
		
		// Check the new direction to see if zombie can move there, if obstacle/out of map turn around
		if (walkValid()) {
			walk();
		}
		else {
			super.turnAround();
		}
	}
	
	public boolean isAlpha() {
		return isAlpha;
	}
	
	private boolean seeHuman() {
		switch(super.getDirection()) {
			case NORTH:
				for (int i = 1; i <= 10; i++) {
					if (super.getLon() - i < 0) return false;
					if (super.getModel().getColor(super.getLat(), super.getLon() - i) == Color.MAGENTA ||
							super.getModel().getColor(super.getLat(), super.getLon() - i) == Color.YELLOW) 
						return true;
				}
				return false;
			case EAST:
				for (int i = 1; i <= 10; i++) {
					if (super.getLat() + i >= (super.getModel().getWidth())) return false;
					if (super.getModel().getColor(super.getLat() + i, super.getLon()) == Color.MAGENTA ||
							super.getModel().getColor(super.getLat() + i, super.getLon()) == Color.YELLOW)
						return true;
				}
				return false;
			case SOUTH:
				for (int i = 1; i <= 10; i++) {
					if (super.getLon() + i >= (super.getModel().getHeight() - 2)) return false;
					if (super.getModel().getColor(super.getLat(), super.getLon() + i) == Color.MAGENTA ||
							super.getModel().getColor(super.getLat(), super.getLon() + i) == Color.YELLOW)
						return true;
				}
				return false;
			case WEST:
				for (int i = 1; i <= 10; i++) {
					if (super.getLat() - i < 0) return false;
					if (super.getModel().getColor(super.getLat() - i, super.getLon()) == Color.MAGENTA || 
							super.getModel().getColor(super.getLat() - i, super.getLon()) == Color.YELLOW)
						return true;
				}
				return false;
		}
		return false;
	}
	
	private boolean walkValid() {
		switch(super.getDirection()) {
			case NORTH:
				if (super.getLon() - 1 < 0) return false;
				else if (super.getModel().getColor(super.getLat(), super.getLon() - 1) == Color.BLACK ||
						super.getModel().getColor(super.getLat(), super.getLon() - 1) == Color.BLUE) return true;
				else return false;
			case EAST:
				if (super.getLat() + 1 >= (super.getModel().getWidth())) return false;
				else if (super.getModel().getColor(super.getLat() + 1, super.getLon()) == Color.BLACK ||
						super.getModel().getColor(super.getLat() + 1, super.getLon()) == Color.BLUE) return true;
				else return false;
			case SOUTH:
				if (super.getLon() + 1 >= (super.getModel().getHeight() - 2)) return false;
				else if (super.getModel().getColor(super.getLat(), super.getLon() + 1) == Color.BLACK ||
						super.getModel().getColor(super.getLat(), super.getLon() + 1) == Color.BLUE) return true;
				else return false;
			case WEST:
				if (super.getLat() - 1 < 0) return false;
				else if (super.getModel().getColor(super.getLat() - 1, super.getLon()) == Color.BLACK ||
						super.getModel().getColor(super.getLat() - 1, super.getLon()) == Color.BLUE) return true;
				else return false;
		}
		return false; // shouldn't get here
	}
	
	private void walk() {
		switch(super.getDirection()) {
			case NORTH:
				super.getModel().setColor(super.getLat(), super.getLon(), prevColor);
				prevColor = super.getModel().getColor(super.getLat(), super.getLon() - 1);
				super.getModel().setColor(super.getLat(), super.getLon() - 1, color);
				super.setLon(super.getLon() - 1);
				break;
			case EAST:
				super.getModel().setColor(super.getLat(), super.getLon(), prevColor);
				prevColor = super.getModel().getColor(super.getLat() + 1, super.getLon());
				super.getModel().setColor(super.getLat() + 1, super.getLon(), color);
				super.setLat(super.getLat() + 1);
				break;
			case SOUTH:
				super.getModel().setColor(super.getLat(), super.getLon(), prevColor);
				prevColor = super.getModel().getColor(super.getLat(), super.getLon() + 1);
				super.getModel().setColor(super.getLat(), super.getLon() + 1, color);
				super.setLon(super.getLon() + 1);
				break;
			case WEST:
				super.getModel().setColor(super.getLat(), super.getLon(), prevColor);
				prevColor = super.getModel().getColor(super.getLat() - 1, super.getLon());
				super.getModel().setColor(super.getLat() - 1, super.getLon(), color);
				super.setLat(super.getLat() - 1);
				break;
		}
	}
	
	public Direction direction(int lat, int lon) {
    	// North hot spot
    	if (lat >= 30 && lat <= 50 && lon >= 0 && lon <= 20) return Direction.NORTH;
    	
    	// East hot spot
    	if (lat >= 60 && lat < super.getModel().getWidth() && lon >= 20 && lon <= 40) return Direction.EAST;
    	
    	// South hot spot
    	if (lat >= 30 && lat <= 50 && lon >= 40 && lon < (super.getModel().getHeight() - 1)) return Direction.SOUTH;
    	
    	// West hot spot
    	if (lat >= 0 && lat <= 20 && lon >= 20 && lon <= 40) return Direction.WEST;
    	
    	// return null if mouse didn't hit hot spot
    	return null;
    }
}
