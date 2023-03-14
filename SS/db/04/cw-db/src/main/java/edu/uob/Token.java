package edu.uob;

public class Token {
    public Token(){

    }


    private Parser.TokenType type;

    private String value;

    private Integer valueInt;

    public void setType(int position){ type = Parser.TokenType.values()[position]; }

    public void setValue(String inpValue){ value = inpValue; }

    public String getValue(){ return value; }

    public Parser.TokenType getType(){ return type; }

    public String typeToString(){
        return type.toString();
    }


}
