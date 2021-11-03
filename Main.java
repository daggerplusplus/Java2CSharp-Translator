import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class Main {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    static List<StmtS2> statements = new ArrayList<>();
    static ProjectGUIS2 gui = new ProjectGUIS2();
    
    public static void main(String[] args) throws IOException {        
        gui.createWindow();        
    }

    static List<StmtS2> getStmts() {
        return statements;
    }

    static void run2() {
        String path = gui.getInputPath();
        LexerS2 lexer = new LexerS2(path);        
        List<TokenS2> tokens = lexer.scanTokens();
        ParserS2 parser = new ParserS2(tokens);            
        statements = parser.parse();
        //private static final Interpreter itptr = new Interpreter();
        //itptr.interpret(statements);
        
        
        gui.fillList(statements); //how to make this work with interpreter output?
    }

    static void run(String source) {    
        gui.createWindow();
        String path = gui.getInputPath();
        LexerS2 lexer = new LexerS2(path);        
        List<TokenS2> tokens = lexer.scanTokens();
        ParserS2 parser = new ParserS2(tokens);            
        statements = parser.parse();       


/*         for (StmtS2 stmt : statements)
      {
        System.out.println(new AstPrinterS2().print(stmt));
        } */

        // //just print tokens
        //   for (Token token : tokens) {
        //   System.out.println(token);
        // }

        // Stop if there was a syntax error.
        if (hadError) return;
/*         for(StmtS2 statment : statements ){
            if(statment != null)
            {
//                System.out.println(statment);
                System.out.println(new AstPrinterS2().print(statment));
            }
//            System.out.println(statment);
//            System.out.println(new AstPrinter().print(statment));
        } */

    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
    
    static void error(TokenS2 token, String message) {
        if (token.type == TokenTypeS2.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

   /*  static void runtimeError(RuntimeErrorS2 error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    } */

}







