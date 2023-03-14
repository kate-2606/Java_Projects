package edu.uob;

public class Parser {
    // Token Type
    public void Parser(LexAnalyser inpLex){ lex=inpLex; }
    private LexAnalyser lex=null;

    private boolean accept(Token t){
        Token tok = lex.getCurrentToken();
        if (tok==t){
            tok = lex.getNextToken();
            return true;
        }
        return false;
    }

    public boolean expect(Token t){
        if (accept(t)){
            return true;
        }
        return false;
    }
    //private enum TokenType {OpenParenth, CloseParenth, Create};

    public enum TokenType {
        USE, CREATE, DROP, ALTER, INSERT, SELECT, JOIN, DATABASE,
        //9
        TABLE, VALUES, INTO, UPDATE, FROM, SET, WHERE, ON,
        //17
        ADD, OPEN_BR, CLOSE_BR, SPACE, COMMA, SEMI_COL, NULL, WILD_CRD,
        //24
        COMPARATOR, BOOL_OP, STRING_LIT, BOOL_LIT, FLOAT_LIT, INT_LIT,
        PLAIN_TXT
    }

    // Array of lambda parse methods, one for each TokenType.
    // Must be in the same order as TokenType
    private Runnable[] parseMethods = {() -> parseUse(), () -> parseCreate(), () -> parseDrop(), () -> parseAlter()/* ... */};

    // Set of parse methods
    private boolean parseUse() {
        System.out.println("In parseOpenParenth");
        return true;
    }

    private boolean parseCreate() {
        System.out.println("In parseCloseParenth");
        return true;
    }

    private boolean parseDrop() {
        System.out.println("In parseCreate");
        return true;
    }

    private boolean parseAlter() {
        return true;
    }

    // Top level parser
    private void parse() {
        // Call a specific parse method
        parseMethods[TokenType.CLOSE_BR.ordinal()].run();

        // Calling each parser in order
       // for(TokenType token : TokenType.values())
         //   parseMethods[token.ordinal()].run();
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.parse();
    }
}



    /*

    public Boolean parser(){
        if(command){
            return true;
        }
        return false;
    }

    private
    int tokenPosition =0;
    private Boolean command(){
        if(commandType){

        }
    }

     */