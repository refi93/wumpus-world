import java.util.HashMap;
import java.util.Map;


public class AgentState {
	int orientation;
	Position pos;
	KnowledgeBase kb;
	String prevAction;
	AgentState prevState; // goal state
	Position dest;
	int dist;
	
	Map<Integer, Integer> dir_r, dir_c;
	
	public AgentState(Position pos, int orientation, KnowledgeBase kb, AgentState prevState, String prevAction, int dist, Position dest){
		this.pos = pos;
		this.orientation = orientation;
		this.kb = kb;
		this.prevAction = prevAction;
		this.prevState = prevState;
		this.dest = dest;
		
		dir_r = new HashMap<Integer, Integer>();
		dir_c = new HashMap<Integer, Integer>();
		
		dir_r.put(Constants.NORTH, -1);
		dir_r.put(Constants.SOUTH, 1);
		dir_r.put(Constants.EAST, 0);
		dir_r.put(Constants.WEST, 0);
		
		dir_c.put(Constants.NORTH, 0);
		dir_c.put(Constants.SOUTH, 0);
		dir_c.put(Constants.EAST, 1);
		dir_c.put(Constants.WEST, -1);
		
		this.dist = dist;
	}
	
	public AgentState rotateLEFT(){
		return new AgentState(this.pos, (orientation - 1 + 4) % 4, kb, this, "rotateLEFT", this.dist + 1, this.dest);
	}
	
	public AgentState rotateRIGHT(){
		return new AgentState(this.pos, (orientation + 1) % 4, kb, this, "rotateRIGHT", this.dist + 1, this.dest);
	}
	
	
	public boolean canFW(){
		if (kb.ask(new ParameterLiteral(pos.r + dir_r.get(orientation), pos.c + dir_c.get(orientation), "wall", true))){
			return false;
		}
		
		return (kb.ask(new ParameterLiteral(pos.r + dir_r.get(orientation), pos.c + dir_c.get(orientation), "safe", true)));
	}
	
	public boolean cannotFW(){
		return (kb.ask(new ParameterLiteral(pos.r + dir_r.get(orientation), pos.c + dir_c.get(orientation), "pit", true))) ||
				(kb.ask(new ParameterLiteral(pos.r + dir_r.get(orientation), pos.c + dir_c.get(orientation), "wumpus", true))) ||
				(kb.ask(new ParameterLiteral(pos.r + dir_r.get(orientation), pos.c + dir_c.get(orientation), "wall", true)));
	}
	
	public AgentState getFW(){
		if (!canFW()) return null;
		return new AgentState(new Position(pos.r + dir_r.get(orientation), pos.c + dir_c.get(orientation)), orientation, kb, this, "forward", this.dist + 1, this.dest);
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
