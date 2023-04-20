package edu.uob;

import java.io.IOException;
import static edu.uob.TokenType.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Stream;


public class Lexer {


    public void initiate(String inpCommand, ArrayList<Token> tokenList){
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

    private final String[] tokenTypeStrings =
            {"USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "JOIN", "DATABASE",
                    "TABLE", "INTO", "UPDATE", "DELETE", "FROM", "SET", "WHERE", "ON", "VALUES",
                    "ADD","NULL", "(", ")", " ", ",", ";",  "*", "="
            };



    public Token getNextToken() throws IOException {
        if(wordIndex==words.size()-1){
            throw new IOException("Parser failed before reaching a ';'");
        }
        wordIndex++;

        Token curToken = new Token();
        String word = words.get(wordIndex);

        int i=0;
        int increment=0;

        for (String s : tokenTypeStrings){
            if(s.equalsIgnoreCase(word)){
                curToken.setType(i);
                if(curToken.getType()==NULL){
                    curToken.setValue("NULL");
                }
                tokens.add(curToken);
                return curToken;
            }
            i++;
        }

        if (isComparator(word)){  return setToken(i+increment, word.toUpperCase()); }
        increment++;

        if (isBoolOp(word)){  return setToken(i+increment, word.toUpperCase()); }
        increment++;

        if (isBoolLit(word)){  return setToken(i+increment, word.toUpperCase()); }
        increment++;

        if (isFloatLit(word)){  return setToken(i+increment, word.toUpperCase()); }
        increment++;

        if (isIntLit(word)){  return setToken(i+increment, word.toUpperCase()); }

        return getNextTextToken();
    }


    public Token getNextTextToken() {

        String word = words.get(wordIndex);
        int increment = STRING_LIT.ordinal();
        if (isStringLit(word)){
            return setToken(increment, word.substring(1,word.length()-1));
        }
        increment ++;
        if (isPlainText(word)){
            return setToken(increment, word);
        }
        increment ++;
        if (isAttributeName(word)){
            return setToken(increment, word);
        }
        return null;
    }

    private Token setToken(int tokenNum, String value){
        Token curToken = new Token();
        curToken.setType(tokenNum);
        curToken.setValue(value);
        tokens.add(curToken);
        return curToken;
    }


    private final String[] comparators = {"==", ">=", "<=", "!=", "<", ">"};

    private Boolean isComparator(String word){
        for (String c : comparators) {

            if (c.equals(word)) { return true; }
        }
        return word.equalsIgnoreCase("LIKE");
    }

    private Boolean isBoolOp(String word){
        if(word.equalsIgnoreCase("AND")) {
            words.remove(wordIndex);
            words.add(wordIndex, "AND");
            return true;
        }

        if(word.equalsIgnoreCase("OR")){
            words.remove(wordIndex);
            words.add(wordIndex,"OR");
            return true;
        }
        return false;
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

    public static Boolean isBoolLit(String word){
        switch (word.toUpperCase()) {
            case "FALSE", "TRUE":
                break;
            default:
                return false;
        }
        return true;
    }

    private final Character[] symbols = { '!', '#', '$', '%','&', '(', ')', '*', '+', ',', '-','.', '/',':',
            ';', '>', '=', '<', '?', '@', '[', '\\',']', '^', '`', '{', '}', '~'};

    private Boolean isCharSymbol (Character symbol){
        for (Character c : symbols) {
            if (c.equals(symbol)) { return true; }
        }
        return false;
    }

    private Boolean isCharLetterOrDigit (Character c) {
        return ((c>='0' && c<='9') || (c>='A' && c<='Z') || (c>='a' && c<='z'));
    }

    // do this smarter with .contains
    public static Boolean isFloatLit(String word){
        Pattern patter = Pattern.compile("^[+-]?([0-9]+[.][0-9]+$)");
        Matcher matcher = patter.matcher(word);

        return matcher.find();
    }

    public static Boolean isIntLit(String word){
        Pattern patter = Pattern.compile("^[+-]?([0-9]+$)");
        Matcher matcher = patter.matcher(word);

        return matcher.find();
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
            return isPlainText(frag1) && isPlainText(frag2);
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

    private final String[] baseChars = {"(",")",",",";","=","!"};

    private final ArrayList<String> specialChars = new ArrayList<>();

    private void getSpecialCharacters(){
        specialChars.clear();

        String[] splitters =
                Stream.concat(Arrays.stream(baseChars), Arrays.stream(comparators)).toArray(String[]::new);

        Collections.addAll(specialChars, splitters);
        specialChars.add("AND");
        specialChars.add("OR");
    }


    private String[] tokenise(String input)
    {
        getSpecialCharacters();
        for(int i=0; i<specialChars.size() ;i++) {
            input = input.replace(specialChars.get(i), " " + specialChars.get(i) + " ");
        }
        while (input.contains(">  =") || input.contains("<  =") || input.contains("=  =") || input.contains("!  =")){
            input = input.replace(">  =", ">=");
            input = input.replace("<  =", "<=");
            input = input.replace("!  =", "!=");
            input = input.replace("=  =", "==");
        }
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        input = input.trim();
        return input.split(" ");
    }
}
