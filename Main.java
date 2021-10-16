import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class Main {
  static boolean hadError = false;

  public static void main(String[] args) throws IOException {
      byte[] bytes = Files.readAllBytes(Paths.get("input.java"));
      String source = new String(bytes, Charset.defaultCharset());
      run(source);
  }

  private static void run(String source) {
    LexerDraft lexer = new LexerDraft(source);
    List<Token> tokens = lexer.scanTokens();
    Parser parser = new Parser(tokens);    
    List<Stmt> statements = parser.parse();      
    
     for (Stmt stmt : statements)
    {
      System.out.println(stmt);
    } 
    
    //just print tokens
/*      for (Token token : tokens) {
      System.out.println(token);
    }  */

    // Stop if there was a syntax error.
    if (hadError) return;    
}






    /* private static void report(int line, String where,
                              String message) {
      System.err.println(
          "[line " + line + "] Error" + where + ": " + message);
      hadError = true;
    }
 */
/*     static void error(Token token, String message) {
      if (token.type == TokenType.EOF) {
        report(token.line, " at end", message);
      } else {
        report(token.line, " at '" + token.lexeme + "'", message);
      }
    }  */







  }
