package edu.uob;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.*;

import static java.lang.Float.parseFloat;


public class Table {
//use file separator instead
    public Table(String name, String databasePath) throws InterpreterException, IOException {
        tableName = name;
        this.keyPath=databasePath + File.separator + tableName + "Key.txt";
        getNextPrimaryKey(true);
    }

    private Attributes attributes;

    private HashMap<Long, Row> dataRows;

    private String keyPath;

    private String tableName;

    private long nextPrimaryKey;

    public String getName(){ return tableName; }

    public String getCellDataByNumber(int columnNumber, int rowNumber){
        Row currRow = dataRows.get(rowNumber);
        return currRow.getCellDataByNumber(columnNumber);
    }


    public long getNextPrimaryKey(Boolean peak) throws IOException, InterpreterException{
        File fileToOpen = new File(keyPath);
        if (!(fileToOpen.isDirectory()) && fileToOpen.exists()) {
            try {
                FileReader reader = new FileReader(fileToOpen);
                BufferedReader buffReader = new BufferedReader(reader);
                String line = buffReader.readLine();
                long ret = Long.valueOf(line);
                setNextPrimaryKey(peak);
                System.out.println("getNext " + ret);
                return ret;
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
        if(!fileToOpen.exists()){
            nextPrimaryKey=0;
            setNextPrimaryKey(peak);
            return nextPrimaryKey;
        }
        throw new InterpreterException.FailedToMakePrimaryKey();
    }

    private void setNextPrimaryKey(Boolean peak) throws IOException {

        File fileToOpen = new File(keyPath);

        new FileOutputStream(fileToOpen, false).close();
        FileWriter writer = new FileWriter(fileToOpen);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        if(!peak)
            nextPrimaryKey++;
        buffWriter.write(String.valueOf(nextPrimaryKey));
        buffWriter.flush();
        buffWriter.close();
    }


    public int getNumberOfDataRows() {
        if (dataRows!=null) {
            return dataRows.size();
        }
        else{
            return 0;
        }
    }

    public HashMap getDataRows() { return dataRows; }

    public Row getRow(long rowNumber) throws InterpreterException {
        for(Long i  : dataRows.keySet()){
            if (i==rowNumber)
                return dataRows.get(i);
        }
        throw new InterpreterException.AccessingNonExistentRow(rowNumber);
    }


    public int getNumberOfAttributes() { return attributes.getNumberOfAttributes(); }


    public void addRowFromCommand(boolean isAttributes, ArrayList<String> valuesList) throws InterpreterException, IOException {
        if(!isAttributes) {
            if (dataRows == null) {
                dataRows = new HashMap<>();
            }
            Row row = new Row(valuesList);
            dataRows.put(getNextPrimaryKey(), row);
        }
        if(isAttributes) { attributes= new Attributes(valuesList);}
    }

    public void addRowFromFile(Boolean isAttributes, String rowStr) throws IOException{
        String separator;
        if (rowStr.contains("\t")) {
            separator = "\t";
        } else {
            throw new IOException("Attribute separators in table are invalid, expected TABS");
        }

        String[] values = rowStr.split(separator);
        ArrayList<String> valueList = new ArrayList<>();
        //try this
        //ArrayList<String> row= new ArrayList<>( Arrays.asList(rowStr.split("\t")));

        for(String s : values) {
            valueList.add(s.trim());
        }

        if(isAttributes){
            attributes = new Attributes(valueList);
        }
        if(!isAttributes){
            if(attributes.getAttributeByNumber(0).equals("id")){
                if (dataRows == null) {
                    dataRows = new HashMap<>();
                }

                long key= Long.parseLong(valueList.get(0));
                System.out.println("reading file key is:" + key+ " " + valueList.get(0));
                valueList.remove(0);
                Row row=new Row(valueList);
                dataRows.put(key, row);
            }
        }
    }

    public String getRowsAsString(){
        String output = "";
        for(Long key : dataRows.keySet()){
            Row row = dataRows.get(key);
            output = output + row.getRowAsString(key) + "\n";
        }
        return output;
    }

    public String getAttributesAsString(){ return attributes.getAttributesAsString(); }

    public Attributes getAttributes() { return attributes; }

    public void addAttribute(String attributeName) throws InterpreterException {
        attributes.addAttribute(attributeName);
        int position = getAttributePosition(attributeName);
        if(dataRows!=null) {
            for (Row r : dataRows.values()) {
                r.addCell(position, "");
            }
        }
    }

    public int getAttributePosition(String attributeName) throws InterpreterException{
        return attributes.getAttributePosition(attributeName);
    }

    public ArrayList<String> getAttributesAsList() { return attributes.getAttributesAsList(); }


    public String getAttributeByNumber(int column) { return attributes.getAttributeByNumber(column); }


    public void removeAttribute(String attributeName) throws InterpreterException {
        int position = attributes.getAttributePosition(attributeName);
        if(position>0) {
            attributes.removeAttribute(position);
        }
        if(dataRows!=null) {
            for (Row r : dataRows.values()) {
                r.removeCell(position);
            }
        }

        if(position==0) { throw new InterpreterException.CannotDeletePrimaryKey();}

        if(position==-1) { throw new InterpreterException.AttributeDoesNotExist(attributeName);}
    }

    //clean this upp!!!!
    public String getDataColumnsAsString(ArrayList<Integer> columns, HashSet<Long> rows){
        String data="";
        Boolean select = false;
        for (Long l: dataRows.keySet()) {
            if (rows == null) { select=true; }
            if(rows!=null) {
                for (Long set : rows) {
                    if (l==set) { select=true; }
                }
            }
            if(select==true){
                for (int i : columns) {
                    if (i==0) {
                        data = data + l.toString() + "\t";
                    }
                    else {
                        data = data + dataRows.get(l).getCellDataByNumber(i-1) + "\t";
                    }
                }
                data = data.trim() + "\n";
            }
            select=false;
        }
        return data;
    }

    public void updateTableData(ArrayList<String[]> valuePairs, HashSet<Long> rows) throws InterpreterException {
        int column;   String data;   Row row;
        for(Long l : rows){
            row=dataRows.get(l);
            for(String[] p : valuePairs) {
                column = attributes.getAttributePosition(p[0])-1;
                data = p[1];
                row.changeCellData(column, data);
            }
        }
    }


    public HashSet<Long> getEqualHash(int column, String target, Boolean equal){
        HashSet<Long> set = new HashSet<>();
        for(Long l : dataRows.keySet()){
            Row r = dataRows.get(l);
            if(r.getCellDataByNumber(column).equals(target) && equal){
                set.add(l);
            }
            else if (!r.getCellDataByNumber(column).equals(target) && !equal){
                set.add(l);
            }
        }
        return set;
    }

    public HashSet<Long> getGreaterOrLessHash(int column, String target, String condition) {
        HashSet<Long> set = new HashSet<>();
        for (Long l : dataRows.keySet()) {
            Row r = dataRows.get(l);
            String data = r.getCellDataByNumber(column);
            if((Lexer.isIntLit(data) || Lexer.isFloatLit(data))&&(Lexer.isIntLit(target) || Lexer.isFloatLit(target))){
                if (condition.contains(">") && parseFloat(data)>parseFloat(target)) {
                    set.add(l);
                }
                if (condition.contains("<") && parseFloat(data)<parseFloat(target)) {
                    set.add(l);
                }
            }

        }
        return set;
    }


    public HashSet<Long> getLikeHash(int column, String target){
        HashSet<Long> set = new HashSet<>();
        for(Long l : dataRows.keySet()){
            Row r = dataRows.get(l);
            if(r.getCellDataByNumber(column).contains(target)){
                set.add(l);
            }
        }
        return set;
    }

    public void deleteRows(HashSet<Long> deletedRows){
        for (Long l : deletedRows){
            dataRows.remove(l);
        }
    }
}
















