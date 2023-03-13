package edu.uob;

public class Token {
    public Token(){

    }

    public enum TokenType {
        USE, CREATE, DROP, ALTER, INSERT, SELECT, JOIN, DATABASE,
        //9
        TABLE, VALUES, INTO, UPDATE, FROM, SET, WHERE, ON,
        //17
        ADD, TRUE,  OPEN_BR, CLOSE_BR, SPACE, COMMA, SEMI_COL,
        //24
        COMPARATOR, BOOL_OP, STRING_LIT, BOOL_LIT, FLOAT_LIT, INT_LIT,
        NULL, PLAIN_TXT, WILD_CRD
    }

    private TokenType type;

    private String value;

    private Integer valueInt;

    public void setTokenType(int position){ type = TokenType.values()[position]; }

    public void setTokenValue(String inpValue){ value = inpValue; }

    public String tokenTypeToString(){
        return type.toString();
    }


}
