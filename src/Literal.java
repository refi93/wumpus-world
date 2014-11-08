
public class Literal extends Formula{
	protected String name;
	protected boolean value;
	
	private int hash;

	public Literal(String name, Boolean value) {
		this.name = name;
		this.value = value;
		
		int hash = 17;
		int pom;
		pom = this.value ? 0 : 1;
		hash = 37*hash + pom;
		pom = this.name.hashCode();
		hash = 37 * hash + pom;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object f) {
		if (f == this){
			return true;
		}
		if (!(f instanceof Literal)){
			return false;		
		}
		
		return match((Literal) f);
	}
	
	public boolean match(Literal l){
		boolean sameName = l.getName().equals(this.name);
		boolean sameValue = l.getValue() == this.value;
		return sameName && sameValue;
	}
		
	private boolean getValue() {
		return this.value;
	}

	public String toString(){
		String result = this.name;
		if (this.value){
			return result;
		}else{
			return "~" + result;
		}  
	}
	
	public String toStringRelative() {
		return toString();
	}
	
	@Override
	public int hashCode() {	
		return hash;
	}

}
