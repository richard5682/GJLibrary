package GJUtil;

public abstract class LogBackAction {
	public void print(String text){}
	public void println(String text){}
	public void printf(String text,float variable){
		String returnval = String.format(text, variable);
		print(returnval);
	}
}
