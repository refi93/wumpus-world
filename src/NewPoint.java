import java.awt.Point;

public class NewPoint extends Point {

	private static final long serialVersionUID = 1L;

	public NewPoint(int x, int y) {
		super(x, y);
	}
	
	@Override
	public int hashCode() {		
		return 37*(37*17 + this.x) + this.y;
	}
	
	public String toStringX(){
		return toStringCoordinate(x);
	}
	
	public String toStringY(){
		return toStringCoordinate(y);
	}
	
	private String toStringCoordinate(int n){
		if (n == 0){
			return "";
		}else if(n > 0){
			return "+"+n;			
		}else{
			return ""+n;
		}
	}

}
