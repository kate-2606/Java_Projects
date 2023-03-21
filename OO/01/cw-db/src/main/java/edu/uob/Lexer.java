package edu.uob;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Stream;

//should be case-insensitive -- .equals case insensitive
//table names etc... can't have the same name as keywords
//select table.attribute test
//make a check that column names aren't the same
//write good error messages
//sort ugly function --- https://stackoverflow.com/questions/3316582/iterating-through-methods

//select sort by id high to low


//key persists --YES
//foriegn keys self generated?
//can you delete a database while in a different database --YES


//trim row strings
//replace lexer stuff with itterarators
//can other columns be labeled id?? -- ASSUMING NO
//LIKE only compares strings
//can't name a columns id or other columns


//what happens if the lexer fails?

//test pdf tests



/*
Any table names provided by the user should be converted into lowercase before saving out to the filesystem. You
should treat column names as case insensitive for querying, but you should preserve the case when storing them
(so that the user can make use of CamelCase if they wish).

select * from people; is equivalent to SELECT * FROM people;. This is true for all keywords in the BNF
(including TRUE/FALSE, AND/OR, LIKE etc.) In addition to this, all SQL keywords are reserved words, therefore you
should not allow them to be used as database/table/attribute names.
 */


public class Lexer {


    public void Lexer(String inpCommand, ArrayList<Token> tokenList){
        tokens=tokenList;
        setCommand(inpCommand);
        setup();
    }

    ArrayList<Token> tokens;

    String storageFolderPath = Paths.get("databases").toAbsolutePath().toString();

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
                    "ADD","NULL", "(", ")", " ", ",", ";",  "*", "="
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

    private String[] comparators = {"==", ">=", "<=", "!=", "<", ">"};

    private Boolean isComparator(String word){
        for (String c : comparators) {

            if (c.equals(word)) { return true; }
        }
        if(word.equals("LIKE")) { return true; }
        return false;
    }

    private Boolean isBoolOp(String word){
        if(word.equals("AND") || word.equals("OR")){
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

    private Boolean isBoolLit(String word){
        switch (word) {
            case "TRUE", "FALSE":
                break;
            default:
                return false;
        }
        return true;
    }

    private Character[] symbols = { '!', '#', '$', '%','&', '(', ')', '*', '+', ',', '-','.', '/',':',
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

        if(matcher.find()){
            return true;
        }
        return false;
    }

    public static Boolean isIntLit(String word){
        Pattern patter = Pattern.compile("^[+-]?([0-9]+$)");
        Matcher matcher = patter.matcher(word);

        if(matcher.find()){
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

    private String[] baseChars = {"(",")",",",";","=","!"};

    private ArrayList<String> specialChars = new ArrayList<>();

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
