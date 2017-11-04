package ui;

import java.util.Scanner;
import java.util.StringTokenizer;

import backend.Interpreter;
import errorhandling.LispException;
import frontend.Parser;
import frontend.Sexp;

public class Main 
{
	public static void main(String args[])
	{
		LispWorker lispworker = new LispWorker();
		lispworker.work();
	}	
}

class LispWorker
{
	private Interpreter interpreter = new Interpreter();
	Scanner sc = new Scanner(System.in);
	
	public void work()
	{
		
		String input = "";
		while(sc.hasNextLine())
		{
			input+=" "+sc.nextLine()+" ";
		}
		
		StringTokenizer st = new StringTokenizer(input);
		
		if(!st.hasMoreTokens())
		{
			System.out.println("Nothing is contained in the input file");
			return;
		}
		
		do_work(input);
	}
	
	public void do_work(String input)
	{
		Parser parser = new Parser(input);
		String s = "";
		Sexp exp;
		
		System.out.println();
		
		try
		{
			exp = parser.input();
			exp = interpreter.eval(exp, new Sexp());
			System.out.println(Parser.output2(exp));
			s+=Parser.output2(exp);
			s+="\n";
		}catch(LispException e1)
		{
			System.out.println(e1.errormsg());
		}
		
		while(parser.skipToken())
		{
			try
			{
				exp = parser.input();
				exp = interpreter.eval(exp, new Sexp());
				System.out.println(Parser.output2(exp));
				s+=Parser.output2(exp);
				s+="\n";
			}
			catch(LispException e1){System.out.println(e1.errormsg());}
		}
	}
}