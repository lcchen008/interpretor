package frontend;

public interface TokenType 
{
	//Lexer used symbol types, will not appear in the backend
	public static final int LEFT_PAR = 6;
	public static final int RIGHT_PAR = 7;
	public static final int DOT = 8;
	
	
	//Back end used Sexp types
	
	//atoms
	public static final int NUMBER = 0;
	public static final int T = 4;
	public static final int NIL = 5;
	public static final int ID = 10;
	
	//non-atom, used in the backend
	public static final int BINARY_TREE = 9;
}