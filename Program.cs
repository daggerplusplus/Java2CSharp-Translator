using System;
public class Program
{
    public class ClassTest1 {
        public int x = 5;
    
    }
    public class ClassTest2 {
        public static void Main (string []  args ) 
        {
            ClassTest1 newObj = new ClassTest1 ();
            Console.WriteLine(newObj .x);
        
        }
    
    }
}