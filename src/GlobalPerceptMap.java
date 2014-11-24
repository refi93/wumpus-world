import java.util.ArrayList;


/* stitches together all percepted data */
public class GlobalPerceptMap {
	int[][] map;
	int percept_size;
	
	public GlobalPerceptMap(int percept_size){
		this.percept_size = percept_size;
		
		// matrix representation of currently known world
		map = new int[Constants.WORLD_MAX_SIZE * 2 + 1][Constants.WORLD_MAX_SIZE * 2 + 1];
		
		//initialize map to walls
		for (int i = 0; i < Constants.WORLD_MAX_SIZE * 2 + 1; i++){
			for (int j = 0; j < Constants.WORLD_MAX_SIZE * 2 + 1; j++){
				map[i][j] = Constants.UNKNOWN;
			}
		}
	}
	
	public int[][] getMap(){
		return map;
	}
	
	
	// based on current agent state and it's current percept we update the map
	public void update(AgentState curState, int[][] curPercept){
		for (int i = 0; i < 2 * percept_size + 1; i++){
			for (int j = 0; j < 2 * percept_size + 1; j++){
				map[curState.pos.r - percept_size + i][curState.pos.c - percept_size + j] = curPercept[i][j];
			}
		}
	}
	
	public String toString(AgentState curState){
		StringBuilder ret = new StringBuilder();
		for (int i = 0;i < Constants.WORLD_MAX_SIZE * 2; i++){
			for (int j = 0; j < Constants.WORLD_MAX_SIZE * 2; j++){
				
				if (curState.pos.r == i && curState.pos.c == j){
					if (map[i][j] == 0)
						ret.append("@");
					else if (map[i][j] == 1)
						ret.append("#");
					else{
						ret.append("$");
					}
				}
				else if (map[i][j] == 0){
					ret.append('0');
				}
				else if (map[i][j] == 1){
					ret.append('1');
				}
				else{
					ret.append('-');
				}
			}
			ret.append('\n');
		}
		return ret.toString();
	}
}
