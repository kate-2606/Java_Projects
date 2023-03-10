package edu.uob;
import edu.uob.OXOMoveException.*;
import java.util.ArrayList;

import static java.lang.Character.isAlphabetic;
import static java.lang.Character.isDigit;

public class OXOController {
    OXOModel gameModel;
    private int curPlayerNum;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {

        if(gameModel.getWinner()!=null || gameModel.isGameDrawn()){
            return ;
        }

        checkInputLengths(command);
        checkNumbersAndLetters(command);

        String str = command.toLowerCase();
        int inputRowNumber =(str.charAt(0)-'a');
        int inputColumnNumber =(str.charAt(1)-'1');
        curPlayerNum=gameModel.getCurrentPlayerNumber();

        checkRange(inputRowNumber, inputColumnNumber);
        checkEmptyCell(inputRowNumber, inputColumnNumber);

        OXOPlayer curPlayer = gameModel.getPlayerByNumber(curPlayerNum);
        gameModel.setCellOwner(inputRowNumber, inputColumnNumber, curPlayer);

        winDetection();
        drawDetection();

        nextPlayerNumber();
        gameModel.setCurrentPlayerNumber(curPlayerNum);
    }

    private void nextPlayerNumber(){
        if(curPlayerNum== gameModel.getNumberOfPlayers()-1){
            curPlayerNum=0;
        }
        else{
            curPlayerNum++;
        }
    }

    static final int maxDim=9;
    static final int minDim=1;

    public void addRow() {
        gameModel.addRow();
    }

    public void removeRow() {
        if(isEdgeEmpty(false)) {
            gameModel.removeRow();
            drawDetection();
            if(gameModel.isGameDrawn()){
                gameModel.resetGameDrawn();
                gameModel.addRow();
            }
        }
    }

    public void addColumn() {
        gameModel.addColumn();
    }

    public void removeColumn() {
        if(isEdgeEmpty(true)) {
            gameModel.removeColumn();
            drawDetection();
            if (gameModel.isGameDrawn()) {
                gameModel.resetGameDrawn();
                gameModel.addColumn();
            }
        }
    }

    private boolean isEdgeEmpty( boolean isColumn ){
        int numOfRows = gameModel.getNumberOfRows();
        int numOfCols = gameModel.getNumberOfColumns();
        if (!isColumn){
            for (int i=0; i<numOfCols; i++) {
                if (gameModel.getCellOwner(numOfRows - 1, i) != null) {
                    return false;
                }
            }
        }
        if (isColumn){
            for (int i=0; i<numOfRows; i++) {
                if (gameModel.getCellOwner(i, numOfCols-1) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void increaseWinThreshold() {
        int winThreshold=gameModel.getWinThreshold();
        winThreshold++;
        gameModel.setWinThreshold(winThreshold);
    }
    static final int minWinThreshold=3;

    public void decreaseWinThreshold() {
        if(gameModel.getWinThreshold()>minWinThreshold && (isGameStart() || gameModel.getWinner()!=null)){
            int winThreshold = gameModel.getWinThreshold();
            winThreshold--;
            gameModel.setWinThreshold(winThreshold);
        }
    }

    public boolean isGameStart(){
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if (gameModel.getCellOwner(i, j) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void reset() {
        resetGrid();
        gameModel.resetGameDrawn();
        gameModel.setWinner(null);
        gameModel.setCurrentPlayerNumber(0);
    }

    private void resetGrid(){

        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                gameModel.setCellOwner(i, j, null);
            }
        }
    }

    private void winDetection() {
       acrossOrDownWin();
       diagonalWin();
    }

    public void acrossOrDownWin() {
        int cnt = 0;
        OXOPlayer player = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber());
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if (gameModel.getCellOwner(i, j) == player) {
                    cnt++;
                }
                if (cnt == gameModel.getWinThreshold()) {
                    gameModel.setWinner(player);
                    return;
                }
            }
            cnt = 0;
        }

        //look for wins vert
        for (int i = 0; i < gameModel.getNumberOfColumns(); i++) {
            for (int j = 0; j < gameModel.getNumberOfRows(); j++) {
                if (gameModel.getCellOwner(j, i) == player) {
                    cnt++;
                }
                if (cnt == gameModel.getWinThreshold()) {
                    gameModel.setWinner(player);
                    return;
                }
            }
            cnt=0;
        }
    }

    private void diagonalWin() {
        curPlayerNum=gameModel.getCurrentPlayerNumber();
        OXOPlayer player = gameModel.getPlayerByNumber(curPlayerNum);
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if (gameModel.getCellOwner(i, j) == player) {
                    diagonalMatchForward(i, j, player, 0);
                    diagonalMatchBackward(i, j, player, 0);
                }
            }
        }
    }

    public void diagonalMatchForward(int i, int j, OXOPlayer player, int cnt){
        if(gameModel.getCellOwner(i, j)==player){
            cnt++;
            if(cnt==gameModel.getWinThreshold()){
                gameModel.setWinner(player);
            }
            if (i<gameModel.getNumberOfRows()-1 && j<gameModel.getNumberOfColumns()-1) {
                diagonalMatchForward(i + 1, j + 1, player, cnt);
            }
        }
    }

    private void diagonalMatchBackward(int i, int j, OXOPlayer player, int cnt){
        if(gameModel.getCellOwner(i, j)==player){
            cnt++;
            if(cnt==gameModel.getWinThreshold()){
                gameModel.setWinner(player);
            }
            if(i<gameModel.getNumberOfRows()-1 && j>0 ) {
                diagonalMatchBackward((i +1), (j - 1), player, cnt);
            }
        }
    }

    private void drawDetection() {
        int cnt=0;
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if(gameModel.getCellOwner(i, j)==null){
                    cnt++;
                }
            }
        }
        if (cnt==0 && gameModel.getWinner()==null){
            gameModel.setGameDrawn();
        }
    }

    private void checkInputLengths(String command) throws InvalidIdentifierLengthException{
        if(command.length()!=2){
            throw new InvalidIdentifierLengthException(command.length());
        }
    }

    private void checkNumbersAndLetters (String command) throws InvalidIdentifierCharacterException{
        char ch = command.charAt(0);
        if(!((ch>='a' && ch<='z') || (ch>='A' && ch<='Z'))) {
            throw new InvalidIdentifierCharacterException(RowOrColumn.ROW, ch);
        }
        ch = command.charAt(1);
        if(!isDigit(ch)){
            throw new InvalidIdentifierCharacterException(RowOrColumn.COLUMN, ch);
        }
    }

    private void checkRange(int inputRowNumber, int inputColumnNumber) throws OutsideCellRangeException{
        if (inputRowNumber>= gameModel.getNumberOfRows()){
            throw new OutsideCellRangeException(RowOrColumn.ROW, inputRowNumber+1);
        }
        if(inputColumnNumber>= gameModel.getNumberOfColumns()){
            throw new OutsideCellRangeException(RowOrColumn.COLUMN, inputColumnNumber+1);
        }
    }

    private void checkEmptyCell(int inputRowNumber, int inputColumnNumber) throws CellAlreadyTakenException{
        if(gameModel.getCellOwner(inputRowNumber, inputColumnNumber)!=null){
            throw new CellAlreadyTakenException(inputRowNumber+1, inputColumnNumber+1);
        }
    }
}
