package edu.uob;

import java.util.*;

public class LexicalAnalyser {

    String query = null;
    String[] specialCharacters = {"(",")",",",";"};

    ArrayList<String> words = new ArrayList<>();

    private ArrayList<Token> tokens = new ArrayList<>();

    private static int tokenNumber = 0;

    public String getWord(int tokenNumber){
        return words.get(tokenNumber);
    }

    public void setQuery(String inpQuery){
        words.clear();
        tokens.clear();
        query=inpQuery;
        tokenNumber=0;

    }

    private String[] tokenTypeStrings =
            {"USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "JOIN", "DATABASE",
                    "TABLE", "VALUES", "INTO", "UPDATE", "FROM", "SET", "WHERE", "ON",
                    "ADD", "(", ")", ",", " ", ";", "NULL"};

    public Token getNextToken (){

        Token curToken = new Token();
        int i;
        int increment=0;
        String word=words.get(tokenNumber);

        for (i=0; i<tokenTypeStrings.length; i++ ){
            if(Objects.equals(word,tokenTypeStrings[i])){
                curToken.setTokenType(i);
                tokens.add(curToken);
                tokenNumber++;
                return curToken;
            }
        }
        if (isTokenComparator(word)){
            curToken.setTokenType(i+increment);
            curToken.setTokenValue(word);
            tokens.add(curToken);
            tokenNumber++;
            return curToken;
        }
        increment ++;

        if (isTokenBoolOp(word)){
            curToken.setTokenType(i+increment);
            curToken.setTokenValue(word);
            tokens.add(curToken);
            tokenNumber++;
            return curToken;
        }
        increment ++;
        if (isTokenStringLit(word)){
            curToken.setTokenType(i+increment);
            curToken.setTokenValue(word.substring(0,word.length()-1));
            tokens.add(curToken);
            tokenNumber++;
            return curToken;
        }
        increment ++;
        if (isTokenBoolLit(word)){
            curToken.setTokenType(i+increment);
            curToken.setTokenValue(word);
            tokens.add(curToken);
            tokenNumber++;
            return curToken;
        }
        increment ++;
        if (isTokenFloatOrIntLit(word, false)){
            curToken.setTokenType(i+increment);
            curToken.setTokenValue(word);
            tokens.add(curToken);
            tokenNumber++;
            return curToken;
        }
        increment ++;
        if (isTokenFloatOrIntLit(word, true)){
            curToken.setTokenType(i+increment);
            curToken.setTokenValue(word);
            tokens.add(curToken);
            tokenNumber++;
            return curToken;
        }
        increment ++;
        if (isTokenPlainText(word)){
            curToken.setTokenType(i+increment);
            curToken.setTokenValue(word);
            tokens.add(curToken);
            tokenNumber++;
            return curToken;
        }
        increment ++;
        if ((word.length()==1) && (word.charAt(0)=='*')){
            curToken.setTokenType(i+increment);
            tokens.add(curToken);
            tokenNumber++;
            return curToken;
        }

        return null;
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

    private Boolean isTokenBoolOp(String word){
        switch (word) {
            case "AND":
            case "OR":
                break;
            default:
                return false;
        }
        return true;
    }

    private Boolean isTokenStringLit(String word) {

        if ((word.charAt(word.length()-1)) == '\''&& (word.charAt(0) == '\'')) {
            String value = word.substring(1, word.length()-1);

            for (int i = 0; i < value.length(); i++) {
                if (!(isCharSymbol(value.charAt(i))) && !(isCharLetterOrDigit(value.charAt(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private Boolean isTokenBoolLit(String word){
        switch (word) {
            case "TRUE":
            case "FALSE":
                break;
            default:
                return false;
        }
        return true;
    }

    private Boolean isCharSymbol (Character symbol){
        Character[] comparators = new Character[] { '!', '#', '$', '%',
            '&', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '>',
            '=', '<', '?', '@', '[', '\\', ']', '^', '`', '{', '}', '~'
        };

        for(int i=0; i<comparators.length; i++){
            if (symbol==comparators[i]){
              return true;
            }
        }
    return false;
    }

    private Boolean isCharLetterOrDigit (Character c) {
        return (c>='0' && c<='9') || (c>='A' && c<='Z') || (c>='a' && c<='z');
    }

    // do this smarter with .contains
    private Boolean isTokenFloatOrIntLit (String word, Boolean isInt){

        int decimals = 0;
        if (Character.isDigit(word.charAt(0)) || word.charAt(0)=='+' || word.charAt(0)=='-'){
            int x = Character.isDigit(word.charAt(0)) ? 0:1;

            for (int i=x; i<word.length(); i++){
                if(!(Character.isDigit(word.charAt(i))) && (word.charAt(i)!='.')){
                   return false;
                }
                if (word.charAt(i)=='.'){
                    decimals++;
                }
            }
            if (!isInt && decimals!=1) { return false; }
            if(isInt && decimals!=0){
                return false;
            }
            return true;

        }
        return false;
    }

    private Boolean isTokenPlainText(String word){
        for(int i=0; i<word.length(); i++){
            if(!(isCharLetterOrDigit(word.charAt(i)))){
                return false;
            }
        }
        return true;
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
