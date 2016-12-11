
package Model;

public class ErrorLogData 
{
    private int line;
    private String message;
    
    public ErrorLogData(int line, String message) 
    {
        this.line = line;
        this.message = message;
    }

    public int getLine() 
    {
        return line;
    }

    public String getMessage() 
    {
        return message;
    }
}