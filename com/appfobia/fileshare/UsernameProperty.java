package fileshare;

public class UsernameProperty {
public String username=null;
public String compName=null;


 UsernameProperty(String username2, String compName2) {
	// TODO Auto-generated constructor stub
	username=username2;
	compName=compName2;
}
public String getUserName(){
	return username;
}
public String getCompName(){
	return compName;
}
}
