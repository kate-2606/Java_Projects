package edu.uob;

import java.io.IOException;
import java.util.*;

import static edu.uob.TokenType.*;

public class Parser {
    public void Parser(ArrayList<Token> tokenList, Lexer inpLex, InterpContext ic) {
        lex=inpLex;
        tokens=tokenList;
        try {
            parsedOkay = command();
            if(!parsedOkay){
                ic.setResult("[ERROR]\nFailed to parse command");
            }
        } catch(Exception e){
            ic.setResult("[ERROR] " +e.getMessage() + "\n");
        }
    }

    private Lexer lex;

    private ArrayList<Token> tokens;

    private boolean parsedOkay;

    public boolean getParserResult(){ return parsedOkay; }


    public boolean acceptToken(TokenType t) throws IOException{
        Token tok = getCurrentToken();
        if (tok.getType()==t){
            if (t != SEMI_COL) {
                tok = lex.getNextToken();
            }
            return true;
        }
        return false;
    }


    private boolean expect(TokenType t) throws IOException{
        if (acceptToken(t)){
            return true;
        }
        return false;
    }


    public Token getCurrentToken() {return tokens.get(tokens.size()-1); }


    private boolean nameValueList() throws IOException{
        if(nameValuePair()){
            if(acceptToken(COMMA)){
                return nameValueList();
            }
            return true;
        }
        Token token = getCurrentToken();
        throw new IOException("Was expecting NameValuePair(s)");
    }


    private boolean acceptPlainTxt() throws IOException {
        Token token = getCurrentToken();
        if(token.getType()==INT_LIT){
            token.changeType(PLAIN_TXT);
        }
        if(acceptToken(PLAIN_TXT)){
            return true;
        }
        return false;
    }


    private boolean nameValuePair() throws IOException{
        if(attributeName()){
            if(acceptToken(EQUALS)){
                if(value()){
                    return true;
                }
            }
        }
        Token token = getCurrentToken();
        throw new IOException("Was expecting EQUALS in NameValuePair");
    }


    private boolean condition() throws IOException{
        if (attributeName()) {
            if (acceptToken(COMPARATOR)) {
                if (value()) {
                    if (acceptToken(BOOL_OP)) {
                        return condition();
                    }
                    return true;
                }
            }
        }
        if (acceptToken(OPEN_BR)) {
            if (condition()) {
                if (acceptToken(CLOSE_BR)) {
                    if (acceptToken(BOOL_OP)) {
                        return condition();
                    }
                    return true;
                }
            }
        }
        throw new IOException("Failed to parse condition statement");
    }


    private boolean wildAttribList() throws IOException{
        if(attributeList() || acceptToken(WILD_CRD)){
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
            if(acceptToken(COMMA)){
                ret = valueList();
            }
        }
        if(ret){
            return ret;
        }
        throw new IOException("Encountered an invalid ValueList");
    }


    private boolean attributeName() throws IOException{
        if(acceptPlainTxt() || acceptToken(ATTRIB_NAME)){
            return true;
        }

        return false;
    }


    private boolean attributeList() throws IOException{
        boolean ret = false;
        if (attributeName()){
            ret=true;
            if(acceptToken(COMMA)){
                ret = attributeList();
            }
        }
        return ret;
    }


    private boolean join() throws IOException{
        lex.getNextToken();
        if(!acceptPlainTxt()){ return false; }

        if(!Objects.equals(getCurrentToken().getValue(),"AND")){ return false; }

        lex.getNextToken();
        if(!acceptPlainTxt()){ return false; }

        if(!acceptToken(ON)){ return false; }

        if(!attributeName()){ return false; }

        if(!Objects.equals(getCurrentToken().getValue(),"AND")){ return false; }

        lex.getNextToken();
        if(attributeName()){
            return true;
        }

        throw new IOException("JOIN syntax failed");
    }


    private boolean delete() throws IOException{
        lex.getNextToken();
        if(!acceptToken(FROM)){ return false; }

        if(!acceptPlainTxt()){ return false; }

        if(!acceptToken(WHERE)){ return false; }

        if(condition()){
            return true;
        }

        throw new IOException("DELETE syntax failed");
    }


    private boolean update() throws IOException{
        lex.getNextToken();
        if(!acceptPlainTxt()) { return false; }

        if(!acceptToken(SET)) { return false; }

        if(!nameValueList()){ return false; }

        if(!acceptToken(WHERE)) { return false; }

        if (condition()) {
            return true;
        }

        throw new IOException("UPDATE syntax failed");
    }


    private boolean select() throws IOException{
        boolean ret =false;
        lex.getNextToken();
        if(!wildAttribList()){ return false; }

        if(!acceptToken(FROM)){ return false; }

        if(acceptPlainTxt()){
            ret=true;
            if(acceptToken(WHERE)){
                ret=condition();
            }
        }
        if(!ret) {
            throw new IOException("SELECT syntax failed");
        }
        return ret;
    }


    private boolean insert() throws IOException{
        lex.getNextToken();
        if(!acceptToken(INTO)){ return false; }

        if (!acceptPlainTxt()){ return false; }

        if(!acceptToken(VALUES)){ return false; }

        if(!acceptToken(OPEN_BR)){ return false; }

        if(valueList()){
            return expect(CLOSE_BR);
        }
        throw new IOException("INSERT syntax failed");
    }


    private boolean alter() throws IOException{
        boolean ret =false;
        lex.getNextToken();
        if(acceptToken(TABLE)){
            if(acceptPlainTxt()){
                if (acceptToken(DROP) || acceptToken(ADD)){
                    return attributeName();
                }
            }
        }
        throw new IOException("ALTER syntax failed");
    }


    private boolean drop() throws IOException{
        lex.getNextToken();
        if(acceptToken(TABLE) || acceptToken(DATABASE)){
            return acceptPlainTxt();
        }
        throw new IOException("DROP syntax failed");
    }


    private boolean create() throws IOException{
        boolean ret =false;
        lex.getNextToken();
        if(expect(TABLE) || expect(DATABASE)) {
            if (acceptPlainTxt()) {
                ret = true;
                if (!(expect(SEMI_COL)) && expect(OPEN_BR)){
                    if (attributeList()) {
                        return expect(CLOSE_BR);
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
        lex.getNextToken();
        if(acceptPlainTxt()){
            return true;
        }
        throw new IOException("USE syntax failed");
    }


    private boolean commandType() throws IOException{
        Token token = getCurrentToken();
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
        lex.getNextToken();
        if (commandType()) {
            if (expect(TokenType.SEMI_COL) && lex.isWordListEnd()) {
                return true;
            }
        }
        return false;
    }
}
