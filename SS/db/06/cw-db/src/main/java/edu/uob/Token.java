package edu.uob;

import java.security.PublicKey;

public class Token {

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

    public void changeType(Parser.TokenType inpType) { this.type = inpType; }


}
