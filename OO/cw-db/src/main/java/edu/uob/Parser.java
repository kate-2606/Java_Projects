package edu.uob;

import java.io.IOException;
import java.util.*;

public class Parser {
    // Token Type
    public void Parser(ArrayList<Token> tokenList, Lexer inpLex) {
        lex=inpLex;
        tokens=tokenList;
        try {
            parsedOkay = command();
        } catch(Exception e){
            // this will catch all the parser exceptions and do whatever with the message
            System.out.println(e.getMessage());
        }
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
        ADD, NULL, OPEN_BR, CLOSE_BR, SPACE, COMMA, SEMI_COL,
        //28
        WILD_CRD, EQUALS, COMPARATOR, BOOL_OP, STRING_LIT, BOOL_LIT,
        //34
        FLOAT_LIT, INT_LIT, PLAIN_TXT, ATTRIB_NAME
    }

    public boolean accept(TokenType t) throws IOException{
        Token tok = getCurrentToken();
        if (tok.getType()==t){
            if (t != TokenType.SEMI_COL) {
                tok = lex.getNextToken();
            }
            return true;
        }
        return false;
    }

    private boolean expect(TokenType t) throws IOException{
        if (accept(t)){
            return true;
        }
        return false;
    }

    public Token getCurrentToken() {return tokens.get(tokens.size()-1); }

    private boolean nameValueList() throws IOException{
        if(nameValuePair()){
            if(accept(TokenType.COMMA)){
                return nameValueList();
            }
            return true;
        }
        Token token = getCurrentToken();
        throw new IOException("Was expecting NameValuePair(s)");
    }

    private boolean acceptPlainTxt() throws IOException {
        Token token = getCurrentToken();
        if(token.getType()==TokenType.INT_LIT){
            token.changeType(TokenType.PLAIN_TXT);
        }
        if(accept(TokenType.PLAIN_TXT)){
            return true;
        }
        return false;
    }

    private boolean nameValuePair() throws IOException{
        if(attributeName()){
            if(accept(TokenType.EQUALS)){
                if(value()){
                    return true;
                }
            }
        }
        Token token = getCurrentToken();
        throw new IOException("Was expecting EQUALS instead of " + token.getType().toString() + " in NameValuePair");
    }


    private boolean condition() throws IOException{
        Token token =getCurrentToken();
        if(debugging){
            System.out.println("in condition, token type is: "+ token.getType());
        }
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
        throw new IOException("Failed to parse condition statement");
    }

    private boolean wildAttribList() throws IOException{
        Token token =getCurrentToken();
        if(debugging){
            System.out.println("in wildcard, token type is: "+ token.getType());
        }
        if(attributeList() || accept(TokenType.WILD_CRD)){
            return true;
        }
        throw new IOException("Was expecting AttributeList or '*'");
    }

    private boolean value() throws IOException {
        Token token = getCurrentToken();
        switch (token.getType()) {
            case STRING_LIT:
            case BOOL_LIT:
            case FLOAT_LIT:
            case INT_LIT:
            case NULL:
                break;
            default:
                throw new IOException("Encountered an invalid VALUE '" + token.getValue()+"' " + token.getType());
        }
        lex.getNextToken();
        return true;
    }


    private boolean valueList() throws IOException{
        boolean ret = false;
        if (value()){
            ret=true;
            if(accept(TokenType.COMMA)){
                ret = valueList();
            }
        }
        if(ret){
            return ret;
        }
        throw new IOException("Encountered an invalid ValueList");
    }


    private boolean attributeName() throws IOException{
        if(acceptPlainTxt() || accept(TokenType.ATTRIB_NAME)){
            return true;
        }

        return false;
    }

    private boolean attributeList() throws IOException{
        boolean ret = false;
        if (attributeName()){
            ret=true;
            if(accept(TokenType.COMMA)){
                ret = attributeList();
            }
        }
        return ret;
    }

    private boolean join() throws IOException{
        lex.getNextToken();
        if(acceptPlainTxt()){
            if(Objects.equals(getCurrentToken().getValue(),"AND")){
                lex.getNextToken();
                if(acceptPlainTxt()){
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
        throw new IOException("JOIN syntax failed");
    }

    private boolean delete() throws IOException{
        lex.getNextToken();
        if(accept(TokenType.FROM)){
            if(acceptPlainTxt()){
                if(accept(TokenType.WHERE)){
                    if(condition()){
                        return true;
                    }
                }
            }
        }
        throw new IOException("DELETE syntax failed");
    }

    private boolean update() throws IOException{
        lex.getNextToken();
        if(acceptPlainTxt()) {
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
        throw new IOException("UPDATE syntax failed");
    }

    private boolean select() throws IOException{
        boolean ret =false;
        lex.getNextToken();
        if(wildAttribList()){
            if(accept(TokenType.FROM)){
                if(acceptPlainTxt()){
                    ret=true;
                    if(accept(TokenType.WHERE)){
                        ret=condition();
                    }
                }
            }
        }
        if(!ret) {
            throw new IOException("SELECT syntax failed");
        }
        return ret;
    }

    private boolean insert() throws IOException{
        Token token =lex.getNextToken();
        if(debugging){
            System.out.println("in insert, token type is: "+ token.getType());
        }
        if(accept(TokenType.INTO)){
            if (acceptPlainTxt()){
                if(accept(TokenType.VALUES)){
                    if(accept(TokenType.OPEN_BR)){
                        if(valueList()){
                            return expect(TokenType.CLOSE_BR);
                        }
                    }
                }
            }
        }
        throw new IOException("INSERT syntax failed");
    }

    private boolean alter() throws IOException{
        boolean ret =false;
        lex.getNextToken();
        if(accept(TokenType.TABLE)){
            if(acceptPlainTxt()){
                if (accept(TokenType.DROP) || accept(TokenType.ADD)){
                    return attributeName();
                }
            }
        }
        throw new IOException("ALTER syntax failed");
    }

    private boolean drop() throws IOException{
        lex.getNextToken();
        if(accept(TokenType.TABLE) || accept(TokenType.DATABASE)){
            return acceptPlainTxt();
        }
        throw new IOException("DROP syntax failed");
    }

    private boolean create() throws IOException{
        boolean ret =false;
        lex.getNextToken();
        if(expect(TokenType.TABLE) || expect(TokenType.DATABASE)) {
            if (acceptPlainTxt()) {
                ret = true;
                if (!(expect(TokenType.SEMI_COL)) && expect(TokenType.OPEN_BR)){
                    if (attributeList()) {
                        return expect(TokenType.CLOSE_BR);
                    }
                }
            }
        }
        if(ret){
            return ret;
        }
        throw new IOException("CREATE syntax failed");
    }

    private boolean use() throws IOException{
        Token token = lex.getNextToken();
        boolean ret=false;
        if(debugging){
            System.out.println("in commandType, token type is: "+ token.getType());
        }
        if(acceptPlainTxt()){
            return true;
        }
        throw new IOException("USE syntax failed");
    }


    private boolean commandType() throws IOException{
        Token token = getCurrentToken();
        if(debugging){
            System.out.println("in commandType, token type is: "+ token.getType().toString());
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
        return ret;
    }


    public boolean command() throws IOException{
        Token token = lex.getNextToken();
        if(debugging){
            System.out.println("in command, token type is: "+ token.getType());
        }
        if (commandType()) {
            if (expect(TokenType.SEMI_COL) && lex.isWordListEnd()) {
                return true;
            }
        }
        return false;
    }
}
