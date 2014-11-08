
public class Tile{
		   
    boolean visited;
    private int isSafe;         
        
	public Tile(){		
		visited = false;
		isSafe = Constants.UNKNOWN;
	}
	
	public int isSafe(){
		return isSafe;
	}
	
	public void setSafe(boolean value){
		if (value){
			isSafe = Constants.TRUE;
		}else{
			isSafe = Constants.FALSE;			
		}
	}
			
}
