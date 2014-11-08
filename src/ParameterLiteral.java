import java.awt.Point;

public class ParameterLiteral extends Literal {
	protected NewPoint point;
	
	private int hash;
	
	ParameterLiteral(int x, int y, String name, boolean value){
		super(name, value);
		point = new NewPoint(x, y);
		
		hash = 17;
		int pom;
		pom = this.value ? 0 : 1;
		hash = 37*hash + pom;
		pom = this.name.hashCode();
		hash = 37 * hash + pom;
	}
	
	public ParameterLiteral(String name, boolean value) {
		this(-1,-1,name,value);
	}

	public String toString(){
		String result = this.name + "(" + this.point.x + "," + this.point.y+")";
		if (this.value){
			return result;
		}else{
			return "~" + result;
		}  
	}
	
	public String toStringRelative(){
		String result = this.name + "(x,y)";
		if (this.value){
			return result;
		}else{
			return "~" + result;
		}		
	}
	
	public String getName(){
		return this.name;
	}
	
	public NewPoint getPoint(){
		return this.point;
	}
	
	public void setPoint(Point p) {
		this.point.x = p.x;
		this.point.y = p.y;
	}
	
	public void setPoint(int x, int y){
		this.point.x = x;
		this.point.y = y;
	}
	
	
	public boolean getValue(){
		return this.value;
	}	

	@Override
	public boolean equals(Object f) {
		if (f == this){
			return true;
		}		
		if (!(f instanceof ParameterLiteral)){
			return false;		
		}
		ParameterLiteral l = (ParameterLiteral) f;	
		boolean sameName = l.getName().equals(this.name);
		boolean samePoint = l.getPoint().equals(this.point);
		boolean sameValue = l.getValue() == this.value; 
		return sameName && samePoint && sameValue;
	}
	
	public boolean match(ParameterLiteral l) {
		boolean sameName = l.getName().equals(this.name);
		boolean sameValue = l.getValue() == this.value; 
		return sameName && sameValue;		
	}

	@Override
	public int hashCode() {
		return hash;
	}

	public String toStringPoints() {		
		return "x="+point.x+" y="+point.y;
	}
	
}
