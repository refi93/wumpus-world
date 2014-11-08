
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class World {
	enum State {EXCEPTION, HALTED, DEAD};
	
	public class Result{
		private State state;
		private int score;
		
		public Result(State state, int score){
			this.state = state;
			this.score = score;
		}
		
		public State getState(){
			return this.state;
		}
		
		public int getScore(){
			return this.score;
		}
	}

	private int w, h; // width and height of the world
	int[][] net;
	private Random r = new Random(new Date().getTime());
	MyAgent agent;
	int agentX;
	int agentY;
	int startX, startY; // starting/exiting coordinates
	int arrowX, arrowY;
	private int numberOfArrows;
	private boolean scream; // true iff agent's last action killed the wumpus;
	private boolean bumped; // true iff agent's last move was towards the wall
	private boolean climbed; // true iff agent climbed
	boolean killedWumpus;
	private boolean killedAgent;

	private int score;
	
	boolean[][] visited;

	private long wait;

	public Present presenter;

	public World(long wait) {
		this.wait = wait;
		initialize();
	}

	private void initialize() {
		bumped = false;
		scream = false;
		climbed = false;
		score = 0;
		killedAgent = false;
		killedWumpus = false;
		numberOfArrows = Constants.NUMBER_OF_ARROWS;
		destroyArrow();		
	}

	public int getScore() {
		return score;
	}
	
	public Point getWorldDimensions(){
		return new Point(w,h);
	}

	public boolean endOfLevel() {
		// level ends iff agent is killed or it has sucessfully climbed out
		return climbed || killedAgent;
	}

	public void loadWorld(String fileName) {
		try {						
			File f = new File(fileName);
			if (!f.exists()) {
				throw new IllegalArgumentException("File " + fileName
						+ " was not found.");
			}
			BufferedReader b = new BufferedReader(new FileReader(f));
			String s = b.readLine();
			if (s.equals("random")) {
				// create a random world

				s = b.readLine();
				w = Integer.parseInt(s);
				s = b.readLine();
				h = Integer.parseInt(s);
				
				visited = new boolean[h][w];
				
				for (int i = 0; i < visited.length; i++) {
					Arrays.fill(visited[i], false);			
				}

				// distributing walls, pits and golds in the world
				s = b.readLine();
				double wallsDistribution = Double.parseDouble(s);
				s = b.readLine();
				double pitsDistribution = Double.parseDouble(s);
				s = b.readLine();
				double goldDistribution = Double.parseDouble(s);
				net = new int[h][w];
				randomizeObjectsInWorld(wallsDistribution, pitsDistribution,
						goldDistribution);

				createAgent(r.nextInt(w - 2) + 1, r.nextInt(h - 2) + 1, r.nextInt(4));

				// creating a wumpus at random position
				int wumpusX, wumpusY;
				do {
					// make sure wumpus will not collide with agent
					wumpusX = r.nextInt(w - 2) + 1;
					wumpusY = r.nextInt(h - 2) + 1;
				} while (wumpusX == agentX && wumpusY == agentY);
				net[wumpusY][wumpusX] = Constants.WUM;

			} else {
				s = b.readLine();
				w = Integer.parseInt(s);
				s = b.readLine();
				h = Integer.parseInt(s);
				
				visited = new boolean[h][w];
				
				for (int i = 0; i < visited.length; i++) {
					Arrays.fill(visited[i], false);			
				}
				
				net = new int[h][w];
				for (int i = 0; i < h; i++) {
					s = b.readLine();
					for (int j = 0; j < s.length(); j++) {
						switch (s.charAt(j)) {
						case Constants.WALL_CHAR:
							net[i][j] = Constants.WALL;
							break;
						case Constants.PIT_CHAR:
							net[i][j] = Constants.PIT;
							break;
						case Constants.CLEAN_CHAR:
							net[i][j] = Constants.CLEAN;
							break;
						case Constants.GOLD_CHAR:
							net[i][j] = Constants.GOLD;
							break;
						case Constants.WUM_CHAR:
							net[i][j] = Constants.WUM;
							break;
						case Constants.AGENT_CHAR:
							createAgent(j, i, Constants.NORTH);
							break;
						}
					}
				}
			}
			b.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean flyingArrow() {
		boolean result = arrowX > -1;

		return result;
	}

	public Result run() {
		State state = State.HALTED;
		
		if (wait > 0 && presenter != null) {
			presenter.redraw();
		}
		
		while (!endOfLevel()) {
			try {
				if (flyingArrow()) {
					switch (agent.getOrientation()) {
					case Constants.NORTH:
						if (net[arrowY - 1][arrowX] != Constants.WALL) {
							arrowY += -1;
						} else {
							destroyArrow();
						}
						break;
					case Constants.EAST:
						if (net[arrowY][arrowX + 1] != Constants.WALL) {
							arrowX += 1;
						} else {
							destroyArrow();
						}
						break;
					case Constants.SOUTH:
						if (net[arrowY + 1][arrowX] != Constants.WALL) {
							arrowY += 1;
						} else {
							destroyArrow();
						}
						break;
					case Constants.WEST:
						if (net[arrowY][arrowX - 1] != Constants.WALL) {
							arrowX += -1;
						} else {
							destroyArrow();
						}
						break;
					}
				} else {
					try {
						int s = agent.getSteps();
						agent.act();
						if (agent.getSteps() - s > 1){
							throw new Exception("Agent performed more than one action in act.");									
						}
					} catch (Exception e) {
						state = State.EXCEPTION;
						e.printStackTrace();
						break;
					}
				}

				if (wait > 0 && presenter != null) {
					presenter.redraw();
					Thread.sleep(wait);
				}				

				if (flyingArrow()
						&& net[arrowY][arrowX] == Constants.WUM
						&& !killedWumpus) {
					destroyArrow();
					killWumpus();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		if (killedAgent){
			state = State.DEAD;
		}
		
		return new Result(state, score);
	}

	private void randomizeObjectsInWorld(double wallsDistribution,
			double pitsDistribution, double goldDistribution) {
		for (int i = 0; i < h; i++)
			for (int j = 0; j < w; j++) {
				double d = r.nextDouble();
				if (d < goldDistribution) {
					net[i][j] = Constants.GOLD;
				} else if (d < pitsDistribution) {
					net[i][j] = Constants.PIT;
				} else if (d < wallsDistribution) {
					net[i][j] = Constants.WALL;
				} else
					net[i][j] = Constants.CLEAN;
				if (i == 0 || j == 0 || i == h - 1 || j == w - 1) {
					net[i][j] = Constants.WALL;
				}
			}
	}

	private void createAgent(int x, int y, int orientation) {
		// creating an agent at random position with random orientation
		agentX = x;
		agentY = y;
		startX = x;
		startY = y;
		net[agentY][agentX] = Constants.CLEAN;
		agent = new MyAgent(orientation, w, h);
		agent.putInWorld(this);
		
		visited[y][x] = true;
	}

	public void moveFW() {
		System.out.println("moveFW");
		score += Constants.ACTION;
		switch (agent.getOrientation()) {
		case Constants.NORTH:
			if ((net[agentY-1][agentX] == Constants.WUM && killedWumpus) ||
					net[agentY - 1][agentX] == Constants.CLEAN
					|| net[agentY - 1][agentX] == Constants.GOLD) {
				agentY += -1;
			} else if (net[agentY - 1][agentX] == Constants.WALL) {
				visited[agentY-1][agentX] = true;
				bumped = true;
				System.out.println("bumped");
			} else if (net[agentY - 1][agentX] == Constants.PIT
					|| (net[agentY - 1][agentX] == Constants.WUM && !killedWumpus)) {
				agentY += -1;
				killAgent();
			}
			break;
		case Constants.EAST:
			if ((net[agentY][agentX + 1] == Constants.WUM && killedWumpus) ||
				net[agentY][agentX + 1] == Constants.CLEAN
					|| net[agentY][agentX + 1] == Constants.GOLD) {
				agentX += 1;
			} else if (net[agentY][agentX + 1] == Constants.WALL) {
				visited[agentY][agentX+1] = true;
				bumped = true;
				System.out.println("bumped");
			} else if (net[agentY][agentX + 1] == Constants.PIT
					|| (net[agentY][agentX + 1] == Constants.WUM && !killedWumpus)) {
				agentX += 1;
				killAgent();
			}
			break;
		case Constants.SOUTH:
			if ((net[agentY + 1][agentX] == Constants.WUM && killedWumpus) ||
				net[agentY + 1][agentX] == Constants.CLEAN
					|| net[agentY + 1][agentX] == Constants.GOLD) {
				agentY += 1;
			} else if (net[agentY + 1][agentX] == Constants.WALL) {
				bumped = true;
				visited[agentY+1][agentX] = true;
				System.out.println("bumped");
			} else if (net[agentY + 1][agentX] == Constants.PIT
					|| (net[agentY + 1][agentX] == Constants.WUM && !killedWumpus)) {
				agentY += 1;
				killAgent();
			}
			break;
		case Constants.WEST:
			if ((net[agentY][agentX - 1] == Constants.WUM && killedWumpus) ||
				net[agentY][agentX - 1] == Constants.CLEAN
					|| net[agentY][agentX - 1] == Constants.GOLD) {
				agentX += -1;
			} else if (net[agentY][agentX - 1] == Constants.WALL) {
				visited[agentY][agentX-1] = true;
				bumped = true;
				System.out.println("bumped");
			} else if (net[agentY][agentX - 1] == Constants.PIT
					|| (net[agentY][agentX - 1] == Constants.WUM && !killedWumpus)) {
				agentX += -1;
				killAgent();
			}
			break;
		}
		visited[agentY][agentX] = true;
	}

	private void killAgent() {
		System.out.println("agent is dead");
		killedAgent = true;
		score += Constants.AGENT_DIED;
		System.out.println("score: " + score);
	}	

	private void killWumpus() {
		System.out.println("you killed the wumpus!");
		killedWumpus = true;
		scream = true;
		score += Constants.KILLED_WUMPUS;
	}

	public void shoot() {
		System.out.println("shoot");
		if (numberOfArrows > 0) {
			arrowX = agentX;
			arrowY = agentY;
		}
		score += Constants.ACTION;
		numberOfArrows--;
	}

	private void destroyArrow() {
		arrowX = -1;
		arrowY = -1;
	}

	public void pickUp() {
		System.out.println("pick UP");
		if (net[agentY][agentX] == Constants.GOLD) {
			System.out.println("gold picked");
			net[agentY][agentX] = Constants.CLEAN;
			score += Constants.FOUND_GOLD;
		}
		score += Constants.ACTION;
	}

	public void climb() {
		if (agentX == startX && agentY == startY) {
			System.out.println("climbed out");
			climbed = true;
			System.out.println("score: " + score);
		}
		score += Constants.ACTION;
	}

	public void turnRIGHT() {
		System.out.println("turn right");
		score += Constants.ACTION;
	}

	public void turnLEFT() {
		System.out.println("turn left");
		score += Constants.ACTION;
	}

	public boolean[] getPercept() {
		boolean[] result = { false, false, false, false, false };

		result[Constants.BREEZE] = isAdjacent(Constants.PIT);
		result[Constants.STENCH] = isAdjacent(Constants.WUM);

		if (bumped) {
			result[Constants.BUMP] = true;
			bumped = false;
		}

		if (scream) {
			result[Constants.SCREAM] = true;
			scream = false;
		}

		result[Constants.GLITTER] = net[agentY][agentX] == Constants.GOLD;

		return result;
	}

	private boolean isAdjacent(int what) {
		// true iff what is horizontally/vertically adjacent to agent
		boolean result = net[agentY - 1][agentX] == what
				|| net[agentY + 1][agentX] == what
				|| net[agentY][agentX - 1] == what
				|| net[agentY][agentX + 1] == what;
		return result;
	}

	public int getHeight() {
		return net.length;
	}

	public int getWidth() {
		return net[0].length;
	}

}
