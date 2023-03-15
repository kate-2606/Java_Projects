package edu.uob;

import java.util.*;

public class Parser {
    // Token Type
    public void Parser(ArrayList<Token> tokenList, Lexer inpLex){
        lex=inpLex;
        tokens=tokenList;
        parsedOkay= command();
    }

    private Lexer lex;

    private ArrayList<Token> tokens;
    private boolean debugging = false;

    private boolean parsedOkay;

    public boolean getParserResult(){ return parsedOkay; }

    public enum TokenType {
        USE, CREATE, DROP, ALTER, INSERT, SELECT, JOIN, DATABASE,
        //10
        TABLE, INTO, UPDATE, DELETE, FROM, SET, WHERE, ON, VALUES,
        //20
        ADD, OPEN_BR, CLOSE_BR, SPACE, COMMA, SEMI_COL, NULL,
        //28
        WILD_CRD, EQUALS, COMPARATOR, BOOL_OP, STRING_LIT, BOOL_LIT,
        //34
        FLOAT_LIT, INT_LIT, PLAIN_TXT, ATTRIB_NAME
    }

    public boolean accept(TokenType t){
        Token tok = getCurrentToken();
        if (tok.getType()==t){
            if (t != TokenType.SEMI_COL) {
                tok = lex.getNextToken();
            }
            return true;
        }
        return false;
    }

    private boolean expect(TokenType t){
        if (accept(t)){
            return true;
        }
        return false;
    }

    public Token getCurrentToken() {return tokens.get(tokens.size()-1); }

    private boolean nameValueList(){
        Boolean ret = false;
        if(nameValuePair()){
            if(accept(TokenType.COMMA)){
                return nameValueList();
            }
            ret=true;
        }
        return ret;
    }

    private boolean nameValuePair(){
        if(attributeName()){
            if(accept(TokenType.EQUALS)){
                if(value()){
                    return true;
                }
            }
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
        Token token = getCurrentToken();
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
                ret = valueList();
            }
        }
        return ret;
    }


    private boolean attributeName() {
        if(accept(TokenType.PLAIN_TXT) || accept(TokenType.ATTRIB_NAME)){
            return true;
        }
        return false;
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
        lex.getNextToken();
        if(accept(TokenType.PLAIN_TXT)){
            if(Objects.equals(getCurrentToken().getValue(),"AND")){
                lex.getNextToken();
                if(accept(TokenType.PLAIN_TXT)){
                    if(accept(TokenType.ON)){
                        if(attributeName()){
                            if(Objects.equals(getCurrentToken().getValue(),"AND")){
                                lex.getNextToken();
                                if(attributeName()){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean delete() {
        lex.getNextToken();
        if(accept(TokenType.FROM)){
            if(accept(TokenType.PLAIN_TXT)){
                if(accept(TokenType.WHERE)){
                    if(condition()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean update() {
        lex.getNextToken();
        if(accept(TokenType.PLAIN_TXT)) {
            if(accept(TokenType.SET)) {
                if(nameValueList()){
                    if(accept(TokenType.WHERE)) {
                        if (condition()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
        Token token =lex.getNextToken();
        if(debugging){
            System.out.println("in insert, token type is: "+ token.getType());
        }
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
        Token token = getCurrentToken();
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
