/*
    Copyright (C) 2021 Gabriel Toban Harris

        This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

        This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
    along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
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
