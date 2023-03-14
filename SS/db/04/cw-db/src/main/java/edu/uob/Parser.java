package edu.uob;

public class Parser {
    // Token Type
    public void Parser(String command){
        LexAnalyser initiateLex = new LexAnalyser();
        lex=initiateLex;
        System.out.println(command);
        lex.LexAnalyser(command);
        parsedOkay=command();
    }

    private boolean debugging = true;
    public LexAnalyser lex=null;

    private boolean parsedOkay;

    public boolean getParseResult(){ return parsedOkay; }

    public enum TokenType {
        USE, CREATE, DROP, ALTER, INSERT, SELECT, JOIN, DATABASE,
        //9
        TABLE, VALUES, INTO, UPDATE, DELETE, FROM, SET, WHERE, ON,
        //17
        ADD, OPEN_BR, CLOSE_BR, SPACE, COMMA, DOT, SEMI_COL, NULL, WILD_CRD,
        //25
        COMPARATOR, BOOL_OP, STRING_LIT, BOOL_LIT, FLOAT_LIT, INT_LIT,
        PLAIN_TXT
    }

    private boolean accept(TokenType t){
        Token tok = lex.getCurrentToken();
        if (tok.getType()==t){
            if (t != TokenType.SEMI_COL) {
                tok = lex.getNextToken();
            }
            return true;
        }
        return false;
    }

    public boolean expect(TokenType t){
        if (accept(t)){
            return true;
        }
        return false;
    }

    private boolean condition() {
        if (attributeName()) {
            if (accept(TokenType.COMPARATOR)) {
                if (value()) {
                    if (accept(TokenType.BOOL_OP)) {
                        return condition();
                    }
                    return true;
                }
            }
        }
        if (accept(TokenType.OPEN_BR)) {
            if (condition()) {
                if (accept(TokenType.BOOL_OP)) {
                    if (condition()) {
                        if(accept(TokenType.CLOSE_BR)) {
                            if (accept(TokenType.BOOL_OP)) {
                                return condition();
                            }
                            return true;
                        }
                    }
                }
                if (accept(TokenType.CLOSE_BR)) {
                    if (accept(TokenType.BOOL_OP)) {
                        return condition();
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private boolean wildAttribList(){
        boolean ret=false;
        if(attributeList() || accept(TokenType.WILD_CRD)){
            ret=true;
        }
        return ret;
    }

    private boolean value() {
        Token token = lex.getCurrentToken();
        switch (token.getType()) {
            case STRING_LIT:
            case BOOL_LIT:
            case FLOAT_LIT:
            case INT_LIT:
            case NULL:
                break;
            default:
                return false;
        }
        lex.getNextToken();
        return true;
    }


    private boolean valueList() {
        boolean ret = false;
        if (value()){
            ret=true;
            if(accept(TokenType.COMMA)){
                System.out.println("got past comma");
                ret = valueList();
            }
        }
        return ret;
    }

    private boolean attributeName() {
        boolean ret=false;
        if(expect(TokenType.PLAIN_TXT)){
            ret=true;
            if (accept(TokenType.DOT)){
                    ret=expect(TokenType.PLAIN_TXT);
            }
        }
        return ret;
    }

    private boolean attributeList() {
        boolean ret = false;
        if (attributeName()){
            ret=true;
            if(accept(TokenType.COMMA)){
                ret = attributeList();
            }
        }
        return ret;
    }

    private boolean join() {
        return true;
    }

    private boolean delete() {
        return true;
    }

    private boolean update() {
        return true;
    }

    private boolean select() {
        boolean ret =false;
        lex.getNextToken();
        if(wildAttribList()){
            if(accept(TokenType.FROM)){
                if(accept(TokenType.PLAIN_TXT)){
                    ret=true;
                    if(accept(TokenType.WHERE)){
                        ret=condition();
                    }
                }
            }
        }
        return ret;
    }

    private boolean insert() {
        boolean ret =false;
        lex.getNextToken();
        if(accept(TokenType.INTO)){
            if (accept(TokenType.PLAIN_TXT)){
                if(accept(TokenType.VALUES)){
                    if(accept(TokenType.OPEN_BR)){
                        if(valueList()){
                            ret=expect(TokenType.CLOSE_BR);
                        }
                    }

                }

            }
        }
        return ret;
    }

    private boolean alter() {
        boolean ret =false;
        lex.getNextToken();
        if(accept(TokenType.TABLE)){
            if(accept(TokenType.PLAIN_TXT)){
                if (accept(TokenType.DROP) || accept(TokenType.ADD)){
                    ret=attributeName();
                }
            }
        }
        return ret;
    }

    private boolean drop() {
        lex.getNextToken();
        return expect(TokenType.PLAIN_TXT);
    }

    private boolean create() {
        boolean ret =false;
        lex.getNextToken();
        if(expect(TokenType.TABLE) || expect(TokenType.DATABASE)) {
            if (expect(TokenType.PLAIN_TXT)) {
                ret = true;
                if (!(expect(TokenType.SEMI_COL)) && expect(TokenType.OPEN_BR)){
                    if (attributeList()) {
                        if (expect(TokenType.CLOSE_BR)) {
                            ret = true;
                        }
                    }
                }
            }
        }
        return ret;
    }

    private boolean use() {
        Token token = lex.getNextToken();
        if(debugging){
            System.out.println("in commandType, token type is: "+ token.getType());
        }
        return expect(TokenType.PLAIN_TXT);
    }

    private boolean commandType(){
        Token token = lex.getCurrentToken();
        if(debugging){
            System.out.println("in commandType, token type is: "+ token.getType());
        }
        boolean ret;
        switch (token.getType()){
            case USE : ret=use();
                break;
            case CREATE : ret=create();
                break;
            case DROP : ret=drop();
                break;
            case ALTER : ret=alter();
                break;
            case INSERT : ret=insert();
                break;
            case SELECT : ret=select();
                break;
            case UPDATE : ret=update();
                break;
            case DELETE : ret=delete();
                break;
            case JOIN : ret=join();
                break;
            default:
                ret = false;
        }
        //System.out.println("command type ret = " + ret);
        return ret;
    }

    public boolean command() {
        Token token = lex.getNextToken();
        if(debugging){
            System.out.println("in command, token type is: "+ token.getType());
        }
        if (commandType()) {
            if (expect(TokenType.SEMI_COL)) {
                return true;
            }
        }
        return false;
    }
}
