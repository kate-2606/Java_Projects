package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.*;

import static java.lang.Float.parseFloat;


public class Table {
//use file separator instead
    public Table(String name, String databasePath) {
        tableName = name;
        this.keyPath=databasePath + File.separator + tableName + "Key.txt";
    }

    private Attributes attributes;

    private HashMap<Long, Row> dataRows;

    private final String keyPath;

    private final String tableName;

    private long nextPrimaryKey;

    public String getName(){ return tableName; }

    public boolean attributeExists(String attributeName){ return attributes.attributeExists(attributeName); }
    public long getNextPrimaryKey() throws IOException, InterpreterException {
        File fileToOpen = new File(keyPath);
        if (!(fileToOpen.isDirectory()) && fileToOpen.exists()) {
            try {
                FileReader reader = new FileReader(fileToOpen);
                BufferedReader buffReader = new BufferedReader(reader);
                String line = buffReader.readLine();
                nextPrimaryKey = Long.valueOf(line);
                setNextPrimaryKey();
                return nextPrimaryKey-1;
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
        if(!fileToOpen.exists()){
            nextPrimaryKey=0L;
            setNextPrimaryKey();
            return nextPrimaryKey;
        }
        throw new InterpreterException.FailedToFindPrimaryKey();
    }

    public void setNextPrimaryKey() throws IOException {
        File fileToOpen = new File(keyPath);

        new FileOutputStream(fileToOpen, false).close();
        FileWriter writer = new FileWriter(fileToOpen);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        nextPrimaryKey++;
        buffWriter.write(String.valueOf(nextPrimaryKey));
        buffWriter.flush();
        buffWriter.close();
    }




    //public long getNextPrimaryKey(){ return nextPrimaryKey; }

    public int getNumberOfDataRows() {
        if (dataRows!=null) {
            return dataRows.size();
        }
        else{
            return 0;
        }
    }

    public HashMap<Long, Row> getDataRows() { return dataRows; }

    public Row getRow(long rowNumber) throws InterpreterException {
        for(Long i  : dataRows.keySet()){
            if (i==rowNumber)
                return dataRows.get(i);
        }
        throw new InterpreterException.AccessingNonExistentRow(rowNumber);
    }


    public int getNumberOfAttributes() throws InterpreterException {
        if(attributes==null){
            throw new InterpreterException.NotExistentAttributes(getName());
        }
        return attributes.getNumberOfAttributes(); }


    public void addRowFromCommand(boolean isAttributes, ArrayList<String> valuesList) throws IOException, InterpreterException {
        if(!isAttributes) {
            if (dataRows == null) {
                dataRows = new HashMap<>();
            }
            Row row = new Row(valuesList);
            dataRows.put(getNextPrimaryKey(), row);
        }
        if(isAttributes) { attributes= new Attributes(valuesList);}
    }

    public void addRowFromFile(Boolean isAttributes, String rowStr) throws IOException, InterpreterException {
        String[] values = rowStr.split("\t");
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

    public String getAttributesAsString() throws InterpreterException {
        if (attributes == null) {
            throw new InterpreterException.NotExistentAttributes(getName());
        }

        return attributes.getAttributesAsString();
    }

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
        if(this.attributes==null) { return -1; }
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

        if(position==-1) { throw new InterpreterException.ExistingAttribute(attributeName);}
    }

    //clean this upp!!!!
    public String getDataColumnsAsString(ArrayList<Integer> columns, HashSet<Long> rows) throws InterpreterException{
        String data="";
        Boolean select = false;
        //this is a hack
        if(dataRows==null){
            throw new InterpreterException.AccessingNonExistentRow(0L);
        }
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


    public HashSet<Long> getEqualHash(int column, String target, String condition){
        HashSet<Long> set = new HashSet<>();
        for(Long l : dataRows.keySet()){
            Row r = dataRows.get(l);
            String data=r.getCellDataByNumber(column);
            if((data==null && target==null)){
                if(!condition.contains(">")&&!condition.contains("<")) {
                    set.add(l);
                }
            }
            if(Lexer.isBoolLit(data) && Lexer.isBoolLit(target)){
                if(!condition.contains(">")&&!condition.contains("<")) {
                    data = data.toLowerCase();
                    target = target.toLowerCase();
                }
            }
            if(data!=null && target !=null) {
                if (data.equals(target) && condition.equals("==")) {
                    set.add(l);
                } else if (!data.equals(target) && condition.equals("!=")) {
                    set.add(l);
                }
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
        if(Lexer.isFloatLit(target)||Lexer.isIntLit(target)||Lexer.isBoolLit(target)||target.equalsIgnoreCase("null")){
            return set;
        }
        for(Long l : dataRows.keySet()){
            Row r = dataRows.get(l);
            if(r.getCellDataByNumber(column).contains(target)){
                set.add(l);
            }
        }
        return set;
    }

    public void deleteRows(HashSet<Long> deletedRows) throws InterpreterException {
        for (Long l : deletedRows){
            dataRows.remove(getRow(l));
            dataRows.remove(l);
        }
    }
}
















