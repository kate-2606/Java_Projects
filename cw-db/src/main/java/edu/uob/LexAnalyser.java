package edu.uob;

import java.util.*;

//use pattern.match() functions (reg expressions)
//should be case insensitive
//table names etc.. can't have the same name as keywords
//tokeniser should split WHERE age<40 into 4 tokens, currently it would be two tokens
//unsigned long for keys
//key persists?
//foriegn keys self generated?
//tokeniser change special chars to strings
//when to write to table?


public class LexAnalyser {

    public void LexAnalyser(String inpCommand){
        setCommand(inpCommand);
        setup();
        /*
        for(int i=0; i<words.size(); i++){
            System.out.println("Word " + i + " = " + words.get(i));
        }

         */
    }

    String command = null;

    ArrayList<String> words = new ArrayList<>();

    private ArrayList<Token> tokens = new ArrayList<>();

    private static int tokenNumber = 0;

    public String getWord(int tokenNumber){
        return words.get(tokenNumber);
    }

    public void setCommand(String inpCommand){
        words.clear();
        tokens.clear();
        command=inpCommand;
        tokenNumber=-1;
    }

    public Token getCurrentToken() {return tokens.get(tokenNumber); }

    private String[] tokenTypeStrings =
            {"USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "JOIN", "DATABASE",
                    "TABLE", "INTO", "UPDATE", "DELETE", "FROM", "SET", "WHERE", "ON", "VALUES",
                    "ADD", "(", ")", " ", ",", ";", "NULL", "*", "="
            };



    public Token getNextToken (){

        tokenNumber++;

        Token curToken = new Token();
        String word = words.get(tokenNumber);

        int i;
        int increment=0;

        for (i=0; i<tokenTypeStrings.length; i++ ){
            if(Objects.equals(word,tokenTypeStrings[i])){
                curToken.setType(i);
                tokens.add(curToken);
                return curToken;
            }
        }

        if (isComparator(word)){
            curToken.setType(i+increment);
            curToken.setValue(word);
            tokens.add(curToken);
            return curToken;
        }
        increment ++;

        if (isBoolOp(word)){
            curToken.setType(i+increment);
            curToken.setValue(word);
            tokens.add(curToken);
            return curToken;
        }
        increment ++;
        if (isStringLit(word)){
            curToken.setType(i+increment);
            curToken.setValue(word.substring(1,word.length()-1));
            tokens.add(curToken);
            return curToken;
        }
        increment ++;
        if (isBoolLit(word)){
            curToken.setType(i+increment);
            curToken.setValue(word);
            tokens.add(curToken);
            return curToken;
        }
        increment ++;
        if (isFloatLit(word)){
            curToken.setType(i+increment);
            curToken.setValue(word);
            tokens.add(curToken);
            return curToken;
        }
        increment ++;
        if (isIntLit(word)){
            curToken.setType(i+increment);
            curToken.setValue(word);
            tokens.add(curToken);
            return curToken;
        }
        increment ++;
        if (isPlainText(word)){
            curToken.setType(i+increment);
            curToken.setValue(word);
            tokens.add(curToken);
            return curToken;
        }


        increment ++;
        System.out.println("before att name "+ word);
        if (isAttributeName(word)){
            System.out.println("passed att name");
            curToken.setType(i+increment);
            curToken.setValue(word);
            tokens.add(curToken);
            return curToken;
        }
        return null;
    }


    private Boolean isComparator(String word){
        switch (word) {
            case "==":
            case ">":
            case "<":
            case ">=":
            case "<=":
            case "!=":
            case "LIKE":
                break;
            default:
                return false;
        }
        return true;
    }

    private Boolean isBoolOp(String word){
        switch (word) {
            case "AND":
            case "OR":
                break;
            default:
                return false;
        }
        return true;
    }

    private Boolean isStringLit(String word) {

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

    private Boolean isBoolLit(String word){
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

        switch (symbol) {
            case '!':
            case '#':
            case '$':
            case '%':
            case '&':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case ':':
            case ';':
            case '>':
            case '=':
            case '<':
            case '?':
            case '@':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '`':
            case '{':
            case '}':
            case '~':
            break;
            default:
                return false;
        }
        return true;
    }

    private Boolean isCharLetterOrDigit (Character c) {
        return (c>='0' && c<='9') || (c>='A' && c<='Z') || (c>='a' && c<='z');
    }

    // do this smarter with .contains
    private Boolean isFloatLit (String word){

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
            if(decimals!=1){
                return false;
            }
            return true;
        }
        return false;
    }

    private Boolean isIntLit (String word){

        if (Character.isDigit(word.charAt(0)) || word.charAt(0)=='+' || word.charAt(0)=='-'){
            int x = Character.isDigit(word.charAt(0)) ? 0:1;

            for (int i=x; i<word.length(); i++) {
                if (!(Character.isDigit(word.charAt(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Boolean isPlainText(String word){
        for(int i=0; i<word.length(); i++){
            if(!(isCharLetterOrDigit(word.charAt(i)))){
                return false;
            }
        }
        return true;
    }

    private Boolean isAttributeName(String word){
        if(word.contains(".")){
            String frag1 = word.substring(0,word.indexOf('.')-1);
            String frag2 = word.substring(word.indexOf('.')+1, word.length()-1);
            if(isPlainText(frag1) && isPlainText(frag2)){
                return  true;
            }
        }
        return false;
    }


    void setup()
    {
        //System.out.println(command);
        command = command.trim();
        String[] fragments = command.split("'");
        for (int i=0; i<fragments.length; i++) {
            if (i%2 != 0) words.add("'" + fragments[i] + "'");
            else {
                String[] nextBatchOfTokens = tokenise(fragments[i]);
                words.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }
    }

    String[] specialCharacters = {"(",")",",",";"};

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
