package edu.uob;
import static edu.uob.BasicCommand.*;

public class InterpretCommand {
    public InterpretCommand(String inpCommand, String response) {

        String command = inpCommand.toLowerCase();
        for(BasicCommand trigger : BasicCommand.values()) {
            if(command.equals("inv")) {

            }
            if(command.equals(trigger.toString())){
                response = handleBasicCommand(command, trigger);
            }
        }
    }

    private String handleBasicCommand(String command, BasicCommand trigger){
        if(trigger == inventory){

        }
        return "";
    }


}
