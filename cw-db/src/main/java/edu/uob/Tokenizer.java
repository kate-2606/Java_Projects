package edu.uob;

import java.util.*;

public class Tokenizer {

    String query = null;
    String[] specialCharacters = {"(",")",",",";"};
    ArrayList<String> tokens = new ArrayList<String>();

    public void tokenizer(String queryStr){
        query=queryStr;
        setup();
    }

    public String getToken(int tokenNumber){
        return tokens.get(tokenNumber);
    }
// INSERT '32' WHERE
    void setup()
    {
        query = query.trim();
        String[] fragments = query.split("'");
        for (int i=0; i<fragments.length; i++) {
            if (i%2 != 0) tokens.add("'" + fragments[i] + "'");
            else {
                String[] nextBatchOfTokens = tokenise(fragments[i]);
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
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
