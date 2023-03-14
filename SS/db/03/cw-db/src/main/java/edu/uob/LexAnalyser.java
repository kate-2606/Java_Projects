package edu.uob;

import java.util.*;

public class LexAnalyser {

    public void LexAnalyser(String inpCommand){
        setCommand(command);
        setup();;
    }

    String command = null;
    String[] specialCharacters = {"(",")",",",";"};

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
                    "TABLE", "VALUES", "INTO", "UPDATE", "FROM", "SET", "WHERE", "ON",
                    "ADD", "(", ")", ",", " ", ";", "NULL", "*"};

    private Runnable[] lexMethods = {() -> isComparator(), () -> isBoolOp(), () -> isStringLit(), () -> isBoolLit(),
            () -> isFloatLit(), () -> isIntLit(), () -> isPlainText() };

    public Token getNextToken (){

        tokenNumber++;

        Token curToken = new Token();

        String word=words.get(tokenNumber);

        for (int i=0; i<tokenTypeStrings.length; i++ ){
            if(Objects.equals(word,tokenTypeStrings[i])){
                curToken.setType(i);
                tokens.add(curToken);
                return curToken;
            }
        }
/*--------------Here, want to run through the lexMethods (^^) if one returns true then:
                curToken.setType(i);
                curToken.setValue(word);
                tokens.add(curToken);
                return curToken;

                The methos are for the 7 last enums in the Parser.TokenType enum
 */
        for (int i=0; i<7; i++){
            lexMethods[i].run();
        }
    }


    private Boolean isComparator(){
        String word=words.get(tokenNumber);
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

    private Boolean isBoolOp(){
        String word=words.get(tokenNumber);
        switch (word) {
            case "AND":
            case "OR":
                break;
            default:
                return false;
        }
        return true;
    }

    private Boolean isStringLit() {
        String word=words.get(tokenNumber);

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

    private Boolean isBoolLit(){
        String word=words.get(tokenNumber);
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
    private Boolean isFloatLit (){
        String word=words.get(tokenNumber);

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

    private Boolean isIntLit (){
        String word=words.get(tokenNumber);

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

    private Boolean isPlainText(){
        String word=words.get(tokenNumber);
        for(int i=0; i<word.length(); i++){
            if(!(isCharLetterOrDigit(word.charAt(i)))){
                return false;
            }
        }
        return true;
    }


    void setup()
    {
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
