package backend;

import debug.Debug;
import errorhandling.LispException;
import frontend.Parser;
import frontend.Sexp;
import frontend.TokenType;

public class Interpreter {

	Sexp dlist;
	
	public Interpreter()
	{
		this.dlist = new Sexp();
	}
	
	public boolean isList(Sexp exp) throws LispException
	{
		if(Debug.DEBUG)
			System.out.println("IsList is called... The list is: "+Parser.output2(exp));
		
		if(exp.type==TokenType.NIL)
			return true;
		else if(exp.type==TokenType.BINARY_TREE)
			return isList(exp.cdr());
		else
			return false;
	}
	
	/**
	 * @author lcchen008
	 * @param p param list
	 * @param x arg list, which has been evaled by the evlis
	 * @param alist association list
	 * */
	public Sexp addpairs(Sexp p, Sexp x, Sexp alist) throws LispException
	{
		if(Debug.DEBUG)
		{
			System.out.println("Addpairs is called...");
			System.out.println("Param list is "+Parser.output2(p));
			System.out.println("Arg list is "+Parser.output2(x));
			System.out.println("The alist is: "+Parser.output2(alist));
		}
		
		if((p.type==TokenType.BINARY_TREE&&x.type!=TokenType.BINARY_TREE)||(p.type!=TokenType.BINARY_TREE&&x.type==TokenType.BINARY_TREE))
			throw new LispException("The lengths of parameter list and argument list are not equal");
		if(alist==null)
			alist = new Sexp(TokenType.NIL, "NIL", 0, null, null);
		/*p is an atom, just return alist*/
		if(p.type!=TokenType.BINARY_TREE)
			return alist;
		Sexp list = p.car().cons(x.car()).cons(alist);
		return addpairs(p.cdr(), x.cdr(), list);
	}
	
	/**
	 * @author lcchen008
	 * find the value by using key
	 * @param key the key used to correspond to the value
	 * @param list the list to be parsed 
	 * */
	public Sexp getval(Sexp key, Sexp list) throws LispException
	{
		if(Debug.DEBUG)
		{
			System.out.println("Getval is called...");
		}
		
		if(list.car().car().name.equals(key.name))
			return list.car().cdr();
		else
			return getval(key, list.cdr());
	}
	
	/**
	 * Check whether a key is bouned to a variable in the alist
	 * Or whether a function is in the dlist
	 * */
	public boolean in(Sexp key, Sexp list) throws LispException
	{
		if(Debug.DEBUG)
		{
			System.out.println("In is called...");
			System.out.println("The list is: "+ Parser.output2(list));
		}
		
		if(list.type==TokenType.NIL)
			return false;
		else if(list.car().car().name.equals(key.name))
			return true;
		else
			return in(key, list.cdr());
	}
	
	public boolean in1(Sexp key, Sexp list) throws LispException
	{
		if(Debug.DEBUG)
		{
			System.out.println("In1 is called...");
			System.out.println("The list is: "+ Parser.output2(list));
		}
		
		if(list.type==TokenType.NIL)
			return false;
		else if(list.car().name.equals(key.name))
			return true;
		else
			return in1(key, list.cdr());
	}
	
	public Sexp evlis(Sexp list, Sexp alist) throws LispException
	{
		if(Debug.DEBUG)
		{
			System.out.println("Evlis is called...");
			System.out.println("The list is: "+Parser.output2(list));
			System.out.println("The alist is: "+Parser.output2(alist));
		}
		
		/*First check if the list is an atom*/
		if(list.type!=TokenType.BINARY_TREE)
		{
			/*if it is nil*/
			if(list.type==TokenType.NIL)
				return list;
			else
				throw new LispException("Evlis error, the arguments should be a list");
		}
		/*If the list is a list*/
		else
		{
			return eval(list.car(), alist).cons(evlis(list.cdr(), alist));
		}
	}
	
	/*count the number of elements in one list*/
	public int countlis(Sexp list) throws LispException
	{
		if(list.type==TokenType.NIL)
			return 0;
		else return 1+ countlis(list.cdr());
	}
	
	/*check whether a list contains duplicate elements*/
	public boolean duplicate(Sexp list) throws LispException
	{
		if(Debug.DEBUG)
			System.out.println("Duplicate is called...");
		
		if(list.type==TokenType.NIL)
			return false;
		else if (in1(list.car(), list.cdr()))
			return true;
		else
			return duplicate(list.cdr());
	}
	
	/*Check whether a list contains some non-atom elements*/
	public boolean checkBinary(Sexp list) throws LispException
	{
		if(Debug.DEBUG)
			System.out.println("CheckBinary is called...");
		
		if(list.type==TokenType.NIL)
			return false;
		else if(list.car().type==TokenType.BINARY_TREE)
			return true;
		else
			return checkBinary(list.cdr());
	}
	
	Sexp evcon(Sexp be, Sexp alist) throws LispException
	{
		if(Debug.DEBUG)
		{
			System.out.println("Evcon is called...");
			System.out.println("The be is: "+Parser.output2(be));
			System.out.println("The alist is: "+Parser.output2(alist));
			System.out.println("The type of be is: "+be.type);
			System.out.println("The cdr is: " + Parser.output2(be.cdr()));
		}
		
		/*be is an atom*/
		if(be.type!=TokenType.BINARY_TREE)
		{
			throw new LispException("Econ error, no condition is true");
		}
		/*get the leftmost condition*/
		Sexp cond = eval(be.car().car(), alist);
		if(cond.type!=TokenType.NIL&&cond.type!=TokenType.T)
		{
			throw new LispException("Econ error, the condition value should be T or NIL");
		}
		/*if cond is T, return the value corresponding to the current condition*/
		if(cond.type==TokenType.T)
			return eval(be.car().cdr().car(), alist);
		/*else we evaluate the next condition pair*/
		else
			return evcon(be.cdr(), alist);
	}
	
	public Sexp eval(Sexp exp, Sexp alist) throws LispException
	{
		if(Debug.DEBUG)
		{
			System.out.println("Eval is called...");
			System.out.println("Exp is: "+Parser.output2(exp));
			System.out.println("Type is: "+exp.type);
			System.out.println("The alist is: "+Parser.output2(alist));
		}
		/*for atoms*/
		if(exp.type!=TokenType.BINARY_TREE)
		{
			/*if it is an INT, or T, or NIL, return directly*/
			if(exp.type==TokenType.NUMBER||exp.type==TokenType.T||exp.type==TokenType.NIL)
			{
				return exp;
			}
			/*if it is an id, look up in the alist*/
			else if(exp.type==TokenType.ID)
			{
				if(in(exp, alist))
				{
					return getval(exp, alist);
				}
				else
					throw new LispException("Id "+exp.name+" is not bounded");
			}
			/*else the id is not bounded*/
			else
				throw new LispException("Id "+exp.name+" is not bounded");
		}
		
		/*for non-atoms, must be a list, car must be an atom*/
		
		/*First, we check whether it is a list*/
		else if(!isList(exp))
		{
			throw new LispException("The form of calling a function \""+exp.car().name+"\" should be a list");
		}
		
		else if(exp.car().type!=TokenType.BINARY_TREE)
		{
			/*function name*/
			Sexp nm = exp.car();
			/*arg list*/
			Sexp al = exp.cdr();
			
			/*it's a quote*/
			if(nm.name.equals("QUOTE"))
			{
				//Check whether the number of arguments is 1
				if(countlis(al)!=1)
				{
					throw new LispException("QUOTE must have 1 argument, not "+countlis(al));
				}
				return al.car();
			}
			
			/*it's a cond*/
			else if(nm.name.equals("COND"))
			{
				/*Check whether evcon has at list one arg*/
				if(countlis(al)==0)
				{
					throw new LispException("COND must have at least 1 argument, not "+countlis(al));
				}
				/*call the cond*/
				return evcon(exp.cdr(), alist);
			}
			
			/*it's a defun*/
			else if(nm.name.equals("DEFUN"))
			{
				if(countlis(al)!=3)
				{
					throw new LispException("DEFUN must have 3 arguments, not "+countlis(al));
				}
				
				/*get the function name*/
				Sexp fn = exp.cdr().car();
				if(fn.type==TokenType.BINARY_TREE)
					throw new LispException("Defun error, the function name should be an atom");
				
				if(Debug.DEBUG)
					System.out.println("Function name is: " + fn.name);
				
				/*check whether the newly defined function name is not appropriate*/
				if(fn.name.equals(new String("COND"))||fn.name.equals(new String("QUOTE"))||fn.name.equals(new String("DEFUN")))
					throw new LispException("cannot use "+"\""+fn.name+"\""+" as a function name in DEFUN");
				
				if(fn.type==TokenType.NUMBER)
					throw new LispException("\""+fn.name+"\" is a number and cannot be used as a function name, please use a symbol");
				
				/*get the para list*/
				Sexp pl = exp.cdr().cdr().car();
				
				if(Debug.DEBUG)
					System.out.println("Para list is: "+Parser.output2(pl));
				
				if(Debug.DEBUG)
					System.out.println("Para list is a list? "+ isList(pl));
				
				if(!isList(pl))
					throw new LispException("The param list of function \""+fn.name + "\" should be a list");
				
				if(checkBinary(pl))
					throw new LispException("The param list of function \""+fn.name + "\" should only contain atoms");
				
				/*check whether there are duplicated para names*/
				if(duplicate(pl))
					throw new LispException("The function \""+fn.name+"\" has duplicated parameter names");
				
				/*get the function body*/
				Sexp fb = exp.cdr().cdr().cdr().car();
				if(Debug.DEBUG)
					System.out.println("Function body is: "+Parser.output2(fb));
				
				/*generate the par-list and fb pair*/
				Sexp pf = pl.cons(fb);
				if(Debug.DEBUG)
					System.out.println("Para and body pair is: "+Parser.output2(pf));
				
				/*generate the pair*/
				Sexp pair = fn.cons(pf);
				if(Debug.DEBUG)
					System.out.println("Pair is: "+Parser.output2(pair));
				
				/*add to dlist*/
				this.dlist = pair.cons(this.dlist);
				if(Debug.DEBUG)
				System.out.println("Dlist becomes"+Parser.output2(this.dlist));
				
				//return new Sexp(TokenType.ID, "function \""+exp.cdr().car().name+"\" is defined", 0, null, null);
				return new Sexp(TokenType.ID, exp.cdr().car().name, 0, null, null);
			}
			
			else
			{
					Sexp arglist = evlis(exp.cdr(), alist);
					if(Debug.DEBUG)
					System.out.println("The arg list is: "+Parser.output2(arglist));
					return apply(exp.car(), arglist, alist);
			}
		}
		throw new LispException(Parser.output2(exp.car())+" is not a function name");
	}
	
	Sexp apply(Sexp f, Sexp x, Sexp alist) throws LispException
	{
		if(Debug.DEBUG)
		{
			System.out.println("Apply is called...");
			System.out.println("Function name: "+f.name);
			System.out.println("Para list: "+Parser.output2(x));
			System.out.println("The alist is: "+Parser.output2(alist));
		}
		/*f should be an atom*/
		if(f.type!=TokenType.BINARY_TREE)
		{
			if(f.type==TokenType.NUMBER)
			{
				throw new LispException("\""+f.name+"\" cannot be a function, it is a number, please use a symbol");
			}
			
			else if(f.name.equals(new String("CAR")))
			{
				int num_args = countlis(x);
				if(num_args!=1)
					throw new LispException("Function CAR must have 1 argument, not "+num_args);
				return x.car().car();
			}
			
			else if(f.name.equals(new String("CDR")))
			{
				int num_args = countlis(x);
				if(num_args!=1)
					throw new LispException("Function CDR must have 1 argument, not "+num_args);
				return x.car().cdr();
			}
			
			else if(f.name.equals(new String("CONS")))
			{
				int num_args = countlis(x);
				if(num_args!=2)
					throw new LispException("Function CONS must have 2 arguments, not "+num_args);
				return x.car().cons(x.cdr().car());
			}
			else if(f.name.equals(new String("ATOM")))
			{
				int num_args = countlis(x);
				if(num_args!=1)
					throw new LispException("Function ATOM must have 1 argument, not "+num_args);
				return x.car().atom();
			}
			
			else if(f.name.equals(new String("NULL")))
			{
				int num_args = countlis(x);
				if(num_args!=1)
					throw new LispException("Function NULL must have 1 argument, not "+num_args);
				return x.car().nul();
			}
			
			else if(f.name.equals(new String("EQ")))
			{
				int num_args = countlis(x);
				if(num_args!=2)
					throw new LispException("Function EQ must have 2 arguments, not "+num_args);
				return x.car().eq(x.cdr().car());
			}
			
			else if(f.name.equals(new String("INT")))
			{
				int num_args = countlis(x);
				if(num_args!=1)
					throw new LispException("Function INT must have 1 argument, not "+num_args);
				return x.car().inte();
			}
			
			else if(f.name.equals(new String("PLUS")))
			{
				int num_args = countlis(x);
				if(num_args!=2)
					throw new LispException("Function PLUS must have 2 arguments, not "+num_args);
				return x.car().plus(x.cdr().car());
			}
			
			else if(f.name.equals(new String("MINUS")))
			{	
				int num_args = countlis(x);
				if(num_args!=2)
					throw new LispException("Function MINUS must have 2 arguments, not "+num_args);
				return x.car().minus(x.cdr().car());
			}
			
			else if(f.name.equals(new String("TIMES")))
			{	
				int num_args = countlis(x);
				if(num_args!=2)
					throw new LispException("Function TIMES must have 2 arguments, not "+num_args);
				return x.car().times(x.cdr().car());
			}
			
			else if(f.name.equals(new String("QUOTIENT")))
			{	
				int num_args = countlis(x);
				if(num_args!=2)
					throw new LispException("Function QUOTIENT must have 2 arguments, not "+num_args);
				return x.car().quotient(x.cdr().car());
			}
			
			else if(f.name.equals(new String("REMAINDER")))
			{	
				int num_args = countlis(x);
				if(num_args!=2)
					throw new LispException("Function REMAINDER must have 2 arguments, not "+num_args);
				return x.car().remainder(x.cdr().car());
			}
			
			
			else if(f.name.equals(new String("LESS")))
			{	
				int num_args = countlis(x);
				if(num_args!=2)
					throw new LispException("Function LESS must have 2 arguments, not "+num_args);
				return x.car().less(x.cdr().car());
			}
			
			else if(f.name.equals(new String("GREATER")))
			{
				int num_args = countlis(x);
				if(num_args!=2)
					throw new LispException("Function GREATER must have 2 arguments, not "+num_args);
				return x.car().greater(x.cdr().car());
			}
			
			else
				if(in(f, dlist))
				{
					Sexp paralist = getval(f, dlist).car();
					int paralen = countlis(paralist);
					int arglen = countlis(x);
					if(paralen!=arglen)
					{
						throw new LispException("Function \""+f.name+"\" must have "+paralen+" arguments");
					}
					return eval(getval(f, dlist).cdr(), addpairs(paralist, x, alist));
				}
				else
					throw new LispException("Function "+f.name+" is not defined");
		}
		else
		{
			throw new LispException("The function name should be an atom.");
		}
	}
}
