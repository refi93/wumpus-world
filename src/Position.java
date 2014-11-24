public class Position { 
  public  int r; 
  public  int c; 
  public Position(int r, int c) { 
    this.r = r; 
    this.c = c; 
  } 
  
  public String toString(){
	  return r + " " + c;
  }
  @Override
  public boolean equals(Object obj) {
   if(this==obj)
    return true;
   Position p = (Position)obj;
   return ((p.r == this.r) && (p.c == this.c));
  }
  
  @Override
  public int hashCode(){
	  int prime = 47;
	  int result = 1;
	  result = prime * result + this.r;
	  result = prime * result + this.c;
      return result;
  }
} 