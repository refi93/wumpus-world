import java.util.ArrayList;
import java.util.HashSet;

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
		
	public MyAgent(int orientation, int w, int h) {
		super(orientation);			
		
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
		
		if (percept[BREEZE]){
			a.add(new ParameterLiteral(r, c, "breeze", true));
		}	
		
		//=====================================================================
		//                      STOP MODIFYING HERE 
		//=====================================================================		
											
		kb.tell(a);		
	}

	private void doAction() {
		//=====================================================================
		//                          MODIFY HERE 
		//=====================================================================			
		
		if (percept[Constants.GLITTER]){
			pickUp();
		}else{
			moveFW();
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