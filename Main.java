import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Main {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    static List<Stmt> statements = new ArrayList<>();
    static ProjectGUI gui = new ProjectGUI();
    
    public static void main(String[] args) throws IOException {        
        gui.createWindow();        
    }

    static List<Stmt> getStmts() {
        return statements;
    }

    static void run2() {
        String path = gui.getInputPath();
        Lexer lexer = new Lexer(path);        
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);            
        statements = parser.parse();        
        for (Token token : tokens) { System.out.println(token);}    //CHECK LEXER
        
        
        gui.fillList(statements); 
    }

    static void run(String source) {    
        gui.createWindow();
        String path = gui.getInputPath();
        Lexer lexer = new Lexer(path);        
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);            
        statements = parser.parse();       

        // Stop if there was a syntax error.
        if (hadError) return;
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
    
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
}







