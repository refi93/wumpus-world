import java.util.ArrayList;
import java.util.HashSet;

public class KnowledgeBase {
	
	HashSet<Formula> kb;
	
	KnowledgeBase(HashSet<Formula> kb){
		this.kb = kb;
	}
	
	public boolean remove(Formula f){
		return kb.remove(f);
	}
	
	public void tell(Formula f){
		if (!kb.contains(f)){
			kb.add(f);
		}
	}
	
	public void tell(ArrayList<Formula> f){		
		for (int i = 0; i < f.size(); i++) {
			tell(f.get(i));
		}		
	}
	
	public boolean ask(Literal[] f){
		boolean result = true;
		
		for (int i = 0; i < f.length && result; i++) {
			result = ask(f[i]);
		}
		
		return result;
	}
	
	public boolean ask(Literal f){
		return kb.contains(f) || backwardChaining(f);
	}	

	private boolean backwardChaining(Literal l) {					
		//=====================================================================
		//                          MODIFY HERE 
		//=====================================================================		
		
		// implement backward chaining
		if (kb.contains(l)) return true;
		
		for (Formula a : kb) {
			if (a.isImplication()){
				Implication impl = (Implication)a;
				if (impl.match(l)){ // ak je l na pravej strane implikacie
					if (ask(impl.getPreconditions())) return true;
				}
			}
		}
		return false;
		//=====================================================================
		//                      STOP MODIFYING HERE 
		//=====================================================================		
	}

	@SuppressWarnings("unused")
	private void showExplanation(Literal l, Formula f) {
		System.out.println("I deduced " + l + " because of\t" + f);		
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder("==============  KB  ==============\n");
		for (Formula a : kb) {
			result.append(a.toString() + "\n");
		}
				
		return result.append("============== END OF KB  ==============\n").toString();
	}

}
