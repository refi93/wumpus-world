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
	
	int x, y;
	KnowledgeBase kb;
		
	public MyAgent(int orientation, int w, int h) {
		super(orientation);			
		
		createKB();
	}	
	
	private void createKB() {
		HashSet<Formula> axioms = new HashSet<Formula>();
		
		//=====================================================================
		//                          MODIFY HERE 
		//=====================================================================	
		
		// if a tile is surrounded by stenches, then it is a wumpus
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(1, 0, "stench", true), new MatchingLiteral(0, -1, "stench", true),
				new MatchingLiteral(0, 1, "stench", true), new MatchingLiteral(-1, 0, "stench", true)));
		
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
			a.add(new ParameterLiteral(x, y, "breeze", true));
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