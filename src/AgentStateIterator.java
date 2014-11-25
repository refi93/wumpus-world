import java.util.Iterator;


public class AgentStateIterator implements Iterator{

	AgentState current;
	int counter;
	
	public AgentStateIterator(AgentState first){
		super();
		current = first;
		counter = 0;
	}
	
	@Override
	public boolean hasNext() {
		return counter < 3;
	}

	@Override
	public AgentState next() {
		
		if (counter == 0){
			counter++;
			return current.getFW();
		}
		if (counter == 1){
			counter++;
			return current.rotateLEFT();
		}
		if (counter == 2){
			counter++;
			return current.rotateRIGHT();
		}
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
}
