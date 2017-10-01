package zombie;

import java.awt.Color;
import java.util.Random;

public class Human {
	private ZombieModel model;
    private int lat = 0;
    private int lon = 0;
    private Direction mDirection;    
    
    private boolean seeZombie = false;
    private boolean isResting = false;
    private boolean hasShield = false;
    private boolean turnZombie = false;
    
    private int runCount = 0;
    private int restCount = 0;
    private int shieldCount = 0;
    
    private Color color = Color.MAGENTA;
    private boolean drawSuccess = false;

    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    public Human(ZombieModel mModel) {
        model = mModel;

        Random random = new Random();
        lat = random.nextInt(model.getWidth());
        lon = random.nextInt(model.getHeight());
        
        while(!drawSuccess) {
            if (checkLocation(lat, lon)) {
                model.setColor(lat, lon, color);
                drawSuccess = true;
                mDirection = Direction.NORTH; // travel north by default
            } else {
                drawSuccess = false;
                break;
            }
        }
    }
    // Zombie superclass constructor (color correctly)
    public Human(ZombieModel mModel, boolean alpha, int lat, int lon) {
    	model = mModel;
    	
    	Color color;
    	if (alpha) color = Color.RED;
    	else color = Color.ORANGE;
 
    	while (!drawSuccess) {
    		if (alpha) {
    			Random random = new Random();
    	        lat = random.nextInt(model.getWidth());
    	        lon = random.nextInt(model.getHeight());
    	        if (checkLocation(lat, lon)) {
                    model.setColor(lat, lon, color);
                    this.lat = lat;
                    this.lon = lon;
                    drawSuccess = true;
                }
    		}
    		else {
    			this.lat = lat;
    	        this.lon = lon;
    	        model.setColor(lat, lon, color);
    	        drawSuccess = true;
    		}
    	}
        mDirection = Direction.NORTH;
    }
    
    public void update() {
    	//===========================================================================
    	// Check squares adjacent to human to see if near zombie (or alpha zombie).
    	//
    	// If zombie is adjacent to human, human becomes a zombie.
    	//
    	//===========================================================================
    	
    	// Test for normal or alpha zombie
    	if (adjacentToZombie() && !hasShield) {
			turnZombie = true;
			return;
    	}
    	//===========================================================================
    	// Check if a human is adjacent to shield, if so:
    	//
    	// 1. Change direction to the direction corresponding to shield adjacency.
    	//
    	// 2. Set hasShield to true.
    	//
    	// 3. Set color to pink.
    	//
    	// 4. Become shielded for 10 turns.
    	//
    	//===========================================================================
    	
    	// Test for shield
    	if (adjacentToShield()) {
    		hasShield = true;
    		color = Color.PINK;
    		takeShield(); // direction set in adjacentToShield(), so take shield
    		walk(); // shield will be cleared, allowing for walk into shield's space
    		shieldCount += 10; // add 10 to shield count, for 10 turns
    		return;
    	}
    	
    	if (hasShield && shieldCount > 0) {
    		shieldCount--;
    	}
    	
    	if (hasShield && shieldCount == 0) {
    		hasShield = false;
    		color = Color.MAGENTA;
    	}
    	
    	//===========================================================================
    	// Check if human has seen a zombie, if so:
    	//
    	// 1. If human hasn't run for 3 turns, keep running
    	//
    	// 2. If human has run for 3 turns, rest for 2 turns.
    	//
    	//===========================================================================

    	if (!hasShield && seeZombie && runCount < 3) {
    		run();
    		runCount++;
    		return;
    	}
    	else if (!hasShield && seeZombie && runCount == 3) {
    		seeZombie = false;
    		runCount = 0;
    		isResting = true;
    	}
    	
    	//===========================================================================
    	// Check if human is resting. If so:
    	//
    	// 1. If human hasn't rested for 2 turns, rest.
    	//
    	// 2. If human has rested for 2 turns, go back to normal movement.
    	//
    	//===========================================================================

    	if (isResting && restCount < 2) {
    		restCount++;
    		return;
    	}
    	else if (isResting && restCount == 2) {
    		isResting = false;
    		restCount = 0;
    	}
    	
    	//===========================================================================
    	// NORMAL HUMAN MOVEMENT
    	//
    	// STEP ONE: determine the direction that the human should move
    	//
    	// Determine the direction that the human will travel during walk.
    	//
    	// Probability of same direction 75%, left turn 10%, right turn 10%, 
    	// 5% turn around.
    	//===========================================================================
    	
    	// Generate random number between 0 and 19, for probability of turning
    	Random random = new Random();
    	int probability = random.nextInt(20);
    	
    	// 0-14 (75%) - same direction (don't change mDirection)
    	
    	// 15-16 (10%) - turn left
    	if (probability >= 15 && probability < 17) turnLeft();
    	
    	// 17-18 (10%) - turn right
    	if (probability >= 17 && probability < 19) turnRight();
    	
    	// 19 (5%) - turn around
    	if (probability == 19) turnAround();
    	
    	//===========================================================================
    	// NORMAL HUMAN MOVEMENT
    	//
    	// STEP TWO: check surroundings of the map
    	//
    	// Check surroundings of the map, in mDirection
    	//
    	// 1. If human sees a zombie within 10 squares in mDirection, turn in
    	// 	  opposite direction and run for 3 turns
    	//
    	// 2. If the square in mDirection isn't off the map or an obstacle,
    	// 	  human walks one square in that direction.
    	//
    	//===========================================================================
    	
    	// Check direction (1) to look for zombie
    	if (!hasShield && seeZombie()) {
    		seeZombie = true;
    		turnAround();
    	}
    	
    	// If human sees a zombie, run in the opposite direction
    	if (seeZombie) {
    		run();
    		runCount++;
    		return;
    	}
    	
    	// Check direction (2) to see if human will hit an obstacle or walk off the map
    	if (walkValid()) {
    		walk();
    	}
    	else {
    		turnAround();
    	}    	
    }
    
    protected void turnLeft() {
    	switch(mDirection) {
			case NORTH: 
				mDirection = Direction.WEST;
				break;
			case EAST:
				mDirection = Direction.NORTH;
				break;
			case SOUTH:
				mDirection = Direction.EAST;
				break;
			case WEST:
				mDirection = Direction.SOUTH;
				break;
		}
    }
    
    protected void turnRight() {
    	switch(mDirection) {
			case NORTH: 
				mDirection = Direction.EAST;
				break;
			case EAST:
				mDirection = Direction.SOUTH;
				break;
			case SOUTH:
				mDirection = Direction.WEST;
				break;
			case WEST:
				mDirection = Direction.NORTH;
				break;
		}
    }
    
    protected void turnAround() {
    	switch(mDirection) {
			case NORTH: 
				mDirection = Direction.SOUTH;
				break;
			case EAST:
				mDirection = Direction.WEST;
				break;
			case SOUTH:
				mDirection = Direction.NORTH;
				break;
			case WEST:
				mDirection = Direction.EAST;
				break;
		}
    }
    
    private boolean seeZombie() {
    	switch(mDirection) {
			case NORTH:
				for (int i = 1; i <= 10; i++) {
					if (lon - i < 0) return false;
					if (model.getColor(lat, lon - i) == Color.ORANGE ||
							model.getColor(lat, lon - i) == Color.RED) return true;
				}
				break;
			case EAST:
				for (int i = 1; i <= 10; i++) {
					if (lat + i >= (model.getWidth())) return false;
					if (model.getColor(lat + i, lon) == Color.ORANGE ||
							model.getColor(lat + i, lon) == Color.RED) return true;
				}
				break;
			case SOUTH:
				for (int i = 1; i <= 10; i++) {
					if (lon + i >= (model.getHeight() - 2)) return false;
					if (model.getColor(lat, lon + i) == Color.ORANGE ||
							model.getColor(lat, lon + i) == Color.RED) return true;
				}
				break;
			case WEST:
				for (int i = 1; i <= 10; i++) {
					if (lat - i < 0) return false;
					if (model.getColor(lat - i, lon) == Color.ORANGE) return true;
				}
				break;
		}
    	return false;
    }
    
    private boolean walkValid() {
    	switch(mDirection) {
			case NORTH:
				if (lon - 1 < 0) return false;
				else if (!(model.getColor(lat, lon - 1) == Color.BLACK)) return false;
				else return true;
			case EAST:
				if (lat + 1 >= (model.getWidth())) return false;
				else if (!(model.getColor(lat + 1, lon) == Color.BLACK)) return false;
				else return true;
			case SOUTH:
				if (lon + 1 >= (model.getHeight() - 2)) return false;
				else if (!(model.getColor(lat, lon + 1) == Color.BLACK)) return false;
				else return true;
			case WEST:
				if (lat - 1 < 0) return false;
				else if (!(model.getColor(lat - 1, lon) == Color.BLACK)) return false;
				else return true;
		}
    	return false; // shouldn't get here
    }
    
    private void walk() {
    	switch(getDirection()) {
			case NORTH:
				model.setColor(getLat(), getLon(), Color.BLACK);
				model.setColor(getLat(), getLon() - 1, color);
				setLon(getLon() - 1);
				break;
			case EAST:
				model.setColor(getLat(), getLon(), Color.BLACK);
				model.setColor(getLat() + 1, getLon(), color);
				setLat(getLat() + 1);
				break;
			case SOUTH:
				model.setColor(getLat(), getLon(), Color.BLACK);
				model.setColor(getLat(), getLon() + 1, color);
				setLon(getLon() + 1);
				break;
			case WEST:
				model.setColor(getLat(), getLon(), Color.BLACK);
				model.setColor(getLat() - 1, getLon(), color);
				setLat(getLat() - 1);
				break;
		}
    }
    
    private void run() {
    	switch(getDirection()) {
    		case NORTH:
    			if (getLon() - 2 < 0) avoidObstacle();
    			else if (!(model.getColor(getLat(), getLon() - 1) == Color.BLACK) || 
    					!(model.getColor(getLat(), getLon() - 2) == Color.BLACK)) 
    				avoidObstacle();  
    			else {
    				model.setColor(getLat(), getLon(), Color.BLACK);
    				model.setColor(getLat(), getLon() - 2, Color.YELLOW);
    				setLon(getLon() - 2);
    			}
    			break;
    		case EAST:
    			if (getLat() + 2 >= (model.getWidth())) avoidObstacle();
    			else if (!(model.getColor(getLat() + 1, getLon()) == Color.BLACK) || 
    					!(model.getColor(getLat() + 2, getLon()) == Color.BLACK)) 
    				avoidObstacle();  
    			else {
    				model.setColor(getLat(), getLon(), Color.BLACK);
    				model.setColor(getLat() + 2, getLon(), Color.YELLOW);
    				setLat(getLat() + 2);
    			}
    			break;
    		case SOUTH:
    			if (getLon() + 2 >= (model.getHeight() - 2)) avoidObstacle();
    			else if (!(model.getColor(getLat(), getLon() + 1) == Color.BLACK) || 
    					!(model.getColor(getLat(), getLon() + 2) == Color.BLACK)) 
    				avoidObstacle();  
    			else {
    				model.setColor(getLat(), getLon(), Color.BLACK);
    				model.setColor(getLat(), getLon() + 2, Color.YELLOW);
    				setLon(getLon() + 2);
    			}
    			break;
    		case WEST:
    			if (getLat() - 2 < 0) avoidObstacle();
    			else if (!(model.getColor(getLat() - 1, getLon()) == Color.BLACK) ||
    					!(model.getColor(getLat() - 2, getLon()) == Color.BLACK)) 
    				avoidObstacle();  
    			else {
    				model.setColor(getLat(), getLon(), Color.BLACK);
    				model.setColor(getLat() - 2, getLon(), Color.YELLOW);
    				setLat(getLat() - 2);
    			}
    			break;
    	}
    }
    
    private void avoidObstacle() {
    	Random random = new Random();
    	int probability = random.nextInt(10);
    	// 0-3 - turn left and run
    	if (probability <= 3) {
    		turnLeft();
    		run();
    	}
    	// 4-7 - turn right and run
    	else if (probability <= 7) {
    		turnRight();
    		run();
    	}
    	// 8 - turn around
    	else if (probability == 8) {
    		turnAround();
    		run();
    	}
    	// 9 - freeze (do nothing)
    }
    
    public boolean turnZombie() {
    	return turnZombie;
    }
    
    public void setDirection(Direction direction) {
    	mDirection = direction;
    }
    
    public Direction getDirection() {
    	return mDirection;
    }
    
    public Color getColor() {
    	return color;
    }
    
    public void setColor(Color color) {
    	this.color = color;
    }
    
    public void setLat(int lat) {
    	this.lat = lat;
    }
    
    public void setLon(int lon) {
    	this.lon = lon;
    }
    public int getLat() {
		return lat;
	}
	public int getLon() {
		return lon;
	}
	public boolean checkLocation(int lat, int lon) {
    	if (lat <= 0 || lon <= 0) return false;
    	
    	if (lat >= (model.getWidth()) || lon >= (model.getHeight() - 2)) return false;
    	
    	if (model.getColor(lat, lon) != Color.BLACK) return false;
    	
    	return true;
    }
    public boolean isSuccess() {
    	return drawSuccess;
    }
    
    protected ZombieModel getModel() {
    	return model;
    }
    
    private boolean adjacentToZombie() {
    	if (lat + 1 < (model.getWidth())) {
    		if (model.getColor(lat + 1, lon) == Color.ORANGE || model.getColor(lat + 1, lon) == Color.RED) {
    			return true;
    		}
    	}
    	if (lat - 1 >= 0) {
    		if (model.getColor(lat - 1, lon) == Color.ORANGE || model.getColor(lat - 1, lon) == Color.RED) {
    			return true;
    		}
    	}
    	if (lon - 1 >= 0) {
    		if (model.getColor(lat, lon - 1) == Color.ORANGE || model.getColor(lat, lon - 1) == Color.RED) {
    			return true;
    		}
    	}
    	if (lon + 1 < (model.getHeight() - 2)) {
    		if (model.getColor(lat, lon + 1) == Color.ORANGE || model.getColor(lat, lon + 1) == Color.RED) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean adjacentToShield() {
    	if ((lon - 1) >= 0 && model.getColor(lat, lon - 1) == Color.CYAN) {
    		setDirection(Direction.NORTH);
    		return true;
    	}
    	else if ((lat + 1) < model.getWidth() && model.getColor(lat + 1, lon) == Color.CYAN) {
    		setDirection(Direction.EAST);
    		return true;
    	}
    	else if ((lon + 1) < (model.getHeight() - 1) && model.getColor(lat, lon + 1) == Color.CYAN) {
    		setDirection(Direction.SOUTH);
    		return true;
    	}
    	else if ((lat - 1) >= 0 && model.getColor(lat - 1, lon) == Color.CYAN) {
    		setDirection(Direction.WEST);
    		return true;
    	}
    	else return false;
    }
    
    private void takeShield() {
    	switch(getDirection()) {
	    	case NORTH:
	    		for (int i = 0; i < 3; i++) {
	    			for (int j = 0; j < 2; j++) {
	    				if ((lat - 1) < 0) continue;
	    				if ((lon - 2) < 0) continue;
	    				if ((lat + 1) >= model.getWidth()) continue;
	    				if (model.getColor((lat - 1 + i), (lon - 2 + j)) == Color.CYAN) {
	    					model.setColor((lat - 1 + i), (lon - 2 + j), Color.BLACK);
	    				}
	    			}
	    		}
	    		break;
	    	case EAST:
	    		for (int i = 0; i < 2; i++) {
	    			for (int j = 0; j < 3; j++) {
	    				if ((lat + 1) >= model.getWidth()) continue;
	    				if ((lon - 1) < 0) continue;
	    				if ((lon + 1) >= (model.getHeight() - 1)) continue; 
	    				if (model.getColor((lat + 1 + i), (lon - 1 + j)) == Color.CYAN) {
	    					model.setColor((lat + 1 + i), (lon - 1 + j), Color.BLACK);
	    				}
	    			}
	    		}
	    		break;
	    	case SOUTH:
	    		for (int i = 0; i < 3; i++) {
	    			for (int j = 0; j < 2; j++) {
	    				if ((lat - 1) < 0) continue;
	    				if ((lat + 1) >= model.getWidth()) continue;
	    				if ((lon + 1) >= (model.getHeight() - 1)) continue;
	    				if (model.getColor((lat - 1 + i), (lon + 1 + j)) == Color.CYAN) {
	    					model.setColor((lat - 1 + i), (lon + 1 + j), Color.BLACK);
	    				}
	    			}
	    		}
	    		break;
	    	case WEST:
	    		for (int i = 0; i < 2; i++) {
	    			for (int j = 0; j < 3; j++) {
	    				if ((lat - 2) < 0) continue;
	    				if ((lon - 1) < 0) continue;
	    				if ((lon + 1) >= (model.getHeight() - 1)) continue;
	    				if (model.getColor((lat - 2 + i), (lon - 1 + j)) == Color.CYAN) {
	    					model.setColor((lat - 2 + i), (lon - 1 + j), Color.BLACK);
	    				}
	    			}
	    		}
	    		break;
    	}
    }
    
    public void setShieldCount(int shieldCount) {
    	this.shieldCount = shieldCount;
    }
    
    public int getShieldCount() {
    	return this.shieldCount;
    }
    
    public boolean hasShield() {
    	return this.hasShield;
    }
    
    public void removeShield() {
    	this.hasShield = false;
    	color = Color.MAGENTA;
    }
    
    public void incrementShieldCount() {
    	shieldCount++;
    }
}
