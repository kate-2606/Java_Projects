package edu.uob;

import java.io.Serial;

public class GameExceptions extends Exception {

    @Serial
    private static final long serialVersionUID = 1;

    public GameExceptions(String message) {
        super(message);
    }

    public static class ActionIsNull extends GameExceptions {
        @Serial
        private static final long serialVersionUID = 1;

        public ActionIsNull() {
            super("Action is null");
        }
    }

    public static class CannotGetOrDropMultiple extends GameExceptions {
        @Serial
        private static final long serialVersionUID = 1;

        public CannotGetOrDropMultiple() {
            super("you cannot GET/DROP multiple items at the same time.");
        }
    }


    public static class CannotGetOrDropItem extends GameExceptions {
        @Serial
        private static final long serialVersionUID = 1;

        public CannotGetOrDropItem(String entityName) {

            super("you cannot GET/DROP the "+(entityName.equals("")? "unknown item" : entityName)); }
    }

    public static class ExeraneousEntities extends GameExceptions {
        @Serial
        private static final long serialVersionUID = 1;

        public ExeraneousEntities() {
            super("Exeranious entities detected. Command not executed.");
        }
    }

    public static class MultipleTriggerWords extends GameExceptions {
        @Serial
        private static final long serialVersionUID = 1;

        public MultipleTriggerWords() {
            super("Multiple trigger words detected. Command not executed.");
        }
    }

    public static class SubjectsNotInVicinity extends GameExceptions {
        @Serial
        private static final long serialVersionUID = 1;

        public SubjectsNotInVicinity() {
            super("The subjects of the action are not in your current vecinity");
        }
    }

    public static class CannotAccessThisLocation extends GameExceptions {
        @Serial
        private static final long serialVersionUID = 1;

        public CannotAccessThisLocation(GameLocation current, GameLocation next) {
            super("You cannot access the "+next.getName()+ " from the " + current.getName());
        }
    }

    public static class CannotPerformActionInLocation  extends GameExceptions {
        @Serial
        private static final long serialVersionUID = 1;

        public CannotPerformActionInLocation(GameLocation current) {
            super("You cannot perform this action in the " + current);
        }
    }
}