package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class JoinCommand extends Interpreter{

    public JoinCommand(InterpContext context, ArrayList<Token> tokens) throws InterpreterException, IOException {
        super(context, tokens);
    }


    public void interpretCommand() throws InterpreterException {
        String tableName=getName();
        System.out.println(tableName);
        Table tableA = context.getWorkingDatabase().getTableByName(tableName);
        getNextToken();
        tableName=getName();
        System.out.println(tableName);
        Table tableB = context.getWorkingDatabase().getTableByName(tableName);
        getNextToken();
        String attributeA=getName();
        getNextToken();
        String attributeB=getName();

        if(getName().contains(tableA.getName())){
            attributeA=getName();
            getNextToken();
            attributeB=getName();
        }
        if(getName().contains(tableB.getName())){
            attributeB=getName();
            getNextToken();
            attributeA=getName();
        }
        checkAttributeName(attributeA, tableA.getName());
        checkAttributeName(attributeB, tableB.getName());

        context.setResult(joinHelper(tableA, tableB, attributeA, attributeB));
    }

    private String joinHelper(Table tableA, Table tableB, String attributeA, String attributeB)
            throws InterpreterException {
        ArrayList<Long[]> joinMap = new ArrayList<>();
        HashMap<Long, Row> mapA = tableA.getDataRows();
        HashMap<Long, Row> mapB = tableB.getDataRows();
        String cellA = "";  String cellB = "";
        int posA = tableA.getAttributePosition(attributeA)-1;
        int posB = tableB.getAttributePosition(attributeB)-1;

        for(Long l : mapA.keySet()){
            System.out.println("key here");
            cellA=mapA.get(l).getCellDataByNumber(posA);
            for(Long k : mapB.keySet()){
                cellB=mapB.get(k).getCellDataByNumber(posB);
                System.out.println(cellA + " " + cellB);
                if(cellA.equals(cellB)){
                    Long[] match = {l, k};
                    joinMap.add(match);
                    System.out.println("match " + match);
                }
            }
        }
        return joinMerge(tableA, tableB, attributeA, attributeB, joinMap);
    }



    private String joinMerge(Table tableA, Table tableB, String attributeA, String attributeB, ArrayList<Long[]> map)
            throws InterpreterException {
        String result = "";
        System.out.println("here");
        for(Long[] l : map){
            System.out.println("here1");
            long keyA = l[0];
            int i=0;
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(keyA));
            while (i<tableA.getNumberOfAttributes()-1){
                if(i!=tableA.getAttributePosition(attributeA)-1)
                    result = result + tableA.getRow(keyA).getCellDataByNumber(i) + "\t";
                i++;
            }
            long keyB = l[0];
            i=0;
            while (i<tableB.getNumberOfAttributes()-1){
                if(i!=tableB.getAttributePosition(attributeB)-1)
                    result = result + tableB.getRow(keyB).getCellDataByNumber(i) + "\t";
                i++;
            }
            result = result.trim() + "\n";
        }
        return result;
    }
}
