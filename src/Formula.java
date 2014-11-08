
public abstract class Formula {
	
	public boolean isImplication(){
		return this instanceof Implication;
	}
	
	@Override
	public abstract boolean equals(Object f); 

	public boolean isLiteral(){
		return this instanceof Literal;
	}
	
}
