package parser;

/**
<b>
Purpose: To be thrown when something should be matched but somehow failed.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-8-5
</b>
*/

public class TerminalMatchException extends Exception
{
    public TerminalMatchException(String error_message)
    {
        super(error_message);
    }
}
