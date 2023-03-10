package edu.uob;

import java.util.ArrayList;

public class OXOModel {

    private ArrayList<ArrayList<OXOPlayer>> cells = new ArrayList<>();
    private ArrayList<OXOPlayer> players = new ArrayList<>();
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        if(winThresh>=3) {winThreshold = winThresh;}
        else{ winThreshold =3; }

        if(numberOfColumns>9){ numberOfColumns=9;}
        if(numberOfRows>9){ numberOfRows=9;}

        if(numberOfColumns<1){ numberOfColumns=1;}
        if(numberOfRows<1){ numberOfRows=1;}

        for(int i=0; i<numberOfRows; i++) {
            cells.add(new ArrayList<>());
            for (int j = 0; j < numberOfColumns; j++) {
                cells.get(i).add(null);
            }
        }
    }

    public int getNumberOfPlayers() { return players.size(); }

    public void addPlayer(OXOPlayer player) { players.add(player); }

    public OXOPlayer getPlayerByNumber(int number) {
        return players.get(number);
    }


    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() { return cells.size(); }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber, player);
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn() {
        gameDrawn = true;
    }


    public boolean isGameDrawn() {
        return gameDrawn;
    }

    public void resetGameDrawn(){
        gameDrawn=false;
    }

    int minGridDim=1;
    int maxGridDim=9;
    public void addRow(){
        if(getNumberOfRows()<maxGridDim) {
            cells.add(new ArrayList<>());
            for (int j = 0; j < getNumberOfColumns(); j++) {
                cells.get(getNumberOfRows() - 1).add(null);
            }
            if (isGameDrawn()) {
                resetGameDrawn();
            }
            getNumberOfRows();
        }
    }

    public void addColumn() {
        if (getNumberOfColumns() < maxGridDim) {
            for (int j = 0; j < getNumberOfRows(); j++) {
                cells.get(j).add(null);
            }
            if (isGameDrawn()) {
                resetGameDrawn();
            }
            getNumberOfColumns();
        }
    }

    public void removeRow() {
        if (getNumberOfRows() > minGridDim) {
            cells.remove(getNumberOfRows() - 1);
            getNumberOfRows();
        }
    }

    public void removeColumn(){
        int numORows=getNumberOfRows();
        int numOCols=getNumberOfColumns();
        if(getNumberOfColumns()>minGridDim) {
            for (int j = 0; j < numORows; j++) {
                cells.get(j).remove(numOCols - 1);
            }
            getNumberOfColumns();
        }
    }



}
