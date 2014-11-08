
public abstract class Agent {
	private int steps; // number of executed actions
	private int orientation;
	private World world;
	
	
	public Agent(int orientation){
		this.orientation = orientation;
		steps = 0;
	}
	
	public int getOrientation(){
		return orientation;
	}
	
	public int getSteps(){
		return steps;
	}
	
	public void putInWorld(World world){
		this.world = world;
	}
	
	public boolean[] getPercept(){
		return world.getPercept();
	}
	
	public abstract void act();
	
	//-----------Agent's actions----------------
	
	public void moveFW(){
		world.moveFW();
		steps++;
	}	
	
	public void turnRIGHT(){
		world.turnRIGHT();
		orientation = (orientation + 1) % 4; 
		steps++;		
	}	

	public void turnLEFT(){
		world.turnLEFT();		
		orientation = (orientation + 3) % 4;		
		steps++;		
	}
	
	public void shoot(){
		world.shoot();
		steps++;
	}
	
	public void pickUp(){
		world.pickUp();
		steps++;		
	}
	
	public void climb(){
		world.climb();
		steps++;		
	}	

}
