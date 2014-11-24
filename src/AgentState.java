import java.util.HashMap;
import java.util.Map;


public class AgentState {
	int orientation;
	Position pos;
	GlobalPerceptMap map;
	String prevAction;
	AgentState prevState; // goal state
	Position dest;
	int dist;
	
	Map<Integer, Integer> dir_r, dir_c;
	
	public AgentState(Position pos, int orientation, GlobalPerceptMap map, AgentState prevState, String prevAction, int dist, Position dest){
		this.pos = pos;
		this.orientation = orientation;
		this.map = map;
		this.prevAction = prevAction;
		this.prevState = prevState;
		this.dest = dest;
		
		dir_r = new HashMap<Integer, Integer>();
		dir_c = new HashMap<Integer, Integer>();
		
		dir_r.put(World.NORTH, -1);
		dir_r.put(World.SOUTH, 1);
		dir_r.put(World.EAST, 0);
		dir_r.put(World.WEST, 0);
		
		dir_c.put(World.NORTH, 0);
		dir_c.put(World.SOUTH, 0);
		dir_c.put(World.EAST, 1);
		dir_c.put(World.WEST, -1);
		
		this.dist = dist;
	}
	
	public AgentState rotateLEFT(){
		return new AgentState(this.pos, (orientation - 1 + 4) % 4, map, this, "rotateLEFT", this.dist + 1, this.dest);
	}
	
	public AgentState rotateRIGHT(){
		return new AgentState(this.pos, (orientation + 1) % 4, map, this, "rotateRIGHT", this.dist + 1, this.dest);
	}
	
	public boolean canFW(){
		int tile = map.getMap()[pos.r + dir_r.get(orientation)][pos.c + dir_c.get(orientation)];
		return (tile != World.WALL);
	}
	
	public AgentState getFW(){
		if (!canFW()) return null;
		return new AgentState(new Position(pos.r + dir_r.get(orientation), pos.c + dir_c.get(orientation)), orientation, map, this, "forward", this.dist + 1, this.dest);
	}
	
	// returns value for comparation in heuristics
	public int compVal(){
		return this.dist + Math.abs(this.pos.r - dest.r) + Math.abs(this.pos.c - dest.c);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		AgentState s = (AgentState)obj;
		return ((s.pos.equals(this.pos)) && (s.orientation == this.orientation));
	}
	 
	@Override
	public int hashCode(){
		final int prime = 47;
		int result = 1;
		result = prime * result + this.pos.hashCode();
		result = prime * result + this.orientation;
		return result;
	}
}
