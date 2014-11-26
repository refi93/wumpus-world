import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public  class MyAgent extends Agent{	
	
	//-------------Percept types------------------------
    static final int BREEZE = 0;
    static final int STENCH = 1;
    static final int BUMP = 2;
    static final int SCREAM = 3;
    static final int GLITTER = 4;    
    
	//-------------Cardinal directions------------------
    static final int NORTH = 0;
    static final int EAST = 1;
    static final int SOUTH = 2;
    static final int WEST = 3;    
	
	private boolean[] percept = new boolean[5];
	
	AgentState myState;
	Deque<String> actionStack;
	KnowledgeBase kb;
	
	HashMap<Integer, Integer> dir_r, dir_c; // vektory smerov vzhladom na svetove strany
	int rotation_count;
	boolean goldGrabbed;
	boolean[][] visited;
		
	public MyAgent(int orientation, int w, int h) {
		
		super(orientation);		
		
		visited = new boolean[300][300];
		
		for (int r = 0; r < 300; r++){
			for (int c = 0; c < 300; c++){
				visited[r][c] = false;
			}
		}
		
		actionStack = new ArrayDeque<String>();
		goldGrabbed = false;
		
		dir_r = new HashMap<Integer, Integer>();
		dir_c = new HashMap<Integer, Integer>();
		
		dir_r.put(NORTH, -1);
		dir_r.put(SOUTH, 1);
		dir_r.put(EAST, 0);
		dir_r.put(WEST, 0);
		
		dir_c.put(NORTH, 0);
		dir_c.put(SOUTH, 0);
		dir_c.put(EAST, 1);
		dir_c.put(WEST, -1);	
		
		createKB();
		
		this.myState = 
				new AgentState(
						new Position(h, w), // current position (global)
						orientation, // current orientation
						kb, // map of World
						null, // previous state
						null, // previous action
						0, // distance from start (in number of actions)
						new Position(-1, -1) // destination - we have none
				);
		
	}	
	
	private void createKB() {
		HashSet<Formula> axioms = new HashSet<Formula>();
		
		//=====================================================================
		//                          MODIFY HERE 
		//=====================================================================	
		
		// if a tile has not stenching neighbour, then it is certainly not a wumpus
		axioms.add(new Implication(new ParameterLiteral("noWumpus", true),
				new MatchingLiteral(1, 0, "noStench", true)));
		
		axioms.add(new Implication(new ParameterLiteral("noWumpus", true),
				new MatchingLiteral(-1, 0, "noStench", true)));
		
		axioms.add(new Implication(new ParameterLiteral("noWumpus", true),
				new MatchingLiteral(0, 1, "noStench", true)));
		
		axioms.add(new Implication(new ParameterLiteral("noWumpus", true),
				new MatchingLiteral(0, -1, "noStench", true)));
		
		// same for noPit and no breeze
		axioms.add(new Implication(new ParameterLiteral("noPit", true),
				new MatchingLiteral(1, 0, "noBreeze", true)));
		
		axioms.add(new Implication(new ParameterLiteral("noPit", true),
				new MatchingLiteral(-1, 0, "noBreeze", true)));
		
		axioms.add(new Implication(new ParameterLiteral("noPit", true),
				new MatchingLiteral(0, 1, "noBreeze", true)));
		
		axioms.add(new Implication(new ParameterLiteral("noPit", true),
				new MatchingLiteral(0, -1, "noBreeze", true)));
		
		
		// ak niektora dvojica protilahlych policok smrdi, tak medzi nimi je wumpus
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(0, 1, "stench", true), new MatchingLiteral(0, -1, "stench", true)));
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(0, 1, "stench", true), new MatchingLiteral(0, -1, "stench", true)));
		
		// pripad po uhlopriecke pre wumpusa
		// severozapad
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(0, -1, "stench", true), new MatchingLiteral(-1, 0, "stench", true), new MatchingLiteral(-1, -1, "noWumpus", true)));
				
		//severovychod
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(-1, 0, "stench", true), new MatchingLiteral(0, 1, "stench", true), new MatchingLiteral(-1, 1, "noWumpus", true)));
		
		//juhovychod
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(0, 1, "stench", true), new MatchingLiteral(1, 0, "stench", true), new MatchingLiteral(1, 1, "noWumpus", true)));
		
		//juhozapad
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(0, -1, "stench", true), new MatchingLiteral(1, 0, "stench", true), new MatchingLiteral(1, -1, "noWumpus", true)));
				
		
		// if a tile is neighbour of breeze and over that tile is no pit and the 2 tiles next to it also, our tile is certainly a pit
		// breeze na vychod
		axioms.add(new Implication(new ParameterLiteral("pit", true),
				new MatchingLiteral(0, 1, "breeze", true), new MatchingLiteral(-1, 1, "noPit", true), new MatchingLiteral(1, 1, "noPit", true), new MatchingLiteral(0, 2, "noPit", true)));
		
		// breeze na juh
		axioms.add(new Implication(new ParameterLiteral("pit", true),
				new MatchingLiteral(1, 0, "breeze", true), new MatchingLiteral(1, 1, "noPit", true), new MatchingLiteral(1, -1, "noPit", true), new MatchingLiteral(2, 0, "noPit", true)));

		// breeze na zapad
		axioms.add(new Implication(new ParameterLiteral("pit", true),
				new MatchingLiteral(0, -1, "breeze", true), new MatchingLiteral(1, -1, "noPit", true), new MatchingLiteral(-1, -1, "noPit", true), new MatchingLiteral(0, -2, "noPit", true)));

		// breeze na sever
		axioms.add(new Implication(new ParameterLiteral("pit", true),
				new MatchingLiteral(-1, 0, "breeze", true), new MatchingLiteral(-1, -1, "noPit", true), new MatchingLiteral(-1, 1, "noPit", true), new MatchingLiteral(-2, 0, "noPit", true)));

		// same for wumpus and stench
		// if a tile is neighbour of stench and over that tile is no wumpus and the 2 tiles next to it also, our tile is certainly a wumpus
		
		// stench na vychod
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(0, 1, "stench", true), new MatchingLiteral(-1, 1, "noWumpus", true), new MatchingLiteral(1, 1, "noWumpus", true), new MatchingLiteral(0, 2, "noWumpus", true)));
		
		// stench na juh
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(1, 0, "stench", true), new MatchingLiteral(1, 1, "noWumpus", true), new MatchingLiteral(1, -1, "noWumpus", true), new MatchingLiteral(2, 0, "noWumpus", true)));

		// stench na zapad
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(0, -1, "stench", true), new MatchingLiteral(-1, -1, "noWumpus", true), new MatchingLiteral(1, -1, "noWumpus", true), new MatchingLiteral(0, -2, "noWumpus", true)));

		// stench na sever
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(-1, 0, "stench", true), new MatchingLiteral(-1, -1, "noWumpus", true), new MatchingLiteral(-1, 1, "noWumpus", true), new MatchingLiteral(-2, 0, "noWumpus", true)));

		
		
		// when a tile is safe - there is no pit and either wumpus is not present or it is dead
		axioms.add(new Implication(new ParameterLiteral("safe", true),
				new MatchingLiteral(0, 0, "noPit", true), new MatchingLiteral(0, 0, "noWumpus", true)));
		axioms.add(new Implication(new ParameterLiteral("safe", true),
				new MatchingLiteral(0, 0, "noPit", true), new Literal("wumpusDead", true)));
		/*
		// if the last movement resulted in bump, the tile, on which we think we are, is a wall
		axioms.add(new Implication(new ParameterLiteral("wall", true),
				new MatchingLiteral(0, 0, "bump", true)));*/
		
		// not safe case
		axioms.add(new Implication(new ParameterLiteral("notSafe", true),
				new MatchingLiteral(0, 0, "wumpus", true)));
		
		axioms.add(new Implication(new ParameterLiteral("notSafe", true),
				new MatchingLiteral(0, 0, "pit", true)));
		
		
		
		//=====================================================================
		//                      STOP MODIFYING HERE 
		//=====================================================================		
		
		kb = new KnowledgeBase(axioms);		
	}

	public void act() {
//		System.out.println(kb.toString());
		percept = getPercept();
		writePercept();
		doAction();		
	}
			
	private void writePercept(){
		ArrayList<Formula> a = new ArrayList<Formula>();
		
		visited[myState.pos.r][myState.pos.c] = true;
		//=====================================================================
		//                          MODIFY HERE 
		//=====================================================================				
		
		// ked sme na policko uspesne stupili, tak urcite tam neni wumpus ani pit
		a.add(new ParameterLiteral(myState.pos.r, myState.pos.c, "noWumpus", true));
		a.add(new ParameterLiteral(myState.pos.r, myState.pos.c, "noPit", true));
		
		
		
		if (percept[BUMP]){
			a.add(new ParameterLiteral(myState.pos.r, myState.pos.c, "wall", true));
			myState.pos.r = myState.pos.r - dir_r.get(myState.orientation);
			myState.pos.c = myState.pos.c - dir_c.get(myState.orientation);
		}
		if (percept[BREEZE]){
			a.add(new ParameterLiteral(myState.pos.r, myState.pos.c, "breeze", true));
		}
		else{
			a.add(new ParameterLiteral(myState.pos.r, myState.pos.c, "noBreeze", true));
		}
		if (percept[STENCH]){
			a.add(new ParameterLiteral(myState.pos.r, myState.pos.c, "stench", true));
		}
		else{
			a.add(new ParameterLiteral(myState.pos.r, myState.pos.c, "noStench", true));
		}
		
		//=====================================================================
		//                      STOP MODIFYING HERE 
		//=====================================================================		
											
		kb.tell(a);		
	}
	
	public boolean isSafe(int r, int c){
		if (kb.ask(new ParameterLiteral(r, c, "wall", true))){
			return false;
		}
		
		return (kb.ask(new ParameterLiteral(r, c, "safe", true)));
	}
	
	public boolean isNotSafe(int r, int c){
		return (kb.ask(new ParameterLiteral(r, c, "pit", true))) ||
				(kb.ask(new ParameterLiteral(r, c, "wumpus", true))) ||
				(kb.ask(new ParameterLiteral(r, c, "wall", true)));
	}
	
	public boolean isUnknown(int r, int c){
		boolean ret = !isSafe(r, c) && !isNotSafe(r, c);
		return ret;
	}
	
	// find nearest not visited safe or at least unknown tile using BFS
	private AgentState BFS(KnowledgeBase kb, AgentState initState, String goal){
		initState.dist = 0;
		if (kb.ask(new ParameterLiteral(initState.pos.r, initState.pos.c, "wall", true))) return null;
		
		
		Queue open = new LinkedList<AgentState>();
		open.add(initState); // states to visit
		
		Set<AgentState> close = new HashSet<AgentState>(); // visited states
		
		int counter = 0;
		while (!open.isEmpty()){
			AgentState curState = (AgentState) open.remove();
			if (curState.pos.equals(curState.dest)) {
				return curState;
			}
			else if (!visited[curState.pos.r][curState.pos.c]){ 
				if (goal.equals("safe")){ // so we don't have specific destination
					if (kb.ask(new ParameterLiteral(curState.pos.r, curState.pos.c, "safe", true)) &&
						!kb.ask(new ParameterLiteral(curState.pos.r, curState.pos.c, "wall", true))){
						return curState;
					}
				}
				else if (goal.equals("unknown")){
					if (isUnknown(curState.pos.r, curState.pos.c)){
						return curState;
					}
				}
				else if (goal.equals("wumpus")){
					if (kb.ask(new ParameterLiteral(curState.pos.r, curState.pos.c, "wumpus", true))){
						curState.prevAction = "killWumpus";
						return curState;
					}
				}
			}
			
			
			// nechceme rozvijat unknown policka dalej
			if (!isUnknown(curState.pos.r, curState.pos.c) && !close.contains(curState)){
				close.add(curState);
				AgentStateIterator it = new AgentStateIterator(curState);
				while(it.hasNext()){
					AgentState pom = it.next();
					if (goal.equals("safe") && !isSafe(pom.pos.r, pom.pos.c)) continue;
					else if (goal.equals("unknown") && !isSafe(pom.pos.r, pom.pos.c) && !isUnknown(pom.pos.r, pom.pos.c)) continue;
					else if (goal.equals("wumpus") && !isSafe(pom.pos.r, pom.pos.c) && !kb.ask(new ParameterLiteral(pom.pos.r, pom.pos.c, "wumpus", true))) continue;
					
					if ((pom != null) && (!close.contains(pom))){
						open.add(pom);
					}
				}
			}
		}
		return null;
	}
	
	private void doAction() {
		//=====================================================================
		//                          MODIFY HERE 
		//=====================================================================			
		
		if (goldGrabbed){
			//return;
		}
		if (percept[Constants.GLITTER]){
			pickUp();
			goldGrabbed = true;
			return;
		}
		else if (actionStack.size() == 0){
			// reset myState previous actions
			myState.prevAction = null;
			myState.prevState = null;
			
			
			AgentState goal = null;
			
			goal = BFS(
				kb,
				myState,
				"safe"
			);
			if (goal == null){
				goal = BFS(
						kb,
						myState,
						"wumpus"
					);
			}
			
			AgentState cur = goal;
			
			if (cur == null){
				//halt(); // we have no position to visit
				return;
			}
			
			while(cur.prevAction != null){
				actionStack.push(cur.prevAction);
				cur = cur.prevState;
			}
		}
		else{
			String action = actionStack.pop();
			if (action == "rotateLEFT"){
				turnLEFT();
				myState = myState.rotateLEFT();
				return;
			}
			else if (action == "rotateRIGHT"){
				turnRIGHT();
				myState = myState.rotateRIGHT();
				return;
			}
			else if (action == "forward"){
				moveFW();
				myState = myState.getFW();
				return;
			}
			else if (action == "killWumpus"){
				shoot();
				kb.tell(new Literal("wumpusDead", true)); // zabili sme wumpusa
				return;
			}
		}
		
		//turnLEFT();		
		//turnRIGHT();		
		//shoot();
		//climb();
		
		//=====================================================================
		//                      STOP MODIFYING HERE 
		//=====================================================================					
	}
	
}