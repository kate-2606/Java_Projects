package edu.uob;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Stream;


//use pattern.match() functions (reg expressions)
//should be case-insensitive
//table names etc... can't have the same name as keywords
//tokeniser should split WHERE age<40 into 4 tokens, currently it would be two tokens --- DONE
//unsigned long for key
//write good error messages
//sort ugly function --- https://stackoverflow.com/questions/3316582/iterating-through-methods


//key persists?
//foriegn keys self generated?
//tokeniser change special chars to strings
//when to write to table?
//parse a wordIndex then interpret?
//can you create a databases in the directory? Or they have to be in a database?
//can you delete a database while in a different database?


//boolean upper or lkowercase?
//replace lexer stuff with itterarators
//sort the spaces either side of like



public class Lexer {

    public void Lexer(String inpCommand, ArrayList<Token> tokenList){
        tokens=tokenList;
        setCommand(inpCommand);
        setup();
    }

    ArrayList<Token> tokens;

    String command = null;

    ArrayList<String> words = new ArrayList<>();

    private int wordIndex;


    public String getWord(int wordIndex){
        return words.get(wordIndex);
    }

    public void setCommand(String inpCommand){
        words.clear();
        command=inpCommand;
        wordIndex=-1;
    }

    public boolean isWordListEnd() {
        if(wordIndex == words.size()-1){
        return true;
        }
        return false;
    }

    private String[] tokenTypeStrings =
            {"USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "JOIN", "DATABASE",
                    "TABLE", "INTO", "UPDATE", "DELETE", "FROM", "SET", "WHERE", "ON", "VALUES",
                    "ADD", "(", ")", " ", ",", ";", "NULL", "*", "="
            };


    public Token getNextToken() throws IOException {
        if(wordIndex==words.size()-1){
            throw new IOException("Parser failed before reaching a ';'");
        }
        wordIndex++;

        Token curToken = new Token();
        String word = words.get(wordIndex);

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
        if (isAttributeName(word)){
            curToken.setType(i+increment);
            curToken.setValue(word);
            tokens.add(curToken);
            return curToken;
        }
        return null;
    }

    String[] comparators = {"==", ">", "<", ">=", "<=", "!="};

    private Boolean isComparator(String word){
        for (String c : comparators) {
            if (c.equals(word)) { return true; }
        }
        if(word.equals("LIKE")) { return true; }
        return false;
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
        Pattern patter = Pattern.compile("[+-]?([0-9]+[.]+[0-9]+)");
        Matcher matcher = patter.matcher(word);

        if(matcher.find()){
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


    public void setup()
    {
        words.clear();
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

    String[] baseChars = {"(",")",",",";"};

    ArrayList<String> specialChars = new ArrayList<>();

    private void getSpecialCharacters(){
        specialChars.clear();

        String[] splitters =
                Stream.concat(Arrays.stream(baseChars), Arrays.stream(comparators)).toArray(String[]::new);

        for(String s : splitters){
            specialChars.add(s);
        }
        specialChars.add("AND");
        specialChars.add("OR");
    }


    private String[] tokenise(String input)
    {
        getSpecialCharacters();
        for(int i=0; i<specialChars.size() ;i++) {
            input = input.replace(specialChars.get(i), " " + specialChars.get(i) + " ");
        }
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        input = input.trim();
        return input.split(" ");
    }
}
