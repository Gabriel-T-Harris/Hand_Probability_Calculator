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
    public EmptySemanticStackException(String error_message)
    {
        super(error_message);
    }
}
