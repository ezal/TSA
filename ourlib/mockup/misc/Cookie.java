package mockup.misc;

public class Cookie {
	String name, value, comment;
	
	static { int z=1; }
	
	public Cookie(String x, String y) {}
	
	public String getName(){
		return name;
	}
	
	public String getValue(){
		return value;
	}
	
	public String getComment(){
		return comment;
	}
	
	public void setComment(String purpose){
		comment = purpose;
	}
}
