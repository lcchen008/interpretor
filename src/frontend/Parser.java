package frontend;

import java.util.StringTokenizer;

import errorhandling.LispException;

public class Parser 
{
	private String nextToken;
	private StringTokenizer st;
	
	public Parser(String input)
	{
		input = preprocess(input);
		this.st = new StringTokenizer(input," \t\n");
		this.nextToken = st.nextToken();
	}
	
	/*add whitespaces after ( and )*/
	public String preprocess(String s)
	{
		s = s.replaceAll("(", " ( ");
		s = s.replace(")", " ) ");
		s = s.replace(".", " . ");
		return s;
	}
	
	public String ckNextToken()
	{
		return nextToken;
	}
	
	public boolean skipToken()
	{
		if(st.hasMoreTokens())
		{
			this.nextToken = st.nextToken();
			return true;
		}
		else return false;
	}
	
	public Sexp input() throws LispException
	{
		String token = ckNextToken();
		
		int tokentype = getTokenType(token);
		
		/*If the input token is a INT, we need to store the value of it to the field "value"*/
		if(tokentype==TokenType.NUMBER)
		{
			if(token.startsWith("+"))
				token = token.replace("+", "");
			return new Sexp(TokenType.NUMBER, token, Integer.parseInt(token), null, null);
		}
		
		/*If the input token is NIL, T, or ID, we need to store their literal value to the field "name"*/
		if(tokentype==TokenType.NIL||
				tokentype==TokenType.T||
				tokentype==TokenType.ID)
		{
			return new Sexp(tokentype, token.toUpperCase(), 0, null, null);
		}
		
		/*If the input token is left parenthesis, go on reading more tokens*/
		else if(tokentype==TokenType.LEFT_PAR)
		{
			/*skip the left parenthesis*/
			if(!skipToken())
			{
				/*If no more tokens follow left parenthesis, it is an error*/
				throw new LispException("Needs an id, or a close parenthesis after (");
			}
			
			/*if the right parenthesis follows the left parenthesis immediately, return NIL, and stop reading*/
			if(getTokenType(ckNextToken())==TokenType.RIGHT_PAR)
				return new Sexp(TokenType.NIL, "NIL", 0, null, null);
			
			/*read the tokens following the left parenthesis, and forms the left child*/
			Sexp car = input();
			
			/*skip whatever before dot, and points to dot, or if dot not following, points to next element in the list*/
			if(!skipToken())
			{
				/*if there are no more tokens, it is an error*/
				throw new LispException("Needs a close parenthesis after token \""+ckNextToken()+"\"");
			} 
			
			Sexp ret;
			
			/*if dot is following, we use the dot notation input procedure*/
			if(getTokenType(ckNextToken())==TokenType.DOT)
			{
				/*skip the dot*/
				if(!skipToken())
				{
					/*if there are no more tokens, it is an error*/
					throw new LispException("Needs more tokens after the dot");
				}
				ret = car.cons(input());
				
				/*point to the last token of the argument for input(), i.e., close parenthesis*/
				if(!skipToken())
				{
					/*if there are no more tokens, it is an error*/
					throw new LispException("Needs a close parenthesis after token \""+ckNextToken()+"\"");
				}
			}
			
			/*if no dot is following, we use the list notation input procedure*/
			else
			{
				ret = car.cons(input2());
			}
			
			return ret;
		}
		else 
		{
			throw new LispException("\""+token+"\" is not correctly used, skip it");
		}
	}
	
	public Sexp input2() throws LispException
	{
		String token = ckNextToken();
		int tokentype = getTokenType(token);
		
		if(tokentype==TokenType.RIGHT_PAR)
		{
			return new Sexp(TokenType.NIL, "NIL", 0, null, null);
		}
		
		else
		{
			Sexp car = input();
			
			/*skip to next element*/
			if(!skipToken())
			{
				throw new LispException("Needs a close parenthesis after token \""+ckNextToken()+"\"");
			}
			
			return car.cons(input2());
		}
	}
	
	public static String output(Sexp exp)
	{
		if(exp==null)
			return "ERROR";
		String s = "";
		if(exp.type==TokenType.NUMBER)
		{
			s+=exp.val;
		}
		
		else if(exp.type==TokenType.NIL||
				exp.type==TokenType.T||
				exp.type==TokenType.ID)
		{
			s+=exp.name;
		}
		
		else if(exp.type==TokenType.BINARY_TREE)
		{
			s+="(";
			s+=output(exp.car);
			s+=exp.name;
			s+=output(exp.cdr);
			s+=")";
		}
		return s;
	}
	
	public static String output2(Sexp exp) throws LispException
	{
		return output1(exp, 0);
	}
	
	/**
	 * Print an Sexp as a list if appropriate
	 * @param exp the Sexp to be printed
	 * @param flag indicates whether Sexp is a list, 1 stands for yes, 0 stands for no
	 * */
	public static String output1(Sexp exp, int flag) throws LispException
	{
		if(exp==null)
			return "ERROR";
		
		String s = "";
		
		if(exp.type==TokenType.NUMBER)
		{
			s+=exp.val;
		}
		
		/*For T and ID, just output directly*/
		else if(exp.type==TokenType.T||exp.type==TokenType.ID)
		{
			s+=exp.name;
		}
		
		/*For non-list, need to print NIL*/
		else if(exp.type==TokenType.NIL&&flag==0)
			s+=exp.name;
		
		/*It's a list and do not need to print NIL*/
		else if(exp.type==TokenType.NIL&&flag==1)      	
			s+="";
		
		else if(exp.type==TokenType.BINARY_TREE)
		{
			/*If it is not list, we output the open parenthesis*/
			if(flag==0)
			s+="(";
			
			s+=output1(exp.car, 0);              		//the left branch is always a new sexp
			
			if(isList(exp))
			{
				if(exp.cdr.type!=TokenType.NIL) s+=" ";
				s+=output1(exp.cdr, 1);
			}
			
			else
			{
				s+=exp.name;
				s+=output1(exp.cdr, 0);
			}
			
			/*If it is not list, we output the close parenthesis*/
			if(flag==0)
			s+=")";
		}
		return s;
	}
	
	public static boolean isList(Sexp exp) throws LispException
	{		
		if(exp.type==TokenType.NIL)
			return true;
		else if(exp.type==TokenType.BINARY_TREE)
			return isList(exp.cdr());
		else
			return false;
	}
	
	public boolean isNumber(String token)
	{
		if(token.startsWith("+")||token.startsWith("-"))
		{
			if(token.length()==1)
				return false;
			else
			{
				for(int i = 1; i<token.length(); i++)
				{
					if(!(token.charAt(i)<'9'&&token.charAt(i)>'0'))
						return false;
				}
				return true;
			}
		}
		else
		{
			for(int i = 0; i<token.length(); i++)
			{
				if(!(token.charAt(i)<'9'&&token.charAt(i)>'0'))
					return false;
			}
			return true;
		}
	}
	
	public boolean isValidID(String token)
	{
		if(token.charAt(0)>'Z'||token.charAt(0)<'A')
			return false;
		if(token.length()>1)
		{
			for(int i = 1; i<token.length(); i++)
			{
				char c = token.charAt(i);
				if(!((c<'9'&&c>'0')||(c<'Z'&&c>'A')))
					return false;
			}
			return true;
		}
		else 
			return true;
	}
	
	private int getTokenType(String token) throws LispException
	{
		/*Convert it to upper case*/
		token = token.toUpperCase();
		
		/*First check whether it is a number*/
		if(isNumber(token))
		{
			return TokenType.NUMBER;
		}
		
		/*Check whether it is T*/
		String t = new String("T");
		if(token.equals(t))
		{
			return TokenType.T;
		}
		
		/*Check whether it is NIL*/
		String nil = new String("NIL");
		if(token.equals(nil))
		{
			return TokenType.NIL;
		}
		
		/*Check whether it is left para*/
		String lp = new String("(");
		if(token.equals(lp))
		{
			return TokenType.LEFT_PAR;
		}
		
		/*Check whether it is right para*/
		String rp = new String(")");
		if(token.equals(rp))
		{
			return TokenType.RIGHT_PAR;
		}
		
		/*Check whether it is a dot*/
		String dot = new String(".");
		if(token.equals(dot))
		{
			return TokenType.DOT;
		}
		
		/*Check whether it is an ID*/
		if(isValidID(token))
		{
			return TokenType.ID;
		}
		
		else
			throw new LispException("\""+token+"\" is not a legal token");
	}
}