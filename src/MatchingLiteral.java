
public class MatchingLiteral extends ParameterLiteral {
	private NewPoint modifier;
	private int hash;
	
	MatchingLiteral(int x, int y, String name, boolean value) {
		super(name, value);
		
		modifier = new NewPoint(x, y);
		
		hash = 17;
		int pom;
		pom = this.value ? 0 : 1;
		hash = 37*hash + pom;
		pom = this.modifier.hashCode();
		hash = 37*hash + pom;
		pom = this.name.hashCode();
		hash = 37 * hash + pom;
	}
	
	public NewPoint getModifier() {
		return modifier;
	}
	
	@Override
	public boolean equals(Object f) {
		if (f == this){
			return true;
		}	
		if (!(f instanceof MatchingLiteral)){
			return false;
		}
			
		MatchingLiteral l = (MatchingLiteral) f;		
		boolean sameName = l.getName().equals(this.name);
		boolean sameModifier = l.getModifier().equals(this.modifier);
		boolean sameValue = l.getValue() == value; 
		
		return sameName && sameModifier && sameValue;		
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	public ParameterLiteral convertToParameterLiteral(){
		return new ParameterLiteral(this.point.x, this.point.y, this.name, this.value);
	}
	
	public String toStringRelative(){
		String result = this.name + "(x" + this.modifier.toStringX() + ",y" + this.modifier.toStringY()+")";
		if (this.value){
			return result;
		}else{
			return "~" + result;
		}  
	}

}
