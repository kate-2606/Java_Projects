package edu.uob;
import static edu.uob.TokenType.*;

import java.security.PublicKey;

public class Token {

    private TokenType type;

    private String value;

    public void setType(int position){ type = TokenType.values()[position]; }

    public void setValue(String inpValue){ value = inpValue; }

    public String getValue(){ return value; }

    public TokenType getType(){ return type; }

    public String typeToString(){
        return type.toString();
    }

    public void changeType(TokenType inpType) { this.type = inpType; }


}
