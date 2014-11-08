import java.awt.Point;


public class Implication extends Formula{
	
	private Literal[] preconditions;
	private ParameterLiteral consequent;
	
	private int name;
	
	private int hash;
	
	Implication(int name, ParameterLiteral consequent, Literal... precondition){
		this(consequent, precondition);
		this.name = name;		
	}
	
	Implication(ParameterLiteral consequent, Literal... precondition){
		this.name = 0;
		
		this.preconditions = precondition;
		this.consequent = consequent; 
		
		hash = 17;		
		int pom;
		
		pom = consequent.hashCode();
		hash = 37*hash + pom;
		
		for (int i = 0; i < preconditions.length; i++) {
			pom = preconditions[i].hashCode();
			hash = 37*hash + pom;
		}			
	}	

	public boolean match(Literal l){
		boolean result =  this.consequent.match(l);
		
		if (result && this.consequent instanceof ParameterLiteral && l instanceof ParameterLiteral){
			ParameterLiteral c = (ParameterLiteral) this.consequent;
			c.setPoint(((ParameterLiteral) l).getPoint());
			Point p = new Point(c.getPoint());
			for (int i = 0; i < preconditions.length; i++) {
				if (this.preconditions[i] instanceof MatchingLiteral){
					MatchingLiteral m = (MatchingLiteral) this.preconditions[i]; 
					m.setPoint(p.x + m.getModifier().x, p.y + m.getModifier().y);
				}				
			}
		}
		
		return result;
	}
	
	public Literal[] getPreconditions(){
		Literal[] result = new Literal[this.preconditions.length];
		
		for (int i = 0; i < result.length; i++) {
			if (this.preconditions[i] instanceof MatchingLiteral){
				result[i] = ((MatchingLiteral)this.preconditions[i]).convertToParameterLiteral();
			}else{
				result[i] = this.preconditions[i];
			}			
		}
		
		return result;
	}

	@Override
	public boolean equals(Object f) {
		if (f == this){
			return true;
		}		
		if (!(f instanceof Implication)){					
			return false;
		}		
		Implication l = (Implication) f;
		boolean result = l.getPreconditions().length == this.preconditions.length && l.consequent.equals(this.consequent);
		for (int i = 0; i < this.preconditions.length && result; i++) {
			result = this.preconditions[i].equals(l.getPreconditions()[i]);
		}
		return result;
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		
		if (this.name > 0){
			result.append("("+this.name + " ");
		}
		
		for (int i = 0; i < preconditions.length; i++) {
			result.append(preconditions[i].toStringRelative() + (i==preconditions.length-1?" ":" & "));
		}
		
		return result.append("---> " + consequent.toStringRelative()+", "+consequent.toStringPoints()+")").toString();
	}
	
	@Override
	public int hashCode() {
		return hash;
	}

}