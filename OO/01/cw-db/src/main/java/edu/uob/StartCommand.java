package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

public class StartCommand extends Interpreter{

    public void StartCommand(ArrayList<Token> tokenList, InterpContext inpIc){
        ic=inpIc;
        tokens=tokenList;
        try {
            commandCommand();
        } catch (InterpreterException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
    private void commandCommand() throws InterpreterException, IOException {
        Token token = getCurrentToken();
        if (debugging) {
            System.out.println("in commandType, token type is: " + token.getType());
        }
        switch (token.getType()) {
            case USE:
                new UseCommand().UseCommand();
                break;
            case CREATE:
                new CreateCommand().createCommand();;
                break;
            case DROP:
                new DropCommand().DropCommand();;
                break;
            case ALTER:
                new AlterCommand().AlterCommand();
                break;
            case INSERT:
                new InsertCommand().InsertCommand();
                break;
            case SELECT:
                new SelectCommand().SelectCommand();
                break;
                /*
            case UPDATE : new UpdateCommand();
                break;
            case DELETE : new DeleteCommand();
                break;
            case JOIN : new JoinCommand();
                break;

                 */
            default:
        }
    }
}
