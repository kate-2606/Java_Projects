package edu.uob;

public class Token {
    public Token(){

    }


    private Parser.TokenType type;

    private String value;

    private Integer valueInt;

    public void setType(int position){ type = Parser.TokenType.values()[position]; }

    public void setValue(String inpValue){ value = inpValue; }

    public String getTokenValue(){ return value; }

    public String typeToString(){
        return type.toString();
    }


}
