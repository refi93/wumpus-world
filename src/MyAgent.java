import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

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
	
	int r, c, orientation;
	KnowledgeBase kb;
	
	HashMap<Integer, Integer> dir_r, dir_c; // vektory smerov vzhladom na svetove strany
	int rotation_count;
	boolean goldGrabbed;
		
	public MyAgent(int orientation, int w, int h) {
		
		super(orientation);		
		
		rotation_count = 0;
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
		
		r = h; c = w; this.orientation = orientation;
		
		createKB();
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
		
		axioms.add(new Implication(new ParameterLiteral("noWumpus", true),
				new MatchingLiteral(0, -1, "stench", true), new MatchingLiteral(-1, 0, "stench", true), new MatchingLiteral(-1, -1, "wumpus", true)));
		
		//severovychod
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(-1, 0, "stench", true), new MatchingLiteral(0, 1, "stench", true), new MatchingLiteral(-1, 1, "noWumpus", true)));
		
		axioms.add(new Implication(new ParameterLiteral("noWumpus", true),
				new MatchingLiteral(-1, 0, "stench", true), new MatchingLiteral(0, 1, "stench", true), new MatchingLiteral(-1, 1, "wumpus", true)));

		//juhovychod
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(0, 1, "stench", true), new MatchingLiteral(1, 0, "stench", true), new MatchingLiteral(1, 1, "noWumpus", true)));
		
		axioms.add(new Implication(new ParameterLiteral(0, 0, "noWumpus", true),
				new MatchingLiteral(0, 1, "stench", true), new MatchingLiteral(1, 0, "stench", true), new MatchingLiteral(1, 1, "wumpus", true)));

		//juhozapad
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(0, -1, "stench", true), new MatchingLiteral(1, 0, "stench", true), new MatchingLiteral(1, -1, "noWumpus", true)));
		
		axioms.add(new Implication(new ParameterLiteral("noWumpus", true),
				new MatchingLiteral(0, -1, "stench", true), new MatchingLiteral(1, 0, "stench", true), new MatchingLiteral(1, -1, "wumpus", true)));
		
		
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
		
		//=====================================================================
		//                          MODIFY HERE 
		//=====================================================================				
		// ked sme na policko stupili, tak urcite tam neni wumpus
		a.add(new ParameterLiteral(r, c, "noWumpus", true));
		a.add(new ParameterLiteral(r, c, "noPit", true));
		
		
		if (percept[BUMP]){
			a.add(new ParameterLiteral(r, c, "wall", true));
			r = r - dir_r.get(orientation);
			c = c - dir_c.get(orientation);
		}
		if (percept[BREEZE]){
			a.add(new ParameterLiteral(r, c, "breeze", true));
		}
		else{
			a.add(new ParameterLiteral(r, c, "noBreeze", true));
		}
		if (percept[STENCH]){
			a.add(new ParameterLiteral(r, c, "stench", true));
		}
		else{
			a.add(new ParameterLiteral(r, c, "noStench", true));
		}
		
		if (percept[BUMP]){
			System.out.println(percept[BUMP]);
			
			//return;
		}
		
		//=====================================================================
		//                      STOP MODIFYING HERE 
		//=====================================================================		
											
		kb.tell(a);		
	}

	public boolean canFW(){
		if (kb.ask(new ParameterLiteral(r + dir_r.get(orientation), c + dir_c.get(orientation), "wall", true))){
			return false;
		}
		
		return (kb.ask(new ParameterLiteral(r + dir_r.get(orientation), c + dir_c.get(orientation), "safe", true)));
	}
	
	public boolean cannotFW(){
		return (kb.ask(new ParameterLiteral(r + dir_r.get(orientation), c + dir_c.get(orientation), "pit", true))) ||
				(kb.ask(new ParameterLiteral(r + dir_r.get(orientation), c + dir_c.get(orientation), "wumpus", true))) ||
				(kb.ask(new ParameterLiteral(r + dir_r.get(orientation), c + dir_c.get(orientation), "wall", true)));
	}
	
	private void doAction() {
		//=====================================================================
		//                          MODIFY HERE 
		//=====================================================================			
		
		if (goldGrabbed){
			return;
		}
		if (percept[Constants.GLITTER]){
			pickUp();
			goldGrabbed = true;
			return;
		}else{
			Random rand = new Random();
			
			while(true){
				int x = rand.nextInt() % 2;
				
				System.out.println(orientation + " " + r + " " + c);
				
				int pomr = r + dir_r.get(orientation);
				int pomc = c + dir_c.get(orientation);
				
				System.out.println("WANT" + pomr + " " + pomc);
				
				if (x == 0 && canFW()){
					moveFW();
					r = r + dir_r.get(orientation);
					c = c + dir_c.get(orientation);
					rotation_count = 0;
					break;
				}
				else if (x == 0 && cannotFW()){
					continue;
				}
				else{
					turnLEFT();
					orientation = (orientation - 1 + 4) % 4;
					rotation_count++;
					break;
				}
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