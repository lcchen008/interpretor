package frontend;

import errorhandling.LispException;

public class Sexp {
	public int type; // 1:atomic, 2:identifier, 3:binary tree
	public String name; //used for identifiers
	public int val;  //used for atomic integers
	public Sexp car;
	public Sexp cdr;
	
	public Sexp()
	{
		this(TokenType.NIL, "NIL", 0, null, null);
	}
	
	public Sexp(int type, String name, int val, Sexp car, Sexp cdr)
	{
		this.type = type;
		this.name = name;
		this.val = val;
		this.car = car;
		this.cdr = cdr;
	}
	
	public Sexp car() throws LispException
	{
		if(this.type!=TokenType.BINARY_TREE)
		{
			throw new LispException("CAR cannot be applied to ATOM: "+this.name);
		}
		else
		return this.car;
	}
	
	public Sexp cdr() throws LispException
	{
		if(this.type!=TokenType.BINARY_TREE)
		{
			throw new LispException("CDR cannot be applied to ATOM: "+this.name);
		}
		else
		return this.cdr;
	}
	
	public Sexp atom()
	{
		if(this.type!=TokenType.BINARY_TREE)
			return new Sexp(TokenType.T, "T", 0, null, null);
		else
			return new Sexp(TokenType.NIL, "NIL", 0, null, null);
	}
	
	public Sexp nul()
	{
		if(this.type==TokenType.NIL)
			return new Sexp(TokenType.T, "T", 0, null, null);
		else
			return new Sexp(TokenType.NIL, "NIL", 0, null, null);
	}
	
	public Sexp cons(Sexp cdr)
	{
		return new Sexp(TokenType.BINARY_TREE, " . ", 0, this, cdr);
	}
	
	public Sexp eq(Sexp exp) throws LispException
	{
		if(this.type==TokenType.BINARY_TREE)
			throw new LispException("EQ can only be applied to atoms");
		else if(exp.type==TokenType.BINARY_TREE)
			throw new LispException("EQ can only be applied to atoms");
		else if(this.type==exp.type)
		{
			if(this.name.equals(new String(exp.name)))
				return new Sexp(TokenType.T, "T", 0, null, null);
			else
				return new Sexp(TokenType.NIL, "NIL", 0, null, null);
		}
		else
			return new Sexp(TokenType.NIL, "NIL", 0, null, null);
	}
	
	public Sexp inte()
	{
		if(this.type==TokenType.NUMBER)
			return new Sexp(TokenType.T, "T", 0, null, null);
		else
			return new Sexp(TokenType.NIL, "NIL", 0, null, null);
	}
	
	public Sexp plus(Sexp exp) throws LispException
	{
		if(this.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the first argument of function PLUS");
		else if(exp.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(exp)+" is not an INT, cannot be the second argument of function PLUS");
		else
		{
			int newvalue = this.val+exp.val;
			return new Sexp(TokenType.NUMBER, ""+newvalue, newvalue, null, null);
		}
	}
	
	public Sexp minus(Sexp exp) throws LispException
	{
		if(this.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the first argument of function MINUS");
		else if(exp.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the second argument of function MINUS");
		else
		{
			int newvalue = this.val-exp.val;
			return new Sexp(TokenType.NUMBER, ""+newvalue, newvalue, null, null);
		}
	}
	
	public Sexp times(Sexp exp) throws LispException
	{
		if(this.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the first argument of function TIMES");
		else if(exp.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the second argument of function TIMES");
		else
		{
			int newvalue = this.val*exp.val;
			return new Sexp(TokenType.NUMBER, ""+newvalue, newvalue, null, null);
		}
	}
	
	public Sexp quotient(Sexp exp) throws LispException
	{
		if(this.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the first argument of function QUOTIENT");
		else if(exp.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the second argument of function QUOTIENT");
		else if(exp.val==0)
			throw new LispException("The second argument of function QUOTIENT should not be 0");
		else
		{
			int newvalue = this.val/exp.val;
			return new Sexp(TokenType.NUMBER, ""+newvalue, newvalue, null, null);
		}
	}
	
	public Sexp remainder(Sexp exp) throws LispException
	{
		if(this.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the first argument of function REMAINDER");
		else if(exp.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the second argument of function REMAINDER");
		else if(exp.val==0)
			throw new LispException("The second argument of function REMAINDER should not be 0");
		else
		{
			int newvalue = this.val%exp.val;
			return new Sexp(TokenType.NUMBER, ""+newvalue, newvalue, null, null);
		}
	}
	
	public Sexp less(Sexp exp) throws LispException
	{
		if(this.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the first argument of function LESS");
		else if(exp.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the second argument of function LESS");
		else
		{
			if(this.val<exp.val)
				return new Sexp(TokenType.T, "T", 0, null, null);
			else
				return new Sexp(TokenType.NIL, "NIL", 0, null, null);
		}
	}
	
	public Sexp greater(Sexp exp) throws LispException
	{
		if(this.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the first argument of function GREATER");
		else if(exp.type!=TokenType.NUMBER)
			throw new LispException(Parser.output2(this)+" is not an INT, cannot be the second argument of function GREATER");
		else
		{
			if(this.val>exp.val)
				return new Sexp(TokenType.T, "T", 0, null, null);
			else
				return new Sexp(TokenType.NIL, "NIL", 0, null, null);
		}
	}
}
