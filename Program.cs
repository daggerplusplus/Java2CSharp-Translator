using System;
public class Program
{
    public static void Main (string []  args ) 
    {
        string [] [] numbers = new Array  [3] [3];
        int temp = 0;
        for (x = 0; x  < 3; x ++)
        {
            for (y = 0; y  < 3; y ++)
            {
                numbers [x ][y ]temp ++;
            }
        }
        string output = "";
        for (x = 0; x  < 3; x ++)
        {
            for (y = 0; y  < 3; y ++)
            {
                output = output  + " " + numbers  [x ] [y ];
            }
            output = output  + "\n";
        }
        Console.WriteLine(output );
    
    }
}