package edu.uob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

class ControllerTests {
  private OXOModel model;
  private OXOController controller;

  @BeforeEach
  void setup() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }

  void sendCommandToController(String command) {
      String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
      assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
  }

  @Test
  void testBasicMoveTaking() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1");
    String failedTestComment = "Cell a1 wasn't claimed by the first player";
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
  }

  // Test out basic win detection
  @Test
  void testBasicWin() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testDiagonalForward() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("c3"); // First player
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testDiagonalBackward() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("c1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("a1"); // Second player
    sendCommandToController("a3"); // First player
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }


  @Test
  void testDiagonalNotSquare() {
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(1);
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("b3"); // Second player
    controller.addRow();
    controller.addColumn();
    controller.addColumn();
    sendCommandToController("c2"); // First player
    controller.increaseWinThreshold();
    sendCommandToController("c3"); // Second player
    sendCommandToController("d2"); // First player
    sendCommandToController("c4"); // Second player
    sendCommandToController("d3"); // Second player
    controller.addColumn();
    sendCommandToController("d5"); // Second player
    String failedTestComment = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testDraw() {
    sendCommandToController("c3"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("c2"); // First player
    sendCommandToController("c1"); // Second player
    sendCommandToController("a1"); // First player
    String failedTestComment = "Draw was expected but wasn't but wasn't";
    assertTrue(model.isGameDrawn(), failedTestComment);
  }

  @Test
  void testExtendDrawToWin() {
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(1);
    sendCommandToController("c3"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("c2"); // First player
    sendCommandToController("c1"); // Second player
    sendCommandToController("a1"); // First player
    assertTrue(model.isGameDrawn());
    controller.addColumn();
    assertFalse( model.isGameDrawn());
    assertNull(model.getWinner());
    assertEquals(secondMovingPlayer, model.getCellOwner(1, 2));
    sendCommandToController("b4"); // Second player
    assertEquals(secondMovingPlayer, model.getCellOwner(1, 3));
    String failedTestComment = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testGridChangeMaintainsPastMoves() {
    model.addColumn();
    model.addColumn();
    model.addColumn();
    model.addRow();
    model.addRow();
    controller.increaseWinThreshold();
    sendCommandToController("c3"); // First player
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("c4"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("d4"); // Second player
    sendCommandToController("c2"); // First player
    sendCommandToController("b4"); // Second player
    controller.removeColumn();
    controller.removeColumn();

    String failedTestComment1 = "Expected "+secondMovingPlayer + " to be in column next to last removed column";
    assertEquals(secondMovingPlayer, model.getCellOwner(3, 3), failedTestComment1);
    assertEquals(secondMovingPlayer, model.getCellOwner(2, 3), failedTestComment1);
    assertEquals(secondMovingPlayer, model.getCellOwner(1, 3), failedTestComment1);
  }


  @Test
  void testGridChangeAfterWin() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    String failedTestComment1 = "Expected winner to be "+firstMovingPlayer + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment1);
    controller.addRow();
    controller.addColumn();
    String failedTestComment2 = "Expected grid to increase after win but didn't";
    assertEquals(4, model.getNumberOfRows(), failedTestComment2);
    assertEquals(4, model.getNumberOfColumns(), failedTestComment2);
    sendCommandToController("d4"); // Second player
    String failedTestComment7 = "Expected no plays to be made after a win and then a grid increase";
    assertNull(model.getCellOwner(3, 3), failedTestComment7);
    controller.removeColumn();
    controller.removeColumn();
    String failedTestComment3 = "Expected no removal of column when there is a player in any affected cells";
    assertEquals(3, model.getNumberOfColumns(), failedTestComment3);
    controller.removeRow();
    controller.removeRow();
    String failedTestComment4 = "Expected to remove row when there are no players in any of the affected cells";
    assertEquals(2, model.getNumberOfRows(), failedTestComment4);
    String failedTestComment5 = "Expected winner to remain after controller denied removing cells";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment5);
    controller.reset();
    String failedTestComment6 = "Expected winner to be null but wasn't";
    assertNull(model.getWinner(), failedTestComment6);
  }


  @Test
  void testPlayOutOfLimits() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.addRow();
    String failedTestComment = "Expected to throw an out of range exception but didn't";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("a4"), failedTestComment);
    String failedTestComment1 = "Expected to be "+firstMovingPlayer + "'s go but wasn't";
    assertEquals(firstMovingPlayer, model.getPlayerByNumber(model.getCurrentPlayerNumber()), failedTestComment1);

    sendCommandToController("d3"); // First player

    String failedTestComment2 = "Expected grid"+model.getNumberOfRows()+"*"+model.getNumberOfColumns() + " but wasn't";
    assertEquals(4, model.getNumberOfRows(), failedTestComment2);
    assertEquals(3, model.getNumberOfColumns(), failedTestComment2);
  }


  @Test
  void testMaxGridSize() {
    String failedTestComment1 = "Expected grid 3*3 but wasn't";
    assertEquals(3, model.getNumberOfRows(), failedTestComment1);
    assertEquals(3, model.getNumberOfColumns(), failedTestComment1);
    for (int i=3; i<10; i++) {
      controller.addRow();
      controller.addColumn();
    }
    String failedTestComment = "Expected grid 9*9 but wasn't";
    assertEquals(9, model.getNumberOfRows(), failedTestComment);
    assertEquals(9, model.getNumberOfColumns(), failedTestComment);
    assertNull(model.getWinner());
    assertFalse(model.isGameDrawn());

    controller.addRow();
    controller.addRow();
    controller.addColumn();
    controller.addColumn();

    assertEquals(9, model.getNumberOfRows(), failedTestComment);
    assertEquals(9, model.getNumberOfColumns(), failedTestComment);
  }

  @Test
  void testWinThresholdRange() {
    String failedTestComment1 = "Expected starting win threshold of 3 but wasn't";
    assertEquals(3, model.getWinThreshold(), failedTestComment1);

    controller.decreaseWinThreshold();

    assertEquals(3, model.getWinThreshold(), failedTestComment1);

    for (int i=0; i<9997; i++) {
      controller.increaseWinThreshold();
    }

    String failedTestComment2 = "Expected win threshold of 10000 but wasn't";
    assertEquals(10000, model.getWinThreshold(), failedTestComment2);
  }

  @Test
  void testMinGridSize() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.removeRow();
    controller.removeRow();
    controller.removeColumn();
    controller.removeColumn();

    String failedTestComment1 = "Expected grid 1*1 but wasn't";
    assertEquals(1, model.getNumberOfRows(), failedTestComment1);
    assertEquals(1, model.getNumberOfColumns(), failedTestComment1);

    controller.removeRow();
    controller.removeColumn();

    assertEquals(1, model.getNumberOfRows(), failedTestComment1);
    assertEquals(1, model.getNumberOfColumns(), failedTestComment1);

    sendCommandToController("a1"); // First player

    assertNull(model.getWinner());
    assertTrue(model.isGameDrawn());

    controller.addRow();
    sendCommandToController("b1"); // Second player

    assertNull(model.getWinner());
    assertTrue(model.isGameDrawn());

    controller.addColumn();
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player

    assertNull(model.getWinner());
    assertTrue(model.isGameDrawn());

    controller.addColumn();
    sendCommandToController("a3"); // First player
    assertEquals(firstMovingPlayer, model.getWinner());
  }

  @Test
  void testUnachievableWin() {
    model.setWinThreshold(10);
    for (int i=3; i<10; i++) {
      controller.addRow();
      controller.addColumn();
    }
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("d1"); // First player
    sendCommandToController("d2"); // Second player
    sendCommandToController("e1"); // First player
    sendCommandToController("e2"); // Second player
    sendCommandToController("f1"); // First player
    sendCommandToController("f2"); // Second player
    sendCommandToController("g1"); // First player
    sendCommandToController("g2"); // Second player
    sendCommandToController("h1"); // First player
    sendCommandToController("h2"); // Second player
    sendCommandToController("i1"); // First player

    String failedTestComment = "Expected no player to win 9*9 with win threshold of 10";
    assertNull(model.getWinner(), failedTestComment);
  }

  @Test
  void nullWin() {
    String failedTestComment = "Winner was expected to be null at game start but wasn't";
    assertNull(model.getWinner(), failedTestComment);
  }


  @Test
  void testColumnRowAddition() {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(0);
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    controller.addRow();
    controller.addColumn();
    sendCommandToController("d1"); // Second player
    sendCommandToController("a4"); // First player

    String failedTestComment = "Expected this cell to contain " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }


  @Test
  void testExtendedWinThreshBeforeWin() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("b2"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a1"); // First player
    sendCommandToController("c1"); // Second player
    controller.addRow();
    controller.addColumn();
    controller.increaseWinThreshold();
    sendCommandToController("a3"); // First player
    sendCommandToController("d1"); // Second player
    sendCommandToController("c3"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("d4"); // First player
    controller.addRow();

    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testNoWinThresholdDecreaseAfterStart(){
    controller.decreaseWinThreshold();
    controller.decreaseWinThreshold();
    String failedTestComment1 = "Win threshold expected to be 3 but wasn't";
    assertEquals(3, model.getWinThreshold(), failedTestComment1);
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    String failedTestComment2 = "Win threshold expected to be 5 but wasn't";
    assertEquals(5, model.getWinThreshold(), failedTestComment2);
    sendCommandToController("a1"); // First player
    controller.decreaseWinThreshold();
    controller.decreaseWinThreshold();
    sendCommandToController("b3"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("c3"); // First player
    String failedTestComment3 = "Expected winner to be null but wasn't";
    assertNull(model.getWinner(), failedTestComment3);
  }

  @Test
  void testWinThresholdReductionAfterGameWin(){
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.addColumn();
    controller.addRow();
    controller.increaseWinThreshold();
    sendCommandToController("a3"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("c1"); // Second player
    sendCommandToController("a1"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("a4"); // First player
    String failedTestComment1 = "Expected winner to be "+firstMovingPlayer+ " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment1);
    controller.decreaseWinThreshold();
    String failedTestComment2 = "Expected win threshold to be 3 but wasn't";
    assertEquals(3, model.getWinThreshold(), failedTestComment2);
  }

  @Test
  void testAfterWinRemoveEmptyColumnAndNotConsideredDraw() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.addColumn();
    controller.addRow();
    sendCommandToController("a3"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("c1"); // First player
    String failedTestComment1 = "Expected winner to be " + firstMovingPlayer + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment1);
    controller.removeColumn();
    controller.removeRow();
    String failedTestComment2 = "Expected to be able to remove empty column after win but couldn't";
    assertEquals(3, model.getNumberOfColumns(), failedTestComment2);
    String failedTestComment3 = "Expected to be able to remove empty row after win but couldn't";
    assertEquals(3, model.getNumberOfRows(), failedTestComment3);
  }

  @Test
  void testNoWinThresholdReductionIfLeadsToDraw(){
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.addColumn();
    controller.addColumn();
    controller.addRow();
    controller.increaseWinThreshold();
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("c3"); // First player
    sendCommandToController("c4"); // Second player
    sendCommandToController("b4"); // First player
    sendCommandToController("a4"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("d3"); // First player
    sendCommandToController("c1"); // Second player
    sendCommandToController("d2"); // First player
    sendCommandToController("d1"); // Second player
    sendCommandToController("c2"); // First player
    sendCommandToController("d4"); // Second player
    assertEquals(firstMovingPlayer, model.getCellOwner(1, 3));
    controller.removeColumn();

    String failedTestComment2 = "Expected not to accept column reduction as it results in a draw";
    assertEquals(5, model.getNumberOfColumns(), failedTestComment2);
    assertEquals(4, model.getNumberOfRows());
    assertEquals(firstMovingPlayer, model.getCellOwner(1, 3));
    assertEquals(4, model.getWinThreshold());
    assertNull(model.getWinner());
    assertFalse(model.isGameDrawn());
  }


  @Test
  void testThreePlayerWin() {
    model.addPlayer(new OXOPlayer('S'));
    controller.addColumn();

    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(2);
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("a3"); // Third player
    sendCommandToController("b1"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("b3"); // Third player
    sendCommandToController("c2"); // First player
    sendCommandToController("c1"); // Second player
    sendCommandToController("c3"); // Third player

    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);

    controller.reset();
    String failedTestComment1 = "Expected number of players to remain the same after reset but didn't";
    assertEquals(3, model.getNumberOfPlayers(), failedTestComment1);
  }

  @Test
  void testManyManyPlayersDraw() {
    for (int i=0; i<201; i++){
      model.addPlayer(new OXOPlayer('D'));
    }
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("a3"); // Third player
    sendCommandToController("b1"); //  X... player
    sendCommandToController("b2"); //  X... player
    sendCommandToController("b3"); //  X... player
    sendCommandToController("c2"); //  X... player
    sendCommandToController("c1"); //  X... player
    sendCommandToController("c3"); //  X... player
    int players = controller.gameModel.getNumberOfPlayers();
    String failedTestComment1 = "Expected game to be drawn when the number of players is " + players;
    assertTrue(model.isGameDrawn(), failedTestComment1);
  }

  @Test
  void testReset() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("c3"); // First player
    controller.reset();
    String failedTestComment1 = "Grid should be empty but wasn't";
    assertNull(model.getCellOwner(0, 0), failedTestComment1);
    String failedTestComment2 = "Winner should be unset but wasn't";
    assertNull(model.getWinner(), failedTestComment2);
    String failedTestComment3 = "Draw state should be false but wasn't";
    assertFalse(model.isGameDrawn(), failedTestComment3);
    String failedTestComment4 = "Current player should be "+ firstMovingPlayer + "but wasn't";
    assertEquals(firstMovingPlayer, model.getPlayerByNumber(model.getCurrentPlayerNumber()), failedTestComment4);
  }

  // Example of how to test for the throwing of exceptions
  @Test
  void testInvalidIdentifierException() {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `abc123`";
    // The next line is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("abc123"), failedTestComment);
  }

  @Test
  void testInvalidIdentifierCharacterException1() {
    String failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command `A%`";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("A%"), failedTestComment);
  }

  @Test
  void testInvalidIdentifierCharacterException2() {
    String failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command `Ç1`";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("Ç1"), failedTestComment);
  }
  @Test
  void testCellAlreadyTakenException() {
    sendCommandToController("b2"); // First player
    String failedTestComment = "Controller failed to throw an CellAlreadyTakenException for input of an occupied cell";
    assertThrows(CellAlreadyTakenException.class, ()-> controller.handleIncomingCommand("b2"), failedTestComment);
  }

  @Test
  void testOutsideCellRangeException() {
    String failedTestComment = "Controller failed to throw an CellAlreadyTakenException for input out of grid range";
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("b4"), failedTestComment);
  }
}
