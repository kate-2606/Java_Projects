package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

public class StartCommand extends Interpreter{

    public StartCommand(InterpContext context, ArrayList<Token> tokens) throws InterpreterException, IOException {
        super(context, tokens);
    }

    public void interpretCommand(){
        try{
        Token token = getCurrentToken();
        System.out.println(getCurrentToken().getType());
        /*
        if (debugging) {
            System.out.println("in commandType, token type is: " + token.getType());
        }
        */
        switch (token.getType()) {
            case USE:
                UseCommand use = new UseCommand(context, tokens);
                use.interpretCommand();
                break;
            case CREATE:
                CreateCommand create = new CreateCommand(context, tokens);
                create.interpretCommand();
                break;
            case DROP:
                DropCommand drop = new DropCommand(context, tokens);
                drop.interpretCommand();
                break;
            case ALTER:
                AlterCommand alter = new AlterCommand(context, tokens);
                alter.interpretCommand();
                break;
            case INSERT:
                InsertCommand insert = new InsertCommand(context, tokens);
                insert.interpretCommand();
                break;
            case SELECT:
                SelectCommand select = new SelectCommand(context, tokens);
                select.interpretCommand();
                break;
            case UPDATE :
                UpdateCommand update = new UpdateCommand(context, tokens);
                update.interpretCommand();
                break;
                /*
            case DELETE : new DeleteCommand();
                break;

                 */
            case JOIN :

                JoinCommand join = new JoinCommand(context, tokens);
                join.interpretCommand();
                break;

            default:
        }
    } catch (InterpreterException | IOException e) {
        context.setResult("[ERROR]\n"+ e.getMessage() + "\n");
    }
    }
}
