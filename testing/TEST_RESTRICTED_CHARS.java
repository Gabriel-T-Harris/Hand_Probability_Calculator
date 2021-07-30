package testing;

import java.util.regex.Pattern;
import parser.Tokenizer;

/**
<b>
Purpose: To make sure that the regex of {@link Tokenizer#RESTRICTED_CHARS} complies properly.<br>
Programmer: Gabriel Toban Harris <br>
Date: 2021-07-30
</b>
*/

public class TEST_RESTRICTED_CHARS
{
    public static void main(String[] args)
    {
        String regex = Tokenizer.RESTRICTED_CHARS;
        Pattern ID_CHAR_SET = Pattern.compile("[" + regex.replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]") + "]");

        for (int i = 0; i < regex.length(); ++i)
            System.out.println("Curent char: " + regex.charAt(i) + ", does it match (it should): " + ID_CHAR_SET.matcher(Character.toString(regex.charAt(i))).matches());
    }
}
