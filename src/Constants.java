
public final class Constants{
	
	//-------------Field input characters---------------
    static final char CLEAN_CHAR = ' ';
	static final char GOLD_CHAR = '*';
    static final char WALL_CHAR = '#';
    static final char PIT_CHAR = 'o';
    static final char WUM_CHAR = 'w';
    static final char AGENT_CHAR = 'a';    
    
	//-------------Field types--------------------------
    static final int CLEAN = 0;
	static final int GOLD = 1;
    static final int WALL = 2;
    static final int PIT = 3;
    static final int WUM = 4;
    
    //-------------Agent's customization----------------
    static final int NUMBER_OF_ARROWS = 1;
    
    //------------3 Boolean values---------------------
    static final int UNKNOWN = 2;
    static final int TRUE = 1;
    static final int FALSE = 0;   

	//-------------Cardinal directions------------------
    static final int NORTH = 0;
    static final int EAST = 1;
    static final int SOUTH = 2;
    static final int WEST = 3;
    
	//-------------Percept types------------------------
    static final int BREEZE = 0;
    static final int STENCH = 1;
    static final int BUMP = 2;
    static final int SCREAM = 3;
    static final int GLITTER = 4;    
    
    //-------------Score points-------------------------
    static final int AGENT_DIED = -10000;
    static final int ACTION = -1;
    static final int FOUND_GOLD = 1000;
    static final int KILLED_WUMPUS = 2000;          
}