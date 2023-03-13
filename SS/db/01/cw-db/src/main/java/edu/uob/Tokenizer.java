package edu.uob;

import java.util.*;

public class Tokenizer {

    String query = null;
    String[] specialCharacters = {"(",")",",",";"};

    //maybe call something else?
    ArrayList<String> words = new ArrayList<>();

    ArrayList<Token> tokens = new ArrayList<>();

    int tokenNumber = 0;

    public void tokenizer(String queryStr){
        query=queryStr;
        setup();
    }

    public String getToken(int tokenNumber){
        return words.get(tokenNumber);
    }


    private String[] tokenstring =
            {"USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "JOIN", "DATABASE",
                    "TABLE", "VALUES", "INTO", "UPDATE", "FROM", "SET", "WHERE", "ON",
                    "ADD", "(", ")", ",", " ", ";"};

    public void setTokenTypeAndValue (Boolean next){

        String word = getToken(tokenNumber);
        Token curToken = new Token();
        int i;

        for (i=0; i<tokenstring.length; i++ ){
            if(word==tokenstring[i]){
                curToken.setTokenType(i);
                tokens.add(curToken);
                return ;
            }
        }
        if (isTokenComparator(word)){
            curToken.setTokenType(i+1);
            curToken.setTokenValue(word);
            tokens.add(curToken);
            return ;
        }
        /*
        if(tokenIsLetter(token)){
            return tokenTicket.LETTER.ordinal();
        }
        if (isDigit(token)) {
        }
        }
        return -1;

         */
        return ;
    }


    private Boolean isTokenComparator(String token){
        String[] comparators = new String[] { "==", ">", "<", ">=", "<=", "!=", "LIKE"};
        for(int i=0; i<comparators.length; i++){
            if (token == comparators[i]){
                return true;
            }
        }
        return false;
    }

    private Boolean isTokenSymbol (String token){
        Character[] comparators = new Character[] { '!', '#', '$', '%',
            '&', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '>',
            '=', '<', '?', '@', '[', '\\', ']', '^', '`', '{', '}', '~'
        };

        for(int i=0; i<comparators.length; i++){
            if ((token.length()==1) && (token.charAt(0)==comparators[i])){
              return true;
            }
        }
    return false;
    }

    private Boolean isTokenDigit (String token) {
        return true;
    }


    private Boolean isUppercase(String token){
        if (token.length()==1 && token.charAt(0)>='A' && token.charAt(0)<='Z'){
            return true;
        }
        return false;
    }


    private Boolean isLowercase(String token){
        if (token.length()==1 && token.charAt(0)>='a' && token.charAt(0)<='z'){
            return true;
        }
        return false;
    }

    private Boolean tokenIsLetter(String token){
        if (token.length()==1){
            if((token.charAt(0)>='A' && token.charAt(0)<='Z') || (token.charAt(0)>='a' && token.charAt(0)<='z')){
                return true;
            }
        }
        return false;
    }




    void setup()
    {
        query = query.trim();
        String[] fragments = query.split("'");
        for (int i=0; i<fragments.length; i++) {
            if (i%2 != 0) words.add("'" + fragments[i] + "'");
            else {
                String[] nextBatchOfTokens = tokenise(fragments[i]);
                words.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }
    }

    String[] tokenise(String input)
    {
        for(int i=0; i<specialCharacters.length ;i++) {
            input = input.replace(specialCharacters[i], " " + specialCharacters[i] + " ");
        }
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        input = input.trim();
        return input.split(" ");
    }


}
