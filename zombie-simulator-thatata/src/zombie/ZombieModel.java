package zombie;

import java.awt.Color;
import java.util.ArrayList;

public class ZombieModel {
	
	private int tempx;
	private int tempy;
	
	private final Color[][] matrix;
	private final int width;
	private final int height;
	private final int dotSize;
	
	private ArrayList<Human> humans;
	private ArrayList<Zombie> zombies;
	
	public ZombieModel(int widthArg, int heightArg, int dotSizeArg) {
		width = widthArg;
		height = heightArg;
		dotSize = dotSizeArg;
		matrix = new Color[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				matrix[i][j] = Color.BLACK;
			}
		}
	}
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getDotSize() { return dotSize; }
	public Color getColor(int x, int y) { return matrix[x][y]; }
	public void setColor(int x, int y, Color color) { matrix[x][y] = color; }
	
	public void initialize() {
		// Initialize river
		@SuppressWarnings("unused")
		River river = new River(this);
		
		// Initialize trees, rocks, and shields
		Tree[] trees = new Tree[20];
		Rock[] rocks = new Rock[6];
		Shield[] shields = new Shield[6];
		
		int i = 0;
		while (i < 6) {
			rocks[i] = new Rock(this);
			if (rocks[i].isSuccess()) {
				i++;
			}
		}
		i = 0;
		while (i < 20) {
			trees[i] = new Tree(this);
			if (trees[i].isSuccess()) {
				i++;
			}
		}	
		i = 0;
		while (i < 6) {
			shields[i] = new Shield(this);
			if (shields[i].isSuccess()) {
				i++;
			}
		}
		
		// Initialize humans
		humans = new ArrayList<Human>();
		i = 0;
		
		while (i < 30) {
			Human human = new Human(this);
			if (human.isSuccess()) {
				humans.add(human);
				i++;
			}
		}
		
		// Initialize zombies ArrayList
		zombies = new ArrayList<Zombie>();
			
		// Initialize Alpha Zombie
		Zombie alphazombie = new Zombie(this, true);
		while (!(alphazombie.isSuccess())) {
			alphazombie = new Zombie(this, true);
		}
		zombies.add(alphazombie);
	}
	
	public void update() {
		// First update all humans, increment shield counts where needed
		for (int i = 0; i < humans.size(); i++) {
			humans.get(i).update();
		}
		
		// Now, check if any humans turned into zombies, if so convert them
		for (int i = 0; i < humans.size(); i++) {
			if (humans.get(i).turnZombie()) {
				Zombie zombie = new Zombie(this, humans.get(i).getLat(), humans.get(i).getLon());
				while (!zombie.isSuccess()) {
					zombie = new Zombie(this, humans.get(i).getLat(), humans.get(i).getLon());
				}
				zombies.add(zombie);
				humans.remove(i);
			}
		}
		
		// Finally, update all zombies
		for (int i = 0; i < zombies.size(); i++) {
			zombies.get(i).update();
		}
	}

	public int getTempx() {
		return tempx;
	}

	public void setTempx(int tempx) {
		this.tempx = tempx;
	}

	public int getTempy() {
		return tempy;
	}

	public void setTempy(int tempy) {
		this.tempy = tempy;
	}
}
