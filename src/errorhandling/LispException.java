package errorhandling;

public class LispException extends Exception
{
	private static final long serialVersionUID = (long) 8F;
	private String errormsg;
	public LispException(String errormsg)
	{
		this.errormsg=errormsg;
	}
	
	public String errormsg()
	{
		return this.errormsg;
	}
}
