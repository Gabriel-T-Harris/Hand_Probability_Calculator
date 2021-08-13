package parser;

/**
<b>
Purpose: Used as the exception when the semantic stack is empty yet there is another token to parse.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-8-5
</b>
*/

public class EmptySemanticStackException extends Exception
{
    private static final long serialVersionUID = 7302965196829903702L;

    public EmptySemanticStackException(String error_message)
    {
        super(error_message);
    }
}
